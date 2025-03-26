package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.enums.UserEnum;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.rabbitmq.producer.RabbitMQUserProducer;
import com.anjox.Gamebox_api.repository.UserRepository;
import com.anjox.Gamebox_api.security.components.AESEncryption;
import com.anjox.Gamebox_api.util.ActivationTokenGenerator;
import com.anjox.Gamebox_api.util.GeneratorNewPassword;
import com.anjox.Gamebox_api.util.ValidationPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQUserProducer rabbitMQUserProducer;
    private final AESEncryption aesEncryption;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RabbitMQUserProducer rabbitMQUserProducer, AESEncryption aesEncryption) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitMQUserProducer = rabbitMQUserProducer;
        this.aesEncryption = aesEncryption;
    }

    public void createUser (RequestRegisterUserDto dto , Authentication auth){

        logger.info("Iniciando a criaçao de um novo usuario");


        logger.info("Verificando se nome ou email ja existe");
        if(VerifyUsernameOrEmailIfExists(dto.username(), dto.email())) {
            logger.error("nome ou email ja existe");
            throw new MessageErrorExeption("nome ou email ja existe", HttpStatus.CONFLICT);
        }

        logger.info("validando senha");
        ValidationPassword.validation(dto.password());

        if (dto.type() == UserEnum.ADM) {
            if (auth == null) {
                logger.error("Token de usuário não fornecido para criação de administrador");
                throw new MessageErrorExeption("Token de usuário não fornecido para criação de administrador", HttpStatus.UNAUTHORIZED);
            }

            if (!isAdmin(auth)) {
                logger.error("Apenas administradores podem criar outros administradores");
                throw new MessageErrorExeption("Apenas administradores podem criar outros administradores", HttpStatus.UNAUTHORIZED);
            }
        }
        logger.info("Encriptando senha");
        String password = passwordEncoder.encode(dto.password());
        String token = ActivationTokenGenerator.accountActivatorTokenGenerator();
        RabbitMQActivationAccountDto rabbitMQActivationAccountDto = new RabbitMQActivationAccountDto(dto.username(), dto.email(), token);
        UserEntity user = new UserEntity(
                dto.username(),
                dto.email(),
                password,
                dto.type(),
                token,
                false
        );
        logger.info("salvando");
        userRepository.save(user);
        logger.info("mandando mensagem para fila do rabbit");
        rabbitMQUserProducer.sendActivationAccountQueue(rabbitMQActivationAccountDto);
    }

    public ResponseUserDto findById (Long  id){
        logger.info("buscando user por id");
        UserEntity user = userRepository.findByid(id);
            return new ResponseUserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getType()
            );
    }

    public ResponsePaginationUserDto findAll( int page, int size) {
        logger.info("buscando todos os usuarios");
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> users = userRepository.findAll(pageable);
        List<ResponseUserDto> usersList = users.stream().map(
                u -> new ResponseUserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getType()
                )
        ).collect(Collectors.toList());
        return new ResponsePaginationUserDto(
                usersList,
                users.getTotalPages(),
                users.getTotalElements(),
                users.getSize(),
                users.getNumber()
        );
    }

    @Transactional
    public void updateUserById(Long userId, RequestUpdateUserDto dto) {
        logger.info("iniciando atualizaçao de usuario");
        UserEntity user = userRepository.findByid(userId);

        if (user == null) {
            logger.error("Usuário não encontrado");
            throw new MessageErrorExeption("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        if (dto.username() != null && !dto.username().isEmpty()) {
            if (!user.getUsername().equals(dto.username()) && userRepository.existsByUsername(dto.username())) {
                logger.error("O username já existe");
                throw new MessageErrorExeption("O username já existe", HttpStatus.CONFLICT);
            }
            user.setUsername(dto.username());
        }

        if (dto.password() != null && !dto.password().isEmpty()) {
            ValidationPassword.validation(dto.password());
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        logger.info("salvando");
        userRepository.save(user);
    }

    @Transactional
    public void deleteById (Long id) {
        logger.info("deletando user por id");
        userRepository.deleteById(id);
    }

    @Transactional
    public boolean activateAccount (String token){
        logger.info("ativando o usuario");
        UserEntity user = userRepository.findByactivationCode(token);
        if(user == null){
            return false;
        }
        user.setActivationCode(null);
        user.setActivated(true);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void refreshPassword(String email) {
        logger.info("iniciando o refresh password");
        UserEntity userFromEmail = userRepository.findByEmail(email);

        if (userFromEmail == null) {
            logger.error("Usuário não encontrado");
            throw new MessageErrorExeption("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        logger.info("gerando nova senha");
        String newPassword = GeneratorNewPassword.generateNewPassword();

        String aesPasswordEncrypted = aesEncryption.encryptPassword(newPassword);

        String passwordEncoded = passwordEncoder.encode(newPassword);

        userFromEmail.setPassword(passwordEncoded);

        logger.info("salvando");
        userRepository.save(userFromEmail);

        RabbitMQResetPasswordDto rabbitMQResetPasswordDto = new RabbitMQResetPasswordDto(
                userFromEmail.getUsername(),
                userFromEmail.getEmail(),
                aesPasswordEncrypted
        );
        logger.info("enviando para fila rabbitmq");
        rabbitMQUserProducer.sendResetPasswordQueue(rabbitMQResetPasswordDto);
    }

    private boolean VerifyUsernameOrEmailIfExists (String username , String email){
        return userRepository.findByUsername(username) != null || userRepository.findByemail(email) != null;
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
}

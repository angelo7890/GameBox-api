package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.enums.UserEnum;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.rabbitmq.producer.RabbitMQUserProducer;
import com.anjox.Gamebox_api.repository.UserRepository;
import com.anjox.Gamebox_api.security.UserPrincipal;
import com.anjox.Gamebox_api.security.components.AESEncryption;
import com.anjox.Gamebox_api.security.service.AuthorizationService;
import com.anjox.Gamebox_api.security.service.JwtService;
import com.anjox.Gamebox_api.util.ActivationTokenGenerator;
import com.anjox.Gamebox_api.util.GeneratorNewPassword;
import com.anjox.Gamebox_api.util.ValidationPassword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RabbitMQUserProducer rabbitMQUserProducer;
    private final AESEncryption aesEncryption;
    private final AuthorizationService authorizationService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RabbitMQUserProducer rabbitMQUserProducer, AESEncryption aesEncryption, AuthorizationService authorizationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.rabbitMQUserProducer = rabbitMQUserProducer;
        this.aesEncryption = aesEncryption;
        this.authorizationService = authorizationService;
    }

    public void createUser (RequestRegisterUserDto dto , String usernameFromToken ){
        if(VerifyUsernameOrEmailIfExists(dto.username(), dto.email())) {
            throw new MessageErrorExeption("nome ou email ja existe", HttpStatus.CONFLICT);
        }

        ValidationPassword.validation(dto.password());

        if (dto.type() == UserEnum.ADM) {
            if (usernameFromToken == null || usernameFromToken.isEmpty()) {
                throw new MessageErrorExeption("Token de usuário não fornecido para criação de administrador", HttpStatus.UNAUTHORIZED);
            }

            UserEntity requester = userRepository.findByUsername(usernameFromToken);
            if (requester == null || requester.getType() != UserEnum.ADM) {
                throw new MessageErrorExeption("Apenas administradores podem criar outros administradores", HttpStatus.UNAUTHORIZED);
            }
        }
        String password = passwordEncoder.encode(dto.password());
        String token = ActivationTokenGenerator.accountActivatorTokenGenerator();
        RabbitMQActivationAccountDto rabbitMQActivationAccountDto = new RabbitMQActivationAccountDto(dto.username(), dto.email(), token);
        UserEntity user = new UserEntity(
                dto.username(),
                dto.email(),
                password,
                dto.type(),
                token,
                true
        );
        userRepository.save(user);
        rabbitMQUserProducer.sendActivationAccountQueue(rabbitMQActivationAccountDto);
    }

    public ResponseJwtTokensDto refreshTokenAccessFromRefreshToken(String usernameFromRefreshToken){
        if(usernameFromRefreshToken == null || usernameFromRefreshToken.isEmpty()){
            throw new MessageErrorExeption("Username nao pode ser nulo ou vazio" , HttpStatus.BAD_REQUEST);
        }
        UserPrincipal user = (UserPrincipal) authorizationService.loadUserByUsername(usernameFromRefreshToken);
        return jwtService.getJwtUserToken(user);
    }

    public ResponseUserDto findById (Long  id){
        UserEntity user = userRepository.findByid(id);
            return new ResponseUserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getType()
            );
    }

    public ResponsePaginationUserDto findAll( int page, int size) {
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
        UserEntity user = userRepository.findByid(userId);

        if (user == null) {
            throw new MessageErrorExeption("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        if (dto.username() != null && !dto.username().isEmpty()) {
            if (!user.getUsername().equals(dto.username()) && userRepository.existsByUsername(dto.username())) {
                throw new MessageErrorExeption("O username já existe", HttpStatus.CONFLICT);
            }
            user.setUsername(dto.username());
        }

        if (dto.password() != null && !dto.password().isEmpty()) {
            ValidationPassword.validation(dto.password());
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteById (Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public boolean activateAccount (String token){
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
        UserEntity userFromEmail = userRepository.findByEmail(email);

        if (userFromEmail == null) {
            throw new MessageErrorExeption("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        String newPassword = GeneratorNewPassword.generateNewPassword();

        String aesPasswordEncrypted = aesEncryption.encryptPassword(newPassword);

        String passwordEncoded = passwordEncoder.encode(newPassword);

        userFromEmail.setPassword(passwordEncoded);
        userRepository.save(userFromEmail);

        RabbitMQResetPasswordDto rabbitMQResetPasswordDto = new RabbitMQResetPasswordDto(
                userFromEmail.getUsername(),
                userFromEmail.getEmail(),
                aesPasswordEncrypted
        );
        rabbitMQUserProducer.sendResetPasswordQueue(rabbitMQResetPasswordDto);
    }
    private boolean VerifyUsernameOrEmailIfExists (String username , String email){
        return userRepository.findByUsername(username) != null || userRepository.findByemail(email) != null;
    }
}

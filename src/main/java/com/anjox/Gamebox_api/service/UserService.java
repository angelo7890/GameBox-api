package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.enums.UserEnum;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.exeption.PasswordErrorExeption;
import com.anjox.Gamebox_api.producer.RabbitMQUserProducer;
import com.anjox.Gamebox_api.repository.UserRepository;
import com.anjox.Gamebox_api.util.AESEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RabbitMQUserProducer rabbitMQUserProducer;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RabbitMQUserProducer rabbitMQUserProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.rabbitMQUserProducer = rabbitMQUserProducer;
    }

    public void createUser (RequestRegisterUserDto dto , String usernameFromToken ){
        if(VerifyUsernameOrEmailIfExists(dto.username(), dto.email())) {
            throw new MessageErrorExeption("nome ou email ja existe", HttpStatus.CONFLICT);
        }

        validationPassword(dto.password());

        if (dto.type() == UserEnum.ADM) {
            if (usernameFromToken == null || usernameFromToken.isEmpty()) {
                throw new MessageErrorExeption("Token de usuário não fornecido para criação de administrador", HttpStatus.UNAUTHORIZED);
            }

            UserEntity requester = userRepository.findByUsername(usernameFromToken);
            if (requester == null || requester.getType() != UserEnum.ADM) {
                throw new MessageErrorExeption("Apenas administradores podem criar outros administradores", HttpStatus.UNAUTHORIZED);//
            }
        }
        String password = passwordEncoder.encode(dto.password());
        String token = accountActivatorTokenGenerator();
        System.out.println(token);
        RabbitMQActivationAccountDto rabbitMQActivationAccountDto = new RabbitMQActivationAccountDto(dto.username(), dto.email(), token);
        UserEntity user = new UserEntity(
                dto.username(),
                dto.email(),
                password,
                dto.type(),
                token,
                false
        );
        userRepository.save(user);
        rabbitMQUserProducer.sendActivationAccountQueue(rabbitMQActivationAccountDto);
    }

    public ResponseJwtTokensDto refreshTokenAccessFromRefreshToken(String usernameFromRefreshToken){
        if(usernameFromRefreshToken == null || usernameFromRefreshToken.isEmpty()){
            throw new MessageErrorExeption("Username nao pode ser nulo ou vazio" , HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userRepository.findByUsername(usernameFromRefreshToken);
        return jwtService.getJwtUserToken(userDetails);
    }

    public ResponseUserDto findById (Long  id , String usernameFromToken){
        UserEntity user = userRepository.findByid(id);
        UserEntity userFromToken = userRepository.findByUsername(usernameFromToken);
        if(userFromToken.getId().equals(user.getId()) || userFromToken.getType().equals(UserEnum.ADM)){
            return new ResponseUserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getType()
            );
        }
        throw new MessageErrorExeption("Voce nao pode buscar Informaçoes de outro usuario", HttpStatus.FORBIDDEN);
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
    public void updateUserById(Long userId, RequestUpdateUserDto dto, String usernameFromToken) {
        UserEntity user = userRepository.findByid(userId);
        UserEntity userFromToken = userRepository.findByUsername(usernameFromToken);

        if (user == null) {
            throw new MessageErrorExeption("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        if (!user.getId().equals(userFromToken.getId()) && !userFromToken.getType().equals(UserEnum.ADM)) {
            throw new MessageErrorExeption("Você não pode alterar informação de outro usuário", HttpStatus.UNAUTHORIZED);
        }

        if (dto.username() != null && !dto.username().isEmpty()) {
            if (!user.getUsername().equals(dto.username()) && userRepository.existsByUsername(dto.username())) {
                throw new MessageErrorExeption("O username já existe", HttpStatus.CONFLICT);
            }
            user.setUsername(dto.username());
        }

        if (dto.password() != null && !dto.password().isEmpty()) {
            validationPassword(dto.password());
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteById (Long id , String usernameFromToken) {
        UserEntity user = userRepository.findByUsername(usernameFromToken);
        if(user.getId().equals(id) || user.getType().equals(UserEnum.ADM)){
            if(userRepository.existsById(id)){
                userRepository.deleteById(id);
            }
            throw new MessageErrorExeption("Usuario nao encontrado", HttpStatus.NOT_FOUND);
        }
        throw new MessageErrorExeption("Voce nao pode excluir a conta de outra pessoa", HttpStatus.FORBIDDEN);
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
    public void refreshPassword(String email , String usernameFromToken){
        UserEntity userFromToken = userRepository.findByUsername(usernameFromToken);
        UserEntity userFromEmail = userRepository.findByemail(email);
        if(userFromEmail != null){
            if(userFromToken.getId().equals(userFromEmail.getId()) || userFromToken.getType().equals(UserEnum.ADM)){
                    String newPassword = genereteNewPassword();
                    String aesPasswordEncrypted = AESEncryption.encryptPassword(newPassword);
                    String passwordEncoded = passwordEncoder.encode(newPassword);
                    userFromEmail.setPassword(passwordEncoded);
                    userRepository.save(userFromEmail);
                    RabbitMQResetPasswordDto rabbitMQResetPasswordDto = new RabbitMQResetPasswordDto(userFromEmail.getUsername(),userFromEmail.getEmail(),aesPasswordEncrypted);
                    rabbitMQUserProducer.sendResetPasswordQueue(rabbitMQResetPasswordDto);
            }
            throw new MessageErrorExeption("Voce nao pode mudar a senha de outra pessoa", HttpStatus.FORBIDDEN);
        }
        throw new MessageErrorExeption("Usuario nao encontrado", HttpStatus.NOT_FOUND);
    }

    private boolean VerifyUsernameOrEmailIfExists (String username , String email){
        return userRepository.findByUsername(username) != null || userRepository.findByemail(email) != null;
    }

    private void validationPassword(String password){
        List<String> list = new ArrayList<String>();
        if(!(password!=null && password.length()>6)) {
            list.add("a senha deve ter no minimo 6 caracteres");
        }
        if(!Pattern.matches(".*[A-Z].*", password)) {
            list.add("a senha deve conter letras maiusculas");
        }
        if(!Pattern.matches(".*[a-z].*", password)) {
            list.add("a senha deve conter letras minusculas");
        }
        if(!Pattern.matches(".*[0-9].*", password)) {
            list.add("a senha deve conter numeros");
        }
        if(!Pattern.matches(".*[!@#$%?+-].*", password)) {
            list.add("a senha deve conter caracteres especiais: ! @ # $ % ? + - ");
        }
        if(!list.isEmpty()) {
            throw new PasswordErrorExeption("Password Incorreto", list, HttpStatus.BAD_REQUEST);
        }
    }
    private String accountActivatorTokenGenerator() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int index = random.nextInt(caracteres.length());
            token.append(caracteres.charAt(index));
        }
        return token.toString();
    }

    private String genereteNewPassword(){
        UUID uuid = UUID.randomUUID();
        String truncatedUUID;
        return truncatedUUID = uuid.toString().replace("-", "").substring(0, 15);
    }
}

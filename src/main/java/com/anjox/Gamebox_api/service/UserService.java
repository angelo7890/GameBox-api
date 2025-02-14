package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.enums.UserEnum;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.exeption.PasswordErrorExeption;
import com.anjox.Gamebox_api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
        UserEntity user = new UserEntity(
                dto.username(),
                dto.email(),
                password,
                dto.type(),
                token,
                true
        );
        userRepository.save(user);
    }

    public ResponseJwtTokensDto refreshTokenAccessFromRefreshToken(String usernameFromRefreshToken){
        if(usernameFromRefreshToken == null || usernameFromRefreshToken.isEmpty()){
            throw new MessageErrorExeption("Username nao pode ser nulo ou vazio" , HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userRepository.findByUsername(usernameFromRefreshToken);
        return jwtService.getJwtUserToken(userDetails);
    }

    public ResponseUserDto findById (Long  id){
        UserEntity user = userRepository.findByid(id);
        if(user != null){
           return new ResponseUserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getType()
            );
        }
        throw new MessageErrorExeption("usuario nao encontrado", HttpStatus.NOT_FOUND);
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
    public void deleteById (Long id){
        userRepository.deleteById(id);
    }

    public void activateAccount (String token){
        UserEntity user = userRepository.findByactivationCode(token);
        if(user == null){
            return;
        }
        user.setActivationCode(null);
        user.setActivated(true);
        userRepository.save(user);
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
        for (int i = 0; i < 255; i++) {
            int index = random.nextInt(caracteres.length());
            token.append(caracteres.charAt(index));
        }
        return token.toString();
    }
}

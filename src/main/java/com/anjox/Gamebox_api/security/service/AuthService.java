package com.anjox.Gamebox_api.security.service;


import com.anjox.Gamebox_api.dto.RequestLoginDto;
import com.anjox.Gamebox_api.dto.ResponseJwtTokensDto;
import com.anjox.Gamebox_api.dto.ResponseLoginDto;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.security.UserPrincipal;
import com.anjox.Gamebox_api.security.components.UserRequestAuthorizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, AuthorizationService authorizationService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authorizationService = authorizationService;
    }


    public ResponseLoginDto authenticate(RequestLoginDto requestLoginDto) {
        logger.info("inciando autenticaçao do usuario");
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(requestLoginDto.username(), requestLoginDto.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            if (auth.isAuthenticated()) {
                UserPrincipal user = (UserPrincipal) auth.getPrincipal();

                logger.info("iniciando criaçao do token jwt para o usuario");
                ResponseJwtTokensDto response = jwtService.getJwtUserToken(user);
                logger.info("retornando o Objeto ResponseLoginDto");
                return new ResponseLoginDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        response
                );
            }
            logger.error("Erro ao autenticar usuario");
            throw new BadCredentialsException("Bad credentials");
        } catch (Exception e) {
            logger.error("erro ao autenticar usuario: {}", e.getMessage());
            throw new MessageErrorExeption(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseJwtTokensDto refreshTokenAccessFromRefreshToken(String usernameFromRefreshToken){
        if(usernameFromRefreshToken == null || usernameFromRefreshToken.isEmpty()){
            throw new MessageErrorExeption("Username nao pode ser nulo ou vazio" , HttpStatus.BAD_REQUEST);
        }
        UserPrincipal user = (UserPrincipal) authorizationService.loadUserByUsername(usernameFromRefreshToken);
        return jwtService.getJwtUserToken(user);
    }
}

package com.anjox.Gamebox_api.security.service;


import com.anjox.Gamebox_api.dto.RequestLoginDto;
import com.anjox.Gamebox_api.dto.ResponseJwtTokensDto;
import com.anjox.Gamebox_api.dto.ResponseLoginDto;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.security.UserPrincipal;
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

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    public ResponseLoginDto authenticate(RequestLoginDto requestLoginDto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(requestLoginDto.username(), requestLoginDto.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            if (auth.isAuthenticated()) {
                UserPrincipal user = (UserPrincipal) auth.getPrincipal();

                ResponseJwtTokensDto response = jwtService.getJwtUserToken(user);
                return new ResponseLoginDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        response
                );
            }
            throw new BadCredentialsException("Bad credentials");
        } catch (Exception e) {
            throw new MessageErrorExeption(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}

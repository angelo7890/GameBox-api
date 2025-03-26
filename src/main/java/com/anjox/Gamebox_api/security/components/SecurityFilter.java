package com.anjox.Gamebox_api.security.components;


import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.exeption.error.ResponseError;
import com.anjox.Gamebox_api.repository.UserRepository;
import com.anjox.Gamebox_api.security.UserPrincipal;
import com.anjox.Gamebox_api.security.service.AuthorizationService;
import com.anjox.Gamebox_api.security.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthorizationService authorizationService;
    private final ObjectMapper objectMapper;

    public SecurityFilter(JwtService jwtService, AuthorizationService authorizationService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.authorizationService = authorizationService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        var token = this.recoverToken(request);
        if (token != null) {

            var login = jwtService.validateToken(token);
            //TODO
            if (login != null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                ResponseError responseError = new ResponseError(
                        "Token JWT inválido ou expirado. Por favor, forneça um token válido.",
                        HttpStatus.UNAUTHORIZED
                );
                response.getWriter().write(objectMapper.writeValueAsString(responseError));
                return;
            }

            String typeFromToken = jwtService.getTypeFromToken(token);
            if (typeFromToken.equals("refresh") && !request.getRequestURI().equals("/api/user/refresh-token")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                ResponseError responseError = new ResponseError(
                        "Token de acesso invalido",
                        HttpStatus.UNAUTHORIZED
                );
                response.getWriter().write(objectMapper.writeValueAsString(responseError));
                return;
            }

            UserPrincipal principal = (UserPrincipal) authorizationService.loadUserByUsername(login);

            var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }


    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if(authHeader==null){
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}

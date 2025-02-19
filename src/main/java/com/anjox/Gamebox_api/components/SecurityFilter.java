package com.anjox.Gamebox_api.components;


import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.repository.UserRepository;
import com.anjox.Gamebox_api.service.JwtService;
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
    private final UserRepository userRepository;

    public SecurityFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null) {
            var login = jwtService.validateToken(token);
            if(login.isEmpty()){
                throw new MessageErrorExeption("Token invalido", HttpStatus.UNAUTHORIZED);
            }
            String typeFromToken = jwtService.getTypeFromToken(token);
            if(typeFromToken.equals("refresh") && !request.getRequestURI().equals("/api/user/refresh-token")){
                throw new MessageErrorExeption("Token de acesso invalido", HttpStatus.UNAUTHORIZED);
            }
            UserDetails user = userRepository.findByUsername(login);
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
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

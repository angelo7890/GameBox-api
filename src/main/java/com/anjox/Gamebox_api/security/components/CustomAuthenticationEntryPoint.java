package com.anjox.Gamebox_api.security.components;

import com.anjox.Gamebox_api.exeption.error.ResponseUnauthorizedError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ResponseUnauthorizedError error = new ResponseUnauthorizedError(request.getRequestURI(),
                "Usuário não autenticado",
                "Voce precisa esta autenticado para usar esse recurso.",
                HttpStatus.UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
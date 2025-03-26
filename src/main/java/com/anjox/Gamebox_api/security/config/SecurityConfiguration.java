package com.anjox.Gamebox_api.security.config;


import com.anjox.Gamebox_api.security.components.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;
    private final UserRequestAuthorizationManager userRequestAuthorizationManager;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final GameRequestAuthorizationManager gameRequestAuthorizationManager;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


    public SecurityConfiguration(SecurityFilter securityFilter, UserRequestAuthorizationManager userRequestAuthorizationManager, CustomAccessDeniedHandler customAccessDeniedHandler, GameRequestAuthorizationManager gameRequestAuthorizationManager, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.securityFilter = securityFilter;
        this.userRequestAuthorizationManager = userRequestAuthorizationManager;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.gameRequestAuthorizationManager = gameRequestAuthorizationManager;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/picture/upload").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/refresh-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/verify").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/findAll").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/user").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/picture/delete/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/user/**").access(this.userRequestAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/user/**").access(this.userRequestAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/api/user/**").access(this.userRequestAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/game/**").access(this.gameRequestAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/game/user/**").access(this.gameRequestAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/api/game/filter/**").access(this.gameRequestAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/api/game").access(this.gameRequestAuthorizationManager)
                        .requestMatchers(HttpMethod.PUT, "/api/game/update/**").access(this.gameRequestAuthorizationManager)

                        .anyRequest().authenticated()
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(this.customAccessDeniedHandler)
                        .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

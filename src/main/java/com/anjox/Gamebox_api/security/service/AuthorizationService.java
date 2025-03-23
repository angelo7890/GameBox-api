package com.anjox.Gamebox_api.security.service;


import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.repository.UserRepository;
import com.anjox.Gamebox_api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            logger.warn("Usuário não encontrado: {}", username);
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        if (!user.isEnabled()) {
            logger.warn("conta desativada");
            throw new MessageErrorExeption("Conta desativada, Verifique seu Email", HttpStatus.BAD_REQUEST);
        }

        logger.info("Usuário autenticado com sucesso: {}", username);
        return new UserPrincipal(user);
    }
}
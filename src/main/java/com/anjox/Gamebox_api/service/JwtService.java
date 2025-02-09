package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.RequestLoginDto;
import com.anjox.Gamebox_api.dto.ResponseLoginDto;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseLoginDto getUserToken (RequestLoginDto login){
        UserEntity user = userRepository.findByUsername(login.username());
        return new ResponseLoginDto(
                generateAccessToken(user),
                generateRefreshToken(user)
        );
    }
    public String refreshAccessToken(String refreshToken) {
        String username = validateTokenJwt(refreshToken);
        if (!username.isEmpty()) {
            UserEntity user = userRepository.findByUsername(username);
            return generateAccessToken(user);
        }
        throw new RuntimeException("Refresh token inválido");
    }

    public String validateTokenJwt(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("Gamebox")
                    .build()
                    .verify(token);
            String tokenType = decodedJWT.getClaim("type").asString();

            if ("access".equals(tokenType)) {
                return decodedJWT.getSubject();
            } else if ("refresh".equals(tokenType)) {
                return decodedJWT.getSubject();
            }
        } catch (JWTVerificationException exception) {
            return "";
        }
        return "";
    }

    public String generateAccessToken(UserEntity user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        try {
            return JWT.create()
                    .withIssuer("Gamebox")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genExpiration(1))
                    .withClaim("type", "access")
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar access token", exception);
        }
    }

    public String generateRefreshToken(UserEntity user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        try {
            return JWT.create()
                    .withIssuer("Gamebox")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genExpiration(24))
                    .withClaim("type", "refresh")
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar refresh token", exception);
        }
    }

    public String getUsernameFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    private Instant genExpiration(int hours){
        return LocalDateTime.now().plusHours(hours).toInstant(ZoneOffset.of("-03:00"));
    }

}

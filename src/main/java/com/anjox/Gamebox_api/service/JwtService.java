package com.anjox.Gamebox_api.service;

import com.anjox.Gamebox_api.dto.ResponseJwtTokensDto;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;


    public ResponseJwtTokensDto getJwtUserToken (UserDetails userDetails) {

        String accessToken = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);
        return new ResponseJwtTokensDto(
                accessToken,
                refreshToken
        );
    }

    public String generateAccessToken(UserDetails user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Game-Box")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genExpiration(accessTokenExpiration))
                    .withClaim("type", "access")
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new MessageErrorExeption("Erro ao gerar token", HttpStatus.NOT_FOUND);
        }
    }

    public String generateRefreshToken(UserDetails user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Game-Box")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genExpiration(refreshTokenExpiration))
                    .withClaim("type", "refresh")
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new MessageErrorExeption("Erro ao gerar refresh token", HttpStatus.NOT_FOUND);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer("Game-Box")
                    .build()
                    .verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }
    public String getTypeFromToken(String token) {
        return JWT.decode(token).getClaim("type").asString();
    }

    private Instant genExpiration(long milliseconds) {
        return LocalDateTime.now()
                .plusSeconds(milliseconds / 1000)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}

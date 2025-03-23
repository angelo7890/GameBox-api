package com.anjox.Gamebox_api.security.service;

import com.anjox.Gamebox_api.dto.ResponseJwtTokensDto;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.security.UserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;


    public ResponseJwtTokensDto getJwtUserToken (UserPrincipal user) {

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return new ResponseJwtTokensDto(
                accessToken,
                refreshToken
        );
    }

    public String generateAccessToken(UserPrincipal user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Game-Box")
                    .withSubject(user.getUsername())
                    .withClaim("userId", user.getId())
                    .withClaim("role", user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .withClaim("type", "access")
                    .withExpiresAt(genExpiration(accessTokenExpiration))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new MessageErrorExeption("Erro ao gerar access token", HttpStatus.NOT_FOUND);
        }
    }

    public String generateRefreshToken(UserPrincipal user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Game-Box")
                    .withSubject(user.getUsername())
                    .withClaim("type", "refresh")
                    .withExpiresAt(genExpiration(refreshTokenExpiration))
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

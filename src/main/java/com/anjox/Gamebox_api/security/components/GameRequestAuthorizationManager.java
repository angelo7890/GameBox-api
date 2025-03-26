package com.anjox.Gamebox_api.security.components;

import com.anjox.Gamebox_api.entity.GameEntity;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.repository.GameRepository;
import com.anjox.Gamebox_api.security.UserPrincipal;
import com.anjox.Gamebox_api.security.service.AuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class GameRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final Logger logger = LoggerFactory.getLogger(GameRequestAuthorizationManager.class);

    private static final UriTemplate CREATE_GAME_URI_TEMPLATE = new UriTemplate("/api/game");
    private static final UriTemplate GAME_URI_TEMPLATE = new UriTemplate("/api/game/{gameId}");
    private static final UriTemplate USER_GAMES_URI_TEMPLATE = new UriTemplate("/api/game/user/{userId}");
    private static final UriTemplate FILTER_GAMES_URI_TEMPLATE = new UriTemplate("/api/game/filter/{userId}/{genre}");
    private static final UriTemplate UPDATE_GAMES_URI_TEMPLATE = new UriTemplate("/api/game/update/{gameId}");
    private static final UriTemplate UPDATE_PICTURE_URI_TEMPLATE = new UriTemplate("/api/game/update/picture/{gameId}");

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final GameRepository gameRepository;
    private final ObjectMapper objectMapper;

    public GameRequestAuthorizationManager(GameRepository gameRepository, ObjectMapper objectMapper) {
        this.gameRepository = gameRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        logger.info("Iniciando verificação do check");

        Authentication auth = authenticationSupplier.get();

        if (auth == null || !auth.isAuthenticated()) {
            logger.error("Autorização negada, pois o usuário não está autenticado");
            return new AuthorizationDecision(false);
        }

        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_ADMIN));

        String requestUri = context.getRequest().getRequestURI();

        if (GAME_URI_TEMPLATE.matches(requestUri)) {
            return handleGameUri(user, isAdmin, requestUri);
        }

        if (USER_GAMES_URI_TEMPLATE.matches(requestUri)) {
            Map<String, String> uriVariables = USER_GAMES_URI_TEMPLATE.match(requestUri);
            return getAuthorizationDecision(user, isAdmin, uriVariables);
        }

        if (FILTER_GAMES_URI_TEMPLATE.matches(requestUri)) {
            Map<String, String> uriVariables = FILTER_GAMES_URI_TEMPLATE.match(requestUri);
            return getAuthorizationDecision(user, isAdmin, uriVariables);
        }

        if (CREATE_GAME_URI_TEMPLATE.matches(requestUri)) {
            return handleCreateGameRequest(user, isAdmin, context);
        }

        if(UPDATE_GAMES_URI_TEMPLATE.matches(requestUri)) {
           return handleGameUri(user, isAdmin, requestUri);
        }

        if(UPDATE_PICTURE_URI_TEMPLATE.matches(requestUri)) {
             return handleGameUri(user, isAdmin, requestUri);
        }

        logger.warn("Requisição negada, pois o usuário não é admin ou está buscando informações de outro usuário");
        return new AuthorizationDecision(false);
    }

    @NotNull
    private AuthorizationDecision getAuthorizationDecision(UserPrincipal user, boolean isAdmin, Map<String, String> uriVariables) {
        String userIdFromRequestUri = uriVariables.get("userId");
        boolean isOwner = userIdFromRequestUri != null && userIdFromRequestUri.equals(user.getId().toString());

        logger.info("User é owner: " + isOwner);
        logger.info("User é admin: " + isAdmin);

        return new AuthorizationDecision(isAdmin || isOwner);
    }

    private AuthorizationDecision handleGameUri(UserPrincipal user, boolean isAdmin, String requestUri) {
        Map<String, String> uriVariables = GAME_URI_TEMPLATE.match(requestUri);
        String gameIdFromRequestUri = uriVariables.get("gameId");

        if (gameIdFromRequestUri == null) {
            return new AuthorizationDecision(false);
        }

        Optional<GameEntity> game = gameRepository.findById(Long.parseLong(gameIdFromRequestUri));
        boolean isOwner = game.isPresent() && game.get().getUserId().equals(user.getId());

        logger.info("User é owner: " + isOwner);
        logger.info("User é admin: " + isAdmin);

        return new AuthorizationDecision(isAdmin || isOwner);
    }

    private AuthorizationDecision handleCreateGameRequest(UserPrincipal user, boolean isAdmin, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();

        try {
            Map<String, Object> requestBody = objectMapper.readValue(request.getInputStream(), Map.class);
            Long userIdFromRequest = ((Number) requestBody.get("userId")).longValue();

            boolean isOwner = userIdFromRequest.equals(user.getId());

            logger.info("User é owner: " + isOwner);
            logger.info("User é admin: " + isAdmin);

            return new AuthorizationDecision(isAdmin || isOwner);

        } catch (IOException e) {
            logger.error("Erro ao ler o corpo da requisição para criar o jogo", e);
            return new AuthorizationDecision(false);
        }
    }
}

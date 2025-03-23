package com.anjox.Gamebox_api.security.components;

import com.anjox.Gamebox_api.entity.GameEntity;
import com.anjox.Gamebox_api.repository.GameRepository;
import com.anjox.Gamebox_api.security.UserPrincipal;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class GameRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final UriTemplate GAME_URI_TEMPLATE = new UriTemplate("/api/game/{gameId}");
    private static final UriTemplate USER_GAMES_URI_TEMPLATE = new UriTemplate("/api/game/user/{userId}");
    private static final UriTemplate FILTER_GAMES_URI_TEMPLATE = new UriTemplate("/api/game/filter/{userId}/{genre}");

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final GameRepository gameRepository;

    public GameRequestAuthorizationManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication auth = authenticationSupplier.get();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_ADMIN));

        String requestUri = context.getRequest().getRequestURI();

        if (GAME_URI_TEMPLATE.matches(requestUri)) {
            Map<String, String> uriVariables = GAME_URI_TEMPLATE.match(requestUri);
            String gameIdFromRequestUri = uriVariables.get("gameId");

            if (gameIdFromRequestUri == null) {
                return new AuthorizationDecision(false);
            }

            Optional<GameEntity> game = gameRepository.findById(Long.parseLong(gameIdFromRequestUri));
            boolean isOwner = game.isPresent() && game.get().getUserId().equals(user.getId());

            return new AuthorizationDecision(isAdmin || isOwner);
        }

        if (USER_GAMES_URI_TEMPLATE.matches(requestUri)) {
            Map<String, String> uriVariables = USER_GAMES_URI_TEMPLATE.match(requestUri);
            String userIdFromRequestUri = uriVariables.get("userId");

            boolean isOwner = userIdFromRequestUri != null && userIdFromRequestUri.equals(user.getId().toString());

            return new AuthorizationDecision(isAdmin || isOwner);
        }

        if (FILTER_GAMES_URI_TEMPLATE.matches(requestUri)) {
            Map<String, String> uriVariables = FILTER_GAMES_URI_TEMPLATE.match(requestUri);
            String userIdFromRequestUri = uriVariables.get("userId");

            boolean isOwner = userIdFromRequestUri != null && userIdFromRequestUri.equals(user.getId().toString());

            return new AuthorizationDecision(isAdmin || isOwner);
        }

        return new AuthorizationDecision(false);
    }
}

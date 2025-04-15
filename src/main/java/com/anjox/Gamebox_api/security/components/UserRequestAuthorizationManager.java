package com.anjox.Gamebox_api.security.components;

import com.anjox.Gamebox_api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UserRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/api/user/{userId}");
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";
    private final Logger logger = LoggerFactory.getLogger(UserRequestAuthorizationManager.class);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {

        logger.info("iniciando check");

        Authentication auth = authenticationSupplier.get();

        if (auth == null) {
            return new AuthorizationDecision(false);
        }

        Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
        String userIdFromRequestUri = uriVariables.get("userId");

        logger.info("Pegando o user na autenticaçao");

        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        String id = user.getId().toString();

        logger.info("Iniciando verificaçao se o user buscado é o mesmo da autenticaçao");

        boolean userIdsMatch = userIdFromRequestUri != null && userIdFromRequestUri.equals(id);
        logger.info("teste: "+userIdsMatch);

        boolean hasAdminRole = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_ADMIN));

        logger.info("User é admin: "+hasAdminRole);

        boolean hasUserRole = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_USER));

        logger.info("User buscado é o da requisiçao: "+hasUserRole);

        logger.info("Retornado o authorization Decision");

        return new AuthorizationDecision(hasAdminRole || (hasUserRole && userIdsMatch));

    }
}

package com.anjox.Gamebox_api.security.components;

import com.anjox.Gamebox_api.security.UserPrincipal;
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

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {

        Authentication auth = authenticationSupplier.get();

        if (auth == null) {
            return new AuthorizationDecision(false);
        }

        Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
        String userIdFromRequestUri = uriVariables.get("userId");

        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        String id = user.getId().toString();

        boolean userIdsMatch = userIdFromRequestUri != null && userIdFromRequestUri.equals(id);

        boolean hasAdminRole = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_ADMIN));

        boolean hasUserRole = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_USER));

        return new AuthorizationDecision(hasAdminRole || (hasUserRole && userIdsMatch));

    }
}

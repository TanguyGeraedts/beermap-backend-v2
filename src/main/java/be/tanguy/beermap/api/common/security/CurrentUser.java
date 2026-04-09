package be.tanguy.beermap.api.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public String getIssuer() {
        Jwt jwt = getJwt();
        return jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
    }

    public String getSubject() {
        return getJwt().getSubject();
    }

    private Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT found");
        }

        return jwt;
    }
}
package be.tanguy.beermap.api.common.security;

import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public String getIssuer() {
        return "dev-issuer";
    }

    public String getSubject() {
        return "dev-user-123";
    }
}
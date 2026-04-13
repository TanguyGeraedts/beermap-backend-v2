package be.tanguy.beermap.api.common.security;

import be.tanguy.beermap.api.users.domain.AccountType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class UserIdentityAuthenticationToken extends AbstractAuthenticationToken {

    private final UserIdentity principal;

    public UserIdentityAuthenticationToken(UserIdentity principal, AccountType accountType) {
        super(authoritiesFrom(accountType));
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserIdentity getPrincipal() {
        return principal;
    }

    private static Collection<? extends GrantedAuthority> authoritiesFrom(AccountType accountType) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + accountType.name()));
    }
}
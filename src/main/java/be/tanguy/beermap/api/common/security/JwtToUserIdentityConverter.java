package be.tanguy.beermap.api.common.security;

import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.repository.AuthIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtToUserIdentityConverter implements Converter<Jwt, UserIdentityAuthenticationToken> {

    private final AuthIdentityRepository authIdentityRepository;

    @Override
    public UserIdentityAuthenticationToken convert(Jwt jwt) {
        String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
        String subject = jwt.getSubject();

        UserIdentity identity = new UserIdentity(issuer, subject);

        // Look up AccountType from DB — repository already JOIN FETCHes userProfile
        AccountType accountType = authIdentityRepository
                .findByIssuerAndSubject(issuer, subject)
                .map(auth -> auth.getUserProfile().getAccountType())
                .orElse(AccountType.NORMAL);

        return new UserIdentityAuthenticationToken(identity, accountType);
    }
}
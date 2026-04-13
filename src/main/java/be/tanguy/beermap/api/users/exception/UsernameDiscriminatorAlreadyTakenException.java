package be.tanguy.beermap.api.users.exception;

import be.tanguy.beermap.api.common.web.ApiException;
import org.springframework.http.HttpStatus;

public class UsernameDiscriminatorAlreadyTakenException extends ApiException {

    public UsernameDiscriminatorAlreadyTakenException(String username, String discriminator) {
        super("Username/targetDiscriminator already taken: " + username + "#" + discriminator);
    }

    @Override public HttpStatus getStatus() { return HttpStatus.CONFLICT; }
    @Override public String getCode()       { return "USERNAME_DISCRIMINATOR_ALREADY_TAKEN"; }
}

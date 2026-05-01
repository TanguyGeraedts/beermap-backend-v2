package be.tanguy.beermap.api.users.exception;

import org.springframework.http.HttpStatus;

public class UsernameDiscriminatorAlreadyTakenException extends UserDomainException {
    public UsernameDiscriminatorAlreadyTakenException(String username, String discriminator) {
        super(HttpStatus.CONFLICT, "USERNAME_DISCRIMINATOR_ALREADY_TAKEN",
                "Username/targetDiscriminator already taken: " + username + "#" + discriminator);
    }
}
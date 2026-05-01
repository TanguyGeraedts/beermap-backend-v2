package be.tanguy.beermap.api.users.exception;

import org.springframework.http.HttpStatus;

public class InvalidDiscriminatorException extends UserDomainException {
    public InvalidDiscriminatorException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_DISCRIMINATOR", message);
    }
}

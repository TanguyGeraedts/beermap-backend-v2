package be.tanguy.beermap.api.users.exception;

import org.springframework.http.HttpStatus;

public class ProfileAlreadyExistsException extends UserDomainException {
    public ProfileAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "PROFILE_ALREADY_EXISTS", "User profile already exists");
    }
}

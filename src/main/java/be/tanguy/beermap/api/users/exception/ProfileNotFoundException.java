package be.tanguy.beermap.api.users.exception;

import org.springframework.http.HttpStatus;

public class ProfileNotFoundException extends UserDomainException {
    public ProfileNotFoundException() {
        super(HttpStatus.NOT_FOUND, "PROFILE_NOT_FOUND", "User profile not found");
    }
}

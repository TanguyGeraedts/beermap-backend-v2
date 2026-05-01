package be.tanguy.beermap.api.users.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserProfileNotFoundException extends UserDomainException {
    public UserProfileNotFoundException(UUID userProfileId) {
        super(HttpStatus.NOT_FOUND, "USER_PROFILE_NOT_FOUND", "User profile not found: " + userProfileId);
    }
}

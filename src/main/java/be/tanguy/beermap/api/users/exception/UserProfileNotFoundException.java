package be.tanguy.beermap.api.users.exception;

import java.util.UUID;

public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException(UUID userProfileId) {
        super("User profile not found: " + userProfileId);
    }
}
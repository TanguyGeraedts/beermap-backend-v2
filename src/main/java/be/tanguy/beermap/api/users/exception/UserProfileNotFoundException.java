package be.tanguy.beermap.api.users.exception;

import be.tanguy.beermap.api.common.web.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserProfileNotFoundException extends ApiException {

    public UserProfileNotFoundException(UUID userProfileId) {
        super("User profile not found: " + userProfileId);
    }

    @Override public HttpStatus getStatus() { return HttpStatus.NOT_FOUND; }
    @Override public String getCode()       { return "USER_PROFILE_NOT_FOUND"; }
}

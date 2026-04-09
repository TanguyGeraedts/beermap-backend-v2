package be.tanguy.beermap.api.users.exception;

import be.tanguy.beermap.api.common.web.ApiException;
import org.springframework.http.HttpStatus;

public class ProfileAlreadyExistsException extends ApiException {

    public ProfileAlreadyExistsException() {
        super("User profile already exists");
    }

    @Override public HttpStatus getStatus() { return HttpStatus.CONFLICT; }
    @Override public String getCode()       { return "PROFILE_ALREADY_EXISTS"; }
}

package be.tanguy.beermap.api.users.exception;

import be.tanguy.beermap.api.common.web.ApiException;
import org.springframework.http.HttpStatus;

public class ProfileNotFoundException extends ApiException {

    public ProfileNotFoundException() {
        super("User profile not found");
    }

    @Override public HttpStatus getStatus() { return HttpStatus.NOT_FOUND; }
    @Override public String getCode()       { return "PROFILE_NOT_FOUND"; }
}

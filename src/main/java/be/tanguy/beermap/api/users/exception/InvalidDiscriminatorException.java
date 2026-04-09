package be.tanguy.beermap.api.users.exception;

import be.tanguy.beermap.api.common.web.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidDiscriminatorException extends ApiException {

    public InvalidDiscriminatorException(String message) {
        super(message);
    }

    @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }
    @Override public String getCode()       { return "INVALID_DISCRIMINATOR"; }
}

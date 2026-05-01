package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

public class InvalidLocationException extends DrinkLogDomainException {
    public InvalidLocationException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_LOCATION", message);
    }
}

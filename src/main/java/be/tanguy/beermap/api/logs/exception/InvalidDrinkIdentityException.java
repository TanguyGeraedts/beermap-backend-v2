package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

public class InvalidDrinkIdentityException extends DrinkLogDomainException {
    public InvalidDrinkIdentityException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_DRINK_IDENTITY", message);
    }
}
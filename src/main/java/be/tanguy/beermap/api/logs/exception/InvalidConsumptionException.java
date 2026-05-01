package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

public class InvalidConsumptionException extends DrinkLogDomainException {
    public InvalidConsumptionException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_CONSUMPTION", message);
    }
}
package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DrinkLogNotFoundException extends DrinkLogDomainException {
    public DrinkLogNotFoundException(UUID logId) {
        super(HttpStatus.NOT_FOUND, "DRINK_LOG_NOT_FOUND", "Drink log not found with ID: " + logId);
    }
}
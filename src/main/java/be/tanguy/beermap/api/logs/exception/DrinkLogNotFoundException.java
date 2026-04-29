package be.tanguy.beermap.api.logs.exception;

import java.util.UUID;

public class DrinkLogNotFoundException extends DrinkLogDomainException {
    public DrinkLogNotFoundException(UUID logId) {
        super("Drink log not found with ID: " + logId);
    }
}
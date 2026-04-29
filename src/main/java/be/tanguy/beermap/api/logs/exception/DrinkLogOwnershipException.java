package be.tanguy.beermap.api.logs.exception;

import java.util.UUID;

public class DrinkLogOwnershipException extends DrinkLogDomainException {
    public DrinkLogOwnershipException(UUID userId, UUID logId) {
        super("User " + userId + " does not own drink log " + logId);
    }
}
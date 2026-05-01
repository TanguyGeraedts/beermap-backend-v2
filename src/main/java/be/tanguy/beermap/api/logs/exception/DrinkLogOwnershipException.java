package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DrinkLogOwnershipException extends DrinkLogDomainException {
    public DrinkLogOwnershipException(UUID userId, UUID logId) {
        super(HttpStatus.FORBIDDEN, "DRINK_LOG_OWNERSHIP", "User " + userId + " does not own drink log " + logId);
    }
}
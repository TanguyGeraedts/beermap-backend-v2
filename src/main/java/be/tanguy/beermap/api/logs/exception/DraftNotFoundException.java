package be.tanguy.beermap.api.logs.exception;

import java.util.UUID;

public class DraftNotFoundException extends DrinkLogDomainException {
    public DraftNotFoundException(UUID draftId) {
        super("Draft log not found with ID: " + draftId);
    }
}
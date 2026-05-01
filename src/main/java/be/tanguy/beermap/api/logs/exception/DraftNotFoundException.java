package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DraftNotFoundException extends DrinkLogDomainException {
    public DraftNotFoundException(UUID draftId) {
        super(HttpStatus.NOT_FOUND, "DRAFT_NOT_FOUND", "Draft log not found with ID: " + draftId);
    }
}
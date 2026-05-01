package be.tanguy.beermap.api.logs.exception;

import be.tanguy.beermap.api.logs.domain.DraftStatus;
import org.springframework.http.HttpStatus;

public class InvalidDraftStateException extends DrinkLogDomainException {
    public InvalidDraftStateException(DraftStatus current, DraftStatus expected) {
        super(HttpStatus.CONFLICT, "INVALID_DRAFT_STATE",
                "Invalid draft state. Expected: " + expected + ", but was: " + current);
    }
}
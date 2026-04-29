package be.tanguy.beermap.api.logs.exception;

import be.tanguy.beermap.api.logs.domain.DraftStatus;

public class InvalidDraftStateException extends RuntimeException {

    public InvalidDraftStateException(DraftStatus current, DraftStatus expected) {
        super("Invalid draft state. Expected: " + expected + ", but was: " + current);
    }

}
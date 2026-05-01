package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

public class DraftLimitExceededException extends DrinkLogDomainException {
    public DraftLimitExceededException(int limit) {
        super(HttpStatus.FORBIDDEN, "DRAFT_LIMIT_EXCEEDED", "Active draft limit of " + limit + " reached");
    }
}

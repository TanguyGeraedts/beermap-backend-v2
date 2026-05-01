package be.tanguy.beermap.api.feed.exception;

import be.tanguy.beermap.api.common.web.ApiException;
import org.springframework.http.HttpStatus;

public abstract class FeedDomainException extends ApiException {
    private final HttpStatus status;
    private final String code;

    protected FeedDomainException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    @Override public HttpStatus getStatus() { return status; }
    @Override public String getCode() { return code; }
}
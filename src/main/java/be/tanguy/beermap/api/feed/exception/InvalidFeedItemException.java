package be.tanguy.beermap.api.feed.exception;

import org.springframework.http.HttpStatus;

public class InvalidFeedItemException extends FeedDomainException {
    public InvalidFeedItemException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_FEED_ITEM", message);
    }
}
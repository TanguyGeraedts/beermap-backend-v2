package be.tanguy.beermap.api.feed.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class FeedItemNotFoundException extends FeedDomainException {
    public FeedItemNotFoundException(UUID feedItemId) {
        super(HttpStatus.NOT_FOUND, "FEED_ITEM_NOT_FOUND", "Feed item not found: " + feedItemId);
    }
}
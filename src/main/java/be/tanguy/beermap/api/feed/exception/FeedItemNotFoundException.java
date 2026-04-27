package be.tanguy.beermap.api.feed.exception;

import be.tanguy.beermap.api.common.web.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class FeedItemNotFoundException extends ApiException {

    public FeedItemNotFoundException(UUID feedItemId) {
        super("Feed item not found: " + feedItemId);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getCode() {
        return "FEED_ITEM_NOT_FOUND";
    }
}

package be.tanguy.beermap.api.feed.repository.projection;

import java.util.UUID;

public interface FeedItemCheersCountProjection {

    UUID getFeedItemId();

    long getCheersCount();
}

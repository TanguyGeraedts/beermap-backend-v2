package be.tanguy.beermap.api.feed.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.feed.web.dto.FeedCheersResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedPageResponse;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;

import java.time.Instant;
import java.util.UUID;

public interface FeedItemService {

    void createDrinkFeedItem(DrinkLogCreatedEvent drinkLogCreatedEvent);

    FeedPageResponse getFeed(UserIdentity userIdentity, Instant cursor, int limit);

    FeedCheersResponse cheerFeedItem(UserIdentity userIdentity, UUID feedItemId);

    FeedCheersResponse uncheerFeedItem(UserIdentity userIdentity, UUID feedItemId);
}

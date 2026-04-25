package be.tanguy.beermap.api.feed.service;


import be.tanguy.beermap.api.feed.web.dto.FeedPageResponse;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;

import java.time.Instant;

public interface FeedItemService {

    void createDrinkFeedItem(DrinkLogCreatedEvent drinkLogCreatedEvent);

    FeedPageResponse getFeed(Instant cursor, int limit);
}

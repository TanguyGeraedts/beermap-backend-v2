package be.tanguy.beermap.api.feed.service;


import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;

public interface FeedItemService {

    void createDrinkFeedItem(DrinkLogCreatedEvent drinkLogCreatedEvent);

}

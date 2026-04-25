package be.tanguy.beermap.api.feed.event;

import be.tanguy.beermap.api.feed.service.FeedItemService;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DrinkLogFeedListener {

    private final FeedItemService feedItemService;

    @EventListener
    public void handleDrinkLogCreated(DrinkLogCreatedEvent event) {
        log.debug(
                "Handling DrinkLogCreatedEvent for userId={} logId={}",
                event.userId(),
                event.logId()
        );

        feedItemService.createDrinkFeedItem(event);
    }
}
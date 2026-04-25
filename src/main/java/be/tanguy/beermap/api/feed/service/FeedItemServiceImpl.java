package be.tanguy.beermap.api.feed.service;

import be.tanguy.beermap.api.feed.domain.FeedItem;
import be.tanguy.beermap.api.feed.domain.FeedItemType;
import be.tanguy.beermap.api.feed.domain.FeedTargetType;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;
import be.tanguy.beermap.api.feed.repository.FeedItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedItemServiceImpl implements FeedItemService {

    /// Repositories
    private final FeedItemRepository feedItemRepository;

    @Override
    public void createDrinkFeedItem(DrinkLogCreatedEvent drinkLogCreatedEvent) {
        FeedItem feedItem = FeedItem.create(
                drinkLogCreatedEvent.userId(),
                FeedItemType.DRINK_LOG_CREATED,
                FeedTargetType.DRINK_LOG,
                drinkLogCreatedEvent.logId(),
                drinkLogCreatedEvent.feedVisibility()
        );

        feedItemRepository.save(feedItem);

        log.debug(
                "Created drink log feed item for userId={} logId={}",
                drinkLogCreatedEvent.userId(),
                drinkLogCreatedEvent.logId()
        );
    }
}
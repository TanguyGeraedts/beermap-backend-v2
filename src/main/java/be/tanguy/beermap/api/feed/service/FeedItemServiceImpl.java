package be.tanguy.beermap.api.feed.service;

import be.tanguy.beermap.api.feed.domain.FeedItem;
import be.tanguy.beermap.api.feed.domain.FeedItemType;
import be.tanguy.beermap.api.feed.domain.FeedTargetType;
import be.tanguy.beermap.api.feed.domain.FeedVisibility;
import be.tanguy.beermap.api.feed.web.dto.FeedItemMapper;
import be.tanguy.beermap.api.feed.web.dto.FeedPageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;
import be.tanguy.beermap.api.feed.repository.FeedItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public FeedPageResponse getFeed(Instant cursor, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);

        Pageable pageable = PageRequest.of(0, safeLimit + 1);

        List<FeedVisibility> allowedVisibilities = List.of(
                FeedVisibility.PUBLIC
        );

        List<FeedItem> feedItems;

        if (cursor == null) {
            feedItems = feedItemRepository.findByVisibilityInOrderByCreatedAtDesc(
                    allowedVisibilities,
                    pageable
            );
        } else {
            feedItems = feedItemRepository.findByCreatedAtBeforeAndVisibilityInOrderByCreatedAtDesc(
                    cursor,
                    allowedVisibilities,
                    pageable
            );
        }

        boolean hasMore = feedItems.size() > safeLimit;

        List<FeedItem> visibleItems = hasMore
                ? feedItems.subList(0, safeLimit)
                : feedItems;

        Instant nextCursor = visibleItems.isEmpty()
                ? null
                : visibleItems.get(visibleItems.size() - 1).getCreatedAt();

        return new FeedPageResponse(
                visibleItems.stream()
                        .map(FeedItemMapper::toResponse)
                        .toList(),
                nextCursor,
                hasMore
        );
    }
}
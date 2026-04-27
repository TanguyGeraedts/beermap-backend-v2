package be.tanguy.beermap.api.feed.service;

import be.tanguy.beermap.api.feed.domain.FeedItem;
import be.tanguy.beermap.api.feed.domain.FeedItemType;
import be.tanguy.beermap.api.feed.domain.FeedTargetType;
import be.tanguy.beermap.api.feed.domain.FeedVisibility;
import be.tanguy.beermap.api.feed.web.dto.FeedActorResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedDrinkLogDetailsResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedItemDetailsResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedItemMapper;
import be.tanguy.beermap.api.feed.web.dto.FeedItemResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedPageResponse;
import be.tanguy.beermap.api.logs.domain.DrinkLog;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;
import be.tanguy.beermap.api.feed.repository.FeedItemRepository;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
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
    private final UserProfileRepository userProfileRepository;
    private final DrinkLogRepository drinkLogRepository;

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

        List<FeedItemResponse> responses = visibleItems.stream()
                .map(this::toResponse)
                .toList();

        return new FeedPageResponse(responses, nextCursor, hasMore);
    }

    private FeedItemResponse toResponse(FeedItem feedItem) {
        FeedActorResponse actor = userProfileRepository.findById(feedItem.getUserId())
                .map(this::toActorResponse)
                .orElse(null);

        FeedDrinkLogDetailsResponse drinkLogDetails = null;
        if (feedItem.getTargetType() == FeedTargetType.DRINK_LOG) {
            drinkLogDetails = drinkLogRepository.findById(feedItem.getTargetId())
                    .map(this::toDrinkLogDetailsResponse)
                    .orElse(null);
        }

        FeedItemDetailsResponse details = new FeedItemDetailsResponse(actor, drinkLogDetails);

        return FeedItemMapper.toResponse(feedItem, details);
    }

    private FeedActorResponse toActorResponse(UserProfile userProfile) {
        return new FeedActorResponse(
                userProfile.getId(),
                userProfile.getInfo().getUsername(),
                userProfile.getInfo().getDiscriminator(),
                userProfile.getInfo().getAvatarUrl()
        );
    }

    private FeedDrinkLogDetailsResponse toDrinkLogDetailsResponse(DrinkLog drinkLog) {
        return new FeedDrinkLogDetailsResponse(
                drinkLog.getId(),
                drinkLog.getDisplayName(),
                drinkLog.getIdentity().getCategory().name(),
                drinkLog.getIdentity().getSubType(),
                drinkLog.getConsumption().getQuantity(),
                drinkLog.getConsumption().getRating(),
                drinkLog.getConsumption().getPhotoUrl(),
                drinkLog.getConsumption().getConsumedAt()
        );
    }
}

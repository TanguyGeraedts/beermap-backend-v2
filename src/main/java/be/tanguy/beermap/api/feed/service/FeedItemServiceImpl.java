package be.tanguy.beermap.api.feed.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.feed.domain.FeedItem;
import be.tanguy.beermap.api.feed.domain.FeedItemCheers;
import be.tanguy.beermap.api.feed.domain.FeedItemType;
import be.tanguy.beermap.api.feed.domain.FeedTargetType;
import be.tanguy.beermap.api.feed.domain.FeedVisibility;
import be.tanguy.beermap.api.feed.exception.FeedItemNotFoundException;
import be.tanguy.beermap.api.feed.repository.FeedItemCheersRepository;
import be.tanguy.beermap.api.feed.web.dto.FeedActorResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedCheersResponse;
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
import be.tanguy.beermap.api.feed.repository.projection.FeedItemCheersCountProjection;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
import be.tanguy.beermap.api.users.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedItemServiceImpl implements FeedItemService {

    /// Auth
    private final UserProfileService userProfileService;

    /// Repositories
    private final FeedItemRepository feedItemRepository;
    private final FeedItemCheersRepository feedItemCheersRepository;
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
    public FeedPageResponse getFeed(UserIdentity userIdentity, Instant cursor, int limit) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        int safeLimit = Math.min(Math.max(limit, 1), 50);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);
        List<FeedVisibility> allowedVisibilities = List.of(FeedVisibility.PUBLIC);

        List<FeedItem> feedItems = (cursor == null)
                ? feedItemRepository.findByVisibilityInOrderByCreatedAtDesc(
                        allowedVisibilities,
                        pageable
                )
                : feedItemRepository.findByCreatedAtBeforeAndVisibilityInOrderByCreatedAtDesc(
                        cursor,
                        allowedVisibilities,
                        pageable
                );

        boolean hasMore = feedItems.size() > safeLimit;
        List<FeedItem> visibleItems = hasMore ? feedItems.subList(0, safeLimit) : feedItems;
        Instant nextCursor = visibleItems.isEmpty()
                ? null
                : visibleItems.get(visibleItems.size() - 1).getCreatedAt();

        Map<UUID, Long> cheersCountByFeedItemId = getCheersCountByFeedItemId(visibleItems);
        Set<UUID> cheeredFeedItemIds = getCheeredFeedItemIds(visibleItems, currentUserId);

        List<FeedItemResponse> responses = visibleItems.stream()
                .map(feedItem -> toResponse(
                        feedItem,
                        cheersCountByFeedItemId.getOrDefault(feedItem.getId(), 0L),
                        cheeredFeedItemIds.contains(feedItem.getId())
                ))
                .toList();

        return new FeedPageResponse(responses, nextCursor, hasMore);
    }

    @Override
    @Transactional
    public FeedCheersResponse cheerFeedItem(UserIdentity userIdentity, UUID feedItemId) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        FeedItem feedItem = getFeedItemOrThrow(feedItemId);
        validateFeedItemCanBeCheered(feedItem);

        boolean alreadyCheered = feedItemCheersRepository.existsByFeedItemIdAndUserId(
                feedItemId,
                currentUserId
        );

        if (!alreadyCheered) {
            feedItemCheersRepository.save(FeedItemCheers.create(feedItemId, currentUserId));
        }

        return buildCheersResponse(feedItemId, currentUserId);
    }

    @Override
    @Transactional
    public FeedCheersResponse uncheerFeedItem(UserIdentity userIdentity, UUID feedItemId) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        FeedItem feedItem = getFeedItemOrThrow(feedItemId);
        validateFeedItemCanBeCheered(feedItem);

        feedItemCheersRepository.deleteByFeedItemIdAndUserId(feedItemId, currentUserId);

        return buildCheersResponse(feedItemId, currentUserId);
    }

    private FeedItemResponse toResponse(
            FeedItem feedItem,
            long cheersCount,
            boolean cheeredByMe
    ) {
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

        return FeedItemMapper.toResponse(feedItem, details, cheersCount, cheeredByMe);
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

    private Map<UUID, Long> getCheersCountByFeedItemId(List<FeedItem> feedItems) {
        List<UUID> feedItemIds = feedItems.stream().map(FeedItem::getId).toList();

        if (feedItemIds.isEmpty()) {
            return Map.of();
        }

        return feedItemCheersRepository.countCheersByFeedItemIds(feedItemIds).stream()
                .collect(Collectors.toMap(
                        FeedItemCheersCountProjection::getFeedItemId,
                        FeedItemCheersCountProjection::getCheersCount
                ));
    }

    private Set<UUID> getCheeredFeedItemIds(List<FeedItem> feedItems, UUID userId) {
        List<UUID> feedItemIds = feedItems.stream().map(FeedItem::getId).toList();

        if (feedItemIds.isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(
                feedItemCheersRepository.findCheeredFeedItemIdsByUserId(feedItemIds, userId)
        );
    }

    private FeedCheersResponse buildCheersResponse(UUID feedItemId, UUID userId) {
        long cheersCount = feedItemCheersRepository.countByFeedItemId(feedItemId);
        boolean cheeredByMe = feedItemCheersRepository.existsByFeedItemIdAndUserId(feedItemId, userId);

        return new FeedCheersResponse(feedItemId, cheersCount, cheeredByMe);
    }

    private FeedItem getFeedItemOrThrow(UUID feedItemId) {
        return feedItemRepository.findById(feedItemId)
                .orElseThrow(() -> new FeedItemNotFoundException(feedItemId));
    }

    private void validateFeedItemCanBeCheered(FeedItem feedItem) {
        if (!feedItem.isPublic()) {
            throw new IllegalArgumentException("Only public feed items can be cheered");
        }
    }
}

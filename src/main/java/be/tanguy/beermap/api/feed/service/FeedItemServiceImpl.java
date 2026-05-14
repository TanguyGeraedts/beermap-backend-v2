package be.tanguy.beermap.api.feed.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.feed.domain.*;
import be.tanguy.beermap.api.feed.exception.FeedItemNotFoundException;
import be.tanguy.beermap.api.feed.repository.FeedItemCheersRepository;
import be.tanguy.beermap.api.feed.repository.FeedItemRepository;
import be.tanguy.beermap.api.feed.repository.projection.FeedItemCheersCountProjection;
import be.tanguy.beermap.api.feed.web.dto.*;
import be.tanguy.beermap.api.logs.domain.DrinkLog;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
import be.tanguy.beermap.api.users.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
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
    public void createDrinkFeedItem(DrinkLogCreatedEvent event) {
        FeedItem feedItem = FeedItem.create(
                event.userId(),
                FeedItemType.DRINK_LOG_CREATED,
                FeedTargetType.DRINK_LOG,
                event.logId(),
                event.feedVisibility()
        );

        feedItemRepository.save(feedItem);

        log.debug("Created drink log feed item for userId={} logId={}", event.userId(), event.logId());
    }

    @Override
    @Transactional(readOnly = true)
    public FeedPageResponse getFeed(UserIdentity userIdentity, Instant cursor, int limit) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        int safeLimit = Math.min(Math.max(limit, 1), 50);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);
        List<FeedVisibility> allowedVisibilities = List.of(FeedVisibility.PUBLIC);

        List<FeedItem> feedItems = (cursor == null)
                ? feedItemRepository.findByVisibilityInOrderByCreatedAtDesc(allowedVisibilities, pageable)
                : feedItemRepository.findByCreatedAtBeforeAndVisibilityInOrderByCreatedAtDesc(cursor, allowedVisibilities, pageable);

        boolean hasMore = feedItems.size() > safeLimit;
        List<FeedItem> visibleItems = hasMore ? feedItems.subList(0, safeLimit) : feedItems;
        Instant nextCursor = visibleItems.isEmpty() ? null : visibleItems.getLast().getCreatedAt();

        Map<UUID, Long> cheersCountByFeedItemId = getCheersCountByFeedItemId(visibleItems);
        Set<UUID> cheeredFeedItemIds = getCheeredFeedItemIds(visibleItems, currentUserId);

        // Batch-load actors and drink logs
        Set<UUID> userIds = visibleItems.stream().map(FeedItem::getUserId).collect(Collectors.toSet());
        Map<UUID, UserProfile> profilesById = userProfileRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserProfile::getId, p -> p));

        Set<UUID> drinkLogIds = visibleItems.stream()
                .filter(f -> f.getTargetType() == FeedTargetType.DRINK_LOG)
                .map(FeedItem::getTargetId)
                .collect(Collectors.toSet());
        Map<UUID, DrinkLog> drinkLogsById = drinkLogRepository.findAllById(drinkLogIds).stream()
                .collect(Collectors.toMap(DrinkLog::getId, d -> d));

        List<FeedItemResponse> responses = visibleItems.stream()
                .map(feedItem -> toResponse(
                        feedItem,
                        cheersCountByFeedItemId.getOrDefault(feedItem.getId(), 0L),
                        cheeredFeedItemIds.contains(feedItem.getId()),
                        profilesById,
                        drinkLogsById
                ))
                .toList();

        return new FeedPageResponse(responses, nextCursor, hasMore);
    }

    @Override
    @Transactional
    public FeedCheersResponse cheerFeedItem(UserIdentity userIdentity, UUID feedItemId) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        FeedItem feedItem = getFeedItemOrThrow(feedItemId);
        feedItem.assertCanBeCheered();

        if (!feedItemCheersRepository.existsByFeedItemIdAndUserId(feedItemId, currentUserId)) {
            feedItemCheersRepository.save(FeedItemCheers.create(feedItemId, currentUserId));
        }

        return buildCheersResponse(feedItemId, currentUserId);
    }

    @Override
    @Transactional
    public FeedCheersResponse uncheerFeedItem(UserIdentity userIdentity, UUID feedItemId) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        getFeedItemOrThrow(feedItemId).assertCanBeCheered();
        feedItemCheersRepository.deleteByFeedItemIdAndUserId(feedItemId, currentUserId);

        return buildCheersResponse(feedItemId, currentUserId);
    }

    /// PRIVATE HELPERS
    private FeedItem getFeedItemOrThrow(UUID feedItemId) {
        return feedItemRepository.findById(feedItemId)
                .orElseThrow(() -> new FeedItemNotFoundException(feedItemId));
    }

    private FeedCheersResponse buildCheersResponse(UUID feedItemId, UUID userId) {
        long cheersCount = feedItemCheersRepository.countByFeedItemId(feedItemId);
        boolean cheeredByMe = feedItemCheersRepository.existsByFeedItemIdAndUserId(feedItemId, userId);
        return new FeedCheersResponse(feedItemId, cheersCount, cheeredByMe);
    }

    private FeedItemResponse toResponse(
            FeedItem feedItem,
            long cheersCount,
            boolean cheeredByMe,
            Map<UUID, UserProfile> profilesById,
            Map<UUID, DrinkLog> drinkLogsById
    ) {
        FeedActorResponse actor = Optional.ofNullable(profilesById.get(feedItem.getUserId()))
                .map(this::toActorResponse)
                .orElse(null);

        FeedDrinkLogDetailsResponse drinkLogDetails = null;
        if (feedItem.getTargetType() == FeedTargetType.DRINK_LOG) {
            drinkLogDetails = Optional.ofNullable(drinkLogsById.get(feedItem.getTargetId()))
                    .map(this::toDrinkLogDetailsResponse)
                    .orElse(null);
        }

        return FeedItemMapper.toResponse(feedItem, new FeedItemDetailsResponse(actor, drinkLogDetails), cheersCount, cheeredByMe);
    }

    private FeedActorResponse toActorResponse(UserProfile userProfile) {
        return new FeedActorResponse(
                userProfile.getId(),
                userProfile.getInfo().getUsername(),
                userProfile.getInfo().getDiscriminator(),
                userProfile.getInfo().getAvatarUrl(),
                userProfile.getAccountType()
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
        List<UUID> ids = feedItems.stream().map(FeedItem::getId).toList();
        if (ids.isEmpty()) return Map.of();

        return feedItemCheersRepository.countCheersByFeedItemIds(ids).stream()
                .collect(Collectors.toMap(
                        FeedItemCheersCountProjection::getFeedItemId,
                        FeedItemCheersCountProjection::getCheersCount
                ));
    }

    private Set<UUID> getCheeredFeedItemIds(List<FeedItem> feedItems, UUID userId) {
        List<UUID> ids = feedItems.stream().map(FeedItem::getId).toList();
        if (ids.isEmpty()) return Set.of();

        return new HashSet<>(feedItemCheersRepository.findCheeredFeedItemIdsByUserId(ids, userId));
    }
}
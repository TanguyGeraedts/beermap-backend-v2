package be.tanguy.beermap.api.feed.web.dto;

import be.tanguy.beermap.api.feed.domain.FeedItemType;
import be.tanguy.beermap.api.feed.domain.FeedTargetType;
import be.tanguy.beermap.api.feed.domain.FeedVisibility;

import java.time.Instant;
import java.util.UUID;

public record FeedItemResponse(
        UUID id,
        UUID userId,
        FeedItemType type,
        FeedTargetType targetType,
        UUID targetId,
        FeedVisibility visibility,
        String metadataJson,
        Instant createdAt
) {}
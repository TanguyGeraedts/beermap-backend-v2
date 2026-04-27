package be.tanguy.beermap.api.feed.web.dto;

import be.tanguy.beermap.api.feed.domain.FeedItem;

public final class FeedItemMapper {

    private FeedItemMapper() {}

    public static FeedItemResponse toResponse(
            FeedItem feedItem,
            FeedItemDetailsResponse details
    ) {
        return new FeedItemResponse(
                feedItem.getId(),
                feedItem.getUserId(),
                feedItem.getType(),
                feedItem.getTargetType(),
                feedItem.getTargetId(),
                feedItem.getVisibility(),
                feedItem.getMetadataJson(),
                feedItem.getCreatedAt(),
                details
        );
    }
}

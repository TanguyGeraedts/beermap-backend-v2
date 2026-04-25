package be.tanguy.beermap.api.logs.event;

import be.tanguy.beermap.api.feed.domain.FeedVisibility;

import java.util.UUID;

public record DrinkLogCreatedEvent(
        UUID userId,
        UUID logId,
        FeedVisibility feedVisibility
) {

    public static DrinkLogCreatedEvent create(UUID userId, UUID logId) {
        return new DrinkLogCreatedEvent(
                userId,
                logId,
                FeedVisibility.PUBLIC
        );
    }
}
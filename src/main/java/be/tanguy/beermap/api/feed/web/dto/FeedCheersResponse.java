package be.tanguy.beermap.api.feed.web.dto;

import java.util.UUID;

public record FeedCheersResponse(
        UUID feedItemId,
        long cheersCount,
        boolean cheeredByMe
) {}

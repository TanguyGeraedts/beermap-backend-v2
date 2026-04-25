package be.tanguy.beermap.api.feed.web.dto;

import java.time.Instant;
import java.util.List;

public record FeedPageResponse(
        List<FeedItemResponse> items,
        Instant nextCursor,
        boolean hasMore
) {}
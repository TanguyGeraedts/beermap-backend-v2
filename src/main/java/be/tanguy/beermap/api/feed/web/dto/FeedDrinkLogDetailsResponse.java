package be.tanguy.beermap.api.feed.web.dto;

import java.time.Instant;
import java.util.UUID;

public record FeedDrinkLogDetailsResponse(
        UUID id,
        String displayName,
        String category,
        String subType,
        Integer quantity,
        Integer rating,
        String photoUrl,
        Instant consumedAt
) {
}

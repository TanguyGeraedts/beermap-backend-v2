package be.tanguy.beermap.api.feed.web.dto;

import java.util.UUID;

public record FeedActorResponse(
        UUID id,
        String username,
        String discriminator,
        String avatarUrl
) {
}

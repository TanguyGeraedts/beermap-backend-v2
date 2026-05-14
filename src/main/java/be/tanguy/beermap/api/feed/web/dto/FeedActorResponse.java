package be.tanguy.beermap.api.feed.web.dto;

import be.tanguy.beermap.api.users.domain.AccountType;

import java.util.UUID;

public record FeedActorResponse(
        UUID id,
        String username,
        String discriminator,
        String avatarUrl,
        AccountType accountType
) {
}

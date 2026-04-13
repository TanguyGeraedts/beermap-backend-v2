package be.tanguy.beermap.api.friends.web.dto;

import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;

import java.time.Instant;
import java.util.UUID;

public record FriendshipResponse(
        UUID id,
        UUID requesterId,
        UUID addresseeId,
        FriendshipStatus status,
        Instant respondedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static FriendshipResponse from(Friendship friendship) {
        return new FriendshipResponse(
                friendship.getId(),
                friendship.getRequesterId(),
                friendship.getAddresseeId(),
                friendship.getStatus(),
                friendship.getRespondedAt(),
                friendship.getCreatedAt(),
                friendship.getUpdatedAt()
        );
    }
}

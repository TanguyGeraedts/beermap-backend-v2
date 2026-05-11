package be.tanguy.beermap.api.friends.web.dto;

import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;
import be.tanguy.beermap.api.users.domain.UserProfile;

import java.time.Instant;
import java.util.UUID;

public record FriendshipResponse(
        UUID id,
        UUID requesterId,
        UUID addresseeId,
        FriendshipStatus status,
        Instant respondedAt,
        Instant createdAt,
        Instant updatedAt,
        UserSummary friend  // ← new
) {
    public record UserSummary(
            UUID id,
            String username,
            String discriminator,
            String avatarUrl
    ) {}

    // Keep the simple factory for cases where you don't have the other user
    public static FriendshipResponse from(Friendship friendship) {
        return from(friendship, null);
    }

    public static FriendshipResponse from(Friendship friendship, UserProfile friend) {
        UserSummary summary = friend == null ? null : new UserSummary(
                friend.getId(),
                friend.getInfo().getUsername(),
                friend.getInfo().getDiscriminator(),
                friend.getInfo().getAvatarUrl()
        );

        return new FriendshipResponse(
                friendship.getId(),
                friendship.getRequesterId(),
                friendship.getAddresseeId(),
                friendship.getStatus(),
                friendship.getRespondedAt(),
                friendship.getCreatedAt(),
                friendship.getUpdatedAt(),
                summary
        );
    }
}

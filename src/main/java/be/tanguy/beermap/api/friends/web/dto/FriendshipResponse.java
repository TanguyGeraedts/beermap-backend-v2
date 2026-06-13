package be.tanguy.beermap.api.friends.web.dto;

import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;
import be.tanguy.beermap.api.users.domain.AccountType;
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
        UserSummary friend
) {
    public record UserSummary(
            UUID id,
            String username,
            String discriminator,
            String avatarUrl,
            AccountType accountType
    ) {}

    public static FriendshipResponse from(Friendship friendship) {
        return from(friendship, null);
    }

    public static FriendshipResponse from(Friendship friendship, UserProfile friend) {
        UserSummary summary = friend == null ? null : new UserSummary(
                friend.getId(),
                friend.getInfo().getUsername(),
                friend.getInfo().getDiscriminator(),
                friend.getInfo().getAvatarUrl(),
                friend.getAccountType()
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

package be.tanguy.beermap.api.friends.event;

import java.util.UUID;

public record FriendshipAcceptedEvent(
        UUID requesterId,
        UUID addresseeId
) {}
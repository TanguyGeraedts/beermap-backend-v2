package be.tanguy.beermap.api.friends.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FriendshipTargetRequest(
        @NotNull UUID userId
) {}
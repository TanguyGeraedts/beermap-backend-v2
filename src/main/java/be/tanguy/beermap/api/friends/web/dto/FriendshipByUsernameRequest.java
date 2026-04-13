package be.tanguy.beermap.api.friends.web.dto;

import be.tanguy.beermap.api.common.validation.ValidDiscriminator;
import be.tanguy.beermap.api.common.validation.ValidUsername;

public record FriendshipByUsernameRequest(
        @ValidUsername String targetUsername,
        @ValidDiscriminator String targetDiscriminator
) {}
package be.tanguy.beermap.api.users.web.dto;

import be.tanguy.beermap.api.users.domain.AccountType;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String username,
        String discriminator,
        String avatarUrl,
        AccountType accountType
) {}
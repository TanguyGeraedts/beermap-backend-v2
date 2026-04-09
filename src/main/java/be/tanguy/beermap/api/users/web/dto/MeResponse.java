package be.tanguy.beermap.api.users.web.dto;

import be.tanguy.beermap.api.users.domain.UserProfile;

import java.time.Instant;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String username,
        String usernameNormalized,
        String discriminator,
        String bio,
        String avatarUrl,
        String accountType,
        Instant createdAt,
        Instant updatedAt
) {
    public static MeResponse from(UserProfile userProfile) {
        return new MeResponse(
                userProfile.getId(),
                userProfile.getInfo().getUsername(),
                userProfile.getInfo().getUsernameNormalized(),
                userProfile.getInfo().getDiscriminator(),
                userProfile.getInfo().getBio(),
                userProfile.getInfo().getAvatarUrl(),
                userProfile.getAccountType().name(),
                userProfile.getCreatedAt(),
                userProfile.getUpdatedAt()
        );
    }
}
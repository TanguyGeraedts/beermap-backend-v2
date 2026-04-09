package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.users.domain.UserProfile;

import java.util.UUID;

public interface UserProfileService {

    UserProfile getOrCreateUser(String issuer, String subject);

    UserProfile getCurrentUser(String issuer, String subject);

    UserProfile updateProfile(
            String issuer,
            String subject,
            String username,
            String bio,
            String avatarUrl,
            String discriminator
    );

    UserProfile upgradeToPremium(UUID userProfileId);
}
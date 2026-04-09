package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.users.domain.UserProfile;

import java.util.UUID;

public interface UserProfileService {

    UserProfile createUser(
            UserIdentity userIdentity,
            String username,
            String bio,
            String avatarUrl
    );

    UserProfile getCurrentUser(UserIdentity userIdentity);

    UserProfile updateProfile(
            UserIdentity userIdentity,
            String username,
            String bio,
            String avatarUrl,
            String discriminator
    );

    UserProfile upgradeToPremium(UUID userProfileId);
}
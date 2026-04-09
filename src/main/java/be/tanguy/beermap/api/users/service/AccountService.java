package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.users.domain.UserProfile;

import java.util.UUID;

public interface AccountService {

    UserProfile upgradeToPremium(UUID userProfileId);

    UserProfile downgradeToUser(UUID userProfileId);

    UserProfile upgradeToAdmin(UUID userProfileId);
}

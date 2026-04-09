package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.exception.UserProfileNotFoundException;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserProfile upgradeToPremium(UUID userProfileId) {
        UserProfile userProfile = findOrThrow(userProfileId);

        if (userProfile.getAccountType() == AccountType.NORMAL) {
            userProfile.upgradeToPremium();
        }

        return userProfile;
    }

    @Override
    @Transactional
    public UserProfile downgradeToUser(UUID userProfileId) {
        UserProfile userProfile = findOrThrow(userProfileId);
        userProfile.downgradeToUser();
        return userProfile;
    }

    @Override
    @Transactional
    public UserProfile upgradeToAdmin(UUID userProfileId) {
        UserProfile userProfile = findOrThrow(userProfileId);
        userProfile.upgradeToAdmin();
        return userProfile;
    }

    private UserProfile findOrThrow(UUID userProfileId) {
        return userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new UserProfileNotFoundException(userProfileId));
    }
}

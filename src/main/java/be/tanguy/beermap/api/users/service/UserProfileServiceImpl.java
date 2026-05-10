package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.common.config.storage.StorageProperties;
import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.media.service.ImageStorageService;
import be.tanguy.beermap.api.users.domain.*;
import be.tanguy.beermap.api.users.exception.*;
import be.tanguy.beermap.api.users.repository.AuthIdentityRepository;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static be.tanguy.beermap.api.users.domain.UserProfileInfo.DISCRIMINATOR_LENGTH;
import static be.tanguy.beermap.api.users.domain.UserProfileInfo.MAX_USERNAME_LENGTH;
import static be.tanguy.beermap.api.users.domain.UserProfileInfo.normalizeUsername;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final AuthIdentityRepository authIdentityRepository;
    private final UserProfileRepository userProfileRepository;
    private final ImageStorageService imageStorageService;
    private final StorageProperties storageProperties;

    @Override
    @Transactional
    public UserProfile createUser(
            UserIdentity userIdentity,
            String username,
            String bio,
            String avatarUrl
    ) {
        if (authIdentityRepository.findByIssuerAndSubject(
                userIdentity.issuer(),
                userIdentity.subject())
                .isPresent())
        {
            throw new ProfileAlreadyExistsException();
        }

        String trimmedUsername = username.trim();
        String normalizedUsername = normalizeUsername(trimmedUsername);
        String discriminator = generateDiscriminator(normalizedUsername);

        UserProfileInfo info = UserProfileInfo.create(trimmedUsername, discriminator);
        UserProfile userProfile = UserProfile.create(info);

        if (bio != null) {
            userProfile.updateBio(normalizeBio(bio));
        }

        if (avatarUrl != null) {
            userProfile.updateAvatarUrl(normalizeAvatarUrl(avatarUrl));
        }

        UserProfile savedUserProfile = userProfileRepository.save(userProfile);

        AuthIdentity authIdentity = AuthIdentity.create(userIdentity.issuer(), userIdentity.subject(), savedUserProfile);
        authIdentityRepository.save(authIdentity);

        return savedUserProfile;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getCurrentUser(UserIdentity userIdentity) {
        return authIdentityRepository.findByIssuerAndSubject(userIdentity.issuer(), userIdentity.subject())
                .map(AuthIdentity::getUserProfile)
                .orElseThrow(ProfileNotFoundException::new);
    }

    @Override
    @Transactional
    public UUID getCurrentUserId(UserIdentity userIdentity) {
        return this.getCurrentUser(userIdentity).getId();
    }

    @Override
    @Transactional
    public UserProfile updateProfile(
            UserIdentity userIdentity,
            String username,
            String bio,
            String avatarUrl,
            String discriminator
    ) {
        UserProfile userProfile = getCurrentUser(userIdentity);

        applyBioUpdate(userProfile, bio);
        applyAvatarUrlUpdate(userProfile, avatarUrl);
        applyUsernameUpdate(userProfile, username);
        applyDiscriminatorUpdate(userProfile, discriminator);

        return userProfile;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountType getAccountType(UUID userId) {
        return userProfileRepository.findById(userId)
                .map(UserProfile::getAccountType)
                .orElseThrow(ProfileNotFoundException::new);
    }

    private void applyBioUpdate(UserProfile userProfile, String bio) {
        if (bio == null) {
            return;
        }

        userProfile.updateBio(normalizeBio(bio));
    }

    private void applyAvatarUrlUpdate(UserProfile userProfile, String avatarUrl) {
        if (avatarUrl == null) {
            return;
        }

        String normalized = normalizeAvatarUrl(avatarUrl);

        if (normalized == null) {
            String currentAvatarUrl = userProfile.getInfo().getAvatarUrl();
            if (currentAvatarUrl != null) {
                String key = extractKeyFromUrl(currentAvatarUrl);
                imageStorageService.deleteImage(key);
                userProfile.updateAvatarUrl(null);
            }
            return;
        }

        userProfile.updateAvatarUrl(normalized);
    }


    private void applyUsernameUpdate(UserProfile userProfile, String username) {
        if (username == null || username.isBlank()) {
            return;
        }

        String trimmedUsername = username.trim();
        String normalizedUsername = normalizeUsername(trimmedUsername);
        String currentNormalizedUsername = userProfile.getInfo().getUsernameNormalized();
        String currentDiscriminator = userProfile.getInfo().getDiscriminator();

        if (normalizedUsername.equals(currentNormalizedUsername)) {
            return;
        }

        boolean taken = userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(
                normalizedUsername,
                currentDiscriminator
        );

        if (taken) {
            throw new UsernameDiscriminatorAlreadyTakenException(trimmedUsername, currentDiscriminator);
        }

        userProfile.changeUsername(trimmedUsername);
    }

    private void applyDiscriminatorUpdate(UserProfile userProfile, String discriminator) {
        if (discriminator == null || discriminator.isBlank()) {
            return;
        }

        if (!userProfile.hasPremiumAccess()) {
            return;
        }

        String normalizedDiscriminator = normalizeDiscriminator(discriminator);
        String currentDiscriminator = userProfile.getInfo().getDiscriminator();

        if (normalizedDiscriminator.equals(currentDiscriminator)) {
            return;
        }

        String usernameNormalized = userProfile.getInfo().getUsernameNormalized();

        boolean taken = userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(
                usernameNormalized,
                normalizedDiscriminator
        );

        if (taken) {
            throw new UsernameDiscriminatorAlreadyTakenException(
                    userProfile.getInfo().getUsername(),
                    normalizedDiscriminator
            );
        }

        userProfile.changeDiscriminator(normalizedDiscriminator);
    }

    private String generateDiscriminator(String usernameNormalized) {
        int maxAttempts = 10;

        for (int i = 0; i < maxAttempts; i++) {
            String discriminator = generateRandomDiscriminator();

            boolean exists = userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(
                    usernameNormalized,
                    discriminator
            );

            if (!exists) {
                return discriminator;
            }
        }

        throw new InvalidDiscriminatorException(
                "Failed to generate unique targetDiscriminator after " + maxAttempts + " attempts"
        );
    }

    private String generateRandomDiscriminator() {
        int max = (int) Math.pow(10, DISCRIMINATOR_LENGTH);
        int value = ThreadLocalRandom.current().nextInt(max);

        return String.format("%0" + DISCRIMINATOR_LENGTH + "d", value);
    }

    private String normalizeBio(String bio) {
        String trimmed = bio.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeAvatarUrl(String avatarUrl) {
        String trimmed = avatarUrl.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeDiscriminator(String discriminator) {
        String trimmed = discriminator.trim();

        if (!trimmed.matches("\\d{" + DISCRIMINATOR_LENGTH + "}")) {
            throw new InvalidDiscriminatorException(
                    "Discriminator must be exactly " + DISCRIMINATOR_LENGTH + " digits"
            );
        }

        return trimmed;
    }

    private String extractKeyFromUrl(String publicUrl) {
        String baseUrl = storageProperties.publicBaseUrl();
        return publicUrl.replace(baseUrl + "/", "");
    }
}

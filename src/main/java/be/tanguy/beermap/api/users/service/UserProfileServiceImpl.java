package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.AuthIdentity;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.domain.UserProfileInfo;
import be.tanguy.beermap.api.users.exception.CurrentUserNotFoundException;
import be.tanguy.beermap.api.users.exception.InvalidDiscriminatorException;
import be.tanguy.beermap.api.users.exception.UserProfileNotFoundException;
import be.tanguy.beermap.api.users.exception.UsernameDiscriminatorAlreadyTakenException;
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

    @Override
    @Transactional
    public UserProfile getOrCreateUser(String issuer, String subject) {
        return authIdentityRepository.findByIssuerAndSubject(issuer, subject)
                .map(AuthIdentity::getUserProfile)
                .orElseGet(() -> createNewUser(issuer, subject));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getCurrentUser(String issuer, String subject) {
        return authIdentityRepository.findByIssuerAndSubject(issuer, subject)
                .map(AuthIdentity::getUserProfile)
                .orElseThrow(CurrentUserNotFoundException::new);
    }

    @Override
    @Transactional
    public UserProfile updateProfile(
            String issuer,
            String subject,
            String username,
            String bio,
            String avatarUrl,
            String discriminator
    ) {
        UserProfile userProfile = getCurrentUser(issuer, subject);

        applyBioUpdate(userProfile, bio);
        applyAvatarUrlUpdate(userProfile, avatarUrl);
        applyUsernameUpdate(userProfile, username);
        applyDiscriminatorUpdate(userProfile, discriminator);

        return userProfile;
    }

    @Override
    @Transactional
    public UserProfile upgradeToPremium(UUID userProfileId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new UserProfileNotFoundException(userProfileId));

        if (userProfile.getAccountType() == AccountType.NORMAL) {
            userProfile.upgradeToPremium();
        }

        return userProfile;
    }

    private UserProfile createNewUser(String issuer, String subject) {
        String username = generateBaseUsername(subject);
        String discriminator = generateDiscriminator(normalizeUsername(username));

        UserProfileInfo info = UserProfileInfo.create(username, discriminator);
        UserProfile userProfile = userProfileRepository.save(UserProfile.create(info));

        authIdentityRepository.save(AuthIdentity.create(issuer, subject, userProfile));

        return userProfile;
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

        userProfile.updateAvatarUrl(normalizeAvatarUrl(avatarUrl));
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

    private String generateBaseUsername(String subject) {
        String cleaned = subject.replaceAll("[^a-zA-Z0-9._-]", "");

        if (cleaned.isBlank()) {
            cleaned = "user";
        }

        if (cleaned.length() > MAX_USERNAME_LENGTH) {
            cleaned = cleaned.substring(0, MAX_USERNAME_LENGTH);
        }

        return cleaned;
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
                "Failed to generate unique discriminator after " + maxAttempts + " attempts"
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
}
package be.tanguy.beermap.api.users.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UserProfileInfo {

    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 32;

    public static final int DISCRIMINATOR_LENGTH = 4;

    public static final int MAX_BIO_LENGTH = 500;
    public static final int MAX_AVATAR_URL_LENGTH = 2048;

    public static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]+$";
    public static final String DISCRIMINATOR_REGEX = "^\\d{4}$";

    @Column(name = "targetUsername", nullable = false, length = MAX_USERNAME_LENGTH)
    private String username;

    @Column(name = "username_normalized", nullable = false, length = MAX_USERNAME_LENGTH)
    private String usernameNormalized;

    @Column(name = "targetDiscriminator", nullable = false, length = DISCRIMINATOR_LENGTH)
    private String discriminator;

    @Column(name = "bio", length = MAX_BIO_LENGTH)
    private String bio;

    @Column(name = "avatar_url", length = MAX_AVATAR_URL_LENGTH)
    private String avatarUrl;

    private UserProfileInfo(
            String username,
            String usernameNormalized,
            String discriminator,
            String bio,
            String avatarUrl
    ) {
        this.username = username;
        this.usernameNormalized = usernameNormalized;
        this.discriminator = discriminator;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    public static UserProfileInfo create(String username, String discriminator) {
        return new UserProfileInfo(
                username,
                normalizeUsername(username),
                discriminator,
                null,
                null
        );
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void changeUsername(String username) {
        this.username = username;
        this.usernameNormalized = normalizeUsername(username);
    }

    public void changeDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public void changeIdentity(String username, String discriminator) {
        this.username = username;
        this.usernameNormalized = normalizeUsername(username);
        this.discriminator = discriminator;
    }

    public static String normalizeUsername(String username) {
        return username == null ? null : username.trim().toLowerCase(Locale.ROOT);
    }
}
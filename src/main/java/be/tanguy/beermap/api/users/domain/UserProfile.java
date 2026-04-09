package be.tanguy.beermap.api.users.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_profiles_username_normalized_discriminator",
                        columnNames = {"username_normalized", "discriminator"}
                )
        }
)
public class UserProfile extends AbstractBaseObject {

    @Embedded
    private UserProfileInfo info;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    private UserProfile(UserProfileInfo info, AccountType accountType) {
        this.info = info;
        this.accountType = accountType;
    }

    public static UserProfile create(UserProfileInfo info) {
        return new UserProfile(info, AccountType.NORMAL);
    }

    public void updateProfileInfo(String bio, String avatarUrl) {
        this.info.updateProfile(bio, avatarUrl);
    }

    public void changeUsername(String username) {
        this.info.changeUsername(username);
    }

    public void changeDiscriminator(String discriminator) {
        this.info.changeDiscriminator(discriminator);
    }

    public void changeIdentity(String username, String discriminator) {
        this.info.changeIdentity(username, discriminator);
    }

    public void upgradeToPremium() {
        this.accountType = AccountType.PREMIUM;
    }

    public void setAdmin() {
        this.accountType = AccountType.ADMIN;
    }

    public void downgradeToNormal() {
        this.accountType = AccountType.NORMAL;
    }

    public boolean hasPremiumAccess() {
        return this.accountType.hasPremiumAccess();
    }
}
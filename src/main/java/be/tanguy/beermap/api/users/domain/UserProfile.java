package be.tanguy.beermap.api.users.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_profiles")
public class UserProfile extends AbstractBaseObject {

    @Embedded
    private UserProfileInfo info;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    private UserProfile(UserProfileInfo info) {
        this.info = info;
        this.accountType = AccountType.NORMAL;
    }

    public static UserProfile create(UserProfileInfo info) {
        return new UserProfile(info);
    }

    /// Profile Updates (PATCH-safe)
    public void updateBio(String bio) {
        this.info.updateBio(bio);
    }

    public void updateAvatarUrl(String avatarUrl) {
        this.info.updateAvatarUrl(avatarUrl);
    }

    public void changeUsername(String username) {
        this.info.changeUsername(username);
    }

    public void changeDiscriminator(String discriminator) {
        this.info.changeDiscriminator(discriminator);
    }

    /// Account Logic
    public void upgradeToPremium() {
        this.accountType = AccountType.PREMIUM;
    }

    public void downgradeToUser(){
        this.accountType = AccountType.NORMAL;
    }

    public void upgradeToAdmin() {
        this.accountType = AccountType.ADMIN;
    }

    public boolean hasPremiumAccess() {
        return this.accountType.hasPremiumAccess();
    }

    public boolean isAdmin() {
        return this.accountType == AccountType.ADMIN;
    }
}
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
        name = "auth_identities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_auth_identities_issuer_subject",
                        columnNames = {"issuer", "subject"}
                )
        }
)
public class AuthIdentity extends AbstractBaseObject {

    @Column(name = "issuer", nullable = false, length = 255)
    private String issuer;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    private AuthIdentity(String issuer, String subject, UserProfile userProfile) {
        this.issuer = issuer;
        this.subject = subject;
        this.userProfile = userProfile;
    }

    public static AuthIdentity create(String issuer, String subject, UserProfile userProfile) {
        return new AuthIdentity(issuer, subject, userProfile);
    }
}
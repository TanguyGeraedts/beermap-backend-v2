package be.tanguy.beermap.api.friends.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import be.tanguy.beermap.api.friends.exception.CannotFriendYourselfException;
import be.tanguy.beermap.api.friends.exception.InvalidFriendshipStateException;
import be.tanguy.beermap.api.friends.exception.UserNotPartOfFriendshipException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "friendships",
        indexes = {
                @Index(name = "idx_friendship_requester", columnList = "requester_id"),
                @Index(name = "idx_friendship_addressee", columnList = "addressee_id"),
                @Index(name = "idx_friendship_status", columnList = "status"),
                @Index(name = "idx_friendship_requester_addressee", columnList = "requester_id, addressee_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_friendship_requester_addressee", columnNames = {"requester_id", "addressee_id"})
        }
)
@Getter
public class Friendship extends AbstractBaseObject {

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "addressee_id", nullable = false)
    private UUID addresseeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendshipStatus status;

    @Column(name = "responded_at")
    private Instant respondedAt;

    private Friendship(UUID requesterId, UUID addresseeId) {
        validateUsers(requesterId, addresseeId);

        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.status = FriendshipStatus.PENDING;
    }

    public static Friendship create(UUID requesterId, UUID addresseeId) {
        return new Friendship(requesterId, addresseeId);
    }

    /// STATE TRANSITIONS
    public void accept() {
        requirePending();
        this.status = FriendshipStatus.ACCEPTED;
        this.respondedAt = Instant.now();
    }

    public void reject() {
        requirePending();
        this.status = FriendshipStatus.REJECTED;
        this.respondedAt = Instant.now();
    }

    public void cancel() {
        if (this.status != FriendshipStatus.PENDING) {
            throw new InvalidFriendshipStateException("Only pending friendships can be cancelled");
        }
        this.status = FriendshipStatus.CANCELLED;
        this.respondedAt = Instant.now();
    }

    public void remove() {
        if (this.status != FriendshipStatus.ACCEPTED) {
            throw new InvalidFriendshipStateException("Only accepted friendships can be removed");
        }
        this.status = FriendshipStatus.REMOVED;
        this.respondedAt = Instant.now();
    }

    /**
     * Blocks this relationship. The blocker becomes the requester so that
     * unblock() can later enforce that only the original blocker can lift it.
     */
    public void blockAs(UUID blockerId, UUID blockedId) {
        validateUsers(blockerId, blockedId);
        this.requesterId = blockerId;
        this.addresseeId = blockedId;
        this.status = FriendshipStatus.BLOCKED;
        this.respondedAt = Instant.now();
    }

    /**
     * Lifts a block. Authorization (only the blocker may call this) is
     * enforced in the service layer by checking requesterId == currentUserId.
     * The friendship transitions to REMOVED rather than PENDING so neither
     * party is auto-connected after an unblock.
     */
    public void unblock() {
        if (this.status != FriendshipStatus.BLOCKED) {
            throw new InvalidFriendshipStateException("Only blocked friendships can be unblocked");
        }
        this.status = FriendshipStatus.REMOVED;
        this.respondedAt = Instant.now();
    }

    public void reopen(UUID requesterId, UUID addresseeId) {
        validateUsers(requesterId, addresseeId);
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.status = FriendshipStatus.PENDING;
        this.respondedAt = null;
    }

    /// HELPERS

    public boolean isPending() {
        return status == FriendshipStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean isBlocked() {
        return status == FriendshipStatus.BLOCKED;
    }

    /**
     * Returns true if the given user is one of the two parties in this friendship.
     * Previously this was inverted (used && instead of ||), which made it always
     * return the wrong result.
     */
    public boolean involves(UUID userId) {
        return requesterId.equals(userId) || addresseeId.equals(userId);
    }

    public UUID getOtherUserId(UUID userId) {
        if (requesterId.equals(userId)) return addresseeId;
        if (addresseeId.equals(userId)) return requesterId;
        throw new UserNotPartOfFriendshipException();
    }

    private void requirePending() {
        if (this.status != FriendshipStatus.PENDING) {
            throw new InvalidFriendshipStateException("Action only allowed for pending friendships");
        }
    }

    private static void validateUsers(UUID requesterId, UUID addresseeId) {
        if (requesterId == null || addresseeId == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }

        if (requesterId.equals(addresseeId)) {
            throw new CannotFriendYourselfException();
        }
    }
}
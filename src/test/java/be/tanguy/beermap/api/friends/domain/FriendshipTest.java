package be.tanguy.beermap.api.friends.domain;

import be.tanguy.beermap.api.friends.exception.CannotFriendYourselfException;
import be.tanguy.beermap.api.friends.exception.InvalidFriendshipStateException;
import be.tanguy.beermap.api.friends.exception.UserNotPartOfFriendshipException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FriendshipTest {

    private UUID userA;
    private UUID userB;
    private UUID userC;

    @BeforeEach
    void setUp() {
        userA = UUID.randomUUID();
        userB = UUID.randomUUID();
        userC = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // create()
    // -------------------------------------------------------------------------

    @Nested
    class Create {

        @Test
        void creates_pending_friendship() {
            Friendship f = Friendship.create(userA, userB);

            assertThat(f.getRequesterId()).isEqualTo(userA);
            assertThat(f.getAddresseeId()).isEqualTo(userB);
            assertThat(f.isPending()).isTrue();
            assertThat(f.getRespondedAt()).isNull();
        }

        @Test
        void throws_when_same_user() {
            assertThatThrownBy(() -> Friendship.create(userA, userA))
                    .isInstanceOf(CannotFriendYourselfException.class);
        }

        @Test
        void throws_when_requester_null() {
            assertThatThrownBy(() -> Friendship.create(null, userB))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void throws_when_addressee_null() {
            assertThatThrownBy(() -> Friendship.create(userA, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------------------------
    // accept()
    // -------------------------------------------------------------------------

    @Nested
    class Accept {

        @Test
        void transitions_pending_to_accepted() {
            Friendship f = Friendship.create(userA, userB);
            f.accept();

            assertThat(f.isAccepted()).isTrue();
            assertThat(f.getRespondedAt()).isNotNull();
        }

        @Test
        void throws_when_already_accepted() {
            Friendship f = Friendship.create(userA, userB);
            f.accept();

            assertThatThrownBy(f::accept)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }

        @Test
        void throws_when_rejected() {
            Friendship f = Friendship.create(userA, userB);
            f.reject();

            assertThatThrownBy(f::accept)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // reject()
    // -------------------------------------------------------------------------

    @Nested
    class Reject {

        @Test
        void transitions_pending_to_rejected() {
            Friendship f = Friendship.create(userA, userB);
            f.reject();

            assertThat(f.getStatus()).isEqualTo(FriendshipStatus.REJECTED);
            assertThat(f.getRespondedAt()).isNotNull();
        }

        @Test
        void throws_when_not_pending() {
            Friendship f = Friendship.create(userA, userB);
            f.accept();

            assertThatThrownBy(f::reject)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // cancel()
    // -------------------------------------------------------------------------

    @Nested
    class Cancel {

        @Test
        void transitions_pending_to_cancelled() {
            Friendship f = Friendship.create(userA, userB);
            f.cancel();

            assertThat(f.getStatus()).isEqualTo(FriendshipStatus.CANCELLED);
            assertThat(f.getRespondedAt()).isNotNull();
        }

        @Test
        void throws_when_already_accepted() {
            Friendship f = Friendship.create(userA, userB);
            f.accept();

            assertThatThrownBy(f::cancel)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // remove()
    // -------------------------------------------------------------------------

    @Nested
    class Remove {

        @Test
        void transitions_accepted_to_removed() {
            Friendship f = Friendship.create(userA, userB);
            f.accept();
            f.remove();

            assertThat(f.getStatus()).isEqualTo(FriendshipStatus.REMOVED);
            assertThat(f.getRespondedAt()).isNotNull();
        }

        @Test
        void throws_when_pending() {
            Friendship f = Friendship.create(userA, userB);

            assertThatThrownBy(f::remove)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }

        @Test
        void throws_when_rejected() {
            Friendship f = Friendship.create(userA, userB);
            f.reject();

            assertThatThrownBy(f::remove)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // blockAs()
    // -------------------------------------------------------------------------

    @Nested
    class BlockAs {

        @Test
        void blocks_pending_friendship_and_sets_blocker_as_requester() {
            Friendship f = Friendship.create(userA, userB);
            f.blockAs(userB, userA); // userB is blocking userA

            assertThat(f.isBlocked()).isTrue();
            assertThat(f.getRequesterId()).isEqualTo(userB);
            assertThat(f.getAddresseeId()).isEqualTo(userA);
            assertThat(f.getRespondedAt()).isNotNull();
        }

        @Test
        void blocks_accepted_friendship() {
            Friendship f = Friendship.create(userA, userB);
            f.accept();
            f.blockAs(userA, userB);

            assertThat(f.isBlocked()).isTrue();
        }

        @Test
        void throws_when_same_user() {
            Friendship f = Friendship.create(userA, userB);

            assertThatThrownBy(() -> f.blockAs(userA, userA))
                    .isInstanceOf(CannotFriendYourselfException.class);
        }
    }

    // -------------------------------------------------------------------------
    // unblock()
    // -------------------------------------------------------------------------

    @Nested
    class Unblock {

        @Test
        void transitions_blocked_to_removed() {
            Friendship f = Friendship.create(userA, userB);
            f.blockAs(userA, userB);
            f.unblock();

            assertThat(f.getStatus()).isEqualTo(FriendshipStatus.REMOVED);
            assertThat(f.getRespondedAt()).isNotNull();
        }

        @Test
        void throws_when_not_blocked() {
            Friendship f = Friendship.create(userA, userB);
            f.accept();

            assertThatThrownBy(f::unblock)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }

        @Test
        void throws_when_pending() {
            Friendship f = Friendship.create(userA, userB);

            assertThatThrownBy(f::unblock)
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // reopen()
    // -------------------------------------------------------------------------

    @Nested
    class Reopen {

        @Test
        void reopens_cancelled_friendship_with_new_requester() {
            Friendship f = Friendship.create(userA, userB);
            f.cancel();
            f.reopen(userB, userA); // B is now sending the request

            assertThat(f.isPending()).isTrue();
            assertThat(f.getRequesterId()).isEqualTo(userB);
            assertThat(f.getAddresseeId()).isEqualTo(userA);
            assertThat(f.getRespondedAt()).isNull();
        }

        @Test
        void reopens_rejected_friendship() {
            Friendship f = Friendship.create(userA, userB);
            f.reject();
            f.reopen(userA, userB);

            assertThat(f.isPending()).isTrue();
        }

        @Test
        void throws_when_same_user() {
            Friendship f = Friendship.create(userA, userB);
            f.cancel();

            assertThatThrownBy(() -> f.reopen(userA, userA))
                    .isInstanceOf(CannotFriendYourselfException.class);
        }
    }

    // -------------------------------------------------------------------------
    // involves()
    // -------------------------------------------------------------------------

    @Nested
    class Involves {

        @Test
        void returns_true_for_requester() {
            Friendship f = Friendship.create(userA, userB);
            assertThat(f.involves(userA)).isTrue();
        }

        @Test
        void returns_true_for_addressee() {
            Friendship f = Friendship.create(userA, userB);
            assertThat(f.involves(userB)).isTrue();
        }

        @Test
        void returns_false_for_unrelated_user() {
            Friendship f = Friendship.create(userA, userB);
            assertThat(f.involves(userC)).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // getOtherUserId()
    // -------------------------------------------------------------------------

    @Nested
    class GetOtherUserId {

        @Test
        void returns_addressee_when_called_by_requester() {
            Friendship f = Friendship.create(userA, userB);
            assertThat(f.getOtherUserId(userA)).isEqualTo(userB);
        }

        @Test
        void returns_requester_when_called_by_addressee() {
            Friendship f = Friendship.create(userA, userB);
            assertThat(f.getOtherUserId(userB)).isEqualTo(userA);
        }

        @Test
        void throws_when_unrelated_user() {
            Friendship f = Friendship.create(userA, userB);

            assertThatThrownBy(() -> f.getOtherUserId(userC))
                    .isInstanceOf(UserNotPartOfFriendshipException.class);
        }
    }
}
package be.tanguy.beermap.api.friends.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;
import be.tanguy.beermap.api.friends.exception.*;
import be.tanguy.beermap.api.friends.repository.FriendshipRepository;
import be.tanguy.beermap.api.users.domain.AuthIdentity;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.exception.ProfileNotFoundException;
import be.tanguy.beermap.api.users.repository.AuthIdentityRepository;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceImplTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FriendshipServiceImpl service;

    private UUID currentUserId;
    private UUID otherUserId;
    private UserIdentity userIdentity;

    private static final String OTHER_USERNAME = "otheruser";
    private static final String OTHER_DISCRIMINATOR = "1234";

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        userIdentity = new UserIdentity("issuer", "subject");

        UserProfile currentProfile = mock(UserProfile.class);
        when(currentProfile.getId()).thenReturn(currentUserId);

        AuthIdentity authIdentity = mock(AuthIdentity.class);
        when(authIdentity.getUserProfile()).thenReturn(currentProfile);

        when(authIdentityRepository.findByIssuerAndSubject("issuer", "subject"))
                .thenReturn(Optional.of(authIdentity));
    }

    // Convenience: stub resolveUserId for send/resend tests
    private void stubResolveOtherUser() {
        UserProfile otherProfile = mock(UserProfile.class);
        when(otherProfile.getId()).thenReturn(otherUserId);
        when(userProfileRepository.findByInfo_UsernameNormalizedAndInfo_Discriminator(OTHER_USERNAME, OTHER_DISCRIMINATOR))
                .thenReturn(Optional.of(otherProfile));
    }

    // -------------------------------------------------------------------------
    // sendFriendRequest
    // -------------------------------------------------------------------------

    @Nested
    class SendFriendRequest {

        @Test
        void creates_new_friendship_when_none_exists() {
            stubResolveOtherUser();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.empty());

            Friendship saved = Friendship.create(currentUserId, otherUserId);
            when(friendshipRepository.save(any())).thenReturn(saved);

            Friendship result = service.sendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR);

            assertThat(result.isPending()).isTrue();
            verify(friendshipRepository).save(any(Friendship.class));
        }

        @Test
        void throws_when_existing_friendship_is_pending() {
            stubResolveOtherUser();
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> service.sendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(FriendshipAlreadyExistsException.class);
        }

        @Test
        void throws_when_existing_friendship_is_accepted() {
            stubResolveOtherUser();
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.accept();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> service.sendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(FriendshipAlreadyExistsException.class);
        }

        @Test
        void throws_when_existing_friendship_is_blocked() {
            stubResolveOtherUser();
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.blockAs(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> service.sendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(FriendshipBlockedException.class);
        }

        @Test
        void throws_when_sending_to_self() {
            // Resolve returns the current user's own profile
            UserProfile selfProfile = mock(UserProfile.class);
            when(selfProfile.getId()).thenReturn(currentUserId);
            when(userProfileRepository.findByInfo_UsernameNormalizedAndInfo_Discriminator(OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .thenReturn(Optional.of(selfProfile));

            assertThatThrownBy(() -> service.sendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(CannotFriendYourselfException.class);
        }

        @Test
        void throws_when_addressee_not_found() {
            when(userProfileRepository.findByInfo_UsernameNormalizedAndInfo_Discriminator(OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.sendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(ProfileNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // resendFriendRequest
    // -------------------------------------------------------------------------

    @Nested
    class ResendFriendRequest {

        @Test
        void reopens_cancelled_friendship() {
            stubResolveOtherUser();
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.cancel();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.resendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR);

            assertThat(result.isPending()).isTrue();
        }

        @Test
        void reopens_rejected_friendship() {
            stubResolveOtherUser();
            Friendship existing = Friendship.create(otherUserId, currentUserId);
            existing.reject();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.resendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR);

            assertThat(result.isPending()).isTrue();
            assertThat(result.getRequesterId()).isEqualTo(currentUserId);
        }

        @Test
        void throws_when_friendship_is_blocked() {
            stubResolveOtherUser();
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.blockAs(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> service.resendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(FriendshipBlockedException.class);
        }

        @Test
        void throws_when_friendship_already_pending() {
            stubResolveOtherUser();
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> service.resendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(FriendshipAlreadyExistsException.class);
        }

        @Test
        void throws_when_no_previous_friendship_exists() {
            stubResolveOtherUser();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.resendFriendRequest(userIdentity, OTHER_USERNAME, OTHER_DISCRIMINATOR))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // acceptFriendRequest
    // -------------------------------------------------------------------------

    @Nested
    class AcceptFriendRequest {

        @Test
        void accepts_incoming_request() {
            Friendship existing = Friendship.create(otherUserId, currentUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeId(otherUserId, currentUserId))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.acceptFriendRequest(userIdentity, otherUserId);

            assertThat(result.isAccepted()).isTrue();
        }

        @Test
        void throws_when_request_not_found() {
            when(friendshipRepository.findByRequesterIdAndAddresseeId(otherUserId, currentUserId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.acceptFriendRequest(userIdentity, otherUserId))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // rejectFriendRequest
    // -------------------------------------------------------------------------

    @Nested
    class RejectFriendRequest {

        @Test
        void rejects_incoming_request() {
            Friendship existing = Friendship.create(otherUserId, currentUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeId(otherUserId, currentUserId))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.rejectFriendRequest(userIdentity, otherUserId);

            assertThat(result.getStatus()).isEqualTo(FriendshipStatus.REJECTED);
        }

        @Test
        void throws_when_request_not_found() {
            when(friendshipRepository.findByRequesterIdAndAddresseeId(otherUserId, currentUserId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.rejectFriendRequest(userIdentity, otherUserId))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // cancelFriendRequest
    // -------------------------------------------------------------------------

    @Nested
    class CancelFriendRequest {

        @Test
        void cancels_outgoing_request() {
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeId(currentUserId, otherUserId))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.cancelFriendRequest(userIdentity, otherUserId);

            assertThat(result.getStatus()).isEqualTo(FriendshipStatus.CANCELLED);
        }

        @Test
        void throws_when_request_not_found() {
            when(friendshipRepository.findByRequesterIdAndAddresseeId(currentUserId, otherUserId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.cancelFriendRequest(userIdentity, otherUserId))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // removeFriend
    // -------------------------------------------------------------------------

    @Nested
    class RemoveFriend {

        @Test
        void removes_accepted_friendship() {
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.accept();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.removeFriend(userIdentity, otherUserId);

            assertThat(result.getStatus()).isEqualTo(FriendshipStatus.REMOVED);
        }

        @Test
        void throws_when_friendship_not_found() {
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.removeFriend(userIdentity, otherUserId))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // blockUser
    // -------------------------------------------------------------------------

    @Nested
    class BlockUser {

        @Test
        void blocks_existing_friendship() {
            when(userProfileRepository.existsById(otherUserId)).thenReturn(true);
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.accept();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.blockUser(userIdentity, otherUserId);

            assertThat(result.isBlocked()).isTrue();
            assertThat(result.getRequesterId()).isEqualTo(currentUserId);
        }

        @Test
        void creates_new_friendship_and_blocks_when_none_exists() {
            when(userProfileRepository.existsById(otherUserId)).thenReturn(true);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.empty());

            Friendship saved = Friendship.create(currentUserId, otherUserId);
            when(friendshipRepository.save(any())).thenReturn(saved);

            Friendship result = service.blockUser(userIdentity, otherUserId);

            assertThat(result.isBlocked()).isTrue();
        }

        @Test
        void is_idempotent_when_already_blocked() {
            when(userProfileRepository.existsById(otherUserId)).thenReturn(true);
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.blockAs(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.blockUser(userIdentity, otherUserId);

            assertThat(result.isBlocked()).isTrue();
            verify(friendshipRepository, never()).save(any());
        }

        @Test
        void throws_when_blocking_self() {
            assertThatThrownBy(() -> service.blockUser(userIdentity, currentUserId))
                    .isInstanceOf(CannotFriendYourselfException.class);
        }
    }

    // -------------------------------------------------------------------------
    // unblockUser
    // -------------------------------------------------------------------------

    @Nested
    class UnblockUser {

        @Test
        void unblocks_user_when_current_user_is_blocker() {
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.blockAs(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            Friendship result = service.unblockUser(userIdentity, otherUserId);

            assertThat(result.getStatus()).isEqualTo(FriendshipStatus.REMOVED);
        }

        @Test
        void throws_when_current_user_is_not_the_blocker() {
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.blockAs(otherUserId, currentUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> service.unblockUser(userIdentity, otherUserId))
                    .isInstanceOf(UserNotPartOfFriendshipException.class);
        }

        @Test
        void throws_when_friendship_is_not_blocked() {
            Friendship existing = Friendship.create(currentUserId, otherUserId);
            existing.accept();
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> service.unblockUser(userIdentity, otherUserId))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }

        @Test
        void throws_when_friendship_not_found() {
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.unblockUser(userIdentity, otherUserId))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    @Nested
    class Queries {

        @Test
        void getPendingIncomingRequests_returns_correct_list() {
            List<Friendship> expected = List.of(Friendship.create(otherUserId, currentUserId));
            when(friendshipRepository.findAllByAddresseeIdAndStatus(currentUserId, FriendshipStatus.PENDING))
                    .thenReturn(expected);

            assertThat(service.getPendingIncomingRequests(userIdentity)).isEqualTo(expected);
        }

        @Test
        void getPendingOutgoingRequests_returns_correct_list() {
            List<Friendship> expected = List.of(Friendship.create(currentUserId, otherUserId));
            when(friendshipRepository.findAllByRequesterIdAndStatus(currentUserId, FriendshipStatus.PENDING))
                    .thenReturn(expected);

            assertThat(service.getPendingOutgoingRequests(userIdentity)).isEqualTo(expected);
        }

        @Test
        void getAcceptedFriendships_uses_single_query() {
            Friendship f = Friendship.create(currentUserId, otherUserId);
            f.accept();
            when(friendshipRepository.findAllByUserIdAndStatus(currentUserId, FriendshipStatus.ACCEPTED))
                    .thenReturn(List.of(f));

            List<Friendship> result = service.getAcceptedFriendships(userIdentity);

            assertThat(result).hasSize(1);
            verify(friendshipRepository).findAllByUserIdAndStatus(currentUserId, FriendshipStatus.ACCEPTED);
            verify(friendshipRepository, never()).findAllByRequesterIdAndStatus(any(), any());
            verify(friendshipRepository, never()).findAllByAddresseeIdAndStatus(any(), any());
        }

        @Test
        void getBlockedUsers_returns_correct_list() {
            Friendship f = Friendship.create(currentUserId, otherUserId);
            f.blockAs(currentUserId, otherUserId);
            when(friendshipRepository.findAllByRequesterIdAndStatus(currentUserId, FriendshipStatus.BLOCKED))
                    .thenReturn(List.of(f));

            assertThat(service.getBlockedUsers(userIdentity)).hasSize(1);
        }

        @Test
        void getFriendship_returns_friendship_when_found() {
            Friendship f = Friendship.create(currentUserId, otherUserId);
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.of(f));

            Friendship result = service.getFriendship(userIdentity, otherUserId);

            assertThat(result).isEqualTo(f);
        }

        @Test
        void getFriendship_throws_when_not_found() {
            when(friendshipRepository.findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(any(), any(), any(), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getFriendship(userIdentity, otherUserId))
                    .isInstanceOf(InvalidFriendshipStateException.class);
        }
    }
}
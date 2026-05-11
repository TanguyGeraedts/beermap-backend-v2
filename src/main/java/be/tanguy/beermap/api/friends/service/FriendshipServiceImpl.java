package be.tanguy.beermap.api.friends.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;
import be.tanguy.beermap.api.friends.event.FriendshipAcceptedEvent;
import be.tanguy.beermap.api.friends.exception.*;
import be.tanguy.beermap.api.friends.repository.FriendshipRepository;
import be.tanguy.beermap.api.users.domain.AuthIdentity;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.domain.UserProfileInfo;
import be.tanguy.beermap.api.users.exception.ProfileNotFoundException;
import be.tanguy.beermap.api.users.repository.AuthIdentityRepository;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final UserProfileRepository userProfileRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Friendship sendFriendRequest(UserIdentity userIdentity, String username, String discriminator) {
        UUID requesterId = getCurrentUserId(userIdentity);
        UUID addresseeId = resolveUserId(username, discriminator);

        validateDifferentUsers(requesterId, addresseeId);

        Friendship existing = friendshipRepository
                .findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(
                        requesterId, addresseeId,
                        addresseeId, requesterId
                )
                .orElse(null);

        if (existing != null) {
            if (existing.isBlocked()) {
                throw new FriendshipBlockedException();
            }

            if (existing.isPending() || existing.isAccepted()) {
                throw new FriendshipAlreadyExistsException();
            }

            existing.reopen(requesterId, addresseeId);
            return friendshipRepository.save(existing);
        }

        Friendship friendship = Friendship.create(requesterId, addresseeId);
        return friendshipRepository.save(friendship);
    }

    @Override
    @Transactional
    public Friendship resendFriendRequest(UserIdentity userIdentity, String username, String discriminator) {
        UUID requesterId = getCurrentUserId(userIdentity);
        UUID addresseeId = resolveUserId(username, discriminator);

        validateDifferentUsers(requesterId, addresseeId);

        Friendship existing = friendshipRepository
                .findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(
                        requesterId, addresseeId,
                        addresseeId, requesterId
                )
                .orElseThrow(() -> new InvalidFriendshipStateException("No previous friendship found to resend"));

        if (existing.isBlocked()) {
            throw new FriendshipBlockedException();
        }

        if (existing.isAccepted() || existing.isPending()) {
            throw new FriendshipAlreadyExistsException();
        }

        existing.reopen(requesterId, addresseeId);
        return existing;
    }

    @Override
    @Transactional
    public Friendship acceptFriendRequest(UserIdentity userIdentity, UUID requesterId) {
        UUID addresseeId = getCurrentUserId(userIdentity);

        Friendship friendship = friendshipRepository
                .findByRequesterIdAndAddresseeId(requesterId, addresseeId)
                .orElseThrow(() -> new InvalidFriendshipStateException("Friend request not found"));

        if (!friendship.getAddresseeId().equals(addresseeId)) {
            throw new UserNotPartOfFriendshipException();
        }

        friendship.accept();

        eventPublisher.publishEvent(
                new FriendshipAcceptedEvent(friendship.getRequesterId(), friendship.getAddresseeId())
        );

        return friendship;
    }

    @Override
    @Transactional
    public Friendship rejectFriendRequest(UserIdentity userIdentity, UUID requesterId) {
        UUID addresseeId = getCurrentUserId(userIdentity);

        Friendship friendship = friendshipRepository
                .findByRequesterIdAndAddresseeId(requesterId, addresseeId)
                .orElseThrow(() -> new InvalidFriendshipStateException("Friend request not found"));

        if (!friendship.getAddresseeId().equals(addresseeId)) {
            throw new UserNotPartOfFriendshipException();
        }

        friendship.reject();
        return friendship;
    }

    @Override
    @Transactional
    public Friendship cancelFriendRequest(UserIdentity userIdentity, UUID addresseeId) {
        UUID requesterId = getCurrentUserId(userIdentity);

        Friendship friendship = friendshipRepository
                .findByRequesterIdAndAddresseeId(requesterId, addresseeId)
                .orElseThrow(() -> new InvalidFriendshipStateException("Friend request not found"));

        if (!friendship.getRequesterId().equals(requesterId)) {
            throw new UserNotPartOfFriendshipException();
        }

        friendship.cancel();
        return friendship;
    }

    @Override
    @Transactional
    public Friendship removeFriend(UserIdentity userIdentity, UUID otherUserId) {
        UUID currentUserId = getCurrentUserId(userIdentity);

        Friendship friendship = friendshipRepository
                .findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(
                        currentUserId, otherUserId,
                        otherUserId, currentUserId
                )
                .orElseThrow(() -> new InvalidFriendshipStateException("Friendship not found"));

        friendship.remove();
        return friendship;
    }

    @Override
    @Transactional
    public Friendship blockUser(UserIdentity userIdentity, UUID otherUserId) {
        UUID currentUserId = getCurrentUserId(userIdentity);

        validateDifferentUsers(currentUserId, otherUserId);
        ensureUserExists(otherUserId);

        Friendship friendship = friendshipRepository
                .findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(
                        currentUserId, otherUserId,
                        otherUserId, currentUserId
                )
                .orElse(null);

        if (friendship == null) {
            friendship = Friendship.create(currentUserId, otherUserId);
            friendshipRepository.save(friendship);
        } else if (friendship.isBlocked()) {
            return friendship;
        }

        friendship.blockAs(currentUserId, otherUserId);
        return friendship;
    }

    @Override
    @Transactional
    public Friendship unblockUser(UserIdentity userIdentity, UUID otherUserId) {
        UUID currentUserId = getCurrentUserId(userIdentity);

        Friendship friendship = friendshipRepository
                .findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(
                        currentUserId, otherUserId,
                        otherUserId, currentUserId
                )
                .orElseThrow(() -> new InvalidFriendshipStateException("No block relationship found"));

        if (!friendship.isBlocked()) {
            throw new InvalidFriendshipStateException("This user is not blocked");
        }

        if (!friendship.getRequesterId().equals(currentUserId)) {
            throw new UserNotPartOfFriendshipException();
        }

        friendship.unblock();
        return friendship;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getPendingIncomingRequests(UserIdentity userIdentity) {
        UUID currentUserId = getCurrentUserId(userIdentity);
        return friendshipRepository.findAllByAddresseeIdAndStatus(currentUserId, FriendshipStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getPendingOutgoingRequests(UserIdentity userIdentity) {
        UUID currentUserId = getCurrentUserId(userIdentity);
        return friendshipRepository.findAllByRequesterIdAndStatus(currentUserId, FriendshipStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getAcceptedFriendships(UserIdentity userIdentity) {
        UUID currentUserId = getCurrentUserId(userIdentity);
        return friendshipRepository.findAllByUserIdAndStatus(currentUserId, FriendshipStatus.ACCEPTED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getBlockedUsers(UserIdentity userIdentity) {
        UUID currentUserId = getCurrentUserId(userIdentity);
        return friendshipRepository.findAllByRequesterIdAndStatus(currentUserId, FriendshipStatus.BLOCKED);
    }

    @Override
    @Transactional(readOnly = true)
    public Friendship getFriendship(UserIdentity userIdentity, UUID otherUserId) {
        UUID currentUserId = getCurrentUserId(userIdentity);

        return friendshipRepository
                .findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(
                        currentUserId, otherUserId,
                        otherUserId, currentUserId
                )
                .orElseThrow(() -> new InvalidFriendshipStateException("Friendship not found"));
    }

    // --- Helpers ---

    private UUID resolveUserId(String username, String discriminator) {
        String normalizedUsername = UserProfileInfo.normalizeUsername(username);

        return userProfileRepository
                .findByInfo_UsernameNormalizedAndInfo_Discriminator(normalizedUsername, discriminator)
                .map(UserProfile::getId)
                .orElseThrow(ProfileNotFoundException::new);
    }

    private UUID getCurrentUserId(UserIdentity userIdentity) {
        UserProfile userProfile = authIdentityRepository
                .findByIssuerAndSubject(userIdentity.issuer(), userIdentity.subject())
                .map(AuthIdentity::getUserProfile)
                .orElseThrow(ProfileNotFoundException::new);

        return userProfile.getId();
    }

    private void ensureUserExists(UUID userId) {
        if (userId == null || !userProfileRepository.existsById(userId)) {
            throw new ProfileNotFoundException();
        }
    }

    private void validateDifferentUsers(UUID requesterId, UUID addresseeId) {
        if (requesterId == null || addresseeId == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }

        if (requesterId.equals(addresseeId)) {
            throw new CannotFriendYourselfException();
        }
    }
}
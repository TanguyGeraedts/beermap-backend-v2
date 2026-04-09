package be.tanguy.beermap.api.friends.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.friends.domain.Friendship;

import java.util.List;
import java.util.UUID;

public interface FriendshipService {

    Friendship sendFriendRequest(UserIdentity userIdentity, UUID addresseeId);

    Friendship resendFriendRequest(UserIdentity userIdentity, UUID addresseeId);

    Friendship acceptFriendRequest(UserIdentity userIdentity, UUID requesterId);

    Friendship rejectFriendRequest(UserIdentity userIdentity, UUID requesterId);

    Friendship cancelFriendRequest(UserIdentity userIdentity, UUID addresseeId);

    Friendship removeFriend(UserIdentity userIdentity, UUID otherUserId);

    Friendship blockUser(UserIdentity userIdentity, UUID otherUserId);

    Friendship unblockUser(UserIdentity userIdentity, UUID otherUserId);

    List<Friendship> getPendingIncomingRequests(UserIdentity userIdentity);

    List<Friendship> getPendingOutgoingRequests(UserIdentity userIdentity);

    List<Friendship> getAcceptedFriendships(UserIdentity userIdentity);

    List<Friendship> getBlockedUsers(UserIdentity userIdentity);

    Friendship getFriendship(UserIdentity userIdentity, UUID otherUserId);
}
package be.tanguy.beermap.api.friends.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.web.dto.FriendshipResponse;

import java.util.List;
import java.util.UUID;

public interface FriendshipService {

    Friendship sendFriendRequest(UserIdentity userIdentity, String username, String discriminator);

    Friendship resendFriendRequest(UserIdentity userIdentity, String username, String discriminator);

    Friendship acceptFriendRequest(UserIdentity userIdentity, UUID requesterId);

    Friendship rejectFriendRequest(UserIdentity userIdentity, UUID requesterId);

    Friendship cancelFriendRequest(UserIdentity userIdentity, UUID addresseeId);

    Friendship removeFriend(UserIdentity userIdentity, UUID otherUserId);

    Friendship blockUser(UserIdentity userIdentity, UUID otherUserId);

    Friendship unblockUser(UserIdentity userIdentity, UUID otherUserId);

    List<FriendshipResponse> getPendingIncomingRequests(UserIdentity userIdentity);

    List<FriendshipResponse> getPendingOutgoingRequests(UserIdentity userIdentity);

    List<FriendshipResponse> getAcceptedFriendships(UserIdentity userIdentity);

    List<FriendshipResponse> getBlockedUsers(UserIdentity userIdentity);

    FriendshipResponse getFriendship(UserIdentity userIdentity, UUID otherUserId);
}
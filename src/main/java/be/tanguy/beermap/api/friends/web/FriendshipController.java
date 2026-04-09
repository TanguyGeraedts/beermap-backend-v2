package be.tanguy.beermap.api.friends.controller;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    /// Queries
    @GetMapping
    public ResponseEntity<List<Friendship>> getAcceptedFriendships(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return ResponseEntity.ok(friendshipService.getAcceptedFriendships(userIdentity));
    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<List<Friendship>> getIncomingRequests(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return ResponseEntity.ok(friendshipService.getPendingIncomingRequests(userIdentity));
    }

    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<Friendship>> getOutgoingRequests(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return ResponseEntity.ok(friendshipService.getPendingOutgoingRequests(userIdentity));
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<Friendship>> getBlockedUsers(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return ResponseEntity.ok(friendshipService.getBlockedUsers(userIdentity));
    }

    @GetMapping("/{otherUserId}")
    public ResponseEntity<Friendship> getFriendship(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID otherUserId
    ) {
        return ResponseEntity.ok(friendshipService.getFriendship(userIdentity, otherUserId));
    }

    /// Friend requests
    @PostMapping("/requests/{addresseeId}")
    public ResponseEntity<Friendship> sendFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID addresseeId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(friendshipService.sendFriendRequest(userIdentity, addresseeId));
    }

    @PostMapping("/requests/{addresseeId}/resend")
    public ResponseEntity<Friendship> resendFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID addresseeId
    ) {
        return ResponseEntity.ok(friendshipService.resendFriendRequest(userIdentity, addresseeId));
    }

    @PostMapping("/requests/{requesterId}/accept")
    public ResponseEntity<Friendship> acceptFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID requesterId
    ) {
        return ResponseEntity.ok(friendshipService.acceptFriendRequest(userIdentity, requesterId));
    }

    @PostMapping("/requests/{requesterId}/reject")
    public ResponseEntity<Friendship> rejectFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID requesterId
    ) {
        return ResponseEntity.ok(friendshipService.rejectFriendRequest(userIdentity, requesterId));
    }

    @DeleteMapping("/requests/{addresseeId}")
    public ResponseEntity<Friendship> cancelFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID addresseeId
    ) {
        return ResponseEntity.ok(friendshipService.cancelFriendRequest(userIdentity, addresseeId));
    }

    /// Friendship management
    @DeleteMapping("/{otherUserId}")
    public ResponseEntity<Friendship> removeFriend(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID otherUserId
    ) {
        return ResponseEntity.ok(friendshipService.removeFriend(userIdentity, otherUserId));
    }

    @PostMapping("/{otherUserId}/block")
    public ResponseEntity<Friendship> blockUser(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID otherUserId
    ) {
        return ResponseEntity.ok(friendshipService.blockUser(userIdentity, otherUserId));
    }

    @DeleteMapping("/{otherUserId}/block")
    public ResponseEntity<Friendship> unblockUser(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID otherUserId
    ) {
        return ResponseEntity.ok(friendshipService.unblockUser(userIdentity, otherUserId));
    }
}
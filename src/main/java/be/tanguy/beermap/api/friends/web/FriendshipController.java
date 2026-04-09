package be.tanguy.beermap.api.friends.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;
import be.tanguy.beermap.api.friends.service.FriendshipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.Friendships.BASE)
@RequiredArgsConstructor
@Validated
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping(ApiPaths.Friendships.REQUEST)
    @ResponseStatus(HttpStatus.CREATED)
    public FriendshipResponse sendFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.sendFriendRequest(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.ACCEPT)
    public FriendshipResponse acceptFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.acceptFriendRequest(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.REJECT)
    public FriendshipResponse rejectFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.rejectFriendRequest(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.CANCEL)
    public FriendshipResponse cancelFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.cancelFriendRequest(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.REMOVE)
    public FriendshipResponse removeFriend(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.removeFriend(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.BLOCK)
    public FriendshipResponse blockUser(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.blockUser(userIdentity, request.userId())
        );
    }

    @GetMapping(ApiPaths.Friendships.INCOMING)
    public List<FriendshipResponse> getPendingIncomingRequests(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return friendshipService.getPendingIncomingRequests(userIdentity)
                .stream()
                .map(FriendshipResponse::from)
                .toList();
    }

    @GetMapping(ApiPaths.Friendships.OUTGOING)
    public List<FriendshipResponse> getPendingOutgoingRequests(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return friendshipService.getPendingOutgoingRequests(userIdentity)
                .stream()
                .map(FriendshipResponse::from)
                .toList();
    }

    @GetMapping(ApiPaths.Friendships.FRIENDS)
    public List<FriendshipResponse> getAcceptedFriendships(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return friendshipService.getAcceptedFriendships(userIdentity)
                .stream()
                .map(FriendshipResponse::from)
                .toList();
    }

    @GetMapping(ApiPaths.Friendships.BLOCKED)
    public List<FriendshipResponse> getBlockedUsers(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return friendshipService.getBlockedUsers(userIdentity)
                .stream()
                .map(FriendshipResponse::from)
                .toList();
    }

    @GetMapping("/{otherUserId}")
    public FriendshipResponse getFriendship(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID otherUserId
    ) {
        return FriendshipResponse.from(
                friendshipService.getFriendship(userIdentity, otherUserId)
        );
    }

    public record FriendshipTargetRequest(
            @NotNull UUID userId
    ) {
    }

    public record FriendshipResponse(
            UUID id,
            UUID requesterId,
            UUID addresseeId,
            FriendshipStatus status,
            Instant respondedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static FriendshipResponse from(Friendship friendship) {
            return new FriendshipResponse(
                    friendship.getId(),
                    friendship.getRequesterId(),
                    friendship.getAddresseeId(),
                    friendship.getStatus(),
                    friendship.getRespondedAt(),
                    friendship.getCreatedAt(),
                    friendship.getUpdatedAt()
            );
        }
    }
}
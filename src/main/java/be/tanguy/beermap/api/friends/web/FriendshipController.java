package be.tanguy.beermap.api.friends.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.friends.service.FriendshipService;
import be.tanguy.beermap.api.friends.web.dto.FriendshipByUsernameRequest;
import be.tanguy.beermap.api.friends.web.dto.FriendshipResponse;
import be.tanguy.beermap.api.friends.web.dto.FriendshipTargetRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody FriendshipByUsernameRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.sendFriendRequest(userIdentity, request.targetUsername(), request.targetDiscriminator())
        );
    }

    @PostMapping(ApiPaths.Friendships.RESEND)
    @ResponseStatus(HttpStatus.OK)
    public FriendshipResponse resendFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipByUsernameRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.resendFriendRequest(userIdentity, request.targetUsername(), request.targetDiscriminator())
        );
    }

    @PostMapping(ApiPaths.Friendships.ACCEPT)
    @ResponseStatus(HttpStatus.OK)
    public FriendshipResponse acceptFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.acceptFriendRequest(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.REJECT)
    @ResponseStatus(HttpStatus.OK)
    public FriendshipResponse rejectFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.rejectFriendRequest(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.CANCEL)
    @ResponseStatus(HttpStatus.OK)
    public FriendshipResponse cancelFriendRequest(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.cancelFriendRequest(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.REMOVE)
    @ResponseStatus(HttpStatus.OK)
    public FriendshipResponse removeFriend(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.removeFriend(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.BLOCK)
    @ResponseStatus(HttpStatus.OK)
    public FriendshipResponse blockUser(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.blockUser(userIdentity, request.userId())
        );
    }

    @PostMapping(ApiPaths.Friendships.UNBLOCK)
    @ResponseStatus(HttpStatus.OK)
    public FriendshipResponse unblockUser(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody FriendshipTargetRequest request
    ) {
        return FriendshipResponse.from(
                friendshipService.unblockUser(userIdentity, request.userId())
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

    @GetMapping(ApiPaths.Friendships.BY_USER)
    public FriendshipResponse getFriendship(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID userId
    ) {
        return FriendshipResponse.from(
                friendshipService.getFriendship(userIdentity, userId)
        );
    }
}
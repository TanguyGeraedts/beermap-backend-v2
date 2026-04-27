package be.tanguy.beermap.api.feed.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.feed.service.FeedItemService;
import be.tanguy.beermap.api.feed.web.dto.FeedCheersResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.Feed.BASE)
public class FeedController {

    private final FeedItemService feedItemService;

    @GetMapping
    public FeedPageResponse getFeed(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return feedItemService.getFeed(userIdentity, cursor, limit);
    }

    @PostMapping(ApiPaths.Feed.CHEERS)
    public FeedCheersResponse cheerFeedItem(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID feedItemId
    ) {
        return feedItemService.cheerFeedItem(userIdentity, feedItemId);
    }

    @DeleteMapping(ApiPaths.Feed.CHEERS)
    public FeedCheersResponse uncheerFeedItem(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID feedItemId
    ) {
        return feedItemService.uncheerFeedItem(userIdentity, feedItemId);
    }
}

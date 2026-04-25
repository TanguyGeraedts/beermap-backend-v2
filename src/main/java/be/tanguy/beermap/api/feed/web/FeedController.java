package be.tanguy.beermap.api.feed.web;

import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.feed.service.FeedItemService;
import be.tanguy.beermap.api.feed.web.dto.FeedPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.Feed.BASE)
public class FeedController {

    private final FeedItemService feedItemService;

    @GetMapping
    public FeedPageResponse getFeed(
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return feedItemService.getFeed(cursor, limit);
    }
}
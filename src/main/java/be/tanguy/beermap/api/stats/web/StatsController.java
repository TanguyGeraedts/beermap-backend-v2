package be.tanguy.beermap.api.stats.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.stats.service.StatsService;
import be.tanguy.beermap.api.stats.web.dto.StatsOverviewResponse;
import be.tanguy.beermap.api.users.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.Stats.BASE)
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final UserProfileService userProfileService;

    @GetMapping(ApiPaths.Stats.ME)
    public StatsOverviewResponse getMyStats(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        UUID userId = userProfileService.getCurrentUser(userIdentity).getId();
        return statsService.getOverview(userId);
    }
}

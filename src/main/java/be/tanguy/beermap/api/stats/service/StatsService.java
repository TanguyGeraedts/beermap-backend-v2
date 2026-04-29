package be.tanguy.beermap.api.stats.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.stats.web.dto.StatsOverviewResponse;

public interface StatsService {

    StatsOverviewResponse getOverview(UserIdentity identity);
}

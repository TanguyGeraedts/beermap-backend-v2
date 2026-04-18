package be.tanguy.beermap.api.stats.service;

import be.tanguy.beermap.api.stats.web.dto.StatsOverviewResponse;

import java.util.UUID;

public interface StatsService {

    StatsOverviewResponse getOverview(UUID userId);
}

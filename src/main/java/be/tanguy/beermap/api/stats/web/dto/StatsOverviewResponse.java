package be.tanguy.beermap.api.stats.web.dto;

public record StatsOverviewResponse(
        TotalsStatsResponse totals,
        TimeStatsResponse time,
        CategoryStatsResponse categories,
        FavoritesStatsResponse favorites
) {
}

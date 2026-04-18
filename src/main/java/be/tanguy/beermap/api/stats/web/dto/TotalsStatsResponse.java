package be.tanguy.beermap.api.stats.web.dto;

public record TotalsStatsResponse(
        long totalEntries,
        long totalDrinks,
        long activeDays
) {
}

package be.tanguy.beermap.api.stats.web.dto;

import java.util.List;

public record CategoryStatsResponse(
        List<CountByCategoryResponse> drinksByCategory,
        List<CountByCategoryResponse> entriesByCategory
) {
}

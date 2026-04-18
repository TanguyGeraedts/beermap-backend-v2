package be.tanguy.beermap.api.stats.web.dto;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;

public record CountByCategoryResponse(
        DrinkCategory category,
        long drinks
) {
}

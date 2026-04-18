package be.tanguy.beermap.api.stats.web.dto;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;

public record FavoriteDrinkResponse(
        String name,
        DrinkCategory category,
        long drinks
) {
}

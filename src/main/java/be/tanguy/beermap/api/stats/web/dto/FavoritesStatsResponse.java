package be.tanguy.beermap.api.stats.web.dto;

public record FavoritesStatsResponse(
        FavoriteDrinkResponse favoriteDrinkOverall,
        FavoriteDrinkResponse favoriteDrinkThisMonth,
        FavoriteCategoryResponse favoriteCategoryThisMonth
) {
}

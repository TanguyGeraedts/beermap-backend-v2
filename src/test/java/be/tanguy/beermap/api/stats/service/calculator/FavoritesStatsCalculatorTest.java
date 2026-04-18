package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.stats.web.dto.FavoritesStatsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FavoritesStatsCalculatorTest {

    @Test
    void maps_overall_and_monthly_favorites() {
        FavoritesStatsCalculator calculator = new FavoritesStatsCalculator(
                StatsCalculatorTestSupport.repositoryReturning(Map.of(
                        "findFavoriteDrink", List.<Object[]>of(new Object[]{"Duvel", "BEER", 9L}),
                        "findFavoriteDrinkBetween", List.<Object[]>of(new Object[]{"Negroni", DrinkCategory.COCKTAIL, 4L}),
                        "findFavoriteCategoryBetween", List.<Object[]>of(new Object[]{"COCKTAIL", 6L})
                ))
        );

        FavoritesStatsResponse response = calculator.calculate(UUID.randomUUID());

        assertThat(response.favoriteDrinkOverall().name()).isEqualTo("Duvel");
        assertThat(response.favoriteDrinkOverall().category()).isEqualTo(DrinkCategory.BEER);
        assertThat(response.favoriteDrinkOverall().drinks()).isEqualTo(9);
        assertThat(response.favoriteDrinkThisMonth().name()).isEqualTo("Negroni");
        assertThat(response.favoriteDrinkThisMonth().category()).isEqualTo(DrinkCategory.COCKTAIL);
        assertThat(response.favoriteDrinkThisMonth().drinks()).isEqualTo(4);
        assertThat(response.favoriteCategoryThisMonth().category()).isEqualTo(DrinkCategory.COCKTAIL);
        assertThat(response.favoriteCategoryThisMonth().drinks()).isEqualTo(6);
    }

    @Test
    void returns_null_favorites_when_user_has_no_logs() {
        FavoritesStatsCalculator calculator = new FavoritesStatsCalculator(
                StatsCalculatorTestSupport.repositoryReturning(Map.of(
                        "findFavoriteDrink", List.of(),
                        "findFavoriteDrinkBetween", List.of(),
                        "findFavoriteCategoryBetween", List.of()
                ))
        );

        FavoritesStatsResponse response = calculator.calculate(UUID.randomUUID());

        assertThat(response.favoriteDrinkOverall()).isNull();
        assertThat(response.favoriteDrinkThisMonth()).isNull();
        assertThat(response.favoriteCategoryThisMonth()).isNull();
    }
}

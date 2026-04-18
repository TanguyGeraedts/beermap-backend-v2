package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.stats.web.dto.CategoryStatsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryStatsCalculatorTest {

    @Test
    void maps_category_quantity_rows() {
        CategoryStatsCalculator calculator = new CategoryStatsCalculator(
                StatsCalculatorTestSupport.repositoryReturning(Map.of(
                        "sumQuantityByCategory", List.<Object[]>of(
                                new Object[]{"BEER", 12L},
                                new Object[]{DrinkCategory.WINE, 3L}
                        )
                ))
        );

        CategoryStatsResponse response = calculator.calculate(UUID.randomUUID());

        assertThat(response.drinksByCategory())
                .extracting("category", "drinks")
                .containsExactly(
                        org.assertj.core.api.Assertions.tuple(DrinkCategory.BEER, 12L),
                        org.assertj.core.api.Assertions.tuple(DrinkCategory.WINE, 3L)
                );
    }
}

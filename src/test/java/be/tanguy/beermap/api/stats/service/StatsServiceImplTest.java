package be.tanguy.beermap.api.stats.service;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.stats.service.calculator.CategoryStatsCalculator;
import be.tanguy.beermap.api.stats.service.calculator.FavoritesStatsCalculator;
import be.tanguy.beermap.api.stats.service.calculator.TimeStatsCalculator;
import be.tanguy.beermap.api.stats.service.calculator.TotalsStatsCalculator;
import be.tanguy.beermap.api.stats.web.dto.CategoryStatsResponse;
import be.tanguy.beermap.api.stats.web.dto.CountByCategoryResponse;
import be.tanguy.beermap.api.stats.web.dto.FavoriteCategoryResponse;
import be.tanguy.beermap.api.stats.web.dto.FavoriteDrinkResponse;
import be.tanguy.beermap.api.stats.web.dto.FavoritesStatsResponse;
import be.tanguy.beermap.api.stats.web.dto.StatsOverviewResponse;
import be.tanguy.beermap.api.stats.web.dto.TimeStatsResponse;
import be.tanguy.beermap.api.stats.web.dto.TotalsStatsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StatsServiceImplTest {

    @Test
    void combines_calculator_outputs_into_overview() {
        StatsServiceImpl service = new StatsServiceImpl(
                new TotalsCalculatorStub(new TotalsStatsResponse(3, 8, 2)),
                new TimeCalculatorStub(new TimeStatsResponse(List.of(), List.of(), 20, 2)),
                new CategoryCalculatorStub(new CategoryStatsResponse(List.of(
                        new CountByCategoryResponse(DrinkCategory.BEER, 8)
                ))),
                new FavoritesCalculatorStub(new FavoritesStatsResponse(
                        new FavoriteDrinkResponse("Duvel", DrinkCategory.BEER, 5),
                        new FavoriteDrinkResponse("Tripel", DrinkCategory.BEER, 3),
                        new FavoriteCategoryResponse(DrinkCategory.BEER, 8)
                ))
        );

        StatsOverviewResponse response = service.getOverview(UUID.randomUUID());

        assertThat(response.totals().totalEntries()).isEqualTo(3);
        assertThat(response.time().peakHour()).isEqualTo(20);
        assertThat(response.categories().drinksByCategory().getFirst().category()).isEqualTo(DrinkCategory.BEER);
        assertThat(response.favorites().favoriteDrinkOverall().name()).isEqualTo("Duvel");
    }

    private static class TotalsCalculatorStub extends TotalsStatsCalculator {
        private final TotalsStatsResponse response;

        private TotalsCalculatorStub(TotalsStatsResponse response) {
            super(null);
            this.response = response;
        }

        @Override
        public TotalsStatsResponse calculate(UUID userId) {
            return response;
        }
    }

    private static class TimeCalculatorStub extends TimeStatsCalculator {
        private final TimeStatsResponse response;

        private TimeCalculatorStub(TimeStatsResponse response) {
            super(null);
            this.response = response;
        }

        @Override
        public TimeStatsResponse calculate(UUID userId) {
            return response;
        }
    }

    private static class CategoryCalculatorStub extends CategoryStatsCalculator {
        private final CategoryStatsResponse response;

        private CategoryCalculatorStub(CategoryStatsResponse response) {
            super(null);
            this.response = response;
        }

        @Override
        public CategoryStatsResponse calculate(UUID userId) {
            return response;
        }
    }

    private static class FavoritesCalculatorStub extends FavoritesStatsCalculator {
        private final FavoritesStatsResponse response;

        private FavoritesCalculatorStub(FavoritesStatsResponse response) {
            super(null);
            this.response = response;
        }

        @Override
        public FavoritesStatsResponse calculate(UUID userId) {
            return response;
        }
    }
}

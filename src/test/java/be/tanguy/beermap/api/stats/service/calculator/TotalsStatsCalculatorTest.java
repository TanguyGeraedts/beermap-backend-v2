package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.stats.web.dto.TotalsStatsResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TotalsStatsCalculatorTest {

    @Test
    void calculates_lifetime_totals() {
        TotalsStatsCalculator calculator = new TotalsStatsCalculator(
                StatsCalculatorTestSupport.repositoryReturning(Map.of(
                        "countByUserId", 7L,
                        "sumQuantityByUserId", 15L,
                        "countActiveDays", 4L
                ))
        );

        TotalsStatsResponse response = calculator.calculate(UUID.randomUUID());

        assertThat(response.totalEntries()).isEqualTo(7);
        assertThat(response.totalDrinks()).isEqualTo(15);
        assertThat(response.activeDays()).isEqualTo(4);
    }
}

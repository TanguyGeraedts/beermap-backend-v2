package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.stats.web.dto.TimeStatsResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TimeStatsCalculatorTest {

    @Test
    void calculates_time_stats_and_streak() {
        TimeStatsCalculator calculator = new TimeStatsCalculator(
                StatsCalculatorTestSupport.repositoryReturning(Map.of(
                        "sumDrinksPerUtcDay", List.<Object[]>of(
                                new Object[]{LocalDate.of(2026, 4, 17), 2L},
                                new Object[]{LocalDate.of(2026, 4, 18), 5L}
                        ),
                        "sumDrinksPerUtcIsoWeek", List.<Object[]>of(
                                new Object[]{2026, 16, 7L}
                        ),
                        "findPeakHourUtc", 21,
                        "findDistinctUtcDaysDesc", List.of(
                                LocalDate.of(2026, 4, 18),
                                LocalDate.of(2026, 4, 17),
                                LocalDate.of(2026, 4, 16),
                                LocalDate.of(2026, 4, 14)
                        )
                ))
        );

        TimeStatsResponse response = calculator.calculate(UUID.randomUUID());

        assertThat(response.drinksPerDay()).hasSize(2);
        assertThat(response.drinksPerDay().getFirst().day()).isEqualTo(LocalDate.of(2026, 4, 17));
        assertThat(response.drinksPerDay().getFirst().drinks()).isEqualTo(2);
        assertThat(response.drinksPerWeek()).hasSize(1);
        assertThat(response.drinksPerWeek().getFirst().isoYear()).isEqualTo(2026);
        assertThat(response.drinksPerWeek().getFirst().isoWeek()).isEqualTo(16);
        assertThat(response.drinksPerWeek().getFirst().drinks()).isEqualTo(7);
        assertThat(response.peakHour()).isEqualTo(21);
        assertThat(response.streak()).isEqualTo(3);
    }

    @Test
    void returns_zero_streak_when_user_has_no_logs() {
        Map<String, Object> repositoryResults = new HashMap<>();
        repositoryResults.put("sumDrinksPerUtcDay", List.of());
        repositoryResults.put("sumDrinksPerUtcIsoWeek", List.of());
        repositoryResults.put("findPeakHourUtc", null);
        repositoryResults.put("findDistinctUtcDaysDesc", List.of());

        TimeStatsCalculator calculator = new TimeStatsCalculator(
                StatsCalculatorTestSupport.repositoryReturning(repositoryResults)
        );

        TimeStatsResponse response = calculator.calculate(UUID.randomUUID());

        assertThat(response.streak()).isZero();
        assertThat(response.peakHour()).isNull();
    }
}

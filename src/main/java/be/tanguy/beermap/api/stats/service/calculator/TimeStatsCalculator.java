package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.stats.web.dto.CountByDayResponse;
import be.tanguy.beermap.api.stats.web.dto.CountByWeekResponse;
import be.tanguy.beermap.api.stats.web.dto.TimeStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TimeStatsCalculator {

    private final DrinkLogRepository drinkLogRepository;

    public TimeStatsResponse calculate(UUID userId) {
        List<CountByDayResponse> drinksPerDay = drinkLogRepository.sumDrinksPerUtcDay(userId)
                .stream()
                .map(this::mapDayRow)
                .toList();
        List<CountByWeekResponse> drinksPerWeek = drinkLogRepository.sumDrinksPerUtcIsoWeek(userId)
                .stream()
                .map(this::mapWeekRow)
                .toList();
        Integer peakHour = drinkLogRepository.findPeakHourUtc(userId);
        int streak = calculateStreakEndingLastDrinkUtc(drinkLogRepository.findDistinctUtcDaysDesc(userId));

        return new TimeStatsResponse(drinksPerDay, drinksPerWeek, peakHour, streak);
    }

    private CountByDayResponse mapDayRow(Object[] row) {
        return new CountByDayResponse(
                toLocalDate(row[0]),
                ((Number) row[1]).longValue()
        );
    }

    private CountByWeekResponse mapWeekRow(Object[] row) {
        return new CountByWeekResponse(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).intValue(),
                ((Number) row[2]).longValue()
        );
    }

    private int calculateStreakEndingLastDrinkUtc(List<LocalDate> daysDesc) {
        if (daysDesc == null || daysDesc.isEmpty()) {
            return 0;
        }

        List<LocalDate> days = daysDesc.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        int streak = 1;
        LocalDate expected = days.getFirst().minusDays(1);

        for (int i = 1; i < days.size(); i++) {
            LocalDate day = days.get(i);
            if (!day.isEqual(expected)) {
                break;
            }

            streak++;
            expected = expected.minusDays(1);
        }

        return streak;
    }

    private LocalDate toLocalDate(Object value) {
        return switch (value) {
            case null -> null;
            case LocalDate localDate -> localDate;
            case Date date -> date.toLocalDate();
            case Timestamp timestamp -> timestamp.toLocalDateTime().toLocalDate();
            default -> throw new IllegalStateException("Unsupported date type from native query: " + value.getClass());
        };
    }
}

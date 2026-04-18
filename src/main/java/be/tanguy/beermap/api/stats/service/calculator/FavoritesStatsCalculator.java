package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.stats.web.dto.FavoriteCategoryResponse;
import be.tanguy.beermap.api.stats.web.dto.FavoriteDrinkResponse;
import be.tanguy.beermap.api.stats.web.dto.FavoritesStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FavoritesStatsCalculator {

    private final DrinkLogRepository drinkLogRepository;

    public FavoritesStatsResponse calculate(UUID userId) {
        YearMonth currentMonth = YearMonth.now(ZoneOffset.UTC);
        Instant monthStart = currentMonth
                .atDay(1)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
        Instant nextMonthStart = currentMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);

        return new FavoritesStatsResponse(
                mapFavoriteDrink(drinkLogRepository.findFavoriteDrink(userId)),
                mapFavoriteDrink(drinkLogRepository.findFavoriteDrinkBetween(userId, monthStart, nextMonthStart)),
                mapFavoriteCategory(drinkLogRepository.findFavoriteCategoryBetween(userId, monthStart, nextMonthStart))
        );
    }

    private FavoriteDrinkResponse mapFavoriteDrink(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return null;
        }

        Object[] row = rows.getFirst();
        return new FavoriteDrinkResponse(
                (String) row[0],
                toCategory(row[1]),
                ((Number) row[2]).longValue()
        );
    }

    private FavoriteCategoryResponse mapFavoriteCategory(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return null;
        }

        Object[] row = rows.getFirst();
        return new FavoriteCategoryResponse(
                toCategory(row[0]),
                ((Number) row[1]).longValue()
        );
    }

    private DrinkCategory toCategory(Object value) {
        if (value instanceof DrinkCategory category) {
            return category;
        }

        return DrinkCategory.valueOf(value.toString());
    }
}

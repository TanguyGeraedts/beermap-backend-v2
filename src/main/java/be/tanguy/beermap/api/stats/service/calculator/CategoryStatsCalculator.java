package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.stats.web.dto.CategoryStatsResponse;
import be.tanguy.beermap.api.stats.web.dto.CountByCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryStatsCalculator {

    private final DrinkLogRepository drinkLogRepository;

    public CategoryStatsResponse calculate(UUID userId) {
        List<CountByCategoryResponse> drinksByCategory = drinkLogRepository.sumQuantityByCategory(userId)
                .stream()
                .map(this::mapCategoryRow)
                .toList();

        return new CategoryStatsResponse(drinksByCategory);
    }

    private CountByCategoryResponse mapCategoryRow(Object[] row) {
        return new CountByCategoryResponse(
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

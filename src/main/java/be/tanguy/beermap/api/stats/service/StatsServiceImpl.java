package be.tanguy.beermap.api.stats.service;

import be.tanguy.beermap.api.stats.service.calculator.CategoryStatsCalculator;
import be.tanguy.beermap.api.stats.service.calculator.FavoritesStatsCalculator;
import be.tanguy.beermap.api.stats.service.calculator.TimeStatsCalculator;
import be.tanguy.beermap.api.stats.service.calculator.TotalsStatsCalculator;
import be.tanguy.beermap.api.stats.web.dto.StatsOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final TotalsStatsCalculator totalsStatsCalculator;
    private final TimeStatsCalculator timeStatsCalculator;
    private final CategoryStatsCalculator categoryStatsCalculator;
    private final FavoritesStatsCalculator favoritesStatsCalculator;

    @Override
    @Transactional(readOnly = true)
    public StatsOverviewResponse getOverview(UUID userId) {
        return new StatsOverviewResponse(
                totalsStatsCalculator.calculate(userId),
                timeStatsCalculator.calculate(userId),
                categoryStatsCalculator.calculate(userId),
                favoritesStatsCalculator.calculate(userId)
        );
    }
}

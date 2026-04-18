package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.stats.web.dto.TotalsStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TotalsStatsCalculator {

    private final DrinkLogRepository drinkLogRepository;

    public TotalsStatsResponse calculate(UUID userId) {
        return new TotalsStatsResponse(
                drinkLogRepository.countByUserId(userId),
                drinkLogRepository.sumQuantityByUserId(userId),
                drinkLogRepository.countActiveDays(userId)
        );
    }
}

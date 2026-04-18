package be.tanguy.beermap.api.stats.web.dto;

import java.util.List;

public record TimeStatsResponse(
        List<CountByDayResponse> drinksPerDay,
        List<CountByWeekResponse> drinksPerWeek,
        Integer peakHour,
        int streak
) {
}

package be.tanguy.beermap.api.stats.web.dto;

public record CountByWeekResponse(
        int isoYear,
        int isoWeek,
        long drinks
) {
}

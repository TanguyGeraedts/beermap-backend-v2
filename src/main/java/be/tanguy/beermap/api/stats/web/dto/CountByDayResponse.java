package be.tanguy.beermap.api.stats.web.dto;

import java.time.LocalDate;

public record CountByDayResponse(
        LocalDate day,
        long drinks
) {
}

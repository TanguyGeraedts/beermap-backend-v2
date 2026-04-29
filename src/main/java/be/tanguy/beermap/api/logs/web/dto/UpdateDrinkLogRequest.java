package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.common.validation.AllowedContent;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateDrinkLogRequest(
        Instant consumedAt,
        @Positive(message = "Quantity must be positive")
        Integer quantity,
        Integer volumeMl,
        BigDecimal abvPercent,
        DrinkCategory category,
        @AllowedContent
        String subType,
        Integer rating,
        @AllowedContent
        String description,
        String photoUrl,
        Double latitude,
        Double longitude
) { }

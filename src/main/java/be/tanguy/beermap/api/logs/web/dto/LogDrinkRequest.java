package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.service.command.LogDrinkCommand;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LogDrinkRequest(
        UUID catalogItemId,
        String customDrinkName,
        DrinkCategory category,
        String subType,
        Instant consumedAt,
        Integer quantity,
        Integer volumeMl,
        BigDecimal abvPercent,
        Integer rating,
        String description,
        String photoUrl,
        Double latitude,
        Double longitude
) {
    public LogDrinkCommand toCommand(UUID userId) {
        return LogDrinkCommand.builder()
                .userId(userId)
                .catalogItemId(catalogItemId)
                .customDrinkName(customDrinkName)
                .category(category)
                .subType(subType)
                .consumedAt(consumedAt)
                .quantity(quantity)
                .volumeMl(volumeMl)
                .abvPercent(abvPercent)
                .rating(rating)
                .description(description)
                .photoUrl(photoUrl)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
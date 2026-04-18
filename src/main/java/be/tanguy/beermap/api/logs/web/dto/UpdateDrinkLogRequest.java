package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.common.validation.AllowedContent;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.service.command.UpdateDrinkLogCommand;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
) {
    public UpdateDrinkLogCommand toCommand(UUID userId, UUID logId) {
        return UpdateDrinkLogCommand.builder()
                .userId(userId)
                .logId(logId)
                .consumedAt(consumedAt)
                .quantity(quantity)
                .volumeMl(volumeMl)
                .abvPercent(abvPercent)
                .category(category)
                .subType(subType)
                .rating(rating)
                .description(description)
                .photoUrl(photoUrl)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}

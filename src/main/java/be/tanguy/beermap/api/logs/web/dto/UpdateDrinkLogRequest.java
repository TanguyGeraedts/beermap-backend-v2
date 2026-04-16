package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.service.command.UpdateDrinkLogCommand;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UpdateDrinkLogRequest(
        Instant consumedAt,
        Integer quantity,
        Integer volumeMl,
        BigDecimal abvPercent,
        DrinkCategory category,
        String subType,
        Integer rating,
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
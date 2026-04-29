package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.logs.domain.DrinkLog;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DrinkLogResponse(
        UUID id,
        String displayName,
        String category,
        String subType,
        BigDecimal abvPercent,
        Instant consumedAt,
        int quantity,
        Integer volumeMl,
        Integer rating,
        String description,
        String photoUrl,
        Double latitude,
        Double longitude
) {
    public static DrinkLogResponse from(DrinkLog log) {
        return new DrinkLogResponse(
                log.getId(),
                log.getDisplayName(),
                log.getIdentity().getCategory().name(),
                log.getIdentity().getSubType(),
                log.getIdentity().getAbvPercent(),
                log.getConsumption().getConsumedAt(),
                log.getConsumption().getQuantity(),
                log.getConsumption().getVolumeMl(),
                log.getConsumption().getRating(),
                log.getConsumption().getDescription(),
                log.getConsumption().getPhotoUrl(),
                log.getLocation() != null ? log.getLocation().latitude() : null,
                log.getLocation() != null ? log.getLocation().longitude() : null
        );
    }
}
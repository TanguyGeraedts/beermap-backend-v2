package be.tanguy.beermap.api.logs.service.command;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Command object for partially updating an existing drink log.
 * <p>
 * All fields except {@code userId} and {@code logId} are optional.
 * Only non-null values will be applied.
 * </p>
 */
@Builder
public record UpdateDrinkLogCommand(

        UUID userId,
        UUID logId,

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

) {}
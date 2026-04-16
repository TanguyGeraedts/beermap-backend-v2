package be.tanguy.beermap.api.logs.service.command;

import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Command object for logging a drink (catalog or custom).
 * <p>
 * Exactly one of {@code catalogItemId} or {@code customDrinkName} must be set.
 * {@code category} is required for custom drinks; for catalog drinks it is
 * derived from the catalog item and any value here is ignored.
 * </p>
 */
@Builder
public record LogDrinkCommand(

        // Required
        UUID userId,

        // Drink identity, exactly one must be non-null
        UUID catalogItemId,
        String customDrinkName,

        // Required for custom drinks, ignored for catalog drinks
        DrinkCategory category,

        // Optional fields
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
    public boolean isCatalog() {
        return catalogItemId != null;
    }
}
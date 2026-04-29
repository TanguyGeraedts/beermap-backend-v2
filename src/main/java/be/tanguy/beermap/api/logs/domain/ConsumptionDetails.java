package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.logs.exception.InvalidConsumptionException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsumptionDetails {

    @NotNull
    @Column(name = "consumed_at", nullable = false)
    private Instant consumedAt;

    @Positive
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @Min(1)
    @Max(10)
    @Column(name = "rating")
    private Integer rating;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "photo_url", length = 2048)
    private String photoUrl;

    public static ConsumptionDetails of(
            Instant consumedAt,
            int quantity,
            Integer volumeMl,
            Integer rating,
            String description,
            String photoUrl
    ) {
        validateVolume(volumeMl);

        ConsumptionDetails d = new ConsumptionDetails();
        d.consumedAt = consumedAt != null ? consumedAt : Instant.now();
        d.quantity = requirePositiveQuantity(quantity);
        d.volumeMl = normalizePositiveOrNull(volumeMl);
        d.rating = rating;
        d.description = normalizeNullable(description);
        d.photoUrl = normalizeNullable(photoUrl);
        return d;
    }

    public void update(
            Instant consumedAt,
            Integer quantity,
            Integer volumeMl,
            Integer rating,
            String description,
            String photoUrl
    ) {
        if (consumedAt != null) this.consumedAt = consumedAt;
        if (quantity != null) this.quantity = requirePositiveQuantity(quantity);
        if (volumeMl != null) {
            validateVolume(volumeMl);
            this.volumeMl = normalizePositiveOrNull(volumeMl);
        }
        if (rating != null) this.rating = rating;
        if (description != null) this.description = normalizeNullable(description);
        if (photoUrl != null) this.photoUrl = normalizeNullable(photoUrl);
    }

    private static void validateVolume(Integer volumeMl) {
        if (volumeMl != null && volumeMl <= 0) {
            throw new InvalidConsumptionException("volumeMl must be positive");
        }
    }

    private static String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static Integer normalizePositiveOrNull(Integer value) {
        if (value == null) return null;
        return value > 0 ? value : null;
    }

    private static int requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidConsumptionException("quantity must be positive");
        }
        return quantity;
    }
}
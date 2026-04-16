package be.tanguy.beermap.api.logs.domain;

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

    @Min(1) @Max(10)
    @Column(name = "rating")
    private Integer rating;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "photo_url", length = 2048)
    private String photoUrl;

    // -------------------------------------------------------------------------
    // Factory method
    // -------------------------------------------------------------------------

    public static ConsumptionDetails of(
            Instant consumedAt,
            int quantity,
            Integer volumeMl,
            Integer rating,
            String description,
            String photoUrl
    ) {
        ConsumptionDetails d = new ConsumptionDetails();
        d.consumedAt  = consumedAt != null ? consumedAt : Instant.now();
        d.quantity    = quantity > 0 ? quantity : 1;
        d.volumeMl    = normalizePositiveOrNull(volumeMl);
        d.rating      = rating;
        d.description = normalizeNullable(description);
        d.photoUrl    = normalizeNullable(photoUrl);
        return d;
    }

    // -------------------------------------------------------------------------
    // Mutation
    // -------------------------------------------------------------------------

    /**
     * Partially updates consumption fields. Only non-null values are applied.
     */
    public void update(
            Instant consumedAt,
            Integer quantity,
            Integer volumeMl,
            Integer rating,
            String description,
            String photoUrl
    ) {
        if (consumedAt != null)  this.consumedAt  = consumedAt;
        if (quantity != null)    this.quantity    = quantity > 0 ? quantity : 1;
        if (volumeMl != null)    this.volumeMl    = volumeMl > 0 ? volumeMl : null;
        if (rating != null)      this.rating      = rating;
        if (description != null) this.description = normalizeNullable(description);
        if (photoUrl != null)    this.photoUrl    = normalizeNullable(photoUrl);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static Integer normalizePositiveOrNull(Integer value) {
        if (value == null) return null;
        return value > 0 ? value : null;
    }
}
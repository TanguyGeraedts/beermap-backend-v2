package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.domain.Location;
import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "drink_log",
        indexes = {
                @Index(name = "idx_drink_log_user_id",    columnList = "user_id"),
                @Index(name = "idx_drink_log_consumed_at", columnList = "consumed_at")
        }
)
@Access(AccessType.FIELD)
public class DrinkLog extends AbstractBaseObject {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Embedded
    private DrinkIdentity identity;

    @Embedded
    private ConsumptionDetails consumption;

    @Embedded
    private Location location;

    // -------------------------------------------------------------------------
    // Factory method
    // -------------------------------------------------------------------------

    /**
     * Creates a new DrinkLog entry.
     */
    public static DrinkLog create(
            UUID userId,
            DrinkIdentity identity,
            ConsumptionDetails consumption,
            Location location
    ) {
        if (userId == null)      throw new IllegalArgumentException("userId is required");
        if (identity == null)    throw new IllegalArgumentException("Drink identity is required");
        if (consumption == null) throw new IllegalArgumentException("Consumption details are required");

        DrinkLog log = new DrinkLog();
        log.id          = UUID.randomUUID();
        log.userId      = userId;
        log.identity    = identity;
        log.consumption = consumption;
        log.location    = location;
        return log;
    }

    // -------------------------------------------------------------------------
    // Mutation
    // -------------------------------------------------------------------------

    /**
     * Partially updates this log entry. Only non-null arguments are applied.
     * Pass an empty string to {@code subType} or {@code description} to clear them.
     */
    public void update(
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
        identity.update(category, subType, abvPercent);
        consumption.update(consumedAt, quantity, volumeMl, rating, description, photoUrl);

        if (latitude != null || longitude != null) {
            this.location = new Location(latitude, longitude);
        }
    }

    // -------------------------------------------------------------------------
    // Query helpers
    // -------------------------------------------------------------------------

    public boolean isCatalogDrink()  { return identity.isCatalog(); }
    public boolean isCustomDrink()   { return !identity.isCatalog(); }
    public String  getDisplayName()  { return identity.getDisplayName(); }
}
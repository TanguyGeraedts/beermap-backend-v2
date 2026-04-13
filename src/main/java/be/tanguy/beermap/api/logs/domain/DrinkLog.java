package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.domain.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "drink_log",
        indexes = {
                @Index(name = "idx_drink_log_user_id", columnList = "user_id"),
                @Index(name = "idx_drink_log_consumed_at", columnList = "consumed_at")
        }
)
@Access(AccessType.FIELD)
public class DrinkLog {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Embedded
    private DrinkIdentity identity;

    @Embedded
    private ConsumptionDetails consumption;

    @Embedded
    private Location location;

    private DrinkLog(UUID userId, DrinkIdentity identity, ConsumptionDetails consumption, Location location) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.identity = identity;
        this.consumption = consumption;
        this.location = location;
    }

    /**
     * Creates a new DrinkLog entry.
     * Validates that the entry is tied to a specific user and contains
     * the necessary identity and consumption metadata.
     */
    public static DrinkLog create(UUID userId, DrinkIdentity identity, ConsumptionDetails consumption, Location location) {
        if (userId == null) throw new IllegalArgumentException("User ID is required");
        if (identity == null) throw new IllegalArgumentException("Drink identity is required");
        if (consumption == null) throw new IllegalArgumentException("Consumption details are required");

        return new DrinkLog(userId, identity, consumption, location);
    }

    public String getDisplayName() {
        return identity.getDisplayName();
    }

    public boolean isCatalogDrink() {
        return identity.getCatalogItemId() != null;
    }

    public boolean isCustomDrink() {
        return identity.getCustomDrinkName() != null;
    }
}
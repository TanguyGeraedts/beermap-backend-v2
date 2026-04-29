package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.domain.Location;
import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import be.tanguy.beermap.api.logs.exception.DrinkLogOwnershipException;
import be.tanguy.beermap.api.logs.exception.InvalidConsumptionException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "drink_log")
public class DrinkLog extends AbstractBaseObject {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Embedded
    private DrinkIdentity identity;

    @Embedded
    private ConsumptionDetails consumption;

    @Embedded
    private Location location;

    public static DrinkLog create(
            UUID userId,
            DrinkIdentity identity,
            ConsumptionDetails consumption,
            Location location
    ) {
        if (userId == null) throw new InvalidConsumptionException("userId is required");
        if (identity == null) throw new InvalidConsumptionException("Drink identity is required");
        if (consumption == null) throw new InvalidConsumptionException("Consumption details are required");

        DrinkLog log = new DrinkLog();
        log.id = UUID.randomUUID();
        log.userId = userId;
        log.identity = identity;
        log.consumption = consumption;
        log.location = location;
        return log;
    }

    public void update(
            Instant consumedAt, Integer quantity, Integer volumeMl,
            BigDecimal abvPercent, DrinkCategory category, String subType,
            Integer rating, String description, String photoUrl,
            Double latitude, Double longitude
    ) {
        identity.update(category, subType, abvPercent);
        consumption.update(consumedAt, quantity, volumeMl, rating, description, photoUrl);

        if (latitude != null || longitude != null) {
            this.location = Location.of(latitude, longitude);
        }
    }

    public String getDisplayName() {
        return identity.getDisplayName();
    }

    public void verifyOwnership(UUID userId) {
        if (!this.userId.equals(userId)) {
            throw new DrinkLogOwnershipException(userId, this.id);
        }
    }
}
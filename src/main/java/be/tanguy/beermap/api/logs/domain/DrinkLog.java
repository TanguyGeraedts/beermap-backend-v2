package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.domain.Location;
import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import be.tanguy.beermap.api.logs.exception.DrinkLogOwnershipException;
import be.tanguy.beermap.api.logs.exception.InvalidConsumptionException;
import be.tanguy.beermap.api.logs.web.dto.UpdateDrinkLogRequest;
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

    public void update(UpdateDrinkLogRequest request, String resolvedCatalogName) {
        identity.updateIdentity(
                request.catalogItemId(),
                resolvedCatalogName,
                request.customDrinkName(),
                request.category(),
                request.subType(),
                request.abvPercent()
        );
        consumption.update(
                request.consumedAt(), request.quantity(), request.volumeMl(),
                request.rating(), request.description(), request.photoUrl()
        );
        if (request.latitude() != null || request.longitude() != null) {
            this.location = Location.of(request.latitude(), request.longitude());
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
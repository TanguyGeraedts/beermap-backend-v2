package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.domain.Location;
import be.tanguy.beermap.api.logs.exception.InvalidConsumptionException;
import be.tanguy.beermap.api.logs.web.dto.UpdateDrinkLogRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DrinkLogTest {

    private UUID userId;
    private DrinkIdentity catalogIdentity;
    private ConsumptionDetails consumption;
    private DrinkIdentity customIdentity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        catalogIdentity = DrinkIdentity.forCatalog(
                UUID.randomUUID(), "Pilsner Urquell",
                DrinkCategory.BEER, "Pilsner", new BigDecimal("4.4")
        );
        customIdentity = DrinkIdentity.forCustom(
                "Original Custom IPA", DrinkCategory.BEER, "IPA", new BigDecimal("6.5")
        );
        consumption = ConsumptionDetails.of(
                Instant.now(), 1, 500, 8, "Great beer", null
        );
    }

    @Nested
    class Create {
        @Test
        void creates_valid_log() {
            Location loc = new Location(51.0, 4.0);
            DrinkLog log = DrinkLog.create(userId, catalogIdentity, consumption, loc);

            assertThat(log.getUserId()).isEqualTo(userId);
            assertThat(log.getIdentity()).isEqualTo(catalogIdentity);
            assertThat(log.getLocation()).isEqualTo(loc);
        }

        @Test
        void throws_when_userId_is_null() {
            assertThatThrownBy(() -> DrinkLog.create(null, catalogIdentity, consumption, null))
                    .isInstanceOf(InvalidConsumptionException.class)
                    .hasMessageContaining("userId is required");
        }
    }

    @Nested
    class Update {
        @Test
        void updates_consumption_fields() {
            DrinkLog log = DrinkLog.create(userId, catalogIdentity, consumption, null);

            UpdateDrinkLogRequest request = new UpdateDrinkLogRequest(
                    null, null, null, null, null, null,
                    null, null, 9, "Updated description", null, 50.0, 3.0
            );

            log.update(request, null);

            assertThat(log.getConsumption().getRating()).isEqualTo(9);
            assertThat(log.getConsumption().getDescription()).isEqualTo("Updated description");
            assertThat(log.getLocation().latitude()).isEqualTo(50.0);
        }

        @Test
        void switches_catalog_to_custom() {
            DrinkLog log = DrinkLog.create(userId, catalogIdentity, consumption, null);

            UpdateDrinkLogRequest request = new UpdateDrinkLogRequest(
                    null, "My Custom IPA", null, null, null, null,
                    DrinkCategory.BEER, "IPA", null, null, null, null, null
            );

            log.update(request, null);

            assertThat(log.getIdentity().isCatalog()).isFalse();
            assertThat(log.getIdentity().getDisplayName()).isEqualTo("My Custom IPA");
        }

        @Test
        void switches_custom_to_catalog() {
            DrinkLog log = DrinkLog.create(userId, customIdentity, consumption, null);

            UpdateDrinkLogRequest request = new UpdateDrinkLogRequest(
                    UUID.randomUUID(), null, null, null, null, null,
                    null, null, null, null, null, null, null
            );

            log.update(request, "Catalog Beer Name");

            assertThat(log.getIdentity().isCatalog()).isTrue();
            assertThat(log.getIdentity().getDisplayName()).isEqualTo("Catalog Beer Name");
        }
    }
}
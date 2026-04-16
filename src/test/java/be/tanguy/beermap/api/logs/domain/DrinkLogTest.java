package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.domain.Location;
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

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        catalogIdentity = DrinkIdentity.forCatalog(
                UUID.randomUUID(), "Pilsner Urquell",
                DrinkCategory.BEER, "Pilsner", new BigDecimal("4.4")
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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("userId is required");
        }
    }

    @Nested
    class Update {
        @Test
        void updates_fields_when_provided() {
            DrinkLog log = DrinkLog.create(userId, catalogIdentity, consumption, null);
            String newDesc = "Updated description";

            log.update(null, null, null, null, null, null, 9, newDesc, null, 50.0, 3.0);

            assertThat(log.getConsumption().getRating()).isEqualTo(9);
            assertThat(log.getConsumption().getDescription()).isEqualTo(newDesc);
            assertThat(log.getLocation().getLatitude()).isEqualTo(50.0);
        }
    }
}
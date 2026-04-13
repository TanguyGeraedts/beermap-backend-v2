package be.tanguy.beermap.api.logs.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrinkIdentity {
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private DrinkCategory category;

    @Column(name = "sub_type", length = 100)
    private String subType;

    @Column(name = "catalog_item_id")
    private UUID catalogItemId;

    @Column(name = "catalog_item_name")
    private String catalogItemName;

    @Column(name = "custom_drink_name")
    private String customDrinkName;

    @Column(name = "abv_percent", precision = 5, scale = 2)
    private BigDecimal abvPercent;

    public String getDisplayName() {
        return catalogItemId != null ? catalogItemName : customDrinkName;
    }

    //To-do
    // Static factory methods for Catalog vs Custom here.
}
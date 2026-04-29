package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.logs.exception.InvalidDrinkIdentityException;
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

    @Column(name = "catalog_item_name", length = 255)
    private String catalogItemName;

    @Column(name = "custom_drink_name", length = 255)
    private String customDrinkName;

    @Column(name = "abv_percent", precision = 5, scale = 2)
    private BigDecimal abvPercent;

    public static DrinkIdentity forCatalog(
            UUID catalogItemId,
            String catalogItemName,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    ) {
        if (catalogItemId == null) throw new InvalidDrinkIdentityException("catalogItemId is required");
        if (catalogItemName == null || catalogItemName.isBlank()) throw new InvalidDrinkIdentityException("catalogItemName is required");

        DrinkIdentity id = new DrinkIdentity();
        id.catalogItemId = catalogItemId;
        id.catalogItemName = catalogItemName.trim();
        id.category = category;
        id.subType = normalizeNullable(subType);
        id.abvPercent = abvPercent;
        return id;
    }

    public static DrinkIdentity forCustom(
            String customDrinkName,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    ) {
        if (customDrinkName == null || customDrinkName.isBlank()) throw new InvalidDrinkIdentityException("customDrinkName is required");
        if (category == null) throw new InvalidDrinkIdentityException("category is required");

        DrinkIdentity id = new DrinkIdentity();
        id.customDrinkName = customDrinkName.trim();
        id.category = category;
        id.subType = normalizeNullable(subType);
        id.abvPercent = abvPercent;
        return id;
    }

    public void update(DrinkCategory category, String subType, BigDecimal abvPercent) {
        if (category != null) this.category = category;
        if (subType != null) this.subType = normalizeNullable(subType);
        if (abvPercent != null) this.abvPercent = abvPercent;
    }

    public String getDisplayName() {
        return catalogItemId != null ? catalogItemName : customDrinkName;
    }

    public boolean isCatalog() {
        return catalogItemId != null;
    }

    private static String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
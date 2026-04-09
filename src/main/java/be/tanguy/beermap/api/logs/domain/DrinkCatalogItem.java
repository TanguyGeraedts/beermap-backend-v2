package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import be.tanguy.beermap.api.logs.exception.InvalidDrinkCatalogItemException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "drink_catalog_item",
        indexes = {
                @Index(name = "idx_drink_catalog_item_normalized_name", columnList = "normalized_name"),
                @Index(name = "idx_drink_catalog_item_normalized_sub_type", columnList = "normalized_sub_type")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_drink_catalog_item_normalized_name", columnNames = "normalized_name")
        }
)
@Access(AccessType.FIELD)
@Getter
public class DrinkCatalogItem extends AbstractBaseObject {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "normalized_name", nullable = false, length = 255)
    private String normalizedName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private DrinkCategory category;

    @Column(name = "sub_type", length = 100)
    private String subType;

    @Column(name = "normalized_sub_type", length = 100)
    private String normalizedSubType;

    @Column(name = "abv_percent", precision = 5, scale = 2)
    private BigDecimal abvPercent;

    private DrinkCatalogItem(
            String name,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    ) {
        setName(name);
        setCategory(category);
        setSubType(subType);
        setAbvPercent(abvPercent);
    }

    public static DrinkCatalogItem create(
            String name,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    ) {
        return new DrinkCatalogItem(name, category, subType, abvPercent);
    }

    public void update(
            String name,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    ) {
        setName(name);
        setCategory(category);
        setSubType(subType);
        setAbvPercent(abvPercent);
    }

    /// SETTERS (DOMAIN LOGIC)
    private void setName(String name) {
        String cleaned = requireText(name, "Drink name cannot be blank");
        this.name = cleaned;
        this.normalizedName = normalize(cleaned);
    }

    private void setCategory(DrinkCategory category) {
        if (category == null) {
            throw new InvalidDrinkCatalogItemException("Drink category cannot be null");
        }
        this.category = category;
    }

    private void setSubType(String subType) {
        String cleaned = cleanOptionalText(subType);
        this.subType = cleaned;
        this.normalizedSubType = cleaned == null ? null : normalize(cleaned);
    }

    private void setAbvPercent(BigDecimal abvPercent) {
        if (abvPercent == null) {
            this.abvPercent = null;
            return;
        }

        BigDecimal normalized = abvPercent.setScale(2, RoundingMode.HALF_UP);

        if (normalized.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidDrinkCatalogItemException("ABV percent cannot be negative");
        }

        if (normalized.compareTo(new BigDecimal("100.00")) > 0) {
            throw new InvalidDrinkCatalogItemException("ABV percent cannot be greater than 100");
        }

        this.abvPercent = normalized;
    }

    /// HELPERS
    private static String requireText(String value, String message) {
        if (value == null) {
            throw new InvalidDrinkCatalogItemException(message);
        }

        String cleaned = value.trim();
        if (cleaned.isEmpty()) {
            throw new InvalidDrinkCatalogItemException(message);
        }

        return cleaned;
    }

    private static String cleanOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
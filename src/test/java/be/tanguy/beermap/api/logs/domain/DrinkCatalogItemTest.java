package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.logs.exception.InvalidDrinkCatalogItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DrinkCatalogItemTest {

    @Test
    void create_shouldCreateItemWithNormalizedFields() {
        DrinkCatalogItem item = DrinkCatalogItem.create(
                "  Duvel  ",
                DrinkCategory.BEER,
                "  Belgian Strong Blonde  ",
                new BigDecimal("8.5")
        );

        assertEquals("Duvel", item.getName());
        assertEquals("duvel", item.getNormalizedName());
        assertEquals(DrinkCategory.BEER, item.getCategory());
        assertEquals("Belgian Strong Blonde", item.getSubType());
        assertEquals("belgian strong blonde", item.getNormalizedSubType());
        assertEquals(new BigDecimal("8.50"), item.getAbvPercent());
    }

    @Test
    void create_shouldAllowNullSubTypeAndAbv() {
        DrinkCatalogItem item = DrinkCatalogItem.create(
                "Guinness",
                DrinkCategory.BEER,
                null,
                null
        );

        assertEquals("Guinness", item.getName());
        assertEquals("guinness", item.getNormalizedName());
        assertNull(item.getSubType());
        assertNull(item.getNormalizedSubType());
        assertNull(item.getAbvPercent());
    }

    @Test
    void create_shouldThrow_whenNameIsBlank() {
        assertThrows(InvalidDrinkCatalogItemException.class, () ->
                DrinkCatalogItem.create(
                        "   ",
                        DrinkCategory.BEER,
                        "Stout",
                        new BigDecimal("4.2")
                )
        );
    }

    @Test
    void create_shouldThrow_whenCategoryIsNull() {
        assertThrows(InvalidDrinkCatalogItemException.class, () ->
                DrinkCatalogItem.create(
                        "Duvel",
                        null,
                        "Blonde",
                        new BigDecimal("8.5")
                )
        );
    }

    @Test
    void create_shouldThrow_whenAbvIsNegative() {
        assertThrows(InvalidDrinkCatalogItemException.class, () ->
                DrinkCatalogItem.create(
                        "Duvel",
                        DrinkCategory.BEER,
                        "Blonde",
                        new BigDecimal("-1.0")
                )
        );
    }

    @Test
    void create_shouldThrow_whenAbvIsGreaterThan100() {
        assertThrows(InvalidDrinkCatalogItemException.class, () ->
                DrinkCatalogItem.create(
                        "Duvel",
                        DrinkCategory.BEER,
                        "Blonde",
                        new BigDecimal("100.01")
                )
        );
    }

    @Test
    void update_shouldUpdateAllFields() {
        DrinkCatalogItem item = DrinkCatalogItem.create(
                "Duvel",
                DrinkCategory.BEER,
                "Blonde",
                new BigDecimal("8.5")
        );

        item.update(
                "  Negroni  ",
                DrinkCategory.COCKTAIL,
                "  Bitter  ",
                new BigDecimal("24")
        );

        assertEquals("Negroni", item.getName());
        assertEquals("negroni", item.getNormalizedName());
        assertEquals(DrinkCategory.COCKTAIL, item.getCategory());
        assertEquals("Bitter", item.getSubType());
        assertEquals("bitter", item.getNormalizedSubType());
        assertEquals(new BigDecimal("24.00"), item.getAbvPercent());
    }

    @Test
    void update_shouldSetOptionalFieldsToNull() {
        DrinkCatalogItem item = DrinkCatalogItem.create(
                "Duvel",
                DrinkCategory.BEER,
                "Blonde",
                new BigDecimal("8.5")
        );

        item.update(
                "Duvel",
                DrinkCategory.BEER,
                "   ",
                null
        );

        assertNull(item.getSubType());
        assertNull(item.getNormalizedSubType());
        assertNull(item.getAbvPercent());
    }
}
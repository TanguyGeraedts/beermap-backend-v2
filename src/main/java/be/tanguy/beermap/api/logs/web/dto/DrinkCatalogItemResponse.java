package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record DrinkCatalogItemResponse(
        UUID id,
        String name,
        String normalizedName,
        DrinkCategory category,
        String subType,
        String normalizedSubType,
        BigDecimal abvPercent
) {
    public static DrinkCatalogItemResponse from(DrinkCatalogItem item) {
        return new DrinkCatalogItemResponse(
                item.getId(),
                item.getName(),
                item.getNormalizedName(),
                item.getCategory(),
                item.getSubType(),
                item.getNormalizedSubType(),
                item.getAbvPercent()
        );
    }
}

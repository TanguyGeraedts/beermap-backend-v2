package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DrinkCatalogItemService {

    DrinkCatalogItem create(
            String name,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    );

    DrinkCatalogItem update(
            UUID id,
            String name,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    );

    DrinkCatalogItem getById(UUID id);

    DrinkCatalogItem getByName(String name);

    List<DrinkCatalogItem> getAll();

    List<DrinkCatalogItem> getByCategory(DrinkCategory category);

    List<DrinkCatalogItem> searchByName(String query);

    List<DrinkCatalogItem> searchByCategoryAndName(DrinkCategory category, String query);

    List<DrinkCatalogItem> searchBySubType(String subType);

    void delete(UUID id);
}
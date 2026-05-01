package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DrinkCatalogItemNotFoundException extends DrinkLogDomainException {
    public DrinkCatalogItemNotFoundException(UUID itemId) {
        super(HttpStatus.NOT_FOUND, "DRINK_CATALOG_ITEM_NOT_FOUND", "Catalog item not found with ID: " + itemId);
    }
    public DrinkCatalogItemNotFoundException(String name) {
        super(HttpStatus.NOT_FOUND, "DRINK_CATALOG_ITEM_NOT_FOUND", "Catalog item not found with name: " + name);
    }
}
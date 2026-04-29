package be.tanguy.beermap.api.logs.exception;

import java.util.UUID;

public class DrinkCatalogItemNotFoundException extends DrinkLogDomainException {
    public DrinkCatalogItemNotFoundException(UUID itemId) {
        super("Catalog item not found with ID: " + itemId);
    }

    public DrinkCatalogItemNotFoundException(String name) {
        super("Catalog item not found with name: " + name);
    }
}
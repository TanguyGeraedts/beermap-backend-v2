package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

public class DrinkCatalogItemAlreadyExistsException extends DrinkLogDomainException {
    public DrinkCatalogItemAlreadyExistsException(String name) {
        super(HttpStatus.CONFLICT, "DRINK_CATALOG_ITEM_ALREADY_EXISTS", "Drink catalog item already exists with " + name);
    }
}
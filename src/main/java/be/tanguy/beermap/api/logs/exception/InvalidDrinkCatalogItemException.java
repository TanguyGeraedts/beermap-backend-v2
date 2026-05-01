package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

public class InvalidDrinkCatalogItemException extends DrinkLogDomainException {
    public InvalidDrinkCatalogItemException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_DRINK_CATALOG_ITEM", message);
    }
}
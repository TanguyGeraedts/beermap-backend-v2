package be.tanguy.beermap.api.logs.exception;

public class DrinkCatalogItemAlreadyExistsException extends RuntimeException {

    public DrinkCatalogItemAlreadyExistsException(String name) {
        super("Drink catalog item already exists with " + name);
    }
}
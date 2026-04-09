package be.tanguy.beermap.api.logs.exception;

public class DrinkCatalogItemAlreadyExistsException extends RuntimeException {

    public DrinkCatalogItemAlreadyExistsException() {
        super("Drink catalog item already exists");
    }

    public DrinkCatalogItemAlreadyExistsException(String message) {
        super(message);
    }
}
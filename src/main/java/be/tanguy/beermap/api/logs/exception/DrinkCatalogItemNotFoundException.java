package be.tanguy.beermap.api.logs.exception;

public class DrinkCatalogItemNotFoundException extends RuntimeException {

    public DrinkCatalogItemNotFoundException() {
        super("Drink catalog item not found");
    }

    public DrinkCatalogItemNotFoundException(String message) {
        super(message);
    }
}
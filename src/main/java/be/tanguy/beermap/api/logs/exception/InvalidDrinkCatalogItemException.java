package be.tanguy.beermap.api.logs.exception;

public class InvalidDrinkCatalogItemException extends RuntimeException {

    public InvalidDrinkCatalogItemException(String message) {
        super(message);
    }
}
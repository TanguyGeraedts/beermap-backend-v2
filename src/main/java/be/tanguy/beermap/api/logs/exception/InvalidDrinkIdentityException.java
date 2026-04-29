package be.tanguy.beermap.api.logs.exception;

public class InvalidDrinkIdentityException extends RuntimeException {
    public InvalidDrinkIdentityException(String message) {
        super(message);
    }
}

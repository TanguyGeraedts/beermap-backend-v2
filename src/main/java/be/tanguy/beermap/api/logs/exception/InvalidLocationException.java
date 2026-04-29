package be.tanguy.beermap.api.logs.exception;

public class InvalidLocationException extends RuntimeException {
    public InvalidLocationException(String message) { super(message); }
}

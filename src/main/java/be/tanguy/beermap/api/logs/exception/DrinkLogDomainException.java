package be.tanguy.beermap.api.logs.exception;

public abstract class DrinkLogDomainException extends RuntimeException {
    protected DrinkLogDomainException(String message) {
        super(message);
    }
}
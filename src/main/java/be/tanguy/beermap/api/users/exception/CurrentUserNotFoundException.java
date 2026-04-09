package be.tanguy.beermap.api.users.exception;

public class CurrentUserNotFoundException extends RuntimeException {

    public CurrentUserNotFoundException() {
        super("Current user not found");
    }
}
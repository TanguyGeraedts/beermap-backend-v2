package be.tanguy.beermap.api.users.exception;

public class ProfileAlreadyExistsException extends RuntimeException {

    public ProfileAlreadyExistsException() {
        super("User profile already exists");
    }
}
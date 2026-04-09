package be.tanguy.beermap.api.users.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException() {
        super("User profile not found");
    }
}
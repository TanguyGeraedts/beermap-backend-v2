package be.tanguy.beermap.api.friends.exception;

public abstract class FriendshipException extends RuntimeException {
    public FriendshipException(String message) {
        super(message);
    }
}
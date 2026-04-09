package be.tanguy.beermap.api.friends.exception;

public class FriendshipAlreadyExistsException extends FriendshipException {
    public FriendshipAlreadyExistsException() {
        super("Friendship already exists");
    }
}
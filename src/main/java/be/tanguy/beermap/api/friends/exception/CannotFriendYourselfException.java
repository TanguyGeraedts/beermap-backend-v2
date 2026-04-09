package be.tanguy.beermap.api.friends.exception;

public class CannotFriendYourselfException extends FriendshipException {
    public CannotFriendYourselfException() {
        super("Users cannot friend themselves");
    }
}
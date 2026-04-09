package be.tanguy.beermap.api.friends.exception;

public class UserNotPartOfFriendshipException extends FriendshipException {
    public UserNotPartOfFriendshipException() {
        super("User is not part of this friendship");
    }
}

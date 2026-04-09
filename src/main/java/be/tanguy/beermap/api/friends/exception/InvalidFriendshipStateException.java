package be.tanguy.beermap.api.friends.exception;

public class InvalidFriendshipStateException extends FriendshipException {
  public InvalidFriendshipStateException(String message) {
    super(message);
  }
}
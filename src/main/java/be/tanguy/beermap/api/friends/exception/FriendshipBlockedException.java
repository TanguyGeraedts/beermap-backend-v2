package be.tanguy.beermap.api.friends.exception;

public class FriendshipBlockedException extends FriendshipException {
  public FriendshipBlockedException() {
    super("Cannot interact with a blocked user");
  }
}

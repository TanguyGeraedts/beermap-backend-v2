package be.tanguy.beermap.api.users.exception;

public class UsernameDiscriminatorAlreadyTakenException extends RuntimeException {

  public UsernameDiscriminatorAlreadyTakenException(String username, String discriminator) {
    super("Username/discriminator already taken: " + username + "#" + discriminator);
  }
}
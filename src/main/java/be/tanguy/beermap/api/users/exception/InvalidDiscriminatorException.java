package be.tanguy.beermap.api.users.exception;

public class InvalidDiscriminatorException extends RuntimeException {

  public InvalidDiscriminatorException(String message) {
    super(message);
  }
}
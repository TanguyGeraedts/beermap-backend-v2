package be.tanguy.beermap.api.friends.exception;

import org.springframework.http.HttpStatus;

public class InvalidFriendshipStateException extends FriendshipDomainException {
    public InvalidFriendshipStateException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_FRIENDSHIP_STATE", message);
    }
}
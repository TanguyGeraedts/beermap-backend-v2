package be.tanguy.beermap.api.friends.exception;

import org.springframework.http.HttpStatus;

public class FriendshipAlreadyExistsException extends FriendshipDomainException {
    public FriendshipAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "FRIENDSHIP_ALREADY_EXISTS", "Friendship already exists");
    }
}
package be.tanguy.beermap.api.friends.exception;

import org.springframework.http.HttpStatus;

public class CannotFriendYourselfException extends FriendshipDomainException {
    public CannotFriendYourselfException() {
        super(HttpStatus.BAD_REQUEST, "CANNOT_FRIEND_YOURSELF", "Users cannot friend themselves");
    }
}
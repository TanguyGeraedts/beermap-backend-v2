package be.tanguy.beermap.api.friends.exception;

import org.springframework.http.HttpStatus;

public class UserNotPartOfFriendshipException extends FriendshipDomainException {
    public UserNotPartOfFriendshipException() {
        super(HttpStatus.FORBIDDEN, "USER_NOT_PART_OF_FRIENDSHIP", "User is not part of this friendship");
    }
}

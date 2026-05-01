package be.tanguy.beermap.api.friends.exception;

import org.springframework.http.HttpStatus;

public class FriendshipBlockedException extends FriendshipDomainException {
    public FriendshipBlockedException() {
        super(HttpStatus.FORBIDDEN, "FRIENDSHIP_BLOCKED", "Cannot interact with a blocked user");
    }
}

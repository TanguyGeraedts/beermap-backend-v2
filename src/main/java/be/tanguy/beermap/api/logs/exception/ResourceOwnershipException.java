package be.tanguy.beermap.api.logs.exception;

import java.util.UUID;

public class ResourceOwnershipException extends RuntimeException {
    public ResourceOwnershipException(String resource, UUID id) {
        super(String.format("User does not own the %s with ID: %s", resource, id));
    }
}

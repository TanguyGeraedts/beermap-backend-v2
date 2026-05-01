package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ResourceOwnershipException extends DrinkLogDomainException {
    public ResourceOwnershipException(String resource, UUID id) {
        super(HttpStatus.FORBIDDEN, "RESOURCE_OWNERSHIP",
                String.format("User does not own the %s with ID: %s", resource, id));
    }
}

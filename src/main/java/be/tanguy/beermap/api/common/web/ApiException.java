package be.tanguy.beermap.api.common.web;

import org.springframework.http.HttpStatus;

/**
 * Base class for domain exceptions that map cleanly to HTTP responses.
 * Extend this in any domain — GlobalExceptionHandler picks them up automatically.
 */
public abstract class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();

    public abstract String getCode();
}

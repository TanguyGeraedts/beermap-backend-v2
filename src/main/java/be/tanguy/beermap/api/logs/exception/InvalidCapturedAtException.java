package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class InvalidCapturedAtException extends DrinkLogDomainException {
    public InvalidCapturedAtException(Instant capturedAt) {
        super(HttpStatus.BAD_REQUEST, "INVALID_CAPTURED_AT", "capturedAt cannot be in the future: " + capturedAt);
    }
}
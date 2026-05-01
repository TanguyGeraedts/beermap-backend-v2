package be.tanguy.beermap.api.logs.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class InvalidCapturedAtException extends DrinkLogDomainException {
    public InvalidCapturedAtException(LocalDateTime capturedAt) {
        super(HttpStatus.BAD_REQUEST, "INVALID_CAPTURED_AT", "capturedAt cannot be in the future: " + capturedAt);
    }
}
package be.tanguy.beermap.api.logs.exception;

import java.time.LocalDateTime;

public class InvalidCapturedAtException extends RuntimeException {
    public InvalidCapturedAtException(LocalDateTime capturedAt) {
        super("capturedAt cannot be in the future: " + capturedAt);
    }
}
package be.tanguy.beermap.api.common.web;

import be.tanguy.beermap.api.friends.exception.CannotFriendYourselfException;
import be.tanguy.beermap.api.friends.exception.FriendshipAlreadyExistsException;
import be.tanguy.beermap.api.friends.exception.FriendshipBlockedException;
import be.tanguy.beermap.api.friends.exception.InvalidFriendshipStateException;
import be.tanguy.beermap.api.friends.exception.UserNotPartOfFriendshipException;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemAlreadyExistsException;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemNotFoundException;
import be.tanguy.beermap.api.logs.exception.InvalidDrinkCatalogItemException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Generic handler for all domain exceptions that implement ApiException.
     * New domains get handled automatically.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ApiError(ex.getCode(), ex.getMessage()));
    }

    /// DRINK CATALOG EXCEPTIONS
    @ExceptionHandler(DrinkCatalogItemNotFoundException.class)
    public ResponseEntity<ApiError> handleDrinkCatalogItemNotFound(DrinkCatalogItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("DRINK_CATALOG_ITEM_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(DrinkCatalogItemAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDrinkCatalogItemAlreadyExists(DrinkCatalogItemAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("DRINK_CATALOG_ITEM_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(InvalidDrinkCatalogItemException.class)
    public ResponseEntity<ApiError> handleInvalidDrinkCatalogItem(InvalidDrinkCatalogItemException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("INVALID_DRINK_CATALOG_ITEM", ex.getMessage()));
    }

    /// FRIENDSHIP EXCEPTIONS
    @ExceptionHandler(CannotFriendYourselfException.class)
    public ResponseEntity<ApiError> handleCannotFriendYourself(CannotFriendYourselfException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("CANNOT_FRIEND_YOURSELF", ex.getMessage()));
    }

    @ExceptionHandler(UserNotPartOfFriendshipException.class)
    public ResponseEntity<ApiError> handleUserNotPartOfFriendship(UserNotPartOfFriendshipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("USER_NOT_PART_OF_FRIENDSHIP", ex.getMessage()));
    }

    @ExceptionHandler(InvalidFriendshipStateException.class)
    public ResponseEntity<ApiError> handleInvalidFriendshipState(InvalidFriendshipStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("INVALID_FRIENDSHIP_STATE", ex.getMessage()));
    }

    @ExceptionHandler(FriendshipAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleFriendshipAlreadyExists(FriendshipAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("FRIENDSHIP_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(FriendshipBlockedException.class)
    public ResponseEntity<ApiError> handleFriendshipBlocked(FriendshipBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("FRIENDSHIP_BLOCKED", ex.getMessage()));
    }

    /// VALIDATION / SECURITY
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("FORBIDDEN", "You are not allowed to perform this action"));
    }

    /// FALLBACK
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }
}
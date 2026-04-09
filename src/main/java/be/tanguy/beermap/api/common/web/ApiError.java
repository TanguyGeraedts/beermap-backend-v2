package be.tanguy.beermap.api.common.web;

public record ApiError(
        String code,
        String message
) {
}
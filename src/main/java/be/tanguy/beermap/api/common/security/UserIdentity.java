package be.tanguy.beermap.api.common.security;

public record UserIdentity(
        String issuer, String subject
) {}

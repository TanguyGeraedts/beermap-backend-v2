package be.tanguy.beermap.api.users.domain;

public enum AccountType {
    NORMAL,
    PREMIUM,
    ADMIN;

    public boolean hasPremiumAccess() {
        return this == PREMIUM || this == ADMIN;
    }
}

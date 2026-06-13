package be.tanguy.beermap.api.map.repository;

import be.tanguy.beermap.api.users.domain.AccountType;

import java.time.Instant;
import java.util.UUID;

public interface MapPinProjection {
    UUID        getLogId();
    Double      getLatitude();
    Double      getLongitude();
    String      getCategory();
    String      getDisplayName();
    String      getSubType();
    Integer     getRating();
    String      getPhotoUrl();
    Instant     getConsumedAt();

    // UserSummary fields
    UUID        getUserId();
    String      getUsername();
    String      getDiscriminator();
    String      getAvatarUrl();
    AccountType getAccountType();
}
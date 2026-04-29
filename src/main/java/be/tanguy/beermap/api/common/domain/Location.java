package be.tanguy.beermap.api.common.domain;

import be.tanguy.beermap.api.logs.exception.InvalidLocationException;

public record Location(Double latitude, Double longitude) {

    public Location {
        if (latitude != null || longitude != null) {
            if (latitude == null || longitude == null) {
                throw new InvalidLocationException("Latitude and longitude must both be provided together");
            }
            if (latitude < -90 || latitude > 90) {
                throw new InvalidLocationException("Latitude must be between -90 and 90");
            }
            if (longitude < -180 || longitude > 180) {
                throw new InvalidLocationException("Longitude must be between -180 and 180");
            }
        }
    }

    public static Location of(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) return null;
        return new Location(latitude, longitude);
    }
}
package be.tanguy.beermap.api.map.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.map.domain.MapMode;
import be.tanguy.beermap.api.map.web.dto.MapPinResponse;

import java.time.Instant;
import java.util.List;

public interface MapService {

    List<MapPinResponse> getPins(
            UserIdentity identity,
            MapMode mode,
            List<String> categories,
            Instant from,
            Instant to,
            double minLat,
            double minLon,
            double maxLat,
            double maxLon,
            int limit
    );
}
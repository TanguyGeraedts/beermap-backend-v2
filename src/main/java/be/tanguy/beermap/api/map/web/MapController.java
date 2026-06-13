package be.tanguy.beermap.api.map.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.map.domain.MapMode;
import be.tanguy.beermap.api.map.service.MapService;
import be.tanguy.beermap.api.map.web.dto.MapPinResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.Map.BASE)
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping(ApiPaths.Map.PINS)
    public List<MapPinResponse> getPins(
            @AuthenticationPrincipal UserIdentity identity,
            @RequestParam(defaultValue = "MINE") MapMode mode,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam double minLat,
            @RequestParam double minLon,
            @RequestParam double maxLat,
            @RequestParam double maxLon,
            @RequestParam(defaultValue = "500") int limit
    ) {
        return mapService.getPins(identity, mode, categories, from, to, minLat, minLon, maxLat, maxLon, limit);
    }
}
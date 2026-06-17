package be.tanguy.beermap.api.logs.web.dto;


import java.time.Instant;

public record CreateDraftLogRequest(
        String photoUrl,
        Instant capturedAt,
        Double latitude,
        Double longitude
) {
}

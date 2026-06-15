package be.tanguy.beermap.api.logs.web.dto;

import java.time.LocalDateTime;

public record CreateDraftLogRequest(
        String photoUrl,
        LocalDateTime capturedAt,
        Double latitude,
        Double longitude
) {
}

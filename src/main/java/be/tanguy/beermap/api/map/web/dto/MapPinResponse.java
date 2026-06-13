package be.tanguy.beermap.api.map.web.dto;

import be.tanguy.beermap.api.map.repository.MapPinProjection;
import be.tanguy.beermap.api.users.web.dto.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record MapPinResponse(
        UUID logId,
        double  latitude,
        double  longitude,
        String  category,
        String  displayName,
        String  subType,
        UserSummary userSummary,

        // optional extras for popups
        Integer rating,
        String  photoUrl,
        Instant consumedAt
) {
    public static MapPinResponse from(MapPinProjection p) {
        return new MapPinResponse(
                p.getLogId(),
                p.getLatitude(),
                p.getLongitude(),
                p.getCategory(),
                p.getDisplayName(),
                p.getSubType(),
                new UserSummary(
                        p.getUserId(),
                        p.getUsername(),
                        p.getDiscriminator(),
                        p.getAvatarUrl(),
                        p.getAccountType()
                ),
                p.getRating(),
                p.getPhotoUrl(),
                p.getConsumedAt()
        );
    }
}
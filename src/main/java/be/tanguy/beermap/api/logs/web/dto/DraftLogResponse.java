package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record DraftLogResponse(
        UUID id,
        UUID userId,
        LocalDateTime capturedAt,
        String photoUrl,
        DraftStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static DraftLogResponse fromEntity(DraftLog draftLog) {
        return new DraftLogResponse(
                draftLog.getId(),
                draftLog.getUserId(),
                draftLog.getCapturedAt(),
                draftLog.getPhotoUrl(),
                draftLog.getStatus(),
                draftLog.getCreatedAt(),
                draftLog.getUpdatedAt()
        );
    }
}
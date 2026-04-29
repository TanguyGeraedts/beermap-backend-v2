package be.tanguy.beermap.api.logs.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import be.tanguy.beermap.api.logs.exception.InvalidCapturedAtException;
import be.tanguy.beermap.api.logs.exception.InvalidDraftStateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "draft_logs",
        indexes = {
                @Index(name = "idx_draft_user_time", columnList = "user_id, captured_at")
        }
)
@Access(AccessType.FIELD)
@Getter
public class DraftLog extends AbstractBaseObject {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;

    @Column(name = "photo_url", length = 2048)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DraftStatus status;

    public static DraftLog createDraft(UUID userId, String photoUrl, LocalDateTime capturedAt) {
        DraftLog draft = new DraftLog();

        draft.userId = userId;
        draft.photoUrl = photoUrl;

        draft.validateCapturedAt(capturedAt);

        LocalDateTime now = LocalDateTime.now();
        draft.capturedAt = capturedAt != null ? capturedAt : now;

        draft.status = DraftStatus.ACTIVE;

        return draft;
    }

    public void markConsumed() {
        if (this.status != DraftStatus.ACTIVE) {
            throw new InvalidDraftStateException(this.status, DraftStatus.ACTIVE);
        }
        this.status = DraftStatus.CONSUMED;
    }

    public void markExpired() {
        if (this.status != DraftStatus.ACTIVE) {
            throw new InvalidDraftStateException(this.status, DraftStatus.ACTIVE);
        }
        this.status = DraftStatus.EXPIRED;
    }

    public void updatePhoto(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isActive() {
        return this.status == DraftStatus.ACTIVE;
    }

    public boolean isConsumed() {
        return this.status == DraftStatus.CONSUMED;
    }

    private void validateCapturedAt(LocalDateTime capturedAt) {
        LocalDateTime now = LocalDateTime.now();

        if (capturedAt != null && capturedAt.isAfter(now)) {
            throw new InvalidCapturedAtException(capturedAt);
        }
    }
}
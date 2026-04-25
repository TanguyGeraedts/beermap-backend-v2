package be.tanguy.beermap.api.feed.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "feed_items",
        indexes = {
                @Index(name = "idx_feed_item_user", columnList = "user_id"),
                @Index(name = "idx_feed_item_type", columnList = "type"),
                @Index(name = "idx_feed_item_target", columnList = "target_type, target_id"),
                @Index(name = "idx_feed_item_visibility", columnList = "visibility"),
                @Index(name = "idx_feed_item_created_at", columnList = "created_at")
        }
)
@Getter
public class FeedItem extends AbstractBaseObject {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FeedItemType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private FeedTargetType targetType;

    /**
     * ID of the thing this feed item points to.
     * For example:
     * - DRINK_LOG -> drink_logs.id
     * - BADGE -> badges/user_badges.id
     * - FRIENDSHIP -> friendships.id
     * - SESSION -> sessions.id
     */
    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private FeedVisibility visibility;

    @Column(name = "metadata_json")
    private String metadataJson;

    private FeedItem(
            UUID userId,
            FeedItemType type,
            FeedTargetType targetType,
            UUID targetId,
            FeedVisibility visibility,
            String metadataJson
    ) {
        validate(userId, type, targetType, targetId, visibility);

        this.userId = userId;
        this.type = type;
        this.targetType = targetType;
        this.targetId = targetId;
        this.visibility = visibility;
        this.metadataJson = metadataJson;
    }

    public static FeedItem create(
            UUID userId,
            FeedItemType type,
            FeedTargetType targetType,
            UUID targetId,
            FeedVisibility visibility
    ) {
        return new FeedItem(
                userId,
                type,
                targetType,
                targetId,
                visibility,
                null
        );
    }

    public static FeedItem create(
            UUID userId,
            FeedItemType type,
            FeedTargetType targetType,
            UUID targetId,
            FeedVisibility visibility,
            String metadataJson
    ) {
        return new FeedItem(
                userId,
                type,
                targetType,
                targetId,
                visibility,
                metadataJson
        );
    }

    /// STATE TRANSITIONS

    public void changeVisibility(FeedVisibility visibility) {
        if (visibility == null) {
            throw new IllegalArgumentException("Feed visibility cannot be null");
        }

        this.visibility = visibility;
    }

    public void updateMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    /// HELPERS

    public boolean isPublic() {
        return visibility == FeedVisibility.PUBLIC;
    }

    public boolean isFriendsOnly() {
        return visibility == FeedVisibility.FRIENDS;
    }

    public boolean isPrivate() {
        return visibility == FeedVisibility.PRIVATE;
    }

    public boolean wasCreatedBy(UUID userId) {
        if (userId == null) {
            return false;
        }

        return this.userId.equals(userId);
    }

    public boolean pointsTo(FeedTargetType targetType, UUID targetId) {
        if (targetType == null || targetId == null) {
            return false;
        }

        return this.targetType == targetType && this.targetId.equals(targetId);
    }

    private static void validate(
            UUID userId,
            FeedItemType type,
            FeedTargetType targetType,
            UUID targetId,
            FeedVisibility visibility
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("Feed item type cannot be null");
        }

        if (targetType == null) {
            throw new IllegalArgumentException("Feed target type cannot be null");
        }

        if (targetId == null) {
            throw new IllegalArgumentException("Feed target ID cannot be null");
        }

        if (visibility == null) {
            throw new IllegalArgumentException("Feed visibility cannot be null");
        }
    }
}
package be.tanguy.beermap.api.feed.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        name = "feed_item_cheers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_feed_item_cheer_user",
                        columnNames = {"feed_item_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_feed_item_cheers_feed_item", columnList = "feed_item_id"),
                @Index(name = "idx_feed_item_cheers_user", columnList = "user_id"),
                @Index(name = "idx_feed_item_cheers_created_at", columnList = "created_at")
        }
)
public class FeedItemCheers extends AbstractBaseObject {

    @Column(name = "feed_item_id", nullable = false)
    private UUID feedItemId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private FeedItemCheers(UUID feedItemId, UUID userId) {
        if (feedItemId == null) {
            throw new IllegalArgumentException("Feed item ID cannot be null");
        }

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        this.feedItemId = feedItemId;
        this.userId = userId;
    }

    public static FeedItemCheers create(UUID feedItemId, UUID userId) {
        return new FeedItemCheers(feedItemId, userId);
    }
}

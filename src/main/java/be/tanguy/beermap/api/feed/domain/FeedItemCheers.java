package be.tanguy.beermap.api.feed.domain;

import be.tanguy.beermap.api.common.persistence.AbstractBaseObject;
import be.tanguy.beermap.api.feed.exception.InvalidFeedItemException;
import jakarta.persistence.*;
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
        if (feedItemId == null) throw new InvalidFeedItemException("feedItemId is required");
        if (userId == null) throw new InvalidFeedItemException("userId is required");

        this.feedItemId = feedItemId;
        this.userId = userId;
    }

    public static FeedItemCheers create(UUID feedItemId, UUID userId) {
        return new FeedItemCheers(feedItemId, userId);
    }
}
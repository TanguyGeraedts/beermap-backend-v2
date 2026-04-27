package be.tanguy.beermap.api.feed.web.dto;

import be.tanguy.beermap.api.feed.domain.FeedItem;
import be.tanguy.beermap.api.feed.domain.FeedItemType;
import be.tanguy.beermap.api.feed.domain.FeedTargetType;
import be.tanguy.beermap.api.feed.domain.FeedVisibility;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FeedItemMapperTest {

    @Test
    void maps_feed_item_with_details() {
        UUID userId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        FeedItem feedItem = FeedItem.create(
                userId,
                FeedItemType.DRINK_LOG_CREATED,
                FeedTargetType.DRINK_LOG,
                targetId,
                FeedVisibility.PUBLIC,
                "{\"example\":true}"
        );

        FeedItemDetailsResponse details = new FeedItemDetailsResponse(
                new FeedActorResponse(userId, "Tanguy", "1234", "https://example.com/avatar.png"),
                new FeedDrinkLogDetailsResponse(
                        targetId,
                        "Duvel",
                        "BEER",
                        "STRONG_ALE",
                        2,
                        8,
                        "https://example.com/photo.png",
                        Instant.parse("2026-04-27T10:15:30Z")
                )
        );

        FeedItemResponse response = FeedItemMapper.toResponse(feedItem, details);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.targetId()).isEqualTo(targetId);
        assertThat(response.type()).isEqualTo(FeedItemType.DRINK_LOG_CREATED);
        assertThat(response.targetType()).isEqualTo(FeedTargetType.DRINK_LOG);
        assertThat(response.visibility()).isEqualTo(FeedVisibility.PUBLIC);
        assertThat(response.metadataJson()).isEqualTo("{\"example\":true}");
        assertThat(response.details()).isEqualTo(details);
    }
}

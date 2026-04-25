package be.tanguy.beermap.api.feed.repository;

import be.tanguy.beermap.api.feed.domain.FeedItem;
import be.tanguy.beermap.api.feed.domain.FeedVisibility;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface FeedItemRepository extends JpaRepository<FeedItem, UUID> {

    List<FeedItem> findByCreatedAtBeforeAndVisibilityInOrderByCreatedAtDesc(
            Instant cursor,
            Collection<FeedVisibility> visibilities,
            Pageable pageable
    );

    List<FeedItem> findByVisibilityInOrderByCreatedAtDesc(
            Collection<FeedVisibility> visibilities,
            Pageable pageable
    );
}

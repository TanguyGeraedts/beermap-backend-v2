package be.tanguy.beermap.api.feed.repository;

import be.tanguy.beermap.api.feed.domain.FeedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedItemRepository extends JpaRepository<FeedItem, UUID> {
}

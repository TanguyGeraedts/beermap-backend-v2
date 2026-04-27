package be.tanguy.beermap.api.feed.repository;

import be.tanguy.beermap.api.feed.domain.FeedItemCheers;
import be.tanguy.beermap.api.feed.repository.projection.FeedItemCheersCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface FeedItemCheersRepository extends JpaRepository<FeedItemCheers, UUID> {

    boolean existsByFeedItemIdAndUserId(UUID feedItemId, UUID userId);

    void deleteByFeedItemIdAndUserId(UUID feedItemId, UUID userId);

    long countByFeedItemId(UUID feedItemId);

    @Query("""
            SELECT c.feedItemId AS feedItemId, COUNT(c) AS cheersCount
            FROM FeedItemCheers c
            WHERE c.feedItemId IN :feedItemIds
            GROUP BY c.feedItemId
            """)
    List<FeedItemCheersCountProjection> countCheersByFeedItemIds(Collection<UUID> feedItemIds);

    @Query("""
            SELECT c.feedItemId
            FROM FeedItemCheers c
            WHERE c.feedItemId IN :feedItemIds
              AND c.userId = :userId
            """)
    List<UUID> findCheeredFeedItemIdsByUserId(Collection<UUID> feedItemIds, UUID userId);
}

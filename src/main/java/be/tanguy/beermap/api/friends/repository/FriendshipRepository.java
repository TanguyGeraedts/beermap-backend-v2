package be.tanguy.beermap.api.friends.repository;

import be.tanguy.beermap.api.friends.domain.Friendship;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    Optional<Friendship> findByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId);

    Optional<Friendship> findByRequesterIdAndAddresseeIdOrRequesterIdAndAddresseeId(
            UUID requesterId1,
            UUID addresseeId1,
            UUID requesterId2,
            UUID addresseeId2
    );

    List<Friendship> findAllByRequesterIdAndStatus(UUID requesterId, FriendshipStatus status);

    List<Friendship> findAllByAddresseeIdAndStatus(UUID addresseeId, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :userId OR f.addresseeId = :userId) AND f.status = :status")
    List<Friendship> findAllByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") FriendshipStatus status);
}
package be.tanguy.beermap.api.logs.repository;

import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;
import be.tanguy.beermap.api.users.domain.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DraftLogRepository extends JpaRepository<DraftLog, UUID> {

    @Query("""
    SELECT d FROM DraftLog d
    JOIN UserProfile u ON u.id = d.userId
    WHERE d.status = :status
    AND u.accountType = :accountType
    AND d.capturedAt < :cutoff
""")
    List<DraftLog> findExpiredDrafts(
            @Param("status") DraftStatus status,
            @Param("accountType") AccountType accountType,
            @Param("cutoff") LocalDateTime cutoff
    );

    List<DraftLog> findAllByUserIdAndStatusOrderByCapturedAtDesc(UUID userId, DraftStatus status);

    List<DraftLog> findAllByIdInAndUserId(List<UUID> ids, UUID userId);

    List<DraftLog> findAllByUserIdAndStatus(UUID userId, DraftStatus status);

    long countByUserIdAndStatus(UUID userId, DraftStatus status);
}

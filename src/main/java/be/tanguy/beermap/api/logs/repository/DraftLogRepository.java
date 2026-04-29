package be.tanguy.beermap.api.logs.repository;

import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DraftLogRepository extends JpaRepository<DraftLog, UUID> {

    List<DraftLog> findAllByUserIdAndStatusOrderByCapturedAtDesc(UUID userId, DraftStatus status);
}

package be.tanguy.beermap.api.logs.repository;

import be.tanguy.beermap.api.logs.domain.DraftLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DraftLogRepository extends JpaRepository<DraftLog, UUID> {
}

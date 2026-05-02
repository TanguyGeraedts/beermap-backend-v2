package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;
import be.tanguy.beermap.api.logs.repository.DraftLogRepository;
import be.tanguy.beermap.api.media.service.ImageStorageService;
import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.PlanLimits;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DraftRetentionServiceImpl implements DraftRetentionService {

    private final DraftLogRepository draftLogRepository;
    private final ImageStorageService imageStorageService;

    @Override
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredDrafts() {
        log.info("Starting scheduled draft retention cleanup...");

        int totalExpired = 0;
        for (AccountType type : AccountType.values()) {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(PlanLimits.maxDraftRetentionDays(type));
            totalExpired += expireDraftsForAccountType(type, cutoff);
        }

        log.info("Draft retention cleanup complete. Expired {} drafts.", totalExpired);
    }

    private int expireDraftsForAccountType(AccountType type, LocalDateTime cutoff) {
        List<DraftLog> toExpire = draftLogRepository.findExpiredDrafts(DraftStatus.ACTIVE, type, cutoff);
        toExpire.forEach(this::processExpiration);
        return toExpire.size();
    }

    private void processExpiration(DraftLog draft) {
        try {
            draft.markExpired();
            if (draft.getPhotoUrl() != null && !draft.getPhotoUrl().isBlank()) {
                imageStorageService.deleteImage(draft.getPhotoUrl());
                draft.updatePhoto(null);
            }
        } catch (Exception e) {
            log.error("Failed to clean up draft {}: {}", draft.getId(), e.getMessage());
        }
    }
}
package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;
import be.tanguy.beermap.api.logs.exception.DraftLimitExceededException;
import be.tanguy.beermap.api.logs.repository.DraftLogRepository;
import be.tanguy.beermap.api.logs.web.dto.CreateDraftLogRequest;
import be.tanguy.beermap.api.media.service.ImageStorageService;
import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.PlanLimits;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DraftLogServiceImpl implements DraftLogService {

    /// Services
    private final UserProfileService userProfileService;
    private final ImageStorageService imageStorageService;

    /// Repositories
    private final DraftLogRepository draftLogRepository;

    @Override
    @Transactional
    public DraftLog createDraftLog(UserIdentity userIdentity, CreateDraftLogRequest request) {
        UserProfile currentUser = userProfileService.getCurrentUser(userIdentity);

        long activeCount = draftLogRepository.countByUserIdAndStatus(currentUser.getId(), DraftStatus.ACTIVE);
        PlanLimits.verifyActiveDraftLimit(currentUser.getAccountType(), activeCount);

        DraftLog draftLog = DraftLog.createDraft(
                currentUser.getId(),
                request.photoUrl(),
                request.capturedAt()
        );

        return draftLogRepository.save(draftLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DraftLog> getActiveDrafts(UserIdentity userIdentity) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);
        return draftLogRepository.findAllByUserIdAndStatusOrderByCapturedAtDesc(currentUserId, DraftStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void deleteDrafts(UserIdentity userIdentity, List<UUID> draftIds) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        List<DraftLog> drafts = draftLogRepository.findAllByIdInAndUserId(draftIds, currentUserId);
        drafts.forEach(this::processExpiration);
    }

    @Override
    @Transactional
    public void deleteAllDrafts(UserIdentity userIdentity) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        List<DraftLog> drafts = draftLogRepository.findAllByUserIdAndStatus(currentUserId, DraftStatus.ACTIVE);
        drafts.forEach(this::processExpiration);
    }

    private void processExpiration(DraftLog draft) {
        draft.markExpired();
        if (draft.getPhotoUrl() != null && !draft.getPhotoUrl().isBlank()) {
            imageStorageService.deleteImage(draft.getPhotoUrl());
            draft.updatePhoto(null);
        }
    }
}

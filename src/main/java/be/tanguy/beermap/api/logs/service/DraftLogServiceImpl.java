package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;
import be.tanguy.beermap.api.logs.repository.DraftLogRepository;
import be.tanguy.beermap.api.logs.web.dto.CreateDraftLogRequest;
import be.tanguy.beermap.api.users.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DraftLogServiceImpl implements DraftLogService {

    /// Auth
    private final UserProfileService userProfileService;

    /// Repositories
    private final DraftLogRepository draftLogRepository;

    @Override
    @Transactional
    public DraftLog createDraftLog(UserIdentity userIdentity, CreateDraftLogRequest request) {
        UUID currentUserId = userProfileService.getCurrentUserId(userIdentity);

        DraftLog draftLog = DraftLog.createDraft(
                currentUserId,
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
}

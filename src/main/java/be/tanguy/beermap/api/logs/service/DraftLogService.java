package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.web.dto.CreateDraftLogRequest;

import java.util.List;
import java.util.UUID;

public interface DraftLogService {

    DraftLog createDraftLog(UserIdentity userIdentity, CreateDraftLogRequest request);

    List<DraftLog> getActiveDrafts(UserIdentity userIdentity);

    void deleteDrafts(UserIdentity userIdentity, List<UUID> draftIds);

    void deleteAllDrafts(UserIdentity userIdentity);
}

package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.web.dto.CreateDraftLogRequest;

import java.util.List;

public interface DraftLogService {

    DraftLog createDraftLog(UserIdentity userIdentity, CreateDraftLogRequest request);

    List<DraftLog> getActiveDrafts(UserIdentity userIdentity);
}

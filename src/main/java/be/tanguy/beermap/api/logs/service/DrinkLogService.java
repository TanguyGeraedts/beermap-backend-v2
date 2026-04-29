package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.logs.domain.DrinkLog;
import be.tanguy.beermap.api.logs.web.dto.LogDrinkRequest;
import be.tanguy.beermap.api.logs.web.dto.UpdateDrinkLogRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DrinkLogService {

    DrinkLog logDrink(UserIdentity identity, LogDrinkRequest request);

    DrinkLog logFromDraft(UserIdentity identity, UUID draftId, LogDrinkRequest request);

    Page<DrinkLog> getTimeline(UserIdentity identity, Pageable pageable);

    DrinkLog getLog(UserIdentity identity, UUID logId);

    DrinkLog updateLog(UserIdentity identity, UUID logId, UpdateDrinkLogRequest request);

    void deleteLog(UserIdentity identity, UUID logId);
}
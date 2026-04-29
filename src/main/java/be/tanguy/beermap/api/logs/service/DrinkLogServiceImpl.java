package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.domain.Location;
import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.logs.domain.*;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;
import be.tanguy.beermap.api.logs.exception.DraftNotFoundException;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemNotFoundException;
import be.tanguy.beermap.api.logs.exception.DrinkLogNotFoundException;
import be.tanguy.beermap.api.logs.repository.DraftLogRepository;
import be.tanguy.beermap.api.logs.repository.DrinkCatalogItemRepository;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.logs.web.dto.LogDrinkRequest;
import be.tanguy.beermap.api.logs.web.dto.UpdateDrinkLogRequest;
import be.tanguy.beermap.api.users.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DrinkLogServiceImpl implements DrinkLogService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserProfileService userProfileService;
    private final DrinkLogRepository drinkLogRepository;
    private final DrinkCatalogItemRepository drinkCatalogItemRepository;
    private final DraftLogRepository draftLogRepository;

    @Override
    public DrinkLog logDrink(UserIdentity identity, LogDrinkRequest request) {
        UUID userId = userProfileService.getCurrentUserId(identity);
        return processLogCreation(userId, request);
    }

    @Override
    public DrinkLog logFromDraft(UserIdentity identity, UUID draftId, LogDrinkRequest request) {
        UUID userId = userProfileService.getCurrentUserId(identity);

        DraftLog draft = draftLogRepository.findById(draftId)
                .orElseThrow(() -> new DraftNotFoundException(draftId));

        draft.verifyOwnership(userId);

        if (!draft.isActive()) {
            throw new IllegalStateException("Draft is already consumed or expired");
        }

        draft.consume();
        draftLogRepository.save(draft);

        return processLogCreation(userId, request);
    }

    @Override
    public DrinkLog updateLog(UserIdentity identity, UUID logId, UpdateDrinkLogRequest request) {
        UUID userId = userProfileService.getCurrentUserId(identity);
        DrinkLog drinkLog = findOwnedLog(userId, logId);

        drinkLog.update(
                request.consumedAt(), request.quantity(), request.volumeMl(),
                request.abvPercent(), request.category(), request.subType(),
                request.rating(), request.description(), request.photoUrl(),
                request.latitude(), request.longitude()
        );

        return drinkLogRepository.save(drinkLog);
    }

    @Override
    public void deleteLog(UserIdentity identity, UUID logId) {
        UUID userId = userProfileService.getCurrentUserId(identity);
        DrinkLog drinkLog = findOwnedLog(userId, logId);
        drinkLogRepository.delete(drinkLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DrinkLog> getTimeline(UserIdentity identity, Pageable pageable) {
        UUID userId = userProfileService.getCurrentUserId(identity);
        return drinkLogRepository.findByUserIdOrderByConsumptionConsumedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkLog getLog(UserIdentity identity, UUID logId) {
        UUID userId = userProfileService.getCurrentUserId(identity);
        return findOwnedLog(userId, logId);
    }

    private DrinkLog processLogCreation(UUID userId, LogDrinkRequest request) {
        Instant consumedAt = request.consumedAt() != null ? request.consumedAt() : Instant.now();

        DrinkIdentity identity = (request.catalogItemId() != null)
                ? buildCatalogIdentity(request.catalogItemId())
                : buildCustomIdentity(request);

        ConsumptionDetails cons = ConsumptionDetails.of(
                consumedAt,
                request.quantity() != null ? request.quantity() : 1,
                request.volumeMl(),
                request.rating(),
                request.description(),
                request.photoUrl()
        );

        Location location = Location.of(request.latitude(), request.longitude());

        DrinkLog saved = drinkLogRepository.save(DrinkLog.create(userId, identity, cons, location));
        eventPublisher.publishEvent(DrinkLogCreatedEvent.create(userId, saved.getId()));

        return saved;
    }

    private DrinkIdentity buildCatalogIdentity(UUID catalogItemId) {
        DrinkCatalogItem item = drinkCatalogItemRepository.findById(catalogItemId)
                .orElseThrow(() -> new DrinkCatalogItemNotFoundException(catalogItemId));

        return DrinkIdentity.forCatalog(
                item.getId(), item.getName(), item.getCategory(),
                item.getSubType(), item.getAbvPercent()
        );
    }

    private DrinkIdentity buildCustomIdentity(LogDrinkRequest request) {
        return DrinkIdentity.forCustom(
                request.customDrinkName(),
                request.category(),
                request.subType(),
                request.abvPercent()
        );
    }

    private DrinkLog findOwnedLog(UUID userId, UUID logId) {
        DrinkLog log = drinkLogRepository.findById(logId)
                .orElseThrow(() -> new DrinkLogNotFoundException(logId));

        log.verifyOwnership(userId);
        return log;
    }
}
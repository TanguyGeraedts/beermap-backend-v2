package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.domain.Location;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent;
import be.tanguy.beermap.api.logs.domain.*;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemNotFoundException;
import be.tanguy.beermap.api.logs.repository.DrinkCatalogItemRepository;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.logs.service.command.LogDrinkCommand;
import be.tanguy.beermap.api.logs.service.command.UpdateDrinkLogCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DrinkLogServiceImpl implements DrinkLogService {

    /// Events
    private final ApplicationEventPublisher eventPublisher;

    /// Repositories
    private final DrinkLogRepository drinkLogRepository;
    private final DrinkCatalogItemRepository drinkCatalogItemRepository;

    @Override
    public DrinkLog logDrink(LogDrinkCommand cmd) {
        requireNonNull(cmd.userId(), "userId");
        validateLocation(cmd.latitude(), cmd.longitude());
        validateVolumeMl(cmd.volumeMl());

        Instant consumedAt = cmd.consumedAt() != null ? cmd.consumedAt() : Instant.now();

        DrinkIdentity identity  = cmd.isCatalog() ? buildCatalogIdentity(cmd) : buildCustomIdentity(cmd);
        ConsumptionDetails cons = ConsumptionDetails.of(
                consumedAt,
                cmd.quantity() != null ? cmd.quantity() : 1,
                cmd.volumeMl(),
                cmd.rating(),
                cmd.description(),
                cmd.photoUrl()
        );
        Location location = toLocation(cmd.latitude(), cmd.longitude());

        DrinkLog saved = drinkLogRepository.save(DrinkLog.create(cmd.userId(), identity, cons, location));

        eventPublisher.publishEvent(DrinkLogCreatedEvent.create(cmd.userId(), saved.getId()));

        log.info("logDrink created: logId={}, userId={}, type={}, category={}, subType={}, volumeMl={}",
                saved.getId(), cmd.userId(), cmd.isCatalog() ? "catalog" : "custom",
                saved.getIdentity().getCategory(), saved.getIdentity().getSubType(),
                saved.getConsumption().getVolumeMl());

        return saved;
    }

    @Override
    public DrinkLog updateLog(UpdateDrinkLogCommand cmd) {
        requireNonNull(cmd.userId(), "userId");
        requireNonNull(cmd.logId(),  "logId");
        validateLocation(cmd.latitude(), cmd.longitude());

        DrinkLog drinkLog = findOwnedLog(cmd.userId(), cmd.logId());

        drinkLog.update(
                cmd.consumedAt(),
                cmd.quantity(),
                cmd.volumeMl(),
                cmd.abvPercent(),
                cmd.category(),
                cmd.subType(),
                cmd.rating(),
                cmd.description(),
                cmd.photoUrl(),
                cmd.latitude(),
                cmd.longitude()
        );

        DrinkLog saved = drinkLogRepository.save(drinkLog);

        log.info("updateLog updated: logId={}, userId={}, category={}, subType={}, volumeMl={}",
                saved.getId(), cmd.userId(),
                saved.getIdentity().getCategory(), saved.getIdentity().getSubType(),
                saved.getConsumption().getVolumeMl());

        return saved;
    }

    @Override
    public void deleteLog(UUID userId, UUID logId) {
        requireNonNull(userId, "userId");
        requireNonNull(logId,  "logId");

        DrinkLog drinkLog = findOwnedLog(userId, logId);
        drinkLogRepository.delete(drinkLog);

        log.info("deleteLog deleted: logId={}, userId={}", logId, userId);
    }

    // -------------------------------------------------------------------------
    // Read operations
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<DrinkLog> getTimeline(UUID userId, Pageable pageable) {
        requireNonNull(userId, "userId");
        log.debug("getTimeline: userId={}, pageable={}", userId, pageable);
        Page<DrinkLog> page = drinkLogRepository.findByUserIdOrderByConsumptionConsumedAtDesc(userId, pageable);
        log.debug("getTimeline: userId={}, returned={}, total={}", userId, page.getNumberOfElements(), page.getTotalElements());
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkLog getLog(UUID userId, UUID logId) {
        requireNonNull(userId, "userId");
        requireNonNull(logId,  "logId");
        return findOwnedLog(userId, logId);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private DrinkIdentity buildCatalogIdentity(LogDrinkCommand cmd) {
        requireNonNull(cmd.catalogItemId(), "catalogItemId");

        DrinkCatalogItem item = drinkCatalogItemRepository.findById(cmd.catalogItemId())
                .orElseThrow(() -> new DrinkCatalogItemNotFoundException(
                        "Catalog item not found: " + cmd.catalogItemId()));

        return DrinkIdentity.forCatalog(
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getSubType(),
                item.getAbvPercent()
        );
    }

    private DrinkIdentity buildCustomIdentity(LogDrinkCommand cmd) {
        requireNonNull(cmd.category(), "category");
        if (cmd.customDrinkName() == null || cmd.customDrinkName().isBlank()) {
            throw new IllegalArgumentException("customDrinkName must not be blank");
        }
        return DrinkIdentity.forCustom(
                cmd.customDrinkName(),
                cmd.category(),
                cmd.subType(),
                cmd.abvPercent()
        );
    }

    private DrinkLog findOwnedLog(UUID userId, UUID logId) {
        DrinkLog log = drinkLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Drink log not found: " + logId));

        if (!log.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not own this drink log");
        }
        return log;
    }

    private static Location toLocation(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) return null;
        return new Location(latitude, longitude);
    }

    private static void requireNonNull(Object value, String field) {
        if (value == null) throw new IllegalArgumentException(field + " must not be null");
    }

    private static void validateLocation(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) return;
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude must both be provided together");
        }
        if (latitude < -90  || latitude > 90)   throw new IllegalArgumentException("Latitude must be between -90 and 90");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Longitude must be between -180 and 180");
    }

    private static void validateVolumeMl(Integer volumeMl) {
        if (volumeMl != null && volumeMl <= 0) throw new IllegalArgumentException("volumeMl must be positive");
    }
}
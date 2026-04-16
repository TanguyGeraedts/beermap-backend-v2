package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DrinkLog;
import be.tanguy.beermap.api.logs.service.command.LogDrinkCommand;
import be.tanguy.beermap.api.logs.service.command.UpdateDrinkLogCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DrinkLogService {

    /**
     * Logs consumption of a drink — either from the catalog or a custom entry.
     * The command must contain exactly one of {@code catalogItemId} or {@code customDrinkName}.
     */
    DrinkLog logDrink(LogDrinkCommand command);

    /**
     * Retrieves a paginated timeline of drink logs for a user,
     * ordered by consumption time descending.
     */
    Page<DrinkLog> getTimeline(UUID userId, Pageable pageable);

    /**
     * Retrieves a single drink log entry owned by the given user.
     */
    DrinkLog getLog(UUID userId, UUID logId);

    /**
     * Partially updates an existing drink log entry.
     * Only non-null fields in the command are applied.
     */
    DrinkLog updateLog(UpdateDrinkLogCommand command);

    /**
     * Deletes a drink log entry owned by the given user.
     */
    void deleteLog(UUID userId, UUID logId);
}
package be.tanguy.beermap.api.logs.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.logs.service.DrinkLogService;
import be.tanguy.beermap.api.logs.web.dto.DrinkLogResponse;
import be.tanguy.beermap.api.logs.web.dto.LogDrinkRequest;
import be.tanguy.beermap.api.logs.web.dto.UpdateDrinkLogRequest;
import be.tanguy.beermap.api.users.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.DrinkLog.BASE)
@RequiredArgsConstructor
public class DrinkLogController {

    ///Services
    private final DrinkLogService drinkLogService;

    @PostMapping(ApiPaths.DrinkLog.ME)
    @ResponseStatus(HttpStatus.CREATED)
    public DrinkLogResponse logDrink(
            @AuthenticationPrincipal UserIdentity identity,
            @Valid @RequestBody LogDrinkRequest request
    ) {
        return DrinkLogResponse.from(drinkLogService.logDrink(identity, request));
    }

    @GetMapping(ApiPaths.DrinkLog.ME)
    public Page<DrinkLogResponse> getTimeline(
            @AuthenticationPrincipal UserIdentity identity,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return drinkLogService.getTimeline(identity, pageable)
                .map(DrinkLogResponse::from);
    }

    @GetMapping(ApiPaths.DrinkLog.ME + ApiPaths.DrinkLog.ID)
    public DrinkLogResponse getLog(
            @AuthenticationPrincipal UserIdentity identity,
            @PathVariable UUID logId
    ) {
        return DrinkLogResponse.from(drinkLogService.getLog(identity, logId));
    }

    @PatchMapping(ApiPaths.DrinkLog.ME + ApiPaths.DrinkLog.ID)
    public DrinkLogResponse updateLog(
            @AuthenticationPrincipal UserIdentity identity,
            @PathVariable UUID logId,
            @Valid @RequestBody UpdateDrinkLogRequest request
    ) {
        return DrinkLogResponse.from(drinkLogService.updateLog(identity, logId, request));
    }

    @DeleteMapping(ApiPaths.DrinkLog.ME + ApiPaths.DrinkLog.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLog(
            @AuthenticationPrincipal UserIdentity identity,
            @PathVariable UUID logId
    ) {
        drinkLogService.deleteLog(identity, logId);
    }

    @PostMapping(ApiPaths.DrinkLog.ME + "/from-draft/{draftId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DrinkLogResponse logFromDraft(
            @AuthenticationPrincipal UserIdentity identity,
            @PathVariable UUID draftId,
            @Valid @RequestBody LogDrinkRequest request
    ) {
        return DrinkLogResponse.from(drinkLogService.logFromDraft(identity, draftId, request));
    }
}

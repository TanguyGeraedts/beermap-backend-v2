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

    private final DrinkLogService drinkLogService;
    private final UserProfileService userProfileService;

    @PostMapping(ApiPaths.DrinkLog.ME)
    @ResponseStatus(HttpStatus.CREATED)
    public DrinkLogResponse logDrink(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody LogDrinkRequest request
    ) {
        UUID userId = currentUserId(userIdentity);
        return DrinkLogResponse.from(
                drinkLogService.logDrink(request.toCommand(userId))
        );
    }

    @GetMapping(ApiPaths.DrinkLog.ME)
    public Page<DrinkLogResponse> getTimeline(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID userId = currentUserId(userIdentity);
        return drinkLogService.getTimeline(userId, pageable)
                .map(DrinkLogResponse::from);
    }

    @GetMapping(ApiPaths.DrinkLog.ME + ApiPaths.DrinkLog.ID)
    public DrinkLogResponse getLog(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID logId
    ) {
        UUID userId = currentUserId(userIdentity);
        return DrinkLogResponse.from(
                drinkLogService.getLog(userId, logId)
        );
    }

    @PatchMapping(ApiPaths.DrinkLog.ME + ApiPaths.DrinkLog.ID)
    public DrinkLogResponse updateLog(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID logId,
            @Valid @RequestBody UpdateDrinkLogRequest request
    ) {
        UUID userId = currentUserId(userIdentity);
        return DrinkLogResponse.from(
                drinkLogService.updateLog(request.toCommand(userId, logId))
        );
    }

    @DeleteMapping(ApiPaths.DrinkLog.ME + ApiPaths.DrinkLog.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLog(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @PathVariable UUID logId
    ) {
        UUID userId = currentUserId(userIdentity);
        drinkLogService.deleteLog(userId, logId);
    }

    private UUID currentUserId(UserIdentity userIdentity) {
        return userProfileService.getCurrentUser(userIdentity).getId();
    }
}

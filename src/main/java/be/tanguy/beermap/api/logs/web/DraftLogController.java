package be.tanguy.beermap.api.logs.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.service.DraftLogService;
import be.tanguy.beermap.api.logs.web.dto.CreateDraftLogRequest;
import be.tanguy.beermap.api.logs.web.dto.DraftLogResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.DraftLog.BASE)
@RequiredArgsConstructor
public class DraftLogController {

    /// Services
    private final DraftLogService draftLogService;

    @GetMapping(ApiPaths.DraftLog.ME)
    public ResponseEntity<List<DraftLogResponse>> getActiveDrafts(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        List<DraftLogResponse> response = DraftLogResponse.fromEntities(draftLogService.getActiveDrafts(userIdentity));

        return ResponseEntity.ok(response);
    }

    @PostMapping(ApiPaths.DraftLog.ME)
    public ResponseEntity<DraftLogResponse> createDraftLog(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody CreateDraftLogRequest request
    ) {
        DraftLog draftLog = draftLogService.createDraftLog(userIdentity, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DraftLogResponse.fromEntity(draftLog));
    }
}

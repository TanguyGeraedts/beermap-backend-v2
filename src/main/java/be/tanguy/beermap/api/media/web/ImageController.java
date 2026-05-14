package be.tanguy.beermap.api.media.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.media.service.ImageStorageService;
import be.tanguy.beermap.api.media.web.dto.CreateUploadUrlRequest;
import be.tanguy.beermap.api.media.web.dto.CreateUploadUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.Images.BASE)
@RequiredArgsConstructor
public class ImageController {

    private final ImageStorageService imageStorageService;

    @PostMapping(ApiPaths.Images.PRESIGN_PROFILE_PICTURE)
    public CreateUploadUrlResponse createProfilePictureUploadUrl(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody CreateUploadUrlRequest request
    ) {
        return imageStorageService.createProfilePictureUploadUrl(
                userIdentity,
                request.contentType()
        );
    }

    @PostMapping(ApiPaths.Images.PRESIGN_DRINK_LOG)
    public CreateUploadUrlResponse createDrinkLogUploadUrl(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody CreateUploadUrlRequest request
    ) {
        return imageStorageService.createDrinkLogUploadUrl(
                userIdentity,
                request.contentType()
        );
    }

    @DeleteMapping(ApiPaths.Images.DELETE)
    public ResponseEntity<Void> deleteImage(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @RequestParam String key
    ) {
        imageStorageService.deleteImage(userIdentity, key);
        return ResponseEntity.noContent().build();
    }
}
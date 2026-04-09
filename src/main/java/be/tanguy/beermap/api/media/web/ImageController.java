package be.tanguy.beermap.api.media.web;

import be.tanguy.beermap.api.common.security.CurrentUser;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.media.service.ImageStorageService;
import be.tanguy.beermap.api.media.web.dto.CreateUploadUrlRequest;
import be.tanguy.beermap.api.media.web.dto.CreateUploadUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.Images.BASE)
@RequiredArgsConstructor
public class ImageController {

    private final ImageStorageService imageStorageService;
    private final CurrentUser currentUser;

    @PostMapping(ApiPaths.Images.PRESIGN_PROFILE_PICTURE)
    public CreateUploadUrlResponse createProfilePictureUploadUrl(
            @Valid @RequestBody CreateUploadUrlRequest request
    ) {
        return imageStorageService.createProfilePictureUploadUrl(
                currentUser.getSubject(),
                request.contentType()
        );
    }

    @PostMapping(ApiPaths.Images.PRESIGN_DRINK_LOG)
    public CreateUploadUrlResponse createDrinkLogUploadUrl(
            @Valid @RequestBody CreateUploadUrlRequest request
    ) {
        return imageStorageService.createDrinkLogUploadUrl(
                currentUser.getSubject(),
                request.drinkLogId(),
                request.contentType()
        );
    }
}
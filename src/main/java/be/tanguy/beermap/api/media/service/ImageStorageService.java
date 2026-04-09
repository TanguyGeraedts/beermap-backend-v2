package be.tanguy.beermap.api.media.service;

import be.tanguy.beermap.api.media.web.dto.CreateUploadUrlResponse;

public interface ImageStorageService {

    CreateUploadUrlResponse createProfilePictureUploadUrl(
            String userId,
            String contentType
    );

    CreateUploadUrlResponse createDrinkLogUploadUrl(
            String userId,
            String drinkLogId,
            String contentType
    );
}
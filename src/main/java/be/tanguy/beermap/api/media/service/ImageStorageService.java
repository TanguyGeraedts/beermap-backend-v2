package be.tanguy.beermap.api.media.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.media.web.dto.CreateUploadUrlResponse;

public interface ImageStorageService {

    CreateUploadUrlResponse createProfilePictureUploadUrl(
            UserIdentity userIdentity,
            String contentType
    );

    CreateUploadUrlResponse createDrinkLogUploadUrl(
            UserIdentity userIdentity,
            String drinkLogId,
            String contentType
    );

    void deleteImage(String key);
}

package be.tanguy.beermap.api.media.service;

import be.tanguy.beermap.api.common.config.storage.StorageProperties;
import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.media.web.dto.CreateUploadUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

    private static final Duration UPLOAD_URL_DURATION = Duration.ofMinutes(10);

    private final S3Presigner s3Presigner;
    private final StorageProperties storageProperties;
    private final ImageKeyService imageKeyService;

    @Override
    public CreateUploadUrlResponse createProfilePictureUploadUrl(
            UserIdentity userIdentity,
            String contentType
    ) {
        String key = imageKeyService.buildProfilePictureKey(userIdentity.subject(), contentType);
        return createPresignedUpload(key, contentType);
    }

    @Override
    public CreateUploadUrlResponse createDrinkLogUploadUrl(
            UserIdentity userIdentity,
            String drinkLogId,
            String contentType
    ) {
        String key = imageKeyService.buildDrinkLogKey(userIdentity.subject(), drinkLogId, contentType);
        return createPresignedUpload(key, contentType);
    }

    private CreateUploadUrlResponse createPresignedUpload(String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(storageProperties.bucketName())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(UPLOAD_URL_DURATION)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return new CreateUploadUrlResponse(
                key,
                presignedRequest.url().toString(),
                buildPublicUrl(key)
        );
    }

    private String buildPublicUrl(String key) {
        return storageProperties.publicBaseUrl() + "/" + key;
    }
}

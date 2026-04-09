package be.tanguy.beermap.api.common.config.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.r2")
public record StorageProperties(
        String accountId,
        String accessKeyId,
        String secretAccessKey,
        String bucketName,
        String publicBaseUrl
) {
    public String endpoint() {
        return "https://" + accountId + ".r2.cloudflarestorage.com";
    }
}
package be.tanguy.beermap.api.media.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ImageKeyService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public String buildProfilePictureKey(String userId, String contentType) {
        validateContentType(contentType);

        String extension = resolveExtension(contentType);
        String fileId = UUID.randomUUID().toString();

        return "users/" + userId + "/profile-picture/" + fileId + extension;
    }

    public String buildDrinkLogKey(String userId, String drinkLogId, String contentType) {
        validateContentType(contentType);

        if (drinkLogId == null || drinkLogId.isBlank()) {
            throw new IllegalArgumentException("Drink log id is required");
        }

        String extension = resolveExtension(contentType);
        String fileId = UUID.randomUUID().toString();

        return "users/" + userId + "/drink-logs/" + drinkLogId + "/" + fileId + extension;
    }

    private void validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content type is required");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Unsupported content type: " + contentType);
        };
    }
}
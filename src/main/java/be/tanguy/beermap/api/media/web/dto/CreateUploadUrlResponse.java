package be.tanguy.beermap.api.media.web.dto;

public record CreateUploadUrlResponse(
        String key,
        String uploadUrl,
        String publicUrl
) {
}
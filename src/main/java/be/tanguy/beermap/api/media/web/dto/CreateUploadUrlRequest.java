package be.tanguy.beermap.api.media.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUploadUrlRequest(

        String drinkLogId,

        @NotBlank(message = "Content type is required")
        String contentType

) {
}
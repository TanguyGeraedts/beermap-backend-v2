package be.tanguy.beermap.api.users.web.dto;

import be.tanguy.beermap.api.common.validation.AllowedContent;
import be.tanguy.beermap.api.common.validation.ValidDiscriminator;
import be.tanguy.beermap.api.common.validation.ValidUsername;
import jakarta.validation.constraints.Size;

import static be.tanguy.beermap.api.users.domain.UserProfileInfo.MAX_AVATAR_URL_LENGTH;
import static be.tanguy.beermap.api.users.domain.UserProfileInfo.MAX_BIO_LENGTH;

public record UpdateProfileRequest(

        @AllowedContent
        @ValidUsername(allowNull = true)
        String username,

        @AllowedContent
        @Size(max = MAX_BIO_LENGTH, message = "Bio must be at most " + MAX_BIO_LENGTH + " characters long")
        String bio,

        @Size(max = MAX_AVATAR_URL_LENGTH, message = "Avatar URL must be at most " + MAX_AVATAR_URL_LENGTH + " characters long")
        String avatarUrl,

        @ValidDiscriminator(allowNull = true)
        String discriminator

) {
}

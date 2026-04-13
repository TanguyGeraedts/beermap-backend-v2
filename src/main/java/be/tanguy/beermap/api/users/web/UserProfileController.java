package be.tanguy.beermap.api.users.web;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.service.UserProfileService;
import be.tanguy.beermap.api.users.web.dto.CreateProfileRequest;
import be.tanguy.beermap.api.users.web.dto.MeResponse;
import be.tanguy.beermap.api.users.web.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.Users.BASE)
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping(ApiPaths.Users.ME)
    public MeResponse getMe(
            @AuthenticationPrincipal UserIdentity userIdentity
    ) {
        return MeResponse.from(
                userProfileService.getCurrentUser(userIdentity)
        );
    }

    @PostMapping(ApiPaths.Users.ME)
    public MeResponse createMe(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody CreateProfileRequest request
    ) {
        UserProfile userProfile = userProfileService.createUser(
                userIdentity,
                request.username(),
                request.bio(),
                request.avatarUrl()
        );

        return MeResponse.from(userProfile);
    }

    @PatchMapping(ApiPaths.Users.ME)
    public MeResponse updateMe(
            @AuthenticationPrincipal UserIdentity userIdentity,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return MeResponse.from(
                userProfileService.updateProfile(
                        userIdentity,
                        request.username(),
                        request.bio(),
                        request.avatarUrl(),
                        request.discriminator()
                )
        );
    }
}
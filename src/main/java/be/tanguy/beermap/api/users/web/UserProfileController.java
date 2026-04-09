package be.tanguy.beermap.api.users.web;

import be.tanguy.beermap.api.common.security.CurrentUser;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.service.UserProfileService;
import be.tanguy.beermap.api.users.web.dto.CreateProfileRequest;
import be.tanguy.beermap.api.users.web.dto.MeResponse;
import be.tanguy.beermap.api.users.web.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.Users.BASE)
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final CurrentUser currentUser;

    @GetMapping(ApiPaths.Users.ME)
    public MeResponse getMe() {
        return MeResponse.from(
                userProfileService.getCurrentUser(currentUser.getIdentity())
        );
    }

    @PostMapping(ApiPaths.Users.ME)
    public MeResponse createMe(@Valid @RequestBody CreateProfileRequest request){
        UserProfile userProfile = userProfileService.createUser(
                currentUser.getIdentity(),
                request.username(),
                request.bio(),
                request.avatarUrl()
        );

        return MeResponse.from(userProfile);
    }

    @PatchMapping(ApiPaths.Users.ME)
    public MeResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return MeResponse.from(
                userProfileService.updateProfile(
                        currentUser.getIdentity(),
                        request.username(),
                        request.bio(),
                        request.avatarUrl(),
                        request.discriminator()
                )
        );
    }
}
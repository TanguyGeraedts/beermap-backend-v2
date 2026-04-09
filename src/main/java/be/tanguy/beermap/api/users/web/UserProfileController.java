package be.tanguy.beermap.api.users.web;

import be.tanguy.beermap.api.common.security.CurrentUser;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.users.service.UserProfileService;
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
                userProfileService.getOrCreateUser(
                        currentUser.getIssuer(),
                        currentUser.getSubject()
                )
        );
    }

    @PatchMapping(ApiPaths.Users.ME)
    public MeResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return MeResponse.from(
                userProfileService.updateProfile(
                        currentUser.getIssuer(),
                        currentUser.getSubject(),
                        request.username(),
                        request.bio(),
                        request.avatarUrl(),
                        request.discriminator()
                )
        );
    }
}
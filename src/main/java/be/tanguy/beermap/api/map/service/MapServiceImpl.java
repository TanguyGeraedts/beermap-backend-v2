package be.tanguy.beermap.api.map.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.friends.domain.FriendshipStatus;
import be.tanguy.beermap.api.friends.repository.FriendshipRepository;

import be.tanguy.beermap.api.map.domain.MapMode;
import be.tanguy.beermap.api.map.repository.MapPinProjection;
import be.tanguy.beermap.api.map.repository.MapRepository;
import be.tanguy.beermap.api.map.web.dto.MapPinResponse;
import be.tanguy.beermap.api.users.exception.ProfileNotFoundException;
import be.tanguy.beermap.api.users.repository.AuthIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapServiceImpl implements MapService {

    private static final int MAX_LIMIT = 1000;

    private final MapRepository mapRepository;
    private final FriendshipRepository friendshipRepository;
    private final AuthIdentityRepository authIdentityRepository;

    @Override
    public List<MapPinResponse> getPins(
            UserIdentity identity,
            MapMode mode,
            List<String> categories,
            Instant from,
            Instant to,
            double minLat,
            double minLon,
            double maxLat,
            double maxLon,
            int limit
    ) {
        int safeLimit = Math.min(limit, MAX_LIMIT);
        boolean hasCategories = categories != null && !categories.isEmpty();

        List<MapPinProjection> pins = switch (mode) {
            case MINE -> {
                UUID userId = resolveUserId(identity);
                yield hasCategories
                        ? mapRepository.findMapPinsForUsersFiltered(List.of(userId), minLat, minLon, maxLat, maxLon, categories, from, to, safeLimit)
                        : mapRepository.findMapPinsForUsers(List.of(userId), minLat, minLon, maxLat, maxLon, from, to, safeLimit);
            }
            case FRIENDS -> {
                UUID userId = resolveUserId(identity);
                List<UUID> friendIds = resolveFriendIds(userId);
                yield hasCategories
                        ? mapRepository.findMapPinsForUsersFiltered(friendIds, minLat, minLon, maxLat, maxLon, categories, from, to, safeLimit)
                        : mapRepository.findMapPinsForUsers(friendIds, minLat, minLon, maxLat, maxLon, from, to, safeLimit);
            }
            case GLOBAL -> hasCategories
                    ? mapRepository.findMapPinsForAllFiltered(minLat, minLon, maxLat, maxLon, categories, from, to, safeLimit)
                    : mapRepository.findMapPinsForAll(minLat, minLon, maxLat, maxLon, from, to, safeLimit);
        };

        return pins.stream()
                .map(MapPinResponse::from)
                .toList();
    }

    /// Helpers
    private UUID resolveUserId(UserIdentity identity) {
        return authIdentityRepository
                .findByIssuerAndSubject(identity.issuer(), identity.subject())
                .map(ai -> ai.getUserProfile().getId())
                .orElseThrow(ProfileNotFoundException::new);
    }

    private List<UUID> resolveFriendIds(UUID userId) {
        return friendshipRepository
                .findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED)
                .stream()
                .map(f -> f.getOtherUserId(userId))
                .toList();
    }
}
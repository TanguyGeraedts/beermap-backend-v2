package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;
import be.tanguy.beermap.api.logs.exception.DraftLimitExceededException;
import be.tanguy.beermap.api.logs.repository.DraftLogRepository;
import be.tanguy.beermap.api.logs.web.dto.CreateDraftLogRequest;
import be.tanguy.beermap.api.media.service.ImageStorageService;
import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.service.UserProfileService;
import be.tanguy.beermap.api.common.security.UserIdentity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DraftLogServiceImplTest {

    @Mock UserProfileService userProfileService;
    @Mock DraftLogRepository draftLogRepository;
    @Mock ImageStorageService imageStorageService;

    @InjectMocks DraftLogServiceImpl draftLogService;

    private UserIdentity userIdentity;
    private UserProfile normalUser;
    private UserProfile premiumUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userIdentity = mock(UserIdentity.class);
        normalUser = mock(UserProfile.class);
        premiumUser = mock(UserProfile.class);

        when(normalUser.getId()).thenReturn(userId);
        when(normalUser.getAccountType()).thenReturn(AccountType.NORMAL);

        when(premiumUser.getId()).thenReturn(userId);
        when(premiumUser.getAccountType()).thenReturn(AccountType.PREMIUM);
    }

    // ── createDraftLog ────────────────────────────────────────────────────────

    @Nested
    class CreateDraftLog {

        @Test
        void createsSuccessfully_whenUnderLimit() {
            when(userProfileService.getCurrentUser(userIdentity)).thenReturn(normalUser);
            when(draftLogRepository.countByUserIdAndStatus(userId, DraftStatus.ACTIVE)).thenReturn(0L);
            when(draftLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            DraftLog result = draftLogService.createDraftLog(userIdentity,
                    new CreateDraftLogRequest(null, LocalDateTime.now().minusHours(1)));

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(DraftStatus.ACTIVE);
            verify(draftLogRepository).save(any());
        }

        @Test
        void throwsDraftLimitExceeded_whenNormalUserAtLimit() {
            when(userProfileService.getCurrentUser(userIdentity)).thenReturn(normalUser);
            when(draftLogRepository.countByUserIdAndStatus(userId, DraftStatus.ACTIVE)).thenReturn(10L);

            assertThatThrownBy(() -> draftLogService.createDraftLog(userIdentity,
                    new CreateDraftLogRequest(null, LocalDateTime.now().minusHours(1))))
                    .isInstanceOf(DraftLimitExceededException.class);

            verify(draftLogRepository, never()).save(any());
        }

        @Test
        void allowsMoreDrafts_forPremiumUser() {
            when(userProfileService.getCurrentUser(userIdentity)).thenReturn(premiumUser);
            when(draftLogRepository.countByUserIdAndStatus(userId, DraftStatus.ACTIVE)).thenReturn(10L);
            when(draftLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            assertThatNoException().isThrownBy(() -> draftLogService.createDraftLog(userIdentity,
                    new CreateDraftLogRequest(null, LocalDateTime.now().minusHours(1))));
        }

        @Test
        void throwsDraftLimitExceeded_whenPremiumUserAtLimit() {
            when(userProfileService.getCurrentUser(userIdentity)).thenReturn(premiumUser);
            when(draftLogRepository.countByUserIdAndStatus(userId, DraftStatus.ACTIVE)).thenReturn(50L);

            assertThatThrownBy(() -> draftLogService.createDraftLog(userIdentity,
                    new CreateDraftLogRequest(null, LocalDateTime.now().minusHours(1))))
                    .isInstanceOf(DraftLimitExceededException.class);
        }
    }

    // ── deleteDrafts ──────────────────────────────────────────────────────────

    @Nested
    class DeleteDrafts {

        @Test
        void expiresDrafts_andDeletesPhotos() {
            when(userProfileService.getCurrentUserId(userIdentity)).thenReturn(userId);

            DraftLog withPhoto = mockActiveDraft("https://cdn.example.com/photo.jpg");
            DraftLog withoutPhoto = mockActiveDraft(null);

            List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
            when(draftLogRepository.findAllByIdInAndUserId(ids, userId))
                    .thenReturn(List.of(withPhoto, withoutPhoto));

            draftLogService.deleteDrafts(userIdentity, ids);

            verify(withPhoto).markExpired();
            verify(withoutPhoto).markExpired();
            verify(imageStorageService).deleteImage("https://cdn.example.com/photo.jpg");
            verify(imageStorageService, times(1)).deleteImage(any()); // only called once
        }

        @Test
        void doesNotDeletePhoto_whenPhotoUrlIsNull() {
            when(userProfileService.getCurrentUserId(userIdentity)).thenReturn(userId);
            DraftLog draft = mockActiveDraft(null);
            when(draftLogRepository.findAllByIdInAndUserId(any(), eq(userId)))
                    .thenReturn(List.of(draft));

            draftLogService.deleteDrafts(userIdentity, List.of(UUID.randomUUID()));

            verify(imageStorageService, never()).deleteImage(any());
        }

        @Test
        void doesNotDeleteOtherUsersDrafts() {
            when(userProfileService.getCurrentUserId(userIdentity)).thenReturn(userId);
            List<UUID> ids = List.of(UUID.randomUUID());

            // repo scopes by userId, returns empty for non-owned drafts
            when(draftLogRepository.findAllByIdInAndUserId(ids, userId)).thenReturn(List.of());

            draftLogService.deleteDrafts(userIdentity, ids);

            verify(imageStorageService, never()).deleteImage(any());
        }
    }

    // ── deleteAllDrafts ───────────────────────────────────────────────────────

    @Nested
    class DeleteAllDrafts {

        @Test
        void expiresAllActiveDrafts() {
            when(userProfileService.getCurrentUserId(userIdentity)).thenReturn(userId);

            DraftLog d1 = mockActiveDraft("https://cdn.example.com/a.jpg");
            DraftLog d2 = mockActiveDraft("https://cdn.example.com/b.jpg");
            DraftLog d3 = mockActiveDraft(null);

            when(draftLogRepository.findAllByUserIdAndStatus(userId, DraftStatus.ACTIVE))
                    .thenReturn(List.of(d1, d2, d3));

            draftLogService.deleteAllDrafts(userIdentity);

            verify(d1).markExpired();
            verify(d2).markExpired();
            verify(d3).markExpired();
            verify(imageStorageService, times(2)).deleteImage(any());
        }

        @Test
        void doesNothingWhenNoDrafts() {
            when(userProfileService.getCurrentUserId(userIdentity)).thenReturn(userId);
            when(draftLogRepository.findAllByUserIdAndStatus(userId, DraftStatus.ACTIVE))
                    .thenReturn(List.of());

            draftLogService.deleteAllDrafts(userIdentity);

            verify(imageStorageService, never()).deleteImage(any());
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private DraftLog mockActiveDraft(String photoUrl) {
        DraftLog draft = mock(DraftLog.class);
        when(draft.getPhotoUrl()).thenReturn(photoUrl);
        return draft;
    }
}
package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DraftLog;
import be.tanguy.beermap.api.logs.domain.DraftStatus;
import be.tanguy.beermap.api.logs.repository.DraftLogRepository;
import be.tanguy.beermap.api.media.service.ImageStorageService;
import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.PlanLimits;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class DraftRetentionServiceImplTest {

    @Mock DraftLogRepository draftLogRepository;
    @Mock ImageStorageService imageStorageService;

    @InjectMocks DraftRetentionServiceImpl retentionService;

    @Test
    void expiresDrafts_forEachAccountType() {
        for (AccountType type : AccountType.values()) {
            when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(type), any()))
                    .thenReturn(List.of());
        }

        retentionService.cleanupExpiredDrafts();

        for (AccountType type : AccountType.values()) {
            verify(draftLogRepository).findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(type), any());
        }
    }

    @Test
    void usesDifferentCutoffs_forNormalAndPremium() {
        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        for (AccountType type : AccountType.values()) {
            when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(type), any()))
                    .thenReturn(List.of());
        }

        retentionService.cleanupExpiredDrafts();

        verify(draftLogRepository, times(AccountType.values().length))
                .findExpiredDrafts(eq(DraftStatus.ACTIVE), any(), cutoffCaptor.capture());

        List<LocalDateTime> cutoffs = cutoffCaptor.getAllValues();

        // normal cutoff should be more recent than premium (shorter retention)
        LocalDateTime normalCutoff = LocalDateTime.now().minusDays(PlanLimits.maxDraftRetentionDays(AccountType.NORMAL));
        LocalDateTime premiumCutoff = LocalDateTime.now().minusDays(PlanLimits.maxDraftRetentionDays(AccountType.PREMIUM));

        assertThat(cutoffs).anySatisfy(c ->
                assertThat(c).isCloseTo(normalCutoff, within(5, ChronoUnit.SECONDS)));
        assertThat(cutoffs).anySatisfy(c ->
                assertThat(c).isCloseTo(premiumCutoff, within(5, ChronoUnit.SECONDS)));
    }

    @Test
    void marksExpiredAndDeletesPhoto_whenPhotoExists() {
        DraftLog draft = mock(DraftLog.class);
        when(draft.getPhotoUrl()).thenReturn("https://cdn.example.com/photo.jpg");

        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.NORMAL), any()))
                .thenReturn(List.of(draft));
        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.PREMIUM), any()))
                .thenReturn(List.of());
        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.ADMIN), any()))
                .thenReturn(List.of());

        retentionService.cleanupExpiredDrafts();

        verify(draft).markExpired();
        verify(imageStorageService).deleteImage("https://cdn.example.com/photo.jpg");
        verify(draft).updatePhoto(null);
    }

    @Test
    void skipsImageDeletion_whenNoPhoto() {
        DraftLog draft = mock(DraftLog.class);
        when(draft.getPhotoUrl()).thenReturn(null);

        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.NORMAL), any()))
                .thenReturn(List.of(draft));
        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.PREMIUM), any()))
                .thenReturn(List.of());
        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.ADMIN), any()))
                .thenReturn(List.of());

        retentionService.cleanupExpiredDrafts();

        verify(draft).markExpired();
        verify(imageStorageService, never()).deleteImage(any());
    }

    @Test
    void continuesProcessing_whenOneDraftFails() {
        DraftLog failingDraft = mock(DraftLog.class);
        DraftLog successDraft = mock(DraftLog.class);
        when(successDraft.getPhotoUrl()).thenReturn(null);

        doThrow(new RuntimeException("S3 error")).when(failingDraft).markExpired();

        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.NORMAL), any()))
                .thenReturn(List.of(failingDraft, successDraft));
        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.PREMIUM), any()))
                .thenReturn(List.of());
        when(draftLogRepository.findExpiredDrafts(eq(DraftStatus.ACTIVE), eq(AccountType.ADMIN), any()))
                .thenReturn(List.of());

        // should not throw
        retentionService.cleanupExpiredDrafts();

        verify(successDraft).markExpired();
    }
}
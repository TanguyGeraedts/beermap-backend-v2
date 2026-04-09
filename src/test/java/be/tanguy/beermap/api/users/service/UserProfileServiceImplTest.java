package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.AuthIdentity;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.domain.UserProfileInfo;
import be.tanguy.beermap.api.users.exception.CurrentUserNotFoundException;
import be.tanguy.beermap.api.users.exception.UserProfileNotFoundException;
import be.tanguy.beermap.api.users.exception.UsernameDiscriminatorAlreadyTakenException;
import be.tanguy.beermap.api.users.repository.AuthIdentityRepository;
import be.tanguy.beermap.api.users.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private static final String ISSUER = "http://localhost:8081/realms/beermap";
    private static final String SUBJECT = "test-user-123";

    private UserProfile existingUser;

    @BeforeEach
    void setUp() {
        UserProfileInfo info = UserProfileInfo.create("Tanguy", "1234");
        existingUser = UserProfile.create(info);
    }

    @Test
    void getOrCreateUser_shouldReturnExistingUser_whenAuthIdentityExists() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        UserProfile result = userProfileService.getOrCreateUser(ISSUER, SUBJECT);

        assertSame(existingUser, result);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
        verify(authIdentityRepository, never()).save(any(AuthIdentity.class));
    }

    @Test
    void getOrCreateUser_shouldCreateNewUser_whenAuthIdentityDoesNotExist() {
        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.empty());

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString()))
                .thenReturn(false);

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(authIdentityRepository.save(any(AuthIdentity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile result = userProfileService.getOrCreateUser(ISSUER, SUBJECT);

        assertNotNull(result);
        assertNotNull(result.getInfo());
        assertNotNull(result.getInfo().getUsername());
        assertNotNull(result.getInfo().getUsernameNormalized());
        assertEquals(AccountType.NORMAL, result.getAccountType());

        verify(userProfileRepository).save(any(UserProfile.class));
        verify(authIdentityRepository).save(any(AuthIdentity.class));
    }

    @Test
    void getCurrentUser_shouldThrow_whenAuthIdentityDoesNotExist() {
        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.empty());

        assertThrows(CurrentUserNotFoundException.class,
                () -> userProfileService.getCurrentUser(ISSUER, SUBJECT));
    }

    @Test
    void updateProfile_shouldUpdateBioAndAvatar() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        UserProfile result = userProfileService.updateProfile(
                ISSUER,
                SUBJECT,
                null,
                "  Hello world  ",
                "  https://cdn.example.com/avatar.png  ",
                null
        );

        assertSame(existingUser, result);
        assertEquals("Hello world", result.getInfo().getBio());
        assertEquals("https://cdn.example.com/avatar.png", result.getInfo().getAvatarUrl());
    }

    @Test
    void updateProfile_shouldUpdateUsername_whenAvailable() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator("newname", "1234"))
                .thenReturn(false);

        UserProfile result = userProfileService.updateProfile(
                ISSUER,
                SUBJECT,
                "NewName",
                null,
                null,
                null
        );

        assertEquals("NewName", result.getInfo().getUsername());
        assertEquals("newname", result.getInfo().getUsernameNormalized());
    }

    @Test
    void updateProfile_shouldThrow_whenUsernameAndDiscriminatorCombinationIsTaken() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator("takenname", "1234"))
                .thenReturn(true);

        assertThrows(UsernameDiscriminatorAlreadyTakenException.class,
                () -> userProfileService.updateProfile(
                        ISSUER,
                        SUBJECT,
                        "TakenName",
                        null,
                        null,
                        null
                ));
    }

    @Test
    void updateProfile_shouldIgnoreDiscriminatorChange_whenUserIsNotPremium() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        String oldDiscriminator = existingUser.getInfo().getDiscriminator();

        UserProfile result = userProfileService.updateProfile(
                ISSUER,
                SUBJECT,
                null,
                null,
                null,
                "9999"
        );

        assertEquals(oldDiscriminator, result.getInfo().getDiscriminator());
        verify(userProfileRepository, never())
                .existsByInfo_UsernameNormalizedAndInfo_Discriminator(existingUser.getInfo().getUsernameNormalized(), "9999");
    }

    @Test
    void updateProfile_shouldUpdateDiscriminator_whenUserIsPremiumAndAvailable() throws Exception {
        setAccountType(existingUser, AccountType.PREMIUM);

        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(
                existingUser.getInfo().getUsernameNormalized(),
                "9999"
        )).thenReturn(false);

        UserProfile result = userProfileService.updateProfile(
                ISSUER,
                SUBJECT,
                null,
                null,
                null,
                "9999"
        );

        assertEquals("9999", result.getInfo().getDiscriminator());
    }

    @Test
    void updateProfile_shouldThrow_whenPremiumUserChoosesTakenDiscriminator() throws Exception {
        setAccountType(existingUser, AccountType.PREMIUM);

        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(
                existingUser.getInfo().getUsernameNormalized(),
                "9999"
        )).thenReturn(true);

        assertThrows(UsernameDiscriminatorAlreadyTakenException.class,
                () -> userProfileService.updateProfile(
                        ISSUER,
                        SUBJECT,
                        null,
                        null,
                        null,
                        "9999"
                ));
    }

    @Test
    void upgradeToPremium_shouldUpgradeNormalUser() {
        UUID userId = existingUser.getId();

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UserProfile result = userProfileService.upgradeToPremium(userId);

        assertEquals(AccountType.PREMIUM, result.getAccountType());
    }

    @Test
    void upgradeToPremium_shouldThrow_whenUserNotFound() {
        UUID randomId = UUID.randomUUID();

        when(userProfileRepository.findById(randomId))
                .thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> userProfileService.upgradeToPremium(randomId));
    }

    private void setAccountType(UserProfile userProfile, AccountType accountType) throws Exception {
        Field field = UserProfile.class.getDeclaredField("accountType");
        field.setAccessible(true);
        field.set(userProfile, accountType);
    }
}
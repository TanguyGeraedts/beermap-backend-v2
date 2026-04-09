package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.AuthIdentity;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.domain.UserProfileInfo;
import be.tanguy.beermap.api.users.exception.InvalidDiscriminatorException;
import be.tanguy.beermap.api.users.exception.ProfileAlreadyExistsException;
import be.tanguy.beermap.api.users.exception.ProfileNotFoundException;
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

    private UserIdentity userIdentity;
    private UserProfile existingUser;

    @BeforeEach
    void setUp() {
        userIdentity = new UserIdentity(ISSUER, SUBJECT);

        UserProfileInfo info = UserProfileInfo.create("Tanguy", "1234");
        existingUser = UserProfile.create(info);
    }

    @Test
    void createUser_shouldCreateNewUser_whenAuthIdentityDoesNotExist() {
        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.empty());

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString()))
                .thenReturn(false);

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(authIdentityRepository.save(any(AuthIdentity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile result = userProfileService.createUser(
                userIdentity,
                "  Tanguy  ",
                "  Hello world  ",
                "  https://cdn.example.com/avatar.png  "
        );

        assertNotNull(result);
        assertNotNull(result.getInfo());
        assertEquals("Tanguy", result.getInfo().getUsername());
        assertEquals("tanguy", result.getInfo().getUsernameNormalized());
        assertNotNull(result.getInfo().getDiscriminator());
        assertEquals(AccountType.NORMAL, result.getAccountType());
        assertEquals("Hello world", result.getInfo().getBio());
        assertEquals("https://cdn.example.com/avatar.png", result.getInfo().getAvatarUrl());

        verify(userProfileRepository).save(any(UserProfile.class));
        verify(authIdentityRepository).save(any(AuthIdentity.class));
    }

    @Test
    void createUser_shouldThrow_whenAuthIdentityAlreadyExists() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        assertThrows(ProfileAlreadyExistsException.class,
                () -> userProfileService.createUser(
                        userIdentity,
                        "Tanguy",
                        null,
                        null
                ));

        verify(userProfileRepository, never()).save(any(UserProfile.class));
        verify(authIdentityRepository, never()).save(any(AuthIdentity.class));
    }

    @Test
    void createUser_shouldNormalizeEmptyBioAndAvatarToNull() {
        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.empty());

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString()))
                .thenReturn(false);

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(authIdentityRepository.save(any(AuthIdentity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile result = userProfileService.createUser(
                userIdentity,
                "Tanguy",
                "   ",
                "   "
        );

        assertNull(result.getInfo().getBio());
        assertNull(result.getInfo().getAvatarUrl());
    }

    @Test
    void getCurrentUser_shouldReturnUser_whenAuthIdentityExists() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        UserProfile result = userProfileService.getCurrentUser(userIdentity);

        assertSame(existingUser, result);
    }

    @Test
    void getCurrentUser_shouldThrow_whenAuthIdentityDoesNotExist() {
        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class,
                () -> userProfileService.getCurrentUser(userIdentity));
    }

    @Test
    void updateProfile_shouldUpdateBioAndAvatar() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        UserProfile result = userProfileService.updateProfile(
                userIdentity,
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
    void updateProfile_shouldNormalizeBlankBioAndAvatarToNull() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        existingUser.updateBio("Existing bio");
        existingUser.updateAvatarUrl("https://cdn.example.com/old.png");

        UserProfile result = userProfileService.updateProfile(
                userIdentity,
                null,
                "   ",
                "   ",
                null
        );

        assertNull(result.getInfo().getBio());
        assertNull(result.getInfo().getAvatarUrl());
    }

    @Test
    void updateProfile_shouldUpdateUsername_whenAvailable() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        when(userProfileRepository.existsByInfo_UsernameNormalizedAndInfo_Discriminator("newname", "1234"))
                .thenReturn(false);

        UserProfile result = userProfileService.updateProfile(
                userIdentity,
                "NewName",
                null,
                null,
                null
        );

        assertEquals("NewName", result.getInfo().getUsername());
        assertEquals("newname", result.getInfo().getUsernameNormalized());
    }

    @Test
    void updateProfile_shouldIgnoreUsernameUpdate_whenBlank() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        String oldUsername = existingUser.getInfo().getUsername();
        String oldUsernameNormalized = existingUser.getInfo().getUsernameNormalized();

        UserProfile result = userProfileService.updateProfile(
                userIdentity,
                "   ",
                null,
                null,
                null
        );

        assertEquals(oldUsername, result.getInfo().getUsername());
        assertEquals(oldUsernameNormalized, result.getInfo().getUsernameNormalized());
        verify(userProfileRepository, never())
                .existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString());
    }

    @Test
    void updateProfile_shouldIgnoreUsernameUpdate_whenNormalizedUsernameIsSame() {
        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        UserProfile result = userProfileService.updateProfile(
                userIdentity,
                "  tanguy  ",
                null,
                null,
                null
        );

        assertEquals("Tanguy", result.getInfo().getUsername());
        assertEquals("tanguy", result.getInfo().getUsernameNormalized());

        verify(userProfileRepository, never())
                .existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString());
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
                        userIdentity,
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
                userIdentity,
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
    void updateProfile_shouldIgnoreDiscriminatorUpdate_whenBlank() throws Exception {
        setAccountType(existingUser, AccountType.PREMIUM);

        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        String oldDiscriminator = existingUser.getInfo().getDiscriminator();

        UserProfile result = userProfileService.updateProfile(
                userIdentity,
                null,
                null,
                null,
                "   "
        );

        assertEquals(oldDiscriminator, result.getInfo().getDiscriminator());
        verify(userProfileRepository, never())
                .existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString());
    }

    @Test
    void updateProfile_shouldIgnoreDiscriminatorUpdate_whenSameAsCurrent() throws Exception {
        setAccountType(existingUser, AccountType.PREMIUM);

        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        UserProfile result = userProfileService.updateProfile(
                userIdentity,
                null,
                null,
                null,
                "1234"
        );

        assertEquals("1234", result.getInfo().getDiscriminator());
        verify(userProfileRepository, never())
                .existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString());
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
                userIdentity,
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
                        userIdentity,
                        null,
                        null,
                        null,
                        "9999"
                ));
    }

    @Test
    void updateProfile_shouldThrow_whenPremiumUserUsesInvalidDiscriminator() throws Exception {
        setAccountType(existingUser, AccountType.PREMIUM);

        AuthIdentity authIdentity = AuthIdentity.create(ISSUER, SUBJECT, existingUser);

        when(authIdentityRepository.findByIssuerAndSubject(ISSUER, SUBJECT))
                .thenReturn(Optional.of(authIdentity));

        assertThrows(InvalidDiscriminatorException.class,
                () -> userProfileService.updateProfile(
                        userIdentity,
                        null,
                        null,
                        null,
                        "99"
                ));

        verify(userProfileRepository, never())
                .existsByInfo_UsernameNormalizedAndInfo_Discriminator(anyString(), anyString());
    }

    private void setAccountType(UserProfile userProfile, AccountType accountType) throws Exception {
        Field field = UserProfile.class.getDeclaredField("accountType");
        field.setAccessible(true);
        field.set(userProfile, accountType);
    }
}

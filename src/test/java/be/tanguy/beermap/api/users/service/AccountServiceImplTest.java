package be.tanguy.beermap.api.users.service;

import be.tanguy.beermap.api.users.domain.AccountType;
import be.tanguy.beermap.api.users.domain.UserProfile;
import be.tanguy.beermap.api.users.domain.UserProfileInfo;
import be.tanguy.beermap.api.users.exception.UserProfileNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private UserProfile existingUser;

    @BeforeEach
    void setUp() {
        UserProfileInfo info = UserProfileInfo.create("Tanguy", "1234");
        existingUser = UserProfile.create(info);
    }

    @Test
    void upgradeToPremium_shouldUpgradeNormalUser() {
        UUID userId = existingUser.getId();

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UserProfile result = accountService.upgradeToPremium(userId);

        assertEquals(AccountType.PREMIUM, result.getAccountType());
    }

    @Test
    void upgradeToPremium_shouldKeepPremiumUserAsPremium() throws Exception {
        setAccountType(existingUser, AccountType.PREMIUM);
        UUID userId = existingUser.getId();

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UserProfile result = accountService.upgradeToPremium(userId);

        assertEquals(AccountType.PREMIUM, result.getAccountType());
    }

    @Test
    void upgradeToPremium_shouldThrow_whenUserNotFound() {
        UUID randomId = UUID.randomUUID();

        when(userProfileRepository.findById(randomId))
                .thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> accountService.upgradeToPremium(randomId));
    }

    @Test
    void downgradeToUser_shouldRevertPremiumToNormal() throws Exception {
        setAccountType(existingUser, AccountType.PREMIUM);
        UUID userId = existingUser.getId();

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UserProfile result = accountService.downgradeToUser(userId);

        assertEquals(AccountType.NORMAL, result.getAccountType());
    }

    @Test
    void downgradeToUser_shouldThrow_whenUserNotFound() {
        UUID randomId = UUID.randomUUID();

        when(userProfileRepository.findById(randomId))
                .thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> accountService.downgradeToUser(randomId));
    }

    @Test
    void upgradeToAdmin_shouldSetAdminAccountType() {
        UUID userId = existingUser.getId();

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UserProfile result = accountService.upgradeToAdmin(userId);

        assertEquals(AccountType.ADMIN, result.getAccountType());
    }

    @Test
    void upgradeToAdmin_shouldThrow_whenUserNotFound() {
        UUID randomId = UUID.randomUUID();

        when(userProfileRepository.findById(randomId))
                .thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> accountService.upgradeToAdmin(randomId));
    }

    private void setAccountType(UserProfile userProfile, AccountType accountType) throws Exception {
        Field field = UserProfile.class.getDeclaredField("accountType");
        field.setAccessible(true);
        field.set(userProfile, accountType);
    }
}

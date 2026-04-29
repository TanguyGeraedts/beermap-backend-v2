package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.common.security.UserIdentity;
import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.domain.DrinkLog;
import be.tanguy.beermap.api.logs.event.DrinkLogCreatedEvent; // Added import
import be.tanguy.beermap.api.logs.repository.DraftLogRepository;
import be.tanguy.beermap.api.logs.repository.DrinkCatalogItemRepository;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.logs.web.dto.LogDrinkRequest;
import be.tanguy.beermap.api.users.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrinkLogServiceImplTest {

    @Mock private DrinkLogRepository drinkLogRepository;
    @Mock private DrinkCatalogItemRepository drinkCatalogItemRepository;
    @Mock private DraftLogRepository draftLogRepository;
    @Mock private UserProfileService userProfileService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DrinkLogServiceImpl service;

    private UserIdentity userIdentity;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userIdentity = mock(UserIdentity.class);
        userId = UUID.randomUUID();
        lenient().when(userProfileService.getCurrentUserId(userIdentity)).thenReturn(userId);
    }

    @Nested
    class LogDrink {

        @Test
        void logs_catalog_drink_successfully() {
            // Arrange
            UUID catalogId = UUID.randomUUID();

            DrinkCatalogItem item = mock(DrinkCatalogItem.class);
            when(item.getId()).thenReturn(catalogId);
            when(item.getName()).thenReturn("Stella Artois");
            when(item.getCategory()).thenReturn(DrinkCategory.BEER);

            when(drinkCatalogItemRepository.findById(catalogId)).thenReturn(Optional.of(item));
            when(drinkLogRepository.save(any(DrinkLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LogDrinkRequest request = new LogDrinkRequest(
                    catalogId, null, null, null, null, 1, 250, null, null, null, null, null, null
            );

            // Act
            DrinkLog result = service.logDrink(userIdentity, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
            verify(drinkLogRepository).save(any(DrinkLog.class));

            verify(eventPublisher).publishEvent(any(DrinkLogCreatedEvent.class));
        }

        @Test
        void logs_custom_drink_successfully() {
            // Arrange
            when(drinkLogRepository.save(any(DrinkLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LogDrinkRequest request = new LogDrinkRequest(
                    null, "Homebrew IPA", DrinkCategory.BEER, "West Coast", null,
                    1, 330, new BigDecimal("7.5"), 5, "Very hoppy", "http://image.url", 51.2194, 4.4025
            );

            // Act
            DrinkLog result = service.logDrink(userIdentity, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getIdentity().getCustomDrinkName()).isEqualTo("Homebrew IPA");
            verify(drinkLogRepository).save(any(DrinkLog.class));

            // Consistent fix for all creation tests
            verify(eventPublisher).publishEvent(any(DrinkLogCreatedEvent.class));
        }
    }
}
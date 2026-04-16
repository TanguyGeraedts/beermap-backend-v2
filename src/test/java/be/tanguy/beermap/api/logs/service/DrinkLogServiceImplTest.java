package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.domain.DrinkLog;
import be.tanguy.beermap.api.logs.repository.DrinkCatalogItemRepository;
import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;
import be.tanguy.beermap.api.logs.service.command.LogDrinkCommand;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrinkLogServiceImplTest {

    @Mock private DrinkLogRepository drinkLogRepository;
    @Mock private DrinkCatalogItemRepository drinkCatalogItemRepository;

    @InjectMocks
    private DrinkLogServiceImpl service;

    @Nested
    class LogDrink {

        @Test
        void logs_catalog_drink_successfully() {
            // Arrange
            UUID userId = UUID.randomUUID();
            UUID catalogId = UUID.randomUUID();

            DrinkCatalogItem item = mock(DrinkCatalogItem.class);
            when(item.getId()).thenReturn(catalogId);
            when(item.getName()).thenReturn("Stella Artois");
            when(item.getCategory()).thenReturn(DrinkCategory.BEER);

            when(drinkCatalogItemRepository.findById(catalogId)).thenReturn(Optional.of(item));

            // FIX: Instead of returning a mock(DrinkLog.class), we return the actual
            // object that was passed into the save method. This ensures getIdentity() is NOT null.
            when(drinkLogRepository.save(any(DrinkLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LogDrinkCommand cmd = LogDrinkCommand.builder()
                    .userId(userId)
                    .catalogItemId(catalogId)
                    .quantity(1)
                    .build();

            // Act
            DrinkLog result = service.logDrink(cmd);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getIdentity().getCategory()).isEqualTo(DrinkCategory.BEER);
            verify(drinkLogRepository).save(any(DrinkLog.class));
        }

        @Test
        void logs_custom_drink_successfully() {
            // Arrange
            // FIX: Use thenAnswer to return the real object being saved
            when(drinkLogRepository.save(any(DrinkLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LogDrinkCommand cmd = LogDrinkCommand.builder()
                    .userId(UUID.randomUUID())
                    .customDrinkName("Homebrew IPA")
                    .category(DrinkCategory.BEER)
                    .quantity(1)
                    .build();

            // Act
            DrinkLog result = service.logDrink(cmd);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getIdentity().getCategory()).isEqualTo(DrinkCategory.BEER);
            verify(drinkLogRepository).save(any(DrinkLog.class));
        }
    }
}
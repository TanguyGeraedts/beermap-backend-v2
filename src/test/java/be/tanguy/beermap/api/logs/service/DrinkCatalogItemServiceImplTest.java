package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemAlreadyExistsException;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemNotFoundException;
import be.tanguy.beermap.api.logs.exception.InvalidDrinkCatalogItemException;
import be.tanguy.beermap.api.logs.repository.DrinkCatalogItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrinkCatalogItemServiceImplTest {

    @Mock
    private DrinkCatalogItemRepository drinkCatalogItemRepository;

    @InjectMocks
    private DrinkCatalogItemServiceImpl drinkCatalogItemService;

    @Test
    void create_shouldCreateItem_whenNameDoesNotExist() {
        when(drinkCatalogItemRepository.existsByNormalizedName("duvel")).thenReturn(false);
        when(drinkCatalogItemRepository.save(any(DrinkCatalogItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DrinkCatalogItem result = drinkCatalogItemService.create(
                "  Duvel  ",
                DrinkCategory.BEER,
                " Blonde ",
                new BigDecimal("8.5")
        );

        assertNotNull(result);
        assertEquals("Duvel", result.getName());
        assertEquals("duvel", result.getNormalizedName());
        assertEquals(DrinkCategory.BEER, result.getCategory());
        verify(drinkCatalogItemRepository).save(any(DrinkCatalogItem.class));
    }

    @Test
    void create_shouldThrow_whenNameAlreadyExists() {
        when(drinkCatalogItemRepository.existsByNormalizedName("duvel")).thenReturn(true);

        assertThrows(DrinkCatalogItemAlreadyExistsException.class, () ->
                drinkCatalogItemService.create(
                        "Duvel",
                        DrinkCategory.BEER,
                        "Blonde",
                        new BigDecimal("8.5")
                )
        );

        verify(drinkCatalogItemRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenNameIsBlank() {
        assertThrows(InvalidDrinkCatalogItemException.class, () ->
                drinkCatalogItemService.create(
                        "   ",
                        DrinkCategory.BEER,
                        null,
                        null
                )
        );

        verify(drinkCatalogItemRepository, never()).existsByNormalizedName(any());
        verify(drinkCatalogItemRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnItem_whenFound() {
        UUID id = UUID.randomUUID();
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, "Blonde", new BigDecimal("8.5"));

        when(drinkCatalogItemRepository.findById(id)).thenReturn(Optional.of(item));

        DrinkCatalogItem result = drinkCatalogItemService.getById(id);

        assertSame(item, result);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(drinkCatalogItemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DrinkCatalogItemNotFoundException.class,
                () -> drinkCatalogItemService.getById(id));
    }

    @Test
    void getByName_shouldReturnItem_whenFound() {
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, "Blonde", new BigDecimal("8.5"));
        when(drinkCatalogItemRepository.findByNormalizedName("duvel")).thenReturn(Optional.of(item));

        DrinkCatalogItem result = drinkCatalogItemService.getByName("  Duvel ");

        assertSame(item, result);
    }

    @Test
    void getByName_shouldThrow_whenNotFound() {
        when(drinkCatalogItemRepository.findByNormalizedName("duvel")).thenReturn(Optional.empty());

        assertThrows(DrinkCatalogItemNotFoundException.class,
                () -> drinkCatalogItemService.getByName("Duvel"));
    }

    @Test
    void update_shouldUpdateItem_whenNewNameIsAvailable() {
        UUID id = UUID.randomUUID();
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, "Blonde", new BigDecimal("8.5"));

        setId(item, id);

        when(drinkCatalogItemRepository.findById(id)).thenReturn(Optional.of(item));
        when(drinkCatalogItemRepository.findByNormalizedName("negroni")).thenReturn(Optional.empty());

        DrinkCatalogItem result = drinkCatalogItemService.update(
                id,
                "Negroni",
                DrinkCategory.COCKTAIL,
                "Bitter",
                new BigDecimal("24")
        );

        assertSame(item, result);
        assertEquals("Negroni", result.getName());
        assertEquals("negroni", result.getNormalizedName());
        assertEquals(DrinkCategory.COCKTAIL, result.getCategory());
    }

    @Test
    void update_shouldThrow_whenAnotherItemAlreadyUsesName() {
        UUID id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, "Blonde", new BigDecimal("8.5"));
        DrinkCatalogItem other = DrinkCatalogItem.create("Negroni", DrinkCategory.COCKTAIL, "Bitter", new BigDecimal("24"));

        setId(item, id);
        setId(other, otherId);

        when(drinkCatalogItemRepository.findById(id)).thenReturn(Optional.of(item));
        when(drinkCatalogItemRepository.findByNormalizedName("negroni")).thenReturn(Optional.of(other));

        assertThrows(DrinkCatalogItemAlreadyExistsException.class, () ->
                drinkCatalogItemService.update(
                        id,
                        "Negroni",
                        DrinkCategory.COCKTAIL,
                        "Bitter",
                        new BigDecimal("24")
                )
        );
    }

    @Test
    void getByCategory_shouldReturnItems() {
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, "Blonde", new BigDecimal("8.5"));
        when(drinkCatalogItemRepository.findAllByCategoryOrderByNameAsc(DrinkCategory.BEER))
                .thenReturn(List.of(item));

        List<DrinkCatalogItem> result = drinkCatalogItemService.getByCategory(DrinkCategory.BEER);

        assertEquals(1, result.size());
        assertSame(item, result.get(0));
    }

    @Test
    void getByCategory_shouldThrow_whenCategoryIsNull() {
        assertThrows(InvalidDrinkCatalogItemException.class,
                () -> drinkCatalogItemService.getByCategory(null));
    }

    @Test
    void searchByName_shouldReturnAll_whenQueryIsBlank() {
        DrinkCatalogItem a = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, null, null);
        DrinkCatalogItem b = DrinkCatalogItem.create("Negroni", DrinkCategory.COCKTAIL, null, null);

        when(drinkCatalogItemRepository.findAll()).thenReturn(List.of(b, a));

        List<DrinkCatalogItem> result = drinkCatalogItemService.searchByName("   ");

        assertEquals(2, result.size());
        assertEquals("Duvel", result.get(0).getName());
        assertEquals("Negroni", result.get(1).getName());
    }

    @Test
    void searchByName_shouldSearchUsingNormalizedValue() {
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, null, null);

        when(drinkCatalogItemRepository.findAllByNormalizedNameContainingIgnoreCaseOrderByNameAsc("duv"))
                .thenReturn(List.of(item));

        List<DrinkCatalogItem> result = drinkCatalogItemService.searchByName(" DUV ");

        assertEquals(1, result.size());
        assertSame(item, result.get(0));
    }

    @Test
    void searchBySubType_shouldReturnAll_whenSubTypeIsBlank() {
        DrinkCatalogItem a = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, "Blonde", null);
        DrinkCatalogItem b = DrinkCatalogItem.create("Tripel Karmeliet", DrinkCategory.BEER, "Tripel", null);

        when(drinkCatalogItemRepository.findAll()).thenReturn(List.of(b, a));

        List<DrinkCatalogItem> result = drinkCatalogItemService.searchBySubType("   ");

        assertEquals(2, result.size());
        assertEquals("Duvel", result.get(0).getName());
    }

    @Test
    void searchBySubType_shouldSearchUsingNormalizedValue() {
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, "Belgian Blonde", null);

        when(drinkCatalogItemRepository.findAllByNormalizedSubTypeContainingIgnoreCaseOrderByNameAsc("blonde"))
                .thenReturn(List.of(item));

        List<DrinkCatalogItem> result = drinkCatalogItemService.searchBySubType(" Blonde ");

        assertEquals(1, result.size());
        assertSame(item, result.get(0));
    }

    @Test
    void searchByCategoryAndName_shouldReturnCategoryItems_whenQueryIsBlank() {
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, null, null);

        when(drinkCatalogItemRepository.findAllByCategoryOrderByNameAsc(DrinkCategory.BEER))
                .thenReturn(List.of(item));

        List<DrinkCatalogItem> result = drinkCatalogItemService.searchByCategoryAndName(DrinkCategory.BEER, "   ");

        assertEquals(1, result.size());
        assertSame(item, result.get(0));
    }

    @Test
    void searchByCategoryAndName_shouldThrow_whenCategoryIsNull() {
        assertThrows(InvalidDrinkCatalogItemException.class,
                () -> drinkCatalogItemService.searchByCategoryAndName(null, "duv"));
    }

    @Test
    void delete_shouldDeleteExistingItem() {
        UUID id = UUID.randomUUID();
        DrinkCatalogItem item = DrinkCatalogItem.create("Duvel", DrinkCategory.BEER, null, null);

        when(drinkCatalogItemRepository.findById(id)).thenReturn(Optional.of(item));

        drinkCatalogItemService.delete(id);

        verify(drinkCatalogItemRepository).delete(item);
    }

    private void setId(DrinkCatalogItem item, UUID id) {
        try {
            var field = item.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(item, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
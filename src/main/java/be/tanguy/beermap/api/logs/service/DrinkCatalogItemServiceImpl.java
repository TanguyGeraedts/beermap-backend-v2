package be.tanguy.beermap.api.logs.service;

import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemAlreadyExistsException;
import be.tanguy.beermap.api.logs.exception.DrinkCatalogItemNotFoundException;
import be.tanguy.beermap.api.logs.exception.InvalidDrinkCatalogItemException;
import be.tanguy.beermap.api.logs.repository.DrinkCatalogItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DrinkCatalogItemServiceImpl implements DrinkCatalogItemService {

    private final DrinkCatalogItemRepository drinkCatalogItemRepository;

    @Override
    public DrinkCatalogItem create(
            String name,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    ) {
        String normalizedName = normalizeRequired(name);

        if (drinkCatalogItemRepository.existsByNormalizedName(normalizedName)) {
            throw new DrinkCatalogItemAlreadyExistsException(name);
        }

        DrinkCatalogItem item = DrinkCatalogItem.create(name, category, subType, abvPercent);
        return drinkCatalogItemRepository.save(item);
    }

    @Override
    public DrinkCatalogItem update(
            UUID id,
            String name,
            DrinkCategory category,
            String subType,
            BigDecimal abvPercent
    ) {
        DrinkCatalogItem item = getById(id);

        String normalizedName = normalizeRequired(name);

        drinkCatalogItemRepository.findByNormalizedName(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DrinkCatalogItemAlreadyExistsException(name);
                });

        item.update(name, category, subType, abvPercent);
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkCatalogItem getById(UUID id) {
        return drinkCatalogItemRepository.findById(id)
                .orElseThrow(() -> new DrinkCatalogItemNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkCatalogItem getByName(String name) {
        String normalizedName = normalizeRequired(name);

        return drinkCatalogItemRepository.findByNormalizedName(normalizedName)
                .orElseThrow(() -> new DrinkCatalogItemNotFoundException(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkCatalogItem> getAll() {
        return drinkCatalogItemRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkCatalogItem> getByCategory(DrinkCategory category) {
        if (category == null) {
            throw new InvalidDrinkCatalogItemException("Drink category cannot be null");
        }

        return drinkCatalogItemRepository.findAllByCategoryOrderByNameAsc(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkCatalogItem> searchByName(String query) {
        String normalizedQuery = normalizeOptional(query);

        if (normalizedQuery == null) {
            return getAll();
        }

        return drinkCatalogItemRepository.findAllByNormalizedNameContainingIgnoreCaseOrderByNameAsc(normalizedQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkCatalogItem> searchBySubType(String subType) {
        String normalizedSubType = normalizeOptional(subType);

        if (normalizedSubType == null) {
            return getAll();
        }

        return drinkCatalogItemRepository
                .findAllByNormalizedSubTypeContainingIgnoreCaseOrderByNameAsc(normalizedSubType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkCatalogItem> searchByCategoryAndName(DrinkCategory category, String query) {
        if (category == null) {
            throw new InvalidDrinkCatalogItemException("Drink category cannot be null");
        }

        String normalizedQuery = normalizeOptional(query);

        if (normalizedQuery == null) {
            return getByCategory(category);
        }

        return drinkCatalogItemRepository.findAllByCategoryAndNormalizedNameContainingIgnoreCaseOrderByNameAsc(
                category,
                normalizedQuery
        );
    }

    @Override
    public void delete(UUID id) {
        DrinkCatalogItem item = getById(id);
        drinkCatalogItemRepository.delete(item);
    }

    private String normalizeRequired(String value) {
        if (value == null) {
            throw new InvalidDrinkCatalogItemException("Drink name cannot be blank");
        }

        String cleaned = value.trim();
        if (cleaned.isEmpty()) {
            throw new InvalidDrinkCatalogItemException("Drink name cannot be blank");
        }

        return cleaned.toLowerCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        if (cleaned.isEmpty()) {
            return null;
        }

        return cleaned.toLowerCase(Locale.ROOT);
    }
}
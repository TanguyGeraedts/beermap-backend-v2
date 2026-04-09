package be.tanguy.beermap.api.logs.repository;

import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DrinkCatalogItemRepository extends JpaRepository<DrinkCatalogItem, UUID> {

    Optional<DrinkCatalogItem> findByNormalizedName(String normalizedName);

    boolean existsByNormalizedName(String normalizedName);

    List<DrinkCatalogItem> findAllByCategoryOrderByNameAsc(DrinkCategory category);

    List<DrinkCatalogItem> findAllByNormalizedNameContainingIgnoreCaseOrderByNameAsc(String normalizedName);

    List<DrinkCatalogItem> findAllByNormalizedSubTypeContainingIgnoreCaseOrderByNameAsc(String normalizedSubType);

    List<DrinkCatalogItem> findAllByCategoryAndNormalizedNameContainingIgnoreCaseOrderByNameAsc(
            DrinkCategory category,
            String normalizedName
    );
}
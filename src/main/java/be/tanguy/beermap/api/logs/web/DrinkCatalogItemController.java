package be.tanguy.beermap.api.logs.web;

import be.tanguy.beermap.api.common.security.IsAdmin;
import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.logs.domain.DrinkCatalogItem;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import be.tanguy.beermap.api.logs.service.DrinkCatalogItemService;
import be.tanguy.beermap.api.logs.web.dto.CreateDrinkCatalogItemRequest;
import be.tanguy.beermap.api.logs.web.dto.DrinkCatalogItemResponse;
import be.tanguy.beermap.api.logs.web.dto.UpdateDrinkCatalogItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.Catalog.Drinks.BASE)
@RequiredArgsConstructor
@Validated
public class DrinkCatalogItemController {

    private final DrinkCatalogItemService drinkCatalogItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
   @IsAdmin
    public DrinkCatalogItemResponse create(@Valid @RequestBody CreateDrinkCatalogItemRequest request) {
        return DrinkCatalogItemResponse.from(
                drinkCatalogItemService.create(
                        request.name(),
                        request.category(),
                        request.subType(),
                        request.abvPercent()
                )
        );
    }

    @PutMapping("/{id}")
    @IsAdmin
    public DrinkCatalogItemResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDrinkCatalogItemRequest request
    ) {
        return DrinkCatalogItemResponse.from(
                drinkCatalogItemService.update(
                        id,
                        request.name(),
                        request.category(),
                        request.subType(),
                        request.abvPercent()
                )
        );
    }

    @GetMapping("/{id}")
    public DrinkCatalogItemResponse getById(@PathVariable UUID id) {
        return DrinkCatalogItemResponse.from(drinkCatalogItemService.getById(id));
    }

    @GetMapping
    public List<DrinkCatalogItemResponse> getAll() {
        return drinkCatalogItemService.getAll()
                .stream()
                .map(DrinkCatalogItemResponse::from)
                .toList();
    }

    @GetMapping(ApiPaths.Catalog.Drinks.BY_NAME)
    public DrinkCatalogItemResponse getByName(@RequestParam @NotBlank String name) {
        return DrinkCatalogItemResponse.from(drinkCatalogItemService.getByName(name));
    }

    @GetMapping(ApiPaths.Catalog.Drinks.BY_CATEGORY)
    public List<DrinkCatalogItemResponse> getByCategory(@RequestParam @NotNull DrinkCategory category) {
        return drinkCatalogItemService.getByCategory(category)
                .stream()
                .map(DrinkCatalogItemResponse::from)
                .toList();
    }

    @GetMapping(ApiPaths.Catalog.Drinks.SEARCH_NAME)
    public List<DrinkCatalogItemResponse> searchByName(@RequestParam(required = false) String query) {
        return drinkCatalogItemService.searchByName(query)
                .stream()
                .map(DrinkCatalogItemResponse::from)
                .toList();
    }

    @GetMapping(ApiPaths.Catalog.Drinks.SEARCH_SUB_TYPE)
    public List<DrinkCatalogItemResponse> searchBySubType(@RequestParam(required = false) String subType) {
        return drinkCatalogItemService.searchBySubType(subType)
                .stream()
                .map(DrinkCatalogItemResponse::from)
                .toList();
    }

    @GetMapping(ApiPaths.Catalog.Drinks.SEARCH_CATEGORY_NAME)
    public List<DrinkCatalogItemResponse> searchByCategoryAndName(
            @RequestParam @NotNull DrinkCategory category,
            @RequestParam(required = false) String query
    ) {
        return drinkCatalogItemService.searchByCategoryAndName(category, query)
                .stream()
                .map(DrinkCatalogItemResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsAdmin
    public void delete(@PathVariable UUID id) {
        drinkCatalogItemService.delete(id);
    }

}
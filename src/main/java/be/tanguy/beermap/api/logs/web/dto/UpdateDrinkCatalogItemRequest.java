package be.tanguy.beermap.api.logs.web.dto;

import be.tanguy.beermap.api.common.validation.AllowedContent;
import be.tanguy.beermap.api.logs.domain.DrinkCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateDrinkCatalogItemRequest(
        @NotBlank @AllowedContent String name,
        @NotNull DrinkCategory category,
        @AllowedContent
        String subType,
        BigDecimal abvPercent
) {}

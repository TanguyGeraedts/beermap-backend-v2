package be.tanguy.beermap.api.common.meta.dto;

import be.tanguy.beermap.api.common.meta.ChangelogProperties;

import java.util.List;

public record ChangelogResponse(
        List<ChangelogProperties.ChangelogEntry> entries
) {}
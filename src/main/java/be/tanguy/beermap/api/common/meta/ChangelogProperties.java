package be.tanguy.beermap.api.common.meta;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "changelog")
public record ChangelogProperties(List<ChangelogEntry> entries) {

    public record ChangelogEntry(
            String version,
            String date,
            List<String> changes
    ) {}
}
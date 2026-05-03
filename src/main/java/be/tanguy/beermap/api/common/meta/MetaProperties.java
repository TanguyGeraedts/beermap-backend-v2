package be.tanguy.beermap.api.common.meta;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.application")
public record MetaProperties(String name, String version) {}
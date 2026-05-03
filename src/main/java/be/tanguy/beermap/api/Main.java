package be.tanguy.beermap.api;

import be.tanguy.beermap.api.common.meta.ChangelogProperties;
import be.tanguy.beermap.api.common.meta.MetaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ MetaProperties.class, ChangelogProperties.class })
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}

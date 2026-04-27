package be.tanguy.beermap.api.feed.web;

import be.tanguy.beermap.api.common.web.ApiPaths;
import be.tanguy.beermap.api.feed.domain.FeedItemType;
import be.tanguy.beermap.api.feed.domain.FeedTargetType;
import be.tanguy.beermap.api.feed.domain.FeedVisibility;
import be.tanguy.beermap.api.feed.service.FeedItemService;
import be.tanguy.beermap.api.feed.web.dto.FeedItemResponse;
import be.tanguy.beermap.api.feed.web.dto.FeedPageResponse;
import be.tanguy.beermap.api.users.repository.AuthIdentityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(
        controllers = FeedController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "be\\.tanguy\\.beermap\\.api\\.common\\.security\\..*"
                )
        }
)
class FeedControllerTest {

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            http.csrf(csrf -> csrf.disable());
            return http.build();
        }
    }

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private FeedItemService feedItemService;

    @MockitoBean
    private AuthIdentityRepository authIdentityRepository;

    private final String BASE_URL = ApiPaths.Feed.BASE;

    @Test
    @DisplayName("GET /feed - Should return first page of feed items")
    void getFeed_FirstPage_Success() {
        UUID feedItemId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();

        Instant createdAt = Instant.parse("2026-04-25T10:48:28.120Z");

        FeedItemResponse item = new FeedItemResponse(
                feedItemId,
                userId,
                FeedItemType.DRINK_LOG_CREATED,
                FeedTargetType.DRINK_LOG,
                logId,
                FeedVisibility.PUBLIC,
                null,
                createdAt,
                null
        );

        FeedPageResponse response = new FeedPageResponse(
                List.of(item),
                createdAt,
                true
        );

        when(feedItemService.getFeed(null, 20)).thenReturn(response);

        mockMvcTester.get().uri(BASE_URL)
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "items": [
                            {
                              "type": "DRINK_LOG_CREATED",
                              "targetType": "DRINK_LOG",
                              "visibility": "PUBLIC"
                            }
                          ],
                          "nextCursor": "2026-04-25T10:48:28.120Z",
                          "hasMore": true
                        }
                        """);
    }

    @Test
    @DisplayName("GET /feed?cursor=&limit= - Should return next page of feed items")
    void getFeed_WithCursor_Success() {
        Instant cursor = Instant.parse("2026-04-25T10:48:28.120Z");
        Instant createdAt = Instant.parse("2026-04-25T10:30:00Z");

        FeedItemResponse item = new FeedItemResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                FeedItemType.DRINK_LOG_CREATED,
                FeedTargetType.DRINK_LOG,
                UUID.randomUUID(),
                FeedVisibility.PUBLIC,
                null,
                createdAt,
                null
        );

        FeedPageResponse response = new FeedPageResponse(
                List.of(item),
                createdAt,
                false
        );

        when(feedItemService.getFeed(cursor, 10)).thenReturn(response);

        mockMvcTester.get()
                .uri(BASE_URL + "?cursor={cursor}&limit={limit}", cursor, 10)
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "items": [
                            {
                              "type": "DRINK_LOG_CREATED",
                              "targetType": "DRINK_LOG",
                              "visibility": "PUBLIC"
                            }
                          ],
                          "nextCursor": "2026-04-25T10:30:00Z",
                          "hasMore": false
                        }
                        """);
    }

    @Test
    @DisplayName("GET /feed - Should return empty feed page")
    void getFeed_Empty_Success() {
        FeedPageResponse response = new FeedPageResponse(
                List.of(),
                null,
                false
        );

        when(feedItemService.getFeed(null, 20)).thenReturn(response);

        mockMvcTester.get().uri(BASE_URL)
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "items": [],
                          "nextCursor": null,
                          "hasMore": false
                        }
                        """);
    }
}

package be.tanguy.beermap.api.feed.web.dto;

public record FeedItemDetailsResponse(
        FeedActorResponse actor,
        FeedDrinkLogDetailsResponse drinkLog
) {
}

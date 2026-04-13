package be.tanguy.beermap.api.feed.event;

import be.tanguy.beermap.api.friends.event.FriendshipAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipFeedListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFriendshipAccepted(FriendshipAcceptedEvent event) {
        log.info("New friendship: {} ↔ {}", event.requesterId(), event.addresseeId());

        // FeedEntry entry = FeedEntry.createNewFriend(event.requesterId(), event.addresseeId());
        // feedEntryRepository.save(entry);
    }
}
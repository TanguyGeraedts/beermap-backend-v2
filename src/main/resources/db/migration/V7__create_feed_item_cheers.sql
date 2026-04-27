CREATE TABLE feed_item_cheers (
                                  id UUID PRIMARY KEY,
                                  feed_item_id UUID NOT NULL,
                                  user_id UUID NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                  CONSTRAINT uq_feed_item_cheer_user UNIQUE (feed_item_id, user_id)
);

CREATE INDEX idx_feed_item_cheers_feed_item
    ON feed_item_cheers (feed_item_id);

CREATE INDEX idx_feed_item_cheers_user
    ON feed_item_cheers (user_id);

CREATE INDEX idx_feed_item_cheers_created_at
    ON feed_item_cheers (created_at);

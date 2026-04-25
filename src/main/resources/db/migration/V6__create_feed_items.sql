CREATE TABLE feed_items (
                            id UUID PRIMARY KEY,

                            user_id UUID NOT NULL,

                            type VARCHAR(50) NOT NULL,

                            target_type VARCHAR(50) NOT NULL,

                            target_id UUID NOT NULL,

                            visibility VARCHAR(50) NOT NULL,

                            metadata_json TEXT,

                            created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                            updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_feed_item_user
    ON feed_items (user_id);

CREATE INDEX idx_feed_item_type
    ON feed_items (type);

CREATE INDEX idx_feed_item_target
    ON feed_items (target_type, target_id);

CREATE INDEX idx_feed_item_visibility
    ON feed_items (visibility);

CREATE INDEX idx_feed_item_created_at
    ON feed_items (created_at);
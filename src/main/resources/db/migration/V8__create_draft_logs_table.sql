CREATE TABLE draft_logs (
                            id UUID PRIMARY KEY,
                            version BIGINT NOT NULL,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                            user_id UUID NOT NULL,
                            captured_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            photo_url VARCHAR(2048),
                            status VARCHAR(20) NOT NULL,

                            CONSTRAINT fk_draft_logs_user_profile
                                FOREIGN KEY (user_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_draft_user_time ON draft_logs (user_id, captured_at);
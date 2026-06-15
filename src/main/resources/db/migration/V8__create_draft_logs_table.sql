CREATE TABLE draft_logs (
                            id UUID PRIMARY KEY,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                            user_id UUID NOT NULL,
                            captured_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            photo_url VARCHAR(2048),
                            status VARCHAR(20) NOT NULL,

                            -- Embedded Location fields
                            latitude DOUBLE PRECISION,
                            longitude DOUBLE PRECISION,

                            CONSTRAINT fk_draft_logs_user_profile
                                FOREIGN KEY (user_id) REFERENCES user_profiles(id) ON DELETE CASCADE,

                            -- Optional: Database-level integrity check matching your Java logic
                            CONSTRAINT chk_location_together
                                CHECK ((latitude IS NULL AND longitude IS NULL) OR (latitude IS NOT NULL AND longitude IS NOT NULL))
);

CREATE INDEX idx_draft_user_time ON draft_logs (user_id, captured_at);
CREATE TABLE friendships (
                             id UUID PRIMARY KEY,
                             created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                             updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                             requester_id UUID NOT NULL,
                             addressee_id UUID NOT NULL,

                             status VARCHAR(32) NOT NULL,
                             responded_at TIMESTAMP WITH TIME ZONE NULL,

                             CONSTRAINT uk_friendship_requester_addressee
                                 UNIQUE (requester_id, addressee_id),

                             CONSTRAINT fk_friendship_requester
                                 FOREIGN KEY (requester_id) REFERENCES user_profiles (id),

                             CONSTRAINT fk_friendship_addressee
                                 FOREIGN KEY (addressee_id) REFERENCES user_profiles (id),

                             CONSTRAINT chk_friendship_status
                                 CHECK (status IN (
                                                   'PENDING',
                                                   'ACCEPTED',
                                                   'REJECTED',
                                                   'CANCELLED',
                                                   'REMOVED',
                                                   'BLOCKED'
                                     )),

                             CONSTRAINT chk_friendship_not_self
                                 CHECK (requester_id <> addressee_id)
);

CREATE INDEX idx_friendship_requester
    ON friendships (requester_id);

CREATE INDEX idx_friendship_addressee
    ON friendships (addressee_id);

CREATE INDEX idx_friendship_status
    ON friendships (status);

CREATE INDEX idx_friendship_requester_addressee
    ON friendships (requester_id, addressee_id);
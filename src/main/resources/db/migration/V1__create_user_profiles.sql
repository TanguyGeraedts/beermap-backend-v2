CREATE TABLE user_profiles (
                               id UUID PRIMARY KEY,

                               username VARCHAR(32) NOT NULL,
                               username_normalized VARCHAR(32) NOT NULL,
                               discriminator VARCHAR(4) NOT NULL,

                               bio VARCHAR(500),
                               avatar_url VARCHAR(2048),

                               account_type VARCHAR(20) NOT NULL,

                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                               CONSTRAINT uk_user_profiles_username_normalized_discriminator
                                   UNIQUE (username_normalized, discriminator)
);

CREATE TABLE auth_identities (
                                 id UUID PRIMARY KEY,

                                 issuer VARCHAR(255) NOT NULL,
                                 subject VARCHAR(255) NOT NULL,

                                 user_profile_id UUID NOT NULL,

                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                 CONSTRAINT fk_auth_identities_user_profile
                                     FOREIGN KEY (user_profile_id)
                                         REFERENCES user_profiles(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT uk_auth_identities_issuer_subject
                                     UNIQUE (issuer, subject)
);
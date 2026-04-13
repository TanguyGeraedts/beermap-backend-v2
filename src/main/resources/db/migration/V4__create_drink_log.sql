CREATE TABLE drink_log (
    -- Identity & Base Object fields
                           id UUID PRIMARY KEY,
                           user_id UUID NOT NULL,

    -- Inherited from AbstractBaseObject (assuming standard audit fields)
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- DrinkIdentity (Embedded)
                           category VARCHAR(50) NOT NULL,
                           sub_type VARCHAR(100),
                           catalog_item_id UUID,
                           catalog_item_name VARCHAR(255),
                           custom_drink_name VARCHAR(255),
                           abv_percent NUMERIC(5,2),

    -- ConsumptionDetails (Embedded)
                           consumed_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           quantity INTEGER NOT NULL DEFAULT 1,
                           volume_ml INTEGER,
                           rating INTEGER,
                           description VARCHAR(2000),
                           photo_url VARCHAR(2048),

    -- Location (Embedded)
                           latitude DOUBLE PRECISION,
                           longitude DOUBLE PRECISION,

    -- Constraints
                           CONSTRAINT chk_drink_log_category
                               CHECK (category IN ('BEER', 'WINE', 'COCKTAIL', 'SPIRITS', 'OTHER')),

                           CONSTRAINT chk_drink_log_quantity
                               CHECK (quantity > 0),

                           CONSTRAINT chk_drink_log_rating
                               CHECK (rating IS NULL OR (rating >= 1 AND rating <= 10)),

                           CONSTRAINT chk_drink_log_location_integrity
                               CHECK ((latitude IS NULL AND longitude IS NULL) OR (latitude IS NOT NULL AND longitude IS NOT NULL)),

    -- Identity constraint: Must be either catalog-backed OR custom
                           CONSTRAINT chk_drink_log_identity_source
                               CHECK (
                                   (catalog_item_id IS NOT NULL AND catalog_item_name IS NOT NULL AND custom_drink_name IS NULL)
                                       OR
                                   (catalog_item_id IS NULL AND catalog_item_name IS NULL AND custom_drink_name IS NOT NULL)
                                   )
);

-- Indexes
CREATE INDEX idx_drink_log_user_id ON drink_log (user_id);
CREATE INDEX idx_drink_log_consumed_at ON drink_log (consumed_at);
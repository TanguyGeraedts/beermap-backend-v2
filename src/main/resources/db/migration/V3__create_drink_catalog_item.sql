CREATE TABLE drink_catalog_item (
                                    id UUID PRIMARY KEY,

                                    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                    name VARCHAR(255) NOT NULL,
                                    normalized_name VARCHAR(255) NOT NULL,

                                    category VARCHAR(30) NOT NULL,

                                    sub_type VARCHAR(100),
                                    normalized_sub_type VARCHAR(100),

                                    abv_percent NUMERIC(5,2),

                                    CONSTRAINT uq_drink_catalog_item_normalized_name
                                        UNIQUE (normalized_name),

                                    CONSTRAINT chk_drink_catalog_item_category
                                        CHECK (category IN (
                                                            'BEER',
                                                            'WINE',
                                                            'COCKTAIL',
                                                            'SPIRITS',
                                                            'OTHER'
                                            )),

                                    CONSTRAINT chk_drink_catalog_item_abv
                                        CHECK (
                                            abv_percent IS NULL
                                                OR (abv_percent >= 0 AND abv_percent <= 100)
                                            )
);

CREATE INDEX idx_drink_catalog_item_normalized_name
    ON drink_catalog_item (normalized_name);

CREATE INDEX idx_drink_catalog_item_normalized_sub_type
    ON drink_catalog_item (normalized_sub_type);
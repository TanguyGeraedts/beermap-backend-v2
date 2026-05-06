-- Seed logs for user: d2e8ad81-55e0-4ba7-9867-194a71cc7dc1
-- Target table: drink_log
-- Safe to re-run: this script removes previous seeded rows by these IDs first.

BEGIN;

DELETE FROM drink_log
WHERE id IN (
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e01',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e02',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e03',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e04',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e05',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e06',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e07',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e08',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e09',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0a',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0b',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0c',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0d',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0e',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0f',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e10',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e11',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e12',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e13',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e14',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e15',
             '7d140ab0-6c8c-4bba-9f3e-5b46a08f5e16'
    );

INSERT INTO drink_log (
    id,
    user_id,
    created_at,
    updated_at,
    category,
    sub_type,
    catalog_item_id,
    catalog_item_name,
    custom_drink_name,
    abv_percent,
    consumed_at,
    quantity,
    volume_ml,
    rating,
    description,
    photo_url,
    latitude,
    longitude
)
VALUES
-- BEER
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e01','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-12T20:15:00Z','2026-04-12T20:15:00Z','BEER','IPA',NULL,NULL,'Neon IPA',6.80,'2026-04-12T20:05:00Z',2,330,8,'Great hop aroma',NULL,50.8503,4.3517),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e02','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-14T21:50:00Z','2026-04-14T21:50:00Z','BEER','TRIPEL',NULL,NULL,'Westmalle Tripel',9.50,'2026-04-14T21:40:00Z',1,330,9,'Classic Belgian tripel','https://images.example.com/beers/westmalle-tripel.jpg',50.8467,4.3525),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e03','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-19T18:20:00Z','2026-04-19T18:20:00Z','BEER','PILSNER',NULL,NULL,'Crisp Pils',5.10,'2026-04-19T18:10:00Z',1,500,7,NULL,NULL,50.8388,4.3495),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e04','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-22T22:40:00Z','2026-04-22T22:40:00Z','BEER','STOUT',NULL,NULL,'Midnight Stout',8.20,'2026-04-22T22:30:00Z',1,330,8,'Coffee notes',NULL,NULL,NULL),

-- WINE
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e05','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-16T19:45:00Z','2026-04-16T19:45:00Z','WINE','RED',NULL,NULL,'Rioja Reserva',13.50,'2026-04-16T19:30:00Z',1,150,8,'Smooth and spicy','https://images.example.com/wine/rioja.jpg',50.8479,4.3583),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e06','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-25T20:10:00Z','2026-04-25T20:10:00Z','WINE','WHITE',NULL,NULL,'Sauvignon Blanc',12.00,'2026-04-25T20:00:00Z',2,150,7,NULL,NULL,NULL,NULL),

-- COCKTAIL
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e07','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-18T23:10:00Z','2026-04-18T23:10:00Z','COCKTAIL','SPRITZ',NULL,NULL,'Aperol Spritz',11.00,'2026-04-18T22:55:00Z',1,250,7,'Sunny terrace drink',NULL,50.8509,4.3453),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e08','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-27T00:05:00Z','2026-04-27T00:05:00Z','COCKTAIL','SOUR',NULL,NULL,'Whiskey Sour',13.00,'2026-04-26T23:50:00Z',1,180,8,'Foamy top, balanced sour','https://images.example.com/cocktails/whiskey-sour.jpg',50.8451,4.3572),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e09','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-01T21:35:00Z','2026-05-01T21:35:00Z','COCKTAIL','GIN',NULL,NULL,'Negroni',24.00,'2026-05-01T21:20:00Z',1,120,9,NULL,NULL,NULL,NULL),

-- SPIRITS
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0a','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-04-29T22:20:00Z','2026-04-29T22:20:00Z','SPIRITS','WHISKY',NULL,NULL,'Islay Single Malt',46.00,'2026-04-29T22:10:00Z',1,40,9,'Smoky and peaty',NULL,50.8542,4.3610),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0b','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-03T23:40:00Z','2026-05-03T23:40:00Z','SPIRITS','RUM',NULL,NULL,'Dark Rum',40.00,'2026-05-03T23:25:00Z',2,40,7,NULL,NULL,NULL,NULL),

-- OTHER
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0c','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-05T17:05:00Z','2026-05-05T17:05:00Z','OTHER','CIDER',NULL,NULL,'Dry Apple Cider',4.50,'2026-05-05T17:00:00Z',1,330,6,'Refreshing after work',NULL,50.8500,4.3470),

-- STELLA ARTOIS x10 (to make favorite drink clearly beer)
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0d','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-06T18:10:00Z','2026-05-06T18:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-06T18:00:00Z',1,330,7,NULL,NULL,50.8501,4.3501),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0e','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-06T19:10:00Z','2026-05-06T19:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-06T19:00:00Z',1,330,7,NULL,NULL,50.8502,4.3502),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e0f','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-06T20:10:00Z','2026-05-06T20:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-06T20:00:00Z',1,330,8,NULL,NULL,50.8503,4.3503),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e10','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-06T21:10:00Z','2026-05-06T21:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-06T21:00:00Z',1,330,8,NULL,NULL,50.8504,4.3504),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e11','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-07T18:10:00Z','2026-05-07T18:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-07T18:00:00Z',1,330,7,NULL,NULL,50.8505,4.3505),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e12','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-07T19:10:00Z','2026-05-07T19:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-07T19:00:00Z',1,330,7,NULL,NULL,50.8506,4.3506),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e13','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-07T20:10:00Z','2026-05-07T20:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-07T20:00:00Z',1,330,8,NULL,NULL,50.8507,4.3507),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e14','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-07T21:10:00Z','2026-05-07T21:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-07T21:00:00Z',1,330,8,NULL,NULL,50.8508,4.3508),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e15','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-08T19:10:00Z','2026-05-08T19:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-08T19:00:00Z',1,330,7,NULL,NULL,50.8509,4.3509),
('7d140ab0-6c8c-4bba-9f3e-5b46a08f5e16','d2e8ad81-55e0-4ba7-9867-194a71cc7dc1','2026-05-08T20:10:00Z','2026-05-08T20:10:00Z','BEER','PILSNER',NULL,NULL,'Stella Artois',5.20,'2026-05-08T20:00:00Z',1,330,8,'House lager baseline',NULL,50.8510,4.3510);

COMMIT;

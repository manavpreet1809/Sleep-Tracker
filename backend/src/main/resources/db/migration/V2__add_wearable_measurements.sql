-- Measurements imported from wearables such as Garmin. Null for nights logged by hand.
ALTER TABLE sleep_night ADD COLUMN sleep_score INTEGER CHECK (sleep_score BETWEEN 0 AND 100);
ALTER TABLE sleep_night ADD COLUMN resting_heart_rate INTEGER;
ALTER TABLE sleep_night ADD COLUMN hrv_ms INTEGER;

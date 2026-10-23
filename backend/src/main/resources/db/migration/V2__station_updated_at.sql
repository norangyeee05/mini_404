ALTER TABLE station
    ADD COLUMN IF NOT EXISTS updated_at timestamp;

UPDATE station
SET updated_at = CURRENT_TIMESTAMP
WHERE updated_at IS NULL;

-- Add the timestamp to pre-existing legacy station tables and initialize only
-- rows whose timestamp is missing. On a fresh database, Hibernate creates the
-- table and mapped column after Flyway runs, so there is nothing to alter here.
DO $$
BEGIN
    IF to_regclass('public.station') IS NOT NULL THEN
        ALTER TABLE public.station
            ADD COLUMN IF NOT EXISTS updated_at timestamp;

        UPDATE public.station
        SET updated_at = CURRENT_TIMESTAMP
        WHERE updated_at IS NULL;
    END IF;
END $$;

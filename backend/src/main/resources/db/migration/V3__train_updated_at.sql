-- Initialize timestamps for legacy or SQL-seeded trains that bypassed JPA callbacks.
DO $$
BEGIN
    IF to_regclass('public.train') IS NOT NULL THEN
        ALTER TABLE public.train
            ADD COLUMN IF NOT EXISTS updated_at timestamp;

        UPDATE public.train
        SET updated_at = COALESCE(updated_at, registered_at::timestamp, CURRENT_TIMESTAMP)
        WHERE updated_at IS NULL;
    END IF;
END $$;

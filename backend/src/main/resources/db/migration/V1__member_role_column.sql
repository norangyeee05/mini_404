-- Add member role support to existing installations without changing member data.
-- Existing members receive USER; existing values remain untouched.
ALTER TABLE IF EXISTS public.member
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';

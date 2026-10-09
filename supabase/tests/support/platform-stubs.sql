-- Platform stubs for acceptance testing against a stock postgres:15.
-- A real Supabase project provides auth.*, storage.* and gen_random_uuid;
-- a stock PostgreSQL only has pgcrypto. These stubs create the minimum
-- surface that migrations 001-006 reference.
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE SCHEMA IF NOT EXISTS auth;
CREATE TABLE IF NOT EXISTS auth.users(id uuid PRIMARY KEY);
CREATE OR REPLACE FUNCTION auth.uid() RETURNS uuid
    LANGUAGE sql STABLE AS $$ SELECT NULL::uuid $$;
CREATE SCHEMA IF NOT EXISTS storage;
CREATE TABLE IF NOT EXISTS storage.buckets(
    id text PRIMARY KEY,
    name text,
    public boolean
);
CREATE TABLE IF NOT EXISTS storage.objects(
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bucket_id text,
    name text
);
CREATE OR REPLACE FUNCTION storage.foldername(name text) RETURNS text[]
    LANGUAGE sql IMMUTABLE AS $$ SELECT string_to_array(name, '/') $$;
INSERT INTO auth.users(id)
    SELECT gen_random_uuid()
    WHERE NOT EXISTS (SELECT 1 FROM auth.users);

-- Platform stubs for acceptance testing against a stock postgres:15.
-- A real Supabase project provides auth.*, storage.* and gen_random_uuid;
-- a stock PostgreSQL only has pgcrypto. These stubs create the minimum
-- surface that migrations 001-006 reference.
--
-- auth.uid() lee el claim de sesión EXACTAMENTE como Supabase real:
-- una prueba puede actuar como cualquier usuario con
--   SELECT set_config('request.jwt.claim.sub', '<uuid>', false);
-- y SET ROLE <rol no superusuario>. Sin claim → NULL (anónimo), igual
-- que en producción. Esto permite probar el COMPORTAMIENTO real de las
-- políticas RLS contra postgres:15 stock (no solo su sintaxis).
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE SCHEMA IF NOT EXISTS auth;
CREATE TABLE IF NOT EXISTS auth.users(id uuid PRIMARY KEY);
CREATE OR REPLACE FUNCTION auth.uid() RETURNS uuid
    LANGUAGE sql STABLE AS $$
        SELECT nullif(current_setting('request.jwt.claim.sub', true), '')::uuid
    $$;
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

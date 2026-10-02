#!/usr/bin/env bash
# Acceptance harness for supabase/migrations/001-006 + supabase/tests/006_*.
#
# A stock postgres:15 has no Supabase platform schemas, so this harness stubs
# the minimum surface migrations 001 reference (auth.users, auth.uid(),
# storage.objects, storage.foldername()) before applying migrations in order
# and running the 006 acceptance test. RLS *behavior* is NOT validated here,
# only the FK SET NULL acceptance assertions; full Supabase validation still
# requires `supabase start` / a hosted project.
#
# Usage: DATABASE_URL=postgresql://postgres:postgres@localhost:5432/postgres ./run-acceptance.sh
set -euo pipefail

DB_URL="${DATABASE_URL:-postgresql://postgres:postgres@localhost:5432/postgres}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

psql "$DB_URL" -v ON_ERROR_STOP=1 <<'SQL'
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE SCHEMA IF NOT EXISTS auth;
CREATE TABLE IF NOT EXISTS auth.users(id uuid PRIMARY KEY);
CREATE OR REPLACE FUNCTION auth.uid() RETURNS uuid
    LANGUAGE sql STABLE AS $$ SELECT NULL::uuid $$;
CREATE SCHEMA IF NOT EXISTS storage;
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
SQL

for migration in "$ROOT"/migrations/00*.sql; do
    echo "Applying $(basename "$migration")"
    psql "$DB_URL" -v ON_ERROR_STOP=1 -f "$migration"
done

echo "Running 006 acceptance test"
psql "$DB_URL" -v ON_ERROR_STOP=1 -f "$ROOT/tests/006_context_fk_set_null_columns.sql"
echo "ACCEPTANCE PASS"

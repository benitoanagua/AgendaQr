alter table public.operations
    add column if not exists updated_at timestamptz not null default created_at;

update public.operations
set updated_at = greatest(updated_at, created_at)
where updated_at is null or updated_at < created_at;

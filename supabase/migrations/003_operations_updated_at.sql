alter table public.operations
    add column if not exists updated_at timestamptz;

update public.operations
set updated_at = created_at
where updated_at is null;

alter table public.operations
    alter column updated_at set not null,
    alter column updated_at set default now();

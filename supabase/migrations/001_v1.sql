create table if not exists public.destinations (
    id text primary key,
    user_id uuid not null references auth.users(id) on delete cascade,
    name text not null,
    qr_raw_content text not null,
    qr_kind text not null,
    category text,
    note text,
    favorite boolean not null default false,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table if not exists public.operations (
    id text primary key,
    user_id uuid not null references auth.users(id) on delete cascade,
    type text not null check (type in ('PAGO', 'COBRO')),
    occurred_at timestamptz not null,
    created_at timestamptz not null,
    amount numeric(20, 6),
    currency text,
    person_or_entity text,
    destination_id text,
    concept text,
    note text
);

create table if not exists public.comprobantes (
    id text primary key,
    user_id uuid not null references auth.users(id) on delete cascade,
    file_path text not null,
    mime_type text,
    extension text,
    created_at timestamptz not null,
    provenance text check (provenance in ('ENVIADO', 'RECIBIDO', 'DESCONOCIDO')),
    operation_id text references public.operations(id) on delete set null
);

create table if not exists public.deleted_operation_history (
    id bigint generated always as identity primary key,
    user_id uuid not null references auth.users(id) on delete cascade,
    deleted_at timestamptz not null default now(),
    date timestamptz not null,
    type text not null check (type in ('PAGO', 'COBRO')),
    amount numeric(20, 6),
    person_or_entity text
);

create index if not exists operations_user_occurred_idx on public.operations(user_id, occurred_at desc);
create index if not exists comprobantes_user_created_idx on public.comprobantes(user_id, created_at desc);
create index if not exists comprobantes_user_operation_idx on public.comprobantes(user_id, operation_id);

alter table public.destinations enable row level security;
alter table public.operations enable row level security;
alter table public.comprobantes enable row level security;
alter table public.deleted_operation_history enable row level security;

create policy "destinations_own_rows" on public.destinations for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "operations_own_rows" on public.operations for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "comprobantes_own_rows" on public.comprobantes for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "deleted_operation_history_own_rows" on public.deleted_operation_history for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

insert into storage.buckets (id, name, public)
values ('comprobantes', 'comprobantes', false)
on conflict (id) do nothing;

create policy "comprobantes_storage_select_own" on storage.objects for select using (
    bucket_id = 'comprobantes' and (storage.foldername(name))[1] = auth.uid()::text
);
create policy "comprobantes_storage_insert_own" on storage.objects for insert with check (
    bucket_id = 'comprobantes' and (storage.foldername(name))[1] = auth.uid()::text
);
create policy "comprobantes_storage_update_own" on storage.objects for update using (
    bucket_id = 'comprobantes' and (storage.foldername(name))[1] = auth.uid()::text
) with check (
    bucket_id = 'comprobantes' and (storage.foldername(name))[1] = auth.uid()::text
);
create policy "comprobantes_storage_delete_own" on storage.objects for delete using (
    bucket_id = 'comprobantes' and (storage.foldername(name))[1] = auth.uid()::text
);

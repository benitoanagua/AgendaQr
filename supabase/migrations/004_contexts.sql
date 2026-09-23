create table if not exists public.contexts (
    id text primary key,
    user_id uuid not null references auth.users(id) on delete cascade,
    name text not null,
    note text,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index if not exists contexts_user_updated_idx
    on public.contexts(user_id, updated_at desc);

alter table public.contexts enable row level security;

create policy "contexts_own_rows"
    on public.contexts
    for all
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

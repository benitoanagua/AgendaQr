-- Context relationships are additive and nullable so existing data remains valid.
-- A context is a human-facing grouping boundary, not the identity of a QR/activity/receipt.

alter table public.contexts
    add constraint contexts_id_user_unique unique (id, user_id);

alter table public.destinations
    add column if not exists context_id text;

alter table public.operations
    add column if not exists context_id text;

alter table public.comprobantes
    add column if not exists context_id text;

alter table public.destinations
    add constraint destinations_context_user_fk
    foreign key (context_id, user_id)
    references public.contexts (id, user_id)
    on delete set null;

alter table public.operations
    add constraint operations_context_user_fk
    foreign key (context_id, user_id)
    references public.contexts (id, user_id)
    on delete set null;

alter table public.comprobantes
    add constraint comprobantes_context_user_fk
    foreign key (context_id, user_id)
    references public.contexts (id, user_id)
    on delete set null;

create index if not exists destinations_user_context_idx
    on public.destinations(user_id, context_id);

create index if not exists operations_user_context_idx
    on public.operations(user_id, context_id);

create index if not exists comprobantes_user_context_idx
    on public.comprobantes(user_id, context_id);

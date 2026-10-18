-- Preserve user_id when a context is deleted.
-- PostgreSQL 15+ supports column-list SET NULL on composite foreign keys.
-- Migration is additive and does not modify migrations 001-005.

alter table public.destinations
    drop constraint if exists destinations_context_user_fk;

alter table public.destinations
    add constraint destinations_context_user_fk
    foreign key (context_id, user_id)
    references public.contexts (id, user_id)
    on delete set null (context_id);

alter table public.operations
    drop constraint if exists operations_context_user_fk;

alter table public.operations
    add constraint operations_context_user_fk
    foreign key (context_id, user_id)
    references public.contexts (id, user_id)
    on delete set null (context_id);

alter table public.comprobantes
    drop constraint if exists comprobantes_context_user_fk;

alter table public.comprobantes
    add constraint comprobantes_context_user_fk
    foreign key (context_id, user_id)
    references public.contexts (id, user_id)
    on delete set null (context_id);

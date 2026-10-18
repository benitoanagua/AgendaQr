-- Acceptance test for migration 006.
-- Run with a Supabase/PostgreSQL test database after migrations 001-006.
-- The database must contain at least one auth.users row.

begin;

DO $$
DECLARE
    v_user uuid;
    v_context text := 'migration006-context';
    v_destination text := 'migration006-destination';
    v_operation text := 'migration006-operation';
    v_comprobante text := 'migration006-comprobante';
    v_now timestamptz := now();
BEGIN
    select id into v_user from auth.users order by id limit 1;
    if v_user is null then
        raise exception '006 acceptance test requires at least one auth.users row';
    end if;

    insert into public.contexts (id, user_id, name, note, created_at, updated_at)
    values (v_context, v_user, 'migration006', null, v_now, v_now)
    on conflict (id) do update set user_id = excluded.user_id, updated_at = excluded.updated_at;

    insert into public.destinations
        (id, user_id, name, qr_raw_content, qr_kind, category, note, favorite, created_at, updated_at, context_id)
    values
        (v_destination, v_user, 'migration006 destination', 'migration006-qr', 'UNKNOWN', null, null, false, v_now, v_now, v_context)
    on conflict (id) do update set user_id = excluded.user_id, context_id = excluded.context_id;

    insert into public.operations
        (id, user_id, type, occurred_at, created_at, amount, currency, person_or_entity, destination_id, concept, note, context_id)
    values
        (v_operation, v_user, 'PAGO', v_now, v_now, 1, 'TEST', 'migration006', null, 'migration006', null, v_context)
    on conflict (id) do update set user_id = excluded.user_id, context_id = excluded.context_id;

    insert into public.comprobantes
        (id, user_id, file_path, mime_type, extension, created_at, provenance, operation_id, context_id)
    values
        (v_comprobante, v_user, 'migration006/test', 'text/plain', 'txt', v_now, 'DESCONOCIDO', v_operation, v_context)
    on conflict (id) do update set user_id = excluded.user_id, context_id = excluded.context_id;

    delete from public.contexts where id = v_context and user_id = v_user;

    if not exists (
        select 1 from public.destinations
        where id = v_destination and user_id = v_user and context_id is null
    ) then
        raise exception 'destination FK acceptance failed';
    end if;

    if not exists (
        select 1 from public.operations
        where id = v_operation and user_id = v_user and context_id is null
    ) then
        raise exception 'operation FK acceptance failed';
    end if;

    if not exists (
        select 1 from public.comprobantes
        where id = v_comprobante and user_id = v_user and context_id is null
    ) then
        raise exception 'comprobante FK acceptance failed';
    end if;
END $$;

rollback;

# Supabase — destinos V1

## Estado

Los destinos usan una estrategia **local-first con espejo remoto**.

- El almacenamiento local sigue siendo la fuente inmediata para la UI.
- Supabase PostgreSQL conserva una copia por usuario.
- RLS limita el acceso a filas cuyo `user_id` coincide con `auth.uid()`.
- Al crear el repositorio se intenta una sincronización remota → local.
- Las altas, ediciones y eliminaciones se escriben primero localmente y luego se registran como mutación durable en la cola de sincronización (ver `05-sync.md`).
- Un fallo de red no bloquea la operación local.
- La próxima inicialización vuelve a intentar la descarga remota.

## Conflictos

V1 utiliza una regla simple para la sincronización inicial:

> gana el registro con `updatedAt` más reciente.

Las mutaciones pendientes usan la cola durable especificada en `05-sync.md` (estados PENDING/PROCESSING/FAILED con recuperación). Una escritura local realizada sin conexión queda encolada hasta su espejo remoto.

## Mapeo actual

El modelo V1 actual de `Destination` contiene `QrAsset.encoded`, que representa los bytes codificados de la imagen QR.

El esquema Supabase existente fue diseñado con `qr_raw_content` y `qr_kind` porque el modelo conceptual futuro distingue contenido decodificado y clasificación. Mientras el dominio actual no exponga esos campos, el adaptador remoto conserva `encoded` en `qr_raw_content` y usa `UNKNOWN` para `qr_kind`.

Esto es una compatibilidad explícita del adaptador, no una reinterpretación del dominio.

## Seguridad

La app utiliza únicamente publishable key. La autorización de datos depende de Supabase Auth + RLS; el cliente nunca contiene service-role keys.

La sesión autenticada proporciona el `user_id` usado por el adaptador remoto.

## Fuera de esta fase

- resolución avanzada de conflictos;
- realtime;
- migración destructiva del modelo QR.

## Siguiente evolución

La misma abstracción de espejo remoto se reutiliza para operaciones y comprobantes a través de la cola durable (`05-sync.md`), manteniendo local-first.

# Supabase — comprobantes V1

Los comprobantes mantienen almacenamiento local para conservar el comportamiento offline y, con sesión autenticada, se espejan en:

- PostgreSQL: metadata;
- Supabase Storage: archivo físico en el bucket privado `comprobantes`.

La ruta remota es:

`<user_id>/<comprobante_id>.<extension>`

La ruta local sigue la misma convención por usuario:

`.../comprobantes/<user_id>/<comprobante_id>.<extension>`

El acceso queda restringido por las policies de Storage y por RLS de la tabla.

## Flujo

1. El archivo se guarda localmente.
2. Se guarda el metadata local.
3. Se intenta subir el archivo a Storage.
4. Se intenta guardar/actualizar el metadata remoto.
5. Un fallo remoto no elimina el respaldo local.
6. Al iniciar el repositorio, los comprobantes remotos se descargan y materializan en almacenamiento local.

## Seguridad

No se genera URL pública. Para descargar se usa el acceso autenticado del bucket privado.

## Limitaciones

La sincronización sigue siendo best-effort; todavía no existe una cola durable de mutaciones pendientes. La siguiente fase debe centralizar esa cola y la política de reintentos.

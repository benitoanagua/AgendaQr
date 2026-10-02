# Sincronización local-first — seguridad de identidad

Los repositorios locales ahora utilizan una clave de almacenamiento derivada del UUID del usuario autenticado.

Esto evita que:

- un usuario vea los destinos del usuario anterior en el mismo dispositivo;
- las operaciones queden mezcladas entre cuentas;
- los comprobantes locales se reutilicen accidentalmente después de cambiar de cuenta;
- el historial de operaciones eliminadas de una cuenta quede visible en otra.

La instancia de repositorio se crea únicamente dentro del árbol de UI autenticado y recibe un namespace como:

`agendaqr.destinations.v1.<user_id>`

La misma estrategia se aplica a operaciones, comprobantes e historial de operaciones eliminadas:

- `agendaqr.operations.v1.<user_id>`
- `agendaqr.comprobantes.v1.<user_id>`
- `agendaqr.deleted_operations.v1.<user_id>`

## Archivos locales de comprobantes

Los bytes de los comprobantes también están aislados por usuario. El directorio local sigue la misma convención que el bucket remoto:

`.../comprobantes/<user_id>/<comprobante_id>.<extensión>`

Esto evita que dos cuentas del mismo dispositivo compartan nombres de archivo.

## Importante

Esto no borra automáticamente los datos locales de una cuenta al cerrar sesión. Los conserva separados para que puedan volver a utilizarse si esa misma cuenta inicia sesión nuevamente.

La cola durable de mutaciones pendientes sigue siendo una fase posterior; los fallos remotos todavía se manejan como best-effort.

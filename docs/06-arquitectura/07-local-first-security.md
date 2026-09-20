# Sincronización local-first — seguridad de identidad

Los repositorios locales ahora utilizan una clave de almacenamiento derivada del UUID del usuario autenticado.

Esto evita que:

- un usuario vea los destinos del usuario anterior en el mismo dispositivo;
- las operaciones queden mezcladas entre cuentas;
- los comprobantes locales se reutilicen accidentalmente después de cambiar de cuenta.

La instancia de repositorio se crea únicamente dentro del árbol de UI autenticado y recibe un namespace como:

`agendaqr.destinations.v1.<user_id>`

La misma estrategia se aplica a operaciones y comprobantes.

## Importante

Esto no borra automáticamente los datos locales de una cuenta al cerrar sesión. Los conserva separados para que puedan volver a utilizarse si esa misma cuenta inicia sesión nuevamente.

La cola durable de mutaciones pendientes sigue siendo una fase posterior; los fallos remotos todavía se manejan como best-effort.

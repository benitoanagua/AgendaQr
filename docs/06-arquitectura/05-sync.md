# Sincronización local-first V1

Agenda QR persiste primero en almacenamiento local y registra una mutación durable para sincronización remota.

## Estados

- PENDING: pendiente de ejecución.
- PROCESSING: tomado por el procesador.
- FAILED: falló y espera el próximo intento.

## Recuperación

Al iniciar la sesión, `SyncRecoveryCoordinator` ejecuta una sincronización inmediata y luego revisa la cola periódicamente. Un elemento que quedó en PROCESSING se recupera como PENDING antes de procesar.

## Principio

La red no debe bloquear la captura de operaciones, destinos o comprobantes. El dato local es la fuente inmediata para la UX; Supabase es la réplica remota.

La conectividad real del sistema operativo se integrará posteriormente mediante un adaptador de plataforma; este coordinador funciona también sin depender de una API de red específica.

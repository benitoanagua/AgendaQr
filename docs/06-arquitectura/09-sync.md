# Sincronización local-first V1

Agenda QR persiste primero en almacenamiento local y registra una mutación durable para sincronización remota.

## Estados

- PENDING: pendiente de ejecución.
- PROCESSING: tomado por el procesador.
- FAILED: falló con error transitorio y espera el próximo intento (backoff exponencial acotado).
- DEAD_LETTER: error permanente (auth/RLS/validación) o sin reintentos; no se reclama hasta redrive manual.

## Resolución de conflictos

`SyncConflictResolver` aplica Last-Write-Wins con tolerancia de skew (1 s): gana el `updatedAt` mayor; en empate gana el servidor. Un DELETE compite con su instante de borrado: solo se propaga si es más nuevo que el remoto; si no, el registro remoto se aplica localmente (resurrección). `syncFromRemote` nunca pisa entidades con mutaciones pendientes: el drain decide.

## Recuperación

Al iniciar la sesión, `SyncRecoveryCoordinator` ejecuta una sincronización inmediata y luego revisa la cola periódicamente. Un elemento que quedó en PROCESSING se recupera como PENDING antes de procesar.

## Principio

La red no debe bloquear la captura de operaciones, destinos o comprobantes. El dato local es la fuente inmediata para la UX; Supabase es la réplica remota.

La conectividad real del sistema operativo se integrará posteriormente mediante un adaptador de plataforma; este coordinador funciona también sin depender de una API de red específica.

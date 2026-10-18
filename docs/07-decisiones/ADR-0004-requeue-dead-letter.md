# ADR-0004 — Recuperación de mutaciones en cuarentena (DEAD_LETTER)

**Estado:** PROPUESTA — requiere aprobación  
**Fecha:** 2026-10-05  
**Área:** Sincronización local-first / UX de estados

## Contexto

La cola durable clasifica los fallos: los transitorios reintentan con
backoff (REINTENTAR acelera el drain) y los permanentes (validación de
esquema, permisos, o reintentos agotados) pasan a `DEAD_LETTER`. El drain
**omite** los `DEAD_LETTER`: es correcto (no va a magically tener éxito),
pero deja al usuario sin salida para esos elementos. U4 separó el estado
visible (`No se pudo sincronizar`, con "Tu información está guardada en el
dispositivo") y retiró el REINTENTAR engañoso para ellos.

Recuperarlos exige una acción que hoy no existe en el contrato V1.

## Opciones

### Opción A — Re-encolar desde el detalle
El elemento en estado Dead ofrece **"Volver a intentar"** que re-encola la
mutación (state=PENDING, attempts=0). Útil si el error fue circunstancial
(p. ej. esquema corregido por una migración nueva, sesión reautenticada).
Riesgo: un error verdaderamente permanente re-entra en el ciclo de
reintentos y vuelve a agotarlo (ruido, no pérdida).

### Opción B — Corregir y re-guardar
El usuario edita el elemento ofensivo (p. ej. el monto inválido) y guarda:
la escritura encola un UPSERT nuevo que sustituye al muerto (la cola ya
deduplica por `(resource, entityId)`). No requiere acción nueva — el
DEAD_LETTER solo se limpia cuando el usuario corrige de verdad. Requiere
que el detalle muestre QUÉ campo falló (`lastError` ya viaja en la
mutación).

### Opción C — A + B
"Volver a intentar" para los circunstanciales + guía de corrección para
los permanentes.

## Recomendación

**Opción B**: es la única que no re-intenta lo que ya se sabe que falla y
usa únicamente acciones existentes (editar + guardar). La A/C solo con
evidencia real de errores circunstanciales en producción.

## Impacto de NO aprobar

Los elementos en Dead permanecen visibles con su estado honesto y el dato
seguro localmente; la cola no pierde nada (la cuarentena es durable).

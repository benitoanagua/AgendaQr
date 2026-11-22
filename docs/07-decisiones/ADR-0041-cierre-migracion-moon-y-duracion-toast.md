# ADR-0041 — Cierre de la migración Moon y duración de `AqToast`

**Estado:** ACEPTADO — 2026-11-22
**Relaciona:** ADR-0039, ADR-0040.

## Decisión

1. **Cierre de la migración Moon.** Se eliminan los wrappers `AqMoon*` y todo el legado de Polaris de `core/ui`. La migración solo se considera cerrada cuando `verifyNoLegacyAnywhere` está en verde (ningún símbolo de `legacySymbols` declarado ni usado en `core`, `feature`, `shared`, `androidApp` ni `iosApp`, tests incluidos).
2. **Duración de `AqToast`: 4 000 ms** (`AqMotion.ToastDurationMs`). Es una decisión de producto, no una transcripción de Moon: Moon solo especifica un recorrido de 64 dp y un intervalo de 200 ms entre toasts.
3. **Nombres de composiciones Moon-only:** nombres propios sin prefijo `AqMoon` y sin colisionar con símbolos legacy (`AqSelectionToolbar`, `AqEmptyPlaceholder`, `AqScreen`, `AqAppBar`).

## Justificación

Un único origen de anatomía y estados evita que convivan dos sistemas de componentes. Fijar la duración del toast en un valor legible corrige un valor anterior de 200 000 ms que dejaba los avisos en pantalla indefinidamente.

# ADR-0036 — Descarte del teclado propio para el importe

**Estado:** APROBADA por delegación del propietario  
**Fecha:** 2026-11-20  
**Área:** UX/UI V1 (S07 editor de importe)  
**Decisor:** agente de ingeniería (delegación del propietario de producto)

## Contexto

D5 del cierre de gaps: se evaluó crear un teclado propio (`AqKeypad`) para el campo de importe de actividades. `AqKeypad` existe pero solo se usa en el PIN (S00).

## Decisión

Descartar definitivamente el teclado propio para el importe. Razones:
- El teclado numérico del sistema respeta IME, accesibilidad (TalkBack/VoiceOver), separador decimal del idioma y autocompletado.
- Un teclado propio duplicaría funcionalidad sin ganancia clara de UX.
- `AqKeypad` queda exclusivamente para el flujo de PIN (S00).

## Implementación

- `OperationEditorScreen` (importe) usa `KeyboardType.Decimal` + `ImeAction.Done` (ya existente).
- Validación existente: dígitos, un solo separador, máx. 2 decimales; se aceptan coma y punto según configuración regional (`isValidAmount` en `OperationFormat.kt`).
- Documentado en la matriz de gaps (fila D5) como descartado.

## Alternativas

- Teclado propio: rechazado (sin ganancia clara, duplicación de funcionalidad del sistema).

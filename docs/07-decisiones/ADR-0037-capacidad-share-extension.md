# ADR-0037 — Capacidad de plataforma para Share Extension

**Estado:** APROBADA por delegación del propietario  
**Fecha:** 2026-11-20  
**Área:** Plataforma (iOS/Android) / S02  
**Decisor:** agente de ingeniería (delegación del propietario de producto)

## Contexto

D11/G8: el bloque «Desde otra app» de S02 no debe prometer una Share Extension
que no existe en iOS. Se añade una capacidad `expect/actual` de plataforma
(`supportsShareExtension`) para que el bloque dependa de ella en lugar de una
condición ad hoc.

## Decisión

- `expect val supportsShareExtension: Boolean` en `shared` (AgendaQrShared).
- `actual val supportsShareExtension = false` en iOS (hasta que exista el
  target Xcode y la firma).
- `actual val supportsShareExtension = true` en Android (donde la app recibe
  intents de compartir).
- S02 muestra el bloque «Desde otra app» condicionado a `supportsShareExtension`.
- Sin UI nueva que prometa algo que no existe. Share Extension iOS completa
  queda fuera de alcance (límite duro: requiere Xcode y firma, no versionado).

## Límites duros vigentes (no se enmiendan)

- Sin Share Extension iOS completa.
- Sin dependencias de Flutter ni Moon en runtime.

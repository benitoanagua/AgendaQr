# ADR-0035 — Insignia de navegación (badge de comprobantes sin asociar)

**Estado:** APROBADA por delegación del propietario  
**Fecha:** 2026-11-20  
**Área:** UX/UI V1 (navegación raíz: AqNavBar / AqNavRail)  
**Decisor:** agente de ingeniería (delegación del propietario de producto)

## Contexto

D3 del cierre de gaps: la navegación no muestra conteo de pendientes. Hay un caso de producto concreto: el usuario quiere ver de un vistazo cuántos comprobantes sin asociar hay en la bandeja (S07 Unassociated) desde la pestaña de Actividades.

## Decisión

1. `AqNavBar` y `AqNavRail` aceptan un parámetro opcional `badgeCount: Int = 0`. Cuando > 0, se muestra un `AqBadge` (tono de atención Polaris) sobre el ítem correspondiente, con tope «99+», y oculto con 0.
2. El `contentDescription` del ítem incluye el conteo («Actividades, 3 sin asociar») para TalkBack.
3. `AqNavChromeState` gana un campo `badgeCount: Int = 0` que se lee para la pestaña activa.
4. El ítem badgede es el de Actividades (`AppRoute.Operations`). El conteo proviene de comprobantes sin asociar (`operationState.unassociated.size`).
5. «Importación incompleta» se descarta definitivamente: no tiene destino de navegación propio ni badge.

## Alternativas

- Badge en todas las pestañas: rechazado, solo Actividades tiene pendientes accionables (bandeja).
- Badge solo en S01: insuficiente, la bandeja es la superficie de pendientes.

## Consecuencias

- Componente `AqBadge` (ya existente en `core/ui`) se reutiliza.
- `AqNavChromeState` cambia su firma (campo con default, no rompe llamadas existentes).
- El plumbado del conteo real desde `operationState` al `navChrome` requiere que el estado esté disponible donde se construye el chrome; se conectará cuando el scope lo permita (documentado en STATE.md).

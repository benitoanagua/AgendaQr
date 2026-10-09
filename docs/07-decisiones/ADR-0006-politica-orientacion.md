# ADR-0006 — Política de orientación en teléfonos

**Estado:** DECIDIDA (técnica, ronda 2 — 2026-10-09); el bloqueo vertical queda como opción de producto sin implementar
**Área:** UX/Android+iOS · Alcance: SOLO teléfonos (tablets/plegables fuera de esta ronda)
**Contexto:** `AuthenticatedAppRoot` usaba `remember { AppBackStack() }`: rotación o muerte de proceso devolvían siempre a Inicio y perdían los flags de formulario (`submitted`, `searchReturnDestinationId`).

## Decisión

**Rotación libre** en teléfonos: no se bloquea `screenOrientation` ni se
añade `configChanges` al manifest. Motivo: la ronda 2 hace el estado
**restaurable** (back stack con `rememberSaveable` + `AppBackStackSaver`
por nombre de ruta; flags de formulario en `rememberSaveable`), con tests
que fijan guardar/restaurar y el fallback conservador. Bloquear la
orientación escondería el problema en vez de resolverlo y castigaría a
quien prefiere horizontal.

## Alternativas

- **Bloquear retrato** (`android:screenOrientation="portrait"` + Info.plist
  de Xcode): menos superficie de recreación, pero pierde la elección del
  usuario y aplaza el estado restaurable. Queda disponible como decisión
  de producto: si se aprueba, añadir también el caso de prueba manual de
  "rotación no recrea" al checklist.
- `configChanges` en el manifest (evitar recreación gestionando cambios):
  requiere manejar manualmente cada cambio; rechazado (fragile).

## Consecuencias

- La cámara se re-liga a la recreación vía `bindToLifecycle` (CameraX) ✓.
- Verificación manual necesaria (checklist): rotación en S01/S06/S07 y
  durante el escaneo; iOS (Xcode) con el mismo criterio.

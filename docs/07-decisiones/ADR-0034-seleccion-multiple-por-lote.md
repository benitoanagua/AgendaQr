# ADR-0034 — Selección múltiple por lote y política de confirmación destructiva

**Estado:** APROBADA por delegación del propietario  
**Fecha:** 2026-11-20  
**Área:** UX/UI V1 (S01 destinos, S07-list actividades, inbox comprobantes) / core:ui  
**Decisor:** agente de ingeniería (delegación del propietario de producto)

## Contexto

D1 del cierre de gaps: no existe selección múltiple; cada acción requiere un toque por elemento. La infraestructura ya existe en `core/ui` (`AqCheckbox`, `AqSelectAllCheckbox`, `AqIndexTable` sin consumo). Hay un caso de producto concreto (eliminar/actualizar varios elementos de un golpe) que justifica el modo selección.

## Decisión

**Patrón de entrada:** pulsación larga (`long-press-select`) en una fila entra en modo selección. En modo selección:
- La barra superior pasa a barra contextual: botón «×» (cerrar), contador «N seleccionados» y `AqSelectAllCheckbox` para seleccionar todo / limpiar.
- Cada fila muestra `AqCheckbox` (objetivo ≥ 48 dp) y un toque alterna la selección.
- `AqSwipeActions` se deshabilita mientras hay selección (los gestos no son la única vía).
- Back del sistema o «×» sale del modo selección. Predictive back soportado (no destruye selección).
- El estado de selección sobrevive a rotación (`rememberSaveable`).

**Acciones masivas por superficie:**
- S01 (destinos): favorito, asignar contexto, eliminar.
- S07-list (actividades): asignar contexto, eliminar.
- Inbox (comprobantes sin asociar): asociar a actividad (flujo existente) y eliminar si el dominio lo permite.

**Política destructiva (eliminar por lote):** una única confirmación con el conteo («¿Eliminar 5 actividades?»), `AqDialog(destructive = true)`, foco inicial en la acción conservativa. Tras ejecutar, se ofrece «Deshacer» en el snackbar (commit diferido en el ViewModel). Favorito masivo y asignar contexto no piden confirmación y ofrecen Deshacer.

**Nivel de precedencia:** Nivel 1 Polaris (`IndexTable` con acciones masivas), adaptado a móvil con tokens Polaris y patrón nativo de selección por pulsación larga.

**Contrato:** se añade el gesto `long-press-select` y el estado `selection` a S01, S07-list e inbox en `ScreenContractGate.kt` y en el documento generado `03-contrato-interaccion-pantallas.md`.

## Alternativas

- Checkbox permanente en todas las filas: rechazado, añade ruido visual al flujo principal (una acción por toque sigue siendo la vía normal).
- Menú contextual por fila: insuficiente para operaciones por lote.

## Consecuencias

- Se añade `AqSelectionBar` (barra contextual) en `core/ui`.
- Las 3 superficies ganan estado de selección y acciones masivas.
- `verifyScreenContract` debe pasar con el nuevo gesto y estados.

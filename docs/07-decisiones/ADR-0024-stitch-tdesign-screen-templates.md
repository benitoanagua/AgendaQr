# ADR-0024 — `TDesign Screen Templates` (Google Stitch) como referencia primaria de composición UX/UI

**Estado:** ACEPTADO — 2026-10-18
**Relaciona:** ADR-0023 (autoridad de tokens y componentes), ADR-0015
(sustituye su fuente Stitch), ADR-0007 (capturas no versionadas), ADR-0016
(identidad local), contrato de interacción por pantalla.

## 1. Contexto

El producto declaró el proyecto de Google Stitch `TDesign Screen Templates`
como base de referencia de la UI/UX. Su inspección por MCP
(`list_projects`, `list_screens`, `get_screen`) confirma, de forma verificable:

- Identificador: `projects/14722565667748094207`.
- Título exacto: `TDesign Screen Templates` (rol `OWNER`, tipo `TEXT_TO_UI_PRO`).
- Tema declarado: `designTheme.customColor = #0052d9` (azul primario de
  TDesign) con `designMd` rotulado `TDesign Agenda Mobile`.
- 12 pantallas `MOBILE` con `htmlCode` y `screenshot` reales, en español y con
  datos del dominio de AgendaQr (destinos QR, moneda `BOB`, contextos,
  actividades).

Hasta ahora la única referencia Stitch reconocida era la de ADR-0015
(`Ant Design Mobile App`) y ADR-0023 congelaba intención, navegación y back
stack. Ambas cosas se reconcilian aquí.

## 2. Decisión

1. El proyecto Stitch **`TDesign Screen Templates`
   (`projects/14722565667748094207`)** es la **referencia primaria de
   composición de pantallas**: estructura, jerarquía, densidad, patrones de
   interacción y flujo UX/UI.
2. `tdesign_flutter@0.2.7` conserva la autoridad **normativa de tokens,
   componentes, medidas, estados y accesibilidad** (ADR-0023). La autoridad
   queda dividida: composición ← Stitch; tokens y componentes ←
   `tdesign_flutter`.
3. Se autoriza rediseñar **intención de pantalla, navegación, orden de flujos,
   jerarquía de acciones, back stack y gestos** para alinearse con el proyecto
   Stitch y TDesign, con la condición de propagar cada cambio a ADR,
   especificación, contrato estructurado, código y pruebas en el mismo cambio
   lógico.
4. Se preservan sin cambios los límites de producto, dominio, privacidad e
   integridad: local-first, identidad local sin cuenta obligatoria, privacidad
   del PIN, guardado distinto de sincronización, asociación reversible,
   comprobante sin QR ni operación y operación sin contexto. Ningún diseño de
   Stitch altera datos ni reglas de dominio.
5. Orden de precedencia: (1) producto, privacidad, integridad y reglas de
   negocio; (2) diseño real del proyecto Stitch; (3) `tdesign_flutter@0.2.7`
   para tokens y componentes; (4) contrato estructurado y ADR reconciliados;
   (5) implementación Compose en `core/ui` y pantallas; (6) pruebas y CI.
6. La trazabilidad es **ejecutable**: el registro tipado `stitchTDesign` de
   `ScreenContractGate.kt` mapea cada superficie del contrato a una pantalla de
   Stitch o a una exclusión con motivo. `verifyScreenContract` falla si una
   superficie no tiene ni lo uno ni lo otro, si una pantalla de Stitch apunta a
   una superficie inexistente o si el documento generado
   `docs/04-ux/06-trazabilidad-stitch-tdesign.md` diverge del registro.
7. No se versionan capturas ni hashes (ADR-0007 vigente).

## 3. Inventario Stitch ↔ AgendaQr

La tabla completa, generada y verificada, vive en
`docs/04-ux/06-trazabilidad-stitch-tdesign.md` (fuente: registro
`stitchTDesign`). Resumen:

| Superficie | Contrato | Pantalla Stitch |
|---|---|---|
| `home` | S01 | S01 — Inicio (Agenda QR) |
| `add` | S02 | S02 — Añadir |
| `search` | S04 | S04 — Buscar y Resultados |
| `contexts` | S06 | S06 — Contextos |
| `operations` | S07-list | S07-list — Actividades |
| `operationeditor` | S07 | S07 — Registrar Actividad |
| `contextpicker` | S08 | S08 — Seleccionar contexto |
| `importreview` | S09 | S09 — Revisar QR |
| `importbatch` | S12 | S12 — Resultado de importación masiva |
| `localaccess` | S00 | S00 — Acceso local |
| `destinationdetail` | destination-detail | Detalle de Destino QR |
| `operationdetail` | operation-detail | Detalle de Actividad |

Superficies sin diseño propio en el proyecto, con exclusión declarada y motivo
en el registro: `destinationedit`, `qrfullscreen`, `contextdetail`,
`unassociated`, `account`, `authform`, `camera`.

Observación registrada: los títulos internos de la barra superior de algunas
pantallas de Stitch no coinciden con su nombre de archivo (por ejemplo, S02 y
S07 muestran "Detalle De Actividad"; S06 y S07-list muestran "Inicio"). El
mapeo se hace por contenido verificado, no por el título de la barra.

## 4. Enmiendas y sustituciones

- **ADR-0023 §1 y §6** (intención, navegación, back stack y "no se incorporan
  gestos nuevos"): enmendados. La composición, navegación, jerarquía y gestos
  pueden cambiar para seguir Stitch TDesign; los tokens y componentes siguen
  rigiéndose por ADR-0023. Se conserva el historial de ADR-0023.
- **ADR-0023 §2.7 y §5.7** ("los cuatro destinos raíz... conservan sus
  contratos", "las pruebas de navegación y back stack pasan sin cambios"):
  enmendados; los cambios autorizados actualizan contrato y pruebas a la vez.
- **ADR-0015** (referencia Stitch `Ant Design Mobile App`): su fuente Stitch
  queda sustituida por `TDesign Screen Templates`. La aportación de ADR-0015
  (anatomía financiera: segmentado, tags tonales, hero QR, meta row) se
  conserva como patrón, no como fuente vigente.
- **D-20** de `01-decisiones-aprobadas.md`: se complementa con D-21.

## 5. Consecuencias

- La composición de cada pantalla se puede rediseñar contra su pantalla de
  Stitch, con evidencia visual y de HTML.
- Toda divergencia entre Stitch y `tdesign_flutter` se resuelve por la
  precedencia del §2.5 y se registra.
- El gate incorpora una nueva clase de divergencia (trazabilidad Stitch) con
  pruebas negativas propias.
- Deuda declarada: la implementación Compose de las pantallas todavía no
  refleja todos los cambios de composición autorizados; se migran por bloques
  con su contrato y pruebas.

## 6. Riesgos

- Tomar una maqueta de Stitch como especificación completa de comportamiento.
  Mitigación: el contrato estructurado sigue siendo la fuente funcional; Stitch
  aporta composición e intención.
- Deriva entre el proyecto Stitch (editable) y el registro tipado. Mitigación:
  el registro es la copia estable auditada; cambiarla exige regenerar el
  documento y pasa el gate.
- MCP no accesible en otro entorno. Mitigación: el registro y el documento
  generado permiten verificar la trazabilidad sin MCP.

## 7. Evidencia

- `list_projects` → proyecto y título exactos, `customColor #0052d9`.
- `list_screens`/`get_screen` → 12 pantallas con `htmlCode` y `screenshot`.
- Inspección visual de los 12 renders y lectura del HTML de S01.
- `ScreenContractGate.kt`: registro `stitchTDesign`,
  `stitchTraceabilityViolations` y `renderStitchTraceabilityDoc`.
- Tareas `verifyScreenContract` y `verifyScreenContractFixtures`.

## 8. NO VERIFICADO

- Comportamiento interactivo real de los prototipos de Stitch (gestos,
  transiciones): el proyecto entrega pantallas estáticas, no un prototipo
  navegable.
- Fidelidad pixel a pixel de la implementación Compose: pendiente de migración.

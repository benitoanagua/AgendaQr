# Trazabilidad Stitch ↔ contrato de pantallas

> Generado por `generateStitchTraceabilityDoc` desde el registro `stitchTDesign` y el
> contrato estructurado. No editar a mano: `verifyScreenContract` falla si diverge.

**Proyecto Stitch (referencia primaria de composición, ADR-0024):** `projects/14722565667748094207` — "TDesign Screen Templates".

## Pantallas con diseño en Stitch

| Superficie | Contrato | Pantalla de Stitch | ID de pantalla |
|---|---|---|---|
| `add` | S02 | S02 — Añadir | `969e742ab31b42578182e1858600712b` |
| `contextpicker` | S08 | S08 — Seleccionar contexto | `abc14a9a650640658ac9cd1c9d02780d` |
| `contexts` | S06 | S06 — Contextos | `ccbb1b4a67524a9fa18085ec1654f52c` |
| `destinationdetail` | destination-detail | Detalle de Destino QR | `333c4142a5d84bd9a73efdfc1d941855` |
| `home` | S01 | S01 — Inicio (Agenda QR) | `0a932278a2ce4319ab2037fad9e33487` |
| `importbatch` | S12 | S12 — Resultado de importación masiva | `1a1756f8592a4d5fab8a375f10c5b52a` |
| `importreview` | S09 | S09 — Revisar QR | `a75ca0eb22ca4f0d83a26b6271e2c803` |
| `localaccess` | S00 | S00 — Acceso local | `1f1c308186cb4950a635298b83f278f3` |
| `operationdetail` | operation-detail | Detalle de Actividad | `a96a5c5dc2ce482db7079a9fada4cab3` |
| `operationeditor` | S07 | S07 — Registrar Actividad | `031d0f657eee4f29838aaa12c8001cce` |
| `operations` | S07-list | S07-list — Actividades | `f75644b1b0f34ed9a53a334f904cb5f3` |
| `search` | S04 | S04 — Buscar y Resultados | `7e61a9c6281844deaa4f5aef667f42e6` |

## Superficies sin diseño en Stitch (exclusión declarada)

| Superficie | Contrato | Motivo |
|---|---|---|
| `account` | account | Sin pantalla propia: "Cuenta y respaldo" es una acción de la barra de S01 Inicio, sin diseño de pantalla dedicado en el proyecto. |
| `authform` | auth | Sin pantalla propia: el proyecto no diseña el formulario de autenticación de cuenta; S00 Acceso local cubre el acceso por PIN. |
| `camera` | S03 | Sin pantalla propia: la cámara aparece como método de captura dentro de S02 Añadir ("Cámara en vivo"), sin superficie independiente. |
| `contextdetail` | context-detail | Sin pantalla propia: los conteos y el estado del fondo ya aparecen en las tarjetas de S06 Contextos; el detalle por contexto no está diseñado. |
| `destinationedit` | destination-edit | Sin pantalla propia en Stitch: la edición de metadatos del destino se resuelve con el editor de la Actividad (S07) y el Detalle de Destino QR. |
| `qrfullscreen` | qr-fullscreen | Sin pantalla propia: el Detalle de Destino QR ofrece "Mostrar QR" como acción, no como superficie con diseño independiente. |
| `unassociated` | inbox | Sin pantalla propia: la bandeja de comprobantes sueltos se referencia desde S07-list Actividades ("Comprobantes") pero no tiene diseño dedicado. |


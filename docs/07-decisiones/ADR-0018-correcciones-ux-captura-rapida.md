# ADR-0018 — Correcciones UX: captura rápida, recuperación y coherencia

**Estado:** APROBADA — 2026-10-16
**Relaciona:** ADR-0009 (propuesto, sin implementar), ADR-0016 (enmendada: política de bloqueo), ADR-0017 (tokens intactos), especificación UX/UI V1.8 (`docs/04-ux/02-especificacion-ux-ui-v1.md`).

## Contexto

Una auditoría de experiencia de uso encontró defectos contra el principio del producto ("Capturar rápido. Organizar cuando sea necesario. Recuperar siempre.") y contra la especificación (una pantalla = una intención + una acción principal): filtros que conmutan al tocar el activo (U-01), bloqueo que interrumpe galería/cámara/compartir (U-02), fecha manual sin hora (U-03), jerarquía invertida en el Detalle (U-04/U-05), editor largo con validación falsa y pérdida silenciosa del borrador (U-06/U-07/U-08), Inicio distinto de la especificación (U-10), jerga y aseguramiento repetido (U-11), contenido del QR en listas (U-12), iconos engañosos (U-13), vocabulario mixto (U-14), N primarios en comprobantes (U-15), biometría manual y foco invasivo (U-20/U-21). U-22 (paginación local 50/50) se reporta sin cambios.

El propietario aprueba explícitamente enmendar la congelación de la UX V1 en lo que este ADR cambia (jerarquía primaria del Detalle, editor, bloqueo, Inicio).

## Decisión

D1. **Bloqueo con gracia acotada (120 s)** para flujos externos iniciados por la app (galería, cámara del sistema, selectores, compartir, ajustes desde permiso). `ExternalFlowTracker` en presentación con reloj inyectable; la decisión vive en `ExternalFlowLockPolicy` (pura, probable): `ON_STOP` consulta `shouldLockOnStop`, `ON_START` bloquea si la gracia venció y consume la marca, y `ON_RESUME` limpia SIEMPRE la marca pendiente — un flujo translúcido (hoja de compartir o diálogo de permiso) puede no producir `ON_STOP`, y una marca viva impediría bloquear un paso a segundo plano posterior dentro de la ventana. Fail-closed sin marca. Riesgo residual: si el sistema destruye el proceso fuera de la gracia, el borrador en memoria se pierde (el draft vive en `rememberSaveable`; el grafo no se elevó por encima del PIN por ser cambio no trivial).

D2. **Fecha y hora con selectores** (`AmDatePickerDialog`/`AmTimePickerDialog` en `core/ui`, M3 1.8.2 disponible en commonMain, verificado bajo Robolectric); atajos Hoy/Ayer; valor por defecto ahora con hora; medianoche legada sin hora y sin reescritura. Sin cambios en el dominio (`occurredAt` ya son milisegundos).

D3. **Detalle: "Mostrar QR" única primaria** (pantalla completa), Compartir secundaria; Registrar/Editar/Eliminar al menú "…". Tarjeta tocable con la misma acción. Pantalla completa con brillo máximo + pantalla encendida y restauración (expect/actual).

D4. **Editor con divulgación progresiva**: Tipo · Importe+Moneda · Persona · Fecha · Contexto · Más detalles · Guardar. Importe con validación real y canonización con punto; moneda BOB/USD/Otra (defecto = última actividad, derivada del estado); contrapartes sugeridas (derivadas del estado); sin persistencia nueva. Salida con cambios → "¿Descartar cambios?" (destructivo, foco conservador).

D5. **Inicio según S01**: FAB "Añadir" abre el hub "¿Qué quieres añadir?" (diálogo: Traer QR → S02, Registrar actividad → S07 sin preasociación, Crear contexto → diálogo existente); fuera acciones duplicadas; "Favoritos" (no "frecuentes"); la lista de Todos excluye los favoritos del carrusel (sin duplicación).

D6. **Copy**: ≤ 60 caracteres, sin jerga ("SQLite", "firma temporal", "integridad histórica" fuera de UI), un aseguramiento por pantalla, "actividad" unificado en texto visible (identificadores intactos). Contenido del QR fuera de las listas (bloque expandible en el Detalle). `FLAG_SECURE` NO implementado (ADR-0009 propuesto): decisión pendiente con consecuencia (bloquea capturas del QR).

D7. **Comprobantes sin asociar**: la fila abre el flujo de asociación; "Crear actividad desde esto" en texto. PIN: biometría automática una vez por bloqueo (botón de respaldo); demo solo en primer arranque. Búsqueda con foco solo desde el disparador de Inicio.

## Consecuencias

- Nuevas acciones de presentación (`SetFavoritesOnly`, `EditorDirtyChanged`, `DiscardEditor`, `DismissDiscardPrompt`); `ToggleFavorites` eliminado; lista de Todos sin favoritos del carrusel.
- Cadenas retiradas/reescritas registradas en el informe del cambio; pruebas por hallazgo (reproducción en rojo primero para U-01/U-03/U-08, rojo local para U-02).
- Tokens, radios y paleta intactos (ADR-0017 vigente).

## Riesgos y alternativas

- Gracia de 120 s (D1): si el propietario prefiere 0 s, se desactiva en `ExternalFlowGraceMillis` (el borrador se conservaría por otra vía, pendiente de diseño).
- `FLAG_SECURE` pendiente de ADR-0009 (propuesto).
- Monedas BOB/USD/Otra: lista de presentación, ampliable sin dominio.
- Hashes visuales sensibles al entorno (ADR-0007): regenerar en CI si difiere.

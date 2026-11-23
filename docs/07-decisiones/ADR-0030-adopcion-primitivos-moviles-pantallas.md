# ADR-0030 — Adopción de primitivos móviles en pantallas de producción

## Estado
Aprobado (normativo para la integración UI/UX en Agenda QR).

## Contexto
La capa `core/ui` cuenta con la reimplementación del sistema visual anterior y una suite de componentes y patrones móviles resultantes de la cadena de precedencia (ADR-0029: Moon → nativo). Sin embargo, existían componentes implementados que permanecían únicamente en pruebas o galería sin uso visible en las pantallas de producción (`AqSwipeActions`, `AqPullToRefresh`, `AqBottomSheet`, `AqActionSheet`, `AqTooltip`, `AqSpinner`, `AqProgressBar`, `AqProgressBlock`, `AqSwitch`, etc.).

Asimismo, es necesario que los contenedores clicables (`AqTappableCard`, `AqQuickAction`) unifiquen su semántica mediante `mergeDescendants = true` para garantizar nombres accesibles inequívocos en TalkBack.

## Decisión
1. **Adopción de gestos contextuales en listas:**
   - Incorporar `AqSwipeActions` en las filas de destinos (S01, `DestinationsScreen`) para alternar favoritos y solicitar eliminación (abriendo el diálogo de confirmación existente).
   - Incorporar `AqSwipeActions` en las filas de operaciones (S07-list / S08, `OperationListScreen`) para editar y solicitar eliminación (preservando el diálogo destructivo existente).
   - Mantener siempre la alternativa accesible `customActions` en la semántica de Compose, de modo que TalkBack ofrezca las acciones sin requerir el gesto táctil.
2. **Refresco táctil:**
   - Integrar `AqPullToRefresh` en S01 y S07/S08 para permitir al usuario solicitar recarga o sincronización mediante arrastre vertical, mostrando el indicador visual del sistema visual anterior.
3. **Hojas y menús de acciones:**
   - Conectar las acciones de desbordamiento de las pantallas de detalle (S03 `DestinationDetailScreen` y S09 `OperationDetailScreen`) con `AqActionSheet` a través del controlador de `AqScreenScaffold`.
   - Ofrecer `AqBottomSheet` para detalles secundarios o contextuales cuando el usuario requiera inspección sin abandonar el flujo.
4. **Progreso y estados de carga:**
   - Emplear `AqSpinner` en `AqLoading` para estandarizar todos los estados de carga de la aplicación con la animación circular canónica y anuncio accesible polite.
   - Usar `AqProgressBar` y `AqProgressBlock` en los flujos de importación y guardado por lotes (`ImportBatchScreen`, `ImportReviewScreen`).
   - Extender el uso de `AqSkeleton` a múltiples superficies (`DestinationsScreen`, `ContextScreen`, `ComprobanteViewer`).
5. **Formularios e interactividad:**
   - Incorporar `AqSwitch` en `DestinationEditorScreen` para controlar el atributo de favorito del destino directamente desde el formulario.
   - Unificar semántica de contenedores tocables en `AqTappableCard` con `mergeDescendants = true`.

## Consecuencias
- Los componentes móviles de `core/ui` pasan a estar activos y visibles en la experiencia diaria del usuario.
- No se introducen dependencias externas ni se alteran contratos de dominio, persistencia ni flujos funcionales.
- Se mantienen todas las políticas de confirmación para acciones destructivas.
- Se eleva la calidad de accesibilidad en TalkBack/VoiceOver y se eliminan advertencias de nodos clicables sin nombre accesible.

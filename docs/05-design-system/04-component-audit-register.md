# Auditoría de componentes — registro de decisiones

Este registro inicia la auditoría del inventario que ya declara el laboratorio de `core:ui`. No equivale a una inspección visual ni a una validación de cada implementación. Las decisiones definitivas se anotarán tras revisar el código fuente de cada componente y sus usos en pantallas reales.

## Criterios de clasificación

- **Conservar:** ya cumple el contrato visual unificado y sus estados/accesibilidad están cubiertos.
- **Adaptar:** aporta una función válida, pero su presentación o API necesita alinearse al contrato.
- **Reemplazar:** existe una necesidad real, pero la implementación actual no puede cumplirla sin complejidad o duplicación excesiva.
- **Retirar:** no tiene uso de producto ni valor de accesibilidad/plataforma y puede eliminarse sin romper compatibilidad necesaria.
- **Pendiente de inspección:** no se decide por el nombre ni por la documentación; requiere leer implementación y consumidores.

## Inventario inicial del laboratorio

| Área | Componentes/tokens existentes | Estado de auditoría |
|---|---|---|
| Fundamentos | `XauxaColor`, `XauxaSpacing`, `XauxaMetrics`, `XauxaType`, `XauxaMotion`, `XauxaFocus` | Pendiente de inspección de definiciones, uso y contraste/escala |
| Superficies | `XauxaScreen`, `XauxaSection`, `XauxaTile`, `XauxaHeroCard`, `XauxaTileHeader`, `XauxaDialog` | Pendiente de inspección de implementación y consumidores |
| Acciones y entrada | `XauxaPrimaryButton`, `XauxaSecondaryButton`, `XauxaDangerButton`, `XauxaTextAction`, `XauxaIconButton`, `XauxaFilterChip`, `XauxaTextInput`, `XauxaSearchBar`, `XauxaSettingRow`, `XauxaFavoriteToggle` | Pendiente de inspección de estados, semántica, targets y usos |
| Feedback | `XauxaToast`, `XauxaStatusBanner`, `XauxaInlineResult`, `XauxaLoading`, `XauxaSkeleton`, `XauxaEmptyState`, `XauxaErrorPage`, `XauxaLoadMoreFooter` | Pendiente de inspección de estados y casos de producto |
| Datos | `XauxaListRow`, `XauxaStatBlock`, `XauxaBadge` | Pendiente de inspección de densidad, significado y accesibilidad |

## Secuencia de revisión

1. Leer las implementaciones y sus APIs públicas; detectar duplicados y estilos locales.
2. Buscar consumidores de producción y verificar que cada pieza resuelve una necesidad real de AgendaQr.
3. Registrar por componente una decisión, la regla que la justifica, los consumidores afectados y los cambios compatibles necesarios.
4. Migrar fundamentos compartidos antes de cambiar pantallas, para evitar estilos alternativos temporales.
5. Actualizar el catálogo del laboratorio junto con cada cambio real, sin marcar como verificado lo que aún no se haya probado.

## Restricción de alcance

La auditoría visual no autoriza cambiar reglas de negocio, modelos, repositorios, navegación funcional ni persistencia. Si una mejora de interfaz requiere alterar un flujo de dominio, se registra como decisión aparte y no se mezcla con la migración visual.

## Hallazgos de la primera inspección de código

Revisión estática inicial de `XauxaComponents.kt`, `XauxaExtendedComponents.kt`, `LabCatalogPane.kt` y búsquedas de consumidores en `feature/destinations/presentation`. Esto confirma la presencia y algunos contratos de API, pero no sustituye la inspección de todos los componentes, la revisión visual en ejecución ni las pruebas.

| Elemento revisado | Hallazgo de código | Decisión provisional |
|---|---|---|
| `XauxaScreen`, `XauxaSection`, `XauxaTile` | Composición compartida y superficies rectangulares; `XauxaTile` admite acción opcional y aplica anillo de foco propio. | Conservar como base; verificar contraste/foco y composición en pantalla al validar visualmente. |
| Botones primario/secundario/peligro y acción de texto | Usan altura mínima semántica, formas rectangulares y tokens de color/tipo. Los textos de botones se convierten incondicionalmente a mayúsculas. | Adaptar: preservar la capitalización editorial recibida, salvo patrón de etiqueta que justifique mayúsculas. Revisar en la migración de acciones para evitar cambios de contenido inesperados. |
| `XauxaIconButton` y filas interactivas | Declaran rol de botón, interacción/foco y descripción accesible en el botón de icono. | Conservar provisionalmente; confirmar navegación por teclado y tamaño táctil efectivo en plataformas. |
| `XauxaFilterChip` / `XauxaCategoryChip` | Hay dos piezas con semánticas distintas: filtro interactivo y etiqueta estática. | Conservar ambas; mantener diferenciación explícita en catálogo y usos. |
| `XauxaTextInput` | API ofrece etiqueta, error, modo de una línea y mínimo de líneas; implementación basada en `OutlinedTextField`. | Inspección parcial; revisar estados de ayuda/error, foco y accesibilidad en la siguiente pasada. |
| `XauxaMetrics.CatalogCardMinWidth` | El ancho mínimo de tarjeta del laboratorio ya está centralizado en token; el consumidor usa ese token en la cuadrícula adaptativa. | Conservar; no sustituir por literal local. |

### Consumidores de producción encontrados

La búsqueda del símbolo `XauxaPrimaryButton` encontró usos en autenticación, destinos, detalle/edición, importación y operaciones. Estos consumidores indican que el cambio de capitalización es transversal y debe hacerse con revisión de capturas/etiquetas y pruebas al final; no se debe alterar la lógica de callbacks ni los textos de negocio durante esa adaptación. La búsqueda no constituye un inventario exhaustivo de todos los componentes ni de todos sus consumidores.

### Siguiente paso

Completar la inspección por grupos (feedback, datos, campos y patrones), registrar excepciones de plataforma y después aplicar cambios compatibles a los componentes compartidos. Las pruebas locales siguen deliberadamente aplazadas hasta la fase final solicitada.

## Segunda pasada: feedback, datos, formularios y plataforma

Revisión estática del resto de `XauxaExtendedComponents.kt` y las piezas básicas de `XauxaComponents.kt`. Las conclusiones son de código fuente; la apariencia y comportamiento final aún requieren ejecución.

| Elemento | Hallazgo | Decisión / acción |
|---|---|---|
| `XauxaStatusBanner` | Ofrece solo variante neutral y de peligro mediante un booleano; no expresa éxito/advertencia/información con el enum semántico ya disponible. | Adaptar API a tono semántico compartido, manteniendo compatibilidad de llamada existente. Evitar cambiarlo hasta revisar usos y firma en conjunto. |
| `XauxaToast`, `XauxaInlineResult` | Componen colores por `XauxaTone`; las acciones son slots opcionales y el cierre del toast depende del llamador, sin temporizador oculto. | Conservar como bloques presentacionales; revisar anuncios de accesibilidad (live region) y contraste de cada tono en prueba visual. |
| `XauxaLoading`, `XauxaSkeleton`, `XauxaLoadMoreFooter` | Carga y skeleton son presentacionales; el footer distingue fin/carga/callback disponible. El skeleton impone al menos una línea. | Conservar provisionalmente; agregar/confirmar descripciones accesibles y evitar que un estado de carga quede comunicado solo visualmente. |
| `XauxaEmptyState`, `XauxaErrorPage` | Estados con CTA; la página de error usa color de peligro también para el título, sin depender de iconografía. | Conservar el patrón; verificar que el texto identifique la causa/recuperación y que no se use peligro para errores recuperables menores. |
| `XauxaBadge`, `XauxaStatBlock`, `XauxaListRow` | Badge distingue tono/solid; el bloque estadístico pone el valor neutral en Brand; fila admite interacción opcional y marcador semántico lateral. | Conservar provisionalmente; validar que el color no sea el único portador de estado y que las cifras tengan contexto comprensible para lector de pantalla. |
| `XauxaFavoriteToggle` | Usa `Role.Checkbox` y descripción, pero el modificador no declara explícitamente el valor `checked` en semántica. | Adaptar para exponer estado seleccionado y etiqueta dinámica (marcado/no marcado) sin alterar callback ni modelo de favoritos. |
| `XauxaSettingRow` | Toggle personalizado comunica acción como `Role.Button`; el estado checked no se expone como estado de control en semántica. Además, con checked presente y callback ausente se presenta un control visual que no puede cambiarse. | Adaptar semántica de control y contrato de callback; definir si fila deshabilitada o solo lectura cuando falte callback. Mantener separado de navegación de ajustes. |
| `XauxaTextInput` | El mensaje de error se dibuja como texto adyacente, pero la API no declara relación semántica explícita entre campo y error; tampoco hay slot de ayuda. | Adaptar en una pasada de formularios: asociación accesible de error/ayuda, sin cambiar validación de dominio. |
| `XauxaSearchBar` | La acción de limpiar se implementa con `XauxaTextAction` dentro del icono trailing; no se muestra si no hay texto. | Conservar con revisión de accesibilidad y comportamiento en teclado; comprobar que la acción no quede comprimida en anchos pequeños. |
| `XauxaFileUpload`, `XauxaScannerViewport`, `XauxaQrPreview` | Upload/viewport son presentacionales y delegan selección/cámara; `QrPreview` es expect por plataforma. | Mantener infraestructura por plataforma, pero no presentarlos en catálogo como integraciones funcionales hasta verificar cada target. |

### Orden de ejecución actualizado

1. Resolver primero semántica accesible de controles de estado (`FavoriteToggle`, toggle de `SettingRow`) y asociación de errores en campos.
2. Revisar los consumidores de estas APIs y mantener callbacks/modelos de producto intactos.
3. Consolidar feedback con un único enum de tonos donde aporte significado real, conservando compatibilidad de API.
4. Revisar componentes específicos de plataforma (cámara, selección de archivo y QR) por separado; la inspección commonMain no valida sus implementaciones Android/iOS.
5. Migrar las pantallas de producción de manera incremental y actualizar catálogo/documentación junto al código.
6. Ejecutar pruebas locales y comprobación visual únicamente en la fase final, como solicitó el usuario.

**Límite actual:** todavía no se han revisado exhaustivamente todos los usos de cada API ni los source sets de plataforma; por tanto, no se marca ningún grupo como completamente auditado.


## Cambio aplicado: semántica de toggles

Se actualizó la implementación común de `XauxaFavoriteToggle` para exponer su estado `On/Off` mediante semántica de control, conservando el callback y el rol de checkbox. En `XauxaSettingRow`, cuando existe `checked` y callback de cambio, ahora se comunica el estado y se declara rol de switch; la fila de navegación conserva rol de botón. Esto es un ajuste de semántica accesible, no una modificación del estado de producto.

**Pendiente:** revisar los consumidores de `XauxaSettingRow` para resolver explícitamente el caso `checked` sin callback (actualmente el estado se muestra, pero no es interactivo), y validar lector de pantalla, foco y teclado en Android/iOS. No se han ejecutado pruebas locales; continúan reservadas para la fase final.


## Cambio aplicado: errores de campo y consumidores del toggle

- `XauxaTextInput`: el mensaje de error ahora se entrega al slot `supportingText` del propio `OutlinedTextField`, en lugar de componerse como texto hermano independiente. Se conserva la API pública y la decisión de validación del llamador. La asociación debe confirmarse con TalkBack/VoiceOver durante la validación final.
- `XauxaSettingRow`: la búsqueda de usos halló ejemplos interactivos en el laboratorio (incluido el patrón de bloqueo de edición); no se identificó en esa búsqueda un consumidor de producción, por lo que no se afirma que el inventario sea exhaustivo. El estado sin callback se mantiene como presentación de solo lectura y no recibe acción clickable; revisar si se necesita un contrato explícito de disabled/read-only en una siguiente pasada.

**Pruebas locales y comprobación visual:** aplazadas deliberadamente hasta la fase final.


## Migración incremental de acciones y pantalla de destinos

- Los componentes compartidos `XauxaPrimaryButton`, `XauxaSecondaryButton` y `XauxaTextAction` ahora preservan la capitalización editorial que recibe cada llamada, en lugar de forzar mayúsculas. Se mantiene la tipografía, la geometría, los colores y los callbacks. Esto aplica el hallazgo transversal de la primera auditoría y evita que el componente altere el texto de producto.
- `DestinationsScreen`: los estados vacíos que estaban en inglés se localizaron al español y el placeholder de búsqueda aclara que se buscan destinos QR. No se modificó filtrado, paginación, acciones ni navegación.
- La revisión de esta pantalla confirma que ya compone búsqueda, filtros, banner de error, carga, estado vacío y lista paginada con componentes comunes. No se hizo una revisión visual ejecutada ni se considera cerrada la migración de toda la pantalla; quedan por revisar densidad/ajuste en pantallas estrechas y los textos de los demás flujos.

**Pruebas:** no ejecutadas, de acuerdo con la instrucción de reservarlas para el cierre.


## Siguiente pasada: editor y operaciones

- `DestinationEditorScreen`: título de creación/edición localizado al español. Se conserva el estado del formulario, persistencia, importación QR y selección de contexto sin cambios.
- `OperationScreens`: los tipos de operación visibles en la lista y en el resumen del detalle se presentan como “Pago” y “Cobro” en lugar de exponer los identificadores internos `PAGO`/`COBRO`. La traducción es exhaustiva para los dos valores de `OperationType` usados en esta pantalla; revisar si el dominio incorpora más tipos en el futuro.
- La inspección estática encontró que el editor y los flujos de operaciones ya usan componentes compartidos para campos, botones, banners, filas, tiles, diálogos y secciones. Persisten oportunidades de revisar disposición responsive, asociación de campos/errores, y estados de accesibilidad en ejecución.
- No se modificaron reglas de negocio, fechas, montos, guardado, asociación/eliminación de comprobantes ni navegación.

**Pruebas:** no ejecutadas; se mantienen reservadas para la fase final.


## Importación y contextos: pasada de consistencia

- `ImportBatchScreen`: el resumen de resultados ahora usa etiquetas descriptivas (“QR reconocidos”, “Comprobantes reconocidos”, “Duplicados”, “Elementos desconocidos para revisar”) en vez de depender de símbolos ✓/!/?. El estado se expresa con texto y conteo; no se altera el resultado ni la clasificación.
- `ImportReviewScreen`: lista de previsualizaciones QR y acciones principales ya usan componentes compartidos; queda pendiente validar comportamiento con lotes largos y accesibilidad del contenido QR en Android/iOS.
- `ContextScreen`: pantalla de lista/detalle ya está en español y consume filas/estados compartidos. El detalle presenta actividad usando el nombre interno del tipo de operación; queda señalado para unificar con las etiquetas de “Pago”/“Cobro” en la siguiente pasada.
- No se ejecutaron pruebas locales ni comprobaciones visuales en esta fase; la validación queda para el cierre acordado.


## Unificación de etiquetas de actividad

- `ContextScreen`: la actividad reciente ahora traduce los tipos internos de operación a “Pago” y “Cobro”, alineándose con la lista y el detalle de operaciones.
- La traducción se limita a la presentación; no cambia el modelo ni los valores persistidos.
- Las etiquetas largas en acciones de la bandeja de comprobantes siguen en mayúsculas porque son textos de llamada explícitos del flujo; revisar longitud y ajuste en pantallas estrechas durante la prueba visual.
- Pendiente de validación final: ejecutar pruebas/builds acordados, revisar tamaños de pantalla y escalado de texto, y comprobar semántica/lectura accesible de los controles y previsualizaciones QR en Android/iOS.


## Consistencia de tipo en el detalle de operación

- `OperationScreens`: el banner del detalle ahora usa la etiqueta de presentación “Pago”/“Cobro” en lugar del nombre interno enum en mayúsculas, alineado con la lista y actividad reciente.
- Cambio solo de presentación; no modifica lógica, modelo ni datos persistidos.
- Pendiente validar visualmente el banner en distintos anchos y escalas de texto junto con la pasada final de accesibilidad.


## Localización de acciones en detalle de destino

- `DestinationDetailScreen`: acciones de QR, regreso, edición, compartir y eliminación se presentan en español (“Mostrar QR”, “Volver”, “Editar”, “Compartir”, “Eliminar”), manteniendo la eliminación dentro del menú de desbordamiento.
- Ajuste de textos de interfaz únicamente; callbacks y comportamiento de acciones permanecen intactos.
- Pendiente revisar en validación visual final el ajuste de etiquetas en la barra contextual y la accesibilidad del menú.


## Localización de categorías de búsqueda global

- `GlobalSearchScreen`: las etiquetas de tipo de resultado ahora se muestran como “Contexto”, “QR”, “Actividad” y “Comprobante”, en lugar de exponer nombres internos del enum.
- La etiqueta se limita a presentación; identificadores, búsqueda y selección del resultado no cambian.
- Pendiente comprobar con pruebas de interfaz y revisión de lector de pantalla que el tipo del resultado se comunica de forma clara y que no se trunca en anchos reducidos.


## Pasada agrupada de consistencia de textos

- `DestinationsScreen`: la acción del estado vacío inicial se presenta como “Agregar QR”, coherente con la acción principal de la pantalla y el idioma de la interfaz.
- Cambio de texto únicamente; no modifica la condición del estado vacío ni el callback.
- En esta pasada se revisaron también las pantallas de editor, revisión de importación e importación por lotes: no se detectó otro literal inglés claro en los rótulos visibles inspeccionados que justificara un cambio sin contexto adicional.
- Validación visual, truncamiento y accesibilidad quedan agrupados para la ronda final; no se ejecutaron pruebas locales.


## Pasada agrupada: selector de tipo de operación

- `OperationScreens`: el selector de tipo presenta “Pago” y “Cobro” con capitalización natural, consistente con las etiquetas de lista, detalle y actividad.
- Los valores enum y callbacks seleccionados permanecen iguales; solo cambia el texto visible.
- El control sigue siendo un par de botones seleccionados/no seleccionados; pendiente revisar con teclado/lector de pantalla y escalado de texto durante la validación final.


## Normalización transversal de capitalización en acciones

- `XauxaComponents`: `XauxaSecondaryButton` y `XauxaTextAction` ya no convierten automáticamente las etiquetas a mayúsculas; respetan el texto entregado por cada pantalla.
- Esto permite capitalización natural en controles como el selector “Pago”/“Cobro” y evita que componentes compartidos alteren el contenido lingüístico del caller. Las pantallas que requieran mayúsculas explícitas pueden solicitarlas en su etiqueta.
- Cambio de presentación solamente; no altera enabled/loading, callbacks ni navegación.
- Pendiente revisión visual transversal de etiquetas existentes, wrapping en anchos estrechos, escalado tipográfico y accesibilidad en la ronda final. No se ejecutaron pruebas locales.


## Acciones de operaciones: capitalización

- Se normalizaron las etiquetas explícitas en mayúsculas de `OperationScreens` a capitalización de frase: asociar comprobante, crear operación, selector Pago/Cobro y adjuntar comprobante.
- Los componentes compartidos respetan ahora la etiqueta del caller, por lo que las pantallas declaran el casing deseado sin transformaciones ocultas.
- Pendiente inspección visual de textos largos y tamaños reducidos; no se ejecutaron pruebas locales en esta tanda.


## Toggle de favorito: anuncio contextual

- `XauxaFavoriteToggle` conserva `ToggleableState.On/Off` y ahora, si no se proporciona una descripción personalizada, anuncia “Marcar como favorito” en estado apagado y “Quitar de favoritos” en estado activado.
- La descripción explícita del caller sigue teniendo prioridad. El cambio aclara la acción disponible para tecnologías de asistencia sin alterar el callback ni el estado visual.
- Pendiente verificar TalkBack/VoiceOver y traducciones/localización en la revisión multiplataforma final; no se ejecutaron pruebas locales.


## SettingRow: semántica solo cuando el control es editable

- `XauxaSettingRow` ahora expone `toggleableState` únicamente cuando existen simultáneamente `checked` y `onCheckedChange`.
- Un estado `checked` sin callback ya no se anuncia como toggle editable; mantiene la ruta de interacción existente mediante `onClick` cuando corresponde.
- El cambio separa estado visual de capacidad de edición y conserva navegación/callbacks. Pendiente validación en TalkBack/VoiceOver durante la ronda final.


## Capitalización: segunda pasada de componentes y laboratorio

- `XauxaSection`, `XauxaDangerButton`, `XauxaHeroCard`, `XauxaBadge`, `XauxaFilterChip`, `XauxaStatBlock` y `XauxaTileHeader` ya no transforman automáticamente etiquetas/títulos/estado a mayúsculas; respetan la capitalización editorial de la pantalla o del catálogo.
- `LabChoiceChip` también respeta la etiqueta recibida, manteniendo el laboratorio alineado con el comportamiento real de los componentes compartidos.
- Se conserva la tipografía, pesos, letter spacing, geometría y comportamiento; el cambio es exclusivamente de presentación textual.
- El objetivo es eliminar transformaciones lingüísticas ocultas y permitir sentence case de forma consistente. La validación visual de los textos existentes queda para la ronda final.


## Estados de carga: adopción del patrón compartido

- `ContextScreen` usa `XauxaLoading` tanto en la lista de contextos como mientras espera el detalle, sustituyendo mensajes de carga sueltos.
- `ImportBatchScreen` usa el mismo patrón para los estados Importando, Analizando y Guardando, manteniendo el texto descriptivo de cada estado en el modelo de estado y el indicador visual común.
- Se reduce la variación visual entre pantallas sin cambiar estados de negocio, secuencia de importación ni callbacks. Pendiente revisión de anuncios accesibles, layout y pruebas de regresión en la validación final.


## Carga compartida: conservar el contexto textual

- `XauxaLoading` admite ahora un mensaje opcional junto al indicador, manteniendo compatibilidad con los usos sin mensaje.
- Los estados de Contextos, Importación por lote y autenticación pasan a mostrar el indicador común con su descripción contextual (“Cargando contextos…”, “Cargando contexto…”, “Importando…”, “Analizando…”, “Guardando elementos reconocidos…” e “Iniciando sesión…”).
- Esta ampliación evita que la unificación visual elimine la información de progreso que antes se mostraba como texto. Pendiente comprobar visualmente el ajuste de línea y el anuncio accesible del estado en ambas plataformas.


## Consistencia de búsqueda y acciones de comprobantes

- La búsqueda global comunica su estado activo con el indicador compartido y el mensaje “Buscando en Agenda QR…”.
- Las acciones del diálogo de comprobante recibido/duplicado usan capitalización de frase (“Cerrar”, “Guardando…”, “Listo”, “Asociar ahora”), alineadas con la regla de preservar el casing editorial y evitar mayúsculas forzadas.
- Pendiente validar lectura accesible de los estados de búsqueda y la adaptación de las acciones en anchos reducidos.


## Actualización del estado de auditoría: casing y carga

Esta nota complementa los hallazgos históricos anteriores; no los elimina, porque describen el estado observado en el momento de cada pasada:

- **Acciones:** los componentes de acción compartidos ya preservan el casing del texto que entrega cada caller. Los literales que aún aparezcan en mayúsculas deben revisarse en contexto de pantalla, no atribuirse a una transformación automática del componente.
- **Carga:** `XauxaLoading` admite `message: String?` opcional. Los consumidores de autenticación, contexto, importación por lote y búsqueda global inspeccionados pasan mensajes de estado específicos. La API sigue permitiendo el indicador sin texto cuando el contexto ya está explicado por otra parte de la pantalla.
- **Límite de verificación:** la presencia de mensajes visibles no confirma por sí sola que las plataformas los anuncien como estado a tecnologías de asistencia. No se ha hecho ejecución visual, prueba de lector de pantalla ni build en esta pasada.

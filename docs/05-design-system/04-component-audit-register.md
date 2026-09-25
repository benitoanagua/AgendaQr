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

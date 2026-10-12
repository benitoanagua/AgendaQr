# Laboratorio de componentes UI (Compose)

> **Nota V1.2 (2026-10-11, ADR-0012):** el catálogo del laboratorio refleja
> el diseño convencional sobre Material 3: envoltorios finos de los
> componentes M3 (botones, campos, listas, diálogos, chrome de
> navegación) y los mismos tokens de color/espaciado/forma/movimiento.
> Las entradas de componentes retirados (tiles, app bar inferior,
> turnstile, dot-progress, toast propio) se eliminan del catálogo con la
> migración.

## Propósito

Espacio aislado para desarrollar, inspeccionar y validar los componentes visuales de Agenda QR antes de integrarlos en los flujos de producto. El laboratorio es un catálogo ejecutable de componentes y tokens reales con su contrato (propiedades, estados, eventos, trazabilidad a Xauxa) y un preview interactivo con datos ficticios. No es una pantalla de producto ni introduce reglas de dominio.

## Autoridad y límites

La implementación respeta esta precedencia:

1. Agenda QR: producto, dominio, requisitos y reglas de negocio.
2. UX/UI V1 de Agenda QR: contrato de interacción y estados.
3. Xauxa Design System + Xauxa: tokens, lenguaje visual, componentes y patrones.
4. WaraWerse: referencia técnica para Kotlin Multiplatform, Compose, modularización y testing.

El laboratorio se construye dentro de Agenda QR. La referencia técnica aporta patrones de arquitectura, no lógica ni modelos de negocio. No se duplican tokens ni se redefinen los componentes normativos de Xauxa.

## Estado: implementado

El laboratorio existe en `core:ui` con arquitectura de catálogo + inspector. Anteriormente la única pantalla del laboratorio tenía errores de compilación (llamadas no validadas contra las firmas reales) e infringía dos compuertas de gobernanza (`RoundedCornerShape` prohibido por Xauxa regla 02, y una mención de texto que disparaba el filtro de leakage de producto). Esa implementación fue reemplazada por completo.

El host original era una actividad Android solo-debug (`ComponentLabActivity`). Ese host fue retirado: el laboratorio se ejecuta ahora en el navegador mediante un target WebAssembly de `core:ui` (patrón del host Wasm de WaraWerse), y la app Android de producción ya no depende del lab ni siquiera en builds debug.

### Modelo puro (sin dependencia de Compose, testeable sin UI)

`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/lab/model/`:

| Archivo | Responsabilidad |
|---|---|
| `LabCatalogModel.kt` | Tipos del contrato: categoría, propiedades, estados, eventos, tokens, clasificación de revisión (`VERIFICADO`/`DOCUMENTADO`/`PENDIENTE`/`NO SOPORTADO`), soporte de tema oscuro como eje propio, guía de uso (`whenToUse`), mapeos nativos y estado real por plataforma. |
| `LabComponentCatalog.kt` | **Inventario central**: 6 fundamentos + 31 componentes. Fuente única de los metadatos que muestra el inspector. |
| `LabInteractionMatrix.kt` | Lentes canónicas de interacción (Normal/Foco/Presionado/Deshabilitado/Carga) con veredicto honesto: `REAL` (estado declarado que la API ofrece), `SIMULADA` (lente transitoria observable en el preview) o `NO APLICA`. |
| `LabSearch.kt` | Búsqueda y filtrado por categoría como funciones puras. |
| `LabCatalogIntegrity.kt` | Reglas de integridad del inventario; corren en las pruebas unitarias y respaldan el indicador del encabezado del lab. |
| `LabTokens.kt` | Índice de los nombres de tokens que existen realmente en `XauxaColor`/`XauxaSpacing`/`XauxaMetrics`/`XauxaType`/`XauxaMotion`; valida que ningún contrato referencie tokens inexistentes. |
| `LabAccessibilityGate.kt` | Puerta de accesibilidad por contrato (callback, target 48dp en acciones, contentDescription, nombre accesible). Adaptada de `WwAccessibilityGate`. |
| `LabApiAudit.kt` | Auditoría de API (props duplicadas, sobrecarga de estados, knobs de color crudo). Adaptada de `WwApiAudit`. |
| `LabApiFreeze.kt` | Congelado del inventario: altas/bajas exigen actualización deliberada. Adaptado de `WwApiFreeze`. |
| `LabPatterns.kt` | 8 patrones de composición como datos puros (pasos + contratos que componen + marca de demo). Valida que solo referencien contratos reales. |

### UI derivada del modelo

`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/lab/`:

| Archivo | Responsabilidad |
|---|---|
| `AgendaQrComponentLab.kt` | Entrada pública. Layout adaptable por `WindowSizeClass`: en compacto la página colapsa a una única columna con un solo dueño de scroll; en medium/expanded, catálogo e inspector en dos paneles con scroll independiente. Toggle de preview claro/oscuro. Navegación por secciones Fundamentos/Componentes/Patrones. |
| `LabCatalogPane.kt` | Secciones (Fundamentos/Componentes/Patrones con conteos), búsqueda por nombre/propósito/uso/estados/tags, filtros por categoría con conteo de componentes, indicador de selección y estado vacío explícito de búsqueda. Las selecciones usan tokens Xauxa (Brand), no el tinte Material. |
| `LabInspectorPane.kt` | Inspector derivado 100 % del contrato: identidad, propósito, preview, matriz de interacción, estados declarados con su clasificación, API (props/eventos), guía de uso, mapeo nativo y plataformas, gobierno (puerta a11y + auditoría API), tokens Xauxa, uso en AgendaQr, restricciones y registro de eventos. |
| `LabPatternPane.kt` | Detalle de patrón: escenario vivo con los contratos reales compuestos y datos ficticios, pasos, contratos que compone (resueltos contra el inventario, con salto a su inspector) y nota explícita de demo cuando aplica. |
| `LabComponentPreview.kt` | Previews interactivos del componente real con datos ficticios y estado local. Los callbacks producen respuesta visible (cambio de estado + entrada en el registro de eventos), nunca un callback vacío. |
| `LabFoundationPreview.kt` | Especímenes de los token groups reales: paleta de color (incluye fondos `-bg` y anillo de foco), escala de espaciado, escala tipográfica, métricas, motion y foco. |
| `LabChrome.kt` | Piezas compartidas del lab (paneles, badges con texto — nunca solo color, filas clave/valor, registro de eventos). |

### Host WebAssembly (wasmJs)

`core/ui/src/wasmJsMain/` — el único host del laboratorio; invisible para los builds Android/iOS:

| Archivo | Responsabilidad |
|---|---|
| `kotlin/.../lab/web/AgendaQrComponentLabWebMain.kt` | Entrypoint del navegador: `ComposeViewport(viewportContainerId = "ComposeTarget")` iniciando directamente en `AgendaQrComponentLab`. Sin navegación, sin servicios, sin runtime de producción. |
| `kotlin/.../components/XauxaQrPreview.wasmJs.kt` | `actual` del contrato expect: placeholder blanco honesto (sin ruta de decodificación en navegador), registrado como PENDIENTE en el catálogo. |
| `resources/index.html` + `resources/styles.css` | Página host y reset de viewport para el canvas de Compose (patrón del host Wasm de la referencia). |

Build: el target se declara en `core/ui/build.gradle.kts` (`wasmJs { browser { ... } }` con `outputFileName = "agendaqr-component-lab.js"`); las tareas raíz `componentLabWeb` y `syncComponentLabWeb` sincronizan la distribución a `build/web/component-lab/`.

### Patrones reutilizados de la referencia técnica (WaraWerse) y por qué encajan

- **Modelo puro separado de Compose**: su laboratorio define los contratos en `catalog/model` sin UI, lo que permite probar inventario y reglas sin levantar Compose. AgendaQr necesita exactamente eso: las reglas de integridad y búsqueda son lógica pura.
- **Contrato como fuente única del inspector**: evita `when` duplicados y listas desincronizadas; el inspector deriva todo del catálogo.
- **Matriz de interacción con veredicto honesto**: distinguir estado `REAL` de lente `SIMULADA` y marcar `N/A` cuando la lente no aplica evita presentar una matriz de revisión como evidencia de pruebas.
- **Registro de eventos visibles**: los previews interactivos jamás usan callbacks vacíos como única representación.
- **Scroll ownership explícito**: en compacto un único scroll de página; en ancho cada panel scrollea independiente. Previene conflictos de scroll anidado en móvil.
- **Adaptación deliberada**: se omitió la lente Hover (la referencia es web/wasm; AgendaQr es táctil Android/iOS y no hay superficie de puntero en el producto — el que el host web del lab tenga puntero no crea estados hover en los componentes bajo inspección). No se copiaron nombres `Ww*`, dominio ni mecánicas.

## Aislamiento de producción

- El lab vive en `core:ui` pero **no está referenciado por ninguna pantalla de producción**; la app de producto (`AgendaQrApp`) no lo importa y `androidApp` ya no declara dependencia hacia el lab (el `debugImplementation(project(":core:ui"))` del host Android fue retirado junto con la actividad).
- El único host es el target `wasmJs` de `core:ui`: el entrypoint `core/ui/src/wasmJsMain/kotlin/com/agendaqr/core/ui/lab/web/AgendaQrComponentLabWebMain.kt` abre `AgendaQrComponentLab` en el navegador. El source set `wasmJsMain` es invisible para los builds Android/iOS de producción.
- El lab no accede a navegación, repositorios, servicios, autenticación, cámara, almacenamiento ni notificaciones. El host web no añade ninguna dependencia de producción: solo Compose Multiplatform y el propio módulo.
- El `actual` wasm de `XauxaQrPreview` muestra placeholder (igual que iOS): no existe ruta de decodificación en navegador y el catálogo lo registra como PENDIENTE, no como implementado.

## Cómo abrirlo (navegador / WebAssembly)

```bash
./gradlew componentLabWeb          # compila el Wasm y sincroniza build/web/component-lab/
python3 -m http.server 8000 -d build/web/component-lab
# abrir http://localhost:8000 — nunca file:// (Wasm requiere servidor HTTP)
```

El host inicia directamente en el catálogo de componentes de AgendaQr: búsqueda y filtros, selección con inspector (contrato, matriz de interacción, estados, tokens, uso), previews interactivos, especímenes de fundamentos y toggle de preview claro/oscuro. La alternativa de un solo paso es la tarea estándar del plugin: `./gradlew :core:ui:wasmJsBrowserDevelopmentRun` (servidor de desarrollo de webpack).

## Inventario del catálogo

Categorías reales tras inspección del código (no se registran componentes que solo existen en documentación):

| Categoría | Entradas |
|---|---|
| Fundamentos | `XauxaColor`, `XauxaSpacing`, `XauxaMetrics`, `XauxaType`, `XauxaMotion`, `XauxaShapes` |
| Superficies | `XauxaScreen`, `XauxaSection`, `XauxaDialog` |
| Acciones | `XauxaPrimaryButton`, `XauxaSecondaryButton`, `XauxaDangerButton`, `XauxaTextAction`, `XauxaIconButton`, `XauxaFavoriteToggle`, `XauxaFilterChip`, `XauxaTextInput`, `XauxaSearchBar`, `XauxaSearchTrigger` |
| Chrome | `XauxaTopBar`, `XauxaNavBar`, `XauxaNavRail`, `XauxaScreenScaffold` |
| Feedback | `XauxaStatusBanner`, `XauxaLoading`, `XauxaSkeleton`, `XauxaEmptyState`, `XauxaLoadMoreFooter`, `XauxaFeedbackEvent` |
| Datos | `XauxaListRow`, `XauxaBadge`, `XauxaCategoryChip` |
| QR y comprobantes | `XauxaQrPreview`, `XauxaScannerViewport`, `XauxaMediaFrame` |

La paginación se compone de `XauxaLoadMoreFooter`: no existe un componente de paginación duplicado. Las integraciones reales pendientes (cámara, decodificación QR en web/iOS) están marcadas PENDING o demo en sus contratos, nunca como implementadas.

## Clasificación honesta (resumen)

- **Verificado en código**: estados Default/Estático/Interactivo/Neutro/Peligro/Marcado/Sin marcar, Deshabilitado en botones/inputs/chips con `enabled`, Carga con `isLoading` en botones y footer, placeholder de QR, render de QR en Android (decodificación base64 → bitmap), y los pares de contraste del esquema.
- **Heredado de M3**: los estados del componente subyacente (ripple, foco, selección) se muestran tal cual; la matriz de interacción los declara heredados del envoltorio.
- **Pendiente**: render de QR en iOS y web, escaneo real (cámara).
- **Pendiente de validación visual**: esquema oscuro de los componentes en dispositivo.

La matriz de interacción del inspector distingue además estado `REAL`, lente `SIMULADA` (foco/presionado, observables en el preview pero no forzables por API) y `NO APLICA`.

## Trazabilidad Xauxa

El detalle token por token y componente documentado vs implementado está en `docs/05-design-system/04-component-audit-register.md`.

## Reglas de arquitectura (vigentes)

- Kotlin idiomático: funciones pequeñas, estado inmutable y dependencias explícitas.
- Compose Multiplatform según las convenciones del repositorio; el lab compila en los targets configurados de `core:ui` (Android + iOS + Wasm, donde Wasm es el host del laboratorio).
- Las muestras no importan repositorios ni casos de uso de negocio.
- Los componentes reciben sus valores y callbacks desde parámetros; no acceden a singletons o servicios globales.
- El chrome del lab usa Material 3 como infraestructura con los colores de `MaterialTheme`, que siguen al esquema; los componentes bajo inspección usan sus tokens Xauxa reales del esquema vigente.
- Sin valores crudos: sin `dp`/`sp`/hex/`Color(...)` fuera de la capa de tokens, sin iconos Material (invariante del set de iconos). Las compuertas `verifyDesignSystemCompliance` (Kotlin), `verifyWebDesignSystem` (CSS del host) y `verifyDesignSystemFixtures` (autoprueba de las reglas) lo verifican. La fuente canónica es `XauxaTokens.kt`; no existe snapshot JSON que sincronizar.

## Validación

El lab es un entorno de inspección, no una puerta de calidad por sí
mismo; su contrato (integridad del inventario, búsqueda, matriz de
interacción, gobierno, patrones) corre en
`./gradlew :core:ui:testDebugUnitTest` y la compilación de los tres
targets se valida con `./gradlew :core:ui:build`. La regresión visual de
las pantallas de producto vive en Roborazzi (ADR-0007) sobre la suite
Robolectric del feature, no en el lab.

- `LabCatalogIntegrityTest`: ids únicos, metadatos obligatorios, estados clasificados, tokens referenciados existentes, cobertura exacta de los componentes y fundamentos reales de `core:ui`, honestidad del soporte de tema oscuro y del render web del QR (PENDING hasta que exista decodificación real en navegador).
- `LabSearchTest`: búsqueda y filtros por categoría con conteos coherentes con el inventario.
- `LabInteractionMatrixTest`: la matriz distingue estado `REAL`, lente `SIMULADA` y `NO APLICA`; los estados del componente M3 subyacente se declaran heredados del envoltorio.
- `LabGovernanceTest`: congelado del inventario, auditoría de API, puerta de accesibilidad (target 48 dp, contentDescription) y documentación por componente.
- `LabPatternsTest` / `XauxaSchemeTest`: patrones contra contratos reales y esquemas completos con pares de contraste verificados.

## Limitaciones conocidas

1. **Pixel-review manual pendiente (parcial)**: queda tab-through completo del lab en navegador y revisión del foco sobre componentes concretos.
2. **Ejecución de tests iOS**: los tests compilan para los tres targets iOS (klibrary) pero no pueden ejecutarse en este host Linux (requieren macOS + simulador). La compilación de los targets iOS sigue validada por `:core:ui:build`.
3. **iOS sin host del lab**: el framework de iOS solo expone la app de producción; abrir el lab en iOS requeriría un host dedicado — pendiente.
4. **Wasm sin navegación/hash**: el host web no implementa deep links por hash; la selección no es compartible por URL.
5. **QR placeholder en el host web**: el `actual` wasm de `XauxaQrPreview` no decodifica (como el de iOS); el catálogo lo registra como PENDIENTE y el preview avisa en pantalla. Igual para escaneo real (cámara).

## Próximos pasos concretos

1. Tab-through completo y pixel-review del foco en el navegador usando los comandos de «Cómo abrirlo».
2. Integrar cámara (CameraX/AVFoundation/getUserMedia) y decodificación QR en iOS/web para convertir los PENDING en implementaciones.
3. Host iOS del lab para inspección en simulador cuando haya acceso a macOS.

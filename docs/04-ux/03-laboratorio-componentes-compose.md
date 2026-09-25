# Laboratorio de componentes UI (Compose)

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
| `AgendaQrComponentLab.kt` | Entrada pública. Layout adaptable: por debajo de 640 dp (breakpoint documentado por Xauxa §07, expuesto como `XauxaMetrics.BreakpointMedium`) la página colapsa a una única columna con un solo dueño de scroll; por encima, catálogo e inspector en dos paneles con scroll independiente. Toggle de preview claro/oscuro. Navegación por secciones Fundamentos/Componentes/Patrones. |
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
| Fundamentos | `XauxaColor`, `XauxaSpacing`, `XauxaMetrics`, `XauxaType`, `XauxaMotion`, `XauxaFocus` |
| Superficies | `XauxaScreen`, `XauxaSection`, `XauxaTile`, `XauxaTileHeader`, `XauxaHeroCard`, `XauxaDialog` |
| Acciones | `XauxaPrimaryButton`, `XauxaSecondaryButton`, `XauxaDangerButton`, `XauxaTextAction`, `XauxaIconButton`, `XauxaFilterChip`, `XauxaTextInput`, `XauxaSearchBar`, `XauxaSettingRow`, `XauxaFavoriteToggle` |
| Feedback | `XauxaToast`, `XauxaStatusBanner`, `XauxaInlineResult`, `XauxaLoading`, `XauxaSkeleton`, `XauxaEmptyState`, `XauxaErrorPage`, `XauxaLoadMoreFooter` |
| Datos | `XauxaListRow`, `XauxaStatBlock`, `XauxaBadge`, `XauxaCategoryChip`, `XauxaFavoriteIndicator` |
| QR y comprobantes | `XauxaQrPreview`, `XauxaScannerViewport`, `XauxaFileUpload` |
| Patrones | primera-vez, fallo-puntual, fallo-contenido, fallo-aplicacion, pantalla-completa, incorporacion (demo), bloqueo-opcional, confirmacion-destructiva (demo) |

La paginación se compone de `XauxaLoadMoreFooter`: no existe un componente de paginación duplicado. `XauxaHeroCard` compone `XauxaTile`. Las integraciones reales pendientes (picker de archivos, cámara, persistencia de favorito, decodificación QR en web/iOS) están marcadas PENDING o demo en sus contratos, nunca como implementadas.

## Clasificación honesta (resumen)

- **Verificado en código**: estados Default/Estático/Interactivo/Neutro/Peligro/Marcado/Sin marcar, Deshabilitado en botones/inputs/chips/icono con `enabled`, Carga con `isLoading` en botones y footer, placeholder de QR, render de QR en Android (decodificación base64 → bitmap), fondos `-bg` y anillo de foco.
- **Documentado en Xauxa, sin implementar**: tercer tono del banner (success/info/warning existen como tokens pero el banner solo distingue neutro/peligro), integración de producto del favorite toggle (control real, demo sin persistencia).
- **Pendiente**: render de QR en iOS y web, escaneo real (cámara), picker real de archivos, familias tipográficas Archivo/Roboto, adopción de `XauxaMotion` en componentes.
- **Pendiente de validación visual**: `enabled` en `XauxaTextAction`, carga determinada en `XauxaLoading` (brechas de API) y el **esquema oscuro de los 31 componentes** (resuelven el esquema vía `XauxaColor`; valores de referencia pendientes de QA de producto).

La matriz de interacción del inspector distingue además estado `REAL`, lente `SIMULADA` (foco/presionado, observables en el preview pero no forzables por API) y `NO APLICA`.

## Trazabilidad Xauxa

El detalle token por token y componente documentado vs implementado está en `docs/05-design-system/03-xauxa-brechas-y-trazabilidad.md`.

## Reglas de arquitectura (vigentes)

- Kotlin idiomático: funciones pequeñas, estado inmutable y dependencias explícitas.
- Compose Multiplatform según las convenciones del repositorio; el lab compila en los targets configurados de `core:ui` (Android + iOS + Wasm, donde Wasm es el host del laboratorio).
- Las muestras no importan repositorios ni casos de uso de negocio.
- Los componentes reciben sus valores y callbacks desde parámetros; no acceden a singletons o servicios globales.
- El chrome del lab usa Material 3 como infraestructura (no hay equivalentes Xauxa de campo de búsqueda o chips) con los colores de `MaterialTheme`, que siguen al esquema; los componentes bajo inspección usan sus tokens Xauxa reales del esquema vigente. El preview oscuro aplica el esquema de referencia pendiente de validación, marcado PENDING en cada contrato.
- Sin valores crudos: sin `dp`/`sp`/hex/`Color(...)` fuera de la capa de tokens, sin radios (Xauxa regla 02), sin iconos Material (invariante del set de iconos). Las compuertas `verifyDesignSystemCompliance` (Kotlin), `verifyWebDesignSystem` (CSS del host) y `verifyDesignSystemFixtures` (autoprueba de las reglas) lo verifican. La fuente canónica es `XauxaTokens.kt`; no existe snapshot JSON que sincronizar.

## Validación ejecutada (host Linux x86_64, JDK 17, rama feat/xauxa-design-system-catalog)

| Comando | Resultado |
|---|---|
| `./gradlew :core:ui:testDebugUnitTest` (con `--rerun-tasks`) | **PASS** — 40 tests (12 integridad + 8 búsqueda + 7 matriz + 5 gobierno + 3 patrones + 5 esquemas), 0 fallos. |
| `./gradlew :feature:destinations:domain/data/presentation:testDebugUnitTest` | **PASS** — 43 + 35 + 3 tests, 0 fallos (sin cambios de lógica; solo tipografía de componentes compartidos). |
| `./gradlew :androidApp:assembleDebug` y `assembleRelease` | **PASS** — producción compila en ambas variantes con los componentes actualizados. |
| `./gradlew componentLabWeb` | **PASS** — target Wasm compilado y empaquetado; `build/web/component-lab/` con `index.html`, `styles.css`, `agendaqr-component-lab.js` y los `.wasm`. |
| `./gradlew verifyAgendaQrArchitecture` | **PASS** — compliance Kotlin, boundaries, CSS web (`verifyWebDesignSystem`) y autofixtures (`verifyDesignSystemFixtures`). |
| `./gradlew :androidApp:assembleDebug` | **PASS** — producción sin el lab. |
| `python3 -m http.server` + Chromium headless | **200 OK** — renderizado real revisado (ver «Revisión visual»): secciones, inspector completo, patrones, preview oscuro, layout 390px y foco por teclado en el chrome. |
| Revisión visual en navegador | **EJECUTADA (headless)** — Chromium headless vía `chrome-headless-shell` + `puppeteer-core` sobre el build actual (verificado con `38 entradas` en el encabezado). Evidencia en `/tmp/opencode/shots/` (no versionadas): `v3-foundations` (sección Fundamentos), `v2/v3-button` (botones con toggles enabled/isLoading), `v2/v3-button→textinput` (campo con estados error/textarea), `v2-toast` + `v2-rowagg→stat` (feedback y stats mono), `v9-tileheader` (banda de marca), `v3-pattern(-tall)` (escenario vivo fallo-puntual), `v5/v3-dark*` (oscuro real), `lab-390` (compacto), `lab-focus` (foco por teclado). Pendiente: tab-through completo y pixel-review del anillo teal sobre un componente. |

Pruebas del modelo (`core/ui/src/commonTest/kotlin/com/agendaqr/core/ui/lab/`):

- `LabCatalogIntegrityTest` (12): ids únicos, metadatos obligatorios, estados clasificados y descritos, tokens referenciados existentes, cobertura exacta de los componentes y fundamentos reales de `core:ui`, honestidad del soporte de tema oscuro, categorías válidas y honestidad del render web del QR (PENDING hasta que exista decodificación real en navegador).
- `LabSearchTest` (8): búsqueda por nombre/propósito/uso/tags/estados, insensibilidad a mayúsculas, filtros por categoría, combinación de filtros, estado vacío, conteos por categoría (5 superficies, 10 acciones, 8 feedback, 5 datos, 3 QR, 6 fundamentos).
- `LabInteractionMatrixTest` (7): Normal real para todo, Disabled real solo con `enabled` en API, la brecha de `XauxaTextAction` sin `enabled`, Loading real en `XauxaLoading` y en botones con `isLoading`, foco/presionado simulados solo en interactivos, los estados `NOT_SUPPORTED` nunca resuelven a estado real, fundamentos sin lentes transitorias.
- `LabGovernanceTest` (5): congelado del inventario, auditoría sin errores, puerta de accesibilidad para todo el catálogo, token 48dp en acciones interactivas, guía/mapeos/plataformas documentados por componente.
- `LabPatternsTest` (3): los patrones solo referencian contratos reales, cubren las 8 composiciones exigidas y las demos declaran su limitación.
- `XauxaSchemeTest` (5): el esquema claro fija la apariencia de producción (sin cambios ciegos), `surface3` documenta la propuesta nueva, el esquema oscuro porta los valores de referencia, ambos esquemas están completos y conmutan, y ambos exponen el mismo conjunto de 21 campos semánticos requeridos (sin tokens solo-dark).

## Limitaciones conocidas

1. **Pixel-review manual pendiente (parcial)**: el renderizado se revisó en Chromium headless (secciones, inspector, patrones, oscuro, 390px, foco en chrome). Queda tab-through completo y pixel-review del anillo teal sobre un componente Xauxa.
2. **Ejecución de tests iOS**: los tests compilan para los tres targets iOS (klibrary) pero no pueden ejecutarse en este host Linux (requieren macOS + simulador). La compilación de los targets iOS sigue validada por `:core:ui:build`.
3. **iOS sin host del lab**: el framework de iOS solo expone la app de producción; abrir el lab en iOS requeriría un host dedicado (p. ej. pantalla de debug tras una build flag) — pendiente.
4. **Sin pruebas visuales/screenshot**: no existe infraestructura de screenshot tests en el repositorio (sin Roborazzi/Paparazzi). Propuesta en Próximos pasos.
5. **El lab muestra el estado, no lo maquilla**: el preview oscuro aplica el esquema de referencia (PENDING de validación) en lugar de simular un oscuro verificado.
6. **QR placeholder en el host web**: el `actual` wasm de `XauxaQrPreview` no decodifica (como el de iOS); el catálogo lo registra como PENDIENTE y el preview avisa en pantalla. Igual para escaneo real (cámara) y picker de archivos.
7. **Wasm sin navegación/hash**: a diferencia de la referencia, el host web de AgendaQr no implementa deep links por hash; la selección no es compartible por URL.

## Próximos pasos concretos

1. Pixel-review manual del anillo de foco teal y tab-through completo en el navegador usando los comandos de «Cómo abrirlo».
2. Validación visual del esquema oscuro de referencia (hoy PENDING en los 31 componentes); hasta entonces, el preview oscuro del lab lo aplica como referencia, no como verificado.
3. Cerrar brechas de API priorizadas: `enabled` en `XauxaTextAction` y tercer tono del banner (tokens `-bg` ya existen).
4. Integrar cámara (CameraX/AVFoundation/getUserMedia), picker de archivos y persistencia de favorito para convertir los PENDING/demos en implementaciones.
5. Habilitar screenshot tests para `core:ui` con Roborazzi (multiplataforma, se ejecuta en el host JVM de Android): registrar previews del lab como fixtures doradas y validar tema claro/oscuro sin dispositivo. Requiere añadir la dependencia y una tarea de CI; hoy no existe.
6. Host iOS del lab para inspección en simulador cuando haya acceso a macOS.

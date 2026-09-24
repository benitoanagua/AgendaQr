# Laboratorio de componentes UI (Compose)

## Propósito

Espacio aislado para desarrollar, inspeccionar y validar los componentes visuales de Agenda QR antes de integrarlos en los flujos de producto. El laboratorio es un catálogo ejecutable de componentes y tokens reales con su contrato (propiedades, estados, eventos, trazabilidad a Xauxa) y un preview interactivo con datos ficticios. No es una pantalla de producto ni introduce reglas de dominio.

## Autoridad y límites

La implementación respeta esta precedencia:

1. Agenda QR: producto, dominio, requisitos y reglas de negocio.
2. UX/UI V1 de Agenda QR: contrato de interacción y estados.
3. Xauxa Design System + XauxaXcan: tokens, lenguaje visual, componentes y patrones.
4. WaraWerse: referencia técnica para Kotlin Multiplatform, Compose, modularización y testing.

El laboratorio se construye dentro de Agenda QR. La referencia técnica aporta patrones de arquitectura, no lógica ni modelos de negocio. No se duplican tokens ni se redefinen los componentes normativos de Xauxa.

## Estado: implementado

El laboratorio existe en `core:ui` con arquitectura de catálogo + inspector. Anteriormente la única pantalla del laboratorio tenía errores de compilación (llamadas no validadas contra las firmas reales) e infringía dos compuertas de gobernanza (`RoundedCornerShape` prohibido por Xauxa regla 02, y una mención de texto que disparaba el filtro de leakage de producto). Esa implementación fue reemplazada por completo.

El host original era una actividad Android solo-debug (`ComponentLabActivity`). Ese host fue retirado: el laboratorio se ejecuta ahora en el navegador mediante un target WebAssembly de `core:ui` (patrón del host Wasm de WaraWerse), y la app Android de producción ya no depende del lab ni siquiera en builds debug.

### Modelo puro (sin dependencia de Compose, testeable sin UI)

`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/lab/model/`:

| Archivo | Responsabilidad |
|---|---|
| `LabCatalogModel.kt` | Tipos del contrato: categoría, propiedades, estados, eventos, tokens, clasificación de revisión (`VERIFICADO`/`DOCUMENTADO`/`PENDIENTE`/`NO SOPORTADO`) y soporte de tema oscuro como eje propio. |
| `LabComponentCatalog.kt` | **Inventario central**: 4 fundamentos + 11 componentes. Fuente única de los metadatos que muestra el inspector. |
| `LabInteractionMatrix.kt` | Lentes canónicas de interacción (Normal/Foco/Presionado/Deshabilitado/Carga) con veredicto honesto: `REAL` (estado declarado que la API ofrece), `SIMULADA` (lente transitoria observable en el preview) o `NO APLICA`. |
| `LabSearch.kt` | Búsqueda y filtrado por categoría como funciones puras. |
| `LabCatalogIntegrity.kt` | Reglas de integridad del inventario; corren en las pruebas unitarias y respaldan el indicador del encabezado del lab. |
| `LabTokens.kt` | Índice de los nombres de tokens que existen realmente en `XauxaColor`/`XauxaSpacing`/`XauxaMetrics`/`XauxaType`; valida que ningún contrato referencie tokens inexistentes. |

### UI derivada del modelo

`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/lab/`:

| Archivo | Responsabilidad |
|---|---|
| `AgendaQrComponentLab.kt` | Entrada pública. Layout adaptable: por debajo de 640 dp (breakpoint documentado por Xauxa §07, expuesto como `XauxaMetrics.BreakpointMedium`) la página colapsa a una única columna con un solo dueño de scroll; por encima, catálogo e inspector en dos paneles con scroll independiente. Toggle de preview claro/oscuro. |
| `LabCatalogPane.kt` | Búsqueda por nombre/propósito/uso/estados/tags, filtros por categoría con conteo de componentes, indicador de selección y estado vacío explícito de búsqueda. |
| `LabInspectorPane.kt` | Inspector derivado 100 % del contrato: identidad, propósito, matriz de interacción, estados declarados con su clasificación, API (props/eventos), tokens Xauxa, uso en AgendaQr, restricciones y registro de eventos. |
| `LabComponentPreview.kt` | Previews interactivos del componente real con datos ficticios y estado local. Los callbacks producen respuesta visible (cambio de estado + entrada en el registro de eventos), nunca un callback vacío. |
| `LabFoundationPreview.kt` | Especímenes de los token groups reales: paleta de color, escala de espaciado, escala tipográfica y métricas. |
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
| Fundamentos | `XauxaColor`, `XauxaSpacing`, `XauxaMetrics`, `XauxaType` |
| Superficies | `XauxaScreen`, `XauxaSection`, `XauxaTile` |
| Acciones | `XauxaPrimaryButton`, `XauxaSecondaryButton`, `XauxaTextAction` |
| Feedback | `XauxaStatusBanner`, `XauxaLoading`, `XauxaEmptyState` |
| Datos | `XauxaFavoriteIndicator` |
| QR y comprobantes | `XauxaQrPreview` |

De la lista de inspección de la tarea, estos equivalentes **no existen** en `core:ui` y quedan fuera del catálogo (registrados como brechas en el documento de trazabilidad): campo de texto, chips de filtro, diálogos/confirmaciones, filas de lista, search bar, skeleton, load more, favorite *toggle* interactivo.

## Clasificación honesta (resumen)

- **Verificado en código**: estados Default/Estático/Interactivo/Neutro/Peligro/Marcado/Sin marcar, Deshabilitado en botones con `enabled`, carga indeterminada, placeholder de QR, render de QR en Android (decodificación base64 → bitmap).
- **Documentado en Xauxa, sin implementar**: tonos success/info/warning del banner (C13/C17), skeleton (C25), favorite toggle interactivo (C21), tokens de fondo `-bg` de la v13.
- **Pendiente**: render de QR en iOS (placeholder hasta validación de hardware), familias tipográficas Archivo/Roboto.
- **No soportado (brechas de API verificadas contra el código)**: `isLoading` en botones (la matriz de estados de Xauxa §10 lo exige), `enabled` en `XauxaTextAction`, carga determinada en `XauxaLoading`, **tema oscuro en todos los componentes** (todos consumen tokens claros fijos; `AgendaQrTheme` solo cambia el esquema Material).

La matriz de interacción del inspector distingue además estado `REAL`, lente `SIMULADA` (foco/presionado, observables en el preview pero no forzables por API) y `NO APLICA`.

## Trazabilidad Xauxa

El detalle token por token y componente documentado vs implementado está en `docs/05-design-system/03-xauxa-brechas-y-trazabilidad.md`.

## Reglas de arquitectura (vigentes)

- Kotlin idiomático: funciones pequeñas, estado inmutable y dependencias explícitas.
- Compose Multiplatform según las convenciones del repositorio; el lab compila en los targets configurados de `core:ui` (Android + iOS + Wasm, donde Wasm es el host del laboratorio).
- Las muestras no importan repositorios ni casos de uso de negocio.
- Los componentes reciben sus valores y callbacks desde parámetros; no acceden a singletons o servicios globales.
- El chrome del lab usa Material 3 como infraestructura (no hay equivalentes Xauxa de campo de búsqueda o chips) con los colores de `MaterialTheme`, que sí se adaptan al preview oscuro; los componentes bajo inspección usan sus tokens Xauxa reales, que no se adaptan. Esa diferencia es deliberada: expone la brecha real de tema oscuro en lugar de simularla.
- Sin valores crudos: sin `dp`/`sp`/hex/`Color(...)` fuera de la capa de tokens, sin radios (Xauxa regla 02), sin iconos Material (invariante del set de iconos). La compuerta `verifyDesignSystemCompliance` lo enforcement.

## Validación ejecutada (host Linux x86_64, JDK 17, rama feat/component-lab-wasm)

| Comando | Resultado |
|---|---|
| `./gradlew :core:ui:testDebugUnitTest` / `testReleaseUnitTest` | **PASS** — 27 tests (12 integridad + 8 búsqueda + 7 matriz de interacción), 0 fallos en ambas variantes. |
| `./gradlew componentLabWeb` | **PASS** — target Wasm compilado y empaquetado por webpack 5.100.2; `build/web/component-lab/` con `index.html`, `styles.css`, `agendaqr-component-lab.js` y los `.wasm` (app + skiko). |
| `./gradlew :core:ui:build` | **PASS** — Android debug/release, targets iOS (iosX64/iosArm64/iosSimulatorArm64), Wasm, lint y tests. `wasmJsBrowserTest` deshabilitado: su ejecución exige navegador headless (Karma + Chrome) que no forma parte del contrato de build del repositorio; las mismas pruebas corren en JVM. |
| `./gradlew verifyAgendaQrArchitecture` | **PASS** — compuertas de design system y boundaries, incluyendo los fuentes de `wasmJsMain`. |
| `./gradlew :androidApp:assembleDebug` | **PASS** — sin el lab: la actividad y `debugImplementation(project(":core:ui"))` fueron retirados; ningún manifiesto mergeado referencia el lab. |
| `./gradlew :androidApp:assembleRelease` | **PASS** — release sin el lab, igual que antes de la migración. |
| `python3 -m http.server 8000 -d build/web/component-lab` | **200 OK** — el servidor estático entrega `index.html` y los assets del host. |
| Revisión visual en navegador | **PENDIENTE** — el entorno de esta sesión no tenía navegador conectado; no se declara validación visual sin ejecutar el navegador. Comandos reproducibles en «Cómo abrirlo». |

Pruebas del modelo (`core/ui/src/commonTest/kotlin/com/agendaqr/core/ui/lab/`):

- `LabCatalogIntegrityTest` (12): ids únicos, metadatos obligatorios, estados clasificados y descritos, tokens referenciados existentes, cobertura exacta de los componentes y fundamentos reales de `core:ui`, honestidad del soporte de tema oscuro, categorías válidas y honestidad del render web del QR (PENDING hasta que exista decodificación real en navegador).
- `LabSearchTest` (8): búsqueda por nombre/propósito/uso/tags/estados, insensibilidad a mayúsculas, filtros por categoría, combinación de filtros, estado vacío, conteos por categoría.
- `LabInteractionMatrixTest` (7): Normal real para todo, Disabled real solo con `enabled` en API, la brecha de `XauxaTextAction` sin `enabled`, Loading real solo en `XauxaLoading`, foco/presionado simulados solo en interactivos, los estados `NOT_SUPPORTED` nunca resuelven a estado real, fundamentos sin lentes transitorias.

## Limitaciones conocidas

1. **Revisión visual en navegador pendiente de ejecución manual**: el host Wasm compila y se sirve, pero la revisión visual en navegador no fue ejecutada por el agente (el entorno de la sesión no tenía navegador conectado). No se declara validación visual sin ejecutar el navegador; los comandos reproducibles están en «Cómo abrirlo».
2. **Ejecución de tests iOS**: los tests compilan para los tres targets iOS (klibrary) pero no pueden ejecutarse en este host Linux (requieren macOS + simulador). La compilación de los targets iOS sigue validada por `:core:ui:build`.
3. **iOS sin host del lab**: el framework de iOS solo expone la app de producción; abrir el lab en iOS requeriría un host dedicado (p. ej. pantalla de debug tras una build flag) — pendiente.
4. **Sin pruebas visuales/screenshot**: no existe infraestructura de screenshot tests en el repositorio (sin Roborazzi/Paparazzi). Propuesta en Próximos pasos.
5. **El lab muestra las brechas, no las corrige**: los componentes siguen consumiendo tokens claros fijos; el preview oscuro deja ver que no cambian.
6. **QR placeholder en el host web**: el `actual` wasm de `XauxaQrPreview` no decodifica (como el de iOS); el catálogo lo registra como PENDIENTE y el preview avisa en pantalla.
7. **Wasm sin navegación/hash**: a diferencia de WaraWerse, el host web de AgendaQr no implementa deep links por hash; la selección no es compartible por URL.

## Próximos pasos concretos

1. Revisión visual manual del lab en el navegador (claro y oscuro con el toggle de preview, anchos estrecho/medio/escritorio redimensionando la ventana) usando los comandos de «Cómo abrirlo».
2. Decisión de diseño sobre tokens dark de Xauxa (dimensión `theme.light/dark` de `tokens.json` v13 ya la modela); hasta entonces, el lab seguirá mostrando la brecha.
3. Cerrar brechas de API priorizadas: `enabled` en `XauxaTextAction` y tonos semánticos del banner (con tokens `-bg`).
4. Habilitar screenshot tests para `core:ui` con Roborazzi (multiplataforma, se ejecuta en el host JVM de Android): registrar previews del lab como fixtures doradas y validar tema claro/oscuro sin dispositivo. Requiere añadir la dependencia y una tarea de CI; hoy no existe.
5. Host iOS del lab para inspección en simulador cuando haya acceso a macOS.
6. Evaluar los componentes Xauxa documentados aún no implementados que las pantallas de producto necesitarán (campo de texto C20, chips C14/C22, modal C24) antes de construir esas pantallas, usando el lab como verificador de contrato.

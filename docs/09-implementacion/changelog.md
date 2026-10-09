# Changelog de implementación

> Historial de pasadas sobre V1. El estado vigente vive en `01-estado.md`.

## 2026-10-04 (2.ª mitad) — Pasada T1–T14 (cierre de gaps UX/UI y deuda)

Sobre main 4fd63c7…03e5697. Una rama por tarea, CI verde en cada merge.

- **T1 (PR #79)** — Contextos sin salida: `onBack` en `ContextsScreen`
  (Lista cierra; Detalle → Lista); origen de apertura conservado para
  volver a la Búsqueda con la consulta (S05). +3 tests.
- **T2 (PR #80)** — Modelo de navegación único: `AppRoute` + `AppBackStack`
  (puro) reemplazan los 4 booleanos; Back del sistema vía
  `androidx.compose.ui.backhandler.BackHandler` (flujo interno → pop →
  sistema); `AgendaQrApp.kt` 62 líneas con `AuthenticatedSessionGraph` +
  `AuthenticatedAppRoot`; turnstile respeta ReducedMotion
  (`XauxaTurnstileMotion`, duración 0). +12 tests (9 stack + 3 motion).
  Absorbe el estado de T1.
- **T3 (PR #81)** — ADR-0003 creación/edición de contextos
  (`PROPUESTA — requiere aprobación`); gap registrado. Sin código.
- **T4 (PR #82)** — `ContextPickerDialog` compartido: título "Seleccionar
  contexto", Cancelar (no toca la selección) y Quitar contexto (solo con
  selección); Back del diálogo = Cancelar (`XauxaDialog.onDismissRequest`).
  +6 tests.
- **T5 (PR #83)** — `UserFacingError(what, dataStatus, action)` + mapeo
  central; nunca `error.message` crudo; banners con acción (REINTENTAR/
  ELEGIR OTRA IMAGEN). +12 tests. `grep error.message ?: presentation` = 0.
- **T6 (PR #85)** — Estado por elemento derivado de la cola:
  `elementSyncStatus` + `ElementSyncLookup` + `ElementSyncBadge`
  (Sincronizado/Pendiente/Sincronizando/Error recuperable con REINTENTAR).
  +8 tests.
- **T7 (PR #84)** — S12 por elemento: "Revisar N pendientes", tarjetas con
  Reintentar clasificación / Descartar (payload eliminado) y para
  duplicados Ver existente / Guardar de todos modos (copia con id nuevo);
  intenciones de UI separadas de eventos del reducer. +11 tests.
- **T8 (PR #86)** — S03 con CameraX + análisis ZXing continuo
  (`QrFrameAnalyzer`/`QrScanSession` puros); emisión única → S09 por el
  mismo canal que Galería; permiso denegado → error contextual T5.
  +6 tests (androidUnitTest con QR sintéticos reales). Detección con QR
  real: NO VALIDADO (sin QR en escena virtual).
- **T9 (PR #87)** — Tipografía: Archivo empaquetada (OFL 1.1) como
  `FamilyDisplay`; `FamilyUi` = sans del sistema (Roboto/SF); `XauxaHeading`
  aplica Archivo a todos los `heading()`; doc de origen y licencia. +2 tests.
- **T10 (PR #88)** — Accesibilidad verificable: tests Compose UI sobre
  Robolectric en el gate (touch targets 48dp, heading, estados con texto,
  nodo único de la barra con `clearAndSetSemantics`). +4 tests; guard
  excluye source sets de test.
- **T11 (PR #89 + hotfix #92)** — S06 con filas navegables (rutas de
  búsqueda reutilizadas; fila QR → popToRoot tras bug real encontrado en
  runtime); "Ver más" → Operaciones; criterio de acción primaria
  documentado; S01 "Cerrar sesión" al pie; S02 Galería primaria (píxel:
  Brand). +3 tests.
- **T12 (PR #90)** — Deuda: scope único de sesión (test de cancelación),
  AppStrings (129 literales), `XauxaText` (cero material3.Text en feature),
  división de OperationState/OperationScreens/XauxaExtendedComponents,
  `XauxaStatusBanner` solo `tone`, guard anti-Material con fixtures. +1 test.
- **T13 (PR #91)** — CI: supabase-acceptance bloqueante (ACCEPTANCE PASS
  local en postgres:15); documentado `-PallowDebugSigningForRc=true`.
- **T14 (este PR)** — Consolidación de este documento.
- Fixes de infra: `kotlin.daemon.jvmargs` con Metaspace explícito y
  `org.gradle.jvmargs` ampliado (el build completo agotaba metaspace con
  emulador+Supabase activos); `Enum.entries` → `values()` (ExperimentalNativeApi)
  y forma `assertTrue(mensaje) { … }` para commonTest multiplataforma.

**Validación final (main 03e5697)**: build completo PASS; arch PASS;
domain 43 / data 69 / presentation 80 / core:ui 46 PASS; lint 0 errores;
componentLabWeb PASS; TZ La_Paz/Kiritimati PASS; ACCEPTANCE PASS;
runtime en emulador: flujos A y C + T1/T2/T4/T5/T6/T7/T8/T11 con dump de
UI + SQL (ver `01-estado.md`).

## 2026-10-05 (3) — Dispositivo físico + stride de cámara (main …a64a549)

Con un moto g(9) plus (Android 11) en la misma LAN que el Supabase local
(APK con `SUPABASE_URL=http://192.168.0.58:54321`; 10.0.2.2 no existe
fuera del emulador): login, S01 sincronizada, S02 en orden, S03 con
CameraX. La primera pasada NO detectó: `planeY()` ignoraba el rowStride
del sensor y todos los frames llegaban cizallados (#116:
`stripStride()` puro y testeado + preview en grises sin el NV21 manual).
Tras el fix, un QR real de Banco Unión detecta → S09 → guardar → sync a
Postgres, con preview limpio (finder patterns intactos en captura). V1
pasa a PASS (ejecutado).

## 2026-10-05 (2) — Revisión humana, lote de gaps (main …fcc39c6)

Sobre la revisión de solo-lectura: S06 con scroll (filas QR/
comprobantes sin tope); listas de diálogos con scroll
(`XauxaDialogList` + token `DialogListMaxHeight`) en S08 y
¿a cuál corresponde?; S05 conserva la consulta para QR/Actividad/
Comprobante (Operaciones se apila sobre Buscar; el detalle QR la
restaura atado a su id); S09 muestra el contenido decodificado
(`QrAsset.content`, solo local); REINTENTAR re-ejecuta de verdad
(`RetryFailed` en los 3 VMs, S09 re-guarda) y CERRAR donde no tiene
sentido + flujo `ContextSave`; S10 con ASOCIAR primario +
`XauxaDialog.confirmAsText`; S02 en orden congelado
(`QrGalleryControls`/`QrCameraEntryControl`); S07 con chips
seleccionables (`OperationTypeSelector`), sin campo ID roto y sin rama
muerta del editor; vocabulario de la spec (Actividades, QR, Inicio);
literales restantes a AppStrings. Runtime en emulador: S09 con
contenido, ASOCIAR primario (píxel), S02/S05/chips/selección (semántica
`checked=true`), S06 scrolleable, picker de 16 scrolleable, reintento de
auth que re-ejecuta. Abierto a propósito: fecha dd/mm/aaaa sin picker
Xauxa (requiere decisión de diseño), pesos de encabezados y posición de
Volver (jerarquía intencional por pantalla).

## 2026-10-05 — Pasada 2: R1 + U1–U5 + D1–D6 + V1–V7 (main 4fd63c7…b1e3ef4)

Bugs reales de R1: clears-null remotos (#94) y Back destructivo en
diálogos (#95). Gaps: contadores U1 (#96), iOS real compilando (#97),
creación de contextos ADR-0003 opción C + fix XauxaListRow (#98),
DEAD_LETTER honesto + ADR-0004 propuesta (#99), predictive back (#100).
Deuda: coordinador de lote D1 (#101), strings completos + guard D2
(#102), a11y ampliada D3 (#103), instrumentados D4 (#104), riesgos de
datos D5 (#105/#107), lint+lab D6 (#106). Validación final: 268 tests
(47/70/105/46), lint 0, aceptación SQL, TZ×4, connectedTest 3/3, runtime
A–F + offline/kill/usuarios/back. iOS runtime BLOCKED; QR real y
TalkBack NO VALIDADO.

## 2026-10-04 (1.ª mitad) — Cierre MVP local (main 75a205d → 2349f51)

Pasada con emulador + Supabase local real: flujos A–F ejecutados con
evidencia (uiautomator, SQL, logcat, píxeles); 14 defectos corregidos
(compilación de main rota, commonTest de presentation que nunca compiló,
serialización camelCase vs snake_case que rompía TODO el sync remoto,
S04 inalcanzable, doble superficie de importación, shares fríos perdidos
y crash con URI revocada, diálogo invisible, kotlinx-datetime 0.7.1,
amounts numeric, ANR de Cerrar sesión, instancias paralelas, enums
técnicos, fechas UTC). Tests: domain 43, data 69, presentation 19,
core:ui 41 (global 172). Pendientes heredados: iOS runtime, TalkBack,
QR real por cámara, ACTION_SEND_MULTIPLE por harness, revisión humana del
laboratorio. Contradicciones históricas (tests "3", líneas duplicadas de
NetworkMonitor, "main contiene Contextos") quedaron resueltas por esta
consolidación.

## 2026-10-01/02 — Backlog A1–A7, PRs #42–#78, laboratorio de componentes

Conjunciones históricas: fundación local-first + Supabase; cola durable
con deduplicación (resource, entityId), revisiones, backoff acotado,
DEAD_LETTER; importación por lote con deduplicación por huella; matching
de comprobantes con "mismo día" por días de calendario; laboratorio Wasm
(40 tests del modelo); CI dividido en jobs + ios-compile + supabase
acceptance; migración 006 (FK ON DELETE SET NULL) sin tocar 001–005;
A4 (targetSdk 36, firma release con guard, allowBackup false).

## 2026-10-05 (4) — Pasada docs-only: UX/UI V1.1 Metro (rama docs/ux-metro-v1.1)

Enmienda de la especificación congelada a **V1.1 FROZEN** por aprobación
humana explícita (ADR-0005, D-18): lenguaje visual Metro como expresión de
Xauxa. Reescritos S01 (rejilla de tiles + búsqueda dominante + recientes
sin borde), S06 (pivot QR · Actividades · Comprobantes), §11 (contraste,
tiles accesibles, etiqueta visible en iconos, reduced motion) y §12
(contrato visual Metro: rejilla/tiles, paleta de 12 acentos con tabla de
contraste calculada, tipografía con Archivo Light, app bar inferior,
campos planos, bordes funcionales, botones, movimiento, iconografía Lucide
ISC, modo oscuro). Intención, navegación conceptual, modelo mental, reglas
de negocio y recuperación: intactos. Design system actualizado (snapshot,
reglas de implementación V1.1, tipografía, auditoría con componentes
PENDIENTES) y nuevo registro de licencias (`06-licencias-terceros.md`).
Suite de validación: invariantes I19–I24 añadidos como PENDIENTES.
**Sin ningún cambio fuera de `docs/`**; el código queda declarado
desalineado hasta la pasada de implementación. Defectos de implementación
abiertos registrados en `01-estado.md` (Volver en dos líneas, primario
recortado, badge de ancho completo, destinos indistinguibles, QR pequeño).

## 2026-10-08 — Pasada Metro T1–T8: implementación UX/UI V1.1 (main 77b636f)

Una rama/PR por tarea, CI verde en cada merge (incluidos ios-compile,
instrumented-tests, supabase-acceptance, lint y componentLabWeb).

- **T1 (#119)** Tokens: paleta de 12 acentos + sistema (tabla de contraste
  fijada por test), accentFor determinista (FNV-1a), rejilla de tiles,
  Archivo Light, DisplayPage/SectionHeader.
- **T2 (#120)** Lucide 1.1.0 (ISC) via `com.composables:icons-lucide`
  (android/ios/wasm verificados); XauxaIcon/XauxaIcons; delta medido
  (+586 KB APK, +5.4 MB dist lab).
- **T3 (#121)** Componentes: MetroTile/TileGrid (tilt/escalonado con
  reduced motion), AppBar, Pivot, PageTitle/SectionHeader, campos planos,
  sin bordes de reposo, LiveTile sin bucle; lab actualizado (freeze +
  gates PASS).
- **T4 (#122)** S01: rejilla + tile vivo + Recientes sin borde + app bar.
- **T5 (#123)** S06 pivot con acento por contexto; S08 filas con acento
  (contrato intacto).
- **T6 (#124)** App bar y títulos ligeros en S02/S04/S05/S07/S09/detalle/
  editor/importación/auth; resultados agrupados por tipo; defectos
  "Volver en dos líneas", "primario recortado" y "QR pegado a la
  izquierda" resueltos.
- **T7+T8 (#125)** Test explícito de defectos; fix del fondo del LiveTile
  (hallado en captura); evidencia visual en emulador claro/oscuro/130 %
  (docs/04-ux/lab-captures/metro-v11/); batería local completa PASS
  (arch, gate xauxa, tests, lint, release RC, lab web, iOS sim compile).

Invariantes I19–I24 marcados IMPLEMENTADO/PENDIENTE con su evidencia en la
suite de validación. Abiertos (decisión de producto, ver ADR-0005 Puntos
abiertos): glifos para Editar/Compartir/Mostrar QR; destinos "Sin nombre"
vs "QR importado"; layout del badge de sync en filas estrechas;
alternancia QR/actividad del tile vivo; chips con contorno de selección.
iOS runtime, TalkBack interactivo y captura de S09 con QR real siguen SIN
validar.

## 2026-10-08 (2) — Corrección de auditoría de accesibilidad del DS (fases 1–4, rama fix/xauxa-a11y-contrast)

Sobre main f79f2c9. Un commit por fase; `verifyAgendaQrArchitecture`,
`verifyDesignSystemCompliance` y la suite de tests en verde al cierre de cada fase.

- **Fase 1 — Contraste**: tonos de estado con par propio (success/warning/info
  + container en ambos temas; fin de la herencia de secondary/tertiary);
  `brandText` (brand como texto fallaba 3.63:1 en oscuro) usado en acciones
  de texto, SectionHeader, Pivot y StatBlock; `borderControl` ≥3:1 para
  bordes de control (track del toggle, chip sin seleccionar, favorite
  toggle); pastilla apagada del toggle ≥3:1 (antes 1.12:1); icono por tono
  en banner/toast/inline (glifos aprobados Info y TriangleAlert); Toast con
  liveRegion; `contrastRatio`/`asTextOn` puras en la capa de tema; los
  acentos de contexto nunca como texto ilegible (marcador de 2 dp crudo).
  El banner de confirmación de registro (Brand sobre BrandContainer, 1:1)
  es legible en claro y oscuro.
- **Fase 2 — Estados/formularios**: AuthScreen valida al enviar (botones
  habilitados; errores visibles; un solo spinner con stateDescription
  "Iniciando sesión…"; imeAction Next/Done; sin autocapitalizar correo);
  botones con carga sin salto de ancho (etiqueta alpha 0 + spinner);
  deshabilitado distinguible (primaria de la app bar en Surface3 +
  TextTertiary); `XauxaDialog(destructive)` con foco inicial en Cancelar
  (marcado en eliminar destino/actividad/comprobante); XauxaTextInput con
  etiqueta asociada (contentDescription) y error liveRegion; indicador de
  favorito cuadrado con glifo (fin de la excepción CircleShape) y
  description accesible; ListRow con `selected` semántico.
- **Fase 3 — Movimiento**: `Modifier.xauxaPressFeedback` (overlay con token
  XauxaOpacity.Pressed; inmediato con reduced motion) en ListRow,
  IconButton, AppBarItem y Pivot; entrada escalonada del grid ahora real
  (Animatable; antes animaba hacia el valor inicial = código muerto) con
  `tileStaggerDelayMs` pura (40 ms/tile, tope 300 ms, 0 con reduced
  motion) y filas memorizadas; el cambio de superficie (AppRoute) del
  AuthenticatedAppRoot transiciona con turnstile (reverse por profundidad
  del stack). **ts3 pendiente de prueba manual**: predictive back animado
  en Android 14+ requiere dispositivo.
- **Fase 4 — Robustez**: `XauxaTheme` define shapes planos (red de
  seguridad; XauxaShapeFlat, la única forma con esquinas, vive en tokens);
  DropdownMenu (AppBar/CommandBar) rectangular, sin elevaciones, con borde
  Xauxa; gate endurecido con fixtures: RoundedCornerShape solo en
  tokens/tema, copy de UI en EXPRESIONES en feature/ (D2), `.clickable(` e
  `indication = null` prohibidos en feature/; `XauxaSearchTrigger` (nodo
  único) sustituye al campo falso+overlay de S01; copy de core/ui
  parametrizado (Descartar/Atrás/Más/Cargar más/Cargando…/Buscar/… desde
  AppStrings del llamador); `XauxaTextStyles` con lineHeight canónico;
  limpieza (fuera `Dp.xauxaBorder`; documentados el alias
  brandContainer==brand, el default oscuro de LocalXauxaColorScheme y las
  constantes de easing en string — consumidas por el laboratorio); margen
  de pantalla unificado en ScreenMargin (Auth/ImportBatch/Operaciones/
  patrones; el padding interno del hint de cámara se mantiene Xxl por ser
  encuadre del overlay, no margen de pantalla).

Pendientes de decisión/verificación manual: mostrar/ocultar contraseña
(glifo Eye fuera del mapeo aprobado + slot trailing en XauxaTextInput);
glifos para Editar/Compartir/Mostrar QR; TalkBack/VoiceOver interactivos,
predictive back (Android 14+), fuente al 200 % y contraste con lente en
dispositivo físico; iOS runtime (Xcode).


## 2026-10-09 — Imágenes fuera del repo (chore/no-track-images)

Decisión del propietario: no trackear binarios de imagen. Se retiran las
17 capturas versionadas (lab D6 + evidencia Metro V1.1) y `.gitignore`
pasa a ignorar png/jpg/jpeg/gif/webp/bmp en todo el repo. No hay recursos
de app afectados (ningún icono/asset de build era imagen trackeada; las
fuentes .ttf de Archivo siguen versionadas — son dependencias de build,
no evidencia). Los READMEs de capturas quedan como registro de qué se
verificó y cómo regenerarlo; la evidencia ejecutable sigue en los tests y
el CI. Nada de código tocado.

## 2026-10-09 (2) — Auditoría ronda 2: contraste, cámara, estado, fuente grande, ciclo de vida, feedback, voz y gate (fix/auditoria-ronda2)

Una rama, un commit por área. Triage completo con evidencia en
`docs/05-design-system/07-auditoria-ronda-2.md`. Resumen por área:

- **A (no-regresión)**: suite verde + barrido de pares REALES halló un
  gap nuevo: brandText sobre contenedor Danger (4,47 claro/3,45 oscuro).
  Acciones de banner/toast heredan el color de contenido del tono
  (par garantizado); tests de pares ambientales nuevos.
- **B (cámara)**: política de permiso pura (REINTENTAR vs "Abrir
  ajustes" por denegación permanente), contexto antes de pedir,
  XauxaFeedbackEvent (háptica + liveRegion por evento) aplicado a la
  lectura del QR; iOS fail-closed con banner de ajustes (la API de
  autorización AVFoundation no está en los bindings: B7, checklist).
- **C (estado)**: back stack restaurable (Saver por nombre de ruta,
  tests round-trip + conservador), rememberSaveable en flags de
  formularios y permiso, StateRestorationTester del login; ADR-0006
  (rotación libre con estado restaurable).
- **D (fuente grande)**: tile con altura mínima + etiqueta 2 líneas;
  app bar 2 líneas; tests de layout a 100/150/200 % en 320dp; capturas
  de referencia Roborazzi + manifiesto de hashes SHA-256 (ADR-0007; sin
  binarios en git, coherente con la decisión de no trackear imágenes).
- **E (ciclo de vida)**: collectAsStateWithLifecycle (artifact KMP de
  JetBrains 2.9.4 — el de androidx es Android-only, hallazgo) + derived
  real para syncLookup.
- **F (feedback)**: anuncios por TRANSICIÓN en sync completada,
  eliminación (3 diálogos) y guardado de lote; el éxito del editor queda
  propuesto como ObservableEvent de VM para V2 (no se tocan VMs).
- **H (voz)**: ADR-0008 tuteo + guía de voz; core/ui sin copy hardcodeado
  (Scanner/FileUpload/SearchBar/CommandBar parametrizados) + regla de
  gate con fixtures.
- **I (limpieza)**: margen unificado; slot trailing del campo con test;
  tokens lab-only y componentes sin uso DECIDIDOS en el audit register.
- **J (privacidad)**: ADR-0009 PROPUESTO (FLAG_SECURE/overlay iOS, costes,
  interruptor) — sin implementar por decisión.
- **G/K/L**: predictive back y runtime iOS en checklist manual con pasos
  y criterio; barrido libre sin hallazgos accionables restantes.

Pendiente de dispositivo/decisión: checklist
`docs/08-validacion/07-checklist-manual-ronda2.md`.

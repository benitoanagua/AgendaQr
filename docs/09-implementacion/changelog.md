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

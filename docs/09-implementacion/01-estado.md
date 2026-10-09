# Estado de implementación — V1 / Release Gate

> Regla de cierre: código presente no equivale a capacidad validada.
> Cada afirmación tiene evidencia (comando + resultado) o está marcada NO
> VALIDADA / BLOCKED con su razón exacta. El historial vive en
> `changelog.md`.

> Regla de cierre: código presente no equivale a capacidad validada.
> Cada afirmación de este documento tiene evidencia (comando + resultado) o
> está marcada NO VALIDADA / BLOCKED con su razón exacta.
> El historial completo vive en `changelog.md`.

## Pasada 2026-10-05 — UX/UI V1.1 (docs-only, ADR-0005)

**Pasada exclusivamente documental.** Se enmendó la especificación congelada
a **V1.1 FROZEN** (lenguaje visual Metro como expresión de Xauxa; ADR-0005,
D-18). **Ningún archivo fuera de `docs/` fue modificado.**

**Declaración explícita:** el código actual está **DESALINEADO** con V1.1
en cuanto a jerarquía visual y composición (S01, S06, §11, §12) hasta la
pasada de implementación que aplique la enmienda. La spec manda sobre el
código (D-17); esta desalineación no es un fallo de implementación sino el
estado esperado tras una enmienda de spec aprobada.

### Defectos de implementación V1.1 — estado tras la pasada Metro (T1–T8, 2026-10-08)

RESUELTOS (con verificación explícita):

1. ~~Botón "Volver" partido en dos líneas en Registrar actividad~~ — vive en
   la flecha de la `XauxaAppBar` (una línea; test
   `t07_actions_live_in_the_bottom_app_bar` + captura
   `docs/04-ux/lab-captures/metro-v11/s07-registrar.png` — capturas no versionadas desde el 2026-10-09, ver el README de esa carpeta).
2. ~~Botón primario recortado al final de Registrar actividad~~ — Guardar es
   la acción principal de la app bar anclada a insets (mismo test; captura
   con fuente al 130 % `s07-font130.png`, no versionada).
3. ~~Revisar QR (S09) con el código pequeño y pegado a la izquierda~~ —
   preview centrada (T6).

ABIERTOS (registrados también en Puntos abiertos del ADR-0005; requieren
decisión de producto, no son fallos de la spec):

4. Badge "Sincronizado" compite por el ancho de fila en pantallas
   estrechas (el estado sigue expresándose con texto; el layout de fila no
   está congelado por la spec).
5. Destinos "Sin nombre"/"QR importado" indistinguibles entre sí (copy o
   marca de origen = decisión de dominio).

### Pasada Metro T1–T8 (2026-10-08): implementación UX/UI V1.1 COMPLETA en Android/wasm

- **T1 Tokens** (PR #119): rejilla de tiles (`TileUnit`/`TileWideHeight`),
  `ScreenMargin`/`TileGap`, `IconSize`/`IconSizeTile`/`AppBarHeight`,
  paleta de 12 acentos Metro + sistema `0067B8` con pares texto/fondo fijos
  por la tabla de contraste, `accentFor(contextId)` determinista (FNV-1a
  propio), Archivo Light (300) empaquetada, `DisplayPage` 40 sp /
  `SectionHeader` 14 sp. Tests: `XauxaAccentTest` (paleta exacta, WCAG de
  cada par, determinismo, reparto), `XauxaTypographyTest`.
- **T2 Iconos** (PR #120): `com.composables:icons-lucide:1.1.0` (ISC)
  verificada contra android/ios-sim/wasm con Kotlin 2.2.20 + CMP 1.8.2;
  wrapper `XauxaIcon` + mapeo `XauxaIcons` (los 11 del mínimo existen);
  delta medido: APK release +586 KB, dist wasm del lab +5.4 MB.
- **T3 Componentes** (PR #121): `XauxaMetroTile`/`XauxaTileGrid` (tilt
  ≤150 ms, escalonado ≤300 ms, reduced motion inmediato), `XauxaAppBar`,
  `XauxaPivot`, `XauxaPageTitle`/`XauxaSectionHeader`, campos planos
  (`XauxaTextInput`/`XauxaSearchBar`), botones secundarios texto+icono sin
  caja, filas/badges/tiles sin borde de reposo, `XauxaLiveTile` sin bucle.
  Contratos nuevos en el lab (freeze + gate a11y/auditoría PASS).
- **T4 S01** (PR #122): título ligero, búsqueda dominante, rejilla
  (Añadir/Registrar/Favoritos/Contextos + tile vivo), Recientes sin borde,
  acciones de sesión en la app bar. Capacidades intactas.
- **T5 S06/S08** (PR #123): pivot QR · Actividades · Comprobantes con acento
  derivado por contexto; selector de contexto con filas de acento (contrato
  Cancelar/Quitar/Back=Cancelar intacto).
- **T6 Flujos** (PR #124): app bar + títulos ligeros en S02/S04/S05/S07/
  S09/detalle/editor/importación/auth; S04/S05 agrupados por tipo con
  encabezados de sección; defectos 1–3 resueltos.
- **T7+T8** (PR #125): test explícito de defectos, fix del fondo del live
  tile, evidencia visual en emulador (claro/oscuro/fuente 130 %,
  `docs/04-ux/lab-captures/metro-v11/` — carpeta no versionada, solo su README —), batería local completa PASS.

Validación local de la pasada (todo ejecutado en este host):
`verifyAgendaQrArchitecture` PASS · `verifyDesignSystemCompliance` verifyDesignSystemCompliance PASS ·
tests domain/data/presentation/androidApp/core:ui PASS · `:androidApp:lint`
0 errores · `assembleDebug`/`assembleRelease` (RC) PASS · `componentLabWeb`
PASS · compilación iOS simulator (shared/data/presentation/core:ui) PASS ·
runtime emulador con Supabase local y capturas de S01/S02/S04/S06/S07.

**Sigue SIN validar** (no afirmado): iOS runtime (Xcode/simulador),
TalkBack/VoiceOver interactivo y foco por teclado físico, captura de S09
con QR real (el flujo exige imagen), revisión de contraste con lente en
dispositivo físico. Invariantes I19–I24 de la suite:
IMPLEMENTADO/PENDIENTE según se detalla en
`docs/08-validacion/02-suite-estados-eventos-v1.md`.

## Estado actual (2026-10-04, cierre de la pasada T1–T14)

**Fuente de verdad**: `main` @ 03e5697. CI del último push de main: verde
(domain, data, presentation, core:ui, androidApp lint+assembleDebug,
assembleRelease RC, componentLabWeb, ios-compile, supabase-acceptance
— ahora bloqueante).

### Funciona (PASS ejecutado)

- **Build completo** (Linux x86_64, JDK 17): `./gradlew build
  -PallowDebugSigningForRc=true` → **BUILD SUCCESSFUL** (Android debug+release,
  iOS arm64+simulator, wasm del laboratorio, todos los tests, lint:
  0 errores / 21 advertencias).
- **Tests** (`testDebugUnitTest`, 0 fallos): **domain 43, data 69,
  presentation 80, core:ui 46 — global 238** (línea base de esta pasada:
  43/69/19/41 = 172; ningún conteo bajó).
- **Zonas horarias** (domain, daemon renovado por corrida):
  `TZ=America/La_Paz` y `TZ=Pacific/Kiritimati` → PASS.
- **Aceptación SQL**: `./gradlew :supabase:acceptance:test` contra
  postgres:15 limpio (podman) → **ACCEPTANCE PASS**. El job de CI es
  bloqueante desde T13.
- **Runtime Android** (AVD `agenda_qr` API 34 + Supabase local completo,
  migraciones 001–006, APK con `SUPABASE_URL=http://10.0.2.2:54321`):
  - Flujos A (QR por galería → S09 → guardar → fila en Postgres) y C
    (operación, con occurredAt local correcto) ejecutados de punta a punta
    con dump de UI + consulta SQL.
  - **T1**: búsqueda "mercado" → resultado Contexto → Back×1 → Lista →
    Back×2 → Búsqueda con la consulta conservada (S05). Sin dead-end.
  - **T2**: Back del sistema desde Revisar QR → Añadir; desde Añadir →
    Inicio; desde el editor de operación → detalle → lista; en la raíz sale
    de la app. Draft del editor intacto tras Back desde el selector S08
    (monto 777 conservado).
  - **T4**: selector "Seleccionar contexto": Cancelar conserva la selección
    ("Para: Mercado Central" sigue); Quitar contexto la limpia; el draft no
    se toca.
  - **T5**: permiso de cámara denegado → "No pudimos abrir la cámara. /
    Concede el permiso o usa Galería." + [REINTENTAR] + [Descartar].
  - **T6**: modo avión → operación guardada → badge **"Pendiente"** por
    elemento + banner "Sincronización pendiente: 1" → reconectar →
    **"Sincronizado"** + fila en Postgres (amount 321.000000, occurred_at
    04/10 correcto en UTC-4) y banners fuera.
  - **T7**: Galería (varios) con imagen no-QR → S12 "? Pendientes: 1" +
    botón **"Revisar 1 pendientes"** → tarjeta "Archivo: png" con
    Reintentar clasificación (permanece pendiente: clasificación honesta) y
    **Descartar** (el elemento sale del lote; payload eliminado por el host).
  - **T8**: S03 abre con CameraX (sesión OPENED, "First frame done" en
    logcat) + hint Xauxa "Encuadre el código QR"; Volver/Back cierran sin
    efectos; Galería sigue siendo la entrada principal (S02).
  - **T11**: S06 con filas navegables (fila QR → detalle del destino;
    bug de ruta encontrado y corregido en PR #92 con re-validación);
    S01 con "Cerrar sesión" al pie (fuera de la fila de acciones); S02 con
    **Galería como botón primario** (verificado por píxel: 0,103,184 =
    Brand) sin alterar el orden congelado.
  - **Tema claro/oscuro**: fondo blanco en light / negro puro en dark
    (píxel), sin crash en la recreación.
  - **Accesibilidad estática**: tests Compose UI (Robolectric) en CI
    verifican touch targets ≥48dp, `heading()` en títulos, estados con
    texto y nodo único en la barra de búsqueda.
  - **Tipografía (T9)**: Archivo empaquetada (OFL 1.1) y aplicada a todos
    los `heading()` vía `XauxaHeading`; el laboratorio Wasm compila con los
    mismos recursos.

### NO VALIDADO (con razón exacta)

- **Decodificación de un QR real por cámara (T8)**: la escena virtual del
  emulador no contiene QR y no hay cámara física controlable (sin
  v4l2loopback en el host). La lógica de decodificación está cubierta por
  tests unitarios con QR sintéticos reales (rotaciones 90/180/270).
  Pendiente: dispositivo físico con QR impreso.
- **TalkBack interactivo / predictive back animado (ts3)**: sin lector de
  pantalla ni gesto físico en headless; el árbol semántico queda fijado
  por los tests de T10.
- **`ACTION_SEND_MULTIPLE` con URIs parcelables reales**: limitación del
  harness documentada (la ruta múltiple se valida por Galería (varios)).

### BLOCKED

- **iOS runtime** (sin Xcode en este entorno): launch, login, persistencia,
  import, cámara/galería/share (stubs de adquisición), NetworkMonitor real,
  restart. Compilación (`compileKotlinIosSimulatorArm64` de shared, domain,
  data, presentation y core:ui) PASS local + job `ios-compile` PASS en CI.
- **Firma de producción**: el guard exige `ANDROID_KEYSTORE_*`; CI usa
  `-PallowDebugSigningForRc=true` solo para RCs (documentado en
  `ANDROID_DISTRIBUTION.md`). PRODUCTION_RELEASE sigue BLOCKED.
- **Creación de contextos (T3)**: ADR-0003 `PROPUESTA — requiere
  aprobación`; `SaveContextUseCase` sigue sin cablear hasta decisión
  humana.

## Pendientes reales

1. **Aprobación del ADR-0003** (creación/edición de contextos): S06/S08/
   resultados Contexto siguen siendo solo lectura (con seed SQL pueden
   navegarse: verificado en runtime).
2. **iOS**: todo el runtime (ver BLOCKED).
3. **QR real por cámara** (dispositivo físico).
4. **TalkBack interactivo y gesto de predictive back** en dispositivo.
5. **Revisión visual humana completa del laboratorio** (claro/oscuro,
   anchos, matriz de estados).
6. **RLS behavior contra el proyecto remoto del usuario** (la validación
   fue contra el stack local con las mismas migraciones).
7. E2E offline→online con kill/restart y cambio de usuario en dispositivo
   físico (validado en emulador en la pasada anterior con la misma cola).

## Reglas que sigue este repositorio

- UX/UI V1 **congelada** (`docs/04-ux/02-especificacion-ux-ui-v1.md`);
  todo cambio cita su sección. Cambios de UX nuevos → ADR en
  `docs/07-decisiones/` y aprobación humana.
- Xauxa es la autoridad visual (`XauxaColor/XauxaSpacing/XauxaMetrics/
  XauxaType/XauxaMotion`); `verifyAgendaQrArchitecture` prohíbe en
  `feature/` los componentes Material crudos (con fixtures que fallan si
  el guard se debilita).
- Modelo local-first intacto: GUARDADO → PENDIENTE → SINCRONIZANDO →
  SINCRONIZADO, cola durable user-scoped, aislamiento por usuario,
  migraciones 001–006 sin tocar.
- Un cambio = un commit convencional; una rama por tarea; CI verde antes
  del merge.


---

## Pasada 2 (2026-10-05) — Regresión R1, gaps U1–U5, deuda D1–D6, V1–V7

Fuente de verdad: `main` @ b1e3ef4. Todos los merges con CI verde
(incluye jobs `instrumented-tests` y `supabase-acceptance` bloqueante).

### R1 — Regresión runtime completa (PASS ejecutado)

Flujos A–F + offline + cambio de usuario + Back del sistema en todas las
superficies, sobre APK de main con Supabase local (evidencia: dumps
uiautomator + SQL en esta pasada):

- **A** QR galería → S09 → guardar → Postgres (`destination-1791…`) →
  editar (categoría "alimentos" → Postgres) → búsqueda "bife" → abrir.
- **B** ACTION_SEND imagen (app cerrada, arranque frío vía resolver) →
  S09; ACTION_SEND PDF (app abierta, `onNewIntent`) → diálogo recibido.
- **C** PAGO 321→321500 con advertencia exacta de sensible + comprobante
  intacto (Postgres: amount 321500.000000, attached=1).
- **D** adjuntar → bandeja → "¿A cuál corresponde?" → retorno al detalle
  → visor → compartir (FileProvider) → Desasociar (Postgres
  operation_id=NULL) → re-asociar.
- **E** PDF compartido → guardar sin operación → bandeja (fila en
  Postgres `comprobante-f74f…`).
- **F** duplicados: "Ver existente" (abre el visor del original) y
  "Guardar de todos modos" (copia deliberada, bandeja +1).
- **Offline**: avión → 999 "Pendiente" → kill → reabrir → banner persiste
  → reconectar → backoff reintenta solo → "Sincronizado" + fila única.
- **Kill durante sync**: force-stop al reconectar → relanzar → drain →
  fila 444 única en Postgres (sin duplicados ni bloqueos).
- **Cambio de usuario**: e2e.a → signup e2e.c (cero datos de a) →
  sign-out → e2e.a (datos intactos); claves locales user-scoped.
- **Back del sistema**: S09→Añadir→Inicio→salir; editor→detalle→lista;
  bandeja→lista; visor (diálogo)→detalle; S12 revisión→resultado→fuera;
  Búsqueda→Inicio (con IME: primero cierra teclado).

**Bugs reales encontrados y corregidos en R1:**
1. **PR #94**: desasociar marcaba "Sincronizado" pero Postgres conservaba
   `operation_id` — el Json por defecto del cliente Supabase omite campos
   null ⇒ los "clear" de TODAS las filas remotas nunca viajaban (context_id,
   note, category…). Fix: `@EncodeDefault(ALWAYS)` + regresión con Json
   por defecto. Verificado runtime: Postgres `operation_id: None`.
2. **PR #95**: Back del sistema en el visor abría "Eliminar" y en el
   diálogo de recibido guardaba implícitamente. Fix: `onDismissRequest`
   explícito (Back cierra visor / abandona el entrante).

### U — gaps UX/plataforma

- **U1 (PR #96)**: contadores en español ("Revisar 1 pendiente",
  "Comprobante sin asociar: 1", "Actividad reciente · 1"); cero
  concatenación manual (helper `Counters`). +5 tests. PASS (unit+runtime).
- **U2 (PR #97)**: iOS con adquisición REAL: PHPicker único/múltiple por
  el mismo canal que Android, cámara AVFoundation con detección nativa de
  QR, escena activa (adiós `keyWindow`), `ImportBatchControls.ios` y
  `observeIncomingComprobantes()` implementados, literales en AppStrings.
  **PASS (solo compila/unit + CI ios-compile)**; runtime BLOCKED (sin
  Xcode) — checklist exacto en `docs/09-implementacion/IOS_RUNTIME_CHECKLIST.md`.
- **U3 (PR #98)**: ADR-0003 **APROBADA — opción C, sin edición**. Creación
  desde S08 (paso "Crear contexto", auto-selección, draft intacto) y
  acción primaria en S06 vacío; `SaveContextUseCase` cableado; parámetro
  muerto `onOpenContexts` eliminado. **Bug raíz de XauxaListRow corregido**
  (fillMaxHeight en slot scrolleable estiraba la fila a todo el alto —
  IntrinsicSize.Min). Runtime: contexto creado visible en selector/S06/
  búsqueda y en Postgres vía cola. +7 tests (5 comunes + 2 Robolectric).
- **U4 (PR #99)**: DEAD_LETTER con estado propio honesto ("No se pudo
  sincronizar" + "Tu información está guardada…"), REINTENTAR solo para
  FAILED (el drain omite DEAD_LETTER). ADR-0004 (recuperación de
  cuarentena, opción B recomendada) **PROPUESTA**. +tests separados.
  Runtime del estado: NO VALIDADO (requiere forzar error permanente).
- **U5 (PR #100)**: `enableOnBackInvokedCallback=true` verificado en el
  manifest fusionado; gesto del borde PASS (ejecutado) en AVD API 34
  (Añadir→Inicio, Buscar→Inicio, raíz→salir). Android 16/API 36: NO
  VALIDADO (sin imagen de sistema en el SDK); preview animado: NO
  VALIDADO (visual transitorio).

### D — deuda técnica

- **D1 (PR #101)**: `ImportBatchCoordinator` (sin Compose) +
  `ImportBatchStateHolder`; `Failed` transporta `UserFacingError`;
  `AuthenticatedAppRoot` 542→262 líneas (fn ~150), `HomeSurface` a su
  archivo. +4 tests del coordinador. R1-S12 re-validado runtime.
- **D2 (PR #102)**: cero literales de UI en `feature/` fuera de
  `AppStrings` (guard con fixtures que fallan/pasan); renombres
  semánticos; decisión strings.xml documentada. +0 tests (guard).
- **D3 (PR #103)**: a11y Compose UI ampliada a S02/S04/S06/S07-S08/S09/
  S12/S01 (+8); **bug S06 vacío corregido** (la acción primaria seguía
  "Volver" tras U3). presentation 105.
- **D4 (PR #104)**: suite instrumentada propia (3 PASS en AVD: SEND con
  grant, URI revocada sin crash, singleTask+filtros); job CI con emulador
  NO bloqueante (promoción tras 5 corridas estables — 1ª verde).
- **D5 (PR #107)**: colisión nowMillis cubierta por test (desempate de
  newEntityId, 500 ids únicos); presupuesto de observe() (100 renders de
  200 elementos) medido y asertado; paginación queda como ADR si V2 crece.
- **D6 (PR #106)**: lint a cero salida (4 coords al toml; 26 advertencias
  baselined con justificación); lab: paleta oscura VERIFIED (píxel),
  escaneo real VERIFIED (CameraX T8), capturas headless 0 errores de
  consola (docs/04-ux/lab-captures/); toggle de tema del lab NO
  AUTOMATIZABLE (canvas) — revisión humana pendiente.

### V — validaciones con entorno especial

- **V1 QR real por cámara**: PASS (ejecutado en físico 2026-10-05; antes
  NO VALIDADO por escena virtual sin QR y sin
  cámara física controlable — documentado desde la primera pasada).
- **V2**: fuente 200% PASS (ejecutado: UI legible, sin crash; escala
  restaurada), tema claro/oscuro PASS (píxel), reduced motion PASS
  (transición instantánea), TalkBack NO VALIDADO (headless).
- **V3 ACTION_SEND_MULTIPLE real**: NO VALIDADO (harness; la ruta
  múltiple se valida por Galería (varios) — PASS).
- **V4**: sin red PASS (banner + cola + recuperación), URI revocada PASS
  (instrumentado, sin crash), **login con sesión revocada PASS** (pantalla
  de login honesta; error con acción "No pudimos iniciar sesión. / No se
  perdió nada. / REINTENTAR" verificado en runtime), almacenamiento lleno
  NO VALIDADO (no se puede llenar el disco del emulador sin riesgo de
  perder el stack), token expirado a mitad de drain NO VALIDADO sin
  romper el stack local (revocar todas las sesiones fuerza re-login, que
  es el camino honesto probado).
- **V5**: no-QR y PDF PASS (E/F), lote PASS (S12), duplicados PASS (F),
  kill durante sync PASS (444 única en Postgres tras relanzar).
- **V6 RLS remoto**: BLOCKED (sin credenciales; nunca inventadas).
- **V7 aceptación SQL**: PASS (postgres:15 limpio en podman).

### Validación final sobre main b1e3ef4 (PASS ejecutado)

`./gradlew build -PallowDebugSigningForRc=true` → BUILD SUCCESSFUL;
`verifyAgendaQrArchitecture` → PASS; tests: **domain 47, data 70,
presentation 105, core:ui 46** (268 global; nada bajó); `:androidApp:lint`
→ "No errors or warnings" (26 filtradas por baseline justificado);
`componentLabWeb` → PASS; TZ La_Paz/Kiritimati/Midway/UTC → PASS;
aceptación SQL → ACCEPTANCE PASS; iOS sim compile (shared, data,
presentation, core:ui) → PASS; `connectedDebugAndroidTest` → 3/3 PASS;
CI de main → success (últimos 3 runs verdes).

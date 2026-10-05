# Estado de implementación — V1 / Release Gate

> Regla de cierre: código presente no equivale a capacidad validada.
> Cada afirmación de este documento tiene evidencia (comando + resultado) o
> está marcada NO VALIDADA / BLOCKED con su razón exacta.
> El historial completo vive en `changelog.md`.

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
- **Aceptación SQL**: `bash supabase/tests/run-acceptance.sh` contra
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

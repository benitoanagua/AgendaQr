# Estado de implementación

> La historia de cambios vive en `git log`, no aquí. Este documento describe
> el estado ACTUAL: capacidades, límites conocidos y puertas de calidad.
> Solo se declara "verificado" lo que una puerta ejecutada en este entorno
> respalda; lo que no puede ejecutarse se registra como **NO VERIFICADO**,
> nunca como PASS (contrato §10).

## Capacidades implementadas y verificadas

| Área | Estado | Verificación |
|---|---|---|
| Dominio (operaciones, comprobantes, contextos) | Implementado | domain tests |
| Datos (Supabase local-first, sync queue) | Implementado | data tests |
| Identidad local sin cuenta (perfil local + namespace por perfil) | Implementado | domain/data tests + `AgendaQrLocalStartupTest` (arranque real) |
| Acceso local con PIN (S00: configurar/confirmar, desbloquear, bloqueo) | Implementado | `LocalAccessGateTest` + Robolectric del arranque real (iOS: compila, runtime NO VERIFICADO) |
| Migración de instalaciones previas al perfil local | Implementado | data tests (idempotencia, no borra origen, no mezcla cuentas, detección por claves sin cliente remoto) |
| Nube opcional ("Cuenta y respaldo" detrás de Inicio) | Implementado | compilación Android/iOS + gate; sincronización real con backend NO VERIFICADA |
| UX/UI V1.6 (referencia Ant Design Mobile, S00 + S01–S12) | Implementado | Robolectric suite + hashes visuales + gate del design system |
| Chrome e insets (TopBar, NavigationBar/NavigationRail, Scaffold con contentWindowInsets) | Implementado | Robolectric suite + gate |
| Edge-to-edge Android (`enableEdgeToEdge`) | Implementado | ensamblado; runtime NO VERIFICADO (dispositivo) |
| Predictive back (PredictiveBackHandler con progreso) | Implementado | compilación 3 targets; gesto NO VERIFICADO (dispositivo) |
| Búsqueda global (S04/S05) | Implementado | Robolectric suite |
| Importación QR (Photo Picker Android/PHPicker iOS, cámara, share Android) | Implementado | unit + compilación de instrumentados (runtime instrumentado con fallos preexistentes de harness, no bloqueante) |
| Registro de actividad (S07) | Implementado | Robolectric suite |
| Permisos de cámara (política de ajustes) | Implementado | unit + compilación de instrumentados (runtime instrumentado con fallos preexistentes de harness, no bloqueante) |
| Navegación (back stack restaurable + NavigationBar 4 destinos) | Implementado | commonTest + RootNavigationChromeTest |
| Accesibilidad (contraste, foco visible, háptica, anuncios, targets 48dp) | Implementado | `core:ui` tests + Robolectric |
| Iconografía (Lucide ISC vía AmIcon) | Implementado | compilación 2 targets (Android/iOS) + gate |
| Feedback transitorio (Snackbar) y persistente (AppStatusHost) | Implementado | RootNavigationChromeTest + AppStatusHostTest |

## NO VERIFICADO en esta pasada

Estas puertas no pueden ejecutarse en este entorno (falta de Xcode,
dispositivo físico o red). No se declaran PASS.

| Capacidad | Motivo | Cómo se verifica |
|---|---|---|
| Runtime iOS (arranque y render) | Requiere Xcode/simulador | `IOS_RUNTIME_CHECKLIST.md` |
| TalkBack/VoiceOver interactivo | Requiere dispositivo físico | checklist manual (ronda 2) |
| Insets/notch, cámara, foco nativo y motion en dispositivo | Requiere dispositivo físico | `ANDROID_PHYSICAL_DEVICE_TEST.md` |
| Predictive back en Android 14+ (gesto real con progreso) | Requiere dispositivo físico | `ANDROID_PHYSICAL_DEVICE_TEST.md` |
| Photo Picker en dispositivo (contractos del sistema) | Robolectric ejecuta el flujo unitario; el picker visual requiere dispositivo | `ANDROID_PHYSICAL_DEVICE_TEST.md` |
| Revisión visual de las capturas Roborazzi regeneradas | El hash PASS es determinista y el análisis de bandas/regiones es programático; el ojo humano valida la composición | abrir `feature/destinations/presentation/build/roborazzi/*.png` (22 capturas × claro/oscuro) |
| Supabase acceptance (JDBC) | Requiere base de datos | `:supabase:acceptance:test` con `DATABASE_URL` |
| Sincronización real contra Supabase (subida/bajada) | Requiere backend y cuenta reales; no se ejercita en tests unitarios | prueba de aceptación contra un proyecto Supabase |
| Desbloqueo biométrico en dispositivo | No implementado todavía; el PIN es el mecanismo actual | implementación nativa + dispositivo |

## Límites conocidos (decisiones, no bugs)

- Glifos "Editar", "Compartir" y "Mostrar QR" sin icono asignado
  (contrato §7): esas acciones de la barra superior van como acción
  de texto (con su descripción accesible como etiqueta) hasta una
  decisión de iconografía.
- El contraste NO textual de algunos acentos de contexto contra el fondo
  del tema es < 3:1 (contrato §4.2): se aceptan como
  identidad de dato (encabezados de S06, punto de fila) siempre con
  texto legible.
- Autofill de credenciales: `KeyboardOptions.contentType` no existe en
  Compose 1.8.2 (llega con 1.9); el autofill funciona por `keyboardType`
  Email/Password (iOS lo mapea a `textContentType`).

## Pendientes conocidos

- **Share Extension de iOS**: `sharedImportsSupported()` devuelve false en
  iOS; el bloque "Desde otra app" de S02 no se muestra hasta que la
  extensión exista.
- **Selector de PDF en iOS** (`UIDocumentPickerViewController`): la acción
  "Elegir archivo (PDF)" solo existe en Android (OpenDocument por el canal
  de comprobantes entrantes); en iOS queda pendiente de la pasada nativa
  con Xcode.
- **NavigationRail**: implementado para ancho expandido vía
  `WindowSizeClass`; su render real en tablet/plegable es NO VERIFICADO
  (no hay dispositivo).

## No implementado en esta pasada (alcance declarado, no defecto)

Se listan explícitamente para no declararlos cubiertos (ADR-0016):

- **Cifrado de datos en reposo con el PIN — NO IMPLEMENTADO.** El PIN es una
  puerta de acceso local (verificador PBKDF2 con sal), no una clave derivada de
  los datos. Los datos locales NO están cifrados con el PIN.
- **Desbloqueo biométrico (`BiometricPrompt`/`LocalAuthentication`) — NO
  IMPLEMENTADO.** El PIN es el único mecanismo de desbloqueo. La taxonomía de
  errores biométricos (cancelado, no inscrito, bloqueo del sensor, recuperable)
  tampoco existe todavía.
- **Migración de los bytes de comprobantes del directorio heredado — NO
  IMPLEMENTADO.** La migración copia las colecciones serializadas; los archivos
  antiguos (`.../comprobantes/<user_id>/`) se conservan en su ruta y no se
  copian a la del perfil local.
- **Vínculo explícito perfil local ↔ cuenta remota — NO IMPLEMENTADO.** "Cuenta
  y respaldo" permite entrar/salir sin borrar datos locales, pero no existe el
  enlace perfil↔cuenta con semántica de respaldo por dispositivo.
- **Canal de recuperación del PIN — NO IMPLEMENTADO.** Olvidar el PIN no tiene
  recuperación en la app; restablecer la protección solo es posible desde
  dentro de la app ya desbloqueada.

## Puertas de calidad

| Puerta | Comando | Resultado (esta pasada) |
|---|---|---|
| Arquitectura + design system + no-scripts | `./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true` | PASS |
| Tests de módulos | `./gradlew :core:ui:testDebugUnitTest :feature:destinations:presentation:testDebugUnitTest :feature:destinations:domain:test :feature:destinations:data:testDebugUnitTest -PallowDebugSigningForRc=true` | PASS |
| Arranque local-first y acceso con PIN (arranque REAL) | `./gradlew :feature:destinations:presentation:testDebugUnitTest --tests "*AgendaQrLocalStartupTest" -PallowDebugSigningForRc=true` | PASS (3/3: instalación limpia → configurar PIN sin login; el perfil local se crea sin cuenta; reinicio → desbloqueo local; sin referencias a correo/contraseña) |
| Máquina de estados del acceso local | `./gradlew :feature:destinations:presentation:testDebugUnitTest --tests "*LocalAccessGateTest" -PallowDebugSigningForRc=true` | PASS (10/10: configurar→desbloquear→bloquear, saneado de dígitos, no coincidencia no guarda, intentos, bloqueo temporal) |
| Namespace por perfil + migración de instalaciones previas | `./gradlew :feature:destinations:data:testDebugUnitTest --tests "*LocalIdentityTest" -PallowDebugSigningForRc=true` | PASS (claves por perfil, detección de datos heredados por claves sin cliente remoto, migración idempotente que no borra el origen y no mezcla cuentas, construcción perezosa de los repositorios remotos) |
| Regresión visual (Roborazzi + SHA-256) | `./gradlew :feature:destinations:presentation:recordRoborazziDebug recordVisualHashes verifyVisualHashes -PallowDebugSigningForRc=true` | PASS (22 capturas × claro/oscuro: s01/s02-add/s04-search/s06-contexts/s07-operation-row/s09-review/login/import-batch/destination-detail/edit-destination/qr-preview-fallback) |
| Contrato de QR (generación + lectura con ZXing) | `./gradlew :feature:destinations:presentation:testDebugUnitTest --tests "*QrPreviewContractTest"` | PASS (5/5: el fixture es un QR real legible; bytes del dominio → imagen; payload no-imagen → estado de error, no un QR falso) |
| Contrato de no solapamiento (dock y FAB) | `./gradlew :core:ui:testDebugUnitTest --tests "*AmScaffoldOverlapContractTest"` | PASS (3/3: el contenido termina por encima del dock de acciones y del FAB; el espacio se deriva del Scaffold y de tokens, sin alturas estimadas) |
| Ensamblado Android | `./gradlew :androidApp:assembleDebug -PallowDebugSigningForRc=true` | PASS |
| Compilación iOS (simulador ARM64, sin runtime) | `./gradlew :core:ui:compileKotlinIosSimulatorArm64 :feature:destinations:presentation:compileKotlinIosSimulatorArm64` | PASS (compila; runtime NO VERIFICADO, sin Xcode) |
| Supabase acceptance (JDBC) | `DATABASE_URL=jdbc:postgresql://... ./gradlew :supabase:acceptance:test` | NO VERIFICADO (sin base de datos) |
| CI (4 jobs) | Push a `main` → Android validation workflow | NO VERIFICADO (fuera del entorno local) |

### Lectura del QR renderizado (verificación puntual, no una puerta)

Las capturas de Roborazzi se volvieron a LEER con el decodificador del
proyecto (ZXing `MultiFormatReader` + `RGBLuminanceSource`, vía `javac`
contra `com.google.zxing:core:3.5.3`) para comprobar que el QR que se ve en
pantalla es real (no una imagen decorativa):

- `destination-detail-light.png` / `-dark.png` → `QR_CODE` = `https://agendaqr.app/d-1`
- `s09-review-light.png` / `-dark.png` → `QR_CODE` = `https://agendaqr.app/d-1`
- `qr-preview-fallback-light.png` / `-dark.png` → **no legible** (el fallback NO es un QR válido, por diseño)

Esta comprobación depende de las capturas ya generadas; la prueba
automatizada permanente (fixture + decodificación) vive en
`QrPreviewContractTest`.

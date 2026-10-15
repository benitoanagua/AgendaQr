# Agenda QR — Estado de implementación

> La historia de cambios vive en `git log`, no aquí. Este documento describe el
> estado ACTUAL: capacidades, límites conocidos y bloqueos. Solo se declara
> «verificado» lo que una puerta ejecutada en este entorno respalda; lo que no
> puede ejecutarse se registra como **NO VERIFICADO**, nunca como PASS.

Las puertas, sus comandos y la validación por plataforma están en
[QUALITY.md](QUALITY.md).

## 1. Implementado y verificado

| Área | Verificación |
|---|---|
| Dominio (operaciones, comprobantes, contextos) | domain tests |
| Datos (persistencia local-first, sync queue) | data tests |
| Identidad local sin cuenta (perfil local + namespace por perfil) | domain/data tests + `AgendaQrLocalStartupTest` (arranque real) |
| Acceso local con PIN (configurar/confirmar, desbloquear, bloqueo) | `LocalAccessGateTest` + Robolectric del arranque real |
| Biometría como atajo: prompt real Android (`BiometricPrompt`) + iOS (`LAContext`), mapeo de errores e invariante fail-closed | `LocalAccessBiometricTest` (contrato) + `BiometricPromptErrorMappingTest` (mapeo y fail-closed); runtime de hardware en §2 |
| Migración de bytes de comprobantes heredados (`<base>/comprobantes/<user_id>/` → perfil local) | `LegacyComprobanteBytesMigrationTest` (idempotencia, no-borrado, fallo parcial, aislamiento) |
| Modo de demostración aislado (perfil `demo-profile-v1`, sin sync) | `DemoDataTest` + `DemoSeederTest` (determinista, idempotente, no toca perfil real) |
| Migración de instalaciones previas al perfil local | data tests (idempotencia, no borra origen, no mezcla cuentas, detección por claves sin cliente remoto) |
| Nube opcional («Cuenta y respaldo» detrás de Inicio) | compilación Android/iOS + gate + captura `account-{light,dark}` (SignedOut con doble determinista) |
| UX/UI V1.6 (referencia Ant Design Mobile, S00 + S01–S12) | Robolectric suite + hashes visuales + gate del design system |
| Chrome e insets (TopBar, NavigationBar/Rail, Scaffold con contentWindowInsets) | Robolectric suite + gate (incl. captura tablet w720dp con NavigationRail) |
| Edge-to-edge Android (`enableEdgeToEdge`) | ensamblado |
| Predictive back (PredictiveBackHandler con progreso) | compilación 3 targets |
| Búsqueda global (S04/S05) | Robolectric suite |
| Importación QR (Photo Picker Android/PHPicker iOS, cámara, share Android, PDF: OpenDocument Android/UIDocumentPicker iOS —runtime iOS en §2) | unit + compilación de instrumentados |
| Registro de actividad (S07) | Robolectric suite |
| Permisos de cámara (política de ajustes) | unit + compilación de instrumentados |
| Navegación (back stack restaurable + NavigationBar 4 destinos) | commonTest + `RootNavigationChromeTest` |
| Accesibilidad (contraste, foco visible, háptica, anuncios, targets 48dp) | `core:ui` tests + Robolectric |
| Iconografía (Lucide ISC vía AmIcon) | compilación 2 targets + gate |
| Feedback transitorio (Snackbar) y persistente (AppStatusHost) | `RootNavigationChromeTest` + `AppStatusHostTest` |

## 2. Implementado pero NO VERIFICADO en este entorno

No se declaran PASS. Requieren Xcode, dispositivo físico o red.

| Capacidad | Motivo | Cómo se verifica |
|---|---|---|
| Runtime iOS (arranque y render) | Requiere Xcode/simulador | [OPERATIONS.md](OPERATIONS.md) (checklist iOS) |
| TalkBack/VoiceOver interactivo | Requiere dispositivo físico | [QUALITY.md](QUALITY.md) §5 |
| Insets/notch, cámara, foco nativo y motion en dispositivo | Requiere dispositivo físico | [QUALITY.md](QUALITY.md) §5 |
| Predictive back en Android 14+ (gesto real con progreso) | Requiere dispositivo físico | [QUALITY.md](QUALITY.md) §5 |
| Photo Picker en dispositivo (contratos del sistema) | Robolectric ejecuta el flujo unitario; el picker visual requiere dispositivo | [QUALITY.md](QUALITY.md) §5 |
| Revisión visual de las capturas Roborazzi regeneradas | El hash PASS es determinista y el análisis de bandas/regiones es programático; el ojo humano valida la composición | abrir `feature/destinations/presentation/build/roborazzi/*.png` (64 capturas: 32 escenarios × claro/oscuro) |
| Supabase acceptance (JDBC) | Requiere base de datos | `:supabase:acceptance:test` con `DATABASE_URL` |
| Sincronización real contra Supabase (subida/bajada) | Requiere backend y cuenta reales | prueba de aceptación contra un proyecto Supabase |
| Prompt biométrico en dispositivo | Código implementado (`BiometricPrompt` con host `FragmentActivity` en Android, `LocalAuthentication`/`LAContext` en iOS); el mapeo de errores y la invariante fail-closed están probados en JVM, pero el prompt sobre hardware real no puede ejecutarse aquí | abrir la app con PIN configurado en dispositivo con biometría inscrita: éxito desbloquea; fallo/cancelación mantiene el bloqueo con el PIN visible |
| Runtime del selector PDF de iOS (`UIDocumentPickerViewController`) | Implementado y compilado contra bindings reales; el contrato del sistema requiere simulador/dispositivo | elegir un PDF en «Elegir archivo (PDF)» en S02 → llega al diálogo «Comprobante recibido» |
| NavigationRail en tablet/plegable | Captura tablet registrada en Roborazzi (w720dp); falta render en hardware real | render real en tablet/plegable |

## 3. Límites conocidos (decisiones, no defectos)

- Glifos «Editar», «Compartir» y «Mostrar QR» sin icono asignado (contrato
  normativo §7): esas acciones de barra superior van como acción de texto (con su
  descripción accesible) hasta una decisión de iconografía.
- El contraste NO textual de algunos acentos de contexto contra el fondo del tema
  es < 3:1 (contrato §4.2): se aceptan como identidad de dato (encabezados y
  punto de fila) siempre con texto legible.
- Autofill de credenciales: `KeyboardOptions.contentType` no existe en Compose
  1.8.2 (llega con 1.9); el autofill funciona por `keyboardType` Email/Password
  (iOS lo mapea a `textContentType`).

## 4. Pendientes conocidos

- **Share Extension de iOS:** `sharedImportsSupported()` devuelve false en iOS; el
  bloque «Desde otra app» de S02 no se muestra hasta que la extensión exista.
  **Bloqueo concreto:** el proyecto Xcode no está versionado en el repositorio
  (política documentada en `iosApp/README.md`) y crear el target de la extensión
  exige Xcode + firma; no puede hacerse ni verificarse desde este entorno.

## 5. No implementado (alcance declarado, no defecto)

Se listan explícitamente para no declararlos cubiertos (ADR-0016):

- **Cifrado de datos en reposo — NO IMPLEMENTADO (decisión documentada).** El
  PIN es una puerta de acceso local (verificador PBKDF2 con sal), no una clave
  derivada de los datos. Almacenamiento: colecciones en
  `SharedPreferences` (JSON, `allowBackup=false`) y bytes de comprobantes en
  `filesDir`/`Application Support` — sin cifrar en reposo. Un cifrado seguro
  requeriría clave envuelta por Keystore/Keychain (no derivada del PIN),
  re-cifrado al cambiar protección y doble lectura durante la migración de
  los datos existentes en claro; no se introduce una migración que pueda
  dejarlos inaccesibles.
- **Vínculo explícito perfil local ↔ cuenta remota — NO IMPLEMENTADO.** «Cuenta y
  respaldo» permite entrar/salir sin borrar datos locales, pero no existe el
  enlace perfil↔cuenta con semántica de respaldo por dispositivo.
- **Canal de recuperación del PIN — NO IMPLEMENTADO.** Olvidar el PIN no tiene
  recuperación en la app; restablecer la protección solo es posible desde dentro
  de la app ya desbloqueada.

## 6. Bloqueos externos

- **Producción Android:** la firma de release requiere keystore y variables
  `ANDROID_KEYSTORE_*`; sin ellas el build de release falla en configuración (ver
  [OPERATIONS.md](OPERATIONS.md)). El ensamblado de release del gate es un
  *candidato* con firma de debug (`-PallowDebugSigningForRc=true`), no una firma
  de producción.
- **Google Play:** la publicación requiere insumos de producto (icono, capturas,
  descripciones, política de privacidad, formulario Data Safety) — ver
  [OPERATIONS.md](OPERATIONS.md).

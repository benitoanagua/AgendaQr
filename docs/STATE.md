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
| Biometría como atajo (disponibilidad Android real, prompt pendiente) + fallback PIN | `LocalAccessBiometricTest` (éxito desbloquea, fallo/cancelación conserva bloqueo) |
| Modo de demostración aislado (perfil `demo-profile-v1`, sin sync) | `DemoDataTest` + `DemoSeederTest` (determinista, idempotente, no toca perfil real) |
| Migración de instalaciones previas al perfil local | data tests (idempotencia, no borra origen, no mezcla cuentas, detección por claves sin cliente remoto) |
| Nube opcional («Cuenta y respaldo» detrás de Inicio) | compilación Android/iOS + gate |
| UX/UI V1.6 (referencia Ant Design Mobile, S00 + S01–S12) | Robolectric suite + hashes visuales + gate del design system |
| Chrome e insets (TopBar, NavigationBar/Rail, Scaffold con contentWindowInsets) | Robolectric suite + gate |
| Edge-to-edge Android (`enableEdgeToEdge`) | ensamblado |
| Predictive back (PredictiveBackHandler con progreso) | compilación 3 targets |
| Búsqueda global (S04/S05) | Robolectric suite |
| Importación QR (Photo Picker Android/PHPicker iOS, cámara, share Android) | unit + compilación de instrumentados |
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
| Revisión visual de las capturas Roborazzi regeneradas | El hash PASS es determinista y el análisis de bandas/regiones es programático; el ojo humano valida la composición | abrir `feature/destinations/presentation/build/roborazzi/*.png` (52 capturas: 26 escenarios × claro/oscuro) |
| Supabase acceptance (JDBC) | Requiere base de datos | `:supabase:acceptance:test` con `DATABASE_URL` |
| Sincronización real contra Supabase (subida/bajada) | Requiere backend y cuenta reales | prueba de aceptación contra un proyecto Supabase |
| Desbloqueo biométrico en dispositivo | Disponibilidad implementada (`BiometricManager` en Android, `LocalAuthentication` pendiente en iOS); el prompt ligado a la Activity y la verificación en hardware requieren dispositivo físico | implementación del prompt + dispositivo (el PIN sigue siendo la vía verificada) |
| NavigationRail en tablet/plegable | No hay dispositivo | render real en tablet/plegable |

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
- **Selector de PDF en iOS** (`UIDocumentPickerViewController`): la acción
  «Elegir archivo (PDF)» solo existe en Android (OpenDocument por el canal de
  comprobantes entrantes); en iOS queda pendiente de la implementación nativa con Xcode.

## 5. No implementado (alcance declarado, no defecto)

Se listan explícitamente para no declararlos cubiertos (ADR-0016):

- **Cifrado de datos en reposo con el PIN — NO IMPLEMENTADO.** El PIN es una
  puerta de acceso local (verificador PBKDF2 con sal), no una clave derivada de
  los datos. Los datos locales NO están cifrados con el PIN.
- **Desbloqueo biométrico (`BiometricPrompt`/`LocalAuthentication`) — PARCIALMENTE
  IMPLEMENTADO.** Disponibilidad real en Android (`BiometricManager`), puerta
  `BiometricGate` con fallback fail-closed al PIN y atajo en `LocalLockScreen`
  cuando está disponible e inscrita. Pendiente: el prompt ligado a la Activity
  (`authenticate()` devuelve `Unavailable` hasta cablearlo) y `LocalAuthentication`
  en iOS; ambos requieren dispositivo físico para verificarse. La taxonomía de
  errores (cancelado, no inscrito, bloqueo del sensor, recuperable) existe en
  `BiometricAuthResult`/`BiometricAvailability`.
- **Migración de los bytes de comprobantes del directorio heredado — NO
  IMPLEMENTADO.** La migración copia las colecciones serializadas; los archivos
  antiguos (`.../comprobantes/<user_id>/`) se conservan en su ruta y no se copian
  a la del perfil local.
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

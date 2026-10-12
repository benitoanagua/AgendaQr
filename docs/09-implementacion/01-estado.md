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
| UX/UI V1.3 (AgendaMobile sobre Material 3, S01–S12) | Implementado | Robolectric suite + hashes visuales + gate del design system |
| Chrome e insets (TopBar, NavigationBar/NavigationRail, Scaffold con contentWindowInsets) | Implementado | Robolectric suite + gate |
| Edge-to-edge Android (`enableEdgeToEdge`) | Implementado | ensamblado; runtime NO VERIFICADO (dispositivo) |
| Predictive back (PredictiveBackHandler con progreso) | Implementado | compilación 3 targets; gesto NO VERIFICADO (dispositivo) |
| Búsqueda global (S04/S05) | Implementado | Robolectric suite |
| Importación QR (Photo Picker Android/PHPicker iOS, cámara, share Android) | Implementado | Instrumentados + unit |
| Registro de actividad (S07) | Implementado | Robolectric suite |
| Permisos de cámara (política de ajustes) | Implementado | Instrumentados |
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
| Revisión visual de las capturas Roborazzi regeneradas | El hash PASS es determinista; el ojo humano valida la composición | abrir `feature/destinations/presentation/build/roborazzi/*.png` (s01/login/s02-add/import-batch × claro/oscuro) |
| Supabase acceptance (JDBC) | Requiere base de datos | `:supabase:acceptance:test` con `DATABASE_URL` |

## Límites conocidos (decisiones, no bugs)

- Glifos "Editar", "Compartir" y "Mostrar QR" sin icono asignado
  (contrato AgendaMobile §7): esas acciones de la barra superior van como acción
  de texto (con su descripción accesible como etiqueta) hasta una
  decisión de iconografía.
- El contraste NO textual de algunos acentos de contexto contra el fondo
  del tema es < 3:1 (contrato AgendaMobile §4.2): se aceptan como
  identidad de dato (encabezados de S06, punto de fila) siempre con
  texto legible.
- Autofill de credenciales: `KeyboardOptions.contentType` no existe en
  Compose 1.8.2 (llega con 1.9); el autofill funciona por `keyboardType`
  Email/Password (iOS lo mapea a `textContentType`).

## Pendientes conocidos

- **Share Extension de iOS**: `sharedImportsSupported()` devuelve false en
  iOS; el bloque "Desde otra app" de S02 no se muestra hasta que la
  extensión exista (ADR-0013).
- **Selector de PDF en iOS** (`UIDocumentPickerViewController`): la acción
  "Elegir archivo (PDF)" solo existe en Android (OpenDocument por el canal
  de comprobantes entrantes); en iOS queda pendiente de la pasada nativa
  con Xcode.
- **NavigationRail**: implementado para ancho expandido vía
  `WindowSizeClass`; su render real en tablet/plegable es NO VERIFICADO
  (no hay dispositivo).

## Puertas de calidad

| Puerta | Comando | Resultado (esta pasada) |
|---|---|---|
| Arquitectura + design system + no-scripts | `./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true` | PASS |
| Tests de módulos | `./gradlew :core:ui:testDebugUnitTest :feature:destinations:presentation:testDebugUnitTest :feature:destinations:domain:test :feature:destinations:data:testDebugUnitTest -PallowDebugSigningForRc=true` | PASS |
| Regresión visual (Roborazzi + SHA-256) | `./gradlew :feature:destinations:presentation:recordRoborazziDebug recordVisualHashes verifyVisualHashes -PallowDebugSigningForRc=true` | PASS (8 capturas renovadas tras el rediseño; revisión visual pendiente — ver NO VERIFICADO) |
| Ensamblado Android | `./gradlew :androidApp:assembleDebug -PallowDebugSigningForRc=true` | PASS |
| Compilación iOS (simulador ARM64, sin runtime) | `./gradlew :core:ui:compileKotlinIosSimulatorArm64 :feature:destinations:presentation:compileKotlinIosSimulatorArm64` | PASS (compila; runtime NO VERIFICADO, sin Xcode) |
| Supabase acceptance (JDBC) | `DATABASE_URL=jdbc:postgresql://... ./gradlew :supabase:acceptance:test` | NO VERIFICADO (sin base de datos) |
| CI (4 jobs) | Push a `main` → Android validation workflow | NO VERIFICADO (fuera del entorno local) |

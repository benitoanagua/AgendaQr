# Agenda QR — Estado de implementación

> Este documento describe el estado ACTUAL: capacidades, límites conocidos y
> bloqueos. La historia de cambios vive en `git log`. Solo se declara
> «verificado» lo que una puerta ejecutada respalda; lo que no puede
> ejecutarse se registra como **NO VERIFICADO**, nunca como PASS.

Las puertas, sus comandos y la validación por plataforma están en
[QUALITY.md](QUALITY.md).

## 1. Implementado y verificado

| Área | Verificación |
|---|---|
| Dominio (operaciones, comprobantes, contextos) | pruebas de dominio |
| Datos locales (persistencia local-first, cola de sincronización) | pruebas de datos |
| Identidad local sin cuenta (perfil local + namespace por perfil) y migración de instalaciones previas | pruebas de dominio/datos + `AgendaQrLocalStartupTest` |
| Acceso local con PIN (configurar/confirmar, desbloquear, bloqueo con gracia de 120 s para flujos externos) | `LocalAccessGateTest` + Robolectric del arranque real |
| Biometría como atajo (Android `BiometricPrompt`, iOS `LAContext`), mapeo de errores e invariante fail-closed | `LocalAccessBiometricTest` + `BiometricPromptErrorMappingTest` |
| Migración de bytes de comprobantes heredados al perfil local | `LegacyComprobanteBytesMigrationTest` |
| Modo de demostración aislado (perfil `demo-profile-v1`, sin sincronización) | `DemoDataTest` + `DemoSeederTest` |
| Nube opcional («Cuenta y respaldo») | compilación Android/iOS + gate + verificación de layout |
| Chrome e insets, edge-to-edge, predictive back, navegación con back stack restaurable | Robolectric + pruebas comunes + gate + compilación de los tres targets |
| Búsqueda global (S04/S05) con conteos reales por tipo | Robolectric |
| Importación QR (Photo Picker/PHPicker, cámara, share Android, PDF) | pruebas unitarias + `ImportFlowRegressionTest` + compilación de instrumentados |
| Jerarquía de acciones de 4 niveles y confirmaciones de descarte | `ActionHierarchyTest` + `ContextPickerDialogTest` + gate |
| Registro de actividad (S07) y detalles | Robolectric |
| Permisos de cámara (política de ajustes) | pruebas unitarias + compilación de instrumentados |
| Accesibilidad (contraste, foco visible, háptica, anuncios, objetivos ≥ 48 dp) | pruebas de `core:ui` + Robolectric |
| Sistema visual Moon (`moon_tokens` 0.0.6, `moon-icons` 10.7.4) en `core/ui`: tokens semánticos claro/oscuro, Inter 4.0 empaquetado, pares AA, iconografía convertida a vectores Compose, movimiento, previews a 200 % y avisos de licencia | `verifyAgendaQrArchitecture` + `:core:ui:testDebugUnitTest` + `:androidApp:assembleDebug` con `-PallowDebugSigningForRc=true` |
| **Moon Design como único sistema visual (ADR-0044): Moon rige todo el UI/UX (anatomía, tamaños, radios squircle, bordes, sombras, opacidades, movimiento, efecto de foco, color, iconos y escala tipográfica; ADR-0044). Cero símbolos legacy en producción, cero wrappers `AqMoon*`, cero Material en feature/apps/shared.** | `:core:ui:testDebugUnitTest` verde; `verifyDesignSystemCompliance` VERDE; `verifyDesignSystemFixtures` VERDE; `verifyControlMinSize` VERDE; `verifyLayoutGeometry` VERDE; `verifyNoScripts` VERDE; `verifyDocs` VERDE |
| Componente `AqCircularProgress` portado (widgets/progress), catálogo y galería claro/oscuro | `:core:ui:testDebugUnitTest` + gate |
| Duración de `AqToast`: 3 000 ms (`AqMotion.ToastDurationMs` = `displayDuration` por defecto de Moon; animación y separación siguen a Moon) | `AqToastTest` |
| Reimplementación de `AqModal` sobre `androidx.compose.ui.window.Dialog` (focus trap, back/predictive back, `paneTitle`, scrim clicable) | `:core:ui:testDebugUnitTest` |
| Migración pantalla por pantalla a componentes Moon reales (sin wrappers `AqMoon*`): UnassociatedScreen (piloto), DestinationsScreen, DestinationDetailScreen, ContextScreen, ImportBatchScreen, OperationDetailScreen, OperationEditorScreen, OperationListContent/Grouped/Screen, ContextPickerDialog, GlobalSearchScreen, DestinationEditorScreen, AddDestinationScreen, ImportReviewScreen, AuthScreen, AccountScreen, LocalOnboardingScreen, LocalLockScreen, ComprobanteViewer, AppStatusHost, AgendaQrPatterns, AgendaQrApp + AuthenticatedAppRoot | `:feature:destinations:presentation:testDebugUnitTest` + `verifyDesignSystemCompliance` + commits por pantalla |
| Pantallas de plataforma (CameraQrCapture, QrImportControls, ImportBatchControls, IosCameraQrCapture, etc.) sin Material ni legacy | compilación de los módulos de plataforma |
| Eliminación del legacy en la capa de producción de `feature/`, `shared/` y las apps (cero símbolos legacy, cero `AqMoon*`, cero Material) | `verifyDesignSystemCompliance` |
| Gate: prohíbe `AqMoon*` y símbolos legacy en `feature/`, `shared/` y las apps, exige componente Moon real por archivo de feature con UI, prohíbe `AqShapes` repo-wide, fixtures negativos y positivos | `verifyDesignSystemCompliance` + `verifyDesignSystemFixtures` |
| Contrato de interacción por pantalla (19 superficies) y galería estricta de capturas por superficie (360 dp, claro/oscuro) | `verifyScreenContract` + `verifyScreenContractFixtures` + `captureGallery` |
| Documentación: enlaces vivos, alcanzabilidad desde el índice, ausencia de marcadores transitorios | `verifyDocs` |
| Selección múltiple por lote (G3 / ADR-0034) | pruebas de presentación + gate |
| Estado `loading` de Actividades y esqueleto en frío (N02) | `:feature:destinations:presentation:testDebugUnitTest` + gate |
| Lista de Actividades virtualizada con LazyColumn (N03) | pruebas de presentación + gate |
| Encabezados sticky mes/día y balance neto mensual por moneda (G4 / ADR-0035) | pruebas de dominio (OperationGroupingTest) + presentación |
| «Destino QR vinculado» en el detalle de actividad (D8) | pruebas de presentación + gate |
| Errores de campo visibles y foco al primer error en editores (G1 / ADR-0033) | pruebas de presentación + `core:ui` (AqFormFocusTest) |
| Límites de campo y contador «n/max» (G2 / ADR-0033) | pruebas de dominio + presentación |
| Insignia de navegación con conteo (G5 / ADR-0035, componente) | `:core:ui:testDebugUnitTest` + gate |
| Filtros S12 siempre que haya pendientes (G7) | pruebas de presentación |
| «Crear contexto» siempre disponible en S08 (G7 / ADR-0033) | pruebas de presentación (ContextPickerDialogTest) |
| Movimiento reducido en swipe/pull (política Moon del contrato normativo) | `:core:ui:testDebugUnitTest` |
| Retiro de componentes sin respaldo AqBox/AqTriState/AqLightButton (N07) | `:core:ui:testDebugUnitTest` + gate |

## 2. Implementado pero NO VERIFICADO

No se declaran PASS. Requieren Xcode, dispositivo físico, red o backend reales.

| Capacidad | Cómo se verifica |
|---|---|
| Runtime iOS (arranque, render, claro/oscuro, fuente al 150 %, Inter y avisos de licencia en el bundle) | [OPERATIONS.md](OPERATIONS.md) (checklist iOS) |
| Pruebas instrumentadas Android (`instrumented-tests`) | El job es no bloqueante y no está en verde; persisten fallos en recorrido de permisos/cámara, acceso a destinos demo, restauración del formulario, capturas del emulador y clicables de S01 sin nombre accesible. No se afirma validación en dispositivo físico. |
| TalkBack/VoiceOver interactivo | [QUALITY.md](QUALITY.md) §5 |
| Insets/notch, cámara, foco nativo y movimiento en dispositivo | [QUALITY.md](QUALITY.md) §5 |
| Predictive back en Android 14+ (gesto real) | [QUALITY.md](QUALITY.md) §5 |
| Photo Picker y selector PDF en dispositivo (Android/iOS) | elegir un archivo en S02 → llega al diálogo «Comprobante recibido» |
| Prompt biométrico sobre hardware | abrir la app con PIN configurado en un dispositivo con biometría: éxito desbloquea; fallo o cancelación mantiene el bloqueo |
| Flujos de importación con fallos parciales en dispositivo | revisión manual |
| `NavigationRail` en tablet/plegable | render en hardware real |
| Revisión estética de la galería en dispositivo | abrir `feature/destinations/presentation/build/captures/gallery/index.html` |
| Supabase acceptance (JDBC) | `:supabase:acceptance:test` con `DATABASE_URL` |
| Sincronización real contra Supabase (subida/bajada) | prueba de aceptación contra un proyecto Supabase con cuenta real |

## 3. Límites conocidos (decisiones, no defectos)

- Gracia de bloqueo de 120 s (`ExternalFlowGraceMillis`): si el sistema destruye el proceso fuera de la gracia, el borrador en memoria se pierde.
- `FLAG_SECURE` no implementado (ADR-0009, propuesto): las pantallas sensibles admiten capturas; implementarlo bloquea también las capturas del QR.
- Paginación local de 50 en 50, sin cambios.
- Divisor decorativo y borde de superficie son tokens de la capa de color, válidos como separadores; los límites funcionales siguen ≥ 3:1 (contrato §4.4).
- Cuerpo a 14 sp (contrato §5; mínimo absoluto 11 sp): a 360 dp y 150 % de fuente los desbordamientos se resuelven con layout, nunca bajando la escala. Al 200 % la capacidad se conserva pero queda fuera de la verificación automática (ADR-0007) y requiere revisión manual.
- Glifos «Editar», «Compartir» y «Mostrar QR» sin icono asignado: esas acciones de barra superior van como acción de texto con su descripción accesible.
- El contraste NO textual de algunos acentos de contexto contra el fondo del tema es < 3:1 (contrato §4.2): se aceptan como identidad de dato, siempre con texto legible.
- Autofill de credenciales: `KeyboardOptions.contentType` no existe en Compose 1.8.2; el autofill funciona por `keyboardType` Email/Password.
- Licencia: Moon se distribuye bajo MIT (Yolo Group); el aviso va en `androidApp/third-party-notices/`. La familia Inter se mantiene porque Averta Std (fuente de Moon) es comercial.

## 4. Pendientes conocidos

- **Share Extension de iOS:** no implementada; el bloque «Desde otra app» de S02 no se muestra en iOS hasta que exista (`supportsShareExtension = false`, ADR-0037). Bloqueo: el proyecto Xcode no está versionado (política en `iosApp/README.md`) y crear el target exige Xcode y firma.

## 5. No implementado (alcance declarado, no defecto)

- **Cifrado de datos en reposo.** El PIN es una puerta de acceso local (verificador PBKDF2 con sal), no una clave derivada de los datos. Almacenamiento: colecciones en `SharedPreferences` (JSON, `allowBackup=false`) y bytes de comprobantes en `filesDir`/`Application Support`, sin cifrar. Un cifrado seguro requiere clave envuelta por Keystore/Keychain (no derivada del PIN), re-cifrado al cambiar la protección y doble lectura durante la migración de datos en claro.
- **Vínculo explícito perfil local ↔ cuenta remota.** «Cuenta y respaldo» permite entrar y salir sin borrar datos locales, pero no existe el enlace perfil↔cuenta con semántica de respaldo por dispositivo.
- **Canal de recuperación del PIN.** Olvidar el PIN no tiene recuperación en la app; restablecer la protección solo es posible con la app desbloqueada.

## 6. Bloqueos externos

- **Producción Android:** la firma de release requiere keystore y variables `ANDROID_KEYSTORE_*`; sin ellas el build de release falla en configuración (ver [OPERATIONS.md](OPERATIONS.md)). El ensamblado de release del gate es un candidato con firma de debug (`-PallowDebugSigningForRc=true`), no una firma de producción.
- **Google Play:** la publicación requiere icono, capturas, descripciones, política de privacidad y formulario Data Safety (ver [OPERATIONS.md](OPERATIONS.md)).

## 7. Convenciones Moon vigentes

- `AqButton(fullWidth = true)` llena el ancho disponible del padre; el alto visual sigue el tamaño Moon y el target táctil se mantiene separado.
- `AqTextInput` md conserva 40 dp de caja visual; el wrapper de toque alcanza 48 dp sin expandir el borde visible.
- `AqDropdown` es un menú anclado, no un campo select. Para selección cerrada se usa `AqTextInput(readOnly = true)` como ancla; no se declara una API `AqSelect` inexistente.
- No se crean wrappers `AqMoon*` ni se reintroducen símbolos visuales legacy.
- `AqToast` dura 3 000 ms (`displayDuration` por defecto de Moon, ADR-0047); Moon aporta el comportamiento de animación y separación.
- Las composiciones Moon-only usan nombres propios de Agenda QR y componentes de `core/ui`.
- La fuente normativa actual es `docs/05-design-system/README.md`; el historial de decisiones anteriores se conserva en Git.

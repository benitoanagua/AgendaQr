# Agenda QR — Estado de implementación

> Este documento describe el estado ACTUAL: capacidades, límites conocidos y
> bloqueos. La historia de cambios vive en `git log`. Solo se declara
> «verificado» lo que una puerta ejecutada respalda; lo que no puede ejecutarse
> se registra como **NO VERIFICADO**, nunca como PASS.

Las puertas, sus comandos y la validación por plataforma están en
[QUALITY.md](QUALITY.md).

## 1. Implementado y verificado

| Área | Verificación |
|---|---|
| Dominio (operaciones, comprobantes, contextos) | pruebas de dominio |
| Datos locales (persistencia local-first, cola de sincronización) | pruebas de datos |
| Identidad local sin cuenta (perfil local + namespace por perfil) y migración de instalaciones previas (idempotente, no borra origen, no mezcla cuentas) | pruebas de dominio/datos + `AgendaQrLocalStartupTest` |
| Acceso local con PIN (configurar/confirmar, desbloquear, bloqueo con gracia de 120 s para flujos externos) | `LocalAccessGateTest` + Robolectric del arranque real |
| Biometría como atajo (Android `BiometricPrompt`, iOS `LAContext`), mapeo de errores e invariante fail-closed | `LocalAccessBiometricTest` + `BiometricPromptErrorMappingTest` |
| Migración de bytes de comprobantes heredados al perfil local | `LegacyComprobanteBytesMigrationTest` |
| Modo de demostración aislado (perfil `demo-profile-v1`, sin sincronización) | `DemoDataTest` + `DemoSeederTest` |
| Nube opcional («Cuenta y respaldo») | compilación Android/iOS + gate + verificación de layout |
| Chrome e insets, edge-to-edge, predictive back, navegación con back stack restaurable | Robolectric + pruebas comunes + gate + compilación de los tres targets |
| Búsqueda global (S04/S05) con conteos reales por tipo | Robolectric |
| Importación QR (Photo Picker/PHPicker, cámara, share Android, PDF), con corrección de fallos parciales y reintentos | pruebas unitarias + `ImportFlowRegressionTest` + compilación de instrumentados |
| Jerarquía de acciones de 4 niveles y confirmaciones de descarte | `ActionHierarchyTest` + `ContextPickerDialogTest` + gate |
| Registro de actividad (S07) y detalles | Robolectric |
| Permisos de cámara (política de ajustes) | pruebas unitarias + compilación de instrumentados |
| Accesibilidad (contraste, foco visible, háptica, anuncios, objetivos ≥ 48 dp) | pruebas de `core:ui` + Robolectric |
| Sistema visual Polaris 9.4.2 en `core/ui`: tokens semánticos claro/oscuro, Inter 4.0 empaquetado, pares AA, iconografía convertida a vectores Compose, movimiento, previews a 200 % y avisos de licencia | `verifyAgendaQrArchitecture` + `:core:ui:testDebugUnitTest` + `:androidApp:assembleDebug` con `-PallowDebugSigningForRc=true` |
| Viraje Moon Design (ADR-0039/0040): Moon rige UI/UX (anatomía, tamaños, radios squircle, bordes, sombras, opacidades, movimiento, efecto de foco); Polaris solo color, tipografía e iconos. **EN PROGRESO — gate `verifyDesignSystemCompliance` en ROJO** (ver § Backlog de migración Moon). Componentes Moon ya puerto como código: `AqButton`, `AqModal`, `AqBottomSheet` (duplicado: `AqOverlay.kt` y `AqSheets.kt`), `AqDrawer`, `AqTabBar`, `AqPillTab`, `AqCircularLoader`, `AqLinearLoader`, `AqCheckbox`, `AqSwitch`, `AqSegmentedControl`, `AqTooltip`. Faltan como código: `AqTextInput`, `AqTextArea`, `AqTextInputGroup`, `AqAlert`, `AqToast`, `AqChip`, `AqTag`, `AqAvatar`, `AqMenuItem`, `AqAccordion`, `AqDropdown`, `AqPopover`, `AqTable`, `AqAuthCode`, `AqDotIndicator`, `AqRadio`, `AqBreadcrumb`, `AqErrorMessage`, `AqCircularProgress`, `AqLinearProgress`, `AqProgressPin`. Las pantallas de `feature/` aún usan 0 componentes Moon; todo el legado Polaris sigue activo. | `:core:ui:testDebugUnitTest` verde; `verifyDesignSystemCompliance` ROJO (484 violaciones legacy + 1 duplicado + 23 incumplimientos de catálogo) |
| Componentes móviles adoptados en pantallas reales: ninguno con componentes Moon. Las pantallas siguen usando el legado Polaris (`AqTopBar`, `AqScreenScaffold`, `AqDialog`, `AqListRow`, `AqTextField`, `AqSnackbarHost`, `AqPrimaryButton`…). La migración pantalla por pantalla es el backlog de § Backlog de migración Moon. | 0 archivos de `feature/` importan un `Aq*` de Moon |
| Contrato de interacción por pantalla (19 superficies) y galería estricta de capturas por superficie (360 dp, claro/oscuro) | `verifyScreenContract` + `verifyScreenContractFixtures` + `captureGallery` |
| Documentación: enlaces vivos, alcanzabilidad desde el índice, ausencia de marcadores transitorios | `verifyDocs` |
| Selección múltiple por lote en Actividades, destinos e inbox (G3 / ADR-0034) | pruebas de presentación + gate |
| Estado `loading` de Actividades y esqueleto en frío (N02) | `:feature:destinations:presentation:testDebugUnitTest` + gate |
| Lista de Actividades virtualizada con LazyColumn (N03) | pruebas de presentación + gate |
| Encabezados sticky mes/día y balance neto mensual por moneda (G4 / ADR-0035) | pruebas de dominio (OperationGroupingTest) + presentación |
| «Destino QR vinculado» en el detalle de actividad (D8) | pruebas de presentación + gate |
| Errores de campo visibles y foco al primer error en editores (G1 / ADR-0033) | pruebas de presentación + `core:ui` (AqFormFocusTest) |
| Límites de campo y contador «n/max» (G2 / ADR-0033) | pruebas de dominio + presentación |
| Insignia de navegación con conteo (G5 / ADR-0035, componente) | `:core:ui:testDebugUnitTest` + gate |
| Filtros S12 siempre que haya pendientes (G7) | pruebas de presentación |
| «Crear contexto» siempre disponible en S08 (G7 / ADR-0033) | pruebas de presentación (ContextPickerDialogTest) |
| Movimiento reducido en swipe/pull (N05 / ADR-0029 §7) | `:core:ui:testDebugUnitTest` |
| Retiro de componentes sin respaldo AqBox/AqTriState/AqLightButton (N07) | `:core:ui:testDebugUnitTest` + gate |

## 2. Implementado pero NO VERIFICADO

No se declaran PASS. Requieren Xcode, dispositivo físico, red o backend reales.

| Capacidad | Cómo se verifica |
|---|---|
| Runtime iOS (arranque, render, claro/oscuro, fuente al 150 %, Inter y avisos de licencia en el bundle) | [OPERATIONS.md](OPERATIONS.md) (checklist iOS) |
| Pruebas instrumentadas Android (`instrumented-tests`) | El job es no bloqueante (`continue-on-error: true`) y no está en verde: persisten fallos en el recorrido de permisos/cámara, acceso a destinos demo, restauración del formulario, capturas del emulador y clicables de S01 sin nombre accesible. No se afirma validación en dispositivo físico. |
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

- Gracia de bloqueo de 120 s (`ExternalFlowGraceMillis`): si el sistema destruye
  el proceso fuera de la gracia, el borrador en memoria se pierde.
- `FLAG_SECURE` no implementado (ADR-0009, propuesto): las pantallas sensibles
  admiten capturas; implementarlo bloquea también las capturas del QR.
- Paginación local de 50 en 50, sin cambios.
- Divisor decorativo y borde de superficie son tokens de la capa de color,
  válidos como separadores; los límites funcionales siguen ≥ 3:1 (contrato §4.4).
- Cuerpo a 14 sp (contrato §5; mínimo absoluto 11 sp): a 360 dp y 150 % de fuente
  los desbordamientos se resuelven con layout, nunca bajando la escala. Al 200 %
  la capacidad se conserva pero queda fuera de la verificación automática
  (ADR-0007) y requiere revisión manual.
- Glifos «Editar», «Compartir» y «Mostrar QR» sin icono asignado: esas acciones
  de barra superior van como acción de texto con su descripción accesible.
- El contraste NO textual de algunos acentos de contexto contra el fondo del
  tema es < 3:1 (contrato §4.2): se aceptan como identidad de dato, siempre con
  texto legible.
- Autofill de credenciales: `KeyboardOptions.contentType` no existe en Compose
  1.8.2; el autofill funciona por `keyboardType` Email/Password.
- Riesgo legal de la reimplementación visual: los recursos de diseño de Polaris
  tienen licencia restrictiva. El propietario aceptó el riesgo documentado; solo
  se reimplementan tokens y patrones en Kotlin/Compose, sin runtime, nombre, logo
  ni marca en la app, la UI ni la documentación de producto.

## 4. Pendientes conocidos

- **Share Extension de iOS:** no implementada; el bloque «Desde otra app» de S02
  no se muestra en iOS hasta que exista (`supportsShareExtension = false`,
  ADR-0037). Bloqueo: el proyecto Xcode no está versionado (política en
  `iosApp/README.md`) y crear el target exige Xcode y firma.

Los demás pendientes del cierre de gaps están implementados y verificados
(ver §1) o descartados definitivamente con ADR (selección múltiple ADR-0034,
teclado propio del importe ADR-0036, badge de navegación ADR-0035, límites de
campo ADR-0033).

## 4.1 Backlog de migración Moon (gate `verifyDesignSystemCompliance` en ROJO)

Este backlog es la salida real del gate con las reglas de la Fase A activas.
El gate pasa a VERDE cuando: (a) no quede ningún símbolo legacy importado ni
llamado en `feature/`, `androidApp`, `iosApp` ni `shared`; (b) no quede más de
una declaración pública de un mismo `Aq*`; (c) el catálogo `13-catalogo-moon.md`
no tenga componentes `pendiente` ni `revisar` y cada `disponible`/`adoptado`
tenga su `Aq*` exportado; y (d) `core/ui/components` no use `AqShapes`.

### 4.1.1 Símbolos legacy prohibidos en uso (484 violaciones en 32 archivos)

Cada archivo de `feature/destinations/presentation` (commonMain, androidMain,
iosMain) importa o llama al menos un símbolo del registro de legacy:
`AqTopBar`, `AqScreenScaffold`, `AqSnackbarHost`, `AqDialog`, `AqDialogList`,
`AqListRow`, `AqListBody`, `AqTextField`, `AqTextAction`, `AqPrimaryButton`,
`AqSecondaryButton`, `AqDangerButton`, `AqBottomAction`, `AqCard`, `AqEmptyState`,
`AqStatusBanner`, `AqLoading`, `AqSearchTrigger`, `AqDatePickerDialog`,
`AqAmountField`, `AqFormField`, `AqChipGroup`, `AqExpansionPanel`, `AqBadge`,
`AqIconBadge`, `AqCompactAction`, `AqTappableCard`, `AqSkeleton`,
`AqLoadMoreFooter`, `AqOverflowAction`, `AqSelectionBar`, `AqSectionHeader`,
`AqSearchField`, `AqFeedbackEvent` y `AqShapes`.

Los archivos con más llamadas legacy (orden aproximado de prioridad de
migración, §2 del mandato): `ContextScreen`, `OperationDetailScreen`,
`OperationListContent`, `OperationEditorScreen`, `ImportBatchScreen`,
`DestinationsScreen`, `UnassociatedScreen`, `ContextPickerDialog`,
`DestinationDetailScreen`, `DestinationEditorScreen`, `GlobalSearchScreen`,
`ImportReviewScreen`, `AuthScreen`, `AgendaQrPatterns`, `OperationListGrouped`,
`LocalOnboardingScreen`, `LocalLockScreen`, `AccountScreen`,
`ComprobanteViewer`, `AddDestinationScreen`, `AppStatusHost`,
`OperationListScreen`. Además `androidApp`, `iosApp` y `shared` (p. ej.
`CameraQrCapture.android.kt`, `QrImportControls.ios.kt`,
`IosCameraQrCapture.ios.kt`).

### 4.1.2 Declaración duplicada (1 símbolo)

- `AqBottomSheet` declarado dos veces: `core/ui/.../components/AqOverlay.kt:95`
  y `core/ui/.../components/AqSheets.kt:52`. Debe quedar una sola
  implementación (el puerto fiel de Moon en `AqSheets.kt`; borrar la de
  `AqOverlay.kt`).

### 4.1.3 Catálogo Moon incompleto (23 incumplimientos)

Componentes con estado `pendiente` (no portados como código):
`AqAccordion`, `AqAlert`, `AqAuthCode`, `AqAvatar`, `AqBottomSheet`,
`AqBreadcrumb`, `AqCarousel`, `AqChip`, `AqDotIndicator`, `AqDropdown`,
`AqErrorMessage`, `AqTextInput`, `AqMenuItem`, `AqPopover`, `AqTable`,
`AqTag`, `AqTextArea`, `AqTextInputGroup`, `AqToast`, `AqTooltip`.

Componentes marcados `disponible` en el catálogo pero **sin declaración en
`core/ui`**: `AqLinearProgress`, `AqProgressPin`, `AqRadio`.

Cada uno requiere: puerto fiel a `moon_flutter` 1.1.0, ficha en
`docs/05-design-system/componentes/<nombre>.md`, entrada en
`AqDesignSystemGallery` (claro y oscuro), pruebas de contrato y
accesibilidad.

### 4.1.4 Cómo se cierra

1. Fase B: implementar los componentes Moon faltantes en `core/ui` y unificar
   `AqBottomSheet`.
2. Fase C: migrar cada pantalla de `feature/` (y `androidApp`/`iosApp`/`shared`
   si tocan UI) sustituyendo legado por Moon sin wrappers.
3. Fase D: borrar el legado de `core/ui` (componentes, tests, previews,
   entradas de galería y fichas de docs), `AqShapes` y los tests de contrato
   obsoletos.
4. El gate queda VERDE cuando `grep -rEn "<legacy>|AqShapes"` devuelve 0
   resultados y `verifyDesignSystemCompliance` pasa.

## 5. No implementado (alcance declarado, no defecto)

- **Cifrado de datos en reposo.** El PIN es una puerta de acceso local
  (verificador PBKDF2 con sal), no una clave derivada de los datos.
  Almacenamiento: colecciones en `SharedPreferences` (JSON, `allowBackup=false`)
  y bytes de comprobantes en `filesDir`/`Application Support`, sin cifrar. Un
  cifrado seguro requiere clave envuelta por Keystore/Keychain (no derivada del
  PIN), re-cifrado al cambiar la protección y doble lectura durante la migración
  de datos en claro.
- **Vínculo explícito perfil local ↔ cuenta remota.** «Cuenta y respaldo» permite
  entrar y salir sin borrar datos locales, pero no existe el enlace
  perfil↔cuenta con semántica de respaldo por dispositivo.
- **Canal de recuperación del PIN.** Olvidar el PIN no tiene recuperación en la
  app; restablecer la protección solo es posible con la app desbloqueada.

## 6. Bloqueos externos

- **Producción Android:** la firma de release requiere keystore y variables
  `ANDROID_KEYSTORE_*`; sin ellas el build de release falla en configuración
  (ver [OPERATIONS.md](OPERATIONS.md)). El ensamblado de release del gate es un
  candidato con firma de debug (`-PallowDebugSigningForRc=true`), no una firma
  de producción.
- **Google Play:** la publicación requiere icono, capturas, descripciones,
  política de privacidad y formulario Data Safety (ver
  [OPERATIONS.md](OPERATIONS.md)).

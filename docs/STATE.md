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
| Nube opcional («Cuenta y respaldo» detrás de Inicio) | compilación Android/iOS + gate + verificación de layout `account-{light,dark}` (SignedOut con doble determinista) |
| Chrome e insets (TopBar, NavigationBar/Rail, Scaffold con contentWindowInsets) | Robolectric suite + gate (incl. verificación de layout tablet w720dp con NavigationRail) |
| Edge-to-edge Android (`enableEdgeToEdge`) | ensamblado |
| Predictive back (PredictiveBackHandler con progreso) | compilación 3 targets |
| Búsqueda global (S04/S05) | Robolectric suite |
| Importación QR (Photo Picker Android/PHPicker iOS, cámara, share Android, PDF: OpenDocument Android/UIDocumentPicker iOS —runtime iOS en §2) | unit + compilación de instrumentados |
| Importación con corrección de fallos parciales y reintentos (bug fix: todos los candidatos fallaban → estado Saved en lugar de Error) | `ImportFlowRegressionTest` (error all-failed, partial save, retry after error, after successful retry, multiple issues, small viewport, login error) |
| Acciones de las tarjetas de revisión de importación separadas visualmente | `ActionHierarchyTest.duplicate_card_offers_view_existing_as_main_and_saves_anyway_as_secondary` verifica que «Guardar de todos modos» y «Descartar» no colisionen |
| Jerarquía de acciones de 4 niveles documentada en `Am*` (primaria/secundaria/terciaria/destructiva) y aplicada en los flujos | javadoc de `AmButtons.kt` + `ActionHierarchyTest` (confirmación de descarte, jerarquía por tipo de incidencia, sin acciones incompatibles con el estado) + gate del design system |
| Descarte de pendientes SIN clasificar con confirmación (pérdida de contenido retenido); duplicados sin confirmación (el original existe) | `ActionHierarchyTest.unknown_card_offers_retry_as_main_action_and_discard_with_confirmation` + `.duplicate_card_offers_view_existing_as_main_and_saves_anyway_as_secondary` |
| «Quitar contexto» como acción destructiva explícita (peligro + glifo) dentro del selector; «Cancelar» única salida del diálogo | `ContextPickerDialogTest` + gate |
| Registro de actividad (S07) | Robolectric suite |
| Permisos de cámara (política de ajustes) | unit + compilación de instrumentados |
| Navegación (back stack restaurable + NavigationBar 4 destinos) | commonTest + `RootNavigationChromeTest` |
| Accesibilidad (contraste, foco visible, háptica, anuncios, targets 48dp) | `core:ui` tests + Robolectric |
| Iconografía TDesign convertida a vectores Compose (34 glifos fuente) | compilación Android/iOS + gate |
| Tokens de color Warning e Info añadidos a AmColor | gate del design system |
| Feedback transitorio (Snackbar) y persistente (AppStatusHost) | `RootNavigationChromeTest` + `AppStatusHostTest` |
| Reformas visuales por pantalla (brechas G1–G9: botón primario fijo al pie, tarjetas de ajustes con encabezado de sección, insignia de estado con texto, evidencia decodificada en tarjeta, metadatos de fecha y marca de tipo en resultados) | gate del design system + Robolectric de presentación + verificación de layout (`LayoutReferenceTest`) |
| S01 Inicio ajustado a la composición UX/UI vigente (spec V1.15): navbar «Inicio» como el resto de raíces y filtros Todos/Favoritos como control segmentado con conteo y semántica de selección | Robolectric de presentación (`DestinationsScreenA11yTest`: etiqueta estable + `selected` + rol radio) + verificación de layout (`LayoutReferenceTest` s01 claro/oscuro) |
| S00 acceso local ajustado a la composición UX/UI vigente (spec V1.16/V1.18): desbloqueo y onboarding (crear → confirmar, dos pasos) con héroe, puntos enmascarados y teclado propio 3×4 con borrado accesible; envío explícito (PIN 4–12), biometría y demo como acciones de texto | `:core:ui` (`AmAccessibilityContractTest`: teclas con rol y ≥48 dp, borrado con nombre accesible) + Robolectric (`LocalAccessGateTest`, `AgendaQrLocalStartupTest` con el flujo completo por teclado, `LocalAccessBiometricTest`) |
| S04 Buscar con conteos reales por tipo (spec V1.19): encabezados de grupo con conteo real. S06 y S08 reconciliadas sin cambios (ya responden a la composición UX/UI verificable; las capacidades no respaldadas se mantienen fuera: datos/agregados inexistentes, decisiones ADR-0003 y reclamos falsos de cifrado) | Robolectric de presentación + contratos S04/S06/S08/S09 con componentes reales (sin `AmTonalTag`/`AmPrimaryButton`/`AmSearchField`/`AmSectionHeader`/`AmDialogList` declarados sin uso) |
| S07/S07-list/S12 y los detalles (destinationdetail/operationdetail) reconciliados con la especificación UX/UI vigente (spec V1.21) sin cambios de composición: ya responden a los diseños en lo verificable (segmentado, FAB, encabezado de mes, tags, hoja de opciones, contenido expandible/copiable, estadísticas del lote); los contratos de estas 5 superficies no tenían deriva | Robolectric de presentación (445 pruebas) + `verifyScreenContract` (19 superficies) |
| Galería estricta por superficie (ADR-0007): 94 capturas — las 19 superficies y sus estados declarados a 360 dp claro/oscuro, subconjunto denso a 320 dp, raíces con `NavigationRail` a 720 dp (tablet), subconjunto a fuente 200 %; overlays (S08/S03) capturados como su ventana de diálogo; manifiesto `agendaqr.captures/3` con dimensiones reales y test de origen | `./gradlew captureGallery -Pagendaqr.capture=true` produce ambas galerías + manifiesto; `verifyScreenContract` valida esquema, dimensiones reales, duplicados y cobertura (94 capturas verificadas) |
| S02 Añadir ajustado a la composición UX/UI vigente (spec V1.17): héroe centrado y grupo único de métodos en orden de jerarquía declarada (Galería → Cámara → Archivo), divisores correctos vía parámetro en los controles de importación | Robolectric de presentación (`ScreensA11yCoverageTest.s02_action_order_follows_declared_hierarchy`, `S02UiPolishTest`) + compilación de los tres targets (controles expect/actual) |
| Dirección visual TDesign Mobile 0.2.7 (ADR-0023): tokens, componentes, acciones de Inicio, escala tipográfica, elevación, movimiento y pulsación | gate del design system + Robolectric de presentación + verificación de layout (escenarios 320 dp, escala de fuente 200 % y estados de botones); revisión en dispositivo real pendiente y declarada como no verificada |
| Contrato de interacción por pantalla (19 superficies: ruta real, destinos válidos, estados, gestos, componentes, pruebas con señal de accesibilidad y escenarios de captura) | `verifyScreenContract` + `verifyScreenContractFixtures` (dentro de `verifyAgendaQrArchitecture`): cruza el contrato con `AppRoute`/rutas de flujo, archivos, símbolos de `core/ui`, claves de `AppStrings`, pruebas y el documento generado |
| Pulsación fiel a TDesign en botones, celdas, tarjetas y acciones de diálogo (color activo, sin ripple) | `:core:ui:testDebugUnitTest`; desviación D-11 para el resto de superficies |

## 2. Implementado pero NO VERIFICADO en este entorno

No se declaran PASS. Requieren Xcode, dispositivo físico/red o ejecución local de las verificaciones de layout.

| Capacidad | Motivo | Cómo se verifica |
|---|---|---|
| Runtime iOS (arranque y render) | Requiere Xcode/simulador | [OPERATIONS.md](OPERATIONS.md) (checklist iOS) |
| CI del PR #143 (commit `ed18b9e`) y push a `main` (`bebe66a`) | En ambos runs, `test`, `ios-compile` y `supabase-acceptance` terminaron en **success**; `instrumented-tests` terminó en **failure** (12/15 fallos en PR; no bloqueante por `continue-on-error: true`). El run de `main` también finalizó y no se declara el workflow completo verde. | [Android validation — PR #143](https://github.com/benitoanagua/AgendaQr/actions/runs/39071445726) · [Android validation — main `bebe66a`](https://github.com/benitoanagua/AgendaQr/actions/runs/39071868104) |
| CI del PR #144 (commit `5c59aaf`) | Los checks bloqueantes `test`, `ios-compile` y `supabase-acceptance` terminaron en **success**. `instrumented-tests` terminó en **failure**: 8/15 fallos; pasan las 3 pruebas de `ShareIntentsTest`, el escenario de error de correo, el de demo sin sincronización y el de movimiento reducido. Persisten fallos en el recorrido de permisos/cámara, acceso a destinos demo, restauración del formulario, capturas del emulador y clicables S01 sin nombre accesible. El job es no bloqueante (`continue-on-error: true`); no se declara el workflow completo verde ni se afirma validación en dispositivo físico. | [Android validation — PR #144](https://github.com/benitoanagua/AgendaQr/actions/runs/39075364369) |
| TalkBack/VoiceOver interactivo | Requiere dispositivo físico | [QUALITY.md](QUALITY.md) §5 |
| Insets/notch, cámara, foco nativo y motion en dispositivo | Requiere dispositivo físico | [QUALITY.md](QUALITY.md) §5 |
| Predictive back en Android 14+ (gesto real con progreso) | Requiere dispositivo físico | [QUALITY.md](QUALITY.md) §5 |
| Photo Picker en dispositivo (contratos del sistema) | Robolectric ejecuta el flujo unitario; el picker visual requiere dispositivo | [QUALITY.md](QUALITY.md) §5 |
| Validación de los nuevos escenarios de importación (corrección de fallos parciales y reintentos) | El bug fix está implementado y cubierto por tests unitarios (`ImportFlowRegressionTest`), pero la validación visual de los nuevos escenarios en el dispositivo no se ha realizado | revisión manual de los flujos de importación con fallos parciales en dispositivo |
| Supabase acceptance (JDBC) | Requiere base de datos | `:supabase:acceptance:test` con `DATABASE_URL` |
| Sincronización real contra Supabase (subida/bajada) | Requiere backend y cuenta reales | prueba de aceptación contra un proyecto Supabase |
| Prompt biométrico en dispositivo | Código implementado (`BiometricPrompt` con host `FragmentActivity` en Android, `LocalAuthentication`/`LAContext` en iOS); el mapeo de errores y la invariante fail-closed están probados en JVM, pero el prompt sobre hardware real no puede ejecutarse aquí | abrir la app con PIN configurado en dispositivo con biometría inscrita: éxito desbloquea; fallo/cancelación mantiene el bloqueo con el PIN visible |
| Runtime del selector PDF de iOS (`UIDocumentPickerViewController`) | Implementado y compilado contra bindings reales; el contrato del sistema requiere simulador/dispositivo | elegir un PDF en «Elegir archivo (PDF)» en S02 → llega al diálogo «Comprobante recibido» |
| NavigationRail en tablet/plegable | El layout se verifica por test (w720dp, `LayoutReferenceTest`); falta el render en hardware real | render real en tablet/plegable |
| Revisión visual de las galerías en dispositivo/emulador | Las capturas se producen y validan en JVM (Roborazzi + gate), pero la revisión estética en hardware real sigue siendo manual; el render del overlay de cámara en JVM no incluye preview (requiere hardware) | abrir `feature/destinations/presentation/build/roborazzi/index.html` y `build/captures/gallery/index.html` en una revisión sobre dispositivo |

## 3. Límites conocidos (decisiones, no defectos)

- Gracia de bloqueo de 120 s (`ExternalFlowGraceMillis`, D1): si el sistema destruye el proceso fuera de la gracia, el borrador en memoria se pierde (el grafo no se elevó por encima del PIN).
- `FLAG_SECURE` no implementado (ADR-0009 propuesto): las pantallas sensibles admiten capturas; implementarlo bloquea screenshots del QR.
- Paginación local de 50 en 50 (U-22): se conserva sin cambios; reportada como opcional.

- Divisor decorativo (`#EEEEEE` claro / `#2A2A2A` oscuro) y borde de superficie (`#E2E4E9` / `#333333`): tokens de la capa de color; válidos como separadores, y los límites funcionales siguen ≥ 3:1 (contrato §4.4).
- Cuerpo a 14 sp (escala del contrato §5; mínimo absoluto 11 sp): a 320 dp y 200 % de fuente los desbordamientos se resuelven con layout, nunca bajando la escala; la suite de layout lo verifica.

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

- **Revisión de composición por pantalla según UX/UI y TDesign:**
  COMPLETA para las superficies revisadas (V1.15–V1.21);
  la trazabilidad es ejecutable y las 7 exclusiones tienen motivo. Cambios
  de composición adoptados en S01/S00/S02/S04; el resto quedó reconciliado
  con matriz y motivos registrados en la spec. **Decisiones de producto
  pendientes** (no son deuda de migración): teclado propio para el importe
  (S07), fila de filtros en S04, balance neto mensual y encabezados por día
  (S07-list), "Destino QR vinculado" en el detalle de actividad (el vínculo
  `Operation.destinationId` existe; falta plumbing de estado), "Filtros" en
  S12 y "Crear nuevo contexto" visible con lista llena (hoy lo impide
  ADR-0003 U3), y copy propio para el vacío de la bandeja de comprobantes
  sin asociar (hoy es byte-idéntico al vacío de Actividades — hallazgo del
  gate de capturas).
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

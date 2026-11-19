# Agenda QR — Estado de implementación

> La historia de cambios vive en `git log`, no aquí. Este documento describe el
> estado ACTUAL: capacidades, límites conocidos y bloqueos. Solo se declara
> «verificado» lo que una puerta ejecutada en este entorno respalda; lo que no
> puede ejecutarse se registra como **NO VERIFICADO**, nunca como PASS.

Las puertas, sus comandos y la validación por plataforma están en
[QUALITY.md](QUALITY.md).

> Las filas visuales anteriores de §1 describen la implementación previa a
> ADR-0025. La autoridad visual vigente es ADR-0025 y el contrato normativo v8.3:
> `core/ui` ya usa tokens semánticos Polaris 9.4.2, Inter 4.0 (OFL 1.1),
> claro/oscuro, pares AA y previews a 200 %. La migración está verificada en
> Android/JVM por el gate de arquitectura y por `:core:ui:testDebugUnitTest`
> (81 pruebas). El render en iOS, incluido el empaquetado de Inter y los avisos
> de licencia, permanece **NO VERIFICADO** en este entorno (sin macOS/Xcode).

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
| Chrome e insets (TopBar, NavigationBar/Rail, Scaffold con contentWindowInsets) | Robolectric suite + gate; la variante tablet (`NavigationRail`) ya no se verifica automáticamente (ADR-0007) |
| Edge-to-edge Android (`enableEdgeToEdge`) | ensamblado |
| Predictive back (PredictiveBackHandler con progreso) | compilación 3 targets |
| Búsqueda global (S04/S05) | Robolectric suite |
| Importación QR (Photo Picker Android/PHPicker iOS, cámara, share Android, PDF: OpenDocument Android/UIDocumentPicker iOS —runtime iOS en §2) | unit + compilación de instrumentados |
| Importación con corrección de fallos parciales y reintentos (bug fix: todos los candidatos fallaban → estado Saved en lugar de Error) | `ImportFlowRegressionTest` (error all-failed, partial save, retry after error, after successful retry, multiple issues, small viewport, login error) |
| Acciones de las tarjetas de revisión de importación separadas visualmente | `ActionHierarchyTest.duplicate_card_offers_view_existing_as_main_and_saves_anyway_as_secondary` verifica que «Guardar de todos modos» y «Descartar» no colisionen |
| Jerarquía de acciones de 4 niveles documentada en `Am*` (primaria/secundaria/terciaria/destructiva) y aplicada en los flujos | javadoc de `AqButtons.kt` + `ActionHierarchyTest` (confirmación de descarte, jerarquía por tipo de incidencia, sin acciones incompatibles con el estado) + gate del design system |
| Descarte de pendientes SIN clasificar con confirmación (pérdida de contenido retenido); duplicados sin confirmación (el original existe) | `ActionHierarchyTest.unknown_card_offers_retry_as_main_action_and_discard_with_confirmation` + `.duplicate_card_offers_view_existing_as_main_and_saves_anyway_as_secondary` |
| «Quitar contexto» como acción destructiva explícita (peligro + glifo) dentro del selector; «Cancelar» única salida del diálogo | `ContextPickerDialogTest` + gate |
| Registro de actividad (S07) | Robolectric suite |
| Permisos de cámara (política de ajustes) | unit + compilación de instrumentados |
| Navegación (back stack restaurable + NavigationBar 4 destinos) | commonTest + `RootNavigationChromeTest` |
| Accesibilidad (contraste, foco visible, háptica, anuncios, targets 48dp) | `core:ui` tests + Robolectric |
| Iconografía Polaris convertida a vectores Compose (34 glifos fuente) | compilación Android/iOS + gate |
| Tokens de color Warning e Info añadidos a AqColor | gate del design system |
| Feedback transitorio (Snackbar) y persistente (AppStatusHost) | `RootNavigationChromeTest` + `AppStatusHostTest` |
| Reformas visuales por pantalla (brechas G1–G9: botón primario fijo al pie, tarjetas de ajustes con encabezado de sección, insignia de estado con texto, evidencia decodificada en tarjeta, metadatos de fecha y marca de tipo en resultados) | gate del design system + Robolectric de presentación + verificación de layout (`LayoutReferenceTest`) |
| S01 Inicio ajustado a la composición UX/UI vigente (spec V1.15): navbar «Inicio» como el resto de raíces y filtros Todos/Favoritos como control segmentado con conteo y semántica de selección | Robolectric de presentación (`DestinationsScreenA11yTest`: etiqueta estable + `selected` + rol radio) + verificación de layout (`LayoutReferenceTest` s01 claro/oscuro) |
| S00 acceso local ajustado a la composición UX/UI vigente (spec V1.16/V1.18): desbloqueo y onboarding (crear → confirmar, dos pasos) con héroe, puntos enmascarados y teclado propio 3×4 con borrado accesible; envío explícito (PIN 4–12), biometría y demo como acciones de texto | `:core:ui` (`AqAccessibilityContractTest`: teclas con rol y ≥48 dp, borrado con nombre accesible) + Robolectric (`LocalAccessGateTest`, `AgendaQrLocalStartupTest` con el flujo completo por teclado, `LocalAccessBiometricTest`) |
| S04 Buscar con conteos reales por tipo (spec V1.19): encabezados de grupo con conteo real. S06 y S08 reconciliadas sin cambios (ya responden a la composición UX/UI verificable; las capacidades no respaldadas se mantienen fuera: datos/agregados inexistentes, decisiones ADR-0003 y reclamos falsos de cifrado) | Robolectric de presentación + contratos S04/S06/S08/S09 con componentes reales (sin `AqTonalTag`/`AqPrimaryButton`/`AqSearchField`/`AqSectionHeader`/`AqDialogList` declarados sin uso) |
| S07/S07-list/S12 y los detalles (destinationdetail/operationdetail) reconciliados con la especificación UX/UI vigente (spec V1.21) sin cambios de composición: ya responden a los diseños en lo verificable (segmentado, FAB, encabezado de mes, tags, hoja de opciones, contenido expandible/copiable, estadísticas del lote); los contratos de estas 5 superficies no tenían deriva | Robolectric de presentación (445 pruebas) + `verifyScreenContract` (19 superficies) |
| Galería estricta por superficie (ADR-0007): 62 capturas — las 19 superficies y sus estados declarados a 360 dp claro/oscuro, con nombres `<superficie>--<estado>--<tema>.png` (el ancho único no se repite en el nombre), sin variantes de ancho, tablet ni fuente 200 %; overlays (S08/S03) capturados como su ventana de diálogo; manifiesto `agendaqr.captures/6` con dimensiones reales y test de origen | `./gradlew captureGallery -Pagendaqr.capture=true` produce la galería única + manifiesto e índice; `verifyScreenContract` valida esquema de nombres sin ancho, dimensiones reales, duplicados y cobertura (62 capturas verificadas) |
| S02 Añadir ajustado a la composición UX/UI vigente (spec V1.17): héroe centrado y grupo único de métodos en orden de jerarquía declarada (Galería → Cámara → Archivo), divisores correctos vía parámetro en los controles de importación | Robolectric de presentación (`ScreensA11yCoverageTest.s02_action_order_follows_declared_hierarchy`, `S02UiPolishTest`) + compilación de los tres targets (controles expect/actual) |
| Dirección visual anterior (registro histórico en ADR-0023; estado previo a ADR-0025): tokens, componentes, acciones de Inicio, escala tipográfica, elevación, movimiento y pulsación | gate del design system + Robolectric de presentación + verificación de layout (escenarios a 360 dp, escala de fuente 100 %/150 % y estados de botones); revisión en dispositivo real pendiente y declarada como no verificada |
| Contrato de interacción por pantalla (19 superficies: ruta real, destinos válidos, estados, gestos, componentes, pruebas con señal de accesibilidad y escenarios de captura) | `verifyScreenContract` + `verifyScreenContractFixtures` (dentro de `verifyAgendaQrArchitecture`): cruza el contrato con `AppRoute`/rutas de flujo, archivos, símbolos de `core/ui`, claves de `AppStrings`, pruebas y el documento generado |
| Pulsación anterior en botones, celdas, tarjetas y acciones de diálogo (color activo, sin ripple; estado previo a ADR-0025) | `:core:ui:testDebugUnitTest`; desviación D-11 para el resto de superficies |
| Sistema visual vigente Polaris 9.4.2 en `core/ui`: tokens semánticos claros/oscuros, Inter 4.0 empaquetado, pares AA, acentos X-06, movimiento, previews claro/oscuro/200 % y avisos de licencia | `verifyAgendaQrArchitecture` + `:core:ui:testDebugUnitTest` (121 pruebas) + `:androidApp:assembleDebug` con `-PallowDebugSigningForRc=true`; iOS **NO VERIFICADO** |
| Capa aditiva de componentes: layout, enlaces/grupos de acciones, formularios (`AqSelect`/`AqCheckbox`/`AqRadioButton`/`AqSwitch`/`AqMultilineTextField`), progreso, hoja modal, tabla de índices, carrusel (`AqCarousel`), panel expandible (`AqExpansionPanel`) y extensiones canónicas X-01/X-02/X-03 | `:core:ui:testDebugUnitTest` (121 pruebas, incluido `AqPhase3ContractTest`, `AqMultilineTextFieldTest`, `AqExpansionPanelTest`, `AqCarouselTest`, `AqFilterChipTest`, `AqSegmentedControlTest`, `AqSearchFieldTest`, `AqListRowTest`) + `verifyAgendaQrArchitecture`; render iOS y TalkBack/VoiceOver **NO VERIFICADOS** |
| Pantallas: X-02 delegado a `AqSyncIndicator` desde `ElementSyncBadge` y X-01 aplicado en el detalle de operación con `AqEvidenceBadge`; X-03 permanece en `AqQrPreview` + efectos de brillo por plataforma | `:feature:destinations:presentation:testDebugUnitTest` (375 pruebas, incluido `AqExtensionAdoptionTest`) + `verifyAgendaQrArchitecture`; iOS **NO VERIFICADO** |
| Limpieza de literales visuales residuales: alfas, proporciones de interlineado/composición y geometría de transición centralizados en `AqTokens.kt` (D-08), sin cambios de valor ni de comportamiento | `:core:ui:testDebugUnitTest` (121 pruebas, con `AqTokenScaleTest`/`AqSurfaceMotionTest` ampliados) + `:feature:destinations:presentation:testDebugUnitTest` (375 pruebas) + gate exacto en verde; iOS y capturas **NO VERIFICADOS** |
| Galería del sistema de diseño (`AqDesignSystemGallery` con 10 familias: Foundations, Actions, Forms, Navigation, Surfaces, Data Display, Feedback, Selection, Media, Mobile Interactions; variantes por componente: normal, seleccionado, deshabilitado, error, carga, contenido corto/largo, claro/oscuro) | `:core:ui:testDebugUnitTest` (121 pruebas) + `:feature:destinations:presentation:testDebugUnitTest` (375 pruebas) en verde; `verifyDocs: PASS (46 documents)`; iOS y revisión estética en dispositivo **NO VERIFICADOS** |
| Limpieza de componentes muertos y duplicados de `core/ui`: retirados `AqModalSheet`, `AqQrSheet`, `AqHeroHeader`, `AqDivider`, `AqVerticalDivider` (sin uso en producción ni pruebas; duplicaban `AqBottomSheet`/`AqQrPreview`/`AqHairline` o estaban sin uso) | `:core:ui:compileDebugKotlin`/`:core:ui:compileReleaseKotlin` + `verifyAgendaQrArchitecture` + `verifyDocs` en verde |
| Estados deshabilitados completados: `AqSearchField` y `AqSegmentedControl` aceptan `enabled` y resuelven colores/target de interacción deshabilitado | `:core:ui:testDebugUnitTest` (121 pruebas) + `verifyAgendaQrArchitecture` en verde |
| Estados seleccionados distinguibles en `core/ui`: `AqSegmentedControl` (opción activa con fondo `SelectionSurface` + borde `SelectionBorder`), `AqFilterChip` (borde de acento cuando seleccionado) y `AqListRow` (fondo `SelectionSurface` + borde `SelectionBorder` cuando selected). Nunca solo color de texto, válido en claro y oscuro. | `:core:ui:testDebugUnitTest` + `verifyAgendaQrArchitecture` en verde |
| `DestinationEditorScreen` (S02/S08): se retiró `AqListBody` envolviendo los campos (eliminada superficie duplicada sobre cada `AqTextField`); el botón de contexto se sustituyó por `AqListRow` seleccionable con chevron y semántica de selección; espaciado entre grupos ajustado al paso canónico 16 dp (`AqSpacing.Lg`); acción fija `Guardar` conservada. | `:feature:destinations:presentation:testDebugUnitTest` + `LayoutReferenceTest` + capturas regeneradas |
| `OperationEditorScreen` (S07): se retiraron las `AqCard` envolventes de Importe/moneda y Concepto/Nota (superficies redundantes sobre los campos); se eliminó `AqListBody(contentPadding=...)` y el padding correctivo de los chips Hoy/Ayer en Fecha y hora; la fila "Más detalles" se desligó de `AqListBody`. Selector Pago/Cobro y chips de moneda heredan el estado seleccionado con fondo + borde corregido en `core/ui`. Reglas de negocio, precisión de importe, moneda por defecto, zona horaria, persistencia y comprobantes sin cambios. | `:feature:destinations:presentation:testDebugUnitTest` + `LayoutReferenceTest` (anatomía del editor: separación de secciones 16 dp, campo de importe ≥ 48 dp, sin nodo `aq-card`) + capturas regeneradas |

## 2. Implementado pero NO VERIFICADO en este entorno

No se declaran PASS. Requieren Xcode, dispositivo físico/red o ejecución local de las verificaciones de layout.

| Capacidad | Motivo | Cómo se verifica |
|---|---|---|
| Runtime iOS (arranque y render) | Requiere Xcode/simulador | [OPERATIONS.md](OPERATIONS.md) (checklist iOS) |
| Sistema visual vigente en iOS (tokens Polaris, Inter 4.0 y avisos de licencia) | Sin macOS/Xcode: `core/ui` compila en common, pero el render y el empaquetado de recursos en el bundle iOS no se han ejecutado | ejecutar la app iOS en simulador y revisar claro/oscuro y fuente al 150 % |
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
| NavigationRail en tablet/plegable | La capacidad adaptativa sigue en producción, pero ADR-0007 retiró su verificación de layout (w720dp, `LayoutReferenceTest`); falta el render en hardware real | render real en tablet/plegable |
| Revisión visual de la galería en dispositivo/emulador | La única galería se produce y valida en JVM (Roborazzi + gate), pero la revisión estética en hardware real sigue siendo manual; el render del overlay de cámara en JVM no incluye preview (requiere hardware) | abrir `feature/destinations/presentation/build/captures/gallery/index.html` en una revisión sobre dispositivo |

## 3. Límites conocidos (decisiones, no defectos)

- Gracia de bloqueo de 120 s (`ExternalFlowGraceMillis`, D1): si el sistema destruye el proceso fuera de la gracia, el borrador en memoria se pierde (el grafo no se elevó por encima del PIN).
- `FLAG_SECURE` no implementado (ADR-0009 propuesto): las pantallas sensibles admiten capturas; implementarlo bloquea screenshots del QR.
- Paginación local de 50 en 50 (U-22): se conserva sin cambios; reportada como opcional.

- Divisor decorativo (`#EEEEEE` claro / `#2A2A2A` oscuro) y borde de superficie (`#E2E4E9` / `#333333`): tokens de la capa de color; válidos como separadores, y los límites funcionales siguen ≥ 3:1 (contrato §4.4).
- Cuerpo a 14 sp (escala del contrato §5; mínimo absoluto 11 sp): a 360 dp y 150 % de fuente los desbordamientos se resuelven con layout, nunca bajando la escala; la suite de layout lo verifica. Al 200 % la capacidad se conserva, pero quedó fuera de la verificación automática (ADR-0007) y requiere revisión manual.

- Glifos «Editar», «Compartir» y «Mostrar QR» sin icono asignado (contrato
  normativo §7): esas acciones de barra superior van como acción de texto (con su
  descripción accesible) hasta una decisión de iconografía.
- El contraste NO textual de algunos acentos de contexto contra el fondo del tema
  es < 3:1 (contrato §4.2): se aceptan como identidad de dato (encabezados y
  punto de fila) siempre con texto legible.
- Autofill de credenciales: `KeyboardOptions.contentType` no existe en Compose
  1.8.2 (llega con 1.9); el autofill funciona por `keyboardType` Email/Password
  (iOS lo mapea a `textContentType`).
- Riesgo legal de la reimplementación visual: los recursos de diseño de
  Polaris tienen una licencia restrictiva. El propietario aceptó el riesgo
  documentado; solo se reimplementan tokens y patrones en Kotlin/Compose, sin
  runtime, nombre, logo ni marca en la app, la UI ni la documentación de
  producto. Las referencias técnicas quedan confinadas a documentación de
  ingeniería.

## 4. Pendientes conocidos

- **Revisión de composición por pantalla según UX/UI y el contrato normativo:**
  implementada para S01/S00/S02/S04 y validada con pruebas de presentación,
  accesibilidad y layout. El resto de superficies conserva la composición
  especificada; las capacidades no respaldadas siguen fuera del alcance.
  **Decisiones de producto
  pendientes** (no son deuda de migración): teclado propio para el importe
  (S07), fila de filtros en S04, balance neto mensual y encabezados por día
  (S7-list), "Destino QR vinculado" en el detalle de actividad (el vínculo
  `Operation.destinationId` existe; falta plumbing de estado), "Filtros" en
  S12 y "Crear nuevo contexto" visible con lista llena (hoy lo impide
  ADR-0003 U3), y copy propio para el vacío de la bandeja de comprobantes
  sin asociar (hoy es byte-idéntico al vacío de Actividades — hallazgo del
  gate de capturas).
- **Consistencia visual Polaris Mobile (rama feat/polaris-mobile-consi
stency-phase2, PR #159):** aplicada transversalmente a componentes
  compartidos de `core/ui` y todos sus consumidores:
  - Teclado PIN: teclas con `SurfaceContainerHighest` + borde hairline en
    tema oscuro (E-08).
  - Bordes de selección: `Border` neutro en lugar de `SelectionBorder`
    intenso en `AqFilterChip`, `AqSegmentedControl`, `AqListRow`.
  - Banner de estado: icono de descarte más pequeño que el indicador de
    severidad.
  - Icono de inicio en campos: `IconSizeCompact` (20 dp) para alineación
    con el texto corporal.
  - Tarjetas de acción rápida: se ajustan al contenido (sin altura mínima
    impuesta que genere espacio vacío).
  - Comprobantes sin asociar: `titleMaxLines=2`, acción de texto en línea
    separada y miniatura real para comprobantes de imagen (icono de tipo de
    archivo para PDF / fallback).
  - Gates y pruebas en verde: `verifyAgendaQrArchitecture`, `verifyDocs`,
    `:core:ui:testDebugUnitTest`, `:feature:destinations:presentation:testDebugUnitTest`,
    `:androidApp:assembleDebug`.
- **Corrección transversal de alineación, composición y Polaris Mobile (rama
  `feat/polaris-mobile-alignment-consistency`):** causas compartidas corregidas
  en `core/ui` y propagadas a todos los consumidores:
  - Alineación vertical de campos: símbolo de moneda de `AqAmountField`
    centrado con el importe (no anclado al fondo).
  - Divisores: márgenes simétricos en `AqListRow.showDivider` y el divisor no
    se renderiza en filas seleccionadas (la selección es una unidad visual).
  - Tabs: `AqTabs` usa indicador lineal inferior que recorre el ancho de la
    pestaña activa (ya no una píldora de 16 dp).
  - Tiles: `AqQuickAction` se ajusta al contenido sin altura impuesta
    excesiva.
  - Feedback: banner de estado diferencia icono de severidad y acción de
    descarte (distinto peso visual).
  - Comprobantes: `ComprobanteThumbnail` renderiza miniatura real para
    imágenes (JPG/PNG), representación de documento para PDF e icono de tipo
    de archivo para el resto.
  - Pruebas: `AqAtomicSpacingContractTest` cubre divisores, filas
    seleccionadas, alineación de importe y tarjetas rápidas.
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

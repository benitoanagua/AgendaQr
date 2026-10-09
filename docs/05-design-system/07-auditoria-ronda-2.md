# Auditoría ronda 2 — triage y plan (2026-10-09)

Alcance: SOLO teléfonos Android/iOS (el laboratorio wasm no es objetivo).
Método: cada hipótesis del brief se verificó contra el código antes de
clasificarla. Estados: **CERRADO** (ya resuelto), **ABIERTO** (confirmado,
se corrige), **NUEVO** (descubierto en esta ronda), **DISPOSITIVO**
(requiere físico/Xcode), **DECISIÓN** (requiere producto).

## Triage inicial

| ID | Área | Evidencia (archivo:línea / grep) | Estado | Severidad | Plan |
|---|---|---|---|---|---|
| A1 | No regresión ronda 1 | Suite completa + gates PASS en main `9387517` (verificación inicial de esta ronda) | CERRADO | — | — |
| A2 | Barrido de pares reales | `brandText` sobre contenedor **Danger** (acciones "REINTENTAR/Descartar" del banner de error): **4,47 claro / 3,45 oscuro** — bajo 4,5:1. Pares sin test: Neutral, textSecondary/textTertiary vs bg/surface2, danger vs bg | NUEVO | Alta | Acciones de banner/toast heredan `tone.content()` (par ya garantizado por test); tests de los pares faltantes |
| A3 | Componentes ronda 1 vs gate/catálogo | `tone.iconVector`, favorito con glifo, `XauxaSearchTrigger`: gate PASS, `LabCatalogIntegrityTest`/freeze PASS | CERRADO | — | — |
| B1 | Permiso cámara "no preguntar más" | CORREGIDO: `cameraPermissionAction` pura + `rememberSaveable` de lo pedido antes; denegación permanente → banner con "Abrir ajustes" (`ACTION_APPLICATION_DETAILS_SETTINGS`). iOS: fallo de input de cámara → banner con ajustes vía `UIApplicationOpenSettingsURLString` (constante que expone el binding) | CERRADO | Alta | — |
| B2 | Contexto previo al permiso | CORREGIDO: "Usamos la cámara solo para escanear códigos QR." visible en la entrada de cámara (Android+iOS), test `camera_entry_shows_permission_context_before_asking` | CERRADO | Media | — |
| B3 | Feedback al leer QR | CORREGIDO: `XauxaFeedbackEvent` (DS) con háptica + liveRegion por evento; en Android el hint pasa a "Código QR detectado" (estado visible+legible); iOS anuncia desde la entrada. TalkBack/VoiceOver → checklist | CERRADO | Media | — |
| B4 | Ciclo de vida cámara | `CameraQrCapture.android.kt:78,99` usa `bindToLifecycle` (CameraX libera en background/llamada); iOS para la sesión al descartar | CERRADO | — | — |
| B5 | Alternativa sin cámara | S02 muestra Galería (entrada principal) junto al banner; banner "Concede el permiso o usa Galería" | CERRADO | — | — |
| B6 | Linterna | Fuera del mapeo de glifos aprobados (spec §12) | DECISIÓN | Baja | Propuesta documentada; no se implementa |
| B7 | Bindings KMP de autorización AVFoundation | Sondas de compilación: `authorizationStatusForMediaType`/`requestAccessForMediaType`/`authorizationStatus`/`requestAccess` (directos, Companion, con cast) NO existen en el cinterop precompilado; sí `AVAuthorizationStatus*` y `UIApplicationOpenSettingsURLString` | NUEVO | Media | Fallback fail-closed visible (input fallido → banner ajustes); pedido explícito del permiso queda en checklist de Xcode (puede requerir wrapper nativo) |
| C1 | (RESUELTO) Back stack no restaurable | `AuthenticatedAppRoot.kt:45` `remember { AppBackStack() }`; manifest sin `screenOrientation`/`configChanges` → rotación recrea y pierde navegación | ABIERTO | Alta | `rememberSaveable` + `Saver` (AppRoute son `data object` sin parámetros → serialización por nombre); tests guardar/restaurar |
| C2 | (RESUELTO) Formularios con `remember` | `submitted` en AuthScreen:50, ContextPickerDialog:87,192; `searchReturnDestinationId` AuthenticatedAppRoot:65; editores ya usan `rememberSaveable` (verificado) | ABIERTO | Media | `rememberSaveable` en lo que el usuario no debe perder |
| C3 | (RESUELTO) Política de orientación | Manifest sin política; teléfono con rotación sin probar | ABIERTO | Media | ADR corto (recomendado: retrato en teléfonos) + manifest coherente; checklist manual para iOS |
| D1 | (RESUELTO) Tile de altura fija | `XauxaMetro.kt:153` `.height(height)` fija (76/160dp); etiqueta sin `maxLines`/`overflow` → fuente grande recorta | ABIERTO | Alta | `heightIn(min=…)` + `maxLines=2` + `overflow=Ellipsis` (nombre accesible intacto); test fontScale |
| D2 | (RESUELTO) Resto de textos con fuente grande | Sin pruebas `fontScale` en el repo (grep 0); AppBar etiquetas 1 línea (`maxLines=1`); ListRow/Badge sin verificación de solape | ABIERTO | Alta | Tests de layout a fontScale 100/150/200 % en 320/360dp con assertions de recorte/target |
| D3 | (RESUELTO) Capturas de referencia | Sin Roborazzi/Paparazzi; repositorios sin binarios de imagen desde 2026-10-09 (decisión del propietario) | ABIERTO | Alta | ADR Roborazzi con **hash manifest** (SHA-256 en JSON de texto): captures locales, sin PNG en git, coherente con la decisión de no trackear imágenes |
| E1 | (RESUELTO) `collectAsState` sin ciclo de vida | 8 sitios (4 en la raíz: `AuthenticatedAppRoot.kt:46,49-52,56-57`, 1 en `AgendaQrApp.kt:33`); 0 `collectAsStateWithLifecycle`; catálogo sin `lifecycle-runtime-compose` | ABIERTO | Media | Añadir `lifecycle-runtime-compose` (ADR corto, KMP) y migrar; medir skippability antes/después |
| E2 | (RESUELTO-parcial) Recomposición de raíz | Raíz recolecta 6 flows que solo usan pantallas concretas | ABIERTO | Media | Recolectar en la pantalla consumidora donde no cambie comportamiento; `derivedStateOf` solo con cálculo real |
| E3 | Listas sin key / scroll largo | `LazyColumn` de S04 usa `key`; importación usa scroll con conteo acotado (verificado) | CERRADO | — | — |
| F1 | (RESUELTO) Háptica y anuncios | 0 usos de `LocalHapticFeedback`; guardado/sync/eliminar/QR sin anuncio accesible propio | ABIERTO | Media | `XauxaFeedback` (DS): háptica ligera + `liveRegion` por EVENTO (no por estado); copy de AppStrings; tests de semántica |
| G1 | Predictive back | `enableOnBackInvokedCallback=true` ya en manifest; `BackHandler` + turnstile en su sitio | DISPOSITIVO | Media | Checklist manual Android 14+ con pasos/criterio |
| H1 | (RESUELTO) Voz mixta | "Inicia sesión/Ingresa" (tuteo) vs "Toque para seleccionar un archivo/Encuadre el código QR" (usted) en AppStrings/core-ui | ABIERTO | Media | ADR guía de voz (tuteo) + unificación + registro de decisión de copy |
| H2 | (RESUELTO) Literales en core/ui | `"Limpiar"` (XauxaInputs), `"Cargando archivo…"`, `"Escaneando…"`, `"PNG, JPG o PDF · máx. 10 MB"`, `"Toque para seleccionar un archivo"`, `"Encuadre el código QR"` (XauxaFeedback), `"Más opciones"` (CommandBar) | ABIERTO | Media | Parametrizar (copy del llamador) + regla de gate con fixtures |
| I1 | (DECIDIDO) Tokens sin consumidor de producción | `BreakpointCompact/Medium`, `ContentMaxWidth`: solo el laboratorio (que sí compila contra ellos) | ABIERTO | Media | Mantener (el lab los consume) y marcar "lab-only: retirar con el lab" en el audit register (opción conservadora: no romper el lab) |
| I2 | (RESUELTO) `XauxaScreenColumn` con Xxl | `XauxaScreenScaffold.kt:33` `.padding(XauxaSpacing.Xxl)` frente a `ScreenMargin` unificado en Fase 4 | ABIERTO | Media | Unificar a `ScreenMargin` |
| I3 | (DECIDIDO) Componentes sin uso | `XauxaErrorPage`, `XauxaToast`, `XauxaInlineResult`, `XauxaSettingRow`, `XauxaIconButton`, `XauxaFavoriteToggle`, `XauxaDangerButton` (solo vía diálogo) | ABIERTO | Media | Registro de decisión por componente en `04-component-audit-register.md` (adoptar/retirar); sin borrados |
| I4 | (SLOT LISTO) Mostrar/ocultar contraseña | Glifo Eye fuera del mapeo aprobado | DECISIÓN | Media | Slot trailing en `XauxaTextInput` con test; decisión de glifo documentada |
| J1 | FLAG_SECURE / privacidad | Sin implementación en comprobantes; coste de soporte sin evaluar | DECISIÓN | Baja | ADR "propuesto" con pantallas, implementación Android/iOS, coste e interruptor |
| K1 | (PARCIAL) Paridad iOS | Sin petición de permiso de cámara en `IosCameraQrCapture.ios.kt` (denegado → dismiss silencioso); sin areas seguras revisadas en el lab iOS; comprobante pendiente de preview real | ABIERTO | Media | Corregir permiso (compile-level) + settings URL; checklist Xcode para el runtime |
| L1 | Barrido libre | Ejecutado: TODOs en código = prosa española (0 reales); 0 clickables sin contentDescription/rol en feature; 0 touch targets <48dp (sizes pequeños son spinners/pastillas dentro de controles de 48dp); banners con acción donde la spec la pide | CERRADO | — | — |
| L2 | Bindings iOS de autorización AVFoundation | Ver B7: fallback fail-closed + checklist Xcode | DISPOSITIVO | Media | — |
| L3 | GraphicsMode Roborazzi | Sin @GraphicsMode(NATIVE) las capturas salían negras e idénticas entre temas; anotación obligatoria documentada en ADR-0007 y el test | NUEVO→CERRADO | Alta | — |

## Área A — resultado del barrido (ronda 2)

Suite de no-regresión PASS (pares de tonos ambos temas, `asTextOn`,
`tileStaggerDelayMs`, `AuditStatesFormsTest` + suite completa de módulos).
Hallazgo A2 corregido: las acciones de banner/toast/inline heredan el color
de contenido del tono (pares garantizados ≥4,5:1 por test) en lugar de
`brandText` sobre el contenedor Danger. Tests nuevos:
`neutral_tone_and_ambient_text_pairs_meet_wcag_aa` y
`banner_actions_inherit_a_tested_tone_pair`.

Este documento se actualiza al cierre de cada área con el estado final.


## Cierre (2026-10-09)

Estados finales: C1–C3, D1–D3, E1, F1, H1–H2, I2, L1/L3 → **CERRADO**.
E2 → **RESUELTO-PARCIAL** (lifecycle-aware + derivedStateOf; sin
@Stable sin evidencia; medición de skippability en checklist). I1/I3 →
**DECIDIDO** (registro en 04-component-audit-register.md). I4 → **SLOT
LISTO** (trailing con test; glifo pendiente de decisión). G1, B7, K1
(runtime iOS) → **DISPOSITIVO** (checklist
`docs/08-validacion/07-checklist-manual-ronda2.md`). B6, J1 →
**DECISIÓN** (linterna sin glifo aprobado; ADR-0009 propuesto).

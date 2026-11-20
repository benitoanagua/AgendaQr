> **HISTÓRICO — no normativo.** Este documento fue consolidado en [11-matriz-gaps-mobile.md](../11-matriz-gaps-mobile.md) y [10-auditoria-fidelidad.md](../10-auditoria-fidelidad.md). No es fuente normativa; se conserva solo como registro histórico. La autoridad vigente es el contrato normativo v9.0 y ADR-0029.


# Auditoría inicial de `core/ui`, documentación y valores visuales

> **Documento histórico.** Refleja el estado previo a ADR-0025 y ADR-0026.
> Las denominaciones de sistemas visuales y las API `Am*` que aparecen en él
> son citas del estado auditado, no nombres vigentes. La autoridad actual es
> ADR-0025, ADR-0026 y `05-design-system/00-contrato-normativo-diseno.md` v8.0;
> el estado final se registra en
> [`10-auditoria-fidelidad-polaris.md`](10-auditoria-fidelidad-polaris.md).

**Tipo:** informe de auditoría de solo lectura (con una excepción autorizada, §10).
**Fecha:** 2026-11-17.
**Alcance:** `androidApp`, `iosApp`, `shared`, `core/ui`, `feature/destinations`,
`build-logic`, `docs/` y configuración Gradle. No se ha modificado lógica de
dominio ni de sincronización.

## 1. Resumen ejecutivo

- El sistema visual vigente es **TDesign Mobile `tdesign_flutter@0.2.7`**
  (ADR-0023), no Polaris. No existe ni una sola mención a «Polaris» ni a
  «Shopify» en todo el repositorio, en código ni en documentación.
- `core/ui` centraliza el estilo con una capa primitiva interna (`AqPalette`)
  y una capa semántica pública (`AqColor`, `AqSpacing`, `AqType`, `AqMotion`,
  `AqElevation`, `AqMetrics`). Las pantallas no importan Material 3.
- Los valores visuales hardcodeados en Kotlin de producción fuera de la capa
  de tokens son **limitados**: predominan `FontWeight.*`, alfas/ratios
  adimensionales y dos constantes de predictive back. El grueso de los
  literales está en los recursos del launcher Android y en el logo SVG.
- Los restos de sistemas anteriores (Metro, Xauxa, Ant Design Mobile,
  Super-App, apps chinas, AgendaMobile, Stitch) viven **solo en documentación
  histórica**, con una excepción: el color `#0067B8` del ciclo Metro/Xauxa
  sigue en el launcher, el logo y el README de iOS.
- El gate `verifyAgendaQrArchitecture` estaba **en rojo antes de empezar** por
  una cita a una ADR inexistente en ADR-0007. Con la corrección mínima
  autorizada (§10), los tres gates están en verde.
- La fuente de valores propuesta no está libre de condiciones: el paquete de
  tokens tiene una licencia custom restrictiva (§11).

## 2. Inventario de `core/ui`

### 2.1 Archivos

| Capa | Archivos |
|---|---|
| Tokens y tema | `theme/AqTokens.kt`, `theme/AqTheme.kt`, `theme/AqWindowSize.kt` (+ `androidMain`, `iosMain`) |
| Iconografía | `icons/TDesignIconVectors.kt` |
| Movimiento | `motion/ReducedMotion.kt` (+ `androidMain`, `iosMain`) |
| Componentes (`commonMain`) | `AqButtons.kt`, `AqInputs.kt`, `AqLists.kt`, `AqText.kt`, `AqStatus.kt`, `AqFeedback.kt`, `AqFeedbackEvent.kt`, `AqDialogs.kt`, `AqActionSheet.kt`, `AqTabs.kt`, `AqChrome.kt`, `AqScaffold.kt`, `AqIcon.kt`, `AqQrPreview.kt` (+ `androidMain`, `iosMain`), `AqClipboard.kt` (+ `androidMain`, `iosMain`), `AqPinEntry.kt`, `AqDateTime.kt`, `AqTone.kt` |
| Pruebas | 9 archivos en `commonTest`, 9 en `androidUnitTest` |

### 2.2 Tokens

- `AqPalette` (`internal`): escalas `lightBrand`/`darkBrand`, `lightWarning`/
  `darkWarning`, `lightError`/`darkError`, `lightSuccess`/`darkSuccess`,
  `lightGray`/`darkGray`, además de derivados de superficie, texto, borde,
  foco e información. Es la única capa que declara colores crudos.
- APIs públicas semánticas: `AqColor` (surface/text/border/action/tonos),
  `AqSpacing`, `AqType`, `AqMotion`, `AqElevation`, `AqMetrics`, `AqTone`.
- `AqTheme.kt` es el único puente autorizado a `MaterialTheme.colorScheme` y
  construye las `Shapes` desde tokens.
- `TDesignIconVectors.kt` es la única excepción permitida para viewport y color
  monocromo de vectores.

### 2.3 Componentes y consumidores

Las familias expuestas cubren acciones (`AqPrimaryButton`,
`AqSecondaryButton`, `AqTextAction`, `AqIconButton`, `AqExtendedFab`,
`AqCompactAction`, `AqQuickAction`, `AqBottomAction`), formularios
(`AqTextField`, `AqPinEntry`, `AqDateTimePicker`), contenido (`AqText`,
`AqHeading`, `AqCard`, `AqTappableCard`, `AqListBody`, `AqListRow`,
`AqMetaRow`, `AqSectionHeader`, `AqHairline`, `AqQrPreview`), feedback
(`AqStatusBanner`, `AqStatusPill`, `AqBadge`, `AqIconBadge`, `AqContextBadge`,
`AqEmptyState`, `AqSkeleton`, `AqLoading`, `AqSnackbarHost`,
`AqSuccessCheck`), overlays (`AqDialog`, `AqDialogList`, `AqActionSheet`),
navegación y chrome (`AqScreenScaffold`, `AqTopBar`, `AqTabs`,
`AqSegmentedControl`, `AqNavChromeState`, `AqNavRail`) e intensión
(`AqTone`, `AqIcons`, `AqIcon`, `rememberAmClipboard`).

Todas las pantallas de `feature/destinations/presentation` consumen estas APIs.
No hay imports de `androidx.compose.material3.` en `commonMain`; la cámara
Android es el único punto que usa Compose puro y
`androidx.compose.ui.window.Dialog`.

### 2.4 Cobertura y código muerto

`core/ui` tiene 48 archivos Kotlin fuente y 75 declaraciones `@Composable`,
ninguna de ellas con `@Preview`. Los registros de excepciones `E-01`…`E-05`
están en uso; la excepción `E-06` ya fue retirada y el gate detecta registros
zombie. Los candidatos a código muerto y las superficies sin preview se
detallan en §8.

## 3. Pantallas y componentes consumidos

| Pantalla | Archivo | Consumo principal |
|---|---|---|
| S01 Inicio | `DestinationsScreen.kt`, `HomeSurface.kt` | scaffold, top bar, búsqueda, segmentado, listas, badges, banners, vacío, skeleton, snackbar |
| Acceso / login | `AuthScreen.kt` | scaffold, botones, campo de texto, banner, actions |
| S02 Añadir | `AddDestinationScreen.kt` | scaffold, lista, iconos, snackbar |
| Importación masiva (S12) | `ImportBatchScreen.kt` | scaffold, card, QR, listas, banner, diálogo, feedback, loading |
| S09 Revisar QR | `ImportReviewScreen.kt` | scaffold, card, QR, vacío, banner |
| Mostrar QR / detalle | `DestinationDetailScreen.kt`, `AgendaQrPatterns.kt` | QR a pantalla completa, card, meta row, botones, diálogo |
| S04 Buscar | `GlobalSearchScreen.kt` | snackbar y primitivas de lista |
| S06 Contextos | `ContextScreen.kt`, `ContextPickerDialog.kt` | tabs, listas, diálogo, campo, acentos |
| S07 Actividades | `OperationListScreen.kt`, `OperationEditorScreen.kt`, `OperationDetailScreen.kt` | FAB, segmentado, top bar, listas |
| S08 Cuenta | `AccountScreen.kt` | scaffold, banner, loading, botones |
| S00 Acceso local y onboarding | `LocalLockScreen.kt`, `LocalOnboardingScreen.kt` | keypad, PIN dots, botones, iconos |
| S03 Cámara | `QrImportControls.android.kt` | controles compartidos + cámara nativa |

## 4. Valores hardcodeados fuera de tokens

### 4.1 Kotlin de producción

| Archivo | Hallazgo |
|---|---|
| `core/ui/.../components/AqText.kt` | Ratios `1.25f`/`1.5f` para interlineado; `FontWeight.Bold` por defecto. |
| `core/ui/.../components/AqButtons.kt` | `Modifier.alpha(0f)`, alfas `0.25f`/`0.2f`, `FontWeight.SemiBold`/`Medium`. |
| `core/ui/.../components/AqStatus.kt` | `FontWeight.SemiBold`; ratio `0.55f` para el tamaño del indicador. |
| `core/ui/.../components/AqChrome.kt` | `AqSurfaceSlideFraction = 8`, pesos tipográficos directos. Las duraciones sí usan `AqMotion`. |
| `core/ui/.../components/AqFeedbackEvent.kt` | `SnackbarDuration.Short` (enum, no milisegundos). |
| `feature/.../AuthenticatedAppRoot.kt` | `PredictiveBackShrink = 0.08f`, `PredictiveBackFade = 0.5f`. |
| Pantallas varias | `FontWeight.SemiBold`/`Medium` en `AuthScreen`, `ImportBatchScreen`, `OperationListScreen`, `OperationDetailScreen`, `LocalLockScreen`, `LocalOnboardingScreen`, `DestinationsScreen`, `AgendaQrPatterns`. |
| `feature/.../data/DemoQr.android.kt` | `0xFF000000` y `0xFFFFFFFF` para píxeles del QR demo. |

**No aparecen** en Kotlin de producción fuera de la capa de tokens: colores
Compose crudos (salvo el QR demo), literales `.dp` o `.sp`, `RoundedCornerShape`,
`tween(<n>)` ni fuentes empaquetadas.

### 4.2 Recursos y configuración

- `androidApp/src/main/res/drawable/ic_launcher_foreground.xml`: 20 `fillColor`
  literales (blanco, `#0067B8`, `#004A87`), `108dp`, transformaciones y 52
  radios embebidos en `pathData`.
- `androidApp/src/main/res/values/ic_launcher_background.xml`: `#0067B8`.
- `androidApp/src/main/res/values/styles.xml`: `fontFamily="sans"`, colores de
  sistema para status/navigation bar.
- `docs/branding/agendaqr-logo.svg`: `#0067B8` y `#004A87`.

### 4.3 Fuera de producción

Los tests y los generadores de galería HTML contienen literales de prueba; no
son UI de la app. El gate los excluye explícitamente.

## 5. Restos de sistemas anteriores

| Sistema | Estado en código | Estado en documentación |
|---|---|---|
| Metro | sin restos de código | ADR-0005 (reemplazada), ADR-0012 y ADR-0011 la citan como historia |
| Xauxa | sin restos de código | ADR-0011 (reemplazada); comentarios en ADR-0005/0012/0013 |
| AgendaMobile | prefijo `Am*` sigue vivo como nombre de API, no como sistema visual | ADR-0013 (suplantada); comentario obsoleto en `gradle/libs.versions.toml` e `iosApp/README.md` |
| Ant Design Mobile | sin restos de código | ADR-0014 y ADR-0017 (suplantadas); ADR-0018:33 dice «ADR-0017 vigente» (residuo) |
| Identidad AgendaQr | sin tokens activos | ADR-0020 (suplantada); README raíz todavía la cita como nivel 3 |
| Apps chinas / Super-App | sin restos de código | ADR-0021, ADR-0022 y `05-design-system/08-referencia-super-app.md` (históricos) |
| Stitch | sin restos de código | ADR-0007 conserva el campo de manifiesto histórico |
| Spectrum | **cero coincidencias** | — |

Color heredado aún visible: `#0067B8` / `#004A87` en launcher, logo e
`iosApp/README.md`, frente al acento actual `#0052D9`.

## 6. Documentos contradictorios, huérfanos o transitorios

1. **README raíz (nivel 3 de precedencia)** cita ADR-0020 y ADR-0021 como
   autoridad visual, cuando ambos están suplantados por ADR-0023. Además
   invierte el orden funcional/visual respecto a la spec UX/UI, que afirma
   que para tokens y componentes prevalece el contrato normativo.
2. **ADR-0007** citaba una ADR inexistente; corregido en §10.
3. **ADR-0018:5,33** mantiene «ADR-0017 vigente», residuo de la época Ant.
4. **ADR-0019** fija `#2B2B2B`, color que ya no pertenece a la paleta vigente.
5. **`docs/CLEANUP-SUMMARY.md`** es una instantánea transitoria de la
   consolidación anterior; debe marcarse como histórico, no normativo.
6. **`docs/branding/agendaqr-logo.svg`** no está enlazado desde `docs/README.md`
   (solo desde `iosApp/README.md` y un comentario XML); `verifyDocs` no cubre
   activos no-Markdown.
7. **`04-ux/03-contrato-interaccion-pantallas.md`** es generado; la fuente real
   es `ScreenContractGate.kt`.
8. **`05-design-system/07-evaluacion-tipografia.md`** documenta MiSans Global
   como candidata no adoptada, con resultados marcados como no verificados.

## 7. Scripts

No hay scripts `sh`/`py`/`js`/`mjs`/`cjs` propios. Únicamente los wrappers
estándar `gradlew` y `gradlew.bat`, permitidos por `verifyNoScripts`.

## 8. Código muerto y superficies sin preview en `core/ui`

Candidatos a código muerto: ninguna llamada dentro ni fuera del módulo.

| Símbolo | Ubicación |
|---|---|
| `AqLightButton` | `AqButtons.kt:388` |
| `AqOutlineButton` | `AqButtons.kt:410` |
| `AqHeroHeader` | `AqChrome.kt:417` |
| `fillPath` | `TDesignIconVectors.kt:157` (privado; los 34 vectores usan `strokePath`) |

Las variantes `Light` y `Outline` siguen presentes en la resolución de colores
y en tests. Tienen uso interno o de test, pero ningún consumo productivo
externo: `AqDangerButton`, `AqNavRail`, `AqNavBar`, `AqHeading` y
`decodeQrImageBitmap`.

Ningún composable del módulo declara `@Preview`: la verificación visual hoy
depende de tests Robolectric y del generador de galería. Es una brecha de
cobertura a cubrir durante la reimplementación.

## 9. Puertas y estado de validación

| Puerta | Estado inicial | Estado tras excepción autorizada |
|---|---|---|
| `verifyAgendaQrArchitecture` | ROJO: `missing-adr` en ADR-0007 | VERDE |
| `:androidApp:testDebugUnitTest` | — | VERDE |
| `:androidApp:assembleDebug` | — | VERDE |

Android está validado por JVM/Robolectric y ensamblado. iOS sigue sin
validación en este entorno (sin macOS/Xcode); se registra como no verificado.

## 10. Excepción autorizada a la solo-lectura

Se cambió una sola línea de `ADR-0007` para sustituir la cita a una ADR
inexistente por «campo histórico, sin ADR asociado». Motivo: el gate ya estaba
en rojo y las reglas exigen no avanzar con una puerta roja. Ningún otro archivo
preexistente fue modificado durante la auditoría.

## 11. Fuente de valores y riesgos

- La documentación web de Polaris redirige hoy a referencias de *web
  components* de Shopify; la escala de espaciado publicada confirma la escala
  «middle-out» (`small-500`…`large-500`).
- **`@shopify/polaris-tokens` no tiene versión 12**: el registro público llega
  hasta `9.4.2`. La versión 12 existe en `@shopify/polaris` (React). Debe
  fijarse `9.4.2` como fuente verificable y marcar la discrepancia como
  pendiente de verificar.
- `9.4.2` contiene capa primitiva y semántica (`--p-space-*`, `--p-color-*`,
  `--p-text-*`, `--p-motion-*`) y temas `light`, `light-mobile`,
  `light-high-contrast-experimental` y `dark-experimental`.
- **Riesgo legal**: la licencia de tokens e iconos de Polaris es una licencia
  custom basada en MIT que limita su uso a aplicaciones que integran o
  interoperan con software/servicios de Shopify y, para aplicaciones externas
  independientes, exige que sean dissimilar y visualmente distintas. AgendaQr
  es independiente, por lo que un clon literal conlleva riesgo de
  incumplimiento. Se continúa con riesgo documentado por decisión explícita
  del propietario del producto.
- **Inter** está bajo SIL Open Font License 1.1; permite empaquetarlo y
  redistribuirlo respetando el aviso de copyright y los nombres reservados.
- No se usará el nombre, el logo ni la marca de Shopify en la app, la UI ni la
  documentación de producto. Las referencias técnicas a Polaris se limitan a
  documentación de ingeniería.

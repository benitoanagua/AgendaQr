# Reglas de implementación del sistema de diseño

**Estado:** Normativo (ADR-0020). El contrato completo está en `00-contrato-normativo-diseno.md`; este documento traduce la identidad visual propia de AgendaQr a reglas de código.

## 1. Mapa de la implementación

```
core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/
├── theme/
│   ├── AmTokens.kt        # tokens privados (AmPalette) + semánticos (única capa con literales)
│   ├── AmTheme.kt         # puente claro/oscuro a Material 3 (esquema M3 derivado completo)
│   └── AmWindowSize.kt    # expect: clase de ancho de ventana (compacto/medium/expanded)
├── motion/
│   └── ReducedMotion.kt   # expect/actual: LocalReducedMotion + ProvideReducedMotion
└── components/
    ├── AmText.kt          # AmText, AmHeading (texto con interlineado propio)
    ├── AmIcon.kt          # AmIcon + AmIcons (Lucide, único punto de entrada)
    ├── AmTone.kt           # AmTone + toneColors + glifo por tono
    ├── AmButtons.kt       # botones radio 4 (solid 48; secundario = botón default con hairline E-01, compacto 40/48 con variante solid; AmCompactAction de fila), píldoras cápsula CapsuleTabs (AmFilterChip: reposo box sin borde, etiqueta 15), segmentado radio 2 sobre pista box (AmSegmentedOption), toggle de favorito
    ├── AmInputs.kt        # AmTextField (etiqueta flotante persistente + guía hairline; glifo de concepto), AmAmountField (importe 26 sp + ⌫), AmSearchField (SearchBar: box, radio 6, borde de acento en foco E-04, 48 dp mínimo, sin etiqueta flotante), AmSearchTrigger
    ├── AmLists.kt         # AmListRow (título 17, descripción 13, showChevron opt-in, slot leading tipado, divisor inset 56), AmListBody (tarjeta radio 8 + hairline E-03), AmTappableCard (tarjeta presionable radio 8), AmHairline, AmBadge/AmTonalTag (Tag radio 2 con round opt-in, 11 normal), AmIconBadge (tile 40), AmMetaRow (con acción final)
    ├── AmStatus.kt        # AmStatusBanner (radio 8), AmStatusPill (tonal/inversa, píldora rounded), AmLoading, AmEmptyState, AmSection
    ├── AmClipboard.kt     # rememberAmClipboard (expect/actual: copiar contenido al portapapeles)
    ├── AmChrome.kt        # AmTopBar, navegación raíz, FAB, transición de superficies
    ├── AmScaffold.kt      # AmScreenScaffold (contenedor de pantalla), AmBottomAction (acción principal fija: ancho completo + navigationBars + IME)
    ├── AmDialogs.kt       # AmDialog (Dialog oficial: popup radio 8, título 18 bold centrado, contenido 15, fila de acciones hairline a ancho completo), AmDialogList
    ├── AmFeedback.kt      # skeleton, paginación, scanner, marco de medios radio 8 (Surface: tarjeta distinta del fondo en ambos temas)
    ├── AmFeedbackEvent.kt # AmFeedbackHost/AmSnackbarHost (contenedor InverseSurface radio 8, texto 15)/AmFeedbackEvent
    ├── AmPivot.kt         # AmSectionHeader (List header: 15, texto secundario, peso normal), AmPivot (Tabs con indicador de acento, título 17)
    └── AmQrPreview.kt     # expect (Android decodifica; fallo de decodificación = glifo explícito sobre el marco, nunca rectángulo blanco; iOS pendiente de hardware)
```

## 2. Reglas de token

- TODO literal visual (hex, `dp`, `sp`, `Color(...)`, `Color.<nombre>`, formas con esquinas) vive en `AmTokens.kt`. El resto del repositorio consume `AmColor`/`AmSpacing`/`AmShapes`/`AmRadius`/`AmMetrics`/`AmType`/`AmTextStyles`/`AmMotion`.
- Un cambio visual es un cambio de token + pin actualizado en `AmSchemeTest` + capturas regeneradas; nunca un literal nuevo en una pantalla. Las referencias externas orientan la exploración, no determinan los valores.
- Los pares de color llegan verificados: `AmSchemeTest` fija los umbrales (4.5:1 contenido, 3:1 bordes/foco, 3:1 suelo deshabilitado); `AmContrastPairsTest` fija los bloques de identidad de contexto, el par del botón primario y el rol focus.

## 3. Reglas de componente

- Envoltorios finos de Material 3 con tokens: el componente fija forma/colores/estados que M3 no mapea (botones radio 4, buscador SearchBar con borde de acento E-04, chips estilo CapsuleTabs) y hereda `typography`/`shapes` del tema; la barra superior (56 dp) y la inferior (56 dp, sin píldora) son composición propia.
- Todo copy llega como parámetro del llamador (ADR-0008); las pantallas lo sirven desde `AppStrings`.
- `AmStatusBanner` se compone en **columna**: fila 1 icono + mensaje a ancho completo; fila 2 acciones alineadas al final con envoltura (sin fila si no hay acciones). API: `AmBannerAction(label, onClick)` para `action`/`dismiss`; imposible el estado inválido (sin `requireNotNull`).
- `AmListRow.meta` solo metadatos/badges: prohibido colocar acciones ahí.
- `AmTextAction` ofrece variantes `Default` (acento), `Neutral` (texto primario semibold) y `Danger` (destructiva); prohibido usar `TextSecondary`/`TextTertiary` como color de acción.
- `AmListBody` (padding 0) agrupa filas de lista; `AmCard` (padding `AmSpacing.Lg`) para contenido libre.
- `AmSecondaryButton` ocupa todo el ancho cuando el llamador pasa `fillMaxWidth()` (superficie visible = ancho completo).
- Regla de colocación: en tarjetas de pendientes, las acciones secundarias se alinean a la derecha (`Arrangement.End`), orden fijo [Guardar de todos modos] [Descartar], Descartar siempre a la derecha en tono peligro.
- Estados obligatorios: pressed/focused/selected/error/disabled/loading, cada uno con señal no exclusivamente cromática.
- Área táctil ≥ 48 dp (`AmMetrics.ControlMinSize`), sin alturas fijas que recorten con fuente grande.
- Iconos: solo `AmIcons`; decorativos con `contentDescription = null`; interactivos con etiqueta visible del llamador.

## 4. Reglas de pantalla

- La capa feature compone SOLO componentes del sistema (`core:ui`); importar controles M3 en `feature/`, `shared/` o `androidApp/` es una fuga del sistema y falla el gate.
- La interacción (clickable, indicación, foco, semántica de fila) vive en el sistema, no en la pantalla.
- El copy de las pantallas sale de `AppStrings` (decisión D2); los tests fijan esa centralización.
- Espaciado entre grupos (20 dp, `AmSpacing.Xl`) frente a distancia intra-grupo (8 dp `Sm` o 4 dp `Xs`): el encabezado de sección vive pegado a su lista (8 dp), la ayuda/error a su campo (4 dp + alineación al texto editable), y la acción principal fija se compone con `AmBottomAction` (nunca paddings repetidos por pantalla).

## 5. Validación

- Gates estáticos: `verifyDesignSystemCompliance`, `verifyDesignSystemFixtures`, `verifyControlMinSize`.
- Contratos de sistema: `AmSchemeTest`, `AmTypographyTest`, `AmAccentTest`, `AmSurfaceMotionTest`, `AmContrastPairsTest` (commonTest); `AmAccessibilityContractTest`, `AmPivotLayoutTest`, `AmUiPolishContractTest` (androidUnitTest/Robolectric).
- Regresión visual: `recordRoborazzi` → `recordVisualHashes`/`verifyVisualHashes` (ADR-0007).
- Agregador: `verifyAgendaQrArchitecture`.

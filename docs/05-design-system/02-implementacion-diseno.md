# Reglas de implementación del sistema de diseño

**Estado:** Guía de implementación. El contrato normativo vigente es `00-contrato-normativo-diseno.md` (ADR-0022); esta guía resume la estructura de `core/ui` y las reglas ejecutables del sistema.

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
    ├── AmButtons.kt       # acciones primarias con gradiente, secundarias y destructivas; filtros, segmentado, favorito
    ├── AmInputs.kt        # AmTextField, AmAmountField (cifras tabulares), AmSearchField y AmSearchTrigger
    ├── AmLists.kt         # filas, tarjetas, badges, metadatos y AmQuickAction (rejilla de Inicio)
    ├── AmStatus.kt        # banners, estados, carga, esqueleto, feedback de éxito
    ├── AmClipboard.kt     # rememberAmClipboard (expect/actual: copiar contenido al portapapeles)
    ├── AmChrome.kt        # AmTopBar, AmHeroHeader, navegación raíz, FAB reutilizable, transición de superficies
    ├── AmScaffold.kt      # AmScreenScaffold (contenedor de pantalla), AmBottomAction (acción principal fija: ancho completo + navigationBars + IME)
    ├── AmDialogs.kt       # AmDialog y AmDialogList con formas y estilos de tokens
    ├── AmFeedback.kt      # skeleton, paginación, scanner y marco de medios
    ├── AmFeedbackEvent.kt # AmFeedbackHost, AmSnackbarHost y eventos de feedback
    ├── AmPivot.kt         # AmSectionHeader y AmPivot (pestañas)
    └── AmQrPreview.kt     # preview QR multiplataforma; lienzo blanco y estado explícito ante errores
```

## 2. Reglas de token

- Todo literal visual (hex, `dp`, `sp`, `Color(...)`, `Color.<nombre>`, formas con esquinas) vive en `AmTokens.kt`. El resto del repositorio consume `AmColor`/`AmSpacing`/`AmShapes`/`AmRadius`/`AmMetrics`/`AmType`/`AmTextStyles`/`AmMotion`.
- Un cambio visual es un cambio de token + pin actualizado en `AmSchemeTest` + capturas regeneradas; nunca un literal nuevo en una pantalla. Las referencias externas orientan la exploración, no determinan los valores.
- Los pares de color llegan verificados: `AmSchemeTest` fija los umbrales (4.5:1 contenido, 3:1 bordes/foco, 3:1 suelo deshabilitado); `AmContrastPairsTest` fija los bloques de identidad de contexto, el par del botón primario y el rol focus.

## 3. Reglas de componente

- Los componentes fijan forma, colores y estados usando tokens semánticos; Material 3 es detalle de implementación y hereda `typography`/`shapes` de `AmTheme`.
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

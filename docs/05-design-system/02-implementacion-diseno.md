# Reglas de implementación del sistema de diseño

**Estado:** Normativo (ADR-0014). El contrato completo está en `00-contrato-normativo-diseno.md`; este documento traduce el contrato a reglas de código.

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
    ├── AmButtons.kt       # botones (solid/outline/none), chips estilo selector, toggle de favorito
    ├── AmInputs.kt        # AmTextField (sin borde, guía hairline; ayuda/error a 4 dp y alineada al texto editable), AmSearchField (radio 6), AmSearchTrigger
    ├── AmLists.kt         # AmListRow (celda), AmBadge
    ├── AmStatus.kt        # AmStatusBanner, AmLoading, AmEmptyState, AmSection
    ├── AmChrome.kt        # AmTopBar, navegación raíz, FAB, transición de superficies
    ├── AmScaffold.kt      # AmScreenScaffold (contenedor de pantalla), AmBottomAction (acción principal fija: ancho completo + navigationBars + IME)
    ├── AmDialogs.kt       # AmDialog, AmDialogList (superposiciones)
    ├── AmFeedback.kt      # skeleton, paginación, scanner, marco de medios (Surface: tarjeta distinta del fondo en ambos temas)
    ├── AmFeedbackEvent.kt # AmFeedbackHost/AmSnackbarHost/AmFeedbackEvent
    ├── AmPivot.kt         # AmSectionHeader, AmPivot (pestañas de S06)
    └── AmQrPreview.kt     # expect (Android decodifica; fallo de decodificación = glifo explícito sobre el marco, nunca rectángulo blanco; iOS pendiente de hardware)
```

## 2. Reglas de token

- TODO literal visual (hex, `dp`, `sp`, `Color(...)`, `Color.<nombre>`, formas con esquinas) vive en `AmTokens.kt`. El resto del repositorio consume `AmColor`/`AmSpacing`/`AmShapes`/`AmRadius`/`AmMetrics`/`AmType`/`AmTextStyles`/`AmMotion`.
- Un cambio visual es un cambio de token + pin actualizado en `AmSchemeTest` + capturas regeneradas; nunca un literal nuevo en una pantalla.
- Los pares de color llegan verificados: `AmSchemeTest` fija los umbrales (4.5:1 contenido, 3:1 bordes/foco, 3:1 suelo deshabilitado); `AmContrastPairsTest` fija los bloques de identidad de contexto, el par del botón primario y el rol focus.

## 3. Reglas de componente

- Envoltorios finos de Material 3 con tokens: el componente fija forma/colores/estados que M3 no mapea (botones radio 4, campos sin borde, chips estilo selector) y hereda `typography`/`shapes` del tema; la barra superior (56 dp) y la inferior (64 dp, sin píldora) son composición propia.
- Todo copy llega como parámetro del llamador (ADR-0008); las pantallas lo sirven desde `AppStrings`.
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

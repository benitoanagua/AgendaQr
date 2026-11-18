# Reglas de implementación del sistema de diseño

**Estado:** Guía histórica de implementación, pendiente de alineación. El contrato normativo vigente es `00-contrato-normativo-diseno.md` (ADR-0025); esta guía resume la estructura previa de `core/ui` y sus reglas ejecutables, hoy sustituidas por el contrato.

## 1. Mapa de la implementación

```
core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/
├── theme/
│   ├── AqTokens.kt        # tokens primitivos y semánticos Polaris 9.4.2 (única capa con literales)
│   ├── AqTheme.kt         # puente claro/oscuro a Material 3 + inyección de Inter
│   ├── AqPreviews.kt      # galería claro/oscuro/200 % para revisión de roles semánticos
│   └── AqWindowSize.kt    # expect: clase de ancho de ventana (compacto/medium/expanded)
├── motion/
│   └── ReducedMotion.kt   # expect/actual: LocalReducedMotion + ProvideReducedMotion
└── components/
    ├── AqText.kt          # AqText, AqHeading (texto con interlineado propio)
    ├── AqIcon.kt          # AqIcon + AqIcons (Polaris Icons 9.3.1, único punto de entrada)
    ├── AqTone.kt           # AqTone + toneColors + glifo por tono
    ├── AqButtons.kt       # acciones planas (sin gradiente), secundarias y destructivas; filtros, segmentado, favorito
    ├── AqInputs.kt        # AqTextField, AqAmountField (cifras tabulares), AqSearchField y AqSearchTrigger
    ├── AqLists.kt         # filas, tarjetas, badges, metadatos y AqQuickAction (rejilla de Inicio)
    ├── AqStatus.kt        # banners, estados, carga, esqueleto, feedback de éxito
    ├── AqClipboard.kt     # rememberAmClipboard (expect/actual: copiar contenido al portapapeles)
    ├── AqChrome.kt        # AqTopBar, AqHeroHeader, navegación raíz, transición de superficies (sin FAB: Inicio usa la rejilla de acciones)
    ├── AqScaffold.kt      # AqScreenScaffold (contenedor de pantalla), AqBottomAction (acción principal fija: ancho completo + navigationBars + IME)
    ├── AqDialogs.kt       # AqDialog y AqDialogList con formas y estilos de tokens
    ├── AqFeedback.kt      # skeleton, paginación, scanner y marco de medios
    ├── AqFeedbackEvent.kt # AqFeedbackHost, AqSnackbarHost y eventos de feedback
    ├── AqTabs.kt          # AqSectionHeader y AqTabs (pestañas)
    ├── AqQrPreview.kt     # preview QR multiplataforma; lienzo blanco y estado explícito ante errores
    ├── AqLayout.kt        # AqBlockStack, AqInlineStack, AqBox, AqDivider y ayudas de página
    ├── AqActions.kt       # AqLink y AqButtonGroup sobre botones existentes
    ├── AqForms.kt         # AqSelect, AqCheckbox, AqRadioButton, AqSwitch y grupos
    ├── AqProgress.kt      # AqProgress, AqProgressBar y AqSpinner
    ├── AqSheets.kt        # AqBottomSheet / AqModalSheet (D-04) con scrim y superficie tokenizada
    ├── AqDataTable.kt     # AqIndexTable con columnas y filas inmutables
    └── AqProductExtensions.kt # X-01 evidencia, X-02 sincronización, X-03 hoja QR
```

## 2. Reglas de token

- Todo literal visual (hex, `dp`, `sp`, `Color(...)`, `Color.<nombre>`, formas con esquinas, alfas y proporciones) vive en `AqTokens.kt`. El resto del repositorio consume `AqAlpha`/`AqColor`/`AqSpacing`/`AqShapes`/`AqRadius`/`AqMetrics`/`AqComposition`/`AqType`/`AqTextStyles`/`AqMotion`.
- Un cambio visual es un cambio de token + pin actualizado en `AqSchemeTest`; nunca un literal nuevo en una pantalla. Las referencias externas orientan la exploración, no determinan los valores.
- Los pares de color llegan verificados: `AqSchemeTest` fija los umbrales (4.5:1 contenido, 3:1 bordes/foco, 3:1 suelo deshabilitado); `AqContrastPairsTest` fija los bloques de identidad de contexto, el par del botón primario y el rol focus.

### Literales residuales centralizados

La limpieza final concentró en `AqTokens.kt` los últimos valores visuales que
vivían en componentes y pantallas, sin cambiar su valor:

- `AqAlpha`: ocultado durante `loading` y alfas `0.25`/`0.20` de selección,
  presentes en la instantánea publicada.
- `AqType.HeadingLineHeightRatio` y `BodyLineHeightRatio`: derivados de los
  pares `font-size`/`font-line-height` publicados para tamaños no canónicos.
- `AqComposition`: ancho del título del skeleton y tamaño del glifo de
  confirmación (D-08), conservados tal como estaban auditados.
- `AqMotion.SurfaceSlideFraction`, `PredictiveBackShrink` y
  `PredictiveBackFade`: geometría de transición y del back nativo (D-03/D-08).

Los vectores Polaris conservan los atributos `d`, el `viewBox` 20×20 y la
regla de relleno publicados; son datos del recurso iconográfico y el único
color intrínseco se sustituye por el rol semántico que recibe `AqIcon`. Los
literales de preview (`density`, `fontScale`, progreso de ejemplo) no son
valores de producto.

## 3. Reglas de componente

- Los componentes fijan forma, colores y estados usando tokens semánticos; Material 3 es detalle de implementación y hereda `typography`/`shapes` de `AqTheme`.
- Todo copy llega como parámetro del llamador (ADR-0008); las pantallas lo sirven desde `AppStrings`.
- `AqStatusBanner` se compone en **columna**: fila 1 icono + mensaje a ancho completo; fila 2 acciones alineadas al final con envoltura (sin fila si no hay acciones). API: `AqBannerAction(label, onClick)` para `action`/`dismiss`; imposible el estado inválido (sin `requireNotNull`).
- `AqListRow.meta` solo metadatos/badges: prohibido colocar acciones ahí.
- `AqTextAction` ofrece variantes `Default` (acento), `Neutral` (texto primario semibold) y `Danger` (destructiva); prohibido usar `TextSecondary`/`TextTertiary` como color de acción.
- `AqListBody` (padding 0) agrupa filas de lista; `AqCard` (padding `AqSpacing.Lg`) para contenido libre.
- `AqSecondaryButton` ocupa todo el ancho cuando el llamador pasa `fillMaxWidth()` (superficie visible = ancho completo).
- Regla de colocación: en tarjetas de pendientes, las acciones secundarias se alinean a la derecha (`Arrangement.End`), orden fijo [Guardar de todos modos] [Descartar], Descartar siempre a la derecha en tono peligro.
- Estados obligatorios: pressed/focused/selected/error/disabled/loading, cada uno con señal no exclusivamente cromática.
- Área táctil ≥ 48 dp (`AqMetrics.ControlMinSize`), sin alturas fijas que recorten con fuente grande.
- Iconos: solo `AqIcons`; decorativos con `contentDescription = null`; interactivos con etiqueta visible del llamador.

### Componentes aditivos del sistema

La capa aditiva de componentes rellena los huecos detectados sin sustituir los
símbolos ya fijados por `verifyScreenContract`: `AqLink` y `AqButtonGroup`
reutilizan `AqTextAction`, `AqPrimaryButton` y `AqSecondaryButton`; los
controles de formulario (`AqSelect`, `AqCheckbox`, `AqRadioButton`, `AqSwitch`)
reutilizan `AqDialog`, `AqDialogList`, `AqBadge` y `AqTextAction`;
`AqProgressBar`/`AqSpinner` usan colores semánticos y anuncian su estado;
`AqBottomSheet` usa el modal del sistema con scrim y superficie tokenizados;
`AqIndexTable` compone columnas flexibles que crecen en altura con la fuente.

Las extensiones canónicas viven ahora en `core/ui`: `AqEvidenceBadge` (X-01)
acepta el texto del llamador y nunca afirma pago ni verificación de dinero;
`AqSyncState` modela los estados como sealed interface y `AqSyncIndicator`
oculta el estado sincronizado por defecto; `AqQrSheet` (X-03) garantiza el
lienzo blanco `AqColor.QrCanvas` en ambos temas y delega el brillo a la
plataforma. La ruta de acceso en ≤2 toques y los efectos de pantalla siguen
residiéndo en la capa feature, donde están verificados en Android.

En las pantallas, `ElementSyncBadge` traduce el estado del dominio a
`AqSyncState` y delega el render en `AqSyncIndicator`, conservando el copy de
`ElementSyncStatus` y el reintento global de `AppStatusHost`. El detalle de
operación usa `AqEvidenceBadge` cuando hay comprobantes asociados, sin
introducir afirmaciones monetarias. El QR a pantalla completa continúa sobre
`AqQrPreview` (lienzo blanco verificado en ambos temas) y los efectos de
brillo siguen siendo `expect/actual` en la capa feature.

## 4. Reglas de pantalla

- La capa feature compone SOLO componentes del sistema (`core:ui`); importar controles M3 en `feature/`, `shared/` o `androidApp/` es una fuga del sistema y falla el gate.
- La interacción (clickable, indicación, foco, semántica de fila) vive en el sistema, no en la pantalla.
- El copy de las pantallas sale de `AppStrings` (decisión D2); los tests fijan esa centralización.
- Espaciado entre grupos (20 dp, `AqSpacing.Xl`) frente a distancia intra-grupo (8 dp `Sm` o 4 dp `Xs`): el encabezado de sección vive pegado a su lista (8 dp), la ayuda/error a su campo (4 dp + alineación al texto editable), y la acción principal fija se compone con `AqBottomAction` (nunca paddings repetidos por pantalla).

## 5. Validación

- Gates estáticos: `verifyDesignSystemCompliance`, `verifyDesignSystemFixtures`, `verifyControlMinSize`, `verifyScreenContract` (contrato de interacción por pantalla, excepciones en uso y capturas), `verifyScreenContractFixtures`.
- Contratos de sistema: `AqSchemeTest`, `AqTypographyTest`, `AqAccentTest`, `AqSurfaceMotionTest`, `AqContrastPairsTest` (commonTest); `AqAccessibilityContractTest`, `AqTabsLayoutTest`, `AqUiPolishContractTest` (androidUnitTest/Robolectric).
- Verificación de layout: `LayoutReferenceTest` (ADR-0007).
- Agregador: `verifyAgendaQrArchitecture`.

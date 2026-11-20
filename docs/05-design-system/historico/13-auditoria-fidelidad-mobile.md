> **HISTÓRICO — no normativo.** Este documento fue consolidado en [11-matriz-gaps-mobile.md](../11-matriz-gaps-mobile.md) y [10-auditoria-fidelidad.md](../10-auditoria-fidelidad.md). No es fuente normativa; se conserva solo como registro histórico. La autoridad vigente es el contrato normativo v9.0 y ADR-0029.


> **HISTÓRICO — no normativo.** Este documento fue consolidado en [](../11-matriz-gaps-mobile.md) y [](../10-auditoria-fidelidad.md). No es fuente normativa; se conserva solo como registro histórico. La autoridad vigente es el contrato normativo v9.0 y ADR-0029.


# Auditoría de fidelidad móvil a Polaris (`light-mobile`)

**Estado:** informe de verificación, anexo al contrato normativo.
**Autoridad:** ADR-0025, ADR-0026, ADR-0028, contrato normativo v8.5,
instantánea `09-instantanea-tokens-polaris-9.4.2.md` (captura 2026-11-17).
**Variante auditada:** `light-mobile` para la escala móvil (tipografía y
densidad de tarjeta) + `light`/`dark-experimental` para color, según el
contrato. Sustituye como referencia puntual a `10-auditoria-fidelidad-polaris.md`
(capa `core/ui` ya migrada) y `11-auditoria-atomic-spacing.md` (espaciado);
este documento cubre la fidelidad **móvil por componente** previa a la
matriz de gaps (`14-matriz-gaps-mobile.md`).

> Numeración: el encargo pedía `10-`/`11-`, pero esos números ya están
> ocupados por auditorías previas; se usan los siguientes libres.

## 0. Método

- Revisión token por token de `AqTokens.kt` contra la instantánea
  (`:43-578`), foco en los roles que consume la app (`light` + derivación
  oscura documentada en PV-02/D-07/D-11).
- Censo de los 90+ composables `Aq*` de `core/ui` y de los composables de
  pantalla de `feature/destinations/presentation`.
- Búsqueda de literales visuales fuera de tokens en `core/ui` y
  `feature/`: solo `PolarisIconVectors.kt` (viewBox 20×20 de los SVG
  oficiales, fuente de iconos, no valor de layout).
- Gates de línea base en `main` antes de la rama: `verifyAgendaQrArchitecture`,
  `:androidApp:testDebugUnitTest`, `:androidApp:assembleDebug` → todos PASS.

## 1. Capa de tokens

| Capa | Resultado | Evidencia |
|---|---|---|
| Color semántico claro | FIel | Roles `background/surface/border/text*/brandFill/emphasis/link/selection*/danger/success/warning/info/focus/disabled/inverse/scrim` coinciden valor a valor con `:63-287`; `inputSurface`/`functionalBorder` usan `input-bg-surface`/`input-border` (`:270-275`). |
| Color semántico oscuro | FIel con derivaciones registradas | `dark-experimental` parcial; contenedores de tono derivados D-11; enlace/enfoce/emphasis vía X-05/PV-06 (`#91D0FF`). |
| Espaciado | FIel | Escala `space-*` publicada interpretada como dp (D-01); `AqSpacing` cubre 0/2/4/8/12/16/20/24/32/48/96. No existe paso de 6 dp (`space-150`); ningún componente lo necesita. |
| Radios | FIel | `AqRadius` = `border-radius-050/100/200/300/400` (2/4/8/12/16 dp); `Pill` usa full. |
| Elevación | FIel | `AqElevation` reproduce `shadow-100..600` con `offsetY`/`spread` como metadato (D-10); `shadow-100` suprimida en claro (`light-mobile:497`) — verificado en `aqElevation`. |
| Tipografía | **3 desviaciones corregidas** | Ver §2. |
| Movimiento | FIel | Duraciones y easings de la instantánea; roles derivados X-04/PV-04. |
| Métricas de composición | Nativas documentadas | Alturas de barra, targets 48 dp, tamaños de glifo: nivel 3 (D-02/D-03) o extensión de producto, nunca valores Polaris inventados. |

## 2. Desviaciones encontradas y corregidas

| # | Hallazgo | Valor anterior | Valor Polaris `light-mobile` | Corrección |
|---|---|---|---|---|
| F-01 | `AqType.Amount` (héroe de importe, detalle y editor) | 30 sp (= `heading-2xl` del tema **base**, `:413`) | 32 sp (`:512`, `font-size-800`) | `Amount` → 32 sp; `LineAmount` 40 sp ya coincide con `line-height-1000` móvil. Commit `fix(ui): align type scale…`. |
| F-02 | `AqType.CaptionSmall` / `AqTextStyles.Tag` (badges de conteo en segmented/chips, texto de tooltip) | 11 sp (= `body-xs` del tema **base**, `:458`) | 12 sp (`:524`, `font-size-300`) | `CaptionSmall` → 12 sp; es el piso tipográfico publicado en móvil. |
| F-03 | Título de `AqTopBar` | 20 sp semibold (= `heading-lg` **base**, `:423-425`) | 18 sp semibold (`:515`, `font-size-450`) | `AqChrome` usa `HeadingLg`/`LineHeightHeadingLg`; `TitleLarge` queda como alias del puente Material. |

Tests actualizados: `AqTypographyTest` (32/12 sp). Suites `core:ui` en verde.

## 3. Clasificación de componentes

Criterio: **FIel** = patrón y valores Polaris (o extensión X-*/desviación D-*
registrada); **Desviado** = corregido en §2; **Ad hoc** = sin componente
Polaris, resuelto por la cadena de precedencia (ADR-0029) con nivel 2/3.

### 3.1 Tokens y fundaciones

| Pieza | Clasificación | Nota |
|---|---|---|
| `AqColorScheme` / `AqPalette` / `AqAccents` (X-06) | FIel | Valores verificados; acentos son extensión documentada. |
| `AqSpacing`/`AqMetrics`/`AqRadius`/`AqShapes`/`AqWeight`/`AqType`/`AqMotion`/`AqElevation` | FIel tras F-01..F-03 | `DisplayMedium` (40 sp) y `HeadlineSmall` (24 sp) son alias/extensiones sin uso en producción fuera del teclado PIN (`HeadlineSmall`, extensión declarada en código). |
| `AqText`/`AqHeading` | FIel | Roles `text-*`/`heading-*` móviles. |
| `AqIcon` + `PolarisIconVectors` | FIel | 34 glifos fuente `@shopify/polaris-icons@9.3.1`, viewBox 20. |

### 3.2 Acciones

| Composable | Polaris / nivel | Clasificación |
|---|---|---|
| `AqPrimaryButton`/`AqSecondaryButton`/`AqLightButton`/`AqOutlineButton`/`AqDangerButton`/`AqTextAction`/`AqIconButton`/`AqCompactAction`/`AqButtonGroup`/`AqLink` | Botón Polaris (relleno/contorno/texto) + variantes | FIel; targets 48 dp por D-02. |
| `AqSegmentedControl` | Elección única compacta (patrón móvil; Polaris la usa en filtros) | FIel; `selectedIndex=-1` admitido desde la rama de jerarquía. |
| `AqFavoriteToggle` | Extensión de producto | FIel (X-07). |

### 3.3 Formularios y entrada

| Composable | Polaris / nivel | Clasificación |
|---|---|---|
| `AqTextField`/`AqMultilineTextField`/`AqAmountField`/`AqSearchField`/`AqSelect` | Campo Polaris (`input-*`) | FIel; `inputSurface`/`input-border` publicados. |
| `AqFormSection`/`AqFormField`/`AqFieldMessages` | Composición de jerarquía (contrato §2) | FIel. |
| `AqCheckbox`/`AqCheckboxGroup`/`AqRadioButton`/`AqSwitch`/`AqChoiceGroup`/`AqSelectAllCheckbox`/`AqTriState` | Controles Polaris | FIel. |
| `AqChipGroup`/`AqChipOption` | Chips Polaris | FIel; count separado (fix de la rama anterior). |
| `AqDatePickerDialog`/`AqTimePickerDialog` | Polaris no define picker móvil → nivel 3 nativo, tokens Polaris | FIel como desviación documentada (D-07: `AqDialog`, nunca sheet). |
| `AqPinDots`/`AqKeypad` | Patrón móvil propio del acceso local | FIel; `error=` desde ADR-0028. |
| `AqSearchTrigger` | Campo de búsqueda Polaris adaptado a móvil | FIel. |

### 3.4 Navegación y chrome

| Composable | Polaris / nivel | Clasificación |
|---|---|---|
| `AqTopBar` (+`overflowActions`, back) | Polaris `TopBar` web adaptado; barra en sí nivel 3 (D-03) | FIel; título corregido en F-03. |
| `AqNavBar`/`AqNavRail` | Navegación inferior/riel nativa (D-03); `nav-*` tokens publicados | FIel. |
| `AqTabs`/`AqSectionHeader` | Pestañas Polaris (`role=Tab`) | FIel. |
| `AqScreenScaffold`/`AqBottomAction`/`AqBottomActionDock` | Composición de página + acción fija (contrato §2) | FIel. |
| `AqActionSheet` (overflow "…") | Menú contextual móvil; Polaris web usa popover → patrón móvil nivel 2/3 | FIel (contrato de pantallas lo exige). |
| `AqBottomSheet` | Bottom sheet nativo (D-04) | FIel como primitivo disponible; sin consumidor en V1 (D-07 prohíbe sheets en flujos congelados). |
| `AqTooltip` | Tooltip Polaris | FIel; texto ahora 12 sp (F-02). |

### 3.5 Superficies, listas y datos

| Composable | Polaris / nivel | Clasificación |
|---|---|---|
| `AqCard`/`AqListBody`/`AqListRow`/`AqHairline`/`AqTappableCard`/`AqIndexTable` | Card/List Polaris | FIel; sombra-100 suprimida en claro, borde hairline. |
| `AqExpansionPanel` | Acordeón móvil (referencia Moon, ADR-0028) | FIel. |
| `AqCarousel` | Carrusel móvil (referencia Moon) | FIel (S01). |
| `AqMetaRow`/`AqBadge`/`AqTonalTag`/`AqIconBadge`/`AqContextBadge`/`AqStatusPill` | Badges/metadatos Polaris | FIel. |
| `AqQuickAction` | Celda de acción rápida móvil | FIel. |

### 3.6 Feedback y estados

| Composable | Polaris / nivel | Clasificación |
|---|---|---|
| `AqStatusBanner`/`AqStatusPill` | Banner Polaris | FIel. |
| `AqSnackbarHost`/`AqFeedbackEvent` | Toast/snackbar móvil (Polaris no tiene equivalente táctil) | FIel como extensión. |
| `AqEmptyState`/`AqLoading`/`AqSuccessCheck`/`AqSection` | Empty state/progreso (sin componente Polaris móvil → nivel 3) | FIel. |
| `AqSkeleton`/`AqProgressBlock`/`AqProgressBar`/`AqSpinner` | Skeleton/progress (Moon `MoonLinearLoader`/`MoonCircularLoader` como referencia nivel 2) | FIel. |
| `AqQrPreview`/`AqScannerFrame`/`AqMediaFrame` | Extensión X-03 / cámara | FIel. |
| `AqEvidenceBadge`/`AqSyncIndicator` | Extensiones X-01/X-02 | FIel. |

### 3.7 Layout

| Composable | Clasificación |
|---|---|
| `AqBox`/`AqBlockStack`/`AqInlineStack` | FIel (utilidades de ritmo sobre `AqSpacing`). |

## 4. Componentes ad hoc en pantallas

Ninguno: `feature/destinations/presentation` no declara colores, radios,
espaciados ni tipografía propios (búsqueda literal + `verifyAgendaQrArchitecture`).
Los únicos valores no token encontrados son el viewBox 20×20 de los vectores
Polaris (fuente iconográfica, no layout).

## 5. Conclusión

La capa visual era ya fiel a Polaris 9.4.2 en color, espaciado, radios,
elevación y movimiento; quedaban **tres desviaciones tipográficas** residuales
de la escala base (tema escritorio) que se corrigieron en esta rama
(F-01..F-03). No se detectaron componentes ad hoc ni valores hardcodeados.
Las diferencias restantes son las desviaciones móviles registradas (D-01..D-12)
y las extensiones de producto (X-01..X-07), todas documentadas.

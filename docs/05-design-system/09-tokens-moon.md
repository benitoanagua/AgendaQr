# Tokens Moon (valores)

**Estado:** Normativo — fuente única de tokens (ADR-0044).
**Fuentes:** `moon_tokens` 0.0.6 (`MoonColors.light` / `MoonColors.dark`),
`moon_design` 1.1.0 (tamaños, bordes, opacidades, transiciones, sombras).
La iconografía de producto es Phosphor `@phosphor-icons/core` v2.0.8 (MIT;
ADR-0052), no forma parte del catálogo de iconos Moon.

Este documento lista los valores que `core/ui/.../theme/AqTokens.kt` transcribe. Los tokens de estado derivados se identifican como adaptaciones semánticas de Agenda QR: no sustituyen efectos ni estados visuales que Moon ya define para un componente (en particular, hover/pressed de botones).
La geometría (`AqSize`, `AqCorner`, `AqStroke`, `AqStateAlpha`, `AqMotion`,
`AqShadow`) está en [`13-catalogo-moon.md`](13-catalogo-moon.md) §1.

## 1. Primitivas de color (`AqPalette`)

Los nombres `piccolo`, `hit`, `beerus`, `goku`, `gohan`, `bulma`, `trunks`,
`goten`, `popo`, `heles`, `zeno`, `krillin`, `chichi`, `roshi`, `dodoria`,
`cell`, `raditz`, `whis`, `nappa` son los de `MoonColors`. Los prefijos `light`
y `dark` separan los valores que Moon define distintos por tema. Los demás
(`accent*`, `light*`, `dark*` con sufijo de rol) son **derivados**: mezclas de
colores Moon calculadas para cumplir AA (ver PV-01…PV-05 del
[contrato](00-contrato-normativo-diseno.md)).

| Primitiva | Valor |
|---|---|
| `piccolo` | #5C33CF |
| `hit` | #1BD29A |
| `goten` | #FFFFFF |
| `popo` | #000000 |
| `krillin` | #FFB319 |
| `chichi` | #FF4E64 |
| `roshi` | #49B356 |
| `dodoria` | #D33030 |
| `cell` | #95F1D5 |
| `raditz` | #B3804A |
| `whis` | #3448F0 |
| `nappa` | #725550 |
| `accentGreen` | #2C6B34 |
| `accentBronze` | #745330 |
| `zeno` | #000000 (α 0.56) |
| `lightBeerus` | #DCDEE3 |
| `lightGoku` | #FFFFFF |
| `lightGohan` | #F6F7F9 |
| `lightBulma` | #000000 |
| `lightTrunks` | #687083 |
| `lightHeles` | #000000 (α 0.04) |
| `darkBeerus` | #292929 |
| `darkGoku` | #000000 |
| `darkGohan` | #1F1F1F |
| `darkBulma` | #FFFFFF |
| `darkTrunks` | #94989E |
| `darkHeles` | #FFFFFF (α 0.08) |
| `piccoloDisabled` | #5C33CF (α 0.60) |
| `lightTrunksDisabled` | #687083 (α 0.60) |
| `darkTrunksDisabled` | #94989E (α 0.60) |
| `lightBrandHover` | #512DB6 |
| `lightBrandActive` | #46279D |
| `lightEmphasisSurface` | #EBE7F9 |
| `lightEmphasisSurfaceHover` | #DED6F5 |
| `lightSelectionFill` | #DED6F5 |
| `lightSurfaceHigh` | #ECEDEF |
| `lightDanger` | #BA2A2A |
| `lightDangerActive` | #A02424 |
| `lightDangerContainer` | #FBEEEE |
| `lightDangerDisabled` | #EDACAC |
| `lightSuccess` | #2C6B34 |
| `lightSuccessFill` | #337D3C |
| `lightSuccessContainer` | #EDF7EE |
| `lightWarning` | #805A0C |
| `lightWarningContainer` | #FFF6E3 |
| `lightInfoContainer` | #EFF0FE |
| `darkBrandHover` | #6943D3 |
| `darkBrandActive` | #7654D7 |
| `darkPiccoloText` | #A58FE5 |
| `darkEmphasisSurface` | #2B2342 |
| `darkEmphasisSurfaceHover` | #332557 |
| `darkSelectionFill` | #372765 |
| `darkSelectionSurface` | #302550 |
| `darkSurfaceHighest` | #3A3A3A |
| `darkDanger` | #FF7A8B |
| `darkDangerActive` | #BA2A2A |
| `darkDangerContainer` | #43272A |
| `darkDangerDisabled` | #6A1818 |
| `darkSuccessContainer` | #263728 |
| `darkWarningContainer` | #43371E |
| `darkInfo` | #8F9AF7 |
| `darkInfoContainer` | #232749 |
| `qrCanvas` | #FFFFFF |

## 2. Roles semánticos (`AqColorScheme`)

| Rol | Claro | Oscuro |
|---|---|---|
| `background` | `lightGoku` · #FFFFFF | `darkGoku` · #000000 |
| `surface` | `lightGohan` · #F6F7F9 | `darkGohan` · #1F1F1F |
| `surfaceSecondary` | `lightGohan` · #F6F7F9 | `darkGohan` · #1F1F1F |
| `inputSurface` | `lightGohan` · #F6F7F9 | `darkGohan` · #1F1F1F |
| `border` | `lightBeerus` · #DCDEE3 | `darkBeerus` · #292929 |
| `functionalBorder` | `lightTrunks` · #687083 | `darkTrunks` · #94989E |
| `divider` | `lightBeerus` · #DCDEE3 | `darkBeerus` · #292929 |
| `textPrimary` | `lightBulma` · #000000 | `darkBulma` · #FFFFFF |
| `textSecondary` | `lightTrunks` · #687083 | `darkTrunks` · #94989E |
| `textPlaceholder` | `lightTrunks` · #687083 | `darkTrunks` · #94989E |
| `textTertiary` | `lightTrunks` · #687083 | `darkTrunks` · #94989E |
| `brandFill` | `piccolo` · #5C33CF | `piccolo` · #5C33CF |
| `brandFillHover` | `lightBrandHover` · #512DB6 | `darkBrandHover` · #6943D3 |
| `brandFillActive` | `lightBrandActive` · #46279D | `darkBrandActive` · #7654D7 |
| `brandFillSelected` | `piccolo` · #5C33CF | `piccolo` · #5C33CF |
| `brandFillDisabled` | `piccoloDisabled` · #5C33CF (α 0.60) | `piccoloDisabled` · #5C33CF (α 0.60) |
| `onBrandFill` | `goten` · #FFFFFF | `goten` · #FFFFFF |
| `onBrandFillDisabled` | `goten` · #FFFFFF | `goten` · #FFFFFF |
| `emphasisFill` | `piccolo` · #5C33CF | `piccolo` · #5C33CF |
| `emphasisBorder` | `piccolo` · #5C33CF | `darkPiccoloText` · #A58FE5 |
| `emphasisText` | `piccolo` · #5C33CF | `darkPiccoloText` · #A58FE5 |
| `emphasisIcon` | `piccolo` · #5C33CF | `darkPiccoloText` · #A58FE5 |
| `emphasisSurface` | `lightEmphasisSurface` · #EBE7F9 | `darkEmphasisSurface` · #2B2342 |
| `emphasisSurfaceHover` | `lightEmphasisSurfaceHover` · #DED6F5 | `darkEmphasisSurfaceHover` · #332557 |
| `emphasisOnFill` | `goten` · #FFFFFF | `goten` · #FFFFFF |
| `link` | `piccolo` · #5C33CF | `darkPiccoloText` · #A58FE5 |
| `selectionFill` | `lightSelectionFill` · #DED6F5 | `darkSelectionFill` · #372765 |
| `selectionSurface` | `lightEmphasisSurface` · #EBE7F9 | `darkSelectionSurface` · #302550 |
| `selectionBorder` | `piccolo` · #5C33CF | `darkPiccoloText` · #A58FE5 |
| `selectionIcon` | `piccolo` · #5C33CF | `darkPiccoloText` · #A58FE5 |
| `danger` | `lightDanger` · #BA2A2A | `darkDanger` · #FF7A8B |
| `dangerFill` | `dodoria` · #D33030 | `dodoria` · #D33030 |
| `onDanger` | `goten` · #FFFFFF | `goten` · #FFFFFF |
| `dangerContainer` | `lightDangerContainer` · #FBEEEE | `darkDangerContainer` · #43272A |
| `success` | `lightSuccess` · #2C6B34 | `roshi` · #49B356 |
| `successFill` | `lightSuccessFill` · #337D3C | `lightSuccessFill` · #337D3C |
| `onSuccessFill` | `goten` · #FFFFFF | `goten` · #FFFFFF |
| `successContainer` | `lightSuccessContainer` · #EDF7EE | `darkSuccessContainer` · #263728 |
| `warning` | `lightWarning` · #805A0C | `krillin` · #FFB319 |
| `warningFill` | `krillin` · #FFB319 | `krillin` · #FFB319 |
| `onWarningFill` | `popo` · #000000 | `popo` · #000000 |
| `warningContainer` | `lightWarningContainer` · #FFF6E3 | `darkWarningContainer` · #43371E |
| `info` | `whis` · #3448F0 | `darkInfo` · #8F9AF7 |
| `infoContainer` | `lightInfoContainer` · #EFF0FE | `darkInfoContainer` · #232749 |
| `focus` | `piccolo` · #5C33CF | `darkPiccoloText` · #A58FE5 |
| `disabled` | `lightHeles` · #000000 (α 0.04) | `darkHeles` · #FFFFFF (α 0.08) |
| `onDisabled` | `lightTrunksDisabled` · #687083 (α 0.60) | `darkTrunksDisabled` · #94989E (α 0.60) |
| `inverseSurface` | `lightBulma` · #000000 | `darkBulma` · #FFFFFF |
| `inverseOnSurface` | `lightGoku` · #FFFFFF | `darkGoku` · #000000 |
| `scrim` | `zeno` · #000000 (α 0.56) | `zeno` · #000000 (α 0.56) |
| `qrCanvas` | `qrCanvas` · #FFFFFF | `qrCanvas` · #FFFFFF |
| `surfaceContainerLow` | `lightGoku` · #FFFFFF | `darkGoku` · #000000 |
| `surfaceContainer` | `lightGohan` · #F6F7F9 | `darkGohan` · #1F1F1F |
| `surfaceContainerHigh` | `lightSurfaceHigh` · #ECEDEF | `darkBeerus` · #292929 |
| `surfaceContainerHighest` | `lightBeerus` · #DCDEE3 | `darkSurfaceHighest` · #3A3A3A |
| `surfaceActive` | `lightSurfaceHigh` · #ECEDEF | `darkBeerus` · #292929 |
| `componentActive` | `lightGohan` · #F6F7F9 | `darkGohan` · #1F1F1F |
| `dangerActive` | `lightDangerActive` · #A02424 | `darkDangerActive` · #BA2A2A |
| `dangerDisabled` | `lightDangerDisabled` · #EDACAC | `darkDangerDisabled` · #6A1818 |

## 3. Tipografía

| Rol | Tamaño / interlineado | Peso |
|---|---|---|
| `Heading3Xl` | 40 / 48 sp | semibold 600 |
| `Heading2Xl` | 32 / 40 sp | semibold 600 |
| `HeadingXl` | 24 / 32 sp | semibold 600 |
| `HeadingLg` | 18 / 24 sp | semibold 600 |
| `HeadingMd` | 16 / 20 sp | semibold 600 |
| `HeadingSm` | 14 / 20 sp | semibold 600 |
| `HeadingXs` | 12 / 16 sp | semibold 600 |
| `BodyLg` | 18 / 28 sp | regular 400 |
| `BodyMd` | 16 / 24 sp | regular 400 |
| `BodySm` | 14 / 20 sp | regular 400 |
| `BodyXs` | 12 / 16 sp | regular 400 |

Los tamaños pertenecen a la escala de texto de Moon (`MoonTextStyles`: 12, 14,
16, 18, 20, 24, 32, 40); Moon no define tracking, así que todo es 0 sp. La
familia es Inter (D-05 del contrato).

## 4. Iconos

Phosphor Icons `@phosphor-icons/core` v2.0.8 es la única familia de iconos de
producto (ADR-0052). Los 37 conceptos semánticos de `AqIcons` se mapean a
SVG oficiales convertidos a `ImageVector` KMP en
`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/icons/PhosphorProduct*IconVectors.kt`.
El catálogo conserva la API de producto; las pantallas no importan vectores
directamente.

- Peso base: Regular para iconos de navegación, contenido y acciones.
- Check/X de `AqSwitch`: Phosphor Bold en 12–14 dp.
- Tinte y tamaño proceden de `AqIcon`/tokens semánticos Moon.
- La licencia MIT y la atribución Phosphor se mantienen en
  [06-licencias-terceros.md](06-licencias-terceros.md) y en el aviso Android.

La antigua sección X-07 del contrato se conserva como historial de decisiones,
pero no es fuente vigente de iconos; ADR-0052 y este catálogo la sustituyen.

# Trazabilidad Moon Design v1 (Figma) → `Aq*`

Fuente de verdad geométrica: `moon-v1-tokens-y-componentes.json` (datos
crudos extraídos del archivo Figma «Moon Design System v1 (Community)») y su
versión legible `figma-moon-v1.md` en esta misma carpeta. Si ambos difieren,
prevalece el JSON. Colores, tipografía e iconos no se replican: rigen los
tokens de producto (ADR-0044/0046/0047/0048); lo que se verifica aquí es
**geometría, densidad, radios, padding/gap, alturas de control, estados y
área táctil**.

Convención: `px` del Figma = `dp` en Compose. «Estado» resume la
verificación: **fiel** = la geometría coincide con la spec; **corregida** =
se ajustó en esta pasada; **cubierta** = el componente `Aq*` implementa la
misma anatomía con otra API.

## Tokens de geometría

| Token Figma | Valor | `Aq*` |
|---|---|---|
| `radius-s-xs/sm/md/lg/rounded` | 4/8/12/16/9999 | `AqCorner.SurfaceXs/Sm/Md/Lg`, `AqRadius.Round` |
| `radius-i-xs/sm/md/lg/rounded` | 4/8/12/16/9999 | `AqCorner.InteractiveXs/Sm/Md`, `AqSquircles.Interactive*` |
| `p-1…p-12` | 4…48 | `AqSize.X5s…X2l`, `AqSpacing` |
| `h-4…h-16` | 16/24/32/40/48/56/64 | `AqSize.X2s/Xs/Sm/Md/Lg/Xl/X2l` |

## Componentes

| Moon (Figma) | `Aq*` | Geometría clave (spec → implementación) | Estado |
|---|---|---|---|
| Button | `AqButton` | xs 24 r4 pad 4/8 gap4 · sm 32 r8 pad 4/12 gap4 · md 40 r8 pad 8/16 gap8 · lg 48 r8 pad 12/16 gap12 · xl 56 r12 pad 16/24 gap16; texto 12/16→16/24 semibold; `Icon=Left/Right/Center` (`iconAtEnd`/`iconOnly`) | fiel + corregida (`iconAtEnd` para `Icon=Right`) |
| Text input | `AqTextInput` | sm 32 / md 40 r8 / lg 48 / xl 56 r8; `Input Label` (etiqueta superior 14/24 sm, 16/24 md-lg, gap 4/8) solo en sm/md/lg; etiqueta flotante dentro solo en xl; hint `textTertiary`; error `Danger`; contador `n / máx` a la derecha con gap 4 | corregida (regla `Input Label`/flotante xl + contador) |
| Text input group | `AqTextInputGroup` | horizontal 56 / vertical 112–160, r8, gap 0–8 | fiel (orientación `AqTextInputGroupOrientation`) |
| Textarea | `AqTextArea` | caja r8 borde `beerus`, alto por llamador (spec 132) | fiel |
| Search | `AqSearchInput` | campo de búsqueda sin etiqueta flotante; placeholder solo en vacío; la lista de resultados es patrón de pantalla | cubierta (input) |
| Select | `AqSelect` | **nuevo**: mismas alturas/radios/bordes que `Text input` (32/40/48/56, r4 sm / r8 resto), etiqueta flotante, chevron, menú `AqDropdown` | corregida (implementado) |
| Combobox | `AqCombobox` | **nuevo**: `AqTextInput` + menú filtrado; `Selected counter` = pie con recuento en multiselección | corregida (implementado) |
| Dropdown | `AqDropdown`/`AqDropdownSurface` | superficie r12 (`InteractiveMd`), pad 4, sombra `sm`, margen 8, filas `AqMenuItem` | fiel |
| Menu item | `AqMenuItem` | h40 (multilínea 56), r8, pad 8–12, gap 8, hover `heles`, seleccionado `SelectionSurface` | fiel |
| Checkbox | `AqCheckbox`/`AqCheckboxSet` | caja 24×24 | fiel |
| Radio button | `AqRadio`/`AqChoiceSet` | botón 24×24, gap 8 con etiqueta | fiel |
| Switch | `AqSwitch` | 2xs 16 (pista 32) · xs 24 (pista 44) · sm 32 (pista 60), pill 999, padding de pista 2/4/4, pulgar 12/16/24 con sombra `sm`, etiqueta 14/24 gap 8, target táctil 48 sin deformar la pista; `labelFirst` para fila de ajuste | fiel (`AqLayout.Switch*`) |
| Segmented control | `AqSegmentedControl` | sm 40 / md 48, r12, pad exterior 4, gap entre segmentos 4 | fiel (`AqLayout.SegmentedControl*`) |
| Tabs | `AqTabBar`/`AqPillTab`/`AqTabs` | pill sm 32 r4 pad 4/12 · md 40 r8 pad 8/16; underline y default misma altura; gap icono-texto 4/8 | fiel (`AqTabSize`) |
| Chip | `AqChip`/`AqChipRow` | sm 32 r4 pad 4/8 · md 40 r8 pad 8/8–12, gap 8, icono 24 | fiel (`AqChipSize`, `AqLayout.ChipGap`) |
| Tag | `AqTag`/`AqEvidenceTag` | 2xs 16 pad 0/8 texto 9–10 · xs 24 pad 4/8 · sm 32; radio 4; variantes con icono `Icon=None/Left/Right/Both` (`icon`/`trailingIcon`) | fiel + corregida (`trailingIcon`) |
| Avatar | `AqAvatar` | xs 24 r4 · sm 32 r8 · md 40 r8 · lg 48 r12 · xl 56 · 2xl 64; iniciales siempre circulares; insignia `badgeSize = tamaño×12/64`, margen `×4/64` | fiel (`AqAvatarSize`, `AqComposition.AvatarBadge*`) |
| Accordion | `AqAccordion` | cabecera sm 32 / md 40 / lg 48 / xl 56, r8, pad horizontal 8/12/12/16, expand con divisor | fiel (`AqAccordionSize`) |
| Alert | `AqAlert` | r8, pad 16, gap 8–16, min 56 (una línea) / 88 (con título), sin `height(maxHeight)` | corregida (ronda `fix/ui-visual-defects`) |
| Snackbar | `AqToast`/`AqToastHost` | r8, pad 16, gap 12–16, alturas por contenido 56–88 | fiel |
| Tooltip | `AqTooltip` | r4, pad 12, texto 12/16, borde 1 | fiel |
| Popover | `AqPopover` | r12, pad 4, gap 4 | fiel |
| Modal | `AqModal` | r12, pad 8, contenido ≤ 90 % del alto | fiel (`AqOverlay`) |
| Drawer | `AqDrawer` | ancho 448, r12, pad 4 | fiel (`AqLayout.DrawerWidth`) |
| Bottom sheet | `AqBottomSheet` | r12, pad 8 (8 arriba sin handle / 0+8 con handle), handle opcional | fiel |
| Progress bar | `AqLinearProgress`/`AqProgressBar`/`AqProgressPin`/`AqCircularProgress` | linear 2/4/8/12/16 (6xs…2xs, parámetro `height`); circular 16/24/32/40/48; pin suma 60/64 con etiqueta gap 4 | fiel + corregida (`height` en `AqProgressBar`) |
| Loader | `AqCircularLoader`/`AqLinearLoader` | 16/24/32/40/48 | fiel |
| Pagination | `AqPagination` | **nuevo**: fila h32, gap 4, ítems 24 r4, página activa `EmphasisFill`/`OnEmphasisFill` | corregida (implementado) |
| Breadcrumb | `AqBreadcrumb` | h24, gap 8, separador `ChevronRight` | fiel (`AqLayout.Breadcrumb*`) |
| Table | `AqTable`/`AqDataTable` | fila por tamaño (56 = `Xl`), pad horizontal por densidad | fiel (`AqTableRowSize`) |
| Auth code | `AqAuthCode` | campos 48×56, gap 8, r8 | fiel |
| Sidebar | `AqSidebar` | **nuevo**: expandida 280 / rail 64, r12, pad 8/4, gap sección 16, ítem h40 icono 24 + texto 14/24 | corregida (implementado) |
| Empty state | `AqEmptyPlaceholder` | pad horizontal 16, gap 8, título 24/32 semibold; `Type=Simple`/`Single button`/`Multi button` (`secondaryActionLabel`)/`Simple+ Illu` (`illustration`) | fiel + corregida (variantes multi-botón e ilustración) |
| Leaderboard | `AqLeaderboard` | **nuevo**: columna gap 12, texto 14/24, insignia de puesto 24, podio 1–3 destacado | corregida (implementado) |

## Barrido de fidelidad total (`fix/moon-fidelity-total`)

Prueba generada desde el JSON (`generateMoonSpecFixtures` →
`MoonSpecFidelityTest`/`MoonSpecRenderedTest`, gate
`verifyMoonFidelityCoverage`): 84 filas canónicas (una por familia+tamaño,
variante menos compleja) con alto, radio, padding, gap, borde y texto en
reposo; tolerancia 0 dp en geometría. Cobertura: 37 componentes con fixture
y 46 excepciones justificadas E-24…E-30 en
`00-contrato-normativo-diseno.md`.

Desviaciones encontradas y corregidas en esta pasada:

| Componente | Desviación | Corrección |
|---|---|---|
| `AqButton` | Radio único para todos los tamaños | `cornerRadius` por tamaño (xs 4, sm/md/lg 8, xl 12) |
| `AqChip` | Padding sm/md | 8/12 dp (`X4s`/`X3s`) |
| `AqEmptyPlaceholder` | Gap entre bloques | 8 dp (`X4s`) |
| `AqTabBar`/`AqPillTab` | Padding de píldora/underline | `size.padding`; underline sm 8 |
| `AqPopover`/`AqDropdown` | Padding de contenido | 4 dp (`PopoverContentPadding`) |
| `AqModal` | Radio/padding | `SurfaceMd` r12 + padding 8 |
| `AqDrawer` | Radio de borde final | `AqCorner.SurfaceMd` en topEnd/bottomEnd |
| `AqBottomSheet` | Radio solo superior | nuevo `AqSquircles.SurfaceTopMd` r12 |
| `AqTooltip` | Sin borde | borde 1 dp `AqColor.Border` |
| `AqSearchInput` | Sin parámetro de tamaño | nuevo `size: AqTextInputSize` |
| `AqTextArea` | Alto por defecto | `AqLayout.TextAreaHeight` = 132 dp |
| `AqMenuItem` | Padding vertical | 8/8/8/12 (spec canónico) |
| `AqTextInput`/`AqSelect` | Etiqueta flotante en todos los tamaños | flotante solo xl; `Input Label` superior en sm/md/lg (14/24 sm, 16/24 md-lg, gap 4/8); contador `n / máx` a 4 dp |
| `AqSwitch` | Etiqueta Medium y target = pista | etiqueta 14/24 Regular gap 8; target ≥ 48 dp con pista centrada; `labelFirst` (fila de ajuste) |
| `AqAppBar` | Flecha atrás como `AqButton` Text (icono 20, tinte `link`) | icono 24 `AqMetrics.IconSize`, tinte `TextPrimary`, táctil 48 |
| `AqButton`/`AqTag`/`AqProgressBar`/`AqEmptyPlaceholder` | Variantes del spec sin soporte | `iconAtEnd` (`Icon=Right`), `trailingIcon` (`Icon=Right/Both`), `height` (lineal 2–16 dp), `secondaryActionLabel` (`Multi button`) e `illustration` (`Simple+ Illu`) |

Pantallas ajustadas: «Editar destino» usa `AqTextArea` a 132 dp (antes
64), `AqSwitch(labelFirst = true)` (etiqueta izquierda, control derecha) y
los campos md muestran ahora la etiqueta superior del spec.

Incongruencias del archivo documentadas (regla aplicada): anchos 28/32 del
Switch 2xs → 32×16 de `moon_flutter`; posición del pulgar inconsistente →
apagado a la izquierda con relleno del tamaño; «DM Sans Bold» en estados
hover/focus → estado en reposo; radios/bordes ausentes en algunas filas del
JSON → relleno desde hermanos de la misma firma de variante.

## Notas

- «Search» del Figma es un campo con panel de resultados; `AqSearchInput`
  cubre el campo y el patrón de resultados vive en las pantallas
  (`SearchScreen`), no en el DS.
- «Sidebar» y «Leaderboard» son patrones de composición del archivo Figma;
  se implementaron como componentes `Aq*` genéricos (slots de copy) aunque
  AgendaQr aún no los usa en pantallas.
- En variantes `hover`/`focus` del Figma el texto pone «DM Sans Bold»: es
  una inconsistencia del propio archivo; se toma el estado en reposo
  (Averta/Aventa semibold), según la instrucción de la tarea.
- Hover = overlay `heles` (negro 4 % claro / blanco 8 % oscuro), pressed =
  reducción de escala 0,2 s, foco = anillo doble 2 px + 6 px `piccolo`@12 %:
  ya implementados en `AqButton`/`AqTextInput`/`AqMenuItem` y verificados
  por la evidencia pixel (`evidencia/informe.md`, 218 pares).

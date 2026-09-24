# Xauxa — Brechas detectadas y trazabilidad del laboratorio

Este documento registra, con evidencia de inspección, las diferencias entre el
Xauxa Design System documentado (snapshot v13 del ZIP `xauxa-design-system-kt.zip`)
y lo realmente implementado en `core:ui` de Agenda QR. Lo mantienen actualizado
el laboratorio de componentes y las compuertas de verificación; el laboratorio
muestra estas brechas en vivo, no las corrige.

## Fuentes inspeccionadas

| Fuente | Qué aporta |
|---|---|
| `xauxa-design-system/tokens/tokens.json` (v13) | Fuente canónica de tokens: primitivos, semánticos dark-first, tema light, marca por producto, focus, motion, tipografía. |
| `xauxa-design-system/docs/design-system.html` (v13) | Documento vivo: 9 foundations, 26 componentes (C01–C26 + C1b), 6 patterns, matriz de estados §10, auditoría de contraste, breakpoints §07. |
| `xauxa-design-system/kotlin-compose/` (v13) | `XauxaLiveTile`, `XauxaCommandBar`, `XauxaTurnstileNav`, `ReducedMotion`: código escrito contra APIs reales, **no compilado contra ningún proyecto**. |
| `design-tokens.json` (raíz del repo, v8) | Snapshot v8 reducido con el que se alineó la primera implementación. |
| `core/ui/.../theme/XauxaTokens.kt` | Tokens implementados que el código consume realmente. |

## 1. Deriva de valores entre Xauxa canónico y la implementación

Los tokens implementados de neutral/semántico **no coinciden** con `tokens.json`
v13 (ni con su tema light). Probablemente fueron derivados del snapshot v8 más
una paleta clara con tinte teal propia. No se modifican en esta pasada (es una
decisión de diseño con impacto visual); se registran:

| Token implementado | Valor actual | Xauxa v13 light | Xauxa v13 dark |
|---|---|---|---|
| `Background` | `F8FAFA` | — (no existe el token) | — |
| `Surface` | `FFFFFF` | `surface` = `FFFFFF` ✓ | `gray.95` = `17171B` |
| `Surface2` | `F0F4F3` | `FAFAF9` ✗ | `gray.90` = `1F1F24` |
| `Border` | `D7DEDC` | `E2E2DF` ✗ | `gray.80` = `2A2A30` |
| `TextPrimary` | `17201E` | `17171B` ✗ | `gray.05` = `F4F4F6` |
| `TextSecondary` | `4E5B57` | `6B6B72` ✗ | `gray.40` = `9C9CA6` |
| `TextTertiary` | `6C7773` | `6A6A75` ✗ | `gray.60` = `90909A` |
| `Success` | `2E7D5B` | `green.50` = `3DA35D` ✗ | ídem |
| `Danger` | `B3261E` | `red.60` = `D9534F` ✗ | ídem |
| `Warning` | `8A5A00` | `amber.60` = `B5790A` ✗ | ídem |
| `Info` | `245E9B` | `blue.55` = `3A7BD5` ✗ | ídem |
| `Brand` / `BrandAccent` / `OnBrand` | `0E7D6E` / `7FD9C9` / blanco | teal.60 / teal.30 / white ✓ | ✓ |

Hallazgo de contraste relevante: `TextTertiary` implementado (`6C7773`) es
prácticamente el valor `text-3` que Xauxa **rechazó en v10** por contraste
insuficiente (2.90:1 sobre `surface-3`; lo corrigió a `90909A` dark / `6A6A75`
light, 4.76:1 / 4.65:1). Agenda QR usa ese color para texto deshabilitado de
botones — conviene revisarlo con la auditoría de contraste de Xauxa antes de
dar por cerrada la paleta.

## 2. Tokens documentados que faltan en la implementación

- **Dimensión de tema oscuro** completa (surface/surface-2/surface-3/border/
  text-1/2/3 para dark). Es la brecha raíz: sin tokens dark, ningún componente
  Xauxa puede adaptarse y el `darkColorScheme` de `AgendaQrTheme` queda como
  infraestructura Material parcial (solo el chrome M3 del lab cambia).
- **Contenedores de estado** `-bg`: `success-bg`, `danger-bg`, `warning-bg`,
  `info-bg`. El banner actual aproxima danger-bg con `Danger.copy(alpha = 0.10f)`.
- **Colores por tipo de QR**: `type-url`, `type-email`, `type-text` (usados por
  XauxaXcan para color-coding de listas; Agenda QR aún no los consume).
- **Anillo de foco**: `focus.ring-color` (= brand-accent), `ring-offset` (2px).
  `XauxaMetrics.Focus` (2dp) existe; falta el color semántico del anillo y el
  offset. Xauxa §10 identifica el foco visible como el gap más serio contra
  Material/Carbon.
- **Motion**: `duration.short/medium/long` y `easing.standard/emphasized/
  decelerate` no existen como tokens Compose. Ningún componente los consume
  aún (invariante 06/07 pendiente de poder aplicarse).
- **Familias tipográficas**: Xauxa documenta Archivo (display) y Roboto (UI);
  `XauxaType` solo define tamaños.

## 3. Componentes documentados (C01–C26) vs implementados

| Xauxa | Componente | Estado en `core:ui` |
|---|---|---|
| C1 | Tile | `XauxaTile` — superficie plana con borde; sin regiones head/body/stats ni alto mínimo 92 del spec |
| C1b | Tile tipográfico | No implementado |
| C2 | Botón | `XauxaPrimaryButton`/`XauxaSecondaryButton`/`XauxaTextAction`; sin estado de carga (la matriz §10 lo exige) y sin `enabled` en el textual |
| C3 | Fila de lista | No implementado |
| C4 | Badge | No implementado |
| C5 | Scanner viewport | No implementado en `core:ui` (el scanner vive en presentation) |
| C6 | Upload zone | No implementado |
| C7 | Notification / toast | No implementado |
| C8 | Settings select / toggle | No implementado |
| C9 | Stats block | No implementado |
| C10 | Error page | Parcialmente cubierto por `XauxaEmptyState` (sin tono de error) |
| C11 | Icon button | No implementado (los iconos Material están vetados por la compuerta) |
| C12 | Botón de acción de tile | Parcialmente cubierto por `XauxaTextAction` |
| C13 | Status badge | `XauxaStatusBanner` — solo neutro/peligro; sin tonos semánticos `-bg` |
| C14 | Filter pills | No implementado (el chrome del lab usa M3 `FilterChip` como infraestructura) |
| C15 | Empty state | `XauxaEmptyState` ✓ |
| C16 | Overflow footer | No implementado |
| C17 | Inline result banner | Cubierto por `XauxaStatusBanner` (misma limitación de tonos) |
| C18 | Loading spinner | `XauxaLoading` — indeterminado solo |
| C19 | Search bar | No implementado (el chrome del lab usa M3 `OutlinedTextField`) |
| C20 | Text input / Textarea | No implementado |
| C21 | Favorite toggle | `XauxaFavoriteIndicator` — indicador de solo presentación, sin evento |
| C22 | Category chip | No implementado |
| C23 | Destination card | No implementado (hoy se compone con `XauxaTile`) |
| C24 | Modal / Dialog | No implementado |
| C25 | Skeleton loading | No implementado |
| C26 | Load more / Paginación | No implementado |

### Kotlin del ZIP no adoptado

`kotlin-compose/` del ZIP trae `XauxaLiveTile`, `XauxaCommandBar`,
`XauxaTurnstileNav` y `ReducedMotion` (§05/§07/§08 del checklist KMP). El propio
README advierte que no fueron compilados contra un proyecto real. No se
copian a `core:ui` en esta pasada; quedan como candidatos cuando producto
los requiera, previa compilación y validación en el laboratorio.

### Decisión vigente que limita variantes

ADR-0002 (v12) **rechazó** la variación de acento por categoría de tile: un
solo acento de marca, resto semántico. El laboratorio no introduce tonos por
categoría.

## 4. Tema oscuro: estado real

- `AgendaQrTheme(darkTheme = true)` instala un `darkColorScheme` Material
  construido con los mismos tokens claros fijos (p. ej. `background =
  XauxaColor.TextPrimary`). Los componentes Xauxa leen `XauxaColor.*`
  directamente, por lo que **no cambian** con el tema.
- En el laboratorio, el toggle de preview oscuro oscurece el chrome Material
  (que sí consume `MaterialTheme`) y deja los componentes bajo inspección con
  su apariencia clara. El inspector lo marca explícitamente como la brecha
  real de tokens, no como un fallo del lab.
- Soporte honesto por componente en el catálogo: `NOT_SUPPORTED` para los 11
  componentes; `NOT_APPLICABLE` para fundamentos no cromáticos.

## 5. Trazabilidad del catálogo del laboratorio

| Entrada del catálogo | Fuente de verdad del código | Referencia Xauxa |
|---|---|---|
| `XauxaScreen` | `core/ui/.../components/XauxaComponents.kt` | §03 superficies planas, regla 02/03 |
| `XauxaSection` | ídem | §03, grid de página §08 |
| `XauxaTile` | ídem | C1 |
| `XauxaPrimaryButton` / `XauxaSecondaryButton` / `XauxaTextAction` | ídem | C2 (+§10 estados) |
| `XauxaStatusBanner` | ídem | C13/C17 |
| `XauxaLoading` | ídem | C18 |
| `XauxaEmptyState` | ídem | C15 |
| `XauxaFavoriteIndicator` | ídem | C21 |
| `XauxaQrPreview` | expect + actuals Android/iOS/Wasm (web muestra placeholder; PENDIENTE) | C23/P3 (patrón QR) |
| `XauxaColor` / `XauxaSpacing` / `XauxaMetrics` / `XauxaType` | `XauxaTokens.kt` | §01 color, §04 spacing, §06 métricas, §02 tipografía, §07 breakpoints |

Cada entrada del inspector enlaza los tokens que consume con `XauxaTokenIndex`
(modelo del lab), que valida contra la capa de tokens real en las pruebas.

## 6. Conclusiones accionables

1. Decidir si la paleta implementada se alinea a Xauxa v13 light o se registra
   como paleta propia de Agenda QR; hoy es una mezcla sin decisión explícita.
2. Añadir la dimensión dark a los tokens es prerrequisito para cualquier
   declaración de soporte de tema oscuro.
3. Priorizar las brechas de API que afectan a pantallas previstas: campo de
   texto (C20), chips/filtros (C14/C22), modal (C24), tonos del banner con `-bg`.
4. Habilitar Roborazzi para fijar los previews del lab como evidencia visual
   automatizada (hoy no existe infraestructura de screenshot tests).

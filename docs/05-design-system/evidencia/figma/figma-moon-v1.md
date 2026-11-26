# Moon Design System v1 — especificación extraída del archivo Figma

Fuente: `Moon Design System v1 (Community).fig` (v1 oficial de Moon). Extraído directamente del archivo (variables, estilos y componentes publicados), no de capturas. Valores en px/dp. La fidelidad en AgendaQr aplica a **geometría, densidad, radios, estados y área táctil**; colores, tipografía e iconos siguen la regla vigente del repo (ADR-0044), por lo que las secciones de color y tipografía son solo referencia.

## 1. Tokens de geometría

| Token | Valor |
|---|---|
| `Surface border radius/--radius-s-xs` | 4 |
| `Surface border radius/--radius-s-sm` | 8 |
| `Surface border radius/--radius-s-md` | 12 |
| `Surface border radius/--radius-s-lg` | 16 |
| `Surface border radius/--radius-s-rounded` | 9999 |
| `Interactive border radius/--radius-i-xs` | 4 |
| `Interactive border radius/--radius-i-sm` | 8 |
| `Interactive border radius/--radius-i-md` | 12 |
| `Interactive border radius/--radius-i-lg` | 16 |
| `Interactive border radius/--radius-i-rounded` | 9999 |
| `Paddings & Gaps/p-1` | 4 |
| `Paddings & Gaps/p-2` | 8 |
| `Paddings & Gaps/p-3` | 12 |
| `Paddings & Gaps/p-4` | 16 |
| `Paddings & Gaps/p-5` | 20 |
| `Paddings & Gaps/p-6` | 24 |
| `Paddings & Gaps/p-7` | 28 |
| `Paddings & Gaps/p-8` | 32 |
| `Paddings & Gaps/p-9` | 36 |
| `Paddings & Gaps/p-10` | 40 |
| `Paddings & Gaps/p-11` | 44 |
| `Paddings & Gaps/p-12` | 48 |

Alturas de control (primitivos): h-4=16, h-6=24, h-8=32, h-10=40, h-12=48, h-14=56, h-16=64. Escala de padding/gap: 0, 4, 8, 12, 16, 20, 24 (px/gap/pl/pr/pt/pb de 0 a 6).

## 2. Colores (referencia; modo MDS Light / MDS Dark)

| Token | Light | Dark |
|---|---|---|
| `Main/piccolo` | #4E46B4 | #4E46B4 |
| `Main/hit` | #40A69F | #40A69F |
| `Main/beerus` | #E2E2E2 | #444444 |
| `Main/goku` | #F5F5F5 | #0B0B0B |
| `Main/gohan` | #FFFFFF | #1F1F1F |
| `Main/bulma` | #000000 | #FFFFFF |
| `Main/trunks` | #595D62 | #595D62 |
| `Main/goten` | #FFFFFF | #FFFFFF |
| `Main/popo` | #000000 | #000000 |
| `Main/jiren` | #4E46B4 @0.12 | #4E46B4 @0.32 |
| `Main/heles` | #000000 @0.04 | #FFFFFF @0.08 |
| `Main/zeno` | #000000 @0.56 | #000000 @0.56 |
| `Supportive/krillin` | #FFB319 | #FFB319 |
| `Supportive/krillin-60` | #FFB319 @0.56 | #FFB319 @0.56 |
| `Supportive/krillin-10` | #FFB319 @0.12 | #FFB319 @0.12 |
| `Supportive/chichi` | #FF4E64 | #FF4E64 |
| `Supportive/chichi-60` | #FF4E64 @0.56 | #FF4E64 @0.56 |
| `Supportive/chichi-10` | #FF4E64 @0.12 | #FF4E64 @0.12 |
| `Supportive/roshi` | #2E7D32 | #2E7D32 |
| `Supportive/roshi-60` | #2E7D32 @0.56 | #2E7D32 @0.56 |
| `Supportive/roshi-10` | #2E7D32 @0.12 | #2E7D32 @0.12 |
| `Supportive/dodoria` | #D33030 | #D33030 |
| `Supportive/dodoria-60` | #D33030 @0.56 | #D33030 @0.56 |
| `Supportive/dodoria-10` | #D33030 @0.12 | #D33030 @0.12 |
| `Supportive/cell` | #95F1D5 | #95F1D5 |
| `Supportive/cell-60` | #95F1D5 @0.56 | #95F1D5 @0.56 |
| `Supportive/cell-10` | #95F1D5 @0.12 | #95F1D5 @0.12 |
| `Supportive/raditz` | #B3804A | #B3804A |
| `Supportive/raditz-60` | #B3804A @0.56 | #B3804A @0.56 |
| `Supportive/raditz-10` | #B3804A @0.12 | #B3804A @0.12 |
| `Supportive/whis` | #3448F0 | #3448F0 |
| `Supportive/whis-60` | #3448F0 @0.56 | #3448F0 @0.56 |
| `Supportive/whis-10` | #3448F0 @0.12 | #3448F0 @0.12 |
| `Supportive/frieza` | #5C33CF | #5C33CF |
| `Supportive/frieza-60` | #5C33CF @0.56 | #5C33CF @0.56 |
| `Supportive/frieza-10` | #5C33CF @0.12 | #5C33CF @0.12 |
| `Supportive/nappa` | #725550 | #725550 |
| `Supportive/nappa-60` | #725550 @0.56 | #725550 @0.56 |
| `Supportive/nappa-10` | #725550 @0.12 | #725550 @0.12 |

Roles clave: `piccolo` = primario; `bulma` = texto principal; `trunks` = texto secundario; `gohan` = superficie; `goku` = fondo de página; `beerus` = borde; `heles` = overlay de hover; `jiren` = primario @12 % (fondo seleccionado); `zeno` = scrim de overlays (negro @56 %); `krillin`=aviso, `chichi`=peligro suave, `roshi`=éxito, `dodoria`=error.

## 3. Tipografía (referencia)

Familias del archivo: Averta Std (UI) y DM Sans (display). Escala observada (tamaño/interlineado): 12/16, 14/20, 14/24, 16/24, 18/24–28, 20/28–32, 24/32, 32/40, 48/56. Pesos: Regular y Semibold. Botones: xs 12/16; sm y md 14/24; lg y xl 16/24, siempre Semibold. Inputs: sm y md 14/24; lg 16/24, Regular.

## 4. Sombras

Elevación (Y/blur/spread, negro): 0/1/0 @0.20 como borde sutil + 6/6/-6 @0.16 (sm), 12/12/-6 @0.16 (md), 8/24/-6 @0.16 y 0/32/-8 @0.12 (lg), 32/32/-8 @0.08 (xl). Variantes oscuras con alfa 0.56–0.88.

## 5. Estados e interacción

- **Hover:** color base + overlay `heles` (negro @4 % en claro, blanco @8 % en oscuro). Aplica a cualquier elemento interactivo.
- **Pressed/active:** el elemento se reduce un poco de escala con animación (ease in/out, 0,2 s).
- **Focus (botón Fill):** anillo doble: 2 px blanco y 6 px de `piccolo` @12 % alrededor del control.
- **Disabled y error:** ver variantes por componente.

Anillos de foco por componente (de las variantes `focus`):

- Button: drop shadow spread 6 blur 0 #4E46B4@0.12; drop shadow spread 2 blur 0 #FFFFFF

## 6. Componentes

Cada fila agrupa variantes con la misma geometría. `Pad` = arriba/derecha/abajo/izquierda. Los anchos de las variantes dependen del contenido y no se listan.

### Button

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 24 | 4 | H 4 | 4/8/4/4 | Averta Std Semibold 12px/16px | — | size xs · Size=xs, Icon=Left, Type=Fill, state=rest (2) |
| 24 | 4 | H 0 | 4/8/4/8 | Averta Std Semibold 12px/16px | — | size xs · Size=xs, Icon=None, Type=Fill, state=rest (2) |
| 24 | 4 | H 4 | 4/8/4/8 | Averta Std Semibold 12px/16px | — | size xs · Size=xs, Icon=Right, Type=Fill, state=rest (1) |
| 24 | 4 | H 0 | 4/4/4/4 | — | — | size xs · Size=xs, Icon=Center, Type=Fill, state=rest (6) |
| 24 | 4 | H 4 | 4/8/4/4 | Averta Std Semibold 12px/16px | #595D62 1px | size xs · Size=xs, Icon=Left, Type=Outline, state=rest (1) |
| 24 | 4 | H 0 | 4/8/4/8 | Averta Std Semibold 12px/16px | #595D62 1px | size xs · Size=xs, Icon=None, Type=Outline, state=rest (1) |
| 24 | 4 | H 4 | 4/8/4/8 | Averta Std Semibold 12px/16px | #595D62 1px | size xs · Size=xs, Icon=Right, Type=Outline, state=rest (1) |
| 24 | 4 | H 0 | 4/4/4/4 | — | #595D62 1px | size xs · Size=xs, Icon=Center, Type=Outline, state=rest (3) |
| 24 | 4 | H 4 | 4/8/4/4 | DM Sans Bold 12px/16px | — | size xs · Size=xs, Icon=Left, Type=Fill, state=hover (4) |
| 24 | 4 | H 0 | 4/8/4/8 | DM Sans Bold 12px/16px | — | size xs · Size=xs, Icon=None, Type=Fill, state=hover (4) |
| 24 | 4 | H 4 | 4/8/4/8 | DM Sans Bold 12px/16px | — | size xs · Size=xs, Icon=Right, Type=Fill, state=hover (2) |
| 24 | 4 | H 4 | 4/8/4/4 | DM Sans Bold 12px/16px | #595D62 1px | size xs · Size=xs, Icon=Left, Type=Outline, state=hover (2) |
| 24 | 4 | H 0 | 4/8/4/8 | DM Sans Bold 12px/16px | #595D62 1px | size xs · Size=xs, Icon=None, Type=Outline, state=hover (2) |
| 24 | 4 | H 4 | 4/8/4/8 | DM Sans Bold 12px/16px | #595D62 1px | size xs · Size=xs, Icon=Right, Type=Outline, state=hover (2) |
| 32 | 4 | H 4 | 8/4/8/8 | Averta Std Semibold 12px/16px | — | size xs · Size=xs, Icon=Right, Type=Ghost, state=rest (1) |
| 32 | 4 | H 4 | 8/4/8/8 | DM Sans Bold 12px/16px | — | size xs · Size=xs, Icon=Right, Type=Ghost, state=hover (2) |
| 32 | 8 | H 4 | 4/12/4/4 | Averta Std Semibold 14px/24px | — | size sm · Size=sm, Icon=Left, Type=Fill, state=rest (2) |
| 32 | 8 | H 4 | 4/12/4/4 | Averta Std Semibold 14px/24px | #595D62 1px | size sm · Size=sm, Icon=Left, Type=Outline, state=rest (1) |
| 32 | 8 | H 0 | 4/12/4/12 | Averta Std Semibold 14px/24px | — | size sm · Size=sm, Icon=None, Type=Fill, state=rest (2) |
| 32 | 8 | H 0 | 4/12/4/12 | Averta Std Semibold 14px/24px | #595D62 1px | size sm · Size=sm, Icon=None, Type=Outline, state=rest (1) |
| 32 | 8 | H 4 | 4/4/4/12 | Averta Std Semibold 14px/24px | — | size sm · Size=sm, Icon=Right, Type=Fill, state=rest (2) |
| 32 | 8 | H 4 | 4/4/4/12 | Averta Std Semibold 14px/24px | #595D62 1px | size sm · Size=sm, Icon=Right, Type=Outline, state=rest (1) |
| 32 | 8 | H 0 | 4/4/4/4 | — | — | size sm · Size=sm, Icon=Center, Type=Fill, state=rest (6) |
| 32 | 8 | H 0 | 4/4/4/4 | — | #595D62 1px | size sm · Size=sm, Icon=Center, Type=Outline, state=rest (3) |
| 32 | 8 | H 4 | 4/12/4/4 | DM Sans Bold 14px/24px | — | size sm · Size=sm, Icon=Left, Type=Fill, state=hover (4) |
| 32 | 8 | H 4 | 4/12/4/4 | DM Sans Bold 14px/24px | #595D62 1px | size sm · Size=sm, Icon=Left, Type=Outline, state=hover (2) |
| 32 | 8 | H 0 | 4/12/4/12 | DM Sans Bold 14px/24px | — | size sm · Size=sm, Icon=None, Type=Fill, state=hover (4) |
| 32 | 8 | H 0 | 4/12/4/12 | DM Sans Bold 14px/24px | #595D62 1px | size sm · Size=sm, Icon=None, Type=Outline, state=hover (2) |
| 32 | 8 | H 4 | 4/4/4/12 | DM Sans Bold 14px/24px | — | size sm · Size=sm, Icon=Right, Type=Fill, state=hover (4) |
| 32 | 8 | H 4 | 4/4/4/12 | DM Sans Bold 14px/24px | #595D62 1px | size sm · Size=sm, Icon=Right, Type=Outline, state=hover (2) |
| 40 | 8 | H 8 | 8/16/8/8 | Averta Std Semibold 14px/24px | — | size md · Size=md, Icon=Left, Type=Fill, state=rest (2) |
| 40 | 8 | H 8 | 8/16/8/8 | Averta Std Semibold 14px/24px | #595D62 1px | size md · Size=md, Icon=Left, Type=Outline, state=rest (1) |
| 40 | 8 | H 0 | 8/16/8/16 | Averta Std Semibold 14px/24px | — | size md · Size=md, Icon=None, Type=Fill, state=rest (2) |
| 40 | 8 | H 0 | 8/16/8/16 | Averta Std Semibold 14px/24px | #595D62 1px | size md · Size=md, Icon=None, Type=Outline, state=rest (1) |
| 40 | 8 | H 8 | 8/8/8/16 | Averta Std Semibold 14px/24px | — | size md · Size=md, Icon=Right, Type=Fill, state=rest (2) |
| 40 | 8 | H 8 | 8/8/8/16 | Averta Std Semibold 14px/24px | #595D62 1px | size md · Size=md, Icon=Right, Type=Outline, state=rest (1) |
| 40 | 8 | H 0 | 8/8/8/8 | — | — | size md · Size=md, Icon=Center, Type=Fill, state=rest (6) |
| 40 | 8 | H 0 | 8/8/8/8 | — | #595D62 1px | size md · Size=md, Icon=Center, Type=Outline, state=rest (3) |
| 40 | 8 | H 8 | 8/16/8/8 | DM Sans Bold 14px/24px | — | size md · Size=md, Icon=Left, Type=Fill, state=hover (4) |
| 40 | 8 | H 8 | 8/16/8/8 | DM Sans Bold 14px/24px | #595D62 1px | size md · Size=md, Icon=Left, Type=Outline, state=hover (2) |
| 40 | 8 | H 0 | 8/16/8/16 | DM Sans Bold 14px/24px | — | size md · Size=md, Icon=None, Type=Fill, state=hover (4) |
| 40 | 8 | H 0 | 8/16/8/16 | DM Sans Bold 14px/24px | #595D62 1px | size md · Size=md, Icon=None, Type=Outline, state=hover (2) |
| 40 | 8 | H 8 | 8/8/8/16 | DM Sans Bold 14px/24px | — | size md · Size=md, Icon=Right, Type=Fill, state=hover (4) |
| 40 | 8 | H 8 | 8/8/8/16 | DM Sans Bold 14px/24px | #595D62 1px | size md · Size=md, Icon=Right, Type=Outline, state=hover (2) |
| 48 | 8 | H 12 | 12/16/12/12 | Averta Std Semibold 16px/24px | — | size lg · Size=lg, Icon=Left, Type=Fill, state=rest (2) |
| 48 | 8 | H 12 | 12/16/12/12 | Averta Std Semibold 16px/24px | #595D62 1px | size lg · Size=lg, Icon=Left, Type=Outline, state=rest (3) |
| 48 | 8 | H 0 | 12/16/12/16 | Averta Std Semibold 16px/24px | — | size lg · Size=lg, Icon=None, Type=Fill, state=rest (2) |
| 48 | 8 | H 0 | 12/16/12/16 | Averta Std Semibold 16px/24px | #595D62 1px | size lg · Size=lg, Icon=None, Type=Outline, state=rest (1) |
| 48 | 8 | H 12 | 12/12/12/16 | Averta Std Semibold 16px/24px | — | size lg · Size=lg, Icon=Right, Type=Fill, state=rest (2) |
| 48 | 8 | H 12 | 12/12/12/16 | Averta Std Semibold 16px/24px | #595D62 1px | size lg · Size=lg, Icon=Right, Type=Outline, state=rest (3) |
| 48 | 8 | H 0 | 12/12/12/12 | — | — | size lg · Size=lg, Icon=Center, Type=Fill, state=rest (6) |
| 48 | 8 | H 0 | 12/12/12/12 | — | #595D62 1px | size lg · Size=lg, Icon=Center, Type=Outline, state=rest (3) |
| 48 | 8 | H 12 | 12/16/12/12 | DM Sans Bold 16px/24px | — | size lg · Size=lg, Icon=Left, Type=Fill, state=hover (4) |
| 48 | 8 | H 0 | 12/16/12/16 | DM Sans Bold 16px/24px | — | size lg · Size=lg, Icon=None, Type=Fill, state=hover (4) |
| 48 | 8 | H 0 | 12/16/12/16 | DM Sans Bold 16px/24px | #595D62 1px | size lg · Size=lg, Icon=None, Type=Outline, state=hover (2) |
| 48 | 8 | H 12 | 12/12/12/16 | DM Sans Bold 16px/24px | — | size lg · Size=lg, Icon=Right, Type=Fill, state=hover (4) |
| 56 | 12 | H 16 | 16/24/16/16 | Averta Std Semibold 16px/24px | #595D62 1px | size xl · Size=xl, Icon=Left, Type=Outline, state=rest (1) |
| 56 | 12 | H 0 | 16/24/16/24 | Averta Std Semibold 16px/24px | #595D62 1px | size xl · Size=xl, Icon=None, Type=Outline, state=rest (1) |
| 56 | 12 | H 16 | 16/16/16/24 | Averta Std Semibold 16px/24px | #595D62 1px | size xl · Size=xl, Icon=Right, Type=Outline, state=rest (1) |
| 56 | 12 | H 16 | 16/24/16/16 | Averta Std Semibold 16px/24px | — | size xl · Size=xl, Icon=Left, Type=Fill, state=rest (2) |
| 56 | 12 | H 0 | 16/24/16/24 | Averta Std Semibold 16px/24px | — | size xl · Size=xl, Icon=None, Type=Fill, state=rest (2) |
| 56 | 12 | H 16 | 16/16/16/24 | Averta Std Semibold 16px/24px | — | size xl · Size=xl, Icon=Right, Type=Fill, state=rest (2) |
| 56 | 12 | H 0 | 16/16/16/16 | — | — | size xl · Size=xl, Icon=Center, Type=Fill, state=rest (6) |
| 56 | 12 | H 0 | 16/16/16/16 | — | #595D62 1px | size xl · Size=xl, Icon=Center, Type=Outline, state=rest (3) |
| 56 | 12 | H 16 | 16/24/16/16 | DM Sans Bold 16px/24px | #595D62 1px | size xl · Size=xl, Icon=Left, Type=Outline, state=hover (2) |
| 56 | 12 | H 0 | 16/24/16/24 | DM Sans Bold 16px/24px | #595D62 1px | size xl · Size=xl, Icon=None, Type=Outline, state=hover (2) |
| 56 | 12 | H 16 | 16/16/16/24 | DM Sans Bold 16px/24px | #595D62 1px | size xl · Size=xl, Icon=Right, Type=Outline, state=hover (2) |
| 56 | 12 | H 16 | 16/24/16/16 | DM Sans Bold 16px/24px | — | size xl · Size=xl, Icon=Left, Type=Fill, state=hover (4) |
| 56 | 12 | H 0 | 16/24/16/24 | DM Sans Bold 16px/24px | — | size xl · Size=xl, Icon=None, Type=Fill, state=hover (4) |
| 56 | 12 | H 16 | 16/16/16/24 | DM Sans Bold 16px/24px | — | size xl · Size=xl, Icon=Right, Type=Fill, state=hover (4) |

### Text input

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 32 | — | V 4 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size sm · Type=Text, Size=sm, Floating label=False, Input Label=False, Trailing  (18) |
| 32 | — | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size sm · Type=Text, Size=sm, Floating label=False, Input Label=False, Trailing  (3) |
| 40 | 8 | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size md · Type=Text, Size=md, Floating label=False, Input Label=False, Trailing  (15) |
| 40 | — | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size md · Type=Search, Size=md, Floating label=False, Input Label=False, Trailin (6) |
| 48 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size lg · Type=Text, Size=lg, Floating label=False, Input Label=False, Trailing  (21) |
| 56 | 8 | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size xl · Type=Text, Size=xl, Floating label=False, Input Label=False, Trailing  (3) |
| 56 | 8 | V 8 | 0/0/0/0 | DM Sans Regular 12px/12px | — | size xl · Type=Text, Size=xl, Floating label=True, Input Label=False, Trailing i (3) |
| 56 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size xl · Type=Password, Size=xl, Floating label=False, Input Label=False, Trail (15) |
| 56 | — | V 8 | 0/0/0/0 | DM Sans Regular 12px/12px | — | size xl · Type=Password, Size=xl, Floating label=True, Input Label=False, Traili (15) |
| 60 | — | V 4 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size sm · Type=Text, Size=sm, Floating label=False, Input Label=True, Trailing i (21) |
| 72 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size md · Type=Text, Size=md, Floating label=False, Input Label=True, Trailing i (21) |
| 80 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size lg · Type=Text, Size=lg, Floating label=False, Input Label=True, Trailing i (21) |

### Text input group

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 56 | 8 | H 0 | 0/0/0/0 | — | — | Direction=Horizontal, Nr of fields=2, Select=False, State=Default (2) |
| 104 | 8 | V 8 | 0/0/0/0 | — | — | Direction=Horizontal, Nr of fields=2, Select=False, State=Active (4) |
| 112.0 | 8 | V 0 | 0/0/0/0 | — | — | Direction=Vertical, Nr of fields=2, Select=False, State=Default (2) |
| 160.0 | 8 | V 8 | 0/0/0/0 | — | — | Direction=Vertical, Nr of fields=2, Select=False, State=Active (4) |

### Textarea

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 132 | 8 | — | — | Averta Std Regular 16px/24px | #E2E2E2 1px | Scaling=True, Button=False (2) |

### Search

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 40 | — | V 0 | 0/0/0/0 | — | — | Clear=True, Loading=False, Empty state=False, Mobile=False, Trigger=Tr (1) |
| 324 | — | — | — | Averta Std Regular 16px/24px | — | Clear=False, Loading=False, Empty state=False, Mobile=False, Trigger=F (4) |
| 324 | — | — | — | Averta Std Regular 14px/20px | — | Clear=True, Loading=False, Empty state=True, Mobile=False, Trigger=Fal (1) |

### Select

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 32 | — | V 4 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Floating label=False, Input Label=False, Leading icon=False,  (2) |
| 32 | — | V 4 | 0/0/0/0 | Averta Std Regular 12px/16px | — | size sm · Size=sm, Floating label=False, Input Label=False, Leading icon=False,  (1) |
| 40 | 8 | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size md · Size=md, Floating label=False, Input Label=False, Leading icon=False,  (3) |
| 48 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size lg · Size=lg, Floating label=False, Input Label=False, Leading icon=False,  (2) |
| 48 | — | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size lg · Size=lg, Floating label=False, Input Label=False, Leading icon=False,  (1) |
| 56 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size xl · Size=xl, Floating label=False, Input Label=False, Leading icon=False,  (2) |
| 56 | — | V 8 | 0/0/0/0 | Averta Std Regular 12px/12px | — | size xl · Size=xl, Floating label=True, Input Label=False, Leading icon=False, C (2) |
| 56 | — | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size xl · Size=xl, Floating label=False, Input Label=False, Leading icon=False,  (2) |
| 60 | — | V 4 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Floating label=False, Input Label=True, Leading icon=False, C (3) |
| 72 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size md · Size=md, Floating label=False, Input Label=True, Leading icon=False, C (3) |
| 80 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size lg · Size=lg, Floating label=False, Input Label=True, Leading icon=False, C (3) |

### Combobox

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 32 | — | V 4 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Floating label=False, Input Label=False, Selected counter=Fal (2) |
| 32 | — | V 8 | 0/0/0/0 | Averta Std Regular 12px/16px | — | size sm · Size=sm, Floating label=False, Input Label=False, Selected counter=Tru (1) |
| 32 | — | V 4 | 0/0/0/0 | Averta Std Regular 12px/16px | — | size sm · Size=sm, Floating label=False, Input Label=False, Selected counter=Tru (1) |
| 40 | 8 | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | #E2E2E2 1px | size md · Size=md, Floating label=False, Input Label=False, Selected counter=Fal (4) |
| 48 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size lg · Size=lg, Floating label=False, Input Label=False, Selected counter=Fal (2) |
| 48 | — | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size lg · Size=lg, Floating label=False, Input Label=False, Selected counter=Tru (2) |
| 56 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size xl · Size=xl, Floating label=False, Input Label=False, Selected counter=Fal (2) |
| 56 | — | V 8 | 0/0/0/0 | Averta Std Regular 12px/12px | — | size xl · Size=xl, Floating label=True, Input Label=False, Selected counter=Fals (2) |
| 56 | — | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size xl · Size=xl, Floating label=False, Input Label=False, Selected counter=Tru (4) |
| 60 | — | V 4 | 0/0/0/0 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Floating label=False, Input Label=True, Selected counter=Fals (4) |
| 72 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size md · Size=md, Floating label=False, Input Label=True, Selected counter=Fals (4) |
| 80 | — | V 8 | 0/0/0/0 | Averta Std Regular 16px/24px | — | size lg · Size=lg, Floating label=False, Input Label=True, Selected counter=Fals (4) |

### Dropdown

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 198 | 12 | V 4 | 4/4/4/4 | — | — | Type=Single, Custom=True (1) |
| 248 | 12 | V 0 | 4/4/4/4 | — | — | Type=Single, Custom=False (1) |

### Menu item

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 40 | 8 | V 8 | 8/8/8/12 | Averta Std Regular 14px/24px | — | Label=False, Meta=False, Multi line=False, Leading element=False, Trai (1) |
| 40 | 8 | H 8 | 8/8/8/12 | Averta Std Regular 14px/24px | — | Label=True, Meta=False, Multi line=False, Leading element=False, Trail (4) |
| 40 | 8 | H 8 | 8/12/8/12 | Averta Std Regular 14px/24px | — | Label=False, Meta=True, Multi line=False, Leading element=False, Trail (1) |
| 40 | 8 | H 8 | 8/12/8/8 | Averta Std Regular 14px/24px | — | Label=False, Meta=True, Multi line=False, Leading element=True, Traili (4) |
| 40 | 8 | H 8 | 8/8/8/8 | Averta Std Regular 14px/24px | — | Label=False, Meta=False, Multi line=False, Leading element=True, Trail (12) |
| 56 | 8 | H 8 | 8/12/8/12 | Averta Std Regular 14px/24px | — | Label=False, Meta=False, Multi line=True, Leading element=False, Trail (2) |
| 56 | 8 | H 8 | 8/8/8/8 | Averta Std Regular 14px/24px | — | Label=False, Meta=False, Multi line=True, Leading element=True, Traili (12) |
| 56 | 8 | H 8 | 8/12/8/8 | Averta Std Regular 14px/24px | — | Label=False, Meta=True, Multi line=True, Leading element=True, Trailin (4) |
| 56 | 8 | H 8 | 8/8/8/12 | Averta Std Regular 14px/24px | — | Label=False, Meta=False, Multi line=True, Leading element=False, Trail (4) |

### Checkbox

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 24 | — | — | — | — | — | Variant=Selected, Label=False, Type=Fill (4) |
| 24 | — | H 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | Variant=Not-selected, Label=True, Type=Stroke (4) |

### Radio button

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 24 | — | — | — | — | — | Type=Selected, Label right=False, Custom colour=False (2) |
| 24 | — | H 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | Type=Not-Selected, Label right=True, Custom colour=False (2) |

### Switch

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 16 | 100 | H 0 | 2/2/2/2 | — | — | size 2xs · State=On, Size=2xs, Icon=False, Custom colour=False (2) |
| 16 | 999 | — | — | — | — | size 2xs · State=On, Size=2xs, Icon=True, Custom colour=True (4) |
| 24 | 99 | — | — | — | — | size xs · State=On, Size=xs, Icon=False, Custom colour=True (2) |
| 24 | 999 | — | — | — | — | size xs · State=On, Size=xs, Icon=False, Custom colour=False (4) |
| 32 | 99 | — | — | — | — | size sm · State=Off, Size=sm, Icon=False, Custom colour=True (1) |
| 32 | 999 | — | — | — | — | size sm · State=On, Size=sm, Icon=False, Custom colour=False (5) |

### Segmented control

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 40 | 12 | H 4 | 4/4/4/4 | — | — | size sm · Items=2, Size=sm, Icon=False (6) |
| 48 | 12 | H 4 | 4/4/4/4 | — | — | size md · Items=2, Size=md, Icon=False (6) |

### Tabs

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 32 | 4 | H 0 | 4/12/4/12 | Averta Std Semibold 14px/24px | — | size sm · Type=Pill, Size=sm, Leading element=False, Trailing Element=False, Ico (1) |
| 32 | 4 | H 4 | 4/12/4/4 | Averta Std Semibold 14px/24px | — | size sm · Type=Pill, Size=sm, Leading element=True, Trailing Element=False, Icon (1) |
| 32 | 4 | H 4 | 4/4/4/4 | — | — | size sm · Type=Pill, Size=sm, Leading element=False, Trailing Element=False, Ico (1) |
| 32 | 4 | H 4 | 4/4/4/12 | Averta Std Semibold 14px/24px | — | size sm · Type=Pill, Size=sm, Leading element=False, Trailing Element=True, Icon (1) |
| 32 | 4 | H 4 | 4/4/4/4 | Averta Std Semibold 14px/24px | — | size sm · Type=Pill, Size=sm, Leading element=True, Trailing Element=True, Icon  (1) |
| 32 | — | H 0 | 4/8/4/8 | Averta Std Semibold 14px/24px | #4E46B4 1px | size sm · Type=Underline, Size=sm, Leading element=False, Trailing Element=False (1) |
| 32 | — | H 4 | 4/8/4/4 | Averta Std Semibold 14px/24px | #4E46B4 1px | size sm · Type=Underline, Size=sm, Leading element=True, Trailing Element=False, (1) |
| 32 | — | H 4 | 4/4/4/4 | — | #4E46B4 1px | size sm · Type=Underline, Size=sm, Leading element=False, Trailing Element=False (1) |
| 32 | — | H 4 | 4/4/4/8 | Averta Std Semibold 14px/24px | #4E46B4 1px | size sm · Type=Underline, Size=sm, Leading element=False, Trailing Element=True, (1) |
| 32 | — | H 4 | 4/4/4/4 | Averta Std Semibold 14px/24px | #4E46B4 1px | size sm · Type=Underline, Size=sm, Leading element=True, Trailing Element=True,  (1) |
| 32 | — | V 0 | 4/8/4/8 | Averta Std Semibold 14px/24px | — | size sm · Type=Default, Size=sm, Leading element=False, Trailing Element=False,  (1) |
| 32 | — | H 4 | 4/8/4/4 | Averta Std Semibold 14px/24px | — | size sm · Type=Default, Size=sm, Leading element=True, Trailing Element=False, I (1) |
| 32 | — | H 4 | 4/4/4/4 | — | — | size sm · Type=Default, Size=sm, Leading element=False, Trailing Element=False,  (1) |
| 32 | — | H 4 | 4/4/4/8 | Averta Std Semibold 14px/24px | — | size sm · Type=Default, Size=sm, Leading element=False, Trailing Element=True, I (1) |
| 32 | — | H 4 | 4/4/4/4 | Averta Std Semibold 14px/24px | — | size sm · Type=Default, Size=sm, Leading element=True, Trailing Element=True, Ic (1) |
| 40 | 8 | H 0 | 8/16/8/16 | Averta Std Semibold 14px/24px | — | size md · Type=Pill, Size=md, Leading element=False, Trailing Element=False, Ico (1) |
| 40 | 8 | H 8 | 8/16/8/8 | Averta Std Semibold 14px/24px | — | size md · Type=Pill, Size=md, Leading element=True, Trailing Element=False, Icon (1) |
| 40 | 8 | H 8 | 8/8/8/8 | — | — | size md · Type=Pill, Size=md, Leading element=False, Trailing Element=False, Ico (1) |
| 40 | 8 | H 8 | 8/8/8/16 | Averta Std Semibold 14px/24px | — | size md · Type=Pill, Size=md, Leading element=False, Trailing Element=True, Icon (1) |
| 40 | 8 | H 8 | 8/8/8/8 | Averta Std Semibold 14px/24px | — | size md · Type=Pill, Size=md, Leading element=True, Trailing Element=True, Icon  (1) |
| 40 | — | H 0 | 8/16/8/16 | Averta Std Semibold 14px/24px | #4E46B4 1px | size md · Type=Underline, Size=md, Leading element=False, Trailing Element=False (1) |
| 40 | — | H 8 | 8/16/8/8 | Averta Std Semibold 14px/24px | #4E46B4 1px | size md · Type=Underline, Size=md, Leading element=True, Trailing Element=False, (1) |
| 40 | — | H 8 | 8/8/8/8 | — | #4E46B4 1px | size md · Type=Underline, Size=md, Leading element=False, Trailing Element=False (1) |
| 40 | — | H 8 | 8/8/8/16 | Averta Std Semibold 14px/24px | #4E46B4 1px | size md · Type=Underline, Size=md, Leading element=False, Trailing Element=True, (1) |
| 40 | — | H 8 | 8/8/8/8 | Averta Std Semibold 14px/24px | #4E46B4 1px | size md · Type=Underline, Size=md, Leading element=True, Trailing Element=True,  (1) |
| 40 | — | V 0 | 8/16/8/16 | Averta Std Semibold 14px/24px | — | size md · Type=Default, Size=md, Leading element=False, Trailing Element=False,  (1) |
| 40 | — | H 8 | 8/16/8/8 | Averta Std Semibold 14px/24px | — | size md · Type=Default, Size=md, Leading element=True, Trailing Element=False, I (1) |
| 40 | — | H 8 | 8/8/8/8 | — | — | size md · Type=Default, Size=md, Leading element=False, Trailing Element=False,  (1) |
| 40 | — | H 8 | 8/8/8/16 | Averta Std Semibold 14px/24px | — | size md · Type=Default, Size=md, Leading element=False, Trailing Element=True, I (1) |
| 40 | — | H 8 | 8/8/8/8 | Averta Std Semibold 14px/24px | — | size md · Type=Default, Size=md, Leading element=True, Trailing Element=True, Ic (1) |

### Chip

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 32 | 4 | H 8 | 4/8/4/8 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Leading element=True, Trailing element=True, Text=True (1) |
| 32 | 4 | H 8 | 4/8/4/8 | — | — | size sm · Size=sm, Leading element=True, Trailing element=True, Text=False (1) |
| 32 | 4 | H 8 | 4/8/4/12 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Leading element=False, Trailing element=True, Text=True (1) |
| 32 | 4 | H 8 | 4/12/4/8 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Leading element=True, Trailing element=False, Text=True (1) |
| 32 | 4 | H 4 | 4/8/4/8 | Averta Std Regular 14px/24px | — | size sm · Size=sm, Leading element=False, Trailing element=False, Text=True (1) |
| 40 | 8 | H 8 | 8/8/8/8 | Averta Std Regular 14px/24px | — | size md · Size=md, Leading element=True, Trailing element=True, Text=True (1) |
| 40 | 8 | H 8 | 8/8/8/8 | — | — | size md · Size=md, Leading element=True, Trailing element=True, Text=False (1) |
| 40 | 8 | H 8 | 8/8/8/12 | Averta Std Regular 14px/24px | — | size md · Size=md, Leading element=False, Trailing element=True, Text=True (1) |
| 40 | 8 | H 8 | 8/12/8/8 | Averta Std Regular 14px/24px | — | size md · Size=md, Leading element=True, Trailing element=False, Text=True (1) |
| 40 | 8 | H 8 | 8/12/8/12 | Averta Std Regular 14px/24px | — | size md · Size=md, Leading element=False, Trailing element=False, Text=True (1) |

### Tag

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 16 | 4 | V 0 | 0/8/0/8 | Averta Std Bold 9px/16px | — | size 2xs · Size=2xs, Icon=None, uppercase=true (1) |
| 16 | 4 | V 0 | 0/8/0/8 | Averta Std Regular 10px/16px | — | size 2xs · Size=2xs, Icon=None, uppercase=false (1) |
| 16 | 4 | H 4 | 0/8/0/2 | Averta Std Bold 9px/16px | — | size 2xs · Size=2xs, Icon=Left, uppercase=true (1) |
| 16 | 4 | H 4 | 0/8/0/2 | Averta Std Regular 10px/16px | — | size 2xs · Size=2xs, Icon=Left, uppercase=false (1) |
| 16 | 4 | H 4 | 0/2/0/8 | Averta Std Bold 9px/16px | — | size 2xs · Size=2xs, Icon=Right, uppercase=true (1) |
| 16 | 4 | H 4 | 0/2/0/8 | Averta Std Regular 10px/16px | — | size 2xs · Size=2xs, Icon=Right, uppercase=false (1) |
| 16 | 4 | H 4 | 0/2/0/2 | Averta Std Bold 9px/16px | — | size 2xs · Size=2xs, Icon=Both, uppercase=true (1) |
| 16 | 4 | H 4 | 0/2/0/2 | Averta Std Regular 10px/16px | — | size 2xs · Size=2xs, Icon=Both, uppercase=false (1) |
| 24 | 4 | H 4 | 4/8/4/4 | Averta Std Semibold 10px/16px | — | size xs · Size=xs, Icon=Left, uppercase=true (1) |
| 24 | 4 | H 4 | 4/8/4/4 | Averta Std Regular 12px/16px | — | size xs · Size=xs, Icon=Left, uppercase=false (1) |
| 24 | 4 | H 4 | 4/4/4/8 | Averta Std Semibold 10px/16px | — | size xs · Size=xs, Icon=Right, uppercase=true (1) |
| 24 | 4 | H 4 | 4/4/4/8 | Averta Std Regular 12px/16px | — | size xs · Size=xs, Icon=Right, uppercase=false (1) |
| 24 | 4 | H 4 | 4/4/4/4 | Averta Std Semibold 10px/16px | — | size xs · Size=xs, Icon=Both, uppercase=true (1) |
| 24 | 4 | H 4 | 4/4/4/4 | Averta Std Regular 12px/16px | — | size xs · Size=xs, Icon=Both, uppercase=false (1) |
| 24 | 4 | V 0 | 4/8/4/8 | Averta Std Semibold 10px/16px | — | size xs · Size=xs, Icon=None, uppercase=true (1) |
| 24 | 4 | V 0 | 4/8/4/8 | Averta Std Regular 12px/16px | — | size xs · Size=xs, Icon=None, uppercase=false (1) |

### Avatar

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 24 | 4 | — | — | — | — | size xs · Type=Image, Size=xs (1) |
| 24 | 99 | — | — | Averta Std Semibold 10px/16px | — | size xs · Type=Initials, Size=xs (1) |
| 32 | 8 | — | — | — | — | size sm · Type=Image, Size=sm (1) |
| 32 | 99 | — | — | Averta Std Semibold 12px/16px | — | size sm · Type=Initials, Size=sm (1) |
| 40 | 8 | — | — | — | — | size md · Type=Image, Size=md (1) |
| 40 | 99 | — | — | Averta Std Semibold 14px/24px | — | size md · Type=Initials, Size=md (1) |
| 48 | 12 | — | — | — | — | size lg · Type=Image, Size=lg (1) |
| 48 | 99 | — | — | Averta Std Semibold 16px/24px | — | size lg · Type=Initials, Size=lg (1) |
| 56 | 99 | — | — | Averta Std Semibold 16px/24px | — | size xl · Type=Initials, Size=xl (1) |
| 56 | 9999 | — | — | — | — | size xl · Type=Image, Size=xl (1) |
| 64 | 12 | — | — | — | — | size 2xl · Type=Image, Size=2xl (1) |
| 64 | 99 | — | — | Averta Std Semibold 20px/32px | — | size 2xl · Type=Initials, Size=2xl (1) |

### Accordion

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 32 | 8 | V 8 | 8/8/8/8 | Averta Std Semibold 12px/16px | — | size sm · Size=sm, Expand icon=False, Leading element=False, Expanded=False (1) |
| 32 | 8 | H 8 | 8/8/8/8 | Averta Std Semibold 12px/16px | — | size sm · Size=sm, Expand icon=False, Leading element=Icon, Expanded=False (3) |
| 40 | 8 | V 0 | 8/12/8/12 | Averta Std Semibold 14px/24px | — | size md · Size=md, Expand icon=False, Leading element=False, Expanded=False (1) |
| 40 | 8 | H 16 | 8/8/8/12 | Averta Std Semibold 14px/24px | — | size md · Size=md, Expand icon=True, Leading element=False, Expanded=False (1) |
| 40 | 8 | H 8 | 8/8/8/8 | Averta Std Semibold 14px/24px | — | size md · Size=md, Expand icon=True, Leading element=Icon, Expanded=False (1) |
| 40 | 8 | H 8 | 8/12/8/8 | Averta Std Semibold 14px/24px | — | size md · Size=md, Expand icon=False, Leading element=Icon, Expanded=False (1) |
| 48 | 8 | H 12 | 12/12/12/12 | Averta Std Semibold 14px/24px | — | size lg · Size=lg, Expand icon=False, Leading element=Icon, Expanded=False (3) |
| 48 | 8 | V 12 | 12/12/12/12 | Averta Std Semibold 14px/24px | — | size lg · Size=lg, Expand icon=False, Leading element=False, Expanded=False (1) |
| 56 | 8 | V 16 | 16/16/16/16 | Averta Std Semibold 16px/24px | — | size xl · Size=xl, Expand icon=False, Leading element=False, Expanded=False (1) |
| 56 | 8 | H 16 | 16/16/16/16 | Averta Std Semibold 16px/24px | — | size xl · Size=xl, Expand icon=False, Leading element=Icon, Expanded=False (3) |
| 136 | 8 | V 0 | 0/0/0/0 | Averta Std Semibold 12px/16px | — | size sm · Size=sm, Expand icon=False, Leading element=False, Expanded=True (4) |
| 144 | 8 | V 0 | 0/0/0/0 | Averta Std Semibold 14px/24px | — | size md · Size=md, Expand icon=False, Leading element=False, Expanded=True (4) |
| 160 | 8 | V 0 | 0/0/0/0 | Averta Std Semibold 14px/24px | — | size lg · Size=lg, Expand icon=False, Leading element=Icon, Expanded=True (4) |
| 176 | 8 | V 0 | 0/0/0/0 | Averta Std Semibold 16px/24px | — | size xl · Size=xl, Expand icon=False, Leading element=False, Expanded=True (4) |

### Alert

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 56 | 8 | H 16 | 16/16/16/16 | Averta Std Regular 14px/24px | — | Icon=False, Title=False, Close=True (1) |
| 56 | 8 | H 12 | 16/16/16/16 | Averta Std Regular 14px/24px | — | Icon=True, Title=False, Close=True (2) |
| 56 | 8 | H 8 | 16/16/16/16 | Averta Std Regular 14px/24px | — | Icon=False, Title=False, Close=False (1) |
| 88 | 8 | H 12 | 16/16/16/16 | Averta Std Semibold 14px/24px | — | Icon=False, Title=True, Close=True (1) |
| 88 | 8 | H 8 | 16/16/16/16 | Averta Std Semibold 14px/24px | — | Icon=False, Title=True, Close=False (1) |
| 88 | 8 | H 16 | 16/16/16/16 | Averta Std Semibold 14px/24px | — | Icon=True, Title=True, Close=True (2) |

### Snackbar

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 56 | 8 | H 12 | 16/16/16/16 | Averta Std Regular 14px/24px | — | Leading icon=False, Line lenght=Single, Close=False (1) |
| 64 | 8 | H 16 | 16/16/16/16 | Averta Std Regular 14px/24px | — | Leading icon=False, Line lenght=Single, Close=True (3) |
| 68 | 8 | H 16 | 16/16/16/16 | Averta Std Semibold 14px/20px | — | Leading icon=True, Line lenght=Multi, Close=False (2) |
| 80 | 8 | H 16 | 16/16/16/16 | Averta Std Regular 14px/24px | — | Leading icon=False, Line lenght=Multi, Close=False (1) |
| 88 | 8 | H 16 | 16/16/16/16 | Averta Std Regular 14px/24px | — | Leading icon=False, Line lenght=Multi, Close=True (1) |

### Tooltip

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 40 | 4 | V 0 | 12/12/12/12 | Averta Std Regular 12px/16px | #FFFFFF 1px | Pointer=False, Pointer position=No arrow (1) |
| 40 | — | H 0 | 0/0/0/0 | Averta Std Regular 12px/16px | — | Pointer=True, Pointer position=Arrow Right (2) |
| 48 | — | V 0 | 0/0/0/0 | Averta Std Regular 12px/16px | — | Pointer=True, Pointer position=Arrow Top Middle (6) |

### Popover

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 198 | 12 | V 4 | 4/4/4/4 | — | — | Custom=True (1) |

### Modal

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 280 | 12 | V 0 | 8/8/8/8 | — | — | Default=Default (1) |

### Drawer

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 852 | 12 | V 0 | 4/4/4/4 | — | — | Default=Default (1) |

### Bottom sheet

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 280 | 12 | V 0 | 8/8/8/8 | — | — | Drag handle=False (1) |
| 280 | 12 | V 8 | 0/8/8/8 | — | — | Drag handle=True (1) |

### Progress bar

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 2 | — | — | — | — | — | size 6xs · Size=6xs, Variant=Linear, pin=false (1) |
| 4 | — | — | — | — | — | size 5xs · Size=5xs, Variant=Linear, pin=false (1) |
| 8 | — | — | — | — | — | size 4xs · Size=4xs, Variant=Linear, pin=false (1) |
| 12 | — | — | — | — | — | size 3xs · Size=3xs, Variant=Linear, pin=false (1) |
| 16 | — | — | — | — | — | size 2xs · Property 1=2xs, Property 2=Circular, Property 3=false, Property 4=fals (2) |
| 24 | — | — | — | — | — | Property 1=xs, Property 2=Circular, Property 3=false, Property 4=false (1) |
| 32 | — | — | — | — | — | Property 1=sm, Property 2=Circular, Property 3=false, Property 4=false (1) |
| 40 | — | — | — | — | — | Property 1=md, Property 2=Circular, Property 3=false, Property 4=false (1) |
| 48 | — | — | — | — | — | Property 1=lg, Property 2=Circular, Property 3=false, Property 4=false (1) |
| 60 | — | V 4 | 0/0/0/0 | Averta Std Semibold 10px/16px | — | size 3xs/4xs/5xs/6xs · Size=6xs, Variant=Linear, pin=true (4) |
| 64 | — | V 4 | 0/0/0/0 | Averta Std Semibold 10px/16px | — | size 2xs · Size=2xs, Variant=Linear, pin=true (1) |

### Loader

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 16 | — | — | — | — | — | size 2xs · Size=2xs (1) |
| 24 | — | — | — | — | — | size xs · Size=xs (1) |
| 32 | — | — | — | — | — | size sm · Size=sm (1) |
| 40 | — | — | — | — | — | size md · Size=md (1) |
| 48 | — | — | — | — | — | size lg · Size=lg (1) |

### Pagination

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 32 | — | H 4 | 0/0/0/0 | — | — | Property 1=Default (1) |

### Breadcrumb

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 24 | — | H 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | More than 4 items=False, Custom seperator=False, Icon as item=False (3) |
| 120 | — | V 8 | 0/0/0/0 | Averta Std Regular 14px/24px | — | More than 4 items=True, Custom seperator=False, Icon as item=False (1) |

### Table

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 56 | — | — | — | — | — | Number or Cells=Number or Cells2 (1) |

### Auth code

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 56 | 8 | H 8 | 0/0/0/0 | — | — | Fields=6 (2) |

### Sidebar

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 616 | 12 | V 16 | 12/12/12/12 | — | — | Device=Mobile, Example=True, Collapse / Expand=False (2) |
| 640 | 12 | V 16 | 4/4/4/4 | — | — | Device=Mobile, Example=False, Collapse / Expand=False (2) |
| 704 | 12 | V 16 | 24/24/24/24 | Averta Std Regular 14px/24px | — | Device=Desktop, Example=True, Collapse / Expand=False (2) |
| 704 | 12 | V 16 | 8/8/8/8 | — | — | Device=Desktop, Example=False, Collapse / Expand=False (2) |

### Empty state

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 88 | — | V 8 | 0/16/0/16 | Averta Std Semibold 24px/32px | — | Type=Simple (1) |
| 176 | — | V 8 | 0/16/0/16 | Averta Std Semibold 24px/32px | — | Type=Simple+ Illu (1) |
| 248 | — | V 8 | 0/16/0/16 | Averta Std Semibold 24px/32px | — | Type=Single button (2) |

### Leaderboard

| Alto | Radio | Layout/gap | Pad | Texto | Borde | Variantes (ej.) |
|---|---|---|---|---|---|---|
| 716 | — | V 12 | 0/0/0/0 | Averta Std Regular 14px/24px | — | MDS-Public-TW-Leaderboard (1) |

# Ficha de componente: Campo de texto (`AqTextInput`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/text_input`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqTextInput.kt`.

## Origen
Puerto de `MoonTextInput` / `MoonFormTextInput`. Etiqueta persistente sobre la caja, borde de 1 dp que pasa a color de foco o error, radio squircle interactivo, slots para ícono leading y contenido trailing.

## Anatomía
Etiqueta persistente (arriba), caja con fondo `InputSurface` y borde, ícono leading opcional, campo de texto, contenido trailing opcional, y debajo: contador de longitud y/o mensaje de error o ayuda (icono + texto, nunca solo color).

## Tamaños (MoonTextInput)
| Tamaño | Alto | Radio | Relleno horiz. |
|--------|------|-------|----------------|
| Sm | 32 dp | InteractiveXs (4 dp) | 8 dp |
| Md | 40 dp | InteractiveSm (8 dp) | 12 dp |
| Lg | 48 dp | InteractiveSm (8 dp) | 12 dp |
| Xl | 56 dp | InteractiveMd (12 dp) | 16 dp |

## Estados
- Reposo: borde `FunctionalBorder`.
- Enfocado: borde `Focus` (énfasis de Polaris).
- Pulsado: borde `Focus`.
- Error: borde `Danger`, mensaje de error con icono `Cancel` y tono `Danger`.
- Deshabilitado: opacidad y color `OnDisabled`.

## Área táctil
Objetivo táctil mínimo de 48 dp (D-02).

## Accesibilidad
Rol de campo editable, `contentDescription` con la etiqueta, `error` semántica con el mensaje, `liveRegion` cortés para errores.

## Uso en pantalla
Disponible; las pantallas ya componen con `AqTextInput` (etiqueta persistente, hueco etiqueta-caja `LabelToField` = 8 dp, relleno vertical por tamaño y área táctil de 48 dp mediante wrapper de toque invisible).

# Ficha de componente: Chip (`AqChip`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/chip`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqChip.kt`.
**Nota:** `AqChipGroup` y `AqChipOption` están retirados (símbolos legacy); usar `AqChip`.

## Origen
Puerto de `MoonChip`. Píldora individual con radio squircle interactivo, etiqueta de texto y ícono opcional leading.

## Anatomía
Contenedor con fondo de superficie/selección, borde opcional, etiqueta centrada y ícono decorativo opcional a la izquierda.

## Tamaños (MoonChip)
| Tamaño | Alto | Relleno horiz. | Hueco ícono-etiqueta | Radio | Ícono |
|--------|------|----------------|----------------------|-------|-------|
| Sm | 32 dp | 8 dp | 8 dp | InteractiveXs (4 dp) | 20 dp |
| Md | 40 dp | 16 dp | 8 dp | InteractiveXs (4 dp) | 20 dp |

## Estados
Default, Selected (fondo `SelectionSurface`), Pressed, Disabled.

## Accesibilidad
Rol `Role.Button`, `contentDescription` con la etiqueta, target táctil 48 dp (D-02).

## Uso en pantalla
Disponible; usado en filtros de importación y selecciones de contexto.

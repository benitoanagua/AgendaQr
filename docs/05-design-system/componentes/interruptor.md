# Ficha de componente: Interruptor (`AqSwitch`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/switch`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqSelection.kt`.

## Origen
Puerto de `MoonSwitch`. Pista con pulgar deslizable, tamaños x2s/xs/sm, relleno activo e inactivo de Moon.

## Tamaños (MoonSwitchSize)
| Tamaño | Pista | Pulgar | Radio |
|--------|-------|--------|-------|
| x2s | 32 × 16 dp | 12 dp | InteractiveXs (4 dp) |
| xs | 44 × 24 dp | 16 dp | InteractiveSm (8 dp) |
| sm | 60 × 32 dp | 24 dp | InteractiveSm (8 dp) |

## Estados
- Activo: pista `EmphasisFill`, pulgar `OnEmphasisFill`.
- Inactivo: pista `SurfaceContainerHighest`, pulgar `Surface`.
- Deshabilitado: opacidad reducida.
- Error: pista `Danger`.

## Objetivo táctil
Ancho mínimo de 48 dp (D-02).

## Accesibilidad
Rol `Role.Switch`, `selected` según estado, `contentDescription` del llamador.

## Uso en pantalla
Disponible; las pantallas aún usan controles Moon originales.

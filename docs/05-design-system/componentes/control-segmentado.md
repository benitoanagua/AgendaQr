# Ficha de componente: Control segmentado (`AqSegmentedControl`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/segmented_control`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqButtons.kt`.

## Origen
Puerto de `MoonSegmentedControl`. Pista `SurfaceSecondary`, opción activa sobre píldora, radios interactivos, mutua exclusividad por semántica (`selectableGroup` + rol `Role.RadioButton`).

## Tamaños
- sm: altura 32 dp, relleno horizontal 12 dp, hueco 4 dp.
- md: altura 40 dp, relleno horizontal 16 dp, hueco 8 dp.

## Estados
- Opción activa: relleno `SelectionSurface`, borde, texto con tono activo.
- Opción inactiva: texto `TextSecondary`.
- Deshabilitado: `OnDisabled`.

## Área táctil
Cada opción conserva el objetivo táctil de 48 dp (D-02).

## Accesibilidad
Rol `Role.RadioButton` por opción, `selectableGroup` para el grupo, `contentDescription` del llamador.

## Uso en pantalla
Disponible; las pantallas aún usan `AqSegmentedControl` (el nombre ya coincide).

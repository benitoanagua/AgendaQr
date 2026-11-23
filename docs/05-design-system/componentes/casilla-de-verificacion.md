# Ficha de componente: Casilla de verificación (`AqCheckbox`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/checkbox/checkbox.dart`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqSelection.kt`.

## Origen
Puerto de `MoonCheckbox`. Caja de 16 dp con radio squircle interactivo (4 dp), marca de verificación cuando está seleccionada, área táctil de 48 dp, soporte tri-estado.

## Tamaño
- Visual: 16 dp × 16 dp.
- Área táctil: 48 dp (D-02).

## Estados
- No seleccionado: borde `FunctionalBorder`, fondo transparente.
- Seleccionado: relleno del tono activo (por defecto énfasis), marca `Check`.
- Deshabilitado: opacidad reducida.
- Error: borde `Danger`.

## Semántica
Rol `Role.Checkbox`, `selected` según estado, `contentDescription` del llamador.

## Uso en pantalla
Disponible; las pantallas aún no han migrado de los controles Moon originales.

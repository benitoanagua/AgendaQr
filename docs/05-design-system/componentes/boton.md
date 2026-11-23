# Ficha de componente: Botón (`AqButton`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/buttons/button.dart`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqButtons.kt`

## Origen
Puerto de `MoonButton` / `MoonFilledButton` / `MoonOutlinedButton` / `MoonTextButton`. Un único componente `AqButton` con variante, tamaño, estados y efectos de Moon. El color y la tipografía vienen de Polaris; la forma, el espacio, el borde y el movimiento de Moon.

## Anatomía
Contenedor con radio squircle (borde interactivo de Moon), relleno, etiqueta centrada, ícono opcional leading y contenido opcional trailing. Carga representada por `AqCircularLoader` centrado.

## Tamaños (MoonButtonSizes)
| Tamaño | Alto | Relleno horiz. | Hueco ícono-etiqueta | Radio | Ícono |
|--------|------|----------------|----------------------|-------|-------|
| Xs | 24 dp | 8 dp | 4 dp | InteractiveXs (4 dp) | 16 dp |
| Sm | 32 dp | 12 dp | 4 dp | InteractiveSm (8 dp) | 20 dp |
| Md | 40 dp | 16 dp | 8 dp | InteractiveSm (8 dp) | 20 dp |
| Lg | 48 dp | 16 dp | 12 dp | InteractiveSm (8 dp) | 24 dp |
| Xl | 56 dp | 24 dp | 16 dp | InteractiveMd (12 dp) | 24 dp |

## Variantes (Polaris)
- `Filled`: relleno de marca (`BrandFill`), contenido `OnBrandFill`.
- `Outlined`: transparente, borde funcional, contenido `TextPrimary`.
- `Text`: sin relleno ni borde, contenido `Link` (acento).

## Estados
- Reposo, pulsado (escala 0,95), enfocado (borde de énfasis), deshabilitado (opacidad 0,6), cargando (oculta etiqueta, muestra `AqCircularLoader`).

## Área táctil
Cuando el tamaño visual es menor a 48 dp (Xs, Sm), se conserva el aspecto y se amplía el área táctil a 48 dp (D-02, `touch_target_padding.dart` de Moon).

## Efectos (MoonEffectsTheme)
- Foco: borde de énfasis (25 % de énfasis de Polaris), extensión 4 dp, duración 200 ms, curva InOutCubic.
- Pulsación: escala 0,95, duración 200 ms, curva InOutCubic.

## Accesibilidad
Rol `Role.Button`, `contentDescription` y `onClickLabel` con la etiqueta del llamador, indicación nula (sin ripple), cambio de color como único estado visual.

## Uso en pantalla
Disponible; pendiente de migración de pantallas (las pantallas aún usan `AqPrimaryButton`/`AqSecondaryButton`/`AqOutlineButton`/`AqDangerButton`/`AqTextAction`, que son envolturas delegadoras de `AqButton`).

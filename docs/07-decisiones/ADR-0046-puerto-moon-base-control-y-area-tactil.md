# ADR-0046 — Puerto de MoonBaseControl y área táctil de 48 dp

**Estado:** ACEPTADO — 2026-11-23  
**Relacionado:** ADR-0039 §4.4, ADR-0044, D-02.

## 1. Contexto

`moon_design` 1.1.0 define `MoonBaseControl` (estados de foco, pressed,
long-press y hover) y `utils/touch_target_padding.dart` (ampliación del área
táctil sin cambiar el aspecto), además de los efectos transversales
`MoonEffectsTheme` (`pulse`, `scale`) y `MoonFocusEffect` (`focus`). El port a
Compose no los había implementado: cada componente duplicaba lógica de
interacción y, cuando el alto visual era inferior a 48 dp (tag x2s/xs/sm,
botón xs/sm, interruptor x2s/xs/sm, chip sm), forzaba `defaultMinSize(48 dp)`,
deformando el control.

## 2. Decisión

Portar `MoonBaseControl` y `touch_target_padding` como **API interna**
`internal` de `core/ui`, reutilizada por todos los componentes, sin duplicar
lógica por componente:

- `AqBaseControl`: gestiona las fuentes de interacción (pressed, focused) a
  través de una `MutableInteractionSource` compartida y aplica el efecto
  `scale` al pulsar (200 ms, `Curves.easeInOutCubic` = `AqMotion.Easings.InOutCubic`),
  respetando «reducir movimiento» (`LocalReducedMotion`, escala = 1f y sin
  animación cuando está activo).
- `AqTouchTarget`: amplía el área táctil a 48 dp (`AqMetrics.ControlMinSize`)
  en ambos ejes **sin deformar** el contenido: el nodo mide el contenido con
  restricciones naturales y lo centra dentro del área táctil. Equivale a
  `touch_target_padding.dart` de Moon.
- El nodo táctil expone `mergeDescendants = true` con el texto del control,
  de modo que consultas de accesibilidad (`onNodeWithText`) resuelven al nodo
  táctil de 48 dp y no al nodo visual interno.

Todos los controles con alto visual < 48 dp (botón xs/sm, tag x2s/xs/sm,
interruptor x2s/xs/sm, chip sm, …) usan `AqTouchTarget` y conservan su
aspecto visual mientras cumplen el objetivo táctil ≥ 48 dp.

## 3. Consecuencias

- Los componentes ya no definen áreas táctil propias; consumen
  `AqTouchTarget` / `AqBaseControl` desde `core/ui.theme` (paquete interno).
- Las pruebas de geometría (`AqLayoutGeometryTest` y análogos) verifican el
  área táctil ≥ 48 dp por tamaño para cada control con alto visual < 48 dp.
- No se introduce `androidx.compose.material.icons` ni controles Material 3
  para el interruptor: `AqSwitch` se compone con piezas Moon (píldora +
  pulgar) y tokens.

## 4. Alternativas consideradas

- Forzar `defaultMinSize(48 dp)` en cada componente: descartado, deforma
  controles pequeños (cambia el aspecto visual, incumple Moon).
- Duplicar lógica de área táctil por componente: descartado, viola
  «sin duplicar lógica por componente» y DRY.

## 5. Atribución

`MoonBaseControl`, `touch_target_padding`, `MoonEffectsTheme` y
`MoonFocusEffect` proceden de `moon_design` 1.1.0 (MIT © Yolo Group).
Atribución en `docs/05-design-system/06-licencias-terceros.md`.

# Ficha de componente: Barra de pestañas (`AqTabBar`, `AqPillTab`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/theme/<componente>/tab_bar_sizes.dart` (§ TabBar).
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqTabBar.kt`.

## Origen
Puerto de `MoonTabBar` (variante indicator y píldora). Cada pestaña tiene etiqueta visible SIEMPRE, ícono opcional y conteo opcional en badge. Semántica `Role.Tab` + `selected`.

## Anatomía
Cada pestaña: ícono opcional (20-24 dp) + etiqueta (una línea, sin ellipsis que la reduzca a cero). El conteo va en un badge circular a la derecha. La pestaña activa muestra fondo de selección (variante píldora) o se destaca con el tono de énfasis.

## Geometría (Moon § TabBar)
| Tamaño | Alto | Relleno horiz. | Hueco entre pestañas | Radio | Ícono |
|--------|------|----------------|----------------------|-------|-------|
| Sm | 32 dp | 12 dp | 4 dp (`AqSize.X5s`) | InteractiveSm (8 dp) | 20 dp |
| Md | 40 dp | 16 dp | 8 dp (`AqSize.X4s`) | InteractiveXs (4 dp) | 20 dp |

## Reglas de composición
- **Nunca `Modifier.weight(1f)` dentro de `horizontalScroll`.** Cuando el ancho intrínseco total de las pestañas cabe, se reparte con `weight(1f)`; si no, cada pestaña usa su ancho intrínseco y el `Row` usa `horizontalScroll` (S2: ninguna etiqueta queda truncada a cero).
- Objetivo táctil ≥ 48 dp (D-02).
- Etiqueta `maxLines = 1`; el conteo es opcional (`AqTab.count`).

## Accesibilidad
Rol `Tab`, `selected`, `contentDescription` del ícono nulo (decorativo), etiqueta siempre visible.

## Uso en pantalla
Disponible; usado en `ContextScreen` (pestañas inferiores) y navegación raíz.

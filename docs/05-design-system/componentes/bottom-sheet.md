# Ficha de componente: Bottom Sheet (`AqBottomSheet`)

- **Nombre:** Bottom Sheet (`AqBottomSheet`)
- **Nivel de origen:** `moon-pattern` (Nivel 2)
- **Anatomía:** Scrim semitransparente de fondo completo, hoja inferior deslizable hacia arriba con borde superior redondeado (`radius-300` / 12 dp), barra indicadora de arrastre superior (drag handle de 32x4 dp centrado), área de encabezado opcional con título (`HeadingLg`) y botón de cierre, contenedor de contenido con padding interno (`space-400` / 16 dp).
- **Variantes y tamaños:** Modal standard (altura fija o wrap-content limitada a 90% de pantalla).
- **Estados:** Hidden, Visible / Expanded, Dragging / Interacting.
- **Tokens Polaris:**
  - Fondo de hoja: `AqColor.BgSurface` (`#FFFFFF` en claro, `#1A1A1A` en oscuro).
  - Drag handle: `AqColor.BorderSubdued` (`#E1E3E5` / `#333333`).
  - Scrim: `AqColor.Scrim` (`rgba(0, 0, 0, 0.45)`).
  - Radios: `AqRadius.Radius300` (12 dp esquinas superiores).
  - Movimiento: `AqMotion.DurationBase` (200 ms), `AqMotion.EasingBase`.
- **Accesibilidad:** Cierre por botón y tecla de retroceso (`system-back`), semántica de diálogo / sheet modal, target táctil mínimo 48 dp para acciones, anunciador TalkBack del título al abrir.
- **Comportamiento Claro / Oscuro:** Superficie cambia a `#1A1A1A` en oscuro, scrim se intensifica, drag handle mantiene contraste WCAG AA.
- **Notas de adaptación:** Polaris no define bottom sheet táctil. Se toma la interacción, gestos de arrastre y drag handle de `MoonBottomSheet` (Moon Design Flutter 1.1.0). Se reemplazan el 100% de los tokens visuales, sombras y squircle por tokens Polaris.
- **Pantallas consumidoras:**
  - `DestinationDetailScreen` (S03): hoja de detalle del destino (categoría, fecha de alta, contenido QR, nota) para inspección sin abandonar el flujo.

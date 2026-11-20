# Ficha de componente: Segmented Control (`AqSegmentedControl`)

- **Nombre:** Segmented Control (`AqSegmentedControl`)
- **Nivel de origen:** `polaris` (Nivel 1) con confirmación de patrón `moon-pattern` (Nivel 2)
- **Anatomía:** Pista contenedora horizontal con relleno (`AqColor.BgSurfaceSecondary`), selector de segmento deslizable o resaltado con borde y fondo blanco (`AqColor.BgSurface`), etiquetas de texto (`CaptionSmall` / `BodyBase`) y badges de conteo numérico opcionales.
- **Variantes y tamaños:** Altura fija de 40 dp (con padding interactivo a 48 dp), soporte de 2 a 4 segmentos excluyentes, admite `selectedIndex = -1` (ninguno seleccionado).
- **Estados:** Default (no seleccionado), Selected (activo), Pressed (efecto táctil), Disabled (opacidad 0.38).
- **Tokens Polaris:**
  - Pista de fondo: `AqColor.BgSurfaceSecondary` (`#F7F7F7` en claro, `#262626` en oscuro).
  - Píldora activa: `AqColor.BgSurface` (`#FFFFFF` en claro, `#303030` en oscuro).
  - Borde de píldora: `AqColor.BorderSubdued` (`#E1E3E5` / `#333333`).
  - Texto activo: `AqColor.Text` (`#202223` / `#E3E3E3`).
  - Texto inactivo: `AqColor.TextSubdued` (`#6D7175` / `#8A8A8A`).
  - Radios: `AqRadius.Radius200` (8 dp).
  - Animación: `AqMotion.DurationFast` (100 ms).
- **Accesibilidad:** `Role.Tab` o selección de radiobutton excluyente, semántica `selected = true/false`, target táctil mínimo 48 dp garantizado por padding o contenedor.
- **Comportamiento Claro / Oscuro:** Inversión de pista a tono secundario oscuro y píldora a tono de superficie elevado con contraste WCAG AA.
- **Notas de adaptación:** Polaris usa filtros segmentados en escritorio; Moon valida la anatomía táctil compacta (`MoonSegmentedControl`). Estilo 100% tokens Polaris.

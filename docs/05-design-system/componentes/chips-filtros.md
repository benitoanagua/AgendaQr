# Ficha de componente: Chips y Filtros (`AqChipGroup`, `AqChipOption`)

- **Nombre:** Chips y Filtros (`AqChipGroup`, `AqChipOption`)
- **Nivel de origen:** `polaris` (Nivel 1) con confirmación de patrón `moon-pattern` (`MoonChip`, `MoonTag`)
- **Anatomía:** Píldora individual con borde sutil, icono check de selección opcional a la izquierda, etiqueta de texto (`CaptionSmall` a 12 sp) y contador numérico en badge secundario a la derecha (`radius-100`).
- **Variantes y tamaños:** Selección única o selección múltiple dentro de `AqChipGroup`. Altura de 32 dp con área táctil mínima de 48 dp.
- **Estados:** Default (deseleccionado con fondo transparente/secundario y borde `BorderSubdued`), Selected (fondo `BgSurface`, borde reforzado, check visible), Pressed, Disabled.
- **Tokens Polaris:**
  - Fondo deseleccionado: `AqColor.BgSurfaceSecondary` (`#F7F7F7` / `#262626`).
  - Fondo seleccionado: `AqColor.BrandFill` suave o `BgSurface` con borde de selección.
  - Texto e icono: `AqColor.Text` / `AqColor.TextSubdued`.
  - Radio: `AqRadius.Radius200` (8 dp) o `Pill`.
  - Contador: `AqColor.BorderSubdued` de fondo con texto `CaptionSmall` (12 sp).
- **Accesibilidad:** `Role.Checkbox` en selección múltiple o `Role.RadioButton` en única, anuncio de estado "seleccionado" en TalkBack, target táctil 48 dp.
- **Comportamiento Claro / Oscuro:** Se adaptan superficies y bordes manteniendo contraste 4.5:1.
- **Notas de adaptación:** Polaris usa chips en sus vistas de índice (`IndexFilters`); Moon valida el formato compacto táctil.

# Ficha de componente: List Cell (`AqListRow`, `AqListBody`)

- **Nombre:** List Cell / Fila de lista (`AqListRow`, `AqListBody`)
- **Nivel de origen:** `moon` (Nivel 1) + `native-pattern` (Nivel 3)
- **Anatomía:** Fila horizontal estructurada dentro de un contenedor `AqListBody` o `AqCard`: área inicial para avatar, icono o thumbnail QR (32 a 40 dp), bloque central de texto con título (`BodyBase` semibold) y subtítulo (`CaptionSmall` a 12 sp), metadatos tonales a la derecha y chevron indicador de navegación (`ChevronRightIcon`). Separadores hairline entre filas.
- **Variantes y tamaños:** Fila estándar (altura mínima 56 dp, área interactiva >= 48 dp), fila compacta, fila de selección con checkbox/radio.
- **Estados:** Default, Pressed (overlay ondulado sutil), Selected, Disabled.
- **Tokens Moon:**
  - Fondo: `AqColor.BgSurface`.
  - Fondo al presionar: `AqColor.BgSurfaceSecondary`.
  - Borde divisor: `AqColor.BorderSubdued` (1 dp o hairline).
  - Título: `AqColor.Text`.
  - Subtítulo / metadatos: `AqColor.TextSubdued`.
  - Padding: `AqSpacing.Space300` (12 dp vertical), `AqSpacing.Space400` (16 dp horizontal).
- **Accesibilidad:** `Role.Button` cuando es clickeable, descripción combinada de accesibilidad (título + estado + metadato), target táctil mayor a 48 dp.
- **Comportamiento Claro / Oscuro:** Se adapta al fondo de superficie y mantiene contraste WCAG AA.
- **Notas de adaptación:** Moon no trae list cells en su catálogo; se adapta a la celda táctil móvil nativa de 48 dp con estilo Moon.

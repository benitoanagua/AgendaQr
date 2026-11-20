# Ficha de componente: Bottom Navigation (`AqNavBar`, `AqNavRail`)

- **Nombre:** Bottom Navigation (`AqNavBar`, `AqNavRail`)
- **Nivel de origen:** `native-pattern` (Nivel 3) estilizado con tokens Polaris
- **Anatomía:** Barra inferior fija o riel lateral para anchos grandes (`AqNavRail`), dividida uniformemente entre los destinos principales (3 a 5 ítems), cada ítem con icono centrado de 20x20 dp, etiqueta de texto compacta (`CaptionSmall` a 12 sp), píldora o fondo indicador de destino activo.
- **Variantes y tamaños:** `AqNavBar` (móvil estándar, altura 56 a 64 dp con target táctil > 48 dp), `AqNavRail` (tablets / desktop lateral).
- **Estados:** Selected (icono resaltado con `BrandFill`, etiqueta `AqColor.Text`), Unselected (icono y texto `AqColor.TextSubdued`), Pressed.
- **Tokens Polaris:**
  - Fondo de barra: `AqColor.BgSurface` con borde superior hairline `AqColor.BorderSubdued`.
  - Icono y texto activo: `AqColor.Text` / `AqColor.BrandFill`.
  - Inactivo: `AqColor.TextSubdued`.
  - Píldora activa: `AqColor.BgSurfaceSecondary`.
  - Radio: `AqRadius.Radius200` (8 dp) o `Pill`.
- **Accesibilidad:** `Role.Tab`, atributo `selected`, etiquetado semántico para TalkBack en cada ítem, orden de foco secuencial.
- **Comportamiento Claro / Oscuro:** Se adapta según el esquema activo con borde hairline superior que delimita el contenido.
- **Notas de adaptación:** Polaris carece de barra inferior móvil (su navegación es barra lateral web). Moon no incluye barra de navegación inferior. Se adopta la convención nativa mobile (Nivel 3) estilizada 100% con tokens Polaris.

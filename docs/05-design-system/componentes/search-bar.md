# Ficha de componente: Search Bar (`AqSearchField`, `AqSearchTrigger`)

- **Nombre:** Search Bar (`AqSearchField`, `AqSearchTrigger`)
- **Nivel de origen:** `polaris` (Nivel 1) con adaptación táctil móvil
- **Anatomía:** Contenedor con borde completo o sutil, icono de lupa (`SearchIcon`) a la izquierda, campo de texto con placeholder descriptivo ("Buscar en la agenda"), botón de borrado rápido (`BackspaceIcon` / `CloseIcon`) visible cuando hay texto, y variante `AqSearchTrigger` de solo lectura que navega a la pantalla completa de búsqueda al tocar.
- **Variantes y tamaños:** `AqSearchField` (campo interactivo con teclado) y `AqSearchTrigger` (botón con apariencia de campo para pantallas principales como S01). Altura 48 dp.
- **Estados:** Default (placeholder), Focused (anillo de foco `Focus`), Typing / With Text (botón de borrado activo), Disabled.
- **Tokens Polaris:**
  - Fondo: `AqColor.InputSurface` (`#FFFFFF` en claro, `#262626` en oscuro).
  - Borde: `AqColor.InputBorder` (`#8C9196` en claro, `#4A4A4A` en oscuro).
  - Borde enfocado: `AqColor.Focus` (`#005BD3` en claro, `#91D0FF` en oscuro).
  - Texto e icono: `AqColor.Text` / `AqColor.TextSubdued`.
  - Radio: `AqRadius.Radius200` (8 dp).
- **Accesibilidad:** Etiqueta accesible explícita ("Buscar"), target táctil de 48 dp, acción de limpiar campo anunciada para lectores de pantalla.
- **Comportamiento Claro / Oscuro:** Fondo y bordes se adaptan manteniendo contraste de accesibilidad.
- **Notas de adaptación:** Polaris define `SearchField` y `IndexFilters`; Moon no trae buscador en su catálogo; se adapta el componente Polaris al área táctil móvil de 48 dp.

# Ficha de componente: Empty State (`AqEmptyState`)

- **Nombre:** Empty State (`AqEmptyState`)
- **Nivel de origen:** `native-pattern` (Nivel 3) estilizado con tokens Polaris
- **Anatomía:** Disposición vertical centrada: contenedor de icono decorativo con fondo tonal circular o cuadrado redondeado (48 a 64 dp), encabezado principal en negrita (`HeadingLg` a 18 sp), texto descriptivo explicativo (`BodyBase`), y botón de llamada a la acción opcional (`AqPrimaryButton`).
- **Variantes y tamaños:** Empty state de pantalla completa (por ejemplo lista vacía S01/S08) y empty state contextual o embebido en tarjetas.
- **Estados:** Estático con CTA interactivo.
- **Tokens Polaris:**
  - Icono decorativo: `AqColor.TextSubdued` sobre círculo `AqColor.BgSurfaceSecondary`.
  - Título: `AqColor.Text` (`HeadingLg`).
  - Descripción: `AqColor.TextSubdued` (`BodyBase`).
  - Botón: `AqPrimaryButton` (`BrandFill`).
  - Espaciado vertical: `AqSpacing.Space400` (16 dp) entre elementos.
- **Accesibilidad:** Jerarquía de encabezado accesible, lectura secuencial título -> descripción -> botón de acción.
- **Comportamiento Claro / Oscuro:** Adaptación de fondos y contraste de textos.
- **Notas de adaptación:** Polaris web carece de un componente móvil de empty state dedicado; Moon no lo incluye en su catálogo; se formaliza la convención nativa mobile (Nivel 3) con tokens Polaris.

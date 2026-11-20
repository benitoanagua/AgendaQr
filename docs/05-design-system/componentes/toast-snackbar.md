# Ficha de componente: Toast / Snackbar (`AqSnackbarHost`, `AqFeedbackEvent`)

- **Nombre:** Toast / Snackbar (`AqSnackbarHost`, `AqFeedbackEvent`)
- **Nivel de origen:** `polaris` (Nivel 1) con adaptación mobile
- **Anatomía:** Contenedor flotante horizontal en la parte inferior de la pantalla sobre la barra de navegación, fondo de alto contraste inverso (`BgFillBrand` o tono inverso Polaris), icono semántico opcional a la izquierda, mensaje breve de confirmación (`BodyBase` inverso), botón de acción o descarte ("Deshacer" / Cerrar) a la derecha.
- **Variantes y tamaños:** Informativo general, Éxito, Advertencia, Error/Destructivo. Ancho adaptable con margen lateral `space-400` (16 dp).
- **Estados:** Apareciendo (fade/slide in), Visible (duración de 3 a 5 segundos), Descartándose (fade/slide out).
- **Tokens Polaris:**
  - Fondo: `AqColor.BrandFill` (`#303030` en claro, `#FFFFFF` en oscuro) o fondo inverso.
  - Texto: `AqColor.TextInverse` (`#FFFFFF` en claro, `#202223` en oscuro).
  - Radio: `AqRadius.Radius200` (8 dp).
  - Elevación: `AqElevation.Shadow400`.
  - Animación: `AqMotion.DurationBase` (200 ms).
- **Accesibilidad:** Evento anunciado con `LiveRegionMode.Polite` o `Assertive`, acción táctil de 48 dp mínimo, no oculta elementos críticos de interacción.
- **Comportamiento Claro / Oscuro:** Inversión de tonos para mantener contraste de 7:1 contra el fondo de la aplicación.
- **Notas de adaptación:** Polaris web usa `Toast`; en mobile se adapta al patrón flotante inferior de snackbar táctil.

# Ficha de componente: Pickers de Fecha y Hora (`AqDatePickerDialog`, `AqTimePickerDialog`)

- **Nombre:** Pickers de Fecha y Hora (`AqDatePickerDialog`, `AqTimePickerDialog`)
- **Nivel de origen:** `native-pattern` (Nivel 3) estilizado con tokens Polaris
- **Anatomía:** Diálogo modal centrado (`AqDialog`) con radio `radius-400` (16 dp). En fecha: cabecera con mes/año actual y controles de avance/retroceso, cuadrícula de días de la semana y matriz de 7 columnas de días. En hora: selector numérico o rueda táctil accesible de horas y minutos con selector AM/PM o formato 24 horas. Acción de cierre mediante el botón "Aceptar" (`AqPrimaryButton`) en la parte inferior.
- **Variantes y tamaños:** Diálogo modal estándar (ancho responsivo, no excede `ContentMaxWidth` ni el 90% de pantalla).
- **Estados:** Default (fecha/hora seleccionada actual), Focused (día seleccionado con círculo de relleno `BrandFill`), Disabled (fechas fuera de rango permitido).
- **Tokens Polaris:**
  - Fondo de diálogo: `AqColor.BgSurface`.
  - Días / horas seleccionadas: `AqColor.BrandFill` con texto blanco.
  - Día de hoy / resaltado: `AqColor.BorderSubdued` de contorno.
  - Botón primario: `AqPrimaryButton`.
  - Radio: `AqRadius.Radius400` (16 dp).
- **Accesibilidad:** Cumple D-07 (diálogo modal cerrado, nunca bottom sheet para selectores críticos según regla de negocio), navegación por teclado y botones accesibles de 48 dp, lectura de fecha completa para TalkBack ("19 de noviembre de 2026").
- **Comportamiento Claro / Oscuro:** Se adapta al modo oscuro manteniendo contraste de los días y botones.
- **Notas de adaptación:** Polaris no tiene selector de fecha móvil táctil; Moon tampoco ofrece date/time picker en su catálogo. Se formaliza el diálogo nativo estilizado 100% con tokens Polaris.

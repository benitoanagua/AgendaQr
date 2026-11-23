# Ficha de componente: Skeleton / Carga (`AqSkeleton`, `AqProgressBlock`)

- **Nombre:** Skeleton / Carga (`AqSkeleton`, `AqProgressBlock`)
- **Nivel de origen:** `moon` (Nivel 1) con referencia `moon-pattern` (Nivel 2)
- **Anatomía:** Formas geométricas rectangulares o circulares con esquinas redondeadas que emulan texto, avatares o tarjetas mientras se cargan los datos, con animación de pulso o brillo ("shimmer").
- **Variantes y tamaños:** `AqSkeleton` (bloque genérico configurable en ancho y alto), `AqProgressBlock` (líneas emuladas de títulos y párrafos).
- **Estados:** Animación continua de brillo/fade.
- **Tokens Moon:**
  - Color base: `AqColor.BgSurfaceSecondary` (`#F7F7F7` / `#262626`).
  - Brillo shimmer: `AqColor.BorderSubdued` o alfa sobre base.
  - Radio: `AqRadius.Radius100` (4 dp) o `Radius200` (8 dp).
  - Duración de ciclo: `AqMotion.DurationSlow` (300 a 800 ms).
- **Accesibilidad:** Marcador de accesibilidad `contentDescription = "Cargando..."`, soporte de `ReducedMotion` (desactiva shimmer continuo y muestra bloque estático).
- **Comportamiento Claro / Oscuro:** Se adapta según la luminancia del fondo para no deslumbrar en modo oscuro.
- **Notas de adaptación:** Moon aporta `MoonLinearLoader` y referencias de loaders táctiles.
- **Pantallas consumidoras:**
  - `ComprobanteViewer` (detalle de comprobante): `AqSkeleton(lines = 3)` durante la carga inicial del comprobante.
  - `ContextScreen` (S02): `AqSkeleton(lines = 3)` en la lista de contextos y `AqSkeleton(lines = 4)` en el detalle de contexto durante la carga inicial.

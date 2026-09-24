# Implementación Xauxa en Agenda QR

La implementación Compose/KMP adopta los invariantes de Xauxa Design System v8:

- tokens antes que valores visuales locales;
- `radius = 0` para rectángulos;
- sin elevación/sombras para jerarquía;
- un único acento de producto: AgendaQr `#0E7D6E` y `#7FD9C9`;
- separación mediante borde y spacing;
- targets táctiles mínimos de 48dp;
- foco visible;
- motion con propósito y respeto de reduced motion;
- paridad Compose/SwiftUI como condición de terminado;
- composición de componentes antes de crear componentes nuevos.

## Aplicación al UX/UI V1 congelado

El contrato visual se aplica a las pantallas definidas en `docs/04-ux/02-especificacion-ux-ui-v1.md`.

- Home prioriza búsqueda sin convertir Agenda QR en dashboard administrativo.
- Añadir prioriza Galería, luego Desde otra app, y deja Cámara como captura secundaria.
- Buscar utiliza resultados heterogéneos con etiquetas semánticas; no crea navegación previa por tipo.
- Contexto utiliza filas/secciones y no se convierte en dashboard financiero.
- Command Bar se reserva para superficies de detalle/foco con acciones concretas.
- El scanner reutiliza lenguaje visual XauxaXcan, sin métricas técnicas.
- Importación, guardado, sincronización y error se expresan como estados de UI, no destinos artificiales.
- Estados semánticos no dependen únicamente del color.
- Reduced motion no puede eliminar información ni recuperación.

## Componentes/patrones

Se prioriza composición de componentes existentes. Pueden reutilizarse, donde corresponda:

- Search bar;
- Text input / Textarea;
- Favorite toggle;
- Category chip;
- Destination card;
- QR fullscreen;
- Multi-source import;
- Optional biometric/PIN lock.

No se crea un componente nuevo solo porque una pantalla tenga una composición diferente.

## Fuente canónica de tokens (Kotlin)

`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/XauxaTokens.kt` es la
única fuente editable (`design-tokens.json` se retiró tras migrar sus valores;
ver historial Git). Estructura:

- `XauxaPrimitive` (interno): valores base sin intención de UI. Solo los
  esquemas los consumen; los componentes tienen prohibido usarlos.
- `XauxaColorScheme` (data class inmutable): intención semántica completa —
  `background`, `surface`/`surface2`/`surface3`, `border`,
  `textPrimary`/`textSecondary`/`textTertiary`, `brand`/`brandAccent`/`onBrand`,
  `success`/`danger`/`warning`/`info` con sus fondos `-bg`, `focusRing` y
  `white` (blanco QR, idéntico en ambos temas).
- `LightXauxaColorScheme`: producción vigente preservada. Solo `surface3`
  (`#E6EBEA`) es propuesta nueva pendiente de validación.
- `DarkXauxaColorScheme`: valores dark-first de la referencia xauxa v13 y
  fondo `#151218` de XauxaXcan. Definidos por referencia, pendientes de
  validación visual de producto.
- `LocalXauxaColorScheme` + fachada `XauxaColor`: los componentes consumen
  `XauxaColor.Surface` etc. y reciben el esquema vigente sin comprobar el
  tema a mano. Sin provider explícito rige el claro.
- `XauxaSpacing`, `XauxaMetrics` (`Border` 1px, `BorderStrong` 2px para marcadores/marcos/footers, `Focus` 2px, `ControlMinSize` 48dp), `XauxaType` (escala + `LetterSpacingWide` + `FamilyUi`/`FamilyMono`), `XauxaMotion`: sin cambios salvo extensiones documentadas.

`AgendaQrTheme(darkTheme)` provee el esquema y mapea `MaterialTheme`. La
producción no usa el wrapper y queda en claro, idéntica a antes.

### Cómo añadir o modificar un token

1. Añadir el primitivo a `XauxaPrimitive` con comentario de procedencia
   (`[prod]`, `[refs]`, `[derivado]` o `[nuevo]`).
2. Exponerlo en `XauxaColorScheme` y en ambos esquemas (un esquema incompleto
   no compila: la data class lo exige).
3. Si es color semántico, añadir la fachada en `XauxaColor` y el nombre en
   `XauxaTokenIndex` (el catálogo lo valida).
4. Añadir/ajustar el specimen en `LabFoundationPreview` y el pin en
   `XauxaSchemeTest` si fija apariencia de producción.

No hay salidas derivadas que regenerar: el host Wasm renderiza los tokens
Kotlin directamente en el canvas (su `styles.css` es un reset sin valores,
verificado por `verifyWebDesignSystem`); no existe CSS generado ni JSON
generado porque no hay consumidores que los necesiten.

## Nota

La especificación UX/UI V1 está congelada. Este documento describe cómo aplicar Xauxa al contrato; no autoriza modificar el contrato UX.

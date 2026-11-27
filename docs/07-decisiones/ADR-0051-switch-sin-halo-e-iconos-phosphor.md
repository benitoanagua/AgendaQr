# ADR-0051 — Switch sin halo + iconografía Phosphor en slots

**Estado:** ACEPTADO — 2026-11-26  
**Relacionado:** ADR-0007, ADR-0044, ADR-0048, ADR-0050, ADR-0054.
**Nota:** la geometría de pista y pulgar (cápsula/stadio) queda
**supersedida por ADR-0054**: el switch es ahora un rectángulo
redondeado. El recorte de la sombra aquí descrito se mantiene, aplicado
sobre la nueva forma.

## 1. Contexto

Dos defectos visibles en `AqSwitch` (editor de destino, fila «Marcar como
favorito») y una necesidad de iconografía para los slots del componente:

1. **Halo ovalado alrededor de la pista.** El pulgar lleva
   `.aqShadow(AqShadow.Sm, AqSquircles.Circle)`. En Android,
   `aqShadowPlatform` dibuja con `BlurMaskFilter` en `drawBehind` sin
   recorte: el blur se sale del pulgar y de la pista y se ve como un óvalo
   más claro que desborda el control. En iOS y wasmJs la sombra usa
   `Modifier.shadow(clip = false)`, que tiene el mismo defecto.
2. **Tema claro/oscuro.** La pista inactiva usaba `AqColor.Border`
   (`beerus`), que queda a ~1,3:1 sobre la superficie: por debajo del
   mínimo de 3:1 que el proyecto aplica a componentes de UI (D-02). En
   oscuro el halo negro además era invisible sobre fondos oscuros.
3. **Iconos en los slots.** Moon muestra una sección «Custom MoonSwitch»
   con glifos dentro del pulgar; el catálogo de producto necesita Check/X
   sin arrastrar una dependencia completa de iconos.

## 2. Decisión

### 2.1 Recorte de la sombra del pulgar (las tres plataformas)

El `Box` interior de la pista (el que ya lleva `.padding(trackPadding)`)
recibe `.clip(AqSquircles.Circle)`. `clip` en Compose recorta **todo** el
dibujo de los descendientes, así que el blur de la sombra —sea
`BlurMaskFilter` en Android o `Modifier.shadow` en iOS/wasmJs— queda
confinado al contorno interior del estadio y no puede pintar fuera de la
pista. Se eligió el recorte frente a «sombra de una capa contenida»
porque reproduce fielmente `shadows.sm` de Moon sin tocar los tokens de
sombra ni las tres implementaciones de plataforma.

### 2.2 Pista inactiva: `FunctionalBorder` en lugar de `Border`

`AqSwitch` usa `AqColor.FunctionalBorder` (`trunks`, ≥ 3:1 sobre
superficie) para la pista apagada en lugar del `beerus` decorativo del
spec. Es un ajuste **a nivel de componente**, no del token global
`Border`, para no afectar al resto de componentes que usan `beerus`
correctamente como borde decorativo. La pista activa conserva
`AqColor.BrandFill` (`piccolo`): es el color de marca mandado por Moon
(ADR-0044) y el estado on/off ya lo codifica la posición del pulgar.
Todos los colores siguen saliendo de `LocalAqColorScheme` vía los
getters composables de `AqColor`, por lo que la recomposición por cambio
de tema actualiza pista, pulgar y sombra.

### 2.3 Iconografía Phosphor

La ampliación puntual original de ADR-0051 queda **supersedida por ADR-0052**:
Phosphor sustituye completamente a Moon Icons en el catálogo de producto.

- `AqIcons` conserva la API semántica del producto y todos sus 39 conceptos
  apuntan a 37 glifos oficiales Phosphor `@phosphor-icons/core` v2.0.8,
  convertidos a vectores Compose KMP locales.
- `AqSwitch` mantiene Check/X Phosphor Bold como valores predeterminados
  de `activeThumbWidget` y `inactiveThumbWidget`; ninguna pantalla,
  galería ni Lab debe configurar overrides visuales para el estado estándar.
- No se permite Moon Icons, Material Icons ni otra familia como fallback.
- La distribución y atribución MIT se registran en la documentación de
  licencias y `androidApp/third-party-notices/THIRD_PARTY_LICENSES.txt`.

### 2.4 Prueba de píxeles bajo Robolectric

`AqSwitchRenderTest` mide la región real renderizada. `captureToImage`
de ui-test no produce frame bajo Robolectric (`ComposeTimeoutException`
en `forceRedraw`), así que se usa `captureRoboImage` de Roborazzi con
`roborazzi.test.record=true` activado en la propia prueba — la misma
propiedad que enciende la evidencia pixel con `-Pagendaqr.capture=true`.
La captura es del canvas etiquetado (no del root, que no produce PNG) y
los bounds de la pista se obtienen de semántica para mapear coordenadas.

## 3. Consecuencias

- La pista inactiva es visible en ambos temas (≥ 3:1); la sombra del
  pulgar es perceptible pero nunca desborda la pista.
- Phosphor queda disponible para iconos de producto puntuales; ampliar el
  set de glifos es añadir rutas al mismo archivo, sin nueva ADR mientras
  se mantenga la regla de peso y la licencia MIT.
- `AqSwitchRenderTest` fija la regresión: cualquier dibujo fuera de
  `aq-switch-track` (más 2 px de tolerancia de antialiasing) rompe el
  build.

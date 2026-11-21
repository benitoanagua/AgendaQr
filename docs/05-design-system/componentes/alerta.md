# Ficha de componente: Alerta (`AqAlert`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/alert`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqFeedback.kt`.

## Origen
Puerto de `MoonAlert`. Superficie con radio squircle, borde opcional, ícono leading, etiqueta, contenido opcional y acción de cierre.

## Tamaño
Altura mínima 48 dp (D-02), relleno horizontal 16 dp, hueco horizontal 12 dp.

## Tonos (Polaris)
Neutral, Success, Danger, Warning, Info — par content/container de `AqTone`.

## Estados
- Visible / oculto (animación de entrada/salida).
- Con borde o sin borde.

## Accesibilidad
Semántica del componente, `contentDescription`, `liveRegion` cortés para el contenido.

## Uso en pantalla
Disponible; las pantallas aún usan `AqStatusBanner` (que permanece disponible).

# Ficha de componente: Alerta (`AqAlert`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/alert`.
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqAlert.kt`.

## Origen
Puerto de `MoonAlert`. Superficie con radio squircle, borde opcional, ícono leading, etiqueta, contenido opcional y acción de cierre.

## Tamaño
Altura mínima 56 dp (Moon § Alert), relleno 16 dp en los cuatro lados, hueco horizontal 12 dp y hueco vertical 8 dp. Con más de una acción, los botones van en una fila debajo del mensaje alineados al final; con una sola, puede ir a la derecha. Cierre con `AqIcons.Close`.

## Tonos (Moon)
Neutral, Success, Danger, Warning, Info — par content/container de `AqTone`.

## Estados
- Visible / oculto (animación de entrada/salida).
- Con borde o sin borde.

## Accesibilidad
Semántica del componente, `contentDescription`, `liveRegion` cortés para el contenido.

## Uso en pantalla
Disponible; las pantallas ya componen con `AqAlert` (mensaje + hasta dos acciones debajo, cierre con `AqIcons.Close`).

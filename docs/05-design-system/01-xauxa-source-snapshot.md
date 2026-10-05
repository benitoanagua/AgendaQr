# Xauxa — fuente visual

Este documento fija la adopción de Xauxa en Agenda QR. Xauxa es la autoridad única para UI/UX visual: fundamentos, tokens, componentes, patrones, estados, accesibilidad, movimiento, tipografía y composición.

## Invariantes

1. Tokens semánticos; no valores visuales crudos en features.
2. Contenedores rectangulares con radio 0; los círculos reales siguen siendo círculos.
3. Sin elevación decorativa ni sombras en el lenguaje base del producto.
4. Acento de sistema `0067B8` más paleta Metro de acentos por contexto (enmienda V1.1, ADR-0005): acento derivado de forma determinista del identificador del contexto, con tabla de contraste calculada en la spec §12; los colores de estado permanecen semánticos y separados de los acentos.
5. Separación estructural mediante spacing y bloques de color (enmienda V1.1, ADR-0005); los bordes pasan a uso funcional (foco, campo en foco/error, tile seleccionado) y se elimina el borde de reposo en filas, botones, badges y tiles.
6. Movimiento con propósito; reduced motion siempre respetado.
7. Targets interactivos mínimos de 48dp / 44pt.
8. Foco visible.
9. Composición full-bleed antes que tarjetas decorativas.
10. Las nuevas composiciones se construyen a partir de primitives/componentes Xauxa.

## Paleta de referencia

La implementación Compose debe reflejar los tokens Material de Xauxa en `XauxaTokens.kt`, incluyendo tema claro y oscuro.

Paleta de acentos (V1.1, ADR-0005): acento de sistema `0067B8` más 12 acentos Metro admitidos con texto asignado por contraste calculado — lime A4C400 (negro), emerald 008A00 (negro), teal 00ABA9 (negro), cyan 1BA1E2 (negro), cobalt 0050EF (blanco), indigo 6A00FF (blanco), violet AA00FF (blanco), magenta D80073 (blanco), crimson A20025 (blanco), red E51400 (blanco), orange FA6800 (negro), amber F0A30A (negro). Tabla completa y regla en la spec §12.

## Tipografía

Xauxa documenta Archivo para display/page/tile headers y Roboto/San Francisco para UI/body/metadata. Con V1.1 (ADR-0005) el display de página usa **Archivo peso Light (300), ≥ 40 sp**; empaquetar ese peso es tarea de implementación registrada en `05-xauxa-tipografia.md`. Cuando las fuentes no estén empaquetadas en el proyecto, se utiliza la familia de sistema sin inventar una identidad tipográfica alternativa.

## Layout

Base de 4px; breakpoints de comportamiento en 480px y 640px; alineación izquierda por defecto; centrado reservado para estados vacíos/error de página completa. V1.1 (ADR-0005): rejilla de tiles de 4 columnas en compacto con tamaños pequeño (1×1), mediano (2×2) y ancho (4×2); margen de pantalla y separación entre tiles como tokens en múltiplos de 4.

## Motion

150/300/600ms y curvas standard/emphasized/decelerate. Los loops decorativos no forman parte del lenguaje visual.

## Norma de adopción

No se mantiene una segunda identidad visual de Agenda QR. Agenda QR expresa su dominio mediante Xauxa.

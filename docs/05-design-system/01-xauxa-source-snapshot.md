# Xauxa — fuente visual

Este documento fija la adopción de Xauxa en Agenda QR. Xauxa es la autoridad única para UI/UX visual: fundamentos, tokens, componentes, patrones, estados, accesibilidad, movimiento, tipografía y composición.

## Invariantes

1. Tokens semánticos; no valores visuales crudos en features.
2. Contenedores rectangulares con radio 0; los círculos reales siguen siendo círculos.
3. Sin elevación decorativa ni sombras en el lenguaje base del producto.
4. El color primario/secundario/terciario de Xauxa se conserva según tema.
5. Separación estructural mediante bordes y spacing.
6. Movimiento con propósito; reduced motion siempre respetado.
7. Targets interactivos mínimos de 48dp / 44pt.
8. Foco visible.
9. Composición full-bleed antes que tarjetas decorativas.
10. Las nuevas composiciones se construyen a partir de primitives/componentes Xauxa.

## Paleta de referencia

La implementación Compose debe reflejar los tokens Material de Xauxa en `XauxaTokens.kt`, incluyendo tema claro y oscuro.

## Tipografía

Xauxa documenta Archivo para display/page/tile headers y Roboto/San Francisco para UI/body/metadata. Cuando las fuentes no estén empaquetadas en el proyecto, se utiliza la familia de sistema sin inventar una identidad tipográfica alternativa.

## Layout

Base de 4px; breakpoints de comportamiento en 480px y 640px; alineación izquierda por defecto; centrado reservado para estados vacíos/error de página completa.

## Motion

150/300/600ms y curvas standard/emphasized/decelerate. Los loops decorativos no forman parte del lenguaje visual.

## Norma de adopción

No se mantiene una segunda identidad visual de Agenda QR. Agenda QR expresa su dominio mediante Xauxa.

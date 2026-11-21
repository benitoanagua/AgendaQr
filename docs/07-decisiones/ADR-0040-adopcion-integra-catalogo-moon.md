# ADR-0040 — Adopción íntegra del catálogo Moon (componentes sin uso incluidos)

**Estado:** ACEPTADO — 2026-11-21
**Relaciona:** ADR-0039, ADR-0031 (adopción de primitivos sin uso), catálogo `05-design-system/13-catalogo-moon.md`.

## Decisión

Se implementan en `core/ui` **todos** los componentes del catálogo Moon (`moon_design` 1.1.0), se usen o no hoy en pantallas. Los que el producto necesita se migran a las pantallas; los demás quedan disponibles, documentados y probados, y se marcan como «disponible» en el catálogo.

## Regla de aceptación de un componente

1. Puerto fiel a la fuente de Moon (anatomía, variantes, tamaños, estados, gestos).
2. Tokens de forma, espacio y movimiento de Moon; color, tipografía e iconos de Polaris.
3. Ficha en `docs/05-design-system/componentes/<nombre>.md` y entrada en `AqDesignSystemGallery` con previews claro y oscuro.
4. Pruebas de contrato (estructura, estados, tamaños) y de accesibilidad (semántica, área táctil ≥ 48 dp, escala de fuente 200 %, reducción de movimiento).
5. Una prueba compara la lista del catálogo con los `Aq*` exportados y la galería, y falla si falta alguno.

«Usado en pantalla» es un requisito adicional solo para los componentes que el producto necesite.

## Justificación

Tener el catálogo completo evita decisiones ad hoc cuando una pantalla nueva pide un patrón, mantiene un único origen de anatomía y estados, y permite verificar fidelidad con Moon de forma sistemática en vez de por pantalla.

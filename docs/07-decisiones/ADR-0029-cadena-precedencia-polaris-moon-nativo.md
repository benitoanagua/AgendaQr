# ADR-0029 — Cadena de precedencia Polaris → Moon → nativo para UI móvil

**Estado:** ACEPTADO — 2026-11-20
**Relaciona:** ADR-0025 (Polaris como autoridad visual), ADR-0026 (cierre de la migración), ADR-0027 (espacio por nivel atómico), ADR-0028 (Moon como referencia de patrones móviles), contrato normativo, `05-design-system/10-auditoria-fidelidad.md` y matriz de gaps `05-design-system/11-matriz-gaps-mobile.md`.

## 1. Contexto y motivación

El sistema visual de AgendaQr tiene como objetivo ser un clon fiel de Shopify Polaris reimplementado en Compose. Sin embargo, Shopify Polaris está históricamente enfocado en aplicaciones web y de escritorio, dejando sin especificación oficial varios patrones de interacción críticos para dispositivos móviles (bottom navigation, bottom sheets, swipe actions, pull-to-refresh, pickers nativos, carruseles y acordeones táctiles).

Para evitar decisiones arbitrarias o mezclas inconsistentes de sistemas de diseño, se requiere una cadena de precedencia normativa cerrada, determinista y auditable.

## 2. Decisión

Toda decisión de interfaz y experiencia de usuario móvil se resuelve en el siguiente orden y solo baja de nivel cuando el nivel anterior no ofrece respuesta:

1. **Nivel 1 — Polaris (instantánea 9.4.2 + documentación oficial):** Convenciones, tokens, componentes, patrones, copy y guía mobile si existe. Siempre gana. Si Polaris solo tiene guía de escritorio para un componente, se anota como «Polaris sin guía mobile» y se adapta usando el criterio de Polaris (tokens, densidad, jerarquía).
2. **Nivel 2 — Moon Design for Flutter (`moon_design` 1.1.0, API en pub.dev/documentation/moon_design):** SOLO si Polaris no define el componente o patrón. Se toma anatomía, estados, tamaños y gestos. El estilo se reemplaza al 100 % con tokens Polaris. **Prohibido** importar paleta, sombras, squircle o tipografía de Moon. No se agrega ninguna dependencia de Moon ni de Flutter.
3. **Nivel 3 — Convención nativa Android (Material 3) / iOS (HIG):** SOLO si ni Polaris ni Moon lo cubren. Se estiliza igualmente con tokens Polaris.

No se puede asignar nivel 3 sin haber descartado por escrito los niveles 1 y 2 en la matriz de gaps.

Esta regla reemplaza cualquier convención previa y aclara que **la navegación también consulta primero Polaris**: Polaris no define barra de navegación móvil inferior, y Moon no trae bottom navigation; por tanto, la barra inferior y el riel se resuelven en el Nivel 3 (nativo), estilizados al 100 % con tokens Polaris.

Esta decisión **complementa** a ADR-0025: la autoridad visual y de diseño sigue siendo exclusivamente Polaris; lo que se formaliza es el orden de resolución para anatomía y patrones móviles que Polaris no cubre directamente.

## 3. Componentes que no trae Moon (descenso a nivel 3)

Moon no incluye: bottom navigation, FAB, search bar, swipe actions, pull-to-refresh, date/time picker, skeleton, empty state, list cell. Cuando Polaris no los cubre o los define sólo para web, se adoptan como nivel 3 nativo estilizados con tokens Polaris.

## 4. Consecuencias

- Se crea la matriz exhaustiva en `docs/05-design-system/11-matriz-gaps-mobile.md` y la auditoría en `docs/05-design-system/10-auditoria-fidelidad.md`.
- Se implementan los componentes primitivos de soporte móvil faltantes en `core/ui`: `AqSwipeActions` (acciones al deslizar filas en listas) y `AqPullToRefresh` (indicador y contenedor de recarga vertical), ambos nivel 3 con tokens Polaris.
- Cada componente nuevo cuenta con ficha de diseño documentada en `docs/05-design-system/componentes/<nombre>.md`.
- La arquitectura sigue garantizando mediante gates que ningún valor visual se hardcodee fuera de tokens.

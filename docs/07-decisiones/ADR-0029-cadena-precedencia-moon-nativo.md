# ADR-0029 — Cadena de precedencia Moon → nativo para UI móvil

> **Reemplazado por [ADR-0044](ADR-0044-moon-unico-sistema-de-diseno.md):** Moon es el único sistema de diseño. Este registro se conserva como historia.

> **Enmienda 2026-11-21 (ADR-0039):** la cadena Moon → nativo queda sustituida por «solo componentes Moon»; lo que Moon no trae se compone con piezas Moon y, en gestos, con mecánica Compose estilizada por Moon.


**Estado:** ACEPTADO — 2026-11-20
**Relaciona:** ADR-0025 (el sistema visual anterior como autoridad visual), ADR-0027 (espacio por nivel atómico), ADR-0028 (Moon como referencia de patrones móviles), contrato normativo y matriz de gaps `05-design-system/11-matriz-gaps-mobile.md`.

## 1. Contexto y motivación

El sistema visual de AgendaQr tiene como objetivo ser un clon fiel de el proveedor del sistema anterior el sistema visual anterior reimplementado en Compose. Sin embargo, el proveedor del sistema anterior el sistema visual anterior se enfoca en aplicaciones web y de escritorio, dejando sin especificación oficial varios patrones de interacción críticos para dispositivos móviles (bottom navigation, bottom sheets, swipe actions, pull-to-refresh, pickers nativos, carruseles y acordeones táctiles).

Para evitar decisiones arbitrarias o mezclas inconsistentes de sistemas de diseño, se requiere una cadena de precedencia normativa cerrada, determinista y auditable.

## 2. Decisión

Toda decisión de interfaz y experiencia de usuario móvil se resuelve en el siguiente orden y solo baja de nivel cuando el nivel anterior no ofrece respuesta:

1. **Nivel 1 — el sistema visual anterior (instantánea 9.4.2 + documentación oficial):** Convenciones, tokens, componentes, patrones, copy y guía mobile si existe. Siempre gana. Si el sistema visual anterior solo tiene guía de escritorio para un componente, se anota como «el sistema visual anterior sin guía mobile» y se adapta usando el criterio del sistema visual anterior (tokens, densidad, jerarquía).
2. **Nivel 2 — Moon Design for Flutter (`moon_design` 1.1.0, API en pub.dev/documentation/moon_design):** SOLO si el sistema visual anterior no define el componente o patrón. Se toma anatomía, estados, tamaños y gestos. El estilo se reemplaza al 100 % con tokens el sistema visual anterior. **Prohibido** importar paleta, sombras, squircle o tipografía de Moon. No se agrega ninguna dependencia de Moon ni de Flutter.
3. **Nivel 3 — Convención nativa Android (Material 3) / iOS (HIG):** SOLO si ni el sistema visual anterior ni Moon lo cubren. Se estiliza igualmente con tokens el sistema visual anterior.

No se puede asignar nivel 3 sin haber descartado por escrito los niveles 1 y 2 en la matriz de gaps.

Esta regla reemplaza cualquier convención previa y aclara que **la navegación también consulta primero el sistema visual anterior**: El sistema visual anterior no define barra de navegación móvil inferior, y Moon no trae bottom navigation; por tanto, la barra inferior y el riel se resuelven en el Nivel 3 (nativo), estilizados al 100 % con tokens el sistema visual anterior.

Esta decisión **complementa** a ADR-0025: la autoridad visual y de diseño sigue siendo exclusivamente el sistema visual anterior; lo que se formaliza es el orden de resolución para anatomía y patrones móviles que el sistema visual anterior no cubre directamente.

## 3. Componentes que no trae Moon (descenso a nivel 3)

Moon no incluye: bottom navigation, FAB, search bar, swipe actions, pull-to-refresh, date/time picker, skeleton, empty state, list cell. Cuando el sistema visual anterior no los cubre o los define sólo para web, se adoptan como nivel 3 nativo estilizados con tokens el sistema visual anterior.

## 4. Consecuencias

- La matriz exhaustiva vive en `docs/05-design-system/11-matriz-gaps-mobile.md`.
- Se implementan los componentes primitivos de soporte móvil faltantes en `core/ui`: `AqSwipeActions` (acciones al deslizar filas en listas) y `AqPullToRefresh` (indicador y contenedor de recarga vertical), ambos nivel 3 con tokens el sistema visual anterior.
- Cada componente nuevo cuenta con ficha de diseño documentada en `docs/05-design-system/componentes/<nombre>.md`.
- La arquitectura sigue garantizando mediante gates que ningún valor visual se hardcodee fuera de tokens.

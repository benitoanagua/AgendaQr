# ADR-0029 — Cadena de precedencia Polaris → Moon → nativo para UI móvil

**Estado:** ACEPTADO — 2026-11-20
**Relaciona:** ADR-0025 (Polaris como autoridad visual), ADR-0026 (cierre de
la migración), ADR-0027 (espacio por nivel atómico), ADR-0028 (Moon como
referencia de patrones móviles), contrato normativo y matriz de gaps
`05-design-system/14-matriz-gaps-mobile.md`.

## 1. Decisión

Toda decisión de UI se resuelve en el siguiente orden y solo baja de nivel
cuando el anterior no tiene respuesta:

1. **Polaris** (instantánea 9.4.2 + documentación oficial). Siempre gana:
   tokens, tipografía, componentes, patrones, copy y guía móvil si existe.
   Si Polaris solo ofrece guía de escritorio para un componente, se anota
   «Polaris sin guía mobile» y se adapta con sus tokens, densidad y
   jerarquía.
2. **Moon Design for Flutter** (`moon_design` 1.1.0, API en
   pub.dev/documentation/moon_design). Solo si Polaris no define el
   componente o patrón. Se toman anatomía, estados, tamaños y gestos; el
   estilo se reemplaza al 100 % por tokens Polaris. **Prohibido** importar
   paleta, sombras, squircle o tipografía de Moon, y prohibida cualquier
   dependencia de Moon o de Flutter.
3. **Convención nativa** Android (Material 3) / iOS (HIG). Solo si ni
   Polaris ni Moon lo cubren; se estiliza igualmente con tokens Polaris.

No se puede asignar nivel 3 sin haber descartado por escrito los niveles 1
y 2 en la matriz de gaps.

Esta regla **complementa** a ADR-0025: la autoridad visual sigue siendo
Polaris; lo que cambia es que la consulta de patrones ya no salta
directamente a la convención nativa — primero Polaris, después Moon como
referencia de interacción, y solo entonces lo nativo.

## 2. Alcance

- Componentes de `core/ui` (API `Aq*`, sin lógica de negocio).
- Patrones de pantalla del contrato UX/UI V1 congelado.
- La navegación raíz sigue siendo nativa (barra inferior/gestos, D-03);
   la diferencia es que ahora ese nivel 3 está **justificado por la cadena**,
   no por omisión.

## 3. Componentes que no trae Moon (nivel 3 directo)

Bottom navigation, FAB, search bar, swipe actions, pull-to-refresh,
pickers de fecha/hora, skeleton, empty state y list cell no existen en el
catálogo de Moon; para ellos el nivel 2 se considera descartado y se pasa a
nivel 3 (o se resuelven con Polaris si existe componente web equivalente,
como es el caso de skeleton y search).

## 4. Consecuencias

- `docs/05-design-system/14-matriz-gaps-mobile.md` registra el nivel y la
  evidencia del descarte de cada patrón de las pantallas V1.
- Los gaps abiertos (swipe actions, pull-to-refresh, menú anclado) quedan
  documentados con nivel y recomendación; no se implementan sin caso de
  producto.
- La gobernanza del contrato (gate `verifyAgendaQrArchitecture`) no cambia:
  los valores visuales siguen saliendo solo de tokens Polaris.

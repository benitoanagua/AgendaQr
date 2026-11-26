# ADR-0044 — Moon como único sistema de diseño

**Estado:** ACEPTADO — 2026-11-23
**Autoridad de implementación:** [Moon Design System — Agenda QR](../05-design-system/README.md).

## Contexto

La documentación acumulaba contratos y decisiones visuales sucesivas que asignaban autoridad distinta a colores, tipografía, iconos, anatomía y patrones móviles. Esa fragmentación permitía que una decisión ya superada siguiera apareciendo como norma.

## Decisión

Moon Design es el único sistema visual y de componentes de Agenda QR.

- Colores: `moon_tokens` 0.0.6 (`MoonColors.light/dark`).
- Componentes, anatomía, tamaños, estados, geometría, opacidades, sombras y movimiento: `moon_design` 1.1.0, commit `177adb7`.
- **Iconografía de producto: Phosphor Icons, exclusivamente** (ADR-0052). Los vectores Moon existentes son legado de migración y deben retirarse.
- Tipografía: escala y pesos Moon; Inter 4.0 es la única sustitución de familia por licencia de Averta Std.
- Implementación: port Kotlin/Compose en `core/ui`; no hay runtime Flutter/Web ni dependencia Moon de ejecución.
- Adaptaciones de plataforma: mecanismos nativos únicamente cuando Moon no ofrece el patrón, compuestos con piezas Moon y tokens semánticos.
- Los componentes Material no se utilizan como autoridad visual ni como sustitutos.
- `AqTokens.kt`, el catálogo Moon y los gates son las fuentes técnicas de la implementación.

## Consecuencias

Las especificaciones visuales activas se consolidan en cuatro documentos: README Moon, contrato, tokens y catálogo; licencias permanecen en su registro. Las auditorías y matrices de migración/fidelidad dejan de ser documentos normativos. Las decisiones visuales previas supersedidas se retiran del árbol activo; su historial permanece en Git.

Todo cambio visual debe actualizar, cuando corresponda, la especificación activa, `core/ui`, la galería, las pruebas y los gates. El build por sí solo no demuestra fidelidad.

# ADR-0044 — Moon como único sistema de diseño

**Estado:** ACEPTADO — 2026-11-23
**Reemplaza:** [ADR-0025](ADR-0025-adopcion-ui-ux-sistema-anterior.md),
[ADR-0028](ADR-0028-moon-patrones-moviles-autoridad-visual-previa.md),
[ADR-0029](ADR-0029-cadena-precedencia-moon-nativo.md),
[ADR-0038](ADR-0038-moon-ux-autoridad-dividida.md) y la excepción de color,
tipografía e iconos de [ADR-0039](ADR-0039-moon-rige-ui-ux.md).

## 1. Contexto

ADR-0039 dejó a Moon rigiendo el UI/UX, pero conservó el sistema visual
anterior para color, tipografía e iconos. Convivir con dos sistemas generaba
confusión: la documentación y el código nombraban autoridades distintas según
la dimensión, y el sistema anterior imponía una condición de uso restrictiva
sobre sus iconos.

## 2. Decisión

Moon Design es el único sistema de diseño.

- **Color:** `MoonColors.light` / `MoonColors.dark` (`moon_tokens` 0.0.6). Los
  valores derivados para AA se documentan en el contrato (PV-01…PV-05).
- **Iconos:** `@heathmont/moon-icons` 10.7.4, 35 glifos en `MoonIconVectors.kt`.
- **Sombras:** `AqShadow` (`MoonShadows`); se elimina `AqElevation`.
- **Tipografía:** escala y pesos de `MoonTextStyles` (cuerpo regular, encabezado
  semibold, sin tracking). La familia sigue siendo Inter, porque Averta Std es
  comercial y no redistribuible.
- **Precedencia:** Moon → nativo estilizado con tokens Moon.
- **Gate:** `verifyNoPreviousDesignSystem` falla si cualquier archivo de texto
  del repo nombra el sistema anterior.

## 3. Consecuencias

- El primario pasa de gris neutro a `piccolo` (#5C33CF); en oscuro, texto,
  icono, foco y borde de énfasis usan `piccolo` aclarado (#A58FE5).
- Los títulos grandes pasan de 36/22 sp a 40/24 sp; los pesos pasan a 400/600.
- Los iconos son de trazo fino (1 sobre 32); Moon no trae todos los conceptos
  (ver X-07 del contrato).
- Hay que regenerar la galería de capturas y revisar el contraste a ojo.
- La licencia MIT de Moon sustituye el aviso restrictivo anterior.

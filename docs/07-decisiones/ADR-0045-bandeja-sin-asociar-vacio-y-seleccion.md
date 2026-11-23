# ADR-0045 — Bandeja «sin asociar»: explicación de vacío y entrada al modo selección

**Estado:** ACEPTADO — 2026-11-23 (decisión delegada: opción más convencional)
**Cierra:** A-09 y A-10 de `docs/05-design-system/12-auditoria-ui-ux-2026-11.md`.
**Relacionado:** ADR-0034 (selección múltiple por lote), ADR-0044.

## 1. Contexto

El contrato `inbox` declaraba acción primaria `—` y gestos vacíos. Resultado:
(A-09) la bandeja vacía no explicaba cómo llegan los comprobantes y (A-10) el
modo selección por lote existía pero no tenía entrada de usuario.

## 2. Decisión

- **A-09:** el estado vacío conserva su título y añade el subtítulo
  `AppStrings.shareFromAnotherAppHint` («Comparte una imagen o PDF con Agenda
  QR desde otra app.»). **Sin botón CTA**: la bandeja es una vista derivada, no
  una superficie de creación; el contrato mantiene acción primaria `—`.
- **A-10:** pulsación larga sobre una fila entra al modo selección con esa fila
  marcada (patrón estándar de listas móviles). Dentro del modo, el toque alterna
  la selección. Nuevo gesto `long-press` en el vocabulario del gate.
  Accesibilidad: `onLongClickLabel` = «Seleccionar comprobante».
- `AqMenuItem` gana `onLongClick` / `onLongClickLabel` opcionales; sin ellos
  usa `clickable` como antes.

## 3. Consecuencias

- Estados del contrato `inbox`: `empty`, `content`, `selection`, `error`.
- No se añaden tokens, literales ni iconos.
- Alternativas descartadas: CTA «Importar» en vacío (duplica el flujo de
  Compartir, requiere navegación nueva); checkbox visible permanente (rompe la
  densidad del Cell-group).

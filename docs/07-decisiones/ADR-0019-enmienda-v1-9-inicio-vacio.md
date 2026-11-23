# ADR-0019 — Enmienda V1.9: inicio vacío y separadores oscuros

## Estado
Aceptado.

## Contexto
El estado vacío de Inicio no usa el FAB como única acción ni carece de botón
propio: el vacío de primera ejecución muestra la acción
"Traer un QR", el FAB se oculta mientras no haya destinos ni búsqueda activa,
y se retira el encabezado "Todos los destinos" sobre el vacío.

## Decisión
- Registrar la enmienda como V1.9 de la especificación UX/UI.
- Separadores y bordes: usan el rol semántico Moon `divider`/`border` del tema; esta decisión de producto no fija colores hex ni reemplaza los tokens Moon.

## Consecuencias
- `DestinationsScreen` actualizado: `scrollable` condicional, acción en el vacío,
  FAB oculto en primera ejecución, sin encabezado sobre el vacío.
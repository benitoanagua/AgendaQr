# ADR-0019 — Enmienda V1.9: inicio vacío y separadores oscuros

## Estado
Aceptado.

## Contexto
La especificación V1.8 definía el estado vacío de Inicio con el FAB cubriendo
la acción y sin botón propio. La ronda de correcciones UX/UI (T4) enmienda
este comportamiento: el vacío de primera ejecución muestra la acción
"Traer un QR", el FAB se oculta mientras no haya destinos ni búsqueda activa,
y se retira el encabezado "Todos los destinos" sobre el vacío.

## Decisión
- Registrar la enmienda como V1.9 de la especificación UX/UI.
- Separadores oscuros: se mantiene `#2B2B2B` (no se cambia a `#3A3A3A`)
  porque las capturas de T10 no mostraron imperceptibilidad; la decisión queda
  registrada aquí y no se modifica `AmSchemeTest`.

## Consecuencias
- `DestinationsScreen` actualizado: `scrollable` condicional, acción en el vacío,
  FAB oculto en primera ejecución, sin encabezado sobre el vacío.
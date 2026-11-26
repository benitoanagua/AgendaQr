# ADR-0052 — Phosphor como catálogo único de iconos de producto

**Estado:** ACEPTADO — migración en curso  
**Fecha:** 2026-11-26  
**Supersede:** sección de iconografía de ADR-0044 y ampliación acotada de ADR-0051.

## Contexto

Agenda QR tenía 35 vectores de producto procedentes de `@heathmont/moon-icons` 10.7.4, expuestos a través de `AqIcons`. ADR-0051 incorporó únicamente Check/X de Phosphor para el switch, dejando dos familias activas y permitiendo que nuevas pantallas siguieran usando el catálogo anterior.

La intención del producto es inequívoca: Phosphor sustituye por completo a Moon Icons. Moon Design continúa siendo referencia de anatomía, componentes, tokens, color, tipografía, movimiento y comportamiento; no es una fuente de glifos.

## Decisión

1. **Phosphor es la única familia de iconos de producto.** No se permiten Moon Icons, Material Icons, emoji ni iconos de otra familia como fallback.
2. **`AqIcons` es la API semántica estable de Agenda QR.** Las pantallas no importan directamente iconos de terceros. La correspondencia semántica y el peso se resuelven en `core/ui`.
3. **Phosphor Bold/Regular/Light** se seleccionan por función y tamaño. Los pesos no se infieren de un nombre de icono ni se mezclan en un mismo patrón sin una regla explícita.
4. **El switch es una composición común.** `AqSwitch` usa Check/X Phosphor por defecto; Lab, galería y producción no definen overrides visuales locales.
5. **No se adopta todavía un wrapper Compose de terceros como dependencia de ejecución.** El candidato `io.github.dev778g-me:phosphoricons-*:1.0.5` se publica con Kotlin 2.3.20 y Compose Multiplatform 1.10.3, mientras Agenda QR está fijado en Kotlin 2.2.20/CMP 1.8.2. La incompatibilidad potencial de metadata y resolución de Compose debe evitarse. Se importa el SVG oficial desde una fuente versionada y se convierte a vectores locales durante el proceso de actualización de iconos; la app no hace llamadas de red ni carga SVG en runtime.
6. **La importación es reproducible.** Cada glifo mantiene nombre Phosphor, nombre de archivo original, peso, versión/commit de origen y licencia. El generador no redibuja ni modifica el path; el tinte se aplica desde `AqIcon`.
7. **La retirada de Moon es verificable.** Tras migrar los call sites, se elimina `MoonIconVectors.kt`, se actualizan licencias/avisos y se añade un gate que falla si aparece cualquier referencia a Moon Icons en código, tests o documentación normativa activa.

## Plan de migración

- **Fase 1 — fuente y contrato:** fijar la versión de Phosphor, documentar la tabla de equivalencias de los 37 conceptos `AqIcons` y definir la regla de pesos.
- **Fase 2 — catálogo:** importar los glifos necesarios a `core/ui` desde SVG oficiales, conservar provenance/licencia y mantener una API vectorial KMP sin runtime externo.
- **Fase 3 — producción:** cambiar todas las equivalencias `AqIcons` a Phosphor, revisar visualmente los significados (no hacer reemplazos ciegos por nombre parecido) y mantener `AqIcon` como punto de render.
- **Fase 4 — enforcement:** pruebas de mapping, tests renderizados, gate de familia única y cobertura de galería; retirar los vectores Moon.
- **Fase 5 — distribución:** actualizar avisos de licencia Android/iOS y documentación, ejecutar compilación/test de Android, iOS y wasmJs (Component Lab).

## Criterios de aceptación

- Cada icono usado en producto procede de Phosphor y tiene una equivalencia semántica revisada.
- `AqIcons` no contiene referencias a `MoonIconVectors`.
- `MoonIconVectors.kt` no existe.
- Ningún código de producción, test o documentación normativa activa menciona `@heathmont/moon-icons` como fuente actual.
- La galería muestra todos los iconos semánticos y pesos usados por el producto.
- Check/X del switch son defaults del `AqSwitch`, no overrides por pantalla.
- Los gates y tests pasan en los targets admitidos.

## Consecuencias

La migración no se considera terminada hasta completar las cinco fases. Mientras exista `MoonIconVectors.kt` o `AqIcons` delegue en él, el estado es **parcial**, no «Phosphor-only».

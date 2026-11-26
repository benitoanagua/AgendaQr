# ADR-0052 — Phosphor como catálogo único de iconos de producto

**Estado:** ACEPTADO — catálogo migrado en código; CI y distribución multiplataforma pendientes  
**Fecha:** 2026-11-26  
**Supersede:** sección de iconografía de ADR-0044 y ampliación acotada de ADR-0051.

## Contexto

Agenda QR tenía 35 vectores de producto procedentes de `@heathmont/moon-icons` 10.7.4, expuestos a través de `AqIcons`. ADR-0051 incorporó Check/X de Phosphor para el switch. La migración actual reemplazó los 35 vectores legacy por 37 glifos Phosphor oficiales y mantuvo 39 conceptos semánticos en `AqIcons` (incluyendo aliases de dirección).

La intención del producto es inequívoca: Phosphor sustituye por completo a Moon Icons. Moon Design continúa siendo referencia de anatomía, componentes, tokens, color, tipografía, movimiento y comportamiento; no es una fuente de glifos.

## Decisión

1. **Phosphor es la única familia de iconos de producto.** No se permiten Moon Icons, Material Icons, emoji ni iconos de otra familia como fallback.
2. **`AqIcons` es la API semántica estable de Agenda QR.** Las pantallas no importan directamente iconos de terceros. La correspondencia semántica y el peso se resuelven en `core/ui`.
3. **Phosphor Bold/Regular/Light** se seleccionan por función y tamaño. Los pesos no se infieren de un nombre de icono ni se mezclan en un mismo patrón sin una regla explícita.
4. **El switch es una composición común.** `AqSwitch` usa Check/X Phosphor por defecto; Lab, galería y producción no definen overrides visuales locales.
5. **No se adopta todavía un wrapper Compose de terceros como dependencia de ejecución.** El candidato `io.github.dev778g-me:phosphoricons-*:1.0.5` se publica con Kotlin 2.3.20 y Compose Multiplatform 1.10.3, mientras Agenda QR está fijado en Kotlin 2.2.20/CMP 1.8.2. La incompatibilidad potencial de metadata y resolución de Compose debe evitarse. Se importa el SVG oficial desde una fuente versionada y se convierte a vectores locales durante el proceso de actualización de iconos; la app no hace llamadas de red ni carga SVG en runtime.
6. **La importación es reproducible.** Cada glifo mantiene nombre Phosphor, nombre de archivo original, peso, versión/commit de origen y licencia. La conversión no redibuja ni modifica el path; el tinte se aplica desde `AqIcon`.
7. **La retirada de Moon es verificable y aplicada.** `MoonIconVectors.kt` y todos sus call sites fueron eliminados; licencias/avisos actualizados y `verifyProductIconCatalog` falla si reaparece la fuente legacy.

## Plan de migración

- **Fase 1 — fuente y contrato: COMPLETADA.** Phosphor `@phosphor-icons/core` v2.0.8 fijado; mapa de equivalencias documentado.
- **Fase 2 — catálogo: COMPLETADA EN CÓDIGO.** 37 SVG oficiales se importaron como vectores KMP locales en `core/ui`; sin dependencia runtime.
- **Fase 3 — producción: COMPLETADA EN CÓDIGO.** Los 39 conceptos de `AqIcons` apuntan a Phosphor; `MoonIconVectors.kt` se eliminó.
- **Fase 4 — enforcement: IMPLEMENTADO, PENDIENTE DE EJECUCIÓN.** `AqIconMappingTest`, `verifyProductIconCatalog` y los gates de configuración compartida están añadidos; CI aún debe confirmar compilación y tests.
- **Fase 5 — distribución: PARCIAL.** Android notice y copia `iosApp/ThirdPartyNotices.txt` están versionados; falta añadir el recurso al target Xcode (proyecto no versionado) y enlazarlo desde la superficie legal/about. CI Android/iOS/wasmJs aún debe confirmar los targets.

## Criterios de aceptación

- Cada icono usado en producto procede de Phosphor y tiene una equivalencia semántica revisada.
- `AqIcons` no contiene referencias a `MoonIconVectors`.
- `MoonIconVectors.kt` no existe.
- `AqIconMappingTest` verifica los 39 conceptos y los 37 glifos Phosphor únicos.
- Ningún código de producción menciona `@heathmont/moon-icons` como fuente actual.
- La galería muestra todos los iconos semánticos y pesos usados por el producto.
- Check/X del switch son defaults del `AqSwitch`, no overrides por pantalla.
- Los gates y tests pasan en los targets admitidos.

## Consecuencias

La migración de código ya es Phosphor-only. El cierre global requiere CI verde, revisión visual de galería/capturas y confirmar distribución/licencia en iOS; hasta entonces la verificación multiplataforma queda **pendiente**, no asumida.

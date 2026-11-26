# ADR-0053 — Configuración única de componentes para Lab y producción

**Estado:** ACEPTADO — implementado en `main`  
**Fecha:** 2026-11-26  
**Relacionado:** ADR-0044, ADR-0049, ADR-0050, ADR-0052.

## Contexto

El Component Lab y la galería de diseño mantenían composiciones de muestra independientes. Aunque podían llamar a los mismos `Aq*`, cada superficie podía definir variantes, slots, tamaños o estados diferentes y dar una falsa impresión de que el Lab representaba producción.

El requisito de Agenda QR es que todos los componentes, sin excepción, usen una sola configuración visual y de comportamiento en Lab y producción.

## Decisión

1. **`core/ui` es la única implementación de componentes.** Los componentes públicos `Aq*` y sus valores visuales provienen de `AqTheme`, `AqTokens` y `LocalAqColorScheme`; no se crean versiones de feature ni versiones especiales para Lab.
2. **`AqComponentRegistry` es la única fuente de muestras, estados y variantes.** Cada estado compone el componente real de `core/ui/components`; no dibuja un mock ni replica su apariencia.
3. **Component Lab y galería consumen el mismo registro.** El Lab añade navegación, selector de estado y registro de eventos. La galería es la vista completa/read-only del mismo catálogo. No mantiene previews de componentes paralelas.
4. **Producción usa los mismos componentes y el mismo tema.** Los roots de aplicación aplican `AqTheme`; las pantallas componen `Aq*` y pueden pasar únicamente valores de estado/datos/slots que formen parte de la API pública del componente. No pueden redefinir estilos internos para aproximar el Lab.
5. **Una excepción debe ser global, no por pantalla.** Si un componente requiere una variante nueva, se agrega al API y al registro compartido, se revisa en ambos temas y targets, y se valida con los gates. No se acepta un override exclusivo de Lab o de producción.
6. **Iconos:** `AqIcons` es la API semántica única y usa exclusivamente Phosphor (ADR-0052). El Lab y producción no importan glifos de forma directa ni configuran iconos estándar del switch por separado.

## Enforcement

- `verifyComponentRegistry`: todo componente público tiene entrada y estados.
- `verifySharedComponentConfiguration`: Lab y galería consumen `AqComponentRegistry`, usan `AqTheme` y no mantienen previews paralelas.
- `verifyProductIconCatalog`: todas las entradas de `AqIcons` apuntan a Phosphor y `MoonIconVectors.kt` no existe.
- `verifyAgendaQrArchitecture` ejecuta estos gates.

## Criterios de aceptación

- Cada componente público `Aq*` aparece en el registro.
- Lab, galería y producción renderizan la misma implementación de componente.
- Las variaciones de tema, escala de fuente y viewport cambian el entorno de prueba, no la configuración del componente.
- No existe una segunda galería con composables de componente duplicados.
- El switch, iconos, botones, campos, selección, navegación, superficies y feedback no tienen configuración exclusiva del Lab.
- Los gates pasan en CI para los targets admitidos.

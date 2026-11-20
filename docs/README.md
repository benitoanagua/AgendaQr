# Agenda QR — Documentación

Índice de la documentación. Cada sección tiene una única fuente de verdad.

## Núcleo

| Documento | Contenido |
|---|---|
| [`PRODUCT.md`](PRODUCT.md) | Producto, alcance, límites, requisitos, dominio y exclusiones de V1 |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | Módulos, fuentes de verdad, local-first, identidad local, Supabase, sync y hosts |
| [`QUALITY.md`](QUALITY.md) | Estrategia de pruebas, suite de estados/eventos, puertas y validación |
| [`STATE.md`](STATE.md) | Estado actual de implementación, NO VERIFICADO, límites y bloqueos |
| [`OPERATIONS.md`](OPERATIONS.md) | Build y pruebas, firma/distribución Android, checklist runtime iOS |

## UX

| Documento | Contenido |
|---|---|
| [`04-ux/01-flujos-confirmados.md`](04-ux/01-flujos-confirmados.md) | Flujos confirmados |
| [`04-ux/02-especificacion-ux-ui-v1.md`](04-ux/02-especificacion-ux-ui-v1.md) | Especificación UX/UI V1 (composición visual; contrato funcional congelado) |
| [`04-ux/03-contrato-interaccion-pantallas.md`](04-ux/03-contrato-interaccion-pantallas.md) | Contrato de interacción por pantalla (generado; verificado por `verifyScreenContract`) |
| [`04-ux/04-capacidades-y-comportamientos.md`](04-ux/04-capacidades-y-comportamientos.md) | Capacidades y comportamientos |
| [`04-ux/05-guia-de-voz.md`](04-ux/05-guia-de-voz.md) | Guía de voz (tuteo) |

## Sistema de diseño (Polaris reimplementado en Kotlin)

| Documento | Contenido |
|---|---|
| [`05-design-system/00-contrato-normativo-diseno.md`](05-design-system/00-contrato-normativo-diseno.md) | Contrato normativo único: fuente, tokens, extensiones y desviaciones |
| [`05-design-system/09-instantanea-tokens-polaris-9.4.2.md`](05-design-system/09-instantanea-tokens-polaris-9.4.2.md) | Instantánea versionada de valores Polaris 9.4.2 |
| [`05-design-system/11-matriz-gaps-mobile.md`](05-design-system/11-matriz-gaps-mobile.md) | Matriz normativa de gaps mobile y cadena de precedencia Polaris → Moon → nativo |
| [`05-design-system/06-licencias-terceros.md`](05-design-system/06-licencias-terceros.md) | Licencias de terceros |
| [`05-design-system/componentes/bottom-sheet.md`](05-design-system/componentes/bottom-sheet.md) | Ficha de componente: Bottom Sheet (`AqBottomSheet`) |
| [`05-design-system/componentes/segmented-control.md`](05-design-system/componentes/segmented-control.md) | Ficha de componente: Segmented Control (`AqSegmentedControl`) |
| [`05-design-system/componentes/chips-filtros.md`](05-design-system/componentes/chips-filtros.md) | Ficha de componente: Chips y Filtros (`AqChipGroup`, `AqChipOption`) |
| [`05-design-system/componentes/toast-snackbar.md`](05-design-system/componentes/toast-snackbar.md) | Ficha de componente: Toast / Snackbar (`AqSnackbarHost`, `AqFeedbackEvent`) |
| [`05-design-system/componentes/search-bar.md`](05-design-system/componentes/search-bar.md) | Ficha de componente: Search Bar (`AqSearchField`, `AqSearchTrigger`) |
| [`05-design-system/componentes/bottom-navigation.md`](05-design-system/componentes/bottom-navigation.md) | Ficha de componente: Bottom Navigation (`AqNavBar`, `AqNavRail`) |
| [`05-design-system/componentes/list-cell.md`](05-design-system/componentes/list-cell.md) | Ficha de componente: List Cell (`AqListRow`, `AqListBody`) |
| [`05-design-system/componentes/empty-state.md`](05-design-system/componentes/empty-state.md) | Ficha de componente: Empty State (`AqEmptyState`) |
| [`05-design-system/componentes/skeleton.md`](05-design-system/componentes/skeleton.md) | Ficha de componente: Skeleton (`AqSkeleton`, `AqProgressBlock`) |
| [`05-design-system/componentes/swipe-actions.md`](05-design-system/componentes/swipe-actions.md) | Ficha de componente: Swipe Actions (`AqSwipeActions`) |
| [`05-design-system/componentes/pull-to-refresh.md`](05-design-system/componentes/pull-to-refresh.md) | Ficha de componente: Pull to Refresh (`AqPullToRefresh`) |
| [`05-design-system/componentes/action-sheet.md`](05-design-system/componentes/action-sheet.md) | Ficha de componente: Action Sheet (`AqActionSheet`) |
| [`05-design-system/componentes/progreso.md`](05-design-system/componentes/progreso.md) | Ficha de componente: Progreso (`AqProgressBar`, `AqProgressBlock`, `AqSpinner`) |
| [`05-design-system/componentes/switch.md`](05-design-system/componentes/switch.md) | Ficha de componente: Switch (`AqSwitch`) |
| [`05-design-system/componentes/tooltip.md`](05-design-system/componentes/tooltip.md) | Ficha de componente: Tooltip (`AqTooltip`) |
| [`05-design-system/componentes/date-time-picker.md`](05-design-system/componentes/date-time-picker.md) | Ficha de componente: Pickers de Fecha/Hora (`AqDatePickerDialog`, `AqTimePickerDialog`) |

## Decisiones

| Documento | Contenido |
|---|---|
| [`07-decisiones/01-decisiones-aprobadas.md`](07-decisiones/01-decisiones-aprobadas.md) | Decisiones aprobadas D-01…D-20 |
| [`07-decisiones/ADR-0003-creacion-de-contextos.md`](07-decisiones/ADR-0003-creacion-de-contextos.md) | ADR: creación de contextos |
| [`07-decisiones/ADR-0004-requeue-dead-letter.md`](07-decisiones/ADR-0004-requeue-dead-letter.md) | ADR: requeue/dead-letter |
| [`07-decisiones/ADR-0006-politica-orientacion.md`](07-decisiones/ADR-0006-politica-orientacion.md) | ADR: política de orientación |
| [`07-decisiones/ADR-0007-capturas-de-referencia.md`](07-decisiones/ADR-0007-capturas-de-referencia.md) | ADR: verificación de layout sin capturas versionadas |
| [`07-decisiones/ADR-0008-guia-de-voz.md`](07-decisiones/ADR-0008-guia-de-voz.md) | ADR: guía de voz |
| [`07-decisiones/ADR-0009-privacidad-en-pantalla.md`](07-decisiones/ADR-0009-privacidad-en-pantalla.md) | ADR: privacidad (propuesto) |
| [`07-decisiones/ADR-0010-uiautomator-en-instrumentados.md`](07-decisiones/ADR-0010-uiautomator-en-instrumentados.md) | ADR: UiAutomator |
| [`07-decisiones/ADR-0016-identidad-local-sin-cuenta.md`](07-decisiones/ADR-0016-identidad-local-sin-cuenta.md) | ADR: identidad local sin cuenta, login no obligatorio, namespace por perfil, PIN, migración y limitaciones |
| [`07-decisiones/ADR-0018-correcciones-ux-captura-rapida.md`](07-decisiones/ADR-0018-correcciones-ux-captura-rapida.md) | ADR: correcciones UX de captura rápida (enmienda ADR-0016 y la congelación UX en su alcance) |
| [`07-decisiones/ADR-0019-enmienda-v1-9-inicio-vacio.md`](07-decisiones/ADR-0019-enmienda-v1-9-inicio-vacio.md) | ADR: enmienda V1.9 inicio vacío y separadores oscuros |
| [`07-decisiones/ADR-0025-adopcion-ui-ux-polaris.md`](07-decisiones/ADR-0025-adopcion-ui-ux-polaris.md) | ADR: adopción del UI/UX de Polaris (autoridad visual) |
| [`07-decisiones/ADR-0027-propiedad-espacio-nivel-atomico.md`](07-decisiones/ADR-0027-propiedad-espacio-nivel-atomico.md) | ADR: propiedad del espacio por nivel atómico y densidad móvil |
| [`07-decisiones/ADR-0028-moon-patrones-moviles-polaris-primario.md`](07-decisiones/ADR-0028-moon-patrones-moviles-polaris-primario.md) | ADR: Moon como referencia de patrones móviles; Polaris autoridad visual |
| [`07-decisiones/ADR-0029-cadena-precedencia-polaris-moon-nativo.md`](07-decisiones/ADR-0029-cadena-precedencia-polaris-moon-nativo.md) | ADR: cadena de precedencia Polaris → Moon → nativo para patrones sin equivalente Polaris |
| [`07-decisiones/ADR-0030-adopcion-primitivos-moviles-pantallas.md`](07-decisiones/ADR-0030-adopcion-primitivos-moviles-pantallas.md) | ADR: adopción de primitivos móviles en pantallas de producción |
| [`07-decisiones/ADR-0031-adopcion-primitivos-componentes-sin-uso.md`](07-decisiones/ADR-0031-adopcion-primitivos-componentes-sin-uso.md) | ADR: política de adopción/retiro de componentes sin uso y criterio de componentes nuevos |

## Cómo se mantiene

- Una sola fuente por tema: producto, arquitectura, calidad, estado y operaciones
  viven en los cinco documentos núcleo; los ADR registran decisiones puntuales.
- `verifyDocs` (dentro de `verifyAgendaQrArchitecture`) verifica enlaces vivos,
  alcanzabilidad de todo `.md` desde este índice y ausencia de marcadores
  transitorios.
- No se documentan aquí resultados de pruebas ad-hoc ni hashes de artefactos: el
  estado verificable vive en `STATE.md` y la historia de cambios en `git log`.

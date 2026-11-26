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

## Sistema de diseño — Moon

| Documento | Autoridad |
|---|---|
| [`05-design-system/README.md`](05-design-system/README.md) | Entrada única: reglas de fidelidad, precedencia, gobernanza y validación |
| [`05-design-system/00-contrato-normativo-diseno.md`](05-design-system/00-contrato-normativo-diseno.md) | Contrato normativo: autoridad Moon, adaptaciones móviles y excepciones del gate |
| [`05-design-system/09-tokens-moon.md`](05-design-system/09-tokens-moon.md) | Valores de color, roles semánticos, tipografía e iconos |
| [`05-design-system/13-catalogo-moon.md`](05-design-system/13-catalogo-moon.md) | Catálogo único de componentes, equivalencias, tamaños y composiciones |
| [`05-design-system/moon-catalogo-referencia.md`](05-design-system/moon-catalogo-referencia.md) | Referencia Moon por widget: API pública, variantes, tamaños y estados (consulta, no norma) |
| [`05-design-system/auditoria-fidelidad-moon.md`](05-design-system/auditoria-fidelidad-moon.md) | Matriz de fidelidad cerrada: 0 desviaciones (registro del ciclo ADR-0047) |
| [`05-design-system/evidencia/informe.md`](05-design-system/evidencia/informe.md) | Informe del comparador pixel/métricas Moon↔Aq (`tools/moon-compare`): 218 pares, 0 desviaciones |
| [`05-design-system/14-verificacion-pixel-moon.md`](05-design-system/14-verificacion-pixel-moon.md) | Proceso de verificación pixel para agentes: harness, comparador, triage y trampas conocidas |
| [`05-design-system/06-licencias-terceros.md`](05-design-system/06-licencias-terceros.md) | Licencias y avisos de recursos redistribuidos |

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
| [`07-decisiones/ADR-0031-adopcion-primitivos-componentes-sin-uso.md`](07-decisiones/ADR-0031-adopcion-primitivos-componentes-sin-uso.md) | ADR: política de adopción/retiro de componentes sin uso y criterio de componentes nuevos |
| [`07-decisiones/ADR-0033-limites-de-campo-y-contador.md`](07-decisiones/ADR-0033-limites-de-campo-y-contador.md) | ADR: límites de campo y contador de caracteres |
| [`07-decisiones/ADR-0034-seleccion-multiple-por-lote.md`](07-decisiones/ADR-0034-seleccion-multiple-por-lote.md) | ADR: selección múltiple por lote y política de confirmación destructiva |
| [`07-decisiones/ADR-0035-insignia-navegacion-badge.md`](07-decisiones/ADR-0035-insignia-navegacion-badge.md) | ADR: insignia de navegación (badge de comprobantes sin asociar) |
| [`07-decisiones/ADR-0036-descarte-teclado-propio-importe.md`](07-decisiones/ADR-0036-descarte-teclado-propio-importe.md) | ADR: descarte del teclado propio para el importe |
| [`07-decisiones/ADR-0037-capacidad-share-extension.md`](07-decisiones/ADR-0037-capacidad-share-extension.md) | ADR: capacidad de plataforma para Share Extension |
| [`07-decisiones/ADR-0043-escala-espaciado-y-gate-de-geometria.md`](07-decisiones/ADR-0043-escala-espaciado-y-gate-de-geometria.md) | ADR: geometría Moon, ancho adaptable y contrato de layout |
| [`07-decisiones/ADR-0044-moon-unico-sistema-de-diseno.md`](07-decisiones/ADR-0044-moon-unico-sistema-de-diseno.md) | ADR: Moon como único sistema de diseño |
| [`07-decisiones/ADR-0045-bandeja-sin-asociar-vacio-y-seleccion.md`](07-decisiones/ADR-0045-bandeja-sin-asociar-vacio-y-seleccion.md) | ADR: bandeja sin asociar — copy de vacío y pulsación larga |
| [`07-decisiones/ADR-0046-puerto-moon-base-control-y-area-tactil.md`](07-decisiones/ADR-0046-puerto-moon-base-control-y-area-tactil.md) | ADR: puerto de MoonBaseControl y área táctil de 48 dp (AqBaseControl / AqTouchTarget) |
| [`07-decisiones/ADR-0047-cierre-fidelidad-moon.md`](07-decisiones/ADR-0047-cierre-fidelidad-moon.md) | ADR: cierre de fidelidad Moon — catálogo de referencia, auditoría y gate `verifyMoonFidelity` |
| [`07-decisiones/ADR-0048-evidencia-pixel-fidelidad-moon.md`](07-decisiones/ADR-0048-evidencia-pixel-fidelidad-moon.md) | ADR: evidencia pixel de fidelidad Moon — comparador, goldens y sombras nativas |

## Cómo se mantiene

- Una sola fuente por tema: producto, arquitectura, calidad, estado y operaciones
  viven en los cinco documentos núcleo; los ADR registran decisiones puntuales.
- `verifyDocs` (dentro de `verifyAgendaQrArchitecture`) verifica enlaces vivos,
  alcanzabilidad de todo `.md` desde este índice y ausencia de marcadores
  transitorios.
- No se documentan aquí resultados de pruebas ad-hoc ni hashes de artefactos: el
  estado verificable vive en `STATE.md` y la historia de cambios en `git log`.

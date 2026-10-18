# Agenda QR — Documentación

Índice de la documentación. Cada sección tiene una única fuente de verdad.

## Núcleo

| Documento | Contenido |
|---|---|
| [`CLEANUP-SUMMARY.md`](CLEANUP-SUMMARY.md) | Resultado del inventario de consolidación UI/UX del 2026-10-17 |
| [`PRODUCT.md`](PRODUCT.md) | Producto, alcance, límites, requisitos, dominio y exclusiones de V1 |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | Módulos, fuentes de verdad, local-first, identidad local, Supabase, sync y hosts |
| [`QUALITY.md`](QUALITY.md) | Estrategia de pruebas, suite de estados/eventos, puertas y validación |
| [`STATE.md`](STATE.md) | Estado actual de implementación, NO VERIFICADO, límites y bloqueos |
| [`OPERATIONS.md`](OPERATIONS.md) | Build y pruebas, firma/distribución Android, checklist runtime iOS |

## UX

| Documento | Contenido |
|---|---|
| [`04-ux/01-flujos-confirmados.md`](04-ux/01-flujos-confirmados.md) | Flujos confirmados |
| [`04-ux/02-especificacion-ux-ui-v1.md`](04-ux/02-especificacion-ux-ui-v1.md) | Especificación UX/UI V1.20 (composición visual; contrato funcional congelado) |
| [`04-ux/03-contrato-interaccion-pantallas.md`](04-ux/03-contrato-interaccion-pantallas.md) | Contrato de interacción por pantalla (generado; verificado por `verifyScreenContract`) |
| [`04-ux/04-capacidades-y-comportamientos.md`](04-ux/04-capacidades-y-comportamientos.md) | Capacidades y comportamientos |
| [`04-ux/05-guia-de-voz.md`](04-ux/05-guia-de-voz.md) | Guía de voz (tuteo) |
| [`04-ux/06-trazabilidad-stitch-tdesign.md`](04-ux/06-trazabilidad-stitch-tdesign.md) | Trazabilidad Stitch ↔ contrato de pantallas (generado; verificado por `verifyScreenContract`) |

## Sistema de diseño (TDesign Mobile)

| Documento | Contenido |
|---|---|
| [`05-design-system/00-contrato-normativo-diseno.md`](05-design-system/00-contrato-normativo-diseno.md) | Contrato normativo (fuente única) |
| [`05-design-system/02-implementacion-diseno.md`](05-design-system/02-implementacion-diseno.md) | Reglas de implementación |
| [`05-design-system/06-licencias-terceros.md`](05-design-system/06-licencias-terceros.md) | Licencias de terceros |
| [`05-design-system/07-evaluacion-tipografia.md`](05-design-system/07-evaluacion-tipografia.md) | Evaluación de la tipografía candidata MiSans Global (no adoptada) |
| [`05-design-system/08-referencia-super-app.md`](05-design-system/08-referencia-super-app.md) | Referencia histórica Super-App (suplantada por ADR-0023) |

## Decisiones

| Documento | Contenido |
|---|---|
| [`07-decisiones/01-decisiones-aprobadas.md`](07-decisiones/01-decisiones-aprobadas.md) | Decisiones aprobadas D-01…D-20 |
| [`07-decisiones/ADR-0003-creacion-de-contextos.md`](07-decisiones/ADR-0003-creacion-de-contextos.md) | ADR: creación de contextos |
| [`07-decisiones/ADR-0004-requeue-dead-letter.md`](07-decisiones/ADR-0004-requeue-dead-letter.md) | ADR: requeue/dead-letter |
| [`07-decisiones/ADR-0005-lenguaje-visual-metro.md`](07-decisiones/ADR-0005-lenguaje-visual-metro.md) | ADR histórico sustituido: Metro |
| [`07-decisiones/ADR-0006-politica-orientacion.md`](07-decisiones/ADR-0006-politica-orientacion.md) | ADR: política de orientación |
| [`07-decisiones/ADR-0007-capturas-de-referencia.md`](07-decisiones/ADR-0007-capturas-de-referencia.md) | ADR: verificación de layout sin capturas versionadas |
| [`07-decisiones/ADR-0008-guia-de-voz.md`](07-decisiones/ADR-0008-guia-de-voz.md) | ADR: guía de voz |
| [`07-decisiones/ADR-0009-privacidad-en-pantalla.md`](07-decisiones/ADR-0009-privacidad-en-pantalla.md) | ADR: privacidad (propuesto) |
| [`07-decisiones/ADR-0010-uiautomator-en-instrumentados.md`](07-decisiones/ADR-0010-uiautomator-en-instrumentados.md) | ADR: UiAutomator |
| [`07-decisiones/ADR-0011-contrato-visual-xauxa.md`](07-decisiones/ADR-0011-contrato-visual-xauxa.md) | ADR histórico sustituido: Xauxa |
| [`07-decisiones/ADR-0012-ui-convencional-material3.md`](07-decisiones/ADR-0012-ui-convencional-material3.md) | ADR histórico; autoría visual sustituida por ADR-0013 |
| [`07-decisiones/ADR-0013-sistema-de-diseno-agendamobile.md`](07-decisiones/ADR-0013-sistema-de-diseno-agendamobile.md) | ADR histórico; autoría visual sustituida por ADR-0014 |
| [`07-decisiones/ADR-0014-referencia-ant-design-mobile.md`](07-decisiones/ADR-0014-referencia-ant-design-mobile.md) | ADR histórico: referencia Ant Design Mobile (suplantada por ADR-0020 y ADR-0021) |
| [`07-decisiones/ADR-0015-referencia-stitch-jerarquia-financiera.md`](07-decisiones/ADR-0015-referencia-stitch-jerarquia-financiera.md) | ADR histórico: patrones financieros de la referencia Stitch (segmentado, tags tonales, hero QR); su fuente Stitch fue sustituida por ADR-0024 |
| [`07-decisiones/ADR-0016-identidad-local-sin-cuenta.md`](07-decisiones/ADR-0016-identidad-local-sin-cuenta.md) | ADR: identidad local sin cuenta, login no obligatorio, namespace por perfil, PIN, migración y limitaciones |
| [`07-decisiones/ADR-0017-alineacion-fiel-ant-design-mobile.md`](07-decisiones/ADR-0017-alineacion-fiel-ant-design-mobile.md) | ADR histórico: alineación con Ant Design Mobile 5.43.0 (suplantada por ADR-0020 y ADR-0021) |
| [`07-decisiones/ADR-0018-correcciones-ux-captura-rapida.md`](07-decisiones/ADR-0018-correcciones-ux-captura-rapida.md) | ADR: correcciones UX de captura rápida (enmienda ADR-0016 y la congelación UX en su alcance) |
| [`07-decisiones/ADR-0019-enmienda-v1-9-inicio-vacio.md`](07-decisiones/ADR-0019-enmienda-v1-9-inicio-vacio.md) | ADR: enmienda V1.9 inicio vacío y separadores oscuros |
| [`07-decisiones/ADR-0020-identidad-visual-agendaqr.md`](07-decisiones/ADR-0020-identidad-visual-agendaqr.md) | ADR histórico: identidad visual AgendaQr (suplantado por ADR-0023) |
| [`07-decisiones/ADR-0021-referencias-apps-chinas-por-pantalla.md`](07-decisiones/ADR-0021-referencias-apps-chinas-por-pantalla.md) | ADR histórico: referencias por pantalla y auditoría de brecha (suplantado por ADR-0023) |
| [`07-decisiones/ADR-0022-direccion-visual-super-app.md`](07-decisiones/ADR-0022-direccion-visual-super-app.md) | ADR histórico: Super-App (suplantado por ADR-0023) |
| [`07-decisiones/ADR-0023-adopcion-fiel-tdesign-mobile.md`](07-decisiones/ADR-0023-adopcion-fiel-tdesign-mobile.md) | ADR: adopción fiel de TDesign Mobile (autoridad de tokens y componentes vigente) |
| [`07-decisiones/ADR-0024-stitch-tdesign-screen-templates.md`](07-decisiones/ADR-0024-stitch-tdesign-screen-templates.md) | ADR: el proyecto Stitch `TDesign Screen Templates` es la referencia primaria de composición UX/UI (enmienda ADR-0023; sustituye la fuente Stitch de ADR-0015) |

## Cómo se mantiene

- Una sola fuente por tema: producto, arquitectura, calidad, estado y operaciones
  viven en los cinco documentos núcleo; los ADR registran decisiones puntuales.
- `verifyDocs` (dentro de `verifyAgendaQrArchitecture`) verifica enlaces vivos,
  alcanzabilidad de todo `.md` desde este índice y ausencia de marcadores
  transitorios.
- No se documentan aquí resultados de pruebas ad-hoc ni hashes de artefactos: el
  estado verificable vive en `STATE.md` y la historia de cambios en `git log`.

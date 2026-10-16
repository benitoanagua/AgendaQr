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
| [`04-ux/02-especificacion-ux-ui-v1.md`](04-ux/02-especificacion-ux-ui-v1.md) | Especificación UX/UI V1.7 (congelada) |
| [`04-ux/04-capacidades-y-comportamientos.md`](04-ux/04-capacidades-y-comportamientos.md) | Capacidades y comportamientos |
| [`04-ux/05-guia-de-voz.md`](04-ux/05-guia-de-voz.md) | Guía de voz (tuteo) |

## Sistema de diseño (referencia Ant Design Mobile)

| Documento | Contenido |
|---|---|
| [`05-design-system/00-contrato-normativo-diseno.md`](05-design-system/00-contrato-normativo-diseno.md) | Contrato normativo (fuente única) |
| [`05-design-system/02-implementacion-diseno.md`](05-design-system/02-implementacion-diseno.md) | Reglas de implementación |
| [`05-design-system/06-licencias-terceros.md`](05-design-system/06-licencias-terceros.md) | Licencias de terceros |

## Decisiones

| Documento | Contenido |
|---|---|
| [`07-decisiones/01-decisiones-aprobadas.md`](07-decisiones/01-decisiones-aprobadas.md) | Decisiones aprobadas D-01…D-18 |
| [`07-decisiones/ADR-0003-creacion-de-contextos.md`](07-decisiones/ADR-0003-creacion-de-contextos.md) | ADR: creación de contextos |
| [`07-decisiones/ADR-0004-requeue-dead-letter.md`](07-decisiones/ADR-0004-requeue-dead-letter.md) | ADR: requeue/dead-letter |
| [`07-decisiones/ADR-0005-lenguaje-visual-metro.md`](07-decisiones/ADR-0005-lenguaje-visual-metro.md) | ADR histórico sustituido: Metro |
| [`07-decisiones/ADR-0006-politica-orientacion.md`](07-decisiones/ADR-0006-politica-orientacion.md) | ADR: política de orientación |
| [`07-decisiones/ADR-0007-capturas-de-referencia.md`](07-decisiones/ADR-0007-capturas-de-referencia.md) | ADR: capturas de referencia |
| [`07-decisiones/ADR-0008-guia-de-voz.md`](07-decisiones/ADR-0008-guia-de-voz.md) | ADR: guía de voz |
| [`07-decisiones/ADR-0009-privacidad-en-pantalla.md`](07-decisiones/ADR-0009-privacidad-en-pantalla.md) | ADR: privacidad (propuesto) |
| [`07-decisiones/ADR-0010-uiautomator-en-instrumentados.md`](07-decisiones/ADR-0010-uiautomator-en-instrumentados.md) | ADR: UiAutomator |
| [`07-decisiones/ADR-0011-contrato-visual-xauxa.md`](07-decisiones/ADR-0011-contrato-visual-xauxa.md) | ADR histórico sustituido: Xauxa |
| [`07-decisiones/ADR-0012-ui-convencional-material3.md`](07-decisiones/ADR-0012-ui-convencional-material3.md) | ADR histórico; autoría visual sustituida por ADR-0013 |
| [`07-decisiones/ADR-0013-sistema-de-diseno-agendamobile.md`](07-decisiones/ADR-0013-sistema-de-diseno-agendamobile.md) | ADR histórico; autoría visual sustituida por ADR-0014 |
| [`07-decisiones/ADR-0014-referencia-ant-design-mobile.md`](07-decisiones/ADR-0014-referencia-ant-design-mobile.md) | ADR: referencia Ant Design Mobile + navegación Android/iOS (enmendada por ADR-0017) |
| [`07-decisiones/ADR-0015-referencia-stitch-jerarquia-financiera.md`](07-decisiones/ADR-0015-referencia-stitch-jerarquia-financiera.md) | ADR: patrones financieros de la referencia Stitch (segmentado, tags tonales, hero QR); paleta e Inter no adoptadas (acotada por ADR-0017) |
| [`07-decisiones/ADR-0016-identidad-local-sin-cuenta.md`](07-decisiones/ADR-0016-identidad-local-sin-cuenta.md) | ADR: identidad local sin cuenta, login no obligatorio, namespace por perfil, PIN, migración y limitaciones |
| [`07-decisiones/ADR-0017-alineacion-fiel-ant-design-mobile.md`](07-decisiones/ADR-0017-alineacion-fiel-ant-design-mobile.md) | ADR: alineación fiel con Ant Design Mobile 5.43.0 (enmienda ADR-0014, acota ADR-0015) |

## Cómo se mantiene

- Una sola fuente por tema: producto, arquitectura, calidad, estado y operaciones
  viven en los cinco documentos núcleo; los ADR registran decisiones puntuales.
- `verifyDocs` (dentro de `verifyAgendaQrArchitecture`) verifica enlaces vivos,
  alcanzabilidad de todo `.md` desde este índice y ausencia de marcadores
  transitorios.
- No se documentan aquí resultados de pruebas ad-hoc ni hashes de artefactos: el
  estado verificable vive en `STATE.md` y la historia de cambios en `git log`.

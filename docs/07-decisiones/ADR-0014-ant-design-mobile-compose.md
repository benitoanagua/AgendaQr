# ADR-0014 — Ant Design Mobile replicado en Compose

**Estado:** APROBADA — 2026-10-12.
**Suplanta:** la autoridad visual de ADR-0013 (AgendaMobile). No revierte la eliminación de Metro/Xauxa ni la retirada del laboratorio de componentes.

## Contexto

La capa visual propia anterior se convirtió en una identidad independiente de la referencia solicitada. La nueva dirección es clonar fielmente Ant Design Mobile (https://mobile.ant.design/) y dejar de usar Vant como referencia. Agenda QR usa Kotlin Multiplatform/Compose; Ant Design Mobile es una biblioteca React para web móvil, así que la réplica debe ser nativa, no una dependencia web incrustada.

## Decisión

1. Ant Design Mobile es la autoridad visual para tokens, componentes, estados, densidad, formularios, listas, tabs, feedback y patrones de interacción.
2. La réplica se implementa en `core/ui` con Compose Multiplatform. Se conservan temporalmente los nombres de código `Am*` para reducir el riesgo; no se presentan como un sistema visual propio.
3. Vant se elimina de la dirección activa: no dependencias, componentes, tokens, documentación normativa ni criterios de aceptación basados en Vant. Su mención solo puede permanecer en el historial de decisiones.
4. Navegación y layout siguen convenciones móviles: barra inferior en 3–5 destinos raíz, barra superior con Atrás en pantallas hijas, stack restaurable, IME/insets, selectores nativos y navegación adaptativa en pantallas grandes.
5. Se preservan rutas, ViewModels, modelos, persistencia, reglas de dominio, copy de producto, permisos y recuperación de errores.
6. La paleta Ant Design se replica con fidelidad; cualquier desviación necesaria para contraste AA se limita al rol específico (por ejemplo, acción sólida accesible) y se prueba.
7. No se añade React, DOM, CSS, npm, target WebAssembly ni laboratorio de componentes.

## Plan de ejecución por lotes

- Lote A: contrato, ADR, especificación UX/UI y tokens principales.
- Lote B: botones, campos, listas, filas, chips, estados, diálogos, feedback e iconografía.
- Lote C: barra superior/inferior, navegación Atrás, layouts compactos/expandidos y patrones nativos Android/iOS.
- Lote D: actualizar capturas, pruebas de accesibilidad/contraste, eliminar referencias activas a Vant/AgendaMobile y validar CI.

Cada lote debe compilar y probarse antes de continuar; nunca se rebajan tests para forzar el verde. No se declara completada una plataforma sin evidencia.

## Criterios de aceptación

- Los tokens y componentes comparados con la referencia oficial tienen correspondencia documentada.
- No quedan referencias activas a Vant ni a AgendaMobile como autoridad visual.
- La navegación se comporta como una app móvil: raíz, detalle, Atrás, restauración, teclado y safe areas.
- Los flujos de producto y reglas de negocio no cambian.
- Tests y CI ejecutados con resultados reales; runtime iOS se marca pendiente cuando no hay Xcode/dispositivo.
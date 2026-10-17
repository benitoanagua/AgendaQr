# Decisiones aprobadas — V1

## D-01 — Propósito

Agenda QR conserva destinos QR y respaldo de operaciones para poder recuperarlos y compartirlos.

## D-02 — Operaciones V1

Los tipos de operación son exclusivamente `PAGO` y `COBRO`.

## D-03 — Operación mínima

Una operación requiere únicamente `id`, `type` y `occurredAt`. Todo el resto es opcional.

## D-04 — Dos timestamps

`occurredAt` y `createdAt` representan eventos diferentes.

## D-05 — Comprobantes first-class

El comprobante es una entidad propia, independiente de la operación.

## D-06 — Asociación flexible

Un comprobante puede estar sin operación o asociado a una operación. La asociación es reversible.

## D-07 — Múltiples comprobantes

Una operación puede tener múltiples comprobantes.

## D-08 — Proveniencia

La proveniencia se limita a `ENVIADO`, `RECIBIDO` y `DESCONOCIDO`.

## D-09 — Guardar primero

Guardar un comprobante debe requerir el mínimo absoluto de interacción.

## D-10 — Duplicados

Se alerta sobre posible duplicado, pero no se bloquea el guardado.

## D-11 — Ediciones sensibles

Solo los cambios sensibles de una operación que ya tiene comprobantes requieren confirmación contextual.

## D-12 — Eliminación

Eliminar una operación elimina sus comprobantes asociados y conserva únicamente un histórico mínimo.

## D-13 — Destinos históricos

Cambiar el QR actual de un destino no reescribe operaciones históricas.

## D-14 — No verificación bancaria

Agenda QR no afirma que un comprobante recibido implique que el dinero fue verificado.

## D-15 — No V1

CRM, obligaciones, saldos, pagos parciales, reembolsos especializados, contabilidad, conciliación bancaria, OCR de comprobantes, IA y cloud complejo quedan fuera de V1.

## D-16 — UX

La UI/UX permanece bajo un único sistema de diseño; esta consolidación no crea un sistema visual paralelo. (Historial: D-16 nombraba a Xauxa; ADR-0005/0012 lo mantuvieron como único sistema. ADR-0013 introdujo un sistema propio. Enmienda 2026-10-13, ADR-0014: la referencia visual es Ant Design Mobile; la autoría propia queda retirada.)

## D-17 — UX/UI V1 congelada

La especificación `docs/04-ux/02-especificacion-ux-ui-v1.md` es el contrato UX/UI V1 congelado.

Quedan consolidados, sin alterar las reglas de negocio existentes:

- una intención principal por pantalla;
- búsqueda global;
- Galería → Desde otra app → Cámara como jerarquía de entrada;
- registro sin contexto obligatorio;
- comprobantes independientes y asociación reversible;
- importación automática y revisión solo cuando sea necesaria;
- importación masiva tolerante a fallos parciales;
- guardado local y sincronización como estados distintos;
- errores recuperables;
- preservación de intención/draft;
- invariantes de idempotencia y recuperación.

La implementación debe adaptarse al contrato UX/UI; un fallo de implementación no modifica automáticamente la especificación.

## D-18 — Sistema visual de AgendaQr (ADR-0020, parcialmente suplantada)

La identidad visual activa es propia de AgendaQr: una herramienta móvil moderna, compacta y pulida, inspirada en aplicaciones chinas de consumo por su utilidad cotidiana, jerarquía funcional y densidad controlada. No replica una librería ni introduce dependencias web. Se implementa en Kotlin/Compose Multiplatform dentro de `core/ui`, con tokens semánticos, temas claro/oscuro y componentes compartidos. Material 3 es detalle de implementación; Android e iOS conservan sus convenciones de navegación.

La paleta usa azul tecnológico, superficies neutras con matiz azul-gris y estados semánticos accesibles. Lucide y la fuente de sistema se conservan en esta fase; cualquier cambio de iconografía o tipografía requiere comparación visual, revisión de licencia y validación local. Las decisiones anteriores sobre Ant Design Mobile (ADR-0014/0017) quedan como historia, no como autoridad visual activa. ADR-0021 fija las referencias de patrón por pantalla (apps chinas de consumo) sin copiar activos. La especificación funcional vigente es V1.9. El estado real de validación por plataforma se documenta únicamente en `docs/STATE.md`.

> **Parcialmente suplantada por D-19 (ADR-0022):** la **paleta** y la **escala tipográfica** de D-18 quedan reemplazadas por la dirección Super-App de alta densidad. De D-18 siguen vigentes la navegación de plataforma, el lienzo QR blanco, Lucide, la fuente de sistema, el prefijo `Am*` y la ausencia de dependencias web.

## D-19 — Dirección visual Super-App de alta densidad (ADR-0022)

La autoridad visual vigente es la especificación Super-App de alta densidad (`docs/05-design-system/08-referencia-super-app.md`): densidad, vibrancia, gradiente de acción rojo→naranja, escala 14/12 (mínimo 11 sp), rejilla de accesos y sombras por niveles `AmElevation`. ADR-0022 suplanta la paleta y la escala de ADR-0020, enmienda ADR-0021 (Inicio gana rejilla de acciones existentes y pierde el FAB con hub) y la spec S01, y resuelve los conflictos con los pisos de accesibilidad ajustando valores, no quitando el estilo: contraste, foco por teclado, objetivos ≥ 48 dp, escalado al 200 %, movimiento reducido y contenido en movimiento detenible. Lo que el producto no puede sostener con datos reales (carrito, cuentas regresivas, monedas, niveles, «HOT/限时/-50 %», servicio flotante, insignias de conteo) no se simula. El estado real de validación por plataforma se documenta únicamente en `docs/STATE.md`.

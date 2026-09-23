# Validación — Pruebas V1

La validación funcional cubre destinos QR, operaciones, comprobantes y el comportamiento UX congelado.

## Suite UX/UI

La matriz completa de estados, eventos, recuperación, caos e invariantes está en `02-suite-estados-eventos-v1.md`.

## Destinos / QR

- incorporar desde galería;
- incorporar desde «Desde otra app»;
- incorporar desde cámara;
- incorporar múltiples imágenes;
- detectar/leer QR;
- revisar candidato;
- guardar destino;
- detectar duplicado sin eliminación automática;
- buscar;
- mostrar;
- compartir;
- reemplazar;
- recuperar sin conexión.

## Operaciones

- crear PAGO mínimo;
- crear COBRO mínimo;
- crear operación sin contexto/destino;
- conservar draft al seleccionar contexto;
- evitar duplicado por doble guardado;
- crear operación sin comprobante;
- adjuntar comprobante después.

## Comprobantes

- guardar comprobante sin operación;
- guardar desde galería/otra app;
- asociar después;
- desasociar;
- reasociar;
- múltiples comprobantes;
- duplicado detectado sin bloqueo;
- ambigüedad;
- compartir;
- eliminar comprobante.

## Importación masiva

- separar reconocidos, duplicados y pendientes;
- guardar reconocidos aunque existan pendientes;
- fallo parcial de un elemento;
- revisión posterior de pendientes.

## Offline / sincronización

- guardar localmente sin conexión;
- pasar a pendiente de sincronización;
- sincronizar al recuperar conexión;
- conservar información ante fallo remoto;
- reintentar sincronización;
- diferenciar «Guardado» de «Sincronizado».

## Recuperación y caos

- Back conserva draft;
- Cancelar abandona la intención;
- doble tap no duplica;
- eventos repetidos no duplican;
- evento tardío no retrocede estado;
- cierre durante guardado;
- cierre durante análisis;
- latencia extrema;
- estado parcial;
- búsqueda sin resultados;
- búsqueda offline.

## Integridad

- editar campo sensible con comprobante;
- editar campo no sensible;
- eliminar operación con comprobantes;
- consultar histórico mínimo tras eliminación;
- cambiar QR del destino y verificar que la historia no se reescribe.

## Accesibilidad

- targets mínimos;
- focus visible;
- texto largo;
- dynamic type;
- lector de pantalla;
- reduced motion;
- estados comprensibles sin depender solo del color.

## Criterio

Los escenarios conceptuales pueden declararse PASS antes de implementación. Los casos que dependen de runtime, persistencia, plataforma o dispositivos permanecen pendientes hasta disponer de evidencia real.

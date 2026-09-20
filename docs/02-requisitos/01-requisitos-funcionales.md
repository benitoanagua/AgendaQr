# Requisitos funcionales confirmados — V1

## Destinos QR

### RF-01 — Incorporar QR

El usuario puede incorporar QR mediante cámara, galería, múltiples imágenes y hoja de compartir del sistema.

### RF-02 — Leer y clasificar QR

El sistema intenta detectar, decodificar y clasificar el contenido del QR. La lectura/clasificación sirve para gestionar y organizar el recurso.

Un QR no se convierte automáticamente en un destino guardado.

### RF-03 — Guardar destino

Un QR leído puede convertirse en un destino guardado para reutilización.

La organización puede incluir nombre, categoría, nota y favorito.

### RF-04 — Buscar destino

El usuario puede buscar y recuperar destinos QR guardados.

### RF-05 — Mostrar y compartir destino

El usuario puede mostrar el QR a pantalla completa y compartirlo mediante las capacidades del sistema.

### RF-06 — Mantener destino

El usuario puede editar metadatos, reemplazar el QR y eliminar el destino.

Los cambios del destino no modifican operaciones históricas que ya estaban asociadas a otro contexto.

### RF-07 — Persistencia local y offline

La información principal se mantiene localmente. La recuperación y visualización principal de destinos funciona sin conexión.

## Operaciones

### RF-08 — Registrar operación

El usuario puede registrar una operación de tipo `PAGO` o `COBRO`.

El mínimo obligatorio de una operación es:

- `id`;
- `type`;
- `occurredAt`.

Los demás datos son opcionales:

- importe;
- moneda;
- persona/entidad;
- destino;
- concepto;
- nota.

### RF-09 — Separar momento ocurrido y momento registrado

`occurredAt` representa cuándo ocurrió la operación.

`createdAt` representa cuándo Agenda QR registró la operación.

Son datos diferentes y no deben confundirse.

## Comprobantes

### RF-10 — Guardar comprobante

Un comprobante puede guardarse con la mínima interacción posible.

Debe ser válido guardar un comprobante sin completar metadatos y sin asociarlo a una operación.

### RF-11 — Proveniencia del comprobante

La proveniencia describe cómo llegó el comprobante a Agenda QR:

- `ENVIADO`;
- `RECIBIDO`;
- `DESCONOCIDO`.

`RECIBIDO` no significa dinero verificado.

Cuando el contexto ya determina la proveniencia, no es necesario preguntar de nuevo.

### RF-12 — Asociar comprobante

Un comprobante puede estar asociado a cero o una operación.

Una operación puede tener cero o múltiples comprobantes.

La asociación puede cambiarse sin eliminar el archivo.

### RF-13 — Comprobante independiente

Un comprobante no asociado puede permanecer guardado y recuperarse/compartirse independientemente.

### RF-14 — Duplicados

Ante un comprobante posiblemente duplicado, Agenda QR informa al usuario pero no bloquea el guardado.

## Edición y eliminación

### RF-15 — Editar operación

Cambios sensibles cuando existen comprobantes:

- tipo;
- fecha/hora ocurrida;
- importe;
- moneda;
- persona/entidad;
- destino.

Esos cambios requieren una única confirmación contextual.

Cambios no sensibles:

- concepto;
- nota;
- organización.

No requieren la misma confirmación.

Editar una operación nunca modifica el archivo del comprobante.

### RF-16 — Eliminar operación

Al eliminar una operación:

- se eliminan sus archivos de comprobante asociados;
- se conserva un registro histórico mínimo.

El histórico conserva únicamente:

- fecha;
- tipo;
- importe;
- persona/entidad.

No conserva la imagen/PDF del comprobante ni el QR.

### RF-17 — Eliminar comprobante

Eliminar un comprobante elimina el archivo y deja intacta la operación, si existe.

## Recuperación

### RF-18 — Buscar operaciones

Los resultados deben poder localizarse por fecha, tipo, importe y persona/entidad.

### RF-19 — Compartir comprobante

El usuario puede abrir y compartir un comprobante cuando el archivo exista.

## Protección

### RF-20 — Protección local opcional

La aplicación contempla biometría/PIN como capacidad local opcional.

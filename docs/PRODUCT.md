# Agenda QR — Producto

Fuente única de producto: concepto, alcance, límites, requisitos, modelo de
dominio y exclusiones de V1. Consolida los antiguos documentos de
`01-producto`, `02-requisitos`, `03-dominio` y `10-fuera-de-alcance`.

## 1. Concepto y problema

**Agenda QR guarda tus destinos de pago/cobro y conserva el respaldo de tus
operaciones para que puedas encontrarlo y compartirlo cuando lo necesites.**

El producto combina dos necesidades relacionadas:

- conservar destinos QR reutilizables;
- conservar evidencia de operaciones y recuperarla rápidamente.

Principio de uso:

**Capturar rápido. Organizar cuando sea necesario. Recuperar siempre.**

### Problema

Los destinos QR y los comprobantes suelen quedar dispersos entre mensajería,
galería, correo, notas y otras aplicaciones. Cuando una persona necesita
reutilizar un destino o demostrar una operación, debe volver a buscar entre
esos lugares.

Agenda QR centraliza ese respaldo sin ejecutar el pago ni convertirse en un
sistema financiero.

### Flujo central

- **Destino QR:** incorporar → guardar → organizar → buscar → mostrar →
  reutilizar/compartir.
- **Operación:** registrar PAGO/COBRO → adjuntar comprobante cuando exista →
  recuperar → compartir.
- **Comprobante:** guardar con mínimo esfuerzo → asociar después cuando sea
  necesario.

### Frontera del producto

Agenda QR registra y conserva información proporcionada por el usuario. No
confirma que el dinero haya llegado, no mueve dinero y no sustituye al banco,
billetera, comercio o sistema contable.

## 2. Alcance confirmado V1

### Destinos QR

- incorporar mediante cámara, galería, múltiples imágenes y hoja de compartir
  del sistema;
- detectar y leer QR;
- clasificar el contenido cuando la lectura lo permita;
- guardar un destino reutilizable;
- organizar mediante nombre, categoría, nota y favorito;
- buscar y recuperar;
- mostrar a pantalla completa;
- compartir;
- editar;
- reemplazar QR;
- eliminar;
- persistencia local;
- recuperación y visualización principal sin conexión.

### Operaciones

La V1 permite registrar dos tipos: `PAGO` y `COBRO`. Una operación puede
registrarse aunque todavía no tenga comprobante.

### Comprobantes

- guardar un comprobante;
- guardar un comprobante sin operación asociada;
- asociar posteriormente;
- desasociar y volver a asociar;
- conservar múltiples comprobantes por operación;
- compartir un comprobante;
- detectar posible duplicado sin bloquear;
- eliminar el comprobante de forma independiente cuando corresponda.

### Búsqueda

La recuperación de operaciones/comprobantes considera como datos principales:
fecha, tipo, importe y persona/entidad. No se requiere OCR del contenido del
comprobante para V1.

### Protección

- biometría/PIN como capacidad local opcional.

## 3. Límites de V1

Agenda QR no es:

- un banco;
- una billetera;
- un procesador de pagos;
- un sistema de transferencias;
- un sistema de custodia de dinero;
- un verificador bancario;
- un sistema contable;
- un CRM en V1.

Un comprobante recibido **no significa que el dinero haya sido verificado**.

Agenda QR conserva evidencia y registros introducidos por el usuario. El pago o
cobro financiero ocurre fuera de la aplicación.

La V1 tampoco resuelve obligaciones, saldos, pagos parciales ni flujos
especializados de devolución/reintegro.

## 4. Requisitos funcionales

### Destinos QR

- **RF-01 — Incorporar QR.** El usuario puede incorporar QR mediante cámara,
  galería, múltiples imágenes y hoja de compartir del sistema.
- **RF-02 — Leer y clasificar QR.** El sistema intenta detectar, decodificar y
  clasificar el contenido del QR. La lectura/clasificación sirve para gestionar
  y organizar el recurso. Un QR no se convierte automáticamente en un destino
  guardado.
- **RF-03 — Guardar destino.** Un QR leído puede convertirse en un destino
  guardado para reutilización. La organización puede incluir nombre, categoría,
  nota y favorito.
- **RF-04 — Buscar destino.** El usuario puede buscar y recuperar destinos QR
  guardados.
- **RF-05 — Mostrar y compartir destino.** El usuario puede mostrar el QR a
  pantalla completa y compartirlo mediante las capacidades del sistema.
- **RF-06 — Mantener destino.** El usuario puede editar metadatos, reemplazar
  el QR y eliminar el destino. Los cambios del destino no modifican operaciones
  históricas que ya estaban asociadas a otro contexto.
- **RF-07 — Persistencia local y offline.** La información principal se
  mantiene localmente. La recuperación y visualización principal de destinos
  funciona sin conexión.

### Operaciones

- **RF-08 — Registrar operación.** El usuario puede registrar una operación de
  tipo `PAGO` o `COBRO`. El mínimo obligatorio es `id`, `type` y `occurredAt`.
  Los demás datos (importe, moneda, persona/entidad, destino, concepto, nota)
  son opcionales.
- **RF-09 — Separar momento ocurrido y momento registrado.** `occurredAt`
  representa cuándo ocurrió la operación; `createdAt` representa cuándo Agenda
  QR la registró. Son datos diferentes.

### Comprobantes

- **RF-10 — Guardar comprobante.** Un comprobante puede guardarse con la mínima
  interacción posible. Debe ser válido guardarlo sin completar metadatos y sin
  asociarlo a una operación.
- **RF-11 — Proveniencia del comprobante.** La proveniencia describe cómo llegó
  el comprobante: `ENVIADO`, `RECIBIDO` o `DESCONOCIDO`. `RECIBIDO` no significa
  dinero verificado. Cuando el contexto ya determina la proveniencia, no es
  necesario preguntar de nuevo.
- **RF-12 — Asociar comprobante.** Un comprobante puede estar asociado a cero o
  una operación. Una operación puede tener cero o múltiples comprobantes. La
  asociación puede cambiarse sin eliminar el archivo.
- **RF-13 — Comprobante independiente.** Un comprobante no asociado puede
  permanecer guardado y recuperarse/compartirse independientemente.
- **RF-14 — Duplicados.** Ante un comprobante posiblemente duplicado, Agenda QR
  informa al usuario pero no bloquea el guardado.

### Edición y eliminación

- **RF-15 — Editar operación.** Cambios sensibles cuando existen comprobantes
  (tipo, fecha/hora ocurrida, importe, moneda, persona/entidad, destino)
  requieren una única confirmación contextual. Cambios no sensibles (concepto,
  nota, organización) no la requieren. Editar una operación nunca modifica el
  archivo del comprobante.
- **RF-16 — Eliminar operación.** Al eliminar una operación se eliminan sus
  archivos de comprobante asociados y se conserva un registro histórico mínimo:
  fecha, tipo, importe y persona/entidad. No conserva la imagen/PDF del
  comprobante ni el QR.
- **RF-17 — Eliminar comprobante.** Eliminar un comprobante elimina el archivo
  y deja intacta la operación, si existe.

### Recuperación

- **RF-18 — Buscar operaciones.** Los resultados deben poder localizarse por
  fecha, tipo, importe y persona/entidad.
- **RF-19 — Compartir comprobante.** El usuario puede abrir y compartir un
  comprobante cuando el archivo exista.

### Protección

- **RF-20 — Protección local opcional.** La aplicación contempla biometría/PIN
  como capacidad local opcional.

## 5. Requisitos no funcionales

- **Local-first.** Los datos principales se conservan localmente.
- **Offline.** La recuperación y visualización principal de destinos y registros
  locales no depende de una conexión de red.
- **Evidencia.** El archivo del comprobante debe conservarse como recurso
  independiente del texto de la operación.
- **Integridad histórica.** Cambiar un destino no debe reescribir el contexto de
  operaciones históricas. Eliminar una operación elimina sus comprobantes
  asociados y deja un registro histórico mínimo.
- **Protección.** La protección por biometría/PIN es opcional y local.

No se fijan proveedores cloud, APIs bancarias, protocolos financieros, objetivos
de rendimiento ni tecnologías concretas no aprobadas.

## 6. Modelo de dominio

### QR

Representa el instrumento QR que puede ser detectado, leído y clasificado.

```text
QrAsset
├── encoded
└── mimeType
```

La clasificación describe qué contiene técnicamente el QR. No define cómo el
usuario lo organiza.

### Destination / Destino

Representa un lugar o cuenta reutilizable asociado a un QR.

```text
Destination
├── id
├── name
├── qr
└── organization
```

La organización puede conservar los metadatos aprobados (categoría, nota,
favorito). Un destino puede existir sin operaciones.

### Operation / Operación

Representa el hecho que el usuario registra como ocurrido.

```text
Operation
├── id
├── type
├── occurredAt
├── createdAt
├── amount?
├── currency?
├── personOrEntity?
├── destination?
├── concept?
└── note?
```

Tipos: `PAGO`, `COBRO`. El único conjunto obligatorio para crear una operación
es `id + type + occurredAt`.

### Comprobante

Es evidencia retenida por el usuario.

```text
Comprobante
├── id
├── file
├── createdAt
├── provenance?
└── operation?
```

Proveniencia: `ENVIADO`, `RECIBIDO`, `DESCONOCIDO`. La proveniencia no
representa el sentido financiero del dinero.

### Reglas de independencia

- Un comprobante puede existir sin operación.
- Una operación puede existir sin comprobante.
- Una operación puede tener múltiples comprobantes.
- Un comprobante pertenece a cero o una operación.

### Context (evolución V1)

La implementación añade `Context` como frontera humana y `contextId?` en
`Destination`, `Operation` y `Comprobante`. Las relaciones son opcionales durante
la migración para preservar datos existentes.

```text
Context
├── Destination / QR *
├── Operation / Actividad *
└── Comprobante *
```

Un comprobante puede tener contexto sin operación. Una operación puede tener
contexto sin QR. Un contexto no necesita hijos para existir.

## 7. Relaciones de dominio

```text
Usuario
 ├── administra → Destination*
 ├── registra   → Operation*
 └── conserva   → Comprobante*

Destination
 └── utiliza → QrAsset

Operation
 ├── contextualiza → Destination?
 └── tiene → Comprobante* (0..N)

Comprobante
 └── pertenece a → Operation? (0..1)
```

### Asociación reversible

```text
Comprobante → Operation A
      ↓ desasociar
Comprobante → sin operación
      ↓ asociar
Comprobante → Operation B
```

Desasociar no elimina el archivo.

### Historial de eliminación

Cuando una operación se elimina, sus comprobantes asociados se eliminan como
recursos. La operación deja solamente una huella histórica mínima:

```text
date
type
amount
person/entity
status = operación eliminada
```

No se conserva el archivo del comprobante.

### Eliminación de destino

Eliminar un destino no elimina sus operaciones: la asociación es contextual, no
de propiedad. En remoto, `operations.destination_id` no declara FK (ver
`supabase/migrations/001_v1.sql`); es una decisión intencional de dominio para
preservar la operación aunque se elimine el destino.

### Contexto histórico de destino

Cambiar el QR actual de un destino no sustituye el contexto histórico de una
operación ya registrada:

```text
Enero → QR A
Marzo → QR B
```

La operación de enero no debe pasar a mostrar QR B por el simple hecho de que el
destino actual haya cambiado.

### Context (integridad remota)

La migración inicial es aditiva: `context_id` es nullable y al eliminar un
contexto las relaciones quedan en `null` (`ON DELETE SET NULL`). La integridad
remota usa `(context_id, user_id)` para evitar asociaciones entre usuarios.

## 8. Reglas de operación y comprobante

- **Operación mínima:** `id + type + occurredAt` (`createdAt` lo genera el
  sistema al registrar; no lo aporta quien crea la operación).
- **Comprobante independiente:** puede existir sin operación o asociado a una;
  una operación puede tener cero o múltiples comprobantes; un comprobante
  pertenece a cero o una operación.
- **Proveniencia:** `ENVIADO`, `RECIBIDO` y `DESCONOCIDO` describen cómo llegó el
  comprobante. No expresan dirección del dinero.
- **Edición:** una edición sensible de una operación con comprobantes requiere
  una única confirmación contextual. Editar la operación nunca edita el archivo
  del comprobante.
- **Eliminación:** al eliminar una operación se eliminan los comprobantes
  asociados y se conserva únicamente fecha, tipo, importe y persona/entidad. La
  eliminación de un comprobante deja intacta la operación.
- **Duplicados:** un posible duplicado genera advertencia, pero el usuario puede
  guardar de todos modos.

## 9. Fuera de alcance V1

Quedan fuera de V1: ejecución de pagos; transferencias; billetera; saldos;
custodia de dinero; verificación bancaria; integración bancaria; CRM; módulo de
clientes; obligaciones; cuentas por cobrar; pagos parciales; flujos
especializados de devolución/reintegro; recordatorios; contabilidad; conciliación
bancaria; OCR de comprobantes; IA; sincronización cloud compleja; marketplace;
publicidad; estadísticas de negocio; cuentas familiares; infraestructura
financiera externa.

Estas capacidades pueden existir en una evolución futura, pero no son parte del
contrato V1.

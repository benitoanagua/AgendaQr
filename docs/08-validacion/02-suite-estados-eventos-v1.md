# Agenda QR — Suite de estados y eventos UX V1

**Estado:** contrato de pruebas conceptual  
**Fecha:** 2026-09-23  
**Base:** UX/UI V1 congelada en docs/04-ux/02-especificacion-ux-ui-v1.md.

Esta suite formaliza las reglas UX ya consolidadas. No introduce requisitos nuevos.

## 1. Estados

    HOME
    SEARCH
    ADD
    CAMERA
    REGISTER
    CONTEXT
    CONTEXT_SELECTION

    SEARCH_IDLE
    SEARCHING
    SEARCH_RESULTS
    SEARCH_EMPTY
    SEARCH_ERROR

    IMPORTING
    ANALYZING
    CLASSIFIED
    REVIEWING

    QR_REVIEW
    QR_DUPLICATE
    QR_SAVED
    QR_ERROR

    RECEIPT_REVIEW
    RECEIPT_MATCHED
    RECEIPT_AMBIGUOUS
    RECEIPT_UNASSOCIATED
    RECEIPT_ASSOCIATED
    RECEIPT_ERROR

    PAYMENT_DRAFT
    PAYMENT_SAVING
    PAYMENT_SAVED
    PAYMENT_ERROR

    PENDING_SYNC
    SYNCING
    SYNCED
    SYNC_ERROR

    BULK_IMPORTING
    BULK_ANALYZING
    BULK_REVIEW
    BULK_PARTIAL_SUCCESS
    BULK_ERROR

## 2. Transiciones críticas

| Estado | Evento | Resultado |
|---|---|---|
| Home | Buscar | Search |
| Home | Añadir | Add |
| Home | Registrar | Register |
| Search | escribir | Searching |
| Searching | resultados | SearchResults |
| Searching | sin resultados | SearchEmpty |
| Searching | error | SearchError |
| Add | Galería | Importing |
| Add | Desde otra app | Importing |
| Add | Cámara | Camera |
| Camera | QR detectado | QRReview |
| Analyzing | QR | QRReview |
| Analyzing | comprobante | ReceiptReview |
| QRReview | Guardar | Saving → Saved |
| QRReview | duplicado | QRDuplicate |
| ReceiptReview | coincidencia | ReceiptMatched |
| ReceiptReview | ambigüedad | ReceiptAmbiguous |
| ReceiptReview | sin coincidencia | ReceiptUnassociated |
| ReceiptReview | Asociar | ReceiptAssociated |
| Register | seleccionar contexto | ContextSelection |
| ContextSelection | seleccionar | Register |
| Register | Guardar | PaymentSaving |
| PaymentSaving | éxito | PaymentSaved |
| PaymentSaving | fallo | PaymentError |
| PendingSync | conexión | Syncing |
| Syncing | éxito | Synced |
| Syncing | fallo | SyncError |

## 3. Casos críticos

### UX-001 — Doble guardado
PaymentDraft → Save → PaymentSaving → segundo Save ignorado/idempotente. Esperado: una sola operación efectiva.

### UX-002 — Back durante selección
PaymentDraft(amount=450) → SelectContext → ContextSelection → Back → PaymentDraft(amount=450). El draft no pierde información.

### UX-003 — Selección de contexto
El contexto seleccionado vuelve al draft original.

### UX-004 — Cancelar registro
PaymentDraft → Cancel → Home. No se guarda una operación no confirmada.

### UX-005 — Pago sin contexto
Debe ser posible guardar una operación mínima con contexto ausente.

### UX-006 — QR desde galería
Gallery → Importing → Analyzing → QRReview. No pedir clasificación si la detección es clara.

### UX-007 — QR desde otra app
La recepción externa converge en el mismo pipeline.

### UX-008 — QR ilegible
Existe recuperación directa mediante otra imagen o back.

### UX-009 — QR duplicado
Informar sin borrar ni fusionar automáticamente.

### UX-010 — Comprobante sin actividad
Debe poder guardarse como ReceiptUnassociated.

### UX-011 — Coincidencia clara
Proponer asociación; el usuario confirma.

### UX-012 — Ambigüedad
No elegir automáticamente entre candidatos materialmente diferentes.

### UX-013 — Asociación reversible
Associated → Detach → Unassociated. El archivo permanece.

### UX-014 — Búsqueda global
Una consulta puede devolver contexto, QR, actividad y comprobante.

### UX-015 — Búsqueda vacía
Ofrecer recuperación contextual sin convertirla en error fatal.

### UX-016 — Búsqueda offline
Los datos locales disponibles siguen siendo consultables. Fuente remota inaccesible no equivale a cero resultados.

### UX-017 — Guardado offline
Save → Saved → PendingSync. No hay pérdida.

### UX-018 — Reconexión
PendingSync → Syncing → Synced.

### UX-019 — Error de sincronización
Conservar el dato local y permitir reintento.

### UX-020 — Cierre durante guardado
La implementación debe determinar de forma fiable si la mutación local quedó persistida. No se acepta un estado ambiguo para el usuario.

### UX-021 — Cierre durante análisis
La implementación debe definir una política determinista de reanudación o descarte explícito del trabajo incompleto.

### UX-022 — Importación masiva parcial
Elementos reconocidos se pueden guardar aunque otros requieran revisión.

### UX-023 — Fallo parcial
Un fallo de un elemento no convierte todos los elementos válidos en fallidos.

### UX-024 — Duplicado dentro de lote
El duplicado se informa sin impedir guardar elementos válidos.

### UX-025 — Pérdida de red
Search, Analyze, Save y Sync tienen recuperaciones diferentes.

### UX-026 — Evento repetido
Save/Associate/Import no deben duplicar.

### UX-027 — Evento tardío
Un resultado tardío no puede regresar la máquina a un estado anterior inválido.

### UX-028 — Latencia extrema
Una operación en curso mantiene una identidad única y no permite escrituras paralelas accidentales.

### UX-029 — Accesibilidad
La jerarquía, acción primaria y estado siguen siendo comprensibles con texto grande, lector de pantalla y reduced motion.

### UX-030 — Texto largo
Nombres y metadatos largos no provocan solapamientos ni ocultan la acción primaria.

### UX-031 — Estado parcial
Una sección vacía no invalida el resto del contexto.

### UX-032 — Intención preservada
Un selector temporal no reemplaza la intención del flujo padre.

### UX-033 — Contexto con distintas intenciones
La misma superficie Context puede actuar como consulta o selección según el flujo que la abrió.

## 4. Invariantes

I01 Back no destruye draft.  
I02 Guardar repetidamente no crea duplicados.  
I03 Offline no implica pérdida.  
I04 Comprobante no requiere QR.  
I05 Operación no requiere contexto.  
I06 QR no requiere cámara.  
I07 Importación válida no depende de importación inválida.  
I08 Asociación es reversible.  
I09 Ambigüedad no se resuelve arbitrariamente.  
I10 Guardado no equivale a sincronizado.  
I11 Error recuperable conserva el trabajo.  
I12 Buscar no requiere elegir tipo antes.  
I13 Contexto preserva la intención del flujo padre.  
I14 Estados técnicos no crean navegación artificial.  
I15 Cerrar la aplicación no convierte un guardado confirmado en estado desconocido.  
I16 Evento tardío no puede retroceder a una fase inválida.  
I17 Una operación en curso tiene una única identidad aunque se dispare varias veces.  
I18 Todo estado no terminal tiene salida recuperable o cancelable.

## 5. Resultado

**44 escenarios conceptuales cubiertos.**

- UX conceptual: PASS.
- Revisión adversarial: PASS con ajustes ya incorporados.
- Kill/restart durante persistencia: pendiente de validación real.
- Latencia extrema: pendiente de implementación.
- Accessibility runtime: pendiente de UI real.

Estos pendientes no modifican la especificación UX congelada.

## 6. Regla de implementación

Si una prueba falla durante implementación, primero se corrige la implementación. No se cambia la especificación para acomodar el fallo sin una decisión UX explícita.

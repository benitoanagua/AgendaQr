# Estado de implementación — alineación V1

## Situación

La especificación funcional V1 y el contrato UX/UI V1 están aprobados. La UX/UI está congelada y pasa a ser el contrato para la implementación.

El repositorio contiene la base KMP de destinos QR, operaciones y comprobantes, además de persistencia local, Supabase y sincronización local-first.

## Estado tras auditoría V1 (2026-09)

- autenticación Supabase y aislamiento por usuario;
- RLS en recursos propios;
- Storage privado por usuario;
- persistencia local user-scoped;
- cola durable user-scoped con backoff, deduplicación y recuperación;
- repositorios Sync* con local-first + encolado en fallo remoto;
- comprobantes independientes, asociación reversible y múltiples comprobantes por operación;
- detección de duplicados no bloqueante;
- eliminación con histórico mínimo;
- indicadores offline/sync pending documentados en la auditoría;
- Android validado según la auditoría V1;
- iOS arquitectónicamente preparado pero funcionalmente UNTESTED.

## UX/UI

La especificación congelada está en:

`docs/04-ux/02-especificacion-ux-ui-v1.md`

La suite de estados/eventos está en:

`docs/08-validacion/02-suite-estados-eventos-v1.md`

La implementación debe satisfacer esos contratos. Estos documentos no autorizan crear un segundo modelo de negocio ni modificar silenciosamente la UX.

## Pendiente / Fuera de V1

- validación UI real de todos los estados;
- E2E offline↔online con kill/restart;
- validación runtime de accesibilidad;
- validación de latencia extrema;
- validación iOS funcional;
- paginación/realtime/biometría según alcance documentado;
- OCR, contabilidad, facturación, CRM, wallet y roles multiempresa.

## Regla

Código existente no equivale a capacidad validada en plataforma.

Una prueba UX conceptual PASS tampoco equivale a una validación runtime. La evidencia de implementación debe conservar esta distinción.


## Fundación de Contexto implementada

- `Context` con persistencia local-first y Supabase.
- `contextId?` en QR/Destination, Activity/Operation y Comprobante.
- migración `005_context_relations.sql` con integridad por usuario.
- casos de uso para asociación reversible a contexto.
- agregación `ContextContents` para la pantalla de contexto.
- búsqueda global sobre Context, QR, Activity y Comprobante.
- tests de dominio para relaciones, asociación y búsqueda.

La siguiente fase adapta estas capacidades al estado/presentación UX/UI V1 sin cambiar el contrato congelado.


## Presentación de Contexto y búsqueda — en implementación

Se añadió la primera capa de presentación alineada con el contrato UX/UI congelado:

- navegación a Contextos desde la superficie existente;
- detalle de Contexto agregando QR, actividades y comprobantes;
- búsqueda global sobre Context/QR/Activity/Comprobante;
- selección de contexto al registrar una actividad;
- selección de contexto al editar/importar un QR;
- IDs de entidad resistentes a colisiones de milisegundos;
- guardas contra doble acción de guardado;
- clasificación Android de imágenes mediante ZXing antes de tratarlas como QR.

La validación de compilación/runtime permanece pendiente de la pasada local de Gradle.

## Fase bulk import + comprobantes

- Agregado `ImportBatch` como contrato común de análisis: `QR / COMPROBANTE / DESCONOCIDO`.
- Un elemento desconocido no invalida los elementos reconocidos.
- Duplicados por huella estable quedan en revisión; la primera ocurrencia reconocida puede guardarse una sola vez.
- Agregado `SuggestReceiptAssociationUseCase`:
  - asociación existente se conserva;
  - un único candidato dentro del contexto se propone;
  - varios candidatos producen estado de ambigüedad;
  - sin candidato de alta confianza se permite guardar sin asociar.
- Tests de dominio agregados para clasificación, duplicados y asociación/ambigüedad.
- La UI completa de lote (`IMPORTANDO → ANALIZANDO → RESULTADO → REVISAR → GUARDAR`) y la persistencia de bytes de comprobantes por lote quedan para la siguiente integración de presentación/Android.
- Gradle todavía no se ejecuta; la validación local se hará al cerrar este bloque de implementación.

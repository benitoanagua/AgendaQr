# UX — Flujos confirmados V1

La UI visual está bajo el contrato de Xauxa y la especificación UX/UI V1 congelada en `02-especificacion-ux-ui-v1.md`.

## Principio

> Una pantalla = una intención principal + una acción principal.

Este documento resume los flujos funcionales. La especificación completa de pantallas, estados, recuperación y accesibilidad vive en `docs/04-ux/02-especificacion-ux-ui-v1.md`.

## Destinos / QR

### Captura e importación

    Galería / Desde otra app / Cámara
                  ↓
              importar
                  ↓
               analizar
                  ↓
              clasificar
                  ↓
                revisar
                  ↓
               guardar

Galería es la entrada principal para información que ya existe. Cámara es un medio de captura.

### Reutilización

    Buscar → Seleccionar → Contexto / QR → Mostrar / reutilizar / compartir

## Operación primero

    Registrar
       ↓
    Datos
       ↓
    (opcional) seleccionar contexto
       ↓
    Guardar

El contexto no bloquea el registro. El draft se conserva al entrar temporalmente al selector.

## Comprobante primero

    Recibir / importar comprobante
                 ↓
              analizar
                 ↓
               revisar
                 ↓
          ┌──────┴──────┐
          ↓             ↓
       asociar      guardar sin asociar

Un comprobante puede guardarse sin operación y asociarse después.

## Ambigüedad

    Revisar → ambiguo → una pregunta clara → seleccionar candidato

No se selecciona automáticamente cuando la ambigüedad puede cambiar el significado.

## Importación masiva

    Importar → Analizar → Clasificar → Resultado
                                  ├→ reconocidos → guardar
                                  ├→ duplicados → informar
                                  └→ pendientes → revisar después

Los elementos válidos no quedan bloqueados por los pendientes.

## Offline y errores

Offline es transversal:

    Guardado → Pendiente → Sincronizando → Sincronizado

Los errores muestran recuperación contextual. No existe una pantalla global de error.

## Eliminación

Eliminar operación con comprobantes:

    Advertencia contextual → Confirmar → Eliminar archivos asociados → Conservar histórico mínimo

Eliminar comprobante:

    Eliminar archivo → Operación permanece

## Contrato de navegación

Back vuelve al estado anterior y conserva el draft cuando existe. Cancelar abandona la intención actual.

La matriz de estados/eventos está en `docs/08-validacion/02-suite-estados-eventos-v1.md`.

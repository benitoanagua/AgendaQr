# UX — Capacidades y comportamientos V1

## Búsqueda

La búsqueda de operaciones/comprobantes prioriza:

- fecha;
- tipo;
- importe;
- persona/entidad.

No depende de OCR para V1.

## Comprobante

La acción principal al recibir/compartir un comprobante debe poder completarse sin obligar al usuario a registrar una operación completa.

## Asociación

La asociación entre comprobante y operación se puede corregir sin volver a importar el archivo.

## Duplicados

La detección de posible duplicado es una ayuda, no un bloqueo.

```text
Parece que este comprobante ya está guardado.
[Ver existente]
[Guardar de todos modos]
```

## Edición de operación

Cuando una operación con comprobantes cambia en un campo sensible, se muestra una única advertencia contextual:

```text
Esta operación tiene comprobantes.
El comprobante no será modificado.
[Cancelar] [Guardar cambio]
```

Los cambios conceptuales, notas u organización no necesitan esa confirmación.

## Semántica

Un comprobante recibido significa que el archivo/evidencia llegó al usuario o fue incorporado desde ese contexto. No implica verificación del dinero.

# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN  
**Fecha:** 2026-09-23  
**Alcance:** contrato UX/UI conceptual; no es implementación Compose.

## 1. Contrato principal

> Una pantalla = una intención principal + una acción principal.

La pantalla puede contener información, campos y acciones secundarias necesarias. La regla define jerarquía de intención, no número de controles.

La UX/UI V1 queda congelada. Un cambio posterior que altere intención, navegación, modelo mental, jerarquía primaria o recuperación requiere una decisión UX explícita.

## 2. Modelo mental

Agenda QR se organiza alrededor del trabajo humano:

    CONTEXTO
    ├── QR
    ├── ACTIVIDADES
    └── COMPROBANTES

Contexto es una superficie de continuidad, no una nueva taxonomía de navegación.

## 3. Reglas congeladas

- Buscar es global: contexto, QR, actividad y comprobante.
- Buscar no exige elegir primero el tipo de resultado.
- Galería es la entrada principal para imágenes que ya existen.
- Desde otra app es una entrada de primer nivel.
- Cámara es un medio de captura, no el modelo mental principal.
- El sistema clasifica automáticamente cuando tiene confianza suficiente.
- Una importación válida no queda bloqueada por otra inválida.
- Un comprobante puede existir sin QR y sin actividad asociada.
- Una actividad puede existir sin QR.
- Una operación puede registrarse sin contexto.
- Las asociaciones son reversibles.
- Back conserva el trabajo del flujo padre.
- Cancelar abandona la intención actual.
- Guardar repetidamente no crea duplicados.
- Guardado y Sincronizado son estados distintos.
- Offline es transversal, no una pantalla.
- Los errores son recuperables y contextuales.
- La ambigüedad real genera una sola pregunta clara.
- Los estados técnicos no crean navegación artificial.

## 4. Pantallas

### S01 — Inicio
Intención: encontrar o iniciar algo. Buscar domina visualmente; Añadir y Registrar son acciones secundarias.

### S02 — Añadir
Intención: traer algo que el usuario ya tiene.

    GALERÍA
    DESDE OTRA APP
    CÁMARA

Jerarquía: Galería → Desde otra app → Cámara.

### S03 — Cámara
Intención: capturar un QR físico. La detección debe ser automática cuando sea posible. No se muestran métricas técnicas del scanner.

### S04 — Buscar
Intención: encontrar cualquier cosa de Agenda QR. Los resultados pueden ser heterogéneos y muestran su tipo mediante texto/semántica.

### S05 — Resultados
Superficie de decisión. Al volver se conserva la consulta.

### S06 — Contexto
Intención: comprender y continuar dentro de un contexto. No es un dashboard financiero. La acción primaria depende del flujo que llevó al usuario allí.

### S07 — Registrar
Intención: registrar una actividad. El contexto ayuda pero no bloquea el registro.

### S08 — Seleccionar contexto
Intención: elegir contexto para el flujo padre. Al terminar, vuelve al flujo original conservando el draft.

### S09 — Revisar QR
Intención: verificar lo que Agenda QR entendió antes de guardar. Debe mostrar evidencia suficiente sin exponer payload técnico innecesario.

### S10 — Revisar comprobante
Intención: confirmar y, cuando corresponda, asociar.

    ASOCIAR
    Guardar sin asociar

Ambos son resultados legítimos.

### S11 — Resolver ambigüedad
Intención: resolver una única duda que el sistema no puede decidir con seguridad.

### S12 — Resultado de importación masiva
Superficie de decisión derivada de Añadir.

    ✓ reconocidos
    ! duplicados
    ? pendientes
    [ GUARDAR RECONOCIDOS ]
    Revisar N pendientes

Los válidos no dependen de los pendientes.

## 5. Estados transversales

Estos son estados, no destinos de navegación:

    IMPORTANDO
    ANALIZANDO
    GUARDANDO
    GUARDADO
    PENDIENTE
    SINCRONIZANDO
    SINCRONIZADO
    ERROR RECUPERABLE
    DUPLICADO
    ASOCIADO
    SIN ASOCIAR

Contrato local-first:

    GUARDADO → PENDIENTE → SINCRONIZANDO → SINCRONIZADO

## 6. Importación

Galería y Desde otra app convergen:

    IMPORTAR → ANALIZAR → CLASIFICAR → REVISAR → GUARDAR

Clasificación conceptual: QR, COMPROBANTE, DESCONOCIDO, POSIBLE DUPLICADO.

Importando y Analizando son estados; no requieren un botón Continuar.

## 7. Búsqueda

Puede devolver Contexto, QR, Actividad o Comprobante. No hay selección previa de tipo.

## 8. Registro

El draft sobrevive al selector de contexto.

    Registrar
      ↓
    Seleccionar contexto
      ↓
    Volver a Registrar

Una operación mínima puede existir sin contexto.

## 9. Comprobantes

    COMPROBANTE
    ├── asociado
    └── sin asociar

Un comprobante puede guardarse antes de conocer la actividad y asociarse después.

## 10. Errores

Todo error debe responder: qué ocurrió, qué pasó con los datos y qué puede hacer el usuario.

Ejemplos:

    No pudimos leer este QR.
    [ ELEGIR OTRA IMAGEN ]

    No pudimos sincronizar.
    Tu información está guardada.
    [ REINTENTAR ]

No existe un ErrorScreen global como destino de navegación.

## 11. Accesibilidad y movimiento

- targets interactivos de 44–48dp como mínimo según plataforma;
- significado independiente del color;
- estados expresados también mediante texto/semántica;
- tipografía adaptable;
- reduced motion respetado;
- comprensión independiente de la animación.

## 12. Contrato visual Xauxa

- composición antes de crear componentes;
- radio 0 en contenedores rectangulares;
- sin sombras/elevación decorativa;
- separación mediante espacio y bordes;
- un único acento de producto;
- colores adicionales solo como semántica de estado;
- Archivo para display/encabezados;
- Roboto/San Francisco para UI/cuerpo;
- spacing según Xauxa;
- Command Bar solo donde aporte acciones de detalle/foco;
- lenguaje visual del scanner XauxaXcan para cámara, sin métricas técnicas.

Los restos históricos rounded/shadow de XauxaXcan no sustituyen el Design System normativo.

## 13. Fuera de esta congelación

No define todavía implementación Kotlin/Compose, navegación concreta, ViewModels/state holders, persistencia concreta, nuevos componentes, tests instrumentados ni validación runtime de accesibilidad.

## 14. Estado

**UX/UI V1: FROZEN.**

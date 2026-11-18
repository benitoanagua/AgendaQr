# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN — contrato funcional V1.9 y composición funcional V1.21; dirección visual vigente en ADR-0025
**Fecha:** 2026-10-11  
**Alcance:** contrato UX/UI conceptual; no es implementación Compose.

**Alcance de V1.13:** la composición de pantallas y la dirección visual se rigen por el contrato normativo vigente (ADR-0025). La intención, el modelo mental (§2), las reglas (§3), las reglas de negocio, el orden de los flujos, la navegación conceptual y su back stack, el contrato local-first, el copy funcional, la guía de voz (ADR-0008) y la accesibilidad (§11) permanecen congelados.

## Historial de enmiendas

| Versión | Fecha | Alcance |
|---|---|---|
| V1.0 | 2026-09-23 | Congelación original. |
| V1.6 | 2026-10-14 | Enmienda por ADR-0016 (identidad local sin cuenta): §4 añade S00 Acceso local (configurar PIN en el primer arranque, desbloquear con PIN) y S01 cambia "Cerrar sesión" por "Cuenta y respaldo" + "Bloquear" en el menú "…". El acceso deja de ser una pantalla de login remoto. Modelo mental, reglas de negocio, accesibilidad y copy de las pantallas existentes: intactos. |
| V1.8 | 2026-10-16 | Enmienda por ADR-0018 (correcciones UX de captura rápida): S00 (gracia de bloqueo 120 s, biometría automática, demo solo al inicio), S01 (hub de creación, sin duplicados, Favoritos, lista sin favoritos del carrusel), Detalle de destino (Mostrar QR como primaria, pantalla completa con brillo), S07 (editor con divulgación progresiva, validación real, descarte confirmado), S04 (foco solo desde Inicio). Intención, modelo mental y reglas de negocio: intactos. Aprobación explícita del propietario para enmendar la congelación en lo que ADR-0018 cambia. |
| V1.9 | 2026-10-16 | Enmienda por ADR-0019 (inicio vacío): S01 vacío de primera ejecución muestra "Traer un QR", el FAB se oculta sin destinos ni búsqueda, y se retira el encabezado "Todos los destinos" sobre el vacío. Intención, modelo mental y reglas de negocio: intactos. |
| V1.13 | 2026-10-17 | Composición y patrones de interacción conforme al sistema de diseño vigente; la intención, las reglas de negocio, la navegación y el copy funcional permanecen intactos. |
| V1.14 | 2026-10-18 | Reconciliación con el contrato normativo de la versión: se corrige la contradicción interna de S07/S08 (selector de contexto, fecha y hora se presentan con `AqDialog` y listas, nunca con bottom sheet, D-07) y se enlaza el contrato de interacción por pantalla (`04-ux/03-contrato-interaccion-pantallas.md`), verificado por `verifyScreenContract`. La reunión de la congelación no aporta ni quita gestos, pantallas ni navegación: la auditoría no halló ningún patrón de la referencia visual anterior que los exija (registro histórico; la autoridad visual vigente es ADR-0025). |
| V1.15 | 2026-10-18 | Revisión de composición de S01: la barra superior usa el nombre de la superficie ("Inicio") y Todos/Favoritos pasa a ser un control segmentado mutuamente excluyente con conteo real. Se descartan controles redundantes, indicaciones ambiguas y exposición de miniaturas QR en la raíz por razones de coherencia y privacidad. |
| V1.16 | 2026-10-18 | S00 adopta una entrada de PIN consistente entre plataformas: héroe centrado, puntos enmascarados y teclado propio 3×4 con borrado accesible. El envío sigue siendo explícito (PIN de 4 a 12); biometría y demostración permanecen como acciones de texto. Se excluye cualquier promesa de recuperación no implementada o de cifrado en reposo inexistente. |
| V1.17 | 2026-10-18 | S02 ordena los métodos en un único grupo: Galería → Cámara, con Archivo al pie; se retiran rótulos redundantes. "Desde otra app" permanece como ayuda breve no destacada y se conserva el copy funcional vigente. |
| V1.18 | 2026-10-18 | La configuración inicial del PIN (S00) usa el mismo teclado propio en dos pasos: crear el código ("Continuar", exige la longitud mínima del dominio) y confirmarlo ("Guardar PIN"), con héroe, puntos enmascarados y errores como texto. Borrar con la confirmación vacía vuelve al paso de creación sin perder el código. La demostración sigue disponible como acción de texto. |
| V1.19 | 2026-10-18 | S04 muestra conteos reales por tipo en los encabezados de grupo. Se mantienen fuera los filtros y acciones que requerirían nuevos estados o cambiarían el modelo de navegación. S09 conserva la evidencia que el dominio puede verificar y no promete métricas, metadatos parseados ni cifrado no implementados. |
| V1.20 | 2026-10-18 | S06 y S08 se validan contra el dominio y las reglas de producto: no se incorporan agregados ni búsquedas sin soporte; S08 conserva el diálogo para mantener el draft y el back stack. Los contratos declaran solo componentes reales. |
| V1.21 | 2026-10-18 | Revisión de composición de S07, lista y detalle de Actividades, S12 y detalles de destino/actividad. Se mantienen fuera funciones, agregados, metadatos y descargas no implementados; mostrar el destino vinculado en el detalle de actividad queda registrado como mejora de estado. |

La composición funcional y las reglas de producto corresponden a V1.9; V1.13 define la composición de pantallas y los patrones de interacción. ADR-0025 y el contrato normativo de diseño fijan el sistema visual vigente; la dirección visual (§12) queda descongelada, el resto de la especificación permanece congelada. Las capacidades verificadas, las limitaciones por plataforma y los resultados de las puertas de calidad se mantienen en `docs/STATE.md` y `docs/QUALITY.md`. Si la implementación se aparta de esta especificación, se corrige el código o se aprueba una enmienda explícita; la especificación gobierna intención, composición funcional y flujos.

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
- Desde otra app es una entrada de primer nivel donde la plataforma lo soporta (Android: share intents; iOS: pendiente de Share Extension, V1.2 no la muestra).
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

El detalle ejecutable por pantalla (ruta, destinos válidos, estados, gestos,
componentes y pruebas) vive en
[`03-contrato-interaccion-pantallas.md`](03-contrato-interaccion-pantallas.md),
generado desde el contrato estructurado y verificado por `verifyScreenContract`.

### S00 — Acceso local (V1.6, ADR-0016; composición V1.18)
Intención: entrar al dispositivo propio. No es un login: no hay correo, contraseña ni red.

    PRIMER ARRANQUE      configurar PIN (código + confirmación, dos pasos) → Guardar
    ARRANQUES POSTERIORES desbloquear con PIN

- Contenido centrado con héroe, título y descripción. Desbloqueo y configuración entran el PIN con teclado propio y puntos enmascarados (S00, V1.16; configuración en dos pasos crear → confirmar, V1.18). La acción de cada paso permanece fija al pie. Biometría y cambio a PIN son acciones de texto cuando correspondan.
- El perfil local se crea solo, sin cuenta; el PIN protege el acceso al dispositivo.
- El PIN es de 4 a 12 dígitos, oculto; los errores y los intentos restantes viven como texto bajo los puntos. El paso de creación exige la longitud mínima del dominio; una confirmación distinta reporta "no coinciden" sin crear credencial.
- Confirmación distinta → error "no coinciden" sin crear credencial.
- Código incorrecto → error con intentos restantes; al agotar intentos hay un bloqueo temporal progresivo.
- El bloqueo ocurre al pasar la app a segundo plano; al reanudar se vuelve a pedir el PIN. Con un flujo externo iniciado por la app (galería, cámara, selectores, compartir) hay una gracia acotada de 120 s; vencida, se bloquea al volver.
- Con biometría disponible e inscrita, el desbloqueo la solicita sola una vez por bloqueo (con el PIN como respaldo); la demostración solo existe en el primer arranque.
- El PIN es local; nunca viaja a la red y no es la clave de cifrado de los datos.

### S01 — Inicio (composición V1.15; presentación V1.13)
Intención: encontrar o iniciar algo. Buscar domina visualmente; las creaciones viven en la rejilla de acciones.

Composición:

    [navbar "Inicio" centrada, overflow "…" → Action-sheet ("Cuenta y respaldo", "Bloquear")]
    [Search como disparador hacia S04]
    [rejilla existente: Traer un QR / Registrar actividad / Crear contexto]
    [Segmented: Todos · Favoritos, con conteo real]
    [Favoritos: carrusel horizontal]
    [Todos los destinos: Cell-group con tile QR, título, descripción y favorito]
    [Empty informativo sin repetir "Traer un QR"]
    [Notice-bar de disponibilidad sin conexión]

- La rejilla reutiliza exactamente las tres creaciones existentes; no añade ni duplica acciones. Cada celda conserva su navegación y efecto actuales.
- Inicio no usa FAB. Los filtros conservan su semántica idempotente y la lista excluye los favoritos ya presentados en el carrusel.
- Favoritos es un filtro de la propia lista (y del cuerpo de Inicio),
  no un destino aparte; en Todos la lista excluye los favoritos del
  carrusel (sin duplicación).
- Navegación inferior (Inicio · Buscar · Actividades · Contextos) solo
  en las superficies raíz; cambiar de destino conserva la raíz del back
  stack (Back desde otro destino vuelve a Inicio; desde Inicio sale de
  la app).
- El menú "…" de la barra superior de Inicio ofrece "Cuenta y respaldo" (nube opcional) y "Bloquear".

### Detalle de destino (V1.8, ADR-0018; presentación V1.13)
Intención: mostrar el QR. La única acción primaria es "Mostrar QR" (pantalla completa); Compartir es secundaria; Registrar actividad, Editar y Eliminar viven en "…". La tarjeta del QR también abre la pantalla completa. La pantalla completa muestra el QR al máximo con el nombre debajo, brillo al máximo y pantalla encendida (restaurados al salir). El contenido decodificado no aparece en las listas; en el Detalle va en bloque expandible, seleccionable y copiable.

Composición: navbar con atrás y overflow "…" (Action-sheet); QR en tarjeta sobre lienzo blanco; "Mostrar QR" como acción primaria y "Compartir" como secundaria. Los metadatos se agrupan como Cell-group; el contenido decodificado permanece expandible y copiable. Eliminar conserva su confirmación destructiva.

### S02 — Añadir (V1.3, ADR-0013; composición V1.17)
Intención: traer algo que el usuario ya tiene.

    [héroe centrado; Cell-group único: Galería → Cámara → Archivo (PDF, solo donde se soporta)]
    [privacidad de cámara como descripción de su celda]
    [ayuda "Desde otra app" en Footer, solo donde se soporta]

- Galería es una única acción con selección múltiple: 1 elemento →
  revisión única (S09); 2 o más → lote (S12). No existe el par
  "Galería / Galería (varios)".
- El bloque "Desde otra app" es una ayuda breve, no una sección
  permanente destacada, y solo aparece donde la plataforma soporta
  compartir hacia Agenda QR.
- Jerarquía conservada: Galería → Cámara. Desde V1.17 el orden visual
  sigue esa jerarquía (Archivo al pie del grupo único), como en la
  pantalla S02; antes el PDF se interponía entre ambas.

### S03 — Cámara (presentación V1.13)
Intención: capturar un QR físico. La detección debe ser automática cuando sea posible. No se muestran métricas técnicas del scanner.

Composición: navbar transparente sobre la vista previa y marco de escaneo con esquinas según el sistema visual vigente. No se añaden métricas ni controles que cambien el flujo de captura.

### S04 — Buscar (composición V1.19)
Intención: encontrar cualquier cosa de Agenda QR. Los resultados pueden ser heterogéneos y muestran su tipo mediante texto/semántica. El foco automático solo llega desde el disparador de Inicio; por pestaña se entra sin foco.

Composición: Search con acción de cancelar. Los resultados se agrupan por tipo en Cell-group y llevan Tag textual; los encabezados de grupo llevan el conteo real (V1.19); la consulta se conserva al volver.

### S05 — Resultados (presentación V1.13)
Superficie de decisión. Al volver se conserva la consulta.

Composición: Search y resultados agrupados por tipo en Cell-group; Empty cuando no hay resultados y el estado de error conserva su recuperación. No se altera el foco ni la persistencia de la consulta.

### S06 — Contexto (V1.3, ADR-0013; presentación V1.13)
Intención: comprender y continuar dentro de un contexto. No es un dashboard financiero. La acción primaria depende del flujo que llevó al usuario allí.

Composición: navbar con título y atrás; Tabs QR · Actividades · Comprobantes, como secciones del mismo contexto y no como destinos raíz. El contenido usa Cell-group. El acento del contexto se conserva para encabezados y marcadores (X-06).

### S07 — Registrar (V1.8, ADR-0018; presentación V1.13)
Intención: registrar una actividad. El contexto ayuda pero no bloquea el registro. Captura rápida con divulgación progresiva: Tipo · Importe+Moneda · Persona o entidad · Fecha y hora · Contexto · Más detalles (plegado salvo que existan) · Guardar fijo abajo.

- Formulario en celdas: Tipo con Segmented; Importe y moneda en Input; entidad con sugerencias; fecha/hora en selector dentro de `AqDialog` con atajos Hoy/Ayer como Tags; Contexto como celda navegable hacia S08; Más detalles como Collapse. Guardar conserva su posición fija.
- Importe opcional con validación real (dígitos y un separador decimal, máx. 2 decimales); moneda BOB/USD/Otra (por defecto, la última usada); persona con sugerencias de contrapartes recientes.
- Fecha y hora con selectores (sin texto libre) y atajos Hoy/Ayer; por defecto, ahora con hora.
- Salir con cambios pide "¿Descartar cambios?"; sin cambios sale de inmediato. El Back del sistema sigue el mismo camino.

### S08 — Seleccionar contexto (composición V1.20)
Intención: elegir contexto para el flujo padre. Al terminar, vuelve al flujo original conservando el draft.

Composición: `AqDialog` con lista de opciones Radio en celdas, creación sin salir del draft y "Quitar contexto" como acción destructiva dentro del contenido; "Cancelar" es la única salida (V1.20: sin búsqueda — el selector trabaja sobre pocos elementos). Si no se puede verificar de forma reproducible, se conserva el patrón de selección en página completa, sin perder el draft ni cambiar el back stack.

### S09 — Revisar QR (presentación V1.13)
Intención: verificar lo que Agenda QR entendió antes de guardar. Debe mostrar evidencia suficiente sin exponer payload técnico innecesario.

Composición: vista previa del QR en tarjeta, evidencia agrupada en Cell-group, avisos en Notice-bar/Message y acción primaria Guardar fija al pie. No se muestra payload técnico innecesario.

### S10 — Revisar comprobante (presentación V1.13)
Intención: confirmar y, cuando corresponda, asociar.

    ASOCIAR
    Guardar sin asociar

Ambos son resultados legítimos.

Composición: vista previa y Cell-group. Asociar es primaria; "Guardar sin asociar" es secundaria `default`; ambos resultados legítimos y permanecen disponibles según el estado actual.

### S11 — Resolver ambigüedad (presentación V1.13)
Intención: resolver una única duda que el sistema no puede decidir con seguridad.

Composición: una pregunta y un Radio-group de celdas; Continuar es primaria y permanece deshabilitada hasta seleccionar una opción.

### S12 — Resultado de importación masiva (V1.3, ADR-0013; presentación V1.13)
Superficie de decisión derivada de Añadir.

    [Result: estado final, resumen y descripción]
    [Cell-group de elementos con Tag textual de estado]
    [GUARDAR RECONOCIDOS — primaria fija, solo si existen reconocidos]
    [Revisar N pendientes — secundaria; Descartar — acción de texto]

Los válidos no dependen de los pendientes. Mientras se guarda no hay
vuelta (el lote se perdería); el Back del estado de revisión regresa al
resultado (flujo padre).
No se añade confeti ni feedback animado nuevo; el resultado conserva los
mensajes y las transiciones funcionales existentes.

### Actividades y detalle (presentación V1.13)
La lista mantiene los filtros y la búsqueda existentes; se compone como Cell-group con título, descripción y nota de importe/fecha. Si la superficie ya ofrece una acción flotante, conserva esa función. El detalle presenta el importe con cifras tabulares y sus metadatos en Cell-group, con Tag textual para el estado. No se modifica el modelo de operaciones ni su significado.

### Cuenta y respaldo (presentación V1.13)
La nube sigue siendo opcional y nunca es puerta de entrada al perfil local. El estado de cuenta/respaldo se presenta en Cell-group; AuthScreen conserva su acción primaria plana y los errores ofrecen explicación y recuperación mediante Notice-bar o Message.

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

## 11. Accesibilidad y movimiento (V1.3, ADR-0013)

- targets interactivos de al menos 48 dp según el contrato normativo (D-02);
- significado independiente del color;
- estados expresados también mediante texto/semántica;
- tipografía adaptable;
- reduced motion respetado;
- comprensión independiente de la animación.

Reglas vigentes V1.3:

- **Contraste:** texto normal ≥ 4.5:1 y texto grande ≥ 3:1 (WCAG), en
  claro y en oscuro; los límites de controles y bordes en reposo ≥ 3:1.
  Los colores de estado (error, advertencia, éxito) son semánticos,
  distintos de los acentos, y el significado nunca depende solo del
  color.
- **Iconos:** todo icono interactivo tiene descripción accesible; los
  destinos de navegación llevan siempre etiqueta + icono (nunca solo
  icono). Los iconos decorativos se marcan como no accesibles para
  lectores de pantalla.
- **Errores de campo:** texto de error contextual debajo del campo, con
  icono + texto (no solo color), asociado también a la semántica del
  campo editable.
- **Reduced motion:** las transiciones son sustituidas por el cambio
  inmediato (duración 0). La comprensión no depende de la animación.
- **Sin bucles de animación:** las animaciones decorativas en bucle no
  forman parte del lenguaje; un indicador de actividad persistente usa
  el componente estándar del sistema.
- **Escalado de fuente:** el contenido esencial y las acciones primarias
  no se truncan con la fuente del sistema al 150 % en la verificación
  automática; el soporte hasta 200 % se conserva y se revisa manualmente
  (ADR-0007).

## 12. Contrato visual — ADR-0025

> **Precedencia:** la intención, los flujos, el orden de navegación y el copy funcional permanecen congelados en esta especificación. La dirección visual, los tokens, los componentes, los estados, el contraste, la accesibilidad visual, las pruebas y la gobernanza se rigen por `docs/05-design-system/00-contrato-normativo-diseno.md` (ADR-0025). Esta sección no define valores visuales propios.

La dirección visual vigente es la del sistema adoptado en ADR-0025, reimplementado en Kotlin/Compose. Su fuente verificable es la instantánea versionada citada en el contrato normativo; `core/ui` es la implementación canónica y las pantallas componen sus componentes y tokens semánticos. Los valores no verificables se marcan en el contrato como pendientes y se resuelven con el valor verificable más cercano de la misma versión, nunca con un valor inventado.

### Composición e interacción

La composición por pantalla se especifica en §4 y conserva la intención y el modelo mental existentes. No se añaden gestos, acciones ni estados que AgendaQr no tuviera. La navegación raíz, sus cuatro destinos, el back stack y el orden de los flujos permanecen invariantes. Las decisiones explícitas del propietario (ADR-0016, ADR-0018 y ADR-0025) son el único mecanismo para modificarlos.

### Principios

1. Una intención y una acción primaria por pantalla; las acciones existentes conservan su semántica y jerarquía.
2. Las superficies y los estados se componen solo con tokens semánticos de `core/ui`, sin gradientes, brillo decorativo ni colores locales.
3. El QR conserva su lienzo blanco y su zona de silencio en cualquier tema (X-03).
4. Accesibilidad, funcionamiento offline y consistencia de intención entre Android/iOS son invariantes.

### Navegación y chrome

- La barra superior conserva título centrado, atrás a la izquierda y acciones a la derecha; el overflow abre una hoja de acciones. La navegación inferior conserva sus cuatro destinos y etiquetas siempre visibles.
- La navegación raíz continúa siendo Inicio, Buscar, Actividades y Contextos; los detalles, editores e importaciones no duplican esa barra.
- La navegación usa las convenciones nativas de Android e iOS (D-03); su estado activo, divisores e indicadores se resuelven con tokens semánticos.
- Tabs mantiene sus secciones y su indicador; sus medidas y colores se toman del contrato normativo.
- Tablet/plegables mantienen la adaptación por `WindowSizeClass` y el límite de ancho de contenido.
- No se cambia la navegación funcional como parte de la reforma visual.

### Composición, listas y tarjetas

- Cell-group sigue siendo la composición de listas y ajustes: fila con icono opcional, título, descripción, nota derecha y chevron cuando navega. El divisor y el encabezado de grupo se resuelven con tokens semánticos.
- Las tarjetas se reservan para contenido agrupado; no se envuelve cada fila en una tarjeta independiente.
- La barra fija inferior `AqBottomAction` contiene como máximo una primaria, respeta safe-area e IME y se separa del contenido por divisor.
- Empty usa icono, descripción y acción útil; no repite una acción ya disponible. Skeleton y Loading no introducen movimiento decorativo.
- Result usa icono, título y descripción secundaria.
- Los errores comunican qué ocurrió, qué pasó con los datos y cómo recuperarse; el color no es la única señal.
- Toast se presenta mediante `AqSnackbarHost`/`AqFeedbackEvent` y es efímero; Message es una tarjeta elevada para avisos de operación; Notice-bar reserva avisos persistentes como modo local/sin conexión.
- Tag comunica el estado con texto y semántica, no solo con color.

### Campos y formularios

- Input mantiene etiqueta, contenido, ayuda y error contextual asociados; el error incluye icono, texto y semántica `error()`.
- Search conserva foco visible y acción de cancelar cuando corresponda.
- La obligatoriedad, los errores y los estados de guardado se comunican con texto/semántica, no solo con color.
- Las acciones fijas respetan las barras del sistema y el teclado (IME); no ocultan el contenido editable.
- Los flujos de importación conservan selección, revisión y confirmación con jerarquía visual consistente.

### Botones y acciones

- Los botones conservan su jerarquía semántica: primaria, secundaria, terciaria, destructiva y acciones de texto. Sus tamaños, radios y estados se toman del contrato normativo; el área interactiva siempre es ≥48 dp (D-02).
- La destructiva requiere confirmación con etiqueta específica de consecuencia.
- Acciones frecuentes visibles donde ahorren pasos; no duplicar la misma acción en varias zonas de una pantalla.
- El estado presionado usa tokens semánticos; se conservan foco por teclado/D-pad, háptica de plataforma y estado deshabilitado accesible. No hay escala de pulsación ni spring.
- Dialog permite apilar botones cuando sus etiquetas no caben; sus medidas y estados se toman del contrato normativo.

### Tipografía e iconografía

- La tipografía es Inter (SIL OFL 1.1), empaquetada como recurso multiplataforma (D-05); sus roles, tamaños e interlíneas se toman del contrato normativo y de la instantánea versionada.
- Los importes conservan cifras tabulares. Se mantiene el escalado del sistema hasta 200 % (D-06); la validación automática cubre 100 %/150 % a 360 dp y el 200 % queda como revisión manual (ADR-0007).
- La iconografía se expone a través de `AqIcon`/`AqIcons` desde un único set vectorial; no se mezclan familias ni se añade una dependencia visual de runtime.

### Color y tema oscuro

- Claro y oscuro usan los roles semánticos del contrato normativo para acciones, estados, neutros, texto, superficies y bordes. No se admiten gradientes ni roles promo/fintech.
- El contraste se verifica según AA para texto y elementos no textuales. Ningún color se define fuera de la capa de tokens.
- El lienzo del QR permanece blanco en ambos temas para preservar su zona de silencio escaneable (X-03).
- El tema oscuro se construye desde los roles publicados y las derivaciones registradas como X-05; no es una inversión mecánica del claro.

### Movimiento

- Las transiciones usan las duraciones y curvas de la instantánea versionada; los roles derivados están registrados como X-04. No se usa spring ni escala de pulsación.
- Movimiento reducido elimina la transición; no se añade confeti, shimmer ni otro movimiento que no tenga función existente.

### Excepciones documentadas

- Los acentos de contexto conservan su asignación determinista por dominio (X-06); sus pares se seleccionan entre los colores verificados de la instantánea y se validan a AA.
- `AqNavRail` se conserva en tablet/plegables donde el sistema visual de referencia no define un equivalente; se rige por el contrato normativo. ADR-0007 retiró su verificación de layout automática; requiere revisión manual en dispositivo.
- Los componentes mantienen targets interactivos ≥48 dp aunque el tamaño visual de referencia sea menor (D-02).
- La sombra CSS de varias capas se representa en Compose con la capa dominante o `drawBehind`; la diferencia queda declarada en el contrato normativo.
- Los menús de opciones y selectores usan `AqDialog` con listas; la alternativa permite verificar de forma estable sus acciones bajo Robolectric sin cambiar el flujo.

## 13. Fuera de esta congelación

Fuera del alcance de esta especificación conceptual: detalles internos de Kotlin/Compose, implementación de ViewModels y state holders, persistencia concreta y resultados de pruebas por plataforma. Esos detalles y su estado de verificación se documentan en `docs/STATE.md`.

## 14. Estado

**UX/UI V1.9 funcional + V1.21 de composición funcional: FROZEN** (ADR-0019, ADR-0025). La dirección visual (§12) está descongelada y se rige por ADR-0025 y el contrato normativo v8.3; las invariantes funcionales listadas al inicio permanecen congeladas.

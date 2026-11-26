# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN — contrato funcional; dirección visual vigente en Moon Design (ADR-0044)
**Alcance:** contrato UX/UI conceptual; no es implementación Compose.

**Alcance:** la composición de pantallas y la dirección visual se rigen por el contrato normativo Moon vigente (ADR-0044). La intención, el modelo mental (§2), las reglas (§3), las reglas de negocio, el orden de los flujos, la navegación conceptual y su back stack, el contrato local-first, el copy funcional, la guía de voz (ADR-0008) y la accesibilidad (§11) permanecen congelados.

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
- Desde otra app es una entrada de primer nivel donde la plataforma lo soporta (Android: share intents; iOS: pendiente de Share Extension; no se muestra).
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

### S00 — Acceso local
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

### S01 — Inicio
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

### Detalle de destino
Intención: mostrar el QR. La única acción primaria es "Mostrar QR" (pantalla completa); Compartir es secundaria; Registrar actividad, Editar y Eliminar viven en "…". La tarjeta del QR también abre la pantalla completa. La pantalla completa muestra el QR al máximo con el nombre debajo, brillo al máximo y pantalla encendida (restaurados al salir). El contenido decodificado no aparece en las listas; en el Detalle va en bloque expandible, seleccionable y copiable.

Composición: navbar con atrás y overflow "…" (Action-sheet); QR en tarjeta sobre lienzo blanco; "Mostrar QR" como acción primaria y "Compartir" como secundaria. Los metadatos se agrupan como Cell-group; el contenido decodificado permanece expandible y copiable. Eliminar conserva su confirmación destructiva.

### S02 — Añadir
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

### S03 — Cámara
Intención: capturar un QR físico. La detección debe ser automática cuando sea posible. No se muestran métricas técnicas del scanner.

Composición: navbar transparente sobre la vista previa y marco de escaneo con esquinas según el sistema visual vigente. No se añaden métricas ni controles que cambien el flujo de captura.

### S04 — Buscar
Intención: encontrar cualquier cosa de Agenda QR. Los resultados pueden ser heterogéneos y muestran su tipo mediante texto/semántica. El foco automático solo llega desde el disparador de Inicio; por pestaña se entra sin foco.

Composición: Search con acción de cancelar. Los resultados se agrupan por tipo en Cell-group y llevan Tag textual; los encabezados de grupo llevan el conteo real (V1.19); la consulta se conserva al volver.

### S05 — Resultados
Superficie de decisión. Al volver se conserva la consulta.

Composición: Search y resultados agrupados por tipo en Cell-group; Empty cuando no hay resultados y el estado de error conserva su recuperación. No se altera el foco ni la persistencia de la consulta.

### S06 — Contexto
Intención: comprender y continuar dentro de un contexto. No es un dashboard financiero. La acción primaria depende del flujo que llevó al usuario allí.

Composición: navbar con título y atrás; Tabs QR · Actividades · Comprobantes, como secciones del mismo contexto y no como destinos raíz. El contenido usa Cell-group. El acento del contexto se conserva para encabezados y marcadores (X-06).

### S07 — Registrar
Intención: registrar una actividad. El contexto ayuda pero no bloquea el registro. Captura rápida con divulgación progresiva: Tipo · Importe+Moneda · Persona o entidad · Fecha y hora · Contexto · Más detalles (plegado salvo que existan) · Guardar fijo abajo.

- Formulario en celdas: Tipo con Segmented; Importe y moneda en Input; entidad con sugerencias; fecha/hora en selector dentro de `AqDialog` con atajos Hoy/Ayer como Tags; Contexto como celda navegable hacia S08; Más detalles como Collapse. Guardar conserva su posición fija.
- Importe opcional con validación real (dígitos y un separador decimal, máx. 2 decimales); moneda BOB/USD/Otra (por defecto, la última usada); persona con sugerencias de contrapartes recientes.
- Fecha y hora con selectores (sin texto libre) y atajos Hoy/Ayer; por defecto, ahora con hora.
- Salir con cambios pide "¿Descartar cambios?"; sin cambios sale de inmediato. El Back del sistema sigue el mismo camino.

### S08 — Seleccionar contexto
Intención: elegir contexto para el flujo padre. Al terminar, vuelve al flujo original conservando el draft.

Composición: `AqDialog` con lista de opciones Radio en celdas, creación sin salir del draft y "Quitar contexto" como acción destructiva dentro del contenido; "Cancelar" es la única salida (V1.20: sin búsqueda — el selector trabaja sobre pocos elementos). Si no se puede verificar de forma reproducible, se conserva el patrón de selección en página completa, sin perder el draft ni cambiar el back stack.

### S09 — Revisar QR
Intención: verificar lo que Agenda QR entendió antes de guardar. Debe mostrar evidencia suficiente sin exponer payload técnico innecesario.

Composición: vista previa del QR en tarjeta, evidencia agrupada en Cell-group, avisos en Notice-bar/Message y acción primaria Guardar fija al pie. No se muestra payload técnico innecesario.

### S10 — Revisar comprobante
Intención: confirmar y, cuando corresponda, asociar.

    ASOCIAR
    Guardar sin asociar

Ambos son resultados legítimos.

Composición: vista previa y Cell-group. Asociar es primaria; "Guardar sin asociar" es secundaria `default`; ambos resultados legítimos y permanecen disponibles según el estado actual.

### S11 — Resolver ambigüedad
Intención: resolver una única duda que el sistema no puede decidir con seguridad.

Composición: una pregunta y un Radio-group de celdas; Continuar es primaria y permanece deshabilitada hasta seleccionar una opción.

### S12 — Resultado de importación masiva
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

### Actividades y detalle
La lista mantiene los filtros y la búsqueda existentes; se compone como Cell-group con título, descripción y nota de importe/fecha. Si la superficie ya ofrece una acción flotante, conserva esa función. El detalle presenta el importe con cifras tabulares y sus metadatos en Cell-group, con Tag textual para el estado. No se modifica el modelo de operaciones ni su significado.

### Cuenta y respaldo
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

## 11. Accesibilidad y movimiento

- targets interactivos de al menos 48 dp según el contrato normativo (D-02);
- significado independiente del color;
- estados expresados también mediante texto/semántica;
- tipografía adaptable;
- reduced motion respetado;
- comprensión independiente de la animación.

Reglas vigentes:

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

## 12. Contrato visual — Moon Design (ADR-0044)

> La intención, los flujos, el orden de navegación, el copy funcional y la recuperación permanecen congelados en esta especificación. Esta sección no define tokens ni anatomía visual.

La dirección visual completa se rige por [Moon Design System](../05-design-system/README.md). Sus fuentes normativas son el contrato, la instantánea de tokens y el catálogo de componentes. `core/ui` es la implementación canónica; las pantallas componen sus componentes y tokens semánticos. No se permiten estilos locales, componentes Material ni valores visuales inventados. Los campos que eligen una opción usan `AqSelect` (campo cerrado con menú `AqDropdown` anclado); `AqCombobox` cubre selección con búsqueda

La navegación, el back stack, las acciones y la intención por pantalla permanecen invariantes. Los cambios funcionales requieren una decisión UX explícita y actualización coordinada del contrato, el código y las pruebas.

**UX/UI V1: FROZEN** para el contrato funcional. La autoridad visual es Moon Design y puede perfeccionarse sin alterar intención, navegación ni reglas de producto.

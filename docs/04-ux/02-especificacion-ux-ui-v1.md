# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN — V1.9 para contrato funcional; identidad visual definida por ADR-0020 y desarrollada en `docs/05-design-system/00-contrato-normativo-diseno.md`  
**Fecha:** 2026-10-11  
**Alcance:** contrato UX/UI conceptual; no es implementación Compose.

## Historial de enmiendas

| Versión | Fecha | Alcance |
|---|---|---|
| V1.0 | 2026-09-23 | Congelación original. |
| V1.1 | 2026-10-05 | Enmienda por ADR-0005 (lenguaje visual Metro dentro de Xauxa): S01, S06, §11 y §12 reescritos. |
| V1.2 | 2026-10-11 | Enmienda por ADR-0012 (UI convencional sobre Material 3, misma en ambas plataformas): §12 reescrito como diseño convencional (M1–M13 eliminados), §4 S01/S02/S12 recompuestos, §3 y §11 ajustados donde cambian. Intención, navegación conceptual, modelo mental, reglas de negocio y recuperación de errores: intactos. Aprobación explícita del propietario para enmendar la congelación registrada en ADR-0012. |
| V1.3 | 2026-10-12 | Enmienda por ADR-0013 (sistema de diseño propio AgendaMobile): §12 actualizado a la autoría, paleta, formas y modo oscuro de AgendaMobile. Intención, navegación, modelo mental, reglas de negocio, accesibilidad y copy: intactos. |
| V1.4 | 2026-10-13 | Enmienda por ADR-0014 (referencia Ant Design Mobile + navegación convencional Android/iOS): §12 actualizado a la referencia, paleta, radios y componentes oficiales; navegación jerárquica y de destinos conforme a plataforma. Intención, navegación conceptual, modelo mental, reglas de negocio, accesibilidad y copy: intactos. |
| V1.5 | 2026-10-13 | Enmienda de compacidad (escala compacta neutra): §12 delega tipografía (cuerpo 14, título 18, tracking neutro), barras compactas (superior 56 dp, inferior 64 dp sin píldora) y densidad al contrato normativo. Intención, flujos, reglas de negocio, accesibilidad y copy: intactos. |
| V1.6 | 2026-10-14 | Enmienda por ADR-0016 (identidad local sin cuenta): §4 añade S00 Acceso local (configurar PIN en el primer arranque, desbloquear con PIN) y S01 cambia "Cerrar sesión" por "Cuenta y respaldo" + "Bloquear" en el menú "…". El acceso deja de ser una pantalla de login remoto. Modelo mental, reglas de negocio, accesibilidad y copy de las pantallas existentes: intactos. |
| V1.7 | 2026-10-16 | Enmienda por ADR-0017 (alineación fiel con Ant Design Mobile 5.43.0): §12 reescrito a los valores oficiales (escala 17/15/13, radios 2/4/6/8, paleta oscura oficial, diálogo con fila hairline, buscador SearchBar, secundario = botón `default`); corrige el error "barra inferior 64 dp" de V1.5 (son 56 dp). Intención, flujos y copy: intactos. Aprobación explícita del propietario para enmendar la congelación del §12 (alineación con Ant Design Mobile, ADR-0017); intención, flujos y copy intactos. |
| V1.8 | 2026-10-16 | Enmienda por ADR-0018 (correcciones UX de captura rápida): S00 (gracia de bloqueo 120 s, biometría automática, demo solo al inicio), S01 (hub de creación, sin duplicados, Favoritos, lista sin favoritos del carrusel), Detalle de destino (Mostrar QR como primaria, pantalla completa con brillo), S07 (editor con divulgación progresiva, validación real, descarte confirmado), S04 (foco solo desde Inicio). Intención, modelo mental y reglas de negocio: intactos. Aprobación explícita del propietario para enmendar la congelación en lo que ADR-0018 cambia. |
| V1.9 | 2026-10-16 | Enmienda por ADR-0019 (inicio vacío): S01 vacío de primera ejecución muestra "Traer un QR", el FAB se oculta sin destinos ni búsqueda, y se retira el encabezado "Todos los destinos" sobre el vacío. Intención, modelo mental y reglas de negocio: intactos. |
| V1.10 | 2026-10-16 | Enmienda visual por ADR-0020: identidad propia inspirada en aplicaciones chinas de consumo; paleta azul tecnológico + superficies neutras azul-gris; sin cambios en navegación, flujos, copy funcional o reglas de negocio. |

La composición funcional vigente corresponde a V1.9; la identidad visual vigente corresponde a V1.10/ADR-0020. Las capacidades verificadas, las limitaciones por plataforma y los resultados de las puertas de calidad se mantienen en `docs/STATE.md` y `docs/QUALITY.md`. Si la implementación se aparta de esta especificación, se corrige el código o se aprueba una enmienda explícita; la especificación gobierna intención, composición funcional y flujos.

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

### S00 — Acceso local (V1.6, ADR-0016)
Intención: entrar al dispositivo propio. No es un login: no hay correo, contraseña ni red.

    PRIMER ARRANQUE      configurar PIN (código + confirmación) → Guardar
    ARRANQUES POSTERIORES desbloquear con PIN

- El perfil local se crea solo, sin cuenta; el PIN protege el acceso al dispositivo.
- El PIN es de 4 a 12 dígitos, campo numérico oculto; el error se asocia al campo.
- Confirmación distinta → error "no coinciden" sin crear credencial.
- Código incorrecto → error con intentos restantes; al agotar intentos hay un bloqueo temporal progresivo.
- El bloqueo ocurre al pasar la app a segundo plano; al reanudar se vuelve a pedir el PIN. Con un flujo externo iniciado por la app (galería, cámara, selectores, compartir) hay una gracia acotada de 120 s; vencida, se bloquea al volver.
- Con biometría disponible e inscrita, el desbloqueo la solicita sola una vez por bloqueo (con el PIN como respaldo); la demostración solo existe en el primer arranque.
- El PIN es local; nunca viaja a la red y no es la clave de cifrado de los datos.

### S01 — Inicio (V1.8, ADR-0018)
Intención: encontrar o iniciar algo. Buscar domina visualmente; Añadir abre el hub de creación.

Composición (la intención no cambia; solo cambia la presentación):

    [barra superior: título "Agenda QR" — título estándar]
    [campo de búsqueda como entrada a Buscar — primer bloque, dominante]
    [filtros de la lista como píldoras: Todos · Favoritos (activo sólido, idempotente)]
    [sección "Favoritos": carrusel horizontal (la siguiente tarjeta asoma como indicio)]
    [sección "Todos los destinos": lista convencional de filas (sin los favoritos del carrusel)]
      [fila: tile QR tonal · título · metadato caption · favorito (estrella)]
      [estado vacío con acción "Traer un QR" cuando no hay nada]
    [tarjeta de disponibilidad sin conexión]
    [botón flotante extendido "Añadir" → diálogo "¿Qué quieres añadir?": Traer un QR / Registrar actividad / Crear contexto]

- Inicio es una lista convencional (sin tiles, sin tile vivo): una sola
  entrada de búsqueda (sin duplicar campo superior y acción inferior) y
  las acciones de pantalla viven en la barra superior, el FAB y la
  barra de navegación inferior. Sin acciones rápidas duplicadas en el
  cuerpo (la entrada a S02 vive en el hub y en S02).
- El FAB abre el hub de creación con las tres creaciones reales del
  producto (traer QR → S02, crear contexto → diálogo de creación,
  registrar actividad → S07 sin preasociación); el hub se materializa
  como diálogo por la limitación de `ModalBottomSheet` bajo Robolectric
  (ADR-0013).
- Favoritos es un filtro de la propia lista (y del cuerpo de Inicio),
  no un destino aparte; en Todos la lista excluye los favoritos del
  carrusel (sin duplicación).
- Navegación inferior (Inicio · Buscar · Actividades · Contextos) solo
  en las superficies raíz; cambiar de destino conserva la raíz del back
  stack (Back desde otro destino vuelve a Inicio; desde Inicio sale de
  la app).
- El menú "…" de la barra superior de Inicio ofrece "Cuenta y respaldo" (nube opcional) y "Bloquear".

### Detalle de destino (V1.8, ADR-0018)
Intención: mostrar el QR. La única acción primaria es "Mostrar QR" (pantalla completa); Compartir es secundaria; Registrar actividad, Editar y Eliminar viven en "…". La tarjeta del QR también abre la pantalla completa. La pantalla completa muestra el QR al máximo con el nombre debajo, brillo al máximo y pantalla encendida (restaurados al salir). El contenido decodificado no aparece en las listas; en el Detalle va en bloque expandible, seleccionable y copiable.

### S02 — Añadir (V1.3, ADR-0013)
Intención: traer algo que el usuario ya tiene.

    ELEGIR DE LA GALERÍA (una o varias imágenes)
    ELEGIR ARCHIVO (PDF, opcional; solo donde la plataforma lo soporta)
    ESCANEAR CON LA CÁMARA
    [texto de privacidad de la cámara junto a esta acción]
    ["Desde otra app": ayuda breve, solo donde la plataforma lo soporta]

- Galería es una única acción con selección múltiple: 1 elemento →
  revisión única (S09); 2 o más → lote (S12). No existe el par
  "Galería / Galería (varios)".
- El bloque "Desde otra app" es una ayuda breve, no una sección
  permanente destacada, y solo aparece donde la plataforma soporta
  compartir hacia Agenda QR.
- Jerarquía conservada: Galería → Cámara.

### S03 — Cámara
Intención: capturar un QR físico. La detección debe ser automática cuando sea posible. No se muestran métricas técnicas del scanner.

### S04 — Buscar
Intención: encontrar cualquier cosa de Agenda QR. Los resultados pueden ser heterogéneos y muestran su tipo mediante texto/semántica. El foco automático solo llega desde el disparador de Inicio; por pestaña se entra sin foco.

### S05 — Resultados
Superficie de decisión. Al volver se conserva la consulta.

### S06 — Contexto (V1.3, ADR-0013)
Intención: comprender y continuar dentro de un contexto. No es un dashboard financiero. La acción primaria depende del flujo que llevó al usuario allí.

Composición: la superficie del contexto se estructura como **secciones QR · Actividades · Comprobantes** (pestañas estándar), reflejando directamente el modelo mental de §2; no crea taxonomía nueva de navegación. El acento del contexto (§12, Color) tiñe sus encabezados de sección y elementos de foco. La barra superior lleva el título y la flecha atrás; las acciones de la pantalla viven en el cuerpo (§12, Navegación y chrome).

### S07 — Registrar (V1.8, ADR-0018)
Intención: registrar una actividad. El contexto ayuda pero no bloquea el registro. Captura rápida con divulgación progresiva: Tipo · Importe+Moneda · Persona o entidad · Fecha y hora · Contexto · Más detalles (plegado salvo que existan) · Guardar fijo abajo.

- Importe opcional con validación real (dígitos y un separador decimal, máx. 2 decimales); moneda BOB/USD/Otra (por defecto, la última usada); persona con sugerencias de contrapartes recientes.
- Fecha y hora con selectores (sin texto libre) y atajos Hoy/Ayer; por defecto, ahora con hora.
- Salir con cambios pide "¿Descartar cambios?"; sin cambios sale de inmediato. El Back del sistema sigue el mismo camino.

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

### S12 — Resultado de importación masiva (V1.3, ADR-0013)
Superficie de decisión derivada de Añadir.

    [barra superior: título "Resultado de importación" + atrás]
    [lista agrupada]
      ✓ reconocidos
      ! duplicados
      ? pendientes
    [ GUARDAR RECONOCIDOS — botón principal fijo abajo,
      solo cuando hay elementos reconocibles ]
    [ Revisar N pendientes — botón secundario ]

Los válidos no dependen de los pendientes. Mientras se guarda no hay
vuelta (el lote se perdería); el Back del estado de revisión regresa al
resultado (flujo padre).

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

- targets interactivos de 44–48dp como mínimo según plataforma;
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
  no se truncan con la fuente del sistema al 200 %.

## 12. Contrato visual — V1.10, ADR-0020

> **Precedencia:** esta sección define la dirección visual de producto. Para tokens, contraste, componentes, estados, accesibilidad, pruebas y gobernanza, prevalece `docs/05-design-system/00-contrato-normativo-diseno.md`. La UX funcional y la navegación raíz no cambian por esta enmienda.

AgendaQr adopta una identidad visual propia inspirada en aplicaciones móviles chinas de consumo: útil desde el primer vistazo, compacta sin ser apretada, jerárquica y visualmente pulida. No se copia una superapp ni se añaden adornos culturales genéricos. La interfaz sigue siendo nativa en Compose Multiplatform; no se incorpora React, CSS ni una librería web.

### Principios

1. Contenido y acción útil antes que decoración.
2. Una intención principal y una acción principal por pantalla.
3. Densidad controlada: filas compactas, metadatos alineados, divisores discretos y pocas tarjetas anidadas.
4. Azul tecnológico para acción y selección; superficies neutras con matiz azul-gris; tonos semánticos solo para estados.
5. El QR es un elemento central del producto: alto contraste, lienzo blanco y acciones directas.
6. Accesibilidad, funcionamiento offline y consistencia Android/iOS son invariantes.

### Navegación y chrome

- Se conservan las convenciones existentes: barra superior jerárquica de 56 dp, navegación inferior de 56 dp solo en superficies raíz, back stack restaurable y comportamiento nativo de Atrás.
- La navegación raíz continúa siendo Inicio, Buscar, Actividades y Contextos; los detalles, editores e importaciones no duplican esa barra.
- La barra de navegación utiliza selección por color y etiqueta legible, sin indicador decorativo tipo píldora.
- Tablet/plegables mantienen adaptación por `WindowSizeClass` y límite de ancho de contenido.
- No se cambia la navegación funcional como parte de la reforma visual.

### Composición, listas y tarjetas

- Una cabecera clara por pantalla; acciones contextuales visibles limitadas a las más importantes.
- Las listas son la composición base para destinos, actividades, contextos y comprobantes. Cada fila distingue título, metadatos y acción sin competir por atención.
- Las tarjetas se reservan para contenido agrupado o acciones que se benefician de un límite visual; no envolver cada fila en una tarjeta independiente.
- Las secciones usan espaciado consistente; el encabezado permanece próximo al contenido que introduce.
- Los estados vacíos incluyen una explicación breve y la acción más útil cuando existe.
- Los estados de error, carga, éxito y sincronización conservan texto e iconografía; el color no es la única señal.

### Campos y formularios

- Etiqueta persistente, contenido, ayuda y error contextual claramente asociados.
- El buscador tiene un tratamiento propio y reconocible, con foco visible y objetivo táctil mínimo de 48 dp.
- La obligatoriedad, los errores y los estados de guardado se comunican con texto/semántica, no solo con color.
- Las acciones fijas respetan las barras del sistema y el teclado (IME); no ocultan el contenido editable.
- Los flujos de importación conservan selección, revisión y confirmación con jerarquía visual consistente.

### Botones y acciones

- Una acción primaria por contexto; las secundarias no compiten en relleno o tamaño.
- Las acciones destructivas se separan visual y semánticamente de las habituales.
- Acciones frecuentes visibles donde ahorren pasos; no duplicar la misma acción en varias zonas de una pantalla.
- Todos los controles interactivos conservan objetivo táctil mínimo de 48 dp, foco, estado presionado y estado deshabilitado accesibles.

### Tipografía e iconografía

- Se conserva inicialmente la familia de sistema y la escala semántica existente: título 18 sp, cuerpo/fila 17 sp, etiqueta 15 sp, metadatos 13 sp, apoyo 12 sp y etiquetas compactas 11 sp.
- MiSans Global y una familia monoespaciada son candidatas para evaluación local, no dependencias aprobadas. No se empaquetará una fuente hasta validar cobertura, licencia, tamaño y capturas en Android/iOS.
- Lucide permanece como familia única a través de `AmIcon`/`AmIcons`. No se mezclan familias ni se migran iconos sin comparar las pantallas reales.
- Los importes y datos técnicos pueden usar alineación numérica o monoespaciado solo si mejora la lectura; no se aplica estética de consola a toda la interfaz.

### Color y tema oscuro

- Tema claro: fondo `F7F8FA`, superficies `FFFFFF`/`F1F4F8`, texto `1F2937`, acento `245FE5`.
- Tema oscuro: fondo `10141B`, superficie `171D26`, secundaria `0C1118`, texto `F1F5F9`, acento `70A5FF` con tinta `101B2E`.
- Los roles semánticos de éxito, advertencia, peligro e información se definen en los tokens. Contraste de texto ≥ 4.5:1 y foco/bordes funcionales ≥ 3:1.
- El lienzo del QR permanece blanco en ambos temas para preservar su zona de silencio escaneable.
- El tema oscuro se diseña como una paleta independiente, no como inversión mecánica del claro.

### Movimiento

- Las transiciones son breves, consistentes y funcionales. El movimiento reducido elimina la animación en lugar de acelerarla.
- No se añaden sombras generalizadas, transparencias decorativas ni animaciones complejas para aparentar una estética de marca.

## 13. Fuera de esta congelación

Fuera del alcance de esta especificación conceptual: detalles internos de Kotlin/Compose, implementación de ViewModels y state holders, persistencia concreta y resultados de pruebas por plataforma. Esos detalles y su estado de verificación se documentan en `docs/STATE.md`.

## 14. Estado

**UX/UI V1.9 funcional + V1.10 visual: FROZEN** (ADR-0019 y ADR-0020, 2026-10-16).

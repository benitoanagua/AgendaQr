# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN — V1.8 para contrato funcional; contrato visual definido por ADR-0017 (alineación fiel con Ant Design Mobile 5.43.0 + navegación Android/iOS) y desarrollado en `docs/05-design-system/00-contrato-normativo-diseno.md`  
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

La implementación vigente corresponde a V1.8. Las capacidades verificadas, las limitaciones por plataforma y los resultados de las puertas de calidad se mantienen en `docs/STATE.md` y `docs/QUALITY.md`. Si la implementación se aparta de esta especificación, se corrige el código o se aprueba una enmienda explícita; la especificación gobierna intención, composición funcional y flujos.

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

## 12. Contrato visual — V1.7, ADR-0017

> **Precedencia:** esta sección conserva la decisión de producto visual adoptada por ADR-0014 (referencia Ant Design Mobile + navegación Android/iOS). Para reglas transversales detalladas, estados, accesibilidad, pruebas, gobernanza y resolución de contradicciones, prevalece `docs/05-design-system/00-contrato-normativo-diseno.md`. Si un detalle de esta sección contradice el contrato normativo, se debe reconciliar documentalmente; no se permite implementar una mezcla tácita.

La UI es convencional, mobile-first y poco disruptiva: componentes estándar de Material 3 con el MISMO diseño en Android e iOS. La marca queda en el color de acento y poco más. Se conservan la paleta verificada, el lienzo claro del QR y las reglas de accesibilidad (§11). La referencia visual es Ant Design Mobile: la capa de tokens (`AmTokens.kt`) y los componentes `Am*` son la única API visual de las features; la navegación sigue las convenciones de Android e iOS.

### Principios

1. Contenido antes que cromo.
2. Componentes estándar: todo control interactivo es Material 3 o un envoltorio fino AgendaMobile con color, forma y tipografía del tema; sin mecánicas de interacción paralelas propias.
3. Sin deriva de plataforma: la composición, el chrome y los componentes son idénticos en Android e iOS; solo la adquisición nativa (selectores, share) y las convenciones obligadas del sistema difieren.
4. La accesibilidad es contrato, no refinamiento (§11).

### Navegación y chrome

- Barra superior de 56 dp (título 18 a la izquierda, estilo estándar) en cada superficie: flecha atrás cuando haya a dónde volver — la misma en ambas plataformas —, acciones de la pantalla (máx. 2 visibles + menú "…").
- Barra de navegación inferior de 56 dp (`NavigationBar`, sin píldora) SOLO en las superficies raíz: Inicio, Buscar, Actividades y Contextos; cada destino con etiqueta 11 + icono (nunca solo icono). No aparece en detalle/edición/importación. Cambiar de destino conserva la raíz del back stack (Back desde otro destino vuelve a Inicio; desde Inicio sale de la app).
- La acción principal contextual vive en el cuerpo como `Button`, o fija abajo solo cuando existe una acción real de guardado (p. ej. "Guardar reconocidos" en S12). Una pantalla cuya barra solo tiene "Volver" no lleva barra inferior.
- El Back del sistema conserva su orden (flujo interno → pop de superficie → sistema); el predictive back de Android 14+ anima con el progreso del gesto.
- Tablet/plegables: adaptación por `WindowSizeClass`; el ancho expandido limita el contenido (`ContentMaxWidth`) y la navegación raíz pasa a riel (`NavigationRail`).
- S06 conserva sus secciones QR · Actividades · Comprobantes (pestañas estándar). S04/S05 conservan resultados agrupados por tipo con encabezados de sección.

### Campos de entrada

- Campo con etiqueta, texto de apoyo y error contextual debajo (icono + texto, no solo color); el campo de texto conserva etiqueta flotante persistente (desviación documentada frente al patrón Form/List) y guía hairline inferior en reposo (divisor), en foco (acento) y en error (peligro).
- Buscador estilo SearchBar: relleno box, radio 6, sin etiqueta flotante visible, borde de acento en foco, altura mínima 48 dp.
- Teclado e IME según el tipo de campo; autofill de credenciales; mostrar/ocultar contraseña con botón de descripción accesible.
- La obligatoriedad se indica con texto/semántica accesible coherente con la guía de voz, no con asterisco decorativo.

### Listas y diálogos

- Filas con título 17 y descripción 13 en cuerpo de lista (card mode radio 8) con hairlines; flecha decorativa opt-in en filas que navegan; acciones por fila como botones de icono (favorito = conmutador de estrella con estado y descripción accesible), no como enlace de texto.
- Diálogos estilo Dialog: popup radio 8, título 18 bold centrado, contenido 15, fila de acciones a ancho completo separada por hairlines (confirmar en negrita, destructiva en peligro); foco inicial en la acción conservadora cuando hay pérdida irreversible. Sin hoja inferior (limitación Robolectric documentada).

### Botones

- Acción principal: `Button` con relleno de acento, radio 4. Secundaria: botón `default` (fondo claro + hairline + texto primario, radio 4). Terciaria: `TextButton`. Icono: `IconButton`/`IconToggleButton` con descripción accesible. Mínimo 48 dp de objetivo táctil; deshabilitado con opacidad .4.
- Estado deshabilitado y "guardando" expresados con etiqueta de texto (nunca solo con apariencia).

### Tipografía

- Familia del sistema en todos los estilos (Roboto en Android, San Francisco en iOS): sin fuente empaquetada propia. Escala de la referencia en sp: título 18, cuerpo/botón/fila 17, etiqueta 15, descripción 13, apoyo 12, tags y barra 11 (peso normal salvo título de diálogo bold y botón Medium); importe 26 SemiBold como extensión financiera; tracking neutro; roles estándar de Material 3.
- Capitalización de oración. El escalado de fuente del sistema se mantiene (§11).

### Color

- Acento del sistema (referencia Ant Design Mobile, recalibrado para contraste AA): claro `1668DC` (texto blanco, 5.19:1), oscuro `3086FF` (oficial) con texto de tinta (5.37:1). Superficies oficiales en ambos temas (página oscura `1A1A1A`, superficie `1F1F1F` con elevación documentada, box `0A0A0A`; hairlines `#EEEEEE`/`#2B2B2B`), pares de contenido verificados por pruebas.
- Paleta de identidad de contextos: el color se deriva de forma **determinista** del identificador del contexto, sin persistir el color ni cambiar el dominio; paleta acotada (12 + sistema), sin ampliar. Elegir color manualmente queda como punto abierto, coherente con ADR-0003 (sin edición de contextos en V1).
- Los colores de estado (error, advertencia, éxito, información) siguen siendo semánticos y distintos del acento; el significado nunca depende solo del color.
- El lienzo del código QR es claro en ambos temas (zona de silencio escaneable).

Tabla de contraste calculada (WCAG 2.x sobre luminancia relativa; razón del par texto asignado/fondo). Regla: texto normal ≥ 4.5:1. Todos los acentos usan texto blanco.

| Acento | Hex | Texto blanco | Texto asignado |
|---|---|---|---|
| blue | 1D4ED8 | 6.70 | blanco |
| sky | 0369A1 | 5.93 | blanco |
| teal | 0F766E | 5.47 | blanco |
| cyan | 0E7490 | 5.36 | blanco |
| emerald | 047857 | 5.48 | blanco |
| green | 15803D | 5.02 | blanco |
| lime | 4D7C0F | 4.99 | blanco |
| amber | A16207 | 4.92 | blanco |
| orange | C2410C | 5.18 | blanco |
| red | B91C1C | 6.47 | blanco |
| rose | BE123C | 6.29 | blanco |
| violet | 6D28D9 | 7.10 | blanco |
| sistema | 1668DC | 5.19 | blanco |

**Paleta final admitida: 12 acentos de contexto + el acento de sistema** (contrato normativo). Los pares están fijados por `AmAccentTest`; la asignación por identificador (FNV-1a) es estable entre plataformas.

### Formas y movimiento

- Escala de radios oficial en tokens (tag/segmentado 2, botón 4, búsqueda 6, tarjeta/diálogo/banner 8, hoja 12, píldora para chips de filtro/FAB/estados), consumida por `MaterialTheme.shapes`; ningún componente fija radios suyos.
- Ripple/indicación de pulsación estándar de Material 3; sin overlay de pulsación propio ni `indication = null`.
- Transición entre superficies: fundido con un desplazamiento horizontal pequeño, corto y con tokens de movimiento; con reduced motion el cambio es inmediato (§11).

### Iconografía

- **Set: Lucide** (licencia ISC — texto del aviso en `docs/05-design-system/06-licencias-terceros.md`), encapsulado por `AmIcon`; tamaño desde tokens. El set de iconos de Material queda prohibido en producción.
- Glifos de línea, trazo uniforme, un solo color. Iconos decorativos fuera del árbol de accesibilidad; iconos interactivos con descripción accesible (§11).
- Fuera del mapeo existente no se añaden iconos sin decisión.

### Modo oscuro

- Esquema oscuro oficial (fondo `1A1A1A`, superficie `1F1F1F` con elevación documentada, box `0A0A0A`, hairlines `2B2B2B`), con los mismos roles semánticos y umbrales de contraste que el claro; el acento es el azul oficial con texto de tinta (par AA verificado).

## 13. Fuera de esta congelación

Fuera del alcance de esta especificación conceptual: detalles internos de Kotlin/Compose, implementación de ViewModels y state holders, persistencia concreta y resultados de pruebas por plataforma. Esos detalles y su estado de verificación se documentan en `docs/STATE.md`.

## 14. Estado

**UX/UI V1.8: FROZEN** (enmienda ADR-0018, 2026-10-16).

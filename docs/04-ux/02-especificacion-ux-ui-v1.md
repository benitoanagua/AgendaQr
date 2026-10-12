# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN — V1.2 para contrato funcional y visual; contrato visual definido por ADR-0012 (diseño convencional sobre Material 3) y desarrollado en `docs/05-design-system/00-xauxa-contrato-normativo.md`  
**Fecha:** 2026-10-11  
**Alcance:** contrato UX/UI conceptual; no es implementación Compose.

## Historial de enmiendas

| Versión | Fecha | Alcance |
|---|---|---|
| V1.0 | 2026-09-23 | Congelación original. |
| V1.1 | 2026-10-05 | Enmienda por ADR-0005 (lenguaje visual Metro dentro de Xauxa): S01, S06, §11 y §12 reescritos. |
| V1.2 | 2026-10-11 | Enmienda por ADR-0012 (UI convencional sobre Material 3, misma en ambas plataformas): §12 reescrito como diseño convencional (M1–M13 eliminados), §4 S01/S02/S12 recompuestos, §3 y §11 ajustados donde cambian. Intención, navegación conceptual, modelo mental, reglas de negocio y recuperación de errores: intactos. Aprobación explícita del propietario para enmendar la congelación registrada en ADR-0012. |

La implementación de V1.2 está aplicada sobre `main` y verificada por las
puertas del design system y de módulos (estado y cobertura reales en
`docs/09-implementacion/01-estado.md`). Si en el futuro la implementación
se aparta de esta spec, el desvío se registra allí y se corrige la fuente
menos específica (contrato §2); la spec manda sobre el código en intención,
composición funcional y flujos.

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

### S01 — Inicio (V1.2, ADR-0012)
Intención: encontrar o iniciar algo. Buscar domina visualmente; Añadir y Registrar son acciones secundarias.

Composición (la intención no cambia; solo cambia la presentación):

    [barra superior: título "Agenda QR" — título estándar]
    [campo de búsqueda como entrada a Buscar — primer bloque, dominante]
    [sección "Recientes": lista convencional de filas]
      [favorito: conmutador de estrella por fila]
      [estado vacío con acción "Añadir" cuando no hay nada]
    [botón flotante extendido "Añadir"]

- Inicio es una lista convencional (sin tiles, sin tile vivo): una sola
  entrada de búsqueda (sin duplicar campo superior y acción inferior) y
  las acciones de pantalla viven en la barra superior, el FAB y la
  barra de navegación inferior.
- Favoritos es un filtro de la propia lista (y del cuerpo de Inicio),
  no un destino aparte.
- Navegación inferior (Inicio · Buscar · Actividades · Contextos) solo
  en las superficies raíz; cambiar de destino conserva la raíz del back
  stack (Back desde otro destino vuelve a Inicio; desde Inicio sale de
  la app).
- Cerrar sesión vive en el menú "…" de la barra superior.

### S02 — Añadir (V1.2, ADR-0012)
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
Intención: encontrar cualquier cosa de Agenda QR. Los resultados pueden ser heterogéneos y muestran su tipo mediante texto/semántica.

### S05 — Resultados
Superficie de decisión. Al volver se conserva la consulta.

### S06 — Contexto (V1.2, ADR-0012)
Intención: comprender y continuar dentro de un contexto. No es un dashboard financiero. La acción primaria depende del flujo que llevó al usuario allí.

Composición: la superficie del contexto se estructura como **secciones QR · Actividades · Comprobantes** (pestañas estándar), reflejando directamente el modelo mental de §2; no crea taxonomía nueva de navegación. El acento del contexto (§12, Color) tiñe sus encabezados de sección y elementos de foco. La barra superior lleva el título y la flecha atrás; las acciones de la pantalla viven en el cuerpo (§12, Navegación y chrome).

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

### S12 — Resultado de importación masiva (V1.2, ADR-0012)
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

## 11. Accesibilidad y movimiento (V1.2, ADR-0012)

- targets interactivos de 44–48dp como mínimo según plataforma;
- significado independiente del color;
- estados expresados también mediante texto/semántica;
- tipografía adaptable;
- reduced motion respetado;
- comprensión independiente de la animación.

Reglas V1.2:

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

## 12. Contrato visual — V1.2, ADR-0012

> **Precedencia:** esta sección conserva la decisión de producto visual adoptada por ADR-0012 (diseño convencional sobre Material 3). Para reglas transversales detalladas, estados, accesibilidad, pruebas, gobernanza y resolución de contradicciones, prevalece `docs/05-design-system/00-xauxa-contrato-normativo.md`. Si un detalle de esta sección contradice el contrato normativo, se debe reconciliar documentalmente; no se permite implementar una mezcla tácita.

La UI es convencional y poco disruptiva: componentes estándar de Material 3 con el MISMO diseño en Android e iOS. La marca queda en el color de acento y poco más. Se conservan la paleta verificada, el lienzo claro del QR y las reglas de accesibilidad (§11). Xauxa sigue siendo el único sistema de diseño: la capa de tokens (`XauxaTokens.kt`) y los envoltorios `Xauxa*` siguen siendo la única API visual de las features.

### Principios

1. Contenido antes que cromo (se conserva de Xauxa).
2. Componentes estándar: todo control interactivo es Material 3 o un envoltorio fino Xauxa con color, forma y tipografía del tema; sin mecánicas de interacción paralelas propias.
3. Sin deriva de plataforma: la composición, el chrome y los componentes son idénticos en Android e iOS; solo la adquisición nativa (selectores, share) y las convenciones obligadas del sistema difieren.
4. La accesibilidad es contrato, no refinamiento (§11).

### Navegación y chrome

- Barra superior (`TopAppBar` pequeña, título a la izquierda, estilo estándar) en cada superficie: flecha atrás cuando haya a dónde volver — la misma en ambas plataformas —, acciones de la pantalla (máx. 2 visibles + menú "…").
- Barra de navegación inferior (`NavigationBar`) SOLO en las superficies raíz: Inicio, Buscar, Actividades y Contextos; cada destino con etiqueta + icono (nunca solo icono). No aparece en detalle/edición/importación. Cambiar de destino conserva la raíz del back stack (Back desde otro destino vuelve a Inicio; desde Inicio sale de la app).
- La acción principal contextual vive en el cuerpo como `Button`, o fija abajo solo cuando existe una acción real de guardado (p. ej. "Guardar reconocidos" en S12). Una pantalla cuya barra solo tiene "Volver" no lleva barra inferior.
- El Back del sistema conserva su orden (flujo interno → pop de superficie → sistema); el predictive back de Android 14+ anima con el progreso del gesto.
- Tablet/plegables: adaptación por `WindowSizeClass`; el ancho expandido limita el contenido (`ContentMaxWidth`) y la navegación raíz pasa a riel (`NavigationRail`).
- S06 conserva sus secciones QR · Actividades · Comprobantes (pestañas estándar). S04/S05 conservan resultados agrupados por tipo con encabezados de sección.

### Campos de entrada

- `OutlinedTextField` con etiqueta, texto de apoyo y error contextual debajo (icono + texto, no solo color). El borde en reposo cumple ≥ 3:1 (token `borderControl`).
- Teclado e IME según el tipo de campo; autofill de credenciales; mostrar/ocultar contraseña con botón de descripción accesible.
- La obligatoriedad se indica con texto/semántica accesible coherente con la guía de voz, no con asterisco decorativo.

### Listas y diálogos

- Filas con `ListItem` (o equivalente M3), separación por divisor o espacio; acciones por fila como botones de icono (favorito = conmutador de estrella con estado y descripción accesible), no como enlace de texto.
- Diálogos y hojas: `AlertDialog`/`ModalBottomSheet` estándar.

### Botones

- Acción principal: `Button`. Secundaria: `OutlinedButton` o `TextButton`. Icono: `IconButton`/`IconToggleButton` con descripción accesible. Mínimo 48 dp.
- Estado deshabilitado y "guardando" expresados con etiqueta de texto (nunca solo con apariencia).

### Tipografía

- Familia del sistema en todos los estilos (Roboto en Android, San Francisco en iOS): sin fuente empaquetada propia. Tamaños en sp; roles estándar de Material 3 (el título de página es `titleLarge`/`headlineSmall`, el cuerpo `bodyLarge`).
- Capitalización de oración. El escalado de fuente del sistema se mantiene (§11).

### Color

- Acento de sistema por defecto: `0067B8` (texto blanco, 5.78:1). Esquemas claro y oscuro propios, con pares de contenido verificados por pruebas.
- Paleta de acentos asignables a contextos: el color se deriva de forma **determinista** del identificador del contexto, sin persistir el color ni cambiar el dominio; paleta acotada, sin ampliar. Elegir color manualmente queda como punto abierto, coherente con ADR-0003 (sin edición de contextos en V1).
- Los colores de estado (error, advertencia, éxito) siguen siendo semánticos y distintos de los acentos; el significado nunca depende solo del color.
- El lienzo del código QR es claro en ambos temas (zona de silencio escaneable).

Tabla de contraste calculada (WCAG 2.x sobre luminancia relativa; razón contra texto blanco y contra texto negro). Regla: texto normal ≥ 4.5:1; texto grande ≥ 3:1. Cada acento usa el color de texto que cumple; si ninguno cumple, se excluye.

| Acento | Hex | Texto blanco | Texto negro | Texto asignado | Estado |
|---|---|---|---|---|---|
| lime | A4C400 | 2.00 | 10.47 | negro | ADMITIDO |
| green | 60A917 | 2.92 | 7.18 | negro | candidato no incluido (paleta acotada a 12) |
| emerald | 008A00 | 4.53 | 4.64 | negro | ADMITIDO |
| teal | 00ABA9 | 2.84 | 7.40 | negro | ADMITIDO |
| cyan | 1BA1E2 | 2.90 | 7.24 | negro | ADMITIDO |
| cobalt | 0050EF | 6.19 | 3.39 | blanco | ADMITIDO |
| indigo | 6A00FF | 6.87 | 3.06 | blanco | ADMITIDO |
| violet | AA00FF | 5.06 | 4.15 | blanco | ADMITIDO |
| magenta | D80073 | 5.04 | 4.17 | blanco | ADMITIDO |
| crimson | A20025 | 8.19 | 2.56 | blanco | ADMITIDO |
| red | E51400 | 4.74 | 4.43 | blanco | ADMITIDO |
| orange | FA6800 | 2.98 | 7.04 | negro | ADMITIDO |
| amber | F0A30A | 2.11 | 9.95 | negro | ADMITIDO |
| yellow | E3C800 | 1.68 | 12.53 | negro | candidato no incluido (paleta acotada a 12) |
| brown | 825A2C | 6.09 | 3.45 | blanco | candidato no incluido (paleta acotada a 12) |
| steel | 647687 | 4.68 | 4.48 | blanco | candidato no incluido (paleta acotada a 12) |
| sistema | 0067B8 | 5.78 | 3.63 | blanco | acento por defecto |

**Paleta final admitida: 12 acentos de contexto** (lime, emerald, teal, cyan, cobalt, indigo, violet, magenta, crimson, red, orange, amber) + el acento de sistema `0067B8`. Ningún candidato fue excluido por contraste; los 4 no incluidos se descartan solo por acotar la paleta al máximo de 12 y quedan documentados. Nota: emerald con texto negro (4.64) queda cerca del umbral — aceptado y registrado en ADR-0005.

### Formas y movimiento

- Conjunto de formas explícito en tokens (extraSmall 4 dp, small 8 dp, medium 12 dp, large 16 dp, extraLarge 28 dp), consumido por `MaterialTheme.shapes`; ningún componente fija radios suyos.
- Ripple/indicación de pulsación estándar de Material 3; sin overlay de pulsación propio ni `indication = null`.
- Transición entre superficies: fundido con un desplazamiento horizontal pequeño, corto y con tokens de movimiento; con reduced motion el cambio es inmediato (§11).

### Iconografía

- **Set: Lucide** (licencia ISC — texto del aviso en `docs/05-design-system/06-licencias-terceros.md`), encapsulado por `XauxaIcon`; tamaño desde tokens. El set de iconos de Material queda prohibido en producción.
- Glifos de línea, trazo uniforme, un solo color. Iconos decorativos fuera del árbol de accesibilidad; iconos interactivos con descripción accesible (§11).
- Fuera del mapeo existente no se añaden iconos sin decisión.

### Modo oscuro

- Esquema oscuro propio (fondo negro puro), con los mismos roles semánticos y umbrales de contraste que el claro; los componentes no cambian entre temas.

## 13. Fuera de esta congelación

No definido en V1.2: implementación Kotlin/Compose concreta de esta enmienda, navegación interna de pantallas, ViewModels/state holders, persistencia concreta, tests instrumentados ni validación runtime en dispositivo. La implementación se alinea a V1.2 en la pasada de migración dedicada (registrado en `docs/09-implementacion/01-estado.md`).

## 14. Estado

**UX/UI V1.2: FROZEN** (enmienda ADR-0012, 2026-10-11).

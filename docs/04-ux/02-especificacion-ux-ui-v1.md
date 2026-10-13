# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN — V1.4 para contrato funcional y visual; la dirección visual vigente es Ant Design Mobile replicado en Compose según ADR-0014  
**Fecha:** 2026-10-11  
**Alcance:** contrato UX/UI conceptual; no es implementación Compose.

## Historial de enmiendas

| Versión | Fecha | Alcance |
|---|---|---|
| V1.0 | 2026-09-23 | Congelación original. |
| V1.1 | 2026-10-05 | Enmienda por ADR-0005 (lenguaje visual Metro dentro de Xauxa): S01, S06, §11 y §12 reescritos. |
| V1.2 | 2026-10-11 | Enmienda por ADR-0012 (UI convencional sobre Material 3, misma en ambas plataformas): §12 reescrito como diseño convencional (M1–M13 eliminados), §4 S01/S02/S12 recompuestos, §3 y §11 ajustados donde cambian. Intención, navegación conceptual, modelo mental, reglas de negocio y recuperación de errores: intactos. Aprobación explícita del propietario para enmendar la congelación registrada en ADR-0012. |
| V1.3 | 2026-10-12 | Enmienda por ADR-0013 (AgendaMobile; histórica). |
| V1.4 | 2026-10-12 | Enmienda por ADR-0014: Ant Design Mobile como autoridad visual y navegación convencional Android/iOS. Intención, modelo mental, reglas de negocio, accesibilidad y copy preservados. |

La implementación objetivo corresponde a V1.4. Los resultados reales de validación por plataforma se mantienen en `docs/09-implementacion/01-estado.md`. Las capacidades verificadas, las limitaciones por plataforma y los resultados de las puertas de calidad se mantienen en `docs/09-implementacion/01-estado.md`. Si la implementación se aparta de esta especificación, se corrige el código o se aprueba una enmienda explícita; la especificación gobierna intención, composición funcional y flujos.

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

### S01 — Inicio (V1.4, ADR-0014)
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

### S02 — Añadir (V1.4, ADR-0014)
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

### S06 — Contexto (V1.4, ADR-0014)
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

### S12 — Resultado de importación masiva (V1.4, ADR-0014)
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

## 11. Accesibilidad y movimiento (V1.4, ADR-0014)

- targets interactivos de 44–48dp como mínimo según plataforma;
- significado independiente del color;
- estados expresados también mediante texto/semántica;
- tipografía adaptable;
- reduced motion respetado;
- comprensión independiente de la animación.

Reglas vigentes V1.4:

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

## 12. Contrato visual — V1.4, ADR-0014

> Ant Design Mobile es la autoridad visual. El contrato detallado vive en `docs/05-design-system/00-agendamobile-contrato-normativo.md`. La implementación es Compose Multiplatform; no se incorpora la biblioteca React a Android/iOS.

- **Estilo:** superficies planas, fondo neutro, filas y formularios compactos, divisores finos, radios moderados y jerarquía tipográfica sobria.
- **Color:** azul Ant Design `#1677FF` para énfasis y selección; acción sólida accesible `#0958D9` cuando se use texto blanco normal. Estados semánticos de éxito, advertencia y error; el significado no depende solo del color.
- **Navegación raíz:** Inicio, Buscar, Actividades y Contextos en barra inferior, con icono y etiqueta. Solo aparece en raíces.
- **Navegación secundaria:** barra superior compacta con Atrás, título y acciones. Detalles, edición, importación y revisión no muestran la barra inferior.
- **Layout:** una columna de lectura en teléfono, contenido desplazable, acciones primarias claramente visibles y controles adaptados al teclado y las áreas seguras. En pantallas grandes se permite rail y ancho máximo de contenido.
- **Componentes:** replicar patrones de Button, Input, SearchBar, List, Tabs, TabBar, NavBar, Dialog/ActionSheet, Toast/NoticeBar, Badge/Tag, Empty, Skeleton y estados de carga/error de Ant Design Mobile.
- **Convenciones nativas:** Back y restauración respetan el stack del flujo; Android conserva el gesto predictivo; iOS conserva las convenciones del sistema. Selectores de medios, cámara y compartir usan APIs nativas.
- **Accesibilidad:** etiquetas accesibles, targets táctiles adecuados, contraste AA, escalado de fuente hasta 200 %, IME, foco y movimiento reducido.

## 13. Fuera de esta congelación

Fuera del alcance de esta especificación conceptual: detalles internos de Kotlin/Compose, implementación de ViewModels y state holders, persistencia concreta y resultados de pruebas por plataforma. Esos detalles y su estado de verificación se documentan en `docs/09-implementacion/01-estado.md`.

## 14. Estado

**UX/UI V1.4: FROZEN** (enmienda ADR-0014, 2026-10-12).

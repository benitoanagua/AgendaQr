# ADR-0031 — Adopción de primitivos móviles, componentes de experiencia nuevos y política de componentes sin uso

## Estado

Aprobado (normativo para la integración UI/UX en Agenda QR).

## Contexto

La capa `core/ui` cuenta con la reimplementación de Polaris y una suite de componentes y patrones móviles resultantes de la cadena de precedencia (ADR-0029: Polaris → Moon → nativo). Varios componentes implementados permanecían únicamente en pruebas, galería o previews sin uso visible en las pantallas de producción:

`AqSwipeActions`, `AqPullToRefresh`, `AqBottomSheet`, `AqActionSheet`, `AqTooltip`, `AqSpinner`, `AqProgressBar`, `AqProgressBlock`, `AqSkeleton`, `AqSelect`, `AqCheckbox`, `AqRadioButton`, `AqSwitch`, `AqSelectAllCheckbox`.

Además, se identificaron oportunidades de mejora de experiencia en flujos reales que requieren componentes o patrones nuevos. Había que decidir, componente por componente, si se adopta en pantallas reales o se retira, y establecer el criterio para crear componentes nuevos sin violar los límites duros.

## Decisión

### 1. Regla de componentes sin uso

Un componente definido en `core/ui` con cero uso en pantallas de producción (excluyendo tests, galería y previews) se adopta o se retira. Nunca se mantiene como "disponible por si acaso".

- **ADOPTAR** si mejora la experiencia en algún flujo real, aunque requiera estado de presentación nuevo (modo de selección, buffer de deshacer…), respetando los límites duros.
- **RETIRAR** (componente + ficha + pruebas + entrada de galería + referencias en docs/contrato) solo si no aporta mejora medible o exigiría romper un límite duro.

### 2. Decisión por los 13 primitivos sin uso

| Primitivo | Decisión | Pantallas y acciones donde quedó visible | Prueba | Captura |
|---|---|---|---|---|
| `AqSwipeActions` | **ADOPTAR** | S01 filas (favorito, eliminar con confirmación vigente); S07-list filas (editar, eliminar) | `DestinationsScreenA11yTest` (customAction), `ScreensA11yCoverageTest` | S01 con acciones reveladas; S07-list con acciones |
| `AqPullToRefresh` | **ADOPTAR** | S01 (recarga/sync por arrastre); S07-list (recarga de actividades) | Robolectric de presentación (invoca el intent de recarga/sync existente) | S01 refrescando; S07-list refrescando |
| `AqBottomSheet` | **ADOPTAR** | S03 detalle de destino (inspección sin abandonar el flujo) | Robolectric de presentación | detalle con hoja de opciones |
| `AqActionSheet` | **MANTENER** (ya en uso) | overflow de detalles (destino, operación) | `AqActionSheetTest` | detalle con overflow |
| `AqTooltip` | **ADOPTAR** | S03 detalle (alternativa accesible al icono de copiado sin texto) | Robolectric de presentación | tooltip de copiado |
| `AqSpinner` | **MANTENER** (en `AqLoading`) | estados de carga estandarizados | `AqAccessibilityContractTest` | carga con spinner |
| `AqProgressBar` | **ADOPTAR** | S09 ImportReview (guardado) | Robolectric de presentación | progreso de importación |
| `AqProgressBlock` | **ADOPTAR** | S12 ImportBatch (progreso real N de M) | Robolectric de presentación | importación con progreso real |
| `AqSkeleton` | **ADOPTAR** | ComprobanteViewer, ContextScreen (carga inicial) | Robolectric de presentación | carga con skeleton |
| `AqSwitch` | **ADOPTAR** | S02/S08 editor de destino (favorito como estado binario real) | Robolectric de presentación | editor con interruptor |
| `AqSelect` | **MANTENER** | sin consumo en pantallas de producción, pero es control de formulario base de Polaris (nivel 1) con pruebas dedicadas (`AqSelectTest`) y uso en la galería de previews; se conserva como parte del conjunto de formularios | `AqSelectTest` | galería de previews |
| `AqCheckbox` | **MANTENER** | sin consumo en pantallas de producción, pero es control de formulario base de Polaris (nivel 1) con pruebas dedicadas y uso en la galería de previews | pruebas de `core/ui` | galería de previews |
| `AqRadioButton` | **MANTENER** | sin consumo en pantallas de producción, pero es control de formulario base de Polaris (nivel 1) con pruebas dedicadas y uso en la galería de previews | pruebas de `core/ui` | galería de previews |
| `AqSelectAllCheckbox` | **MANTENER** | sin consumo en pantallas de producción, pero es control de formulario base de Polaris (nivel 1) con pruebas dedicadas (`AqSelectAllCheckboxTest`) y uso en la galería de previews | `AqSelectAllCheckboxTest` | galería de previews |

Los retirados se eliminan junto con su ficha, pruebas, entrada de galería y referencias en docs/contrato (criterio ya aplicado a `AqModalSheet`, `AqQrSheet`, `AqHeroHeader`, `AqDivider`, `AqVerticalDivider`).

**Justificación de los controles de formulario mantenidos sin consumo en pantalla:** `AqSelect`, `AqCheckbox`, `AqRadioButton` y `AqSelectAllCheckbox` son controles de formulario base del sistema Polaris (nivel 1), no primitivos móviles de interacción. Tienen pruebas dedicadas en `core/ui` y se usan en la galería de previews (`AqDesignSystemGallery`). Retirarlos rompería la compilación de la galería y de sus tests sin aportar beneficio de producto. Se conservan como parte del conjunto de formularios y se documentan como "sin consumo en pantallas de producción" para no afirmar uso que no existe.

### 3. Criterio de aceptación de componentes o patrones nuevos

Un componente o patrón nuevo se implementa solo si cumple TODO:

- **(a) Mejora UX demostrable:** nombra la heurística que mejora (visibilidad de estado, retroalimentación inmediata, prevención de errores, reconocer vs recordar, alcance del pulgar, menos toques, consistencia, jerarquía) y cómo se comprobará en código, pruebas o capturas.
- **(b) Convención:** sigue Polaris (nivel 1, con referencia a su componente o patrón en la instantánea 9.4.2 o documentación pública) o, si Polaris no lo define, Moon (nivel 2, citando la clase real del catálogo verificado de `moon_design` 1.1.0), o es un patrón estándar nativo de los que ni Polaris ni Moon cubren (nivel 3, con descarte escrito de 1 y 2 con evidencia verificable).
- **(c) Consumo real:** tiene uso en al menos una pantalla de producción.
- **(d) Límites duros:** respeta todos los límites duros (no nuevos datos persistidos, red, permisos, telemetría; no toca modelo de seguridad; solo invoca casos de uso/intents existentes).
- **(e) Artefactos:** trae ficha, pruebas, galería, capturas y entrada en el contrato de pantallas.

Lo que no cumpla se descarta con el motivo en el backlog (`11-matriz-gaps-mobile.md` §4). Un componente sin consumo real nunca se crea.

### 4. Política de gestos

- Toda acción por gesto (swipe, pull, pulsación larga, arrastre) expone `customActions` en semántica de Compose como alternativa no gestual (TalkBack/VoiceOver).
- Toda acción por gesto tiene una alternativa no gestual en detalle u hoja (menú, botón, acción de texto).
- Respetar movimiento reducido: sin animaciones decorativas ni bucles de animación.
- Predictive back (Android 14+): el swipe horizontal de filas no debe iniciarse en la zona de borde reservada al gesto de sistema. Documentar el umbral y probarlo en lo posible con Robolectric.

### 5. Reglas de Deshacer y de acciones por lote

- Un patrón «Deshacer» solo reemplaza una confirmación donde hoy NO hay confirmación; nunca donde la política vigente exige confirmar.
- Un swipe o una acción por lote nunca ejecuta una destrucción sin la confirmación vigente.
- Eliminar por lote: aplica la confirmación vigente como confirmación única para el lote; si la política exige confirmar por elemento, no se admite eliminación por lote sin ADR de política.
- Descarte de pendientes sin clasificar: con confirmación. Duplicados: sin confirmación (el original existe). «Quitar contexto»: acción destructiva explícita.
- Feedback honesto: el feedback dice lo que realmente ocurrió (no mostrar «sincronizado» si solo se recargó localmente).

### 6. «COMPLETO» en la matriz de gaps

Cada flujo y patrón se marca COMPLETO solo con:
1. uso en pantalla (consumo real en al menos una superficie del contrato),
2. prueba que cubre el gesto/estado (Robolectric o común),
3. captura clara/oscura a 360 dp (`<superficie>--<estado>--<tema>.png`).

Mientras no exista la captura, el flujo se anota como IMPLEMENTADO EN PANTALLA y no como COMPLETO.

### 7. Mejoras que exceden los límites duros

Si una mejora exigiera un caso de uso nuevo, un cambio de dominio, un cambio de política de confirmación, categorías nuevas de datos persistidos, llamadas de red, permisos de sistema o telemetría, NO se implementa. Se registra como ADR en estado «propuesto» con problema, mejora esperada y coste, y se sigue con la siguiente.

## Consecuencias

- Los componentes móviles de `core/ui` pasan a estar activos y visibles en la experiencia diaria del usuario.
- No se introducen dependencias externas ni se alteran contratos de dominio, persistencia ni flujos funcionales.
- Se mantienen todas las políticas de confirmación para acciones destructivas.
- Los nombres «Polaris» y «Moon» no aparecen en la UI, en strings de producto ni en documentación de producto; solo en documentación de ingeniería.

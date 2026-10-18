# ADR-0015: Referencia visual Stitch (Ant Design Mobile) para la jerarquía financiera

**Estado:** Aceptada · **Fecha:** 2026-10-13 · **Acotada por ADR-0017** (2026-10-16: Stitch solo aporta anatomía financiera y patrones — segmentado, tag tonal, icon badge, meta row, hero QR —; paleta, tipografía y radios los define Ant Design Mobile oficial; la historia de esta decisión se conserva) · **Fuente Stitch sustituida por ADR-0024** (2026-10-18: la referencia Stitch vigente es el proyecto `TDesign Screen Templates`; se conservan los patrones financieros) · **Relaciona:** ADR-0013, ADR-0014, contrato normativo (docs/05-design-system/00-contrato-normativo-diseno.md)

## Contexto

El producto recibió un paquete de referencia visual de alta prioridad
(`stitch_ant_design_mobile_app`: 4 pantallas HTML/Tailwind + DESIGN.md)
que traduce Ant Design Mobile a un organizador financiero: destinos QR,
registro/edición de operaciones, historial y detalle/exhibición de QR.

El sistema ya tenía (ADR-0014) la base tokenizada de la misma referencia.
La referencia Stitch añade patrones que V1.4 no cubría:

- Anatomía de fila financiera: identidad tipada a la izquierda (flecha
  entrante/saliente), contraparte como título, tipo+fecha como subtítulo
  y monto con signo direccional a la derecha.
- Control segmentado para filtros mutuamente excluyentes (Todos · Pagos ·
  Cobros) y para el tipo de actividad del editor.
- Etiquetas tonales de procedencia (Enviado/Recibido/Desconocido) con
  bloque de color del tono.
- Metadatos en filas compactas label/valor en tarjetas de detalle.
- Hero de exhibición del QR (320 dp) con metadatos agrupados debajo.

## Decisión

1. **Adoptar los patrones** anteriores como componentes del design
   system, no como composición por pantalla: `AmSegmentedControl`,
   `AmTonalTag`, `AmIconBadge`, `AmMetaRow` y el slot `leading` tipado de
   `AmListRow`. Sin segundo sistema de diseño paralelo.
2. **Paleta: NO se adopta.** La referencia propone `#1677ff`/`#52c41a`/
   `#ff4d4f` y un lienzo cálido `#fbf9f8`. El esquema vigente
   (`AmPalette`, ADR-0014) ya cumple los mismos roles con pares AA
   verificados por `AmSchemeTest`; cambiar la paleta no aporta y
   invalidaría todos los pines. Los montos y flechas usan los roles
   `success`/`danger` existentes (≥ 4.5:1 sobre `surface`, ya pinneado).
3. **Tipografía Inter: NO se empaqueta.** La referencia usa Inter; el
   producto usa la familia de sistema (ADR-0013: Roboto/San Francisco,
   sin fuentes empaquetadas). Inter añadiría ~1 MB por plataforma,
   gestión de licencias OFL en el distribuible y trabajo de regresión
   tipográfica, sin beneficio verificable en densidad de datos numéricos
   a los tamaños del producto (11–18 sp). Diferencia intencional.
4. **Filtro de tipo del historial:** estado de presentación local
   (`rememberSaveable`) sobre la lista ya visible del VM. No crea
   acciones ni estado de dominio; el significado de los tipos no cambia.
5. **Monto con signo:** presentación (cobro `+` success, pago `−`
   danger). El valor registrado, la validación y los reportes no cambian.
6. **Comprobantes en el editor:** la referencia los muestra en el flujo
   de edición; el VM solo carga los comprobantes al abrir el detalle.
   Mostrarlos en el editor requeriría cargarlos con datos que no están
   garantizados en esa ruta (o ampliar el VM: fuera de alcance visual).
   Diferencia intencional: los comprobantes se gestionan en el detalle,
   como hasta ahora.
7. **TabBar con botón central de escáner de la referencia:** no se
   adopta; la navegación raíz (Inicio · Buscar · Actividades · Contextos)
   es un contrato de producto vigente.

## Consecuencias

- Componentes nuevos verificados por la suite existente: el selector
  segmentado mantiene 48 dp y la selección por semántica
  (`ScreensA11yCoverageTest.s07`); los pares tonales de etiquetas e
  insignias ya estaban pinneados (`AmSchemeTest`).
- Evidencia visual: 4 capturas nuevas (detalle QR claro/oscuro, anatomía
  de fila de actividad claro/oscuro) en el manifiesto ADR-0007. Las
  pantallas con `OperationsViewModel` siguen sin componerse en
  Robolectric (precedente S10): su patrón visual se captura con los
  mismos helpers internos de la pantalla.
- El contrato normativo registra los componentes nuevos (§9), los glifos
  financieros (§7) y esta decisión.

## Revisión 2 — Stitch MCP como agente de diseño (2026-10-13/14)

La fuente de diseño pasa a ser el **proyecto Stitch "Ant Design Mobile
App" consultado por MCP** (16 pantallas móviles + sistema de diseño con
tokens Inter/paleta/espaciados). Cuatro pantallas eran versiones
ACTUALIZADAS nunca implementadas antes; esta pasada las implementó:

- **Compactación (mandato explícito):** separación de superficie visible
  y zona interactiva — secundarios 40 dp visual / 48 táctil, píldoras
  ~32/48 con CONTEO real, entradas 44, acciones de fila ~36/48. Sin
  literal dp fuera de tokens; `verifyControlMinSize` sigue fijando el
  target de 48.
- **Destinos QR - Agenda:** tarjeta de garantía local ("Almacenamiento
  100% local" — cierto) + acciones rápidas (Escanear QR solid /
  Importar imagen tonal → S02 real), pestañas cápsula con conteo del
  dominio, **carrusel horizontal de favoritos reales** ("Destinos
  frecuentes"), lista agrupada con tile QR y contenido decodificado
  como evidencia, banner de privacidad y FAB "Añadir destino" directo
  a S02 (la hoja de 3 opciones del diseño anterior se retira: las
  creaciones viven en sus superficies).
- **Historial de Operaciones:** pestañas cápsula con conteo (Todas /
  Pagos / Cobros) + "Comprobantes sueltos" que abre la bandeja real,
  barra de mes derivada de los datos ("Mayo 2024 · N registros
  locales" + "Respaldo seguro"), **tarjeta por operación** con tile
  direccional, importe COLOREADO con signo (pago rojo / cobro verde —
  el MCP historial sí colorea; revierte el criterio neutral del
  detalle), metadato temporal "fecha · Reg: hh:mm" (createdAt real),
  tags de contexto/sync y acción compacta Compartir.
- **Detalle y Exhibición QR:** tarjeta núcleo con el código REAL
  decodificado (nunca imagen estática), esquinas de encuadre reutilizadas
  del scanner, guía de uso, botón "Pantalla completa" (ruta real
  FullscreenQr/RF-05), metadatos en filas con **copiar contenido al
  portapapeles** (nueva capacidad multiplataforma `rememberAmClipboard`,
  feedback "Copiado"), fecha de alta (createdAt) y dock de acciones
  reales (Registrar actividad — sin preasociación, regla inexistente en
  el dominio; Compartir; Editar metadatos/reemplazar QR) + reaseguro de
  integridad histórica (cierto en el dominio).
- **Registrar:** píldora de requisito mínimo honesta (tipo + momento).
- **Portapapeles:** `rememberAmClipboard()` expect/actual (Android:
  `LocalClipboardManager`; iOS: `UIPasteboard`) — solo escritura.
- **Diferencias documentadas:** banner de imagen de comercio (sin
  dato de imagen en el dominio), "Ver comprobante" por tarjeta del
  historial (el listado no carga comprobantes por operación; el detalle
  sí), píldora "Local" en la barra del detalle (el host de estado
  global ya comunica el modo), preasociación de destino al registrar
  (regla de negocio inexistente), chips de categoría fija en Destinos
  (la categoría es texto libre del dominio).
- **Consultas MCP reales:** `list_projects`, `get_project` (tokens del
  design system), `list_screens`, `get_screen` + descarga de HTML de
  las 4 pantallas prioritarias actualizadas. El ZIP local NO se usó
  como fuente en esta pasada.

## Revisión 1 — fidelidad por pantalla (2026-10-13, paquete ampliado)

El paquete de referencia se amplió a las pantallas reales del producto
(S01–S12); esta pasada adoptó la composición por pantalla, no solo los
patrones:

- **Tipografía**: título de sección 18/600 en color PRIMARIO
  (headline-sm), título de fila 15/500 (label-lg), metadato 13
  (body-sm), importe 26 SemiBold, botón 15/500 (label-lg), tags 11/600
  (label-sm). `AmTypographyTest` y `S02UiPolishTest` repinneados.
- **Formas**: botones 8 dp, tarjetas/diálogos/buscador/marcos 12 dp,
  chips/tags/FAB/píldoras full-pill. Barra inferior 56 dp (h-14).
- **Superficies**: tarjetas con hairline perimetral `#F0F0F0`
  (nueva excepción E-03 del gate; DESIGN.md: "flat white surface
  delineated by a crisp 1px solid #f0f0f0 border"). Sin sombras: la
  propia DESIGN.md prohíbe sombras pesadas y el sistema ya resuelve la
  elevación por tono (los `shadow-sm` del HTML son sutiles y quedan
  cubiertos por el hairline + contraste tonal).
- **Divisores inset a 56 dp** bajo la marca de fila (referencia ml-14).
- **S01**: píldora de reaseguro "Guardado en tu teléfono" (siempre
  cierta), acciones rápidas reales (Escanear QR → S02; Registrar pago →
  S07 vía VM central), encabezado Recientes con conteo y "Ver todo"
  (expande paginación), tarjeta de disponibilidad, y FAB que abre la
  hoja "¿Qué quieres añadir?" con las tres creaciones reales (QR,
  contexto, actividad). La hoja se materializa como `AmDialog` por la
  limitación documentada de `ModalBottomSheet` bajo Robolectric.
- **S07**: píldora inversa "Modo local" + "SQLite local" (ambas ciertas),
  segmentado con glifos, tarjeta de importe con `AmAmountField`
  (26 sp + ⌫), tarjeta de momento (ocurrido editable + registrado
  automático de solo lectura con dato real `createdAt`), opcionales con
  glifos de concepto, contexto como fila con chevron, texto de
  confianza bajo la acción.
- **Detalle de actividad**: hero con píldora de TIPO (pago danger /
  cobro success, con texto), cifra NEUTRA con signo — la referencia NO
  colorea el importe y esta pasada REVIERTe el coloreado de la iteración
  anterior —, contraparte 18/600 y caja temporal (ocurrido/registrado).
- **Divisores y colores no adoptados**: `#0057C2`/`#1677FF` dobles de la
  referencia se consolidan en el acento único `#1668DC` (es literalmente
  el azul del CTA "Guardar operación" de S07 y pasa AA como texto);
  success/danger de la referencia (`#52c41a`/`#ff4d4f`) siguen
  recalibrados por AA (§4.4). Inter sigue sin empaquetarse.
- **Pendientes honestos**: "Al día" del header de S01 requiere un estado
  global de sincronización que Inicio no posee (solo hay estado por
  elemento); los chips por TIPO de S01 requieren un feed mixto
  (contextos+QR+actividades) que no existe en la lista de destinos — se
  muestran los filtros reales (Todo · Favoritos). Los comprobantes en el
  editor siguen en el detalle (§6).

## Alternativas descartadas

- **Copiar el HTML/Tailwind literalmente:** no es una arquitectura
  multiplataforma; se adoptan principios, no píxeles.
- **Sustituir la paleta por la de DESIGN.md:** rompería los pines AA y
  los hashes visuales sin beneficio funcional.
- **Ampliar el VM para el editor con comprobantes:** cambio de flujo
  fuera del alcance visual de esta pasada.

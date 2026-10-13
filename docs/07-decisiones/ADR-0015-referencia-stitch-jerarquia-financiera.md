# ADR-0015: Referencia visual Stitch (Ant Design Mobile) para la jerarquía financiera

**Estado:** Aceptada · **Fecha:** 2026-10-13 · **Relaciona:** ADR-0013, ADR-0014, contrato normativo (docs/05-design-system/00-contrato-normativo-diseno.md)

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

## Alternativas descartadas

- **Copiar el HTML/Tailwind literalmente:** no es una arquitectura
  multiplataforma; se adoptan principios, no píxeles.
- **Sustituir la paleta por la de DESIGN.md:** rompería los pines AA y
  los hashes visuales sin beneficio funcional.
- **Ampliar el VM para el editor con comprobantes:** cambio de flujo
  fuera del alcance visual de esta pasada.

# ADR-0005 — Lenguaje visual Metro dentro de Xauxa

**Estado:** APROBADA — Opción C (Metro completo), con Lucide como set de iconos  
**Aprobación:** 2026-10-05, decisión humana registrada en los prompts de la pasada docs-only (UX Metro + complemento Lucide): "Quiero el espíritu Metro completo. Actualiza la especificación UX/UI congelada." y "Usaremos Lucide como set de iconos."  
**Fecha:** 2026-10-05  
**Área:** UX/UI V1.1 (S01, S06, §11, §12) / Xauxa Design System  
**Decisor requerido:** humano (propietario de producto/UX)

## Contexto

Las capturas actuales de la app se leen como Carbon/Material genérico:
bordes de 1 px por todas partes, botones outline, campos con etiqueta
flotante, lista plana. Sin embargo, Xauxa ya contiene la materia prima del
lenguaje Metro: tokens planos (radio 0, sin sombras, acento `0067B8`,
fondo negro puro en oscuro), `XauxaTile`, `XauxaLiveTile` y
`XauxaTurnstileNav`, hoy infrautilizados en producción.

La especificación congelada V1 (`docs/04-ux/02-especificacion-ux-ui-v1.md`)
protege intención, navegación conceptual, modelo mental, reglas de negocio
y recuperación de errores, pero también fijó una jerarquía visual y un
contrato visual (§12) que el propietario decide ahora alterar:

- **Sí cambia:** jerarquía visual y composición (S01, S06, §11, §12).
- **No cambia:** intención de cada pantalla, navegación conceptual,
  modelo mental (§2), reglas de negocio (§3) ni recuperación de errores
  (§10). §1 ("una pantalla = una intención principal + una acción
  principal") se mantiene intacta.

Xauxa sigue siendo el único sistema de diseño (D-16). Metro se expresa
como una **enmienda del contrato visual de Xauxa**, no como un segundo
sistema ni como copia de assets: queda prohibido usar Segoe UI, Segoe MDL2
u otros assets/fuentes de Microsoft con licencia restringida. Solo se
admiten fuentes e iconos con licencia libre, registrados en
`docs/05-design-system/06-licencias-terceros.md`.

## Opciones

### Opción A — Mantener la expresión actual

Conservar bordes de reposo, botones outline, etiquetas flotantes y lista
plana con el acento único de producto.

- **Impacto:** ninguno en código; la app sigue leyéndose como
  Material/Carbon sin identidad propia.
- **Coste:** cero. **Beneficio:** cero sobre el objetivo declarado.

### Opción B — Solo tiles en la pantalla de inicio

Adoptar rejilla de tiles únicamente en S01 y dejar el resto de pantallas
con el cromo actual.

- **Impacto:** identidad parcial e incoherente: la primera pantalla
  habla Metro y el resto de la app habla Material; dos criterios visuales
  conviviendo.
- **Coste:** bajo-medio; deriva estética alta.

### Opción C — Metro completo como expresión de Xauxa (ELEGIDA)

Adoptar el lenguaje visual Metro completo — tiles, tipografía como
interfaz, cromo mínimo, acentos por contexto, movimiento con propósito —
expresado como enmienda V1.1 del contrato visual Xauxa. Incluye:

- S01 se recompone con til de Metro (búsqueda como primer bloque, rejilla
  de tiles, recientes como filas abiertas sin borde).
- S06 se estructura como pivot QR · Actividades · Comprobantes (refleja
  el modelo mental de §2; no crea taxonomía nueva).
- Barra de aplicación inferior (2–4 acciones con icono + etiqueta y
  menú "…") en todas las pantallas, sustituyendo a los botones
  "Volver/Guardar/Cerrar sesión" sueltos en el cuerpo.
- Paleta de acentos Metro asignable a contextos, derivada de forma
  determinista del identificador del contexto (sin persistir ni cambiar
  el dominio), con tabla de contraste WCAG calculada (§12 de la spec).
- Campos de entrada planos con etiqueta fija pequeña (no flotante).
- Bordes solo funcionales (foco, campo en foco/error, tile seleccionado);
  se elimina el borde de reposo en filas, botones, badges y tiles.
- Movimiento: turnstile entre pantallas (ya existe), tilt al presionar
  tile (≤150 ms), entrada escalonada de tiles (30–50 ms, total ≤300 ms),
  live tile sin bucles; reduced motion desactiva tilt/escalonado/flip.
- **Iconografía: Lucide** (licencia ISC), vía dependencia KMP candidata
  `com.composables:icons-lucide` (wrapper no oficial; verificación
  pendiente en implementación). Se descarta la vía de vector drawables
  sugerida anteriormente. Concesión registrada: Lucide usa terminales
  redondeadas, menos angulosas que el Metro original; se acepta (ver
  Puntos abiertos). Todo icono interactivo lleva etiqueta de texto
  visible, incluida la barra inferior: decisión de accesibilidad y
  desviación consciente de Metro (cuya barra solo mostraba etiquetas al
  expandirse).

No hay panorama horizontal en V1.1.

## Matriz de impacto por pantalla

| Pantalla | Intención | Composición V1.1 |
|---|---|---|
| S01 Inicio | SIN CAMBIO (encontrar o iniciar; Buscar domina) | CAMBIA: título de página, búsqueda como primer bloque, rejilla de tiles, recientes sin borde |
| S02 Añadir | SIN CAMBIO | sin cambio de composición normativa en V1.1 |
| S03 Cámara | SIN CAMBIO | sin cambio (lenguaje del scanner Xauxa se conserva) |
| S04 Buscar | SIN CAMBIO | resultados agrupados por tipo con encabezados de sección (refuerzo, no alteración) |
| S05 Resultados | SIN CAMBIO | idem S04 |
| S06 Contexto | SIN CAMBIO (comprender y continuar) | CAMBIA: pivot de secciones QR · Actividades · Comprobantes |
| S07 Registrar | SIN CAMBIO | sin cambio de intención; acciones pasan a la app bar (defectos abiertos registrados) |
| S08 Seleccionar contexto | SIN CAMBIO | sin cambio normativo |
| S09 Revisar QR | SIN CAMBIO | defecto abierto registrado (QR pequeño a la izquierda); composición pendiente de pasada de implementación |
| S10 Revisar comprobante | SIN CAMBIO | sin cambio normativo |
| S11 Resolver ambigüedad | SIN CAMBIO | sin cambio normativo |
| S12 Importación masiva | SIN CAMBIO | sin cambio normativo |

## Consecuencias y riesgos

- **Deriva código vs spec:** la implementación actual queda
  DESALINEADA con V1.1 en cuanto este ADR entra en vigor. Se declara en
  `docs/09-implementacion/01-estado.md` y se resuelve en una pasada de
  implementación posterior dedicada.
- **Contraste:** cada acento admite un solo color de texto (blanco o
  negro) calculado por script; los acentos cuyo par no alcanza ningún
  umbral quedan excluidos. La tabla calculada vive en la spec §12. Riesgo:
  emerald (`008A00` negro, 4.64) queda cerca del umbral 4.5 — aceptado.
- **Accesibilidad:** tiles con rol de botón, etiqueta visible, estado y
  orden de foco lógico; foco visible sobre el color del tile (el anillo
  `FocusRing` debe mantener contraste sobre acentos claros y oscuros —
  verificar en implementación). Los iconos interactivos siempre llevan
  etiqueta visible (desviación consciente de Metro, a favor de la
  accesibilidad).
- **Reduced motion:** tilt, escalonado y flip desaparecen; solo cambio de
  estado inmediato. La comprensión nunca depende de la animación.
- **Rendimiento:** la entrada escalonada se acota (total ≤300 ms); el
  live tile solo recompone cuando cambia el dato (no hay rotación en
  bucle).
- **Dependencia Lucide (tercero, wrapper no oficial):** versión fijada en
  la pasada de implementación; impacto de tamaño del APK/IPA a medir;
  compatibilidad con Kotlin 2.2.20 y con el target wasmJs (el laboratorio
  corre en wasm) a verificar; obligación de conservar el aviso de
  copyright ISC (y MIT para los glifos derivados de Feather) — ver
  `docs/05-design-system/06-licencias-terceros.md`.
- **Licencias:** se mantiene la prohibición de assets Microsoft
  restringidos (Segoe UI, Segoe MDL2). Archivo (OFL 1.1) y Lucide (ISC)
  quedan registrados en el registro de licencias.
- **Riesgo "disfraz de Windows Phone":** se mitiga manteniendo los
  invariantes Xauxa (radio 0, sin sombras, full-bleed, foco visible,
  targets 44–48 dp) y los colores de estado semánticos separados de los
  acentos.

## Puntos abiertos

1. **Color de contexto manual:** hoy el color se deriva de forma
   determinista del identificador del contexto. Elegir color manualmente
   queda abierto (coherente con ADR-0003, que excluye la edición de
   contextos en V1). Opciones: (a) mantener solo derivado; (b) añadir
   elección de acento al ADR de edición de contextos futuro.
2. **Terminales de los trazos:** Lucide usa extremos redondeados;
   Metro original era más anguloso. Opciones: (a) aceptar Lucide tal
   cual; (b) sustituir en el futuro por un set de terminales cuadradas
   si aparece uno con licencia libre y cobertura KMP.
3. **Conflicto §3/§5 vs V1.1:** los badges de estado ("Sincronizado" etc.)
   ocupan ancho de fila con caja; M9 elimina la caja en reposo y M1
   reduce cromo. §5 exige que el estado se exprese también con texto/
   semántica — se cumple (el texto se mantiene; solo desaparece la caja).
   No se modifica §5; la forma visual del badge queda definida en §12 y
   la implementación la aplica en la pasada siguiente. Sin conflicto
   normativo residual detectado, pero cualquier choque futuro se registra
   aquí.
4. **Orden y tamaño del tile vivo** en S01 (4×2 en rejilla de 4
   columnas en compacto): RESUELTO (2026-10-10). La rejilla deriva el
   alto de SMALL/MEDIUM/WIDE de la unidad calculada (no de `TileUnit`
   fijo) y crece con la fuente; `XauxaTileGridTest`,
   `XauxaTileContractTest` y `XauxaResponsiveLayoutContractTest` cubren
   320/360 dp y font scale 100/150/200 %.
5. **Etiqueta de la app bar en idiomas largos:** la regla de etiqueta
   visible puede desbordar en idiomas de etiquetas largas; se evalúa con
   datos reales en la pasada de implementación (no se afloja la regla
   sin decisión).
6. **Glifos fuera del mapeo mínimo (hallazgo de T6):** "Editar",
   "Compartir" y "Mostrar QR" no tienen glifo asignado en la spec §12;
   esas acciones de la app bar van etiquetadas en texto (la etiqueta
   siempre visible, §11) hasta una decisión de iconografía. Opciones:
   (a) mantener texto; (b) proponer glifos de Lucide (`Pencil`, `Share2`,
   `Maximize2`) para decisión explícita de producto.
7. **Defectos de implementación que la spec no resuelve (T7):** RESUELTOS
   (2026-10-10). Los destinos "Sin nombre" y "QR importado" muestran un
   subtítulo distintivo con origen y fecha; el badge de sincronización
   vive en su línea propia bajo el título (slot `meta` de `XauxaListRow`)
   y no compite por el ancho de la fila. Cubierto por
   `DestinationRowLayoutTest` (320/360 dp, font scale 100/150/200 %) y
   `DestinationSubtitleTest`. No queda defecto abierto de implementación.
8. **Contenido del tile vivo (S01):** RESUELTO (2026-10-10). El tile vivo
   se deriva de los datos SIN filtrar (el más reciente entre el último QR
   y la actividad reciente), no del resultado visible del filtro/búsqueda;
   una sola transición por cambio de dato, sin bucles.
9. **Chips de filtro (T3):** conservan su contorno de selección porque
   son controles con estado (borde funcional de M9), no contenedores.
   Si producto prefiere chips planos al 100 %, es un cambio de diseño a
   decidir.

## Consecuencias si NO se aprueba nada

- La app conserva la lectura Carbon/Material; la decisión humana
  registrada quedaría sin efecto y esta página pasaría a histórico.

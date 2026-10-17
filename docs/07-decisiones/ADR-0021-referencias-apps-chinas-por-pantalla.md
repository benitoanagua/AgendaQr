# ADR-0021 — Referencias de UI/UX de apps chinas de consumo, por pantalla

**Estado:** ACEPTADO — 2026-10-17 (instrucción del propietario)
**Enmienda:** ADR-0020 (concreta su "inspiración" en referencias nombradas por pantalla).
**Suplanta:** ADR-0014 y ADR-0017 (Ant Design Mobile deja de ser referencia; su historia se conserva).
**Relaciona:** ADR-0015 (anatomía financiera), ADR-0018, contrato normativo `docs/05-design-system/00-contrato-normativo-diseno.md` §16, especificación UX/UI §12.

## Contexto

ADR-0020 declaró la identidad de AgendaQr como "inspirada en aplicaciones móviles chinas de consumo" sin nombrar referencias, lo que impide revisar una pantalla contra algo concreto. El propietario decidió prescindir de Ant Design Mobile y usar la UI/UX de apps chinas de consumo como referencia.

## Decisión

1. **Se imitan patrones de UX, no activos.** Estructura de pantalla, jerarquía, densidad, composición de listas y estados. Quedan fuera iconos, ilustraciones, logotipos, tipografías propietarias, textos, colores de marca exactos y composiciones calcadas pantalla a pantalla.
2. **Ant Design Mobile deja de ser referencia** en toda fuente normativa. Los valores geométricos heredados (radios 2/4/6/8/12, escala 17/15/13) permanecen como tokens propios de AgendaQr bajo ADR-0020; no se cambian en bloque.
3. **Referencias por pantalla** (reparto por defecto; el propietario puede reasignarlo):

| Pantalla | Referencia principal | Patrón |
|---|---|---|
| S01 Inicio | Alipay (inicio) y WeChat (búsqueda) | Búsqueda dominante, filtros en píldora, favoritos como carrusel, lista densa agrupada, una acción de creación |
| Detalle de destino / QR | Alipay (código de cobro) | QR centrado sobre lienzo blanco, una acción primaria, metadatos en filas clave-valor |
| S04–S05 Buscar y resultados | WeChat (búsqueda) | Campo compacto, resultados en lista densa |
| S06 / S08 Contexto | WeChat (chats y contactos) | Filas con marca a la izquierda, título + descripción, separador discreto |
| Actividades y su detalle, S09–S12 | Alipay / WeChat Pay (detalle de factura) | Hero con cifra y píldora de tipo, filas etiqueta-valor, insignia de estado con texto |
| S00, S02, S07 Formularios | HyperOS / WeChat (ajustes) | Listas agrupadas por sección, botón primario al pie |
| Navegación raíz | WeChat | Barra inferior de 4 destinos con etiqueta |

4. **Se conserva sin cambio:** navegación y comportamiento de Android/iOS, accesibilidad (texto ≥ 4.5:1, objetivos ≥ 48 dp, escalado 200 %, movimiento reducido), lienzo QR blanco, paleta de ADR-0020, Compose Multiplatform, prefijo `Am*`, Lucide, flujos y copy funcional.
5. **No se añade una cuadrícula de accesos rápidos en Inicio** (patrón típico de superapps): la especificación S01 prohíbe acciones rápidas duplicadas y el hub del botón flotante ya cubre las tres creaciones.

## Auditoría de brecha (código al 2026-10-17)

| Superficie | Implementación verificada | Brecha frente al patrón |
|---|---|---|
| S01 Inicio | Búsqueda como primer bloque, filtros `AmFilterChip`, carrusel de favoritos, `AmListBody` + filas, FAB con hub de tres creaciones (`AmIconBadge`) | Ninguna estructural |
| Detalle de destino | `AmQrPreview` en `AmTappableCard` sobre lienzo blanco, dock con "Mostrar QR" primaria y "Compartir" secundaria, `AmCard` con `AmMetaRow` | Ninguna estructural |
| S04–S05 Buscar y resultados | `AmSearchField` (48 dp mínimo, desviación documentada) como cuerpo, resultados agrupados por tipo con `AmSectionHeader` + `AmListBody`/`AmListRow`, estados de vacío, carga y error | Filas solo de texto: sin marca de tipo a la izquierda ni metadato a la derecha; el dominio de búsqueda no expone fecha |
| S06 / S08 Contexto | Filas con marca tonal a la izquierda, título y descripción, separador discreto | Ninguna estructural |
| Actividades y su detalle | Hero con píldora de tipo y cifra neutra, filas etiqueta-valor, `AmListRow` con `endMeta` | Ninguna estructural en Actividades; en Inicio no aplica (la fila lleva la acción de favorito en `trailing`) |
| S09–S12 Importación | Resumen en `AmCard` con filas etiqueta-valor (`AmMetaRow`) de reconocidos, duplicados y pendientes; `PendingItemCard` por elemento con insignia de estado con texto; `ImportReviewScreen` con vista previa y evidencia decodificada en tarjeta | Ninguna estructural |
| S00 Acceso local | Título y descripción, campos `AmTextField` en columna, botón primario dentro del scroll, bloque de demostración opcional | Campos sin agrupar en tarjetas de ajustes; el botón primario no queda fijo al pie |
| S02 Añadir | Texto de intención y controles de fuente agrupados por rótulo de sección (archivos y cámara) | Ninguna: los controles son botones, no filas, así que no usan `AmListBody` (deliberado) |
| S07 Editor de actividad | Selector segmentado de tipo, `AmCard` de importe y moneda, grupos en `AmListBody` con rótulo de sección, botón primario fijo al pie | Ninguna estructural |
| Navegación raíz | `AmNavBar` de 56 dp, color + etiqueta, sin píldora | Ninguna |
| Formularios | `AmTextField` con etiqueta flotante persistente y guía hairline | Deliberada: la etiqueta persistente es un requisito de accesibilidad |

### Brechas priorizadas

Prioridad por relación impacto/esfuerzo. El riesgo se mide sobre las capturas existentes y sobre la accesibilidad (§10).

| ID | Brecha | Superficie | Impacto | Esfuerzo | Riesgo |
|---|---|---|---|---|---|
| G1 | Botón primario fijo al pie (`bottomAction`) en lugar de dentro del scroll | S00 | Alto | Bajo | Alto en capturas (login-* y local-*); neutro en accesibilidad |
| G2 | Estado del lote como insignia con texto (`AmStatusPill`/`AmTonalTag`) en vez de icono + texto suelto | S09–S12 | Alto | Medio | Alto en capturas (import-batch-*); refuerza la señal no dependiente del color |
| G3 | Resumen del lote como filas etiqueta-valor (`AmMetaRow`) para reconocidos, duplicados y pendientes | S09–S12 | Medio | Medio | Alto en capturas (import-batch-*) |
| G4 | Campos agrupados en tarjetas de ajustes (`AmListBody` + `AmSectionHeader`) | S00 | Medio | Medio | Alto en capturas (local-*); neutro en accesibilidad |
| G5 | Evidencia decodificada como fila etiqueta-valor o tarjeta en vez de texto suelto | S09 | Bajo | Bajo | Medio en capturas (s09-review-*) |
| G6 | `endMeta` con fecha en resultados de actividad y comprobante | S04–S05 | Medio | Alto (requiere campo de fecha en el dominio de búsqueda) | Medio en capturas (s04-search-*); neutro en accesibilidad |
| G7 | Encabezados de sección para las fuentes de importación | S02 | Bajo | Bajo | Medio en capturas (s02-add-*) |
| G8 | Encabezados de sección para los grupos del editor | S07 | Bajo | Bajo | Nulo (el editor no tiene captura directa) |
| G9 | Marca de tipo a la izquierda en cada resultado | S04–S05 | Bajo | Bajo | Medio en capturas (s04-search-*); se solapa con el encabezado de sección |

Ninguna de estas brechas se implementa sin decisión del propietario. Las que cambian capturas exigen regenerarlas y revisarlas en local (ADR-0007, ADR-0020 §8) antes de darse por válidas. La brecha del metadato a la derecha en `AmListRow` se cerró con el parámetro `endMeta`, aplicado a la fecha real de Actividades y de las actividades de un contexto.

### Brechas implementadas

- **G8** — Encabezados de sección en los grupos del editor (S07): `Importe y moneda`, `Fecha y hora`, `Persona o entidad` y `Contexto`, con el rótulo pegado a su cuerpo. Commit: «Agrega encabezados de sección a los grupos del editor».
- **G7** — Encabezados de sección para las fuentes de importación (S02): `Desde archivos` y `Desde la cámara`. Commit: «Agrega encabezados de sección a las fuentes de importación».
- **G5** — Evidencia decodificada (S09) como tarjeta del sistema con etiqueta `Contenido` y valor, en lugar de texto suelto. Commit: «Presenta la evidencia decodificada como tarjeta en la revisión».
- **G3** — Resumen del lote (S09–S12) como filas etiqueta-valor `AmMetaRow` (reconocidos, duplicados, pendientes) con su conteo a la derecha. Commit: «Convierte el resumen del lote en filas etiqueta-valor».
- **G2** — Estado de cada elemento pendiente (S12) como insignia con texto `AmStatusPill` (`Posible duplicado` / `Pendiente de revisión`), con glifo y tono de acompañamiento; el texto comunica. Commit: «Expresa el estado de cada pendiente como insignia con texto».

## Consecuencias

- Contrato normativo versión 3.1 con la tabla de referencias (§16); especificación UX/UI V1.11.
- ADR-0014 y ADR-0017 pasan a SUPLANTADOS; README y arquitectura dejan de citar a Ant Design Mobile como autoridad.
- Los comentarios de código que atribuían valores a Ant Design Mobile se reescriben (sin cambio de comportamiento).

## Riesgos

- **Parecido excesivo con una app concreta:** se mitiga con la regla 1; la revisión de capturas comprueba que ninguna pantalla replique una composición o un activo protegido.
- **Referencias no contrastadas con capturas actuales:** la tabla describe patrones conocidos de esas apps; conviene validarla con capturas antes de rediseñar una pantalla.
- **Validación visual:** ningún cambio visual se da por válido sin capturas claro/oscuro revisadas en local.

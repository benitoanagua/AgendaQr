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
| Navegación raíz | `AmNavBar` de 56 dp, color + etiqueta, sin píldora | Ninguna |
| Detalle de actividad | Hero con píldora de tipo y cifra neutra, filas etiqueta-valor | Ninguna estructural (revisión de comentarios y estructura, no de capturas) |
| Filas de lista | `AmListRow`: título, subtítulo, meta bajo el subtítulo, acciones finales o chevron | Sin metadato alineado a la derecha (fecha o estado en la misma línea del título), habitual en listas de chats y facturas |
| Formularios | `AmTextField` con etiqueta flotante persistente y guía hairline | Deliberada: la etiqueta persistente es un requisito de accesibilidad |

No auditado a fondo: Buscar, Importación (S09–S12) y S00. La única brecha abierta (metadato a la derecha en `AmListRow`) cambia capturas, por lo que exige regenerar y revisar los hashes visuales en local (ADR-0007, ADR-0020 §8) antes de implementarse.

## Consecuencias

- Contrato normativo versión 3.1 con la tabla de referencias (§16); especificación UX/UI V1.11.
- ADR-0014 y ADR-0017 pasan a SUPLANTADOS; README y arquitectura dejan de citar a Ant Design Mobile como autoridad.
- Los comentarios de código que atribuían valores a Ant Design Mobile se reescriben (sin cambio de comportamiento).

## Riesgos

- **Parecido excesivo con una app concreta:** se mitiga con la regla 1; la revisión de capturas comprueba que ninguna pantalla replique una composición o un activo protegido.
- **Referencias no contrastadas con capturas actuales:** la tabla describe patrones conocidos de esas apps; conviene validarla con capturas antes de rediseñar una pantalla.
- **Validación visual:** ningún cambio visual se da por válido sin capturas claro/oscuro revisadas en local.

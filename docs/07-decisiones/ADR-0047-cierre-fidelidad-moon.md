# ADR-0047 — Cierre de fidelidad Moon: catálogo de referencia, auditoría y guardarraíl `verifyMoonFidelity`

**Estado:** ACEPTADO — 2026-11-24  
**Relacionado:** ADR-0044, ADR-0046, contrato Moon §E.

## 1. Contexto

El ciclo de fidelidad Moon reescribió la práctica totalidad de `core/ui`
contra la fuente `moon_design` 1.1.0 (widgets, themes y sizes en
`lib/src/widgets/**` y `lib/src/theme/**`). Al cierre quedaban tres huecos de
gobernanza:

- No existía un documento de referencia con la especificación Moon por
  widget (API pública, variantes, tamaños, estados), solo volcados de trabajo.
- No existía la matriz de auditoría exigida por el contrato de fidelidad.
- El gate `verifyMoonFidelity` previsto no existía: las invariantes de
  literales/legacy ya las cubría `verifyDesignSystemCompliance`, pero no había
  una tarea con ese nombre ni fixtures que la ejercitaran.
- La galería no cubría siete componentes del catálogo (`AqTable`,
  `AqProgressPin`, `AqLinearLoader`, `AqDrawer`, `AqDropdown`, `AqAuthCode`,
  `AqTextInputGroup`).

## 2. Decisión

1. **`docs/05-design-system/moon-catalogo-referencia.md`**: catálogo de
   referencia (no normativo) con la API pública y la especificación de cada
   widget Moon, organizado por familia. Es la fuente de consulta para futuras
   auditorías; la fuente canónica sigue siendo el paquete `moon_design`.
2. **`docs/05-design-system/auditoria-fidelidad-moon.md`**: matriz cerrada
   con 0 desviaciones P0/P1/P2, una fila por widget Moon y por verificación
   transversal (literales, legacy, táctil, claro/oscuro, galería, tests).
3. **`verifyMoonFidelity`** en `build-logic` (`agendaqr.design-system.gradle.kts`):
   tarea agregada a `verifyAgendaQrArchitecture` que ejecuta las mismas
   invariantes del gate de diseño (`kotlinDesignViolations` sobre todos los
   fuentes de producción, duplicados `Aq*`, completitud del catálogo, símbolos
   prohibidos, registro de excepciones) y falla si el catálogo o las docs de
   referencia desaparecen. Incluye fixtures negativos/positivos en
   `verifyMoonFidelityFixtures` (mismo patrón que `verifyDesignSystemFixtures`).
4. **Galería**: se añaden previews de los siete componentes que faltaban
   (`AqTable`, `AqLinearLoader`, `AqLinearProgress` con `showPin`,
   `AqDrawer`, `AqDropdown`, `AqAuthCode`, `AqTextInputGroup`).
5. **Toast**: `AqMotion.ToastDurationMs` = 3 000 ms, el `displayDuration` por
   defecto de `MoonToast` (`toast.dart`); se corrige la nota de catálogo que
   declaraba 4 000 ms como decisión de producto.
6. **Selección en Compose**: `AqSegmentedControl`, `AqTabBar` y
   `AqBottomNavigation` usan `clickable` + `semantics { selected; contentDescription }`
   en lugar de `Modifier.selectable`, porque `selectable` no disparaba el
   callback de forma fiable bajo la ruta de eventos de Compose usada en tests
   y producción. La semántica de selección (`selected`, `Role`) se conserva.
7. **`AqCarousel`** se reescribe a la especificación Moon: `itemExtent`,
   `gap` (defecto `AqLayout.CarouselGap` = `sizes.x2s`), `isCentered`,
   `loop`, `autoPlay`, `autoPlayDelay`, `onIndexChanged` y eje vertical;
   implementado con `LazyRow`/`LazyColumn` + `rememberSnapFlingBehavior`.

## 3. Consecuencias

- El gate de fidelidad tiene nombre propio y forma parte de
  `verifyAgendaQrArchitecture`; cualquier literal visual fuera de la capa de
  tokens, símbolo legacy o componente del catálogo sin `Aq*` exportado rompe
  el build.
- Las dos matrices documentales conviven con el contrato: la referencia
  describe Moon, la auditoría registra el cumplimiento; ninguna sustituye a
  `13-catalogo-moon.md` como inventario normativo.
- Los callers de `AqCarousel` migran de `itemWidth` a `itemExtent`/`gap`
  (`DestinationsScreen`, galería).

## 4. Alternativas consideradas

- Mantener solo `verifyDesignSystemCompliance` sin alias `verifyMoonFidelity`:
  descartado, el contrato de fidelidad exige un gate con ese nombre y sus
  fixtures.
- Conservar `selectable` y arreglar los tests: descartado, el fallo era de
  entrega del callback, no del test; `clickable` + semantics es el patrón ya
  usado por `AqChip` y preserva accesibilidad.
- Conservar 4 000 ms de toast por decisión de producto: descartado, Moon es
  la autoridad UI/UX y su `displayDuration` por defecto es 3 s; el producto
  no tenía requisito funcional de duración.

## 5. Atribución

Especificaciones de `moon_design` 1.1.0 (MIT © Yolo Group). Atribución en
`docs/05-design-system/06-licencias-terceros.md`.

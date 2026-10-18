# ADR-0012 — UI convencional sobre Material 3, idéntica en Android e iOS

**Estado:** REEMPLAZADA por ADR-0013 en la autoría del sistema visual (AgendaMobile, 2026-10-12) — la DIRECCIÓN de esta decisión se conserva íntegra: UI convencional sobre Material 3, idéntica en Android e iOS, tipografía de sistema, sin tiles. La marca de autoridad pasa de Xauxa a AgendaMobile.
**Aprobación original:** decisión humana registrada en el prompt de la pasada de migración (2026-10-11): "una UI convencional y poco disruptiva, basada en componentes estándar de Material 3, con el MISMO diseño en Android e iOS. La marca queda en el color de acento y poco más. Las reglas de accesibilidad se conservan íntegras."  
**Aprobación explícita de enmienda:** el mismo prompt aprueba "enmendar la especificación congelada (docs/04-ux/02-especificacion-ux-ui-v1.md) y revertir parcialmente ADR-0005"; esa aprobación se registra aquí.  
**Fecha:** 2026-10-11  
**Área:** UX/UI V1.2 / Xauxa Design System / core:ui, feature:destinations:presentation, androidApp  
**Decisor requerido:** humano (propietario de producto/UX) — dado  
**Parámetro de producto:** `TILES_EN_INICIO = false` (Inicio pasa a ser lista convencional; no se conservan tiles en S01)

## Contexto

La UI actual expresa el lenguaje "Metro" sobre Xauxa (ADR-0005, ADR-0011):
tiles planos, radio 0, título de página en Archivo Light de 40 sp, barra de
aplicación inferior en cada pantalla, ripple desactivado con overlay de
pulsación propio, turnstile propio entre pantallas, campos de relleno plano
sin borde en reposo y componentes propios paralelos a Material 3.

Esa expresión genera fricción sostenida con el ecosistema:

- **Con Compose:** cada componente Xauxa reimplementa mecánicas que
  Material 3 ya resuelve (ripple, foco, semántica de toggle, estados de
  chip, listas). El equipo mantiene dos capas (la propia y la de M3
  debajo), y el gate `verifyDesignSystemCompliance` existe en gran parte
  para blindar esa duplicación.
- **Con las convenciones de plataforma:** flecha atrás solo en iOS,
  etiquetas siempre visibles en la barra inferior, radio 0, sin ripple:
  cada una es una desviación que el usuario de Android/iOS debe
  reaprender, y el mantenimiento de capturas/hashes/tests de layout
  (D1–D6 de pulido) demuestra el coste continuo de sostenerla.

## Decisión

1. **Material 3 estándar como lenguaje visual**, no como infraestructura
   oculta: los componentes Xauxa pasan a ser envoltorios finos del
   componente M3 equivalente (Button, OutlinedButton, TextButton,
   OutlinedTextField, ListItem, AlertDialog, ModalBottomSheet,
   NavigationBar, TopAppBar, Scaffold, FilterChip, TabRow, chips e
   indicadores de progreso), con color, forma y tipografía del tema.
2. **Mismo diseño en Android e iOS.** Sin bifurcación visual por
   plataforma: misma flecha atrás (arriba a la izquierda), misma
   NavigationBar inferior en las superficies raíz, mismos componentes.
   Las únicas diferencias admitidas son las de adquisición nativa
   (selectores, share) y las convenciones obligadas del sistema.
3. **La marca queda en el acento** `0067B8` y poco más: esquemas claro y
   oscuro conservados (XauxaSchemeTest), lienzo del QR claro en ambos
   temas, Lucide como único set de iconos.
4. **Chrome convencional:** `TopAppBar` pequeña con título a la izquierda
   en las superficies; `NavigationBar` inferior solo en las cuatro
   superficies raíz (Inicio, Buscar, Actividades, Contextos); acción
   principal contextual en el cuerpo o fija abajo solo cuando hay una
   acción real de guardado. Una pantalla cuya barra solo tiene "Volver"
   no lleva barra inferior.
5. **Formas y movimiento:** conjunto de formas explícito en tokens
   (4/8/12/16/28 dp), ripple/indicación estándar de M3 restaurados,
   transición corta fundido+desplazamiento en lugar de turnstile,
   reduced motion = cambio inmediato (duración 0).
6. **Predictive back:** `PredictiveBackHandler` (disponible en
   commonMain en `ui-backhandler` 1.8.2) con progreso para la
   animación, manteniendo el orden de decisión `systemBackAction`
   (flujo interno → pop de superficie → sistema). En iOS se comporta
   como back simple.
7. **Adaptación de ancho:** `WindowSizeClass`
   (`material3-window-size-class` 1.8.2, multiplataforma) sustituye los
   breakpoints propios; ancho expandido limita el contenido con
   `ContentMaxWidth` y usa `NavigationRail`.

## Qué reemplaza de ADR-0005/ADR-0011 y qué conserva

**Reemplazado** (reversión parcial de ADR-0005, enmienda de ADR-0011):

- Tiles (rejilla, tile vivo, entrada escalonada, tilt) — con
  `TILES_EN_INICIO = false` desaparecen de S01 y del sistema.
- Título de página en Archivo Light 40 sp → título M3 estándar
  (titleLarge) y familia de sistema en todos los estilos; Archivo se
  retira del empaquetado.
- Barra de aplicación inferior en cada pantalla → TopAppBar +
  NavigationBar solo en raíces.
- Radio 0 forzado → formas de tokens 4/8/12/16/28 dp.
- Ripple desactivado + overlay propio → indicación estándar de M3.
- Turnstile → transición estándar corta.
- Campo plano sin borde en reposo → `OutlinedTextField` con borde
  `borderControl` (≥3:1).
- Botón/acción sin caja → jerarquía Button/OutlinedButton/TextButton.
- Bordes "solo funcionales" y marcadores laterales → separadores y
  componentes M3 convencionales.
- `XauxaDotProgress` → indicadores de progreso de M3.

**Conservado:**

- Paleta y contraste: acento `0067B8`, esquemas claro/oscuro, tonos de
  estado con par propio, `brandText`, `borderControl`, lienzo del QR
  claro en ambos temas (XauxaSchemeTest, XauxaFocusRingContrastTest).
- Paleta de acentos por contexto derivada determinísticamente (M4/M5),
  sin ampliar.
- Accesibilidad íntegra: contraste (texto ≥4.5:1, componentes ≥3:1),
  targets ≥48 dp, escalado de fuente hasta 200 % sin truncar contenido
  esencial, reduced motion, foco visible, liveRegion, significado nunca
  solo por color, etiquetas accesibles.
- `AppBackStack`, `AppBackStackSaver`, `systemBackAction` y el orden de
  Back (flujo interno → pop de superficie → sistema).
- Iconografía Lucide vía `XauxaIcon`; guía de voz (ADR-0008).
- Lienzo del scanner (S03 congelado) con su encuadre de marca (excepción
  E-03).
- Tokens semánticos como única capa editable (`XauxaTokens.kt`) y el
  gate que prohíbe literales crudos fuera de ella.

## Consecuencias

- **Spec:** V1.1 se enmienda a **V1.2**: §12 reescrito como diseño
  convencional, M1–M13 eliminados, S01/S02/S12 recompuestos (historial
  de enmiendas en la propia spec).
- **Pruebas:** desaparecen las que fijaban decisiones Metro
  (tiles/turnstile/pivot/display ligero/radio 0/app bar inferior:
  `XauxaTileContractTest`, `XauxaTileGridTest`,
  `XauxaTurnstileMotionTest`, `XauxaPivotLayoutTest`,
  `XauxaUiPolishDefectsTest` en su forma Metro, `LatestTileDatumTest`,
  `S02UiPolishTest`, partes de `Round2FontScaleTest`/
  `XauxaResponsiveLayoutContractTest`). Se conservan TODAS las de
  accesibilidad y contraste y se añaden las del nuevo contrato
  (NavigationBar, AppStatusHost, Snackbar, campos, IconToggleButton,
  escalado 200 %).
- **Regresión visual:** los 8 hashes Roborazzi cambian; se regeneran con
  `recordRoborazziDebug` + `recordVisualHashes` y se revisan los PNG
  localmente.
- **Gate:** `verifyDesignSystemCompliance` se actualiza al nuevo
  contrato (formas desde tokens; sigue prohibiendo literales crudos
  fuera de la capa de tokens). No se desactiva. La tabla de excepciones
  del contrato §12 y el registro del gate se actualizan juntas.
- **API:** los nombres `Xauxa*` se conservan donde el componente sigue
  existiendo como envoltorio; lo que desaparece se elimina (nada queda
  deprecated sin usos).
- **Licencias:** Archivo (OFL 1.1) se retira del empaquetado y del
  registro; Lucide (ISC) se conserva.
- **iOS:** no existe Share Extension (`observeSharedQrImports` devuelve
  vacío); el bloque "Desde otra app" queda gated por capability
  (`sharedImportsSupported`) y la extensión queda como pendiente
  documentado.

## Alternativas descartadas

- **Mantener Metro (ADR-0005 íntegro):** el coste de sostener el
  paralelismo con M3 y las desviaciones de plataforma es constante y
  el producto no lo requiere.
- **Metro solo en S01 (opción B de ADR-0005):** dos criterios visuales
  conviviendo; expresamente evitado.
- **Material 3 "pelado" sin capa Xauxa:** perdería la paleta/contraste
  verificados y el control de accesibilidad; la capa de tokens y
  envoltorios se conserva por eso.

## Criterio de retirada

Este ADR queda supersedido si el producto aprueba otro lenguaje visual
(nuevo ADR con alternativas y evidencia).

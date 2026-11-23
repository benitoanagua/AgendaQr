# ADR-0039 — Moon Design rige el UI/UX; el sistema visual anterior solo aporta color, tipografía e iconos

> **Enmendado por [ADR-0044](ADR-0044-moon-unico-sistema-de-diseno.md):** la excepción de color, tipografía e iconos queda retirada; Moon rige todo.

**Estado:** ACEPTADO — 2026-11-21
**Reemplaza parcialmente a:** ADR-0025, ADR-0028, ADR-0029 y ADR-0038 (ver nota de enmienda en cada uno).
**Relaciona:** ADR-0040 (adopción íntegra del catálogo), contrato normativo de diseño (`05-design-system/00-contrato-normativo-diseno.md`), matriz de gaps (`05-design-system/11-matriz-gaps-mobile.md`) y catálogo (`05-design-system/13-catalogo-moon.md`).

## 1. Contexto

ADR-0038 dividió la autoridad por dimensión (Moon para la interacción, el sistema visual anterior para la apariencia) pero dejó al sistema visual anterior con radios, espaciado, elevación y forma, y prohibió importar squircle y sombras de Moon. En la práctica el producto seguía leyéndose como un clon de un sistema de administración web con parches móviles. El sistema visual anterior no está pensado para móvil; Moon Design for Flutter sí lo está.

## 2. Decisión

**Moon Design (`moon_design` 1.1.0) define todo el UI/UX**: componentes, anatomía, variantes, tamaños, espaciado, radios y forma (squircle), bordes, elevación y sombras, gestos, movimiento, orden de acciones, feedback, vacíos, carga y error.

**Solo tres dimensiones vienen del residual del sistema visual anterior** (los tokens del sistema visual anterior, los iconos del sistema visual anterior):

| Dimensión | Fuente | Capa de código |
|---|---|---|
| Colores (paleta, roles semánticos, tonos, claro/oscuro, contraste AA) | El sistema visual anterior | `AqPalette`, `AqColorScheme`, `AqColor` |
| Tipografía (Inter y roles tipográficos) | El sistema visual anterior | `AqType`, `AqTextRoles`, `AqTextStyles` |
| Iconos | El sistema visual anterior | `MoonIconVectors`, `AqIcon` |

Todo lo demás —incluidos los tamaños, radios, bordes, sombras, opacidades de estado y curvas de transición— se transcribe de Moon a la capa de tokens (`AqSize`, `AqCorner`, `AqStroke`, `AqStateAlpha`, `AqShadow`, `AqMotion.Easings.InOutCubic`, `AqSquircleShape`).

**Solo se usan componentes de Moon.** Toda pieza de interfaz es un puerto de un componente del catálogo Moon o una composición hecha únicamente con piezas Moon. Se elimina la cadena «Moon → nativo».

No hay dependencia de Flutter ni de Moon en tiempo de ejecución: se porta a Compose. La API pública sigue siendo `Aq*`.

## 3. Lo que Moon no trae

Moon no incluye bottom navigation, FAB, search bar, swipe actions, pull-to-refresh, date/time picker, skeleton, empty state ni list cell. Se resuelven así:

1. **Composición con piezas Moon** siempre que sea posible (barra inferior = `MoonTabBar`/`MoonPillTab`; FAB y acción fija = `MoonButton`; buscador = `MoonTextInput`; celda de lista = `MoonMenuItem`; empty state = avatar/icono el sistema visual anterior + texto + `MoonButton`; skeleton = loaders y formas Moon; pickers = `MoonModal` o `MoonBottomSheet` con controles Moon, respetando D-07).
2. **Solo la mecánica gestual** (deslizar para revelar, tirar para refrescar, fling del carrusel) se implementa con APIs de Compose; su aspecto (acciones reveladas, indicador, umbrales visuales, curvas) usa botones, loaders, formas, sombras y movimiento Moon.
3. Cada caso se registra en el catálogo con su justificación. No se inventa estilo propio.

## 4. Reglas

1. Ningún valor visual (color, dp, sp, radio, duración, elevación, alfa) se declara fuera de `AqTokens.kt`.
2. Los valores de Moon se transcriben de la fuente (`moon_design` 1.1.0, `moon_tokens` 0.0.6) citando el archivo de origen; no se estiman.
3. El **color de las sombras** sale de la paleta (negro con el alfa de Moon); la geometría, de Moon.
4. Los controles Moon menores de 48 dp (`xs`, `sm`, algunos `md`) conservan su tamaño visual y amplían el **área táctil** a 48 dp (D-02, `verifyControlMinSize`).
5. Las excepciones de bordes (`E-xx`) y de sombras del gate de diseño se amplían de forma explícita y registrada, no por omisión.
6. Cada componente nuevo tiene ficha en `docs/05-design-system/componentes/<nombre>.md` y entrada en `AqDesignSystemGallery`.

## 5. Invariantes que se mantienen

- «Comprobante recibido» **no** significa «dinero verificado»: nada de «Verificado» ni «Pagado» (ADR-0025, D-14).
- Objetivos táctiles ≥ 48 dp; reducción de movimiento respetada; contraste AA (4,5:1 texto, 3:1 no textual).
- QR negro sobre blanco con zona de silencio; «Mostrar QR» en ≤ 2 toques (X-03).
- Copy, flujos, reglas de negocio, local-first y guía de voz del contrato UX/UI V1 (`docs/04-ux`) permanecen congelados (D-17). Si un patrón Moon contradice un invariante, gana el invariante y se documenta.

## 6. Consecuencias

- La matriz de gaps se reescribe con una única columna «Componente Moon» y, donde Moon no trae el patrón, «Composición/gesto».
- El gate `agendaqr.design-system` (bordes, sombras, formas) se actualiza al nuevo origen de forma, con excepciones registradas.
- iOS sigue sin validación en este entorno; se registra como no verificado.

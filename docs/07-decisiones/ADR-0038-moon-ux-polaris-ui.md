# ADR-0038 — Moon Design rige la UX móvil; Polaris rige la UI

**Estado:** ACEPTADO — 2026-11-20
**Relaciona:** ADR-0025 (Polaris como autoridad visual), ADR-0028 (Moon como referencia de patrones móviles), ADR-0029 (cadena de precedencia), contrato normativo de diseño (`05-design-system/00-contrato-normativo-diseno.md`) y matriz de gaps (`05-design-system/11-matriz-gaps-mobile.md`).

## 1. Contexto y motivación

El sistema visual de AgendaQr es una reimplementación en Kotlin/Compose de Shopify Polaris (`@shopify/polaris-tokens@9.4.2`, iconografía `@shopify/polaris-icons@9.3.1`). Polaris es un sistema web/admin: define tokens, jerarquía y componentes de escritorio, pero deja sin especificar varios patrones críticos de interacción en dispositivos móviles (bottom sheet, toast, acordeón, auth code, input con label flotante, indicadores de carga, estados vacíos, etc.).

ADR-0028 ya adoptó Moon Design (`moon_design` 1.1.0, solo referencia anatómica y gestual; sin dependencia Flutter ni runtime Moon) para cubrir esos gaps, y ADR-0029 fijó la cadena Polaris → Moon → nativo. En la práctica, esa cadena dejaba a Moon casi sin voz: como Polaris es la «única autoridad visual», muchos patronos móviles se resolvieron directamente como nativos sin consultar la anatomía y los estados de Moon.

Esta decisión formaliza una autoridad dividida por **dimensión**, no un solo escalafón ciego:

| Dimensión | Autoridad | Qué se toma |
|---|---|---|
| **UX / interacción** (anatomía del componente, estados, tamaños táctiles, gestos, orden de acciones, feedback, vacíos, carga, error) | **Moon** (nivel 1 para patrones móviles) | Comportamiento y estructura. Ej.: `MoonAvatar` con fallback, `MoonChip`/`MoonTag`, `MoonBottomSheet`, `MoonToast`, `MoonAccordion`, `MoonAuthCode`, `MoonTextInput` (label flotante), `MoonButton` (estados loading/disabled/pressed), `MoonCircularLoader`. |
| **UI / apariencia** (color, tipografía Inter, radios, elevación, iconografía, espaciado, tonos semánticos, modo oscuro) | **Polaris** (siempre) | 100 % tokens Polaris. **Prohibido** importar paleta, sombras, squircle o tipografía de Moon. |
| Lo que ni Moon ni Polaris cubren (bottom nav, FAB, swipe, pull-to-refresh, date picker, skeleton) | **Nativo** (M3 / HIG) | Estilizado con tokens Polaris (como hoy). |

## 2. Decisión

Toda decisión de interfaz y experiencia de usuario móvil se resuelve por dimensión:

1. **UX / interacción:** Moon Design for Flutter (`moon_design` 1.1.0) es la referencia de nivel 1 para anatomía, estados, tamaños táctiles, gestos y orden de acciones. El estilo se reemplaza al 100 % por tokens Polaris; no hay dependencia de Moon ni de Flutter, y la API pública sigue siendo `Aq*`.
2. **UI / apariencia:** Polaris es la autoridad exclusiva y siempre gana: color, tipografía (Inter), radios, elevación, iconografía, espaciado, tonos semánticos y modo oscuro. Se prohíbe importar paleta, sombras, squircle o tipografía de Moon.
3. **Nativo:** solo lo que ni Moon ni Polaris cubren (bottom navigation, FAB, swipe actions, pull-to-refresh, pickers de fecha/hora, skeleton, empty state, list cell) se resuelve como convención nativa Android (Material 3) / iOS (HIG), estilizado al 100 % con tokens Polaris.

## 3. Reglas

1. Ningún valor visual (color, dp, sp, radio, duración, elevación) se declara fuera de la capa de tokens de `core/ui`.
2. La navegación raíz sigue siendo nativa (barra inferior, gestos) por ADR-0029; Polaris se consulta primero y no define navegación táctil móvil, Moon tampoco la trae, por tanto nivel 3 nativo estilizado con tokens Polaris.
3. Los componentes que porten un patrón Moon se implementan en `core/ui` como `Aq*` y se consumen en `feature/.../presentation`.
4. Cada componente nuevo cuenta con ficha de diseño en `docs/05-design-system/componentes/<nombre>.md` y entrada en `AqDesignSystemGallery`.
5. Las desviaciones o extensiones se documentan en el contrato normativo y en la matriz de gaps.

## 4. Invariantes que se mantienen

- «Comprobante recibido» **no** significa «dinero verificado»: no se usan etiquetas como «Verificado» ni «Pagado» (ADR-0025, D-14).
- Objetivos táctiles ≥ 48 dp (D-02).
- Reducción de movimiento respetada («reducir movimiento» elimina la transición continua).
- QR negro sobre blanco con zona de silencio; acceso a «Mostrar QR» en ≤ 2 toques (X-03).
- Copy, flujos, reglas de negocio, local-first y voice guide del contrato UX/UI V1 (`docs/04-ux`) permanecen congelados (D-17); un patrón Moon solo se aplica si no contradice el contrato funcional.

## 5. Consecuencias

- La matriz de gaps (`11-matriz-gaps-mobile.md` §1) y el contrato normativo (§6) se actualizan para reflejar la autoridad dividida por dimensión.
- ADR-0025, ADR-0028 y ADR-0029 siguen vigentes; ADR-0038 las refina definiendo Moon como nivel 1 de UX/interacción móvil sin ceder la autoridad visual de Polaris.
- iOS sigue sin validación en este entorno (sin macOS/Xcode); se registra como no verificado.

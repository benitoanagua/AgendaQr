# ADR-0011 — Contrato visual Xauxa: Metro sin Carbon, radio conmutable y calidad de tiles

**Estado:** APROBADA  
**Fecha:** 2026-10-10  
**Ámbito:** Xauxa Design System (core:ui); Android e iOS  

## Contexto

El contrato normativo (`docs/05-design-system/00-xauxa-contrato-normativo.md`)
fija la autoridad visual pero carecía de un ADR propio: las citas hacían
referencia a «ADR-0010» que en realidad es «UiAutomator en instrumentados».
Este ADR formaliza las decisiones visuales transversales.

## Decisión

1. **Radio base 0 dp conmutable** vía `XauxaRadius.Base` (rango permitido
   0–6 dp). Cambiar el valor requiere revisión visual global (tests +
   capturas). Prohibido introducir radios mayores.
2. **Paleta M3 neutralizada**: los slots `secondary`/`tertiary` de
   Material Theme se alían al acento de marca; los tonos semánticos
   (success/warning/info) tienen par propio content/container.
3. **XauxaDotProgress**: sustituye a `CircularProgressIndicator` en
   toda la superficie Xauxa; reduced motion = representación estática.
4. **Calidad de tiles**:
   - Área táctil FUERA de la capa que escala (clickable antes de
     graphicsLayer).
   - Overlay de pulsación de color OPUESTO al texto (garantiza ≥4,5:1).
   - Alto mínimo (no fijo): crece con la fuente del usuario.
   - Columnas por escala de fuente (menos columnas con fuente grande).
   - Título de página acotado a 1,3× (el cuerpo escala completo).
5. **Gate ampliado**: `verifyDesignSystemCompliance` cubre feature,
   shared/src, androidApp/src y core:ui (no solo feature). Cero scripts.

## Alternativas descartadas

- **Radios 16–24 dp (bento/Material You)**: rompe el lenguaje Metro;
  genera cromo decorativo que no comunica jerarquía.
- **Capas de gris y bordes de reposo (Carbon)**: contradice el principio
  de contenido antes que cromo; la separación es por espacio y bloques.
- **Ripple de Material 3**: feedback con sombra/degradado que no es
  plano; sustituido por overlay de color plano.

## Consecuencias

- Todo componente nuevo usa `XauxaShape` (radio base conmutable).
- Los overlays/menús/diálogos usan `XauxaShape` vía la red de seguridad
  de `MaterialTheme.shapes`.
- La paleta M3 interna queda neutralizada en `XauxaTheme`.

## Pruebas

- `XauxaSchemeTest`: pares content/container de tonos ≥4,5:1; `brandText`
  ≥4,5:1; `borderControl` ≥3:1; todos los acentos en reposo y presionado.
- `XauxaTileContractTest` (commonTest): pure functions (maxLines,
  columnsFor, scaleFactor, tileRows, stagger, contraste en reposo/presión).
- `XauxaAccessibilityContractTest` (Robolectric): contratos de a11y.
- `verifyDesignSystemCompliance`: gate estático con fixtures.
- `verifyVisualHashes`: regresión visual (hashes SHA-256).

## Criterio de retirada

Este ADR queda supersedido si el producto aprueba un lenguaje visual
distinto (nuevo ADR con alternativas y evidencia); el radio base puede
cambiar dentro del rango 0–6 dp sin nuevo ADR, pero requiere re-ejecutar
toda la batería visual.

# ADR-0023 — Sistema de diseño de AgendaQr

**Estado:** ACEPTADO — versión 0.2.7 del 2026-10-18
**Relaciona:** especificación UX/UI V1.13, contrato normativo de diseño,
ADR-0007 (verificación de layout), ADR-0008 (voz), ADR-0015 (jerarquía
financiera).

## 1. Decisión

AgendaQr usa `tdesign_flutter@0.2.7` como especificación normativa de valores
visuales y patrones de interacción. Su implementación Compose Multiplatform vive
en `core/ui`; las pantallas componen las APIs de ese módulo. TDesign no es una
dependencia de ejecución.

La fuente normativa es el código del paquete oficial `tdesign_flutter` versión
`0.2.7` (paquete `tdesign-component`), en particular
`lib/src/theme/td_default_theme.dart` para tokens y los componentes de
`lib/src/components/` para tamaños, estados y patrones. Cuando cualquier otra
fuente lo contradiga, el código de `tdesign_flutter@0.2.7` prevalece. Los tests
de tokens fijan esos valores y citan esta versión como fuente.

Las pantallas mantienen la intención, el modelo mental, las reglas de negocio,
el orden de los flujos, la navegación conceptual y el back stack, la persistencia
local-first, el copy funcional, la guía de voz y los requisitos de accesibilidad
definidos por el producto.

## 2. Aplicación

1. Los temas claro y oscuro, roles semánticos, superficies, radios, espaciado,
   tipografía, movimiento, elevación y componentes siguen
   `tdesign_flutter@0.2.7`.
2. `core/ui` mantiene los tokens y componentes canónicos. El gate
   `verifyDesignSystemCompliance` evita literales visuales y controles
   prefabricados de `material3` fuera de esa capa.
3. La implementación usa Material 3 como infraestructura de componentes base;
   sus valores predeterminados no son autoridad visual.
4. El estado presionado usa los tokens activos de TDesign, sin gradiente,
   escala ni spring. Se conservan el foco de teclado/D-pad y la háptica de
   plataforma.
5. Los targets interactivos miden al menos 48 dp y el texto visible al menos
   11 sp. El tamaño visual de cada componente sigue la escala oficial aplicable.
6. Los cuatro destinos raíz, el back stack y las acciones existentes conservan
   sus contratos. No se incorporan gestos ni capacidades de producto nuevos.
7. `AmIcon` y `AmIcons` exponen los glifos TDesign usados por el producto. Los
   recursos derivados y avisos de licencia se registran antes de distribuirlos.

## 3. Desviaciones autorizadas

| ID | Desviación | Aplicación |
|---|---|---|
| D-01 | `AmAccents` contiene 12 tonos deterministas para identificar contextos. | Se conservan los tonos y el hash FNV-1a; sus pares de contraste mantienen la cobertura existente. |
| D-02 | Algunos colores de la paleta oficial no alcanzan el contraste requerido para texto o indicadores. | Se elige el siguiente escalón de la misma familia TDesign que cumple ≥4.5:1 para texto y ≥3:1 para elementos no textuales. |
| D-03 | Algunos controles TDesign tienen una superficie visual menor que el target accesible. | La geometría visual sigue la especificación y el target interactivo es ≥48 dp. |
| D-04 | `fontBodyExtraSmall` mide 10 sp. | No se usa para texto visible; el mínimo de texto aplicado es el siguiente tamaño oficial, 12 sp. |
| D-05 | TDesign Mobile no define un rail adaptativo. | `AmNavRail` conserva la navegación en ventanas amplias y usa los tokens del sistema. |
| D-06 | Las sombras de varias capas no tienen una reproducción directa en Compose. | Se usa la capa dominante de la sombra TDesign o una representación equivalente en `drawBehind`; la diferencia se documenta en el contrato. |
| D-07 | El overlay de hoja inferior no ofrece interacciones verificables de forma estable en Robolectric. | Los menús de opciones y selectores se presentan con `AmDialog` y listas; su contenido y flujo se mantienen accesibles y comprobables. |
| D-08 | Las familias tipográficas de TDesign varían por plataforma. | Se usa la fuente del sistema, sin empaquetar fuentes ni modificar el copy. |
| D-09 | La familia numérica `TCloudNumber` no se empaqueta. | Los importes usan la fuente del sistema con cifras tabulares y el peso que fija la escala. |
| D-10 | La fuente no expone tokens de movimiento. | Los valores de easing y duración se declaran en `AmMotion`. |

Los estados conservan contraste; el lienzo QR permanece blanco. Las
implementaciones nativas se limitan a servicios de plataforma y mantienen la
misma intención y transición funcional.

## 4. Consecuencias

- `core/ui` concentra los tokens, componentes y vectores Compose del sistema.
- La especificación UX/UI describe la composición por pantalla; el contrato
  normativo define valores, componentes, accesibilidad, excepciones y validación.
- `tdesign_flutter` y `tdesign-icons-svg` no forman parte del grafo de
  dependencias de ejecución.
- La licencia MIT de los vectores TDesign y su aviso de atribución figuran en
  `docs/05-design-system/06-licencias-terceros.md` y en los avisos Android.
- La verificación de interfaz es por aserciones de layout y contraste por tema,
  tamaño y escala de fuente; no se versionan capturas ni hashes (ADR-0007).

## 5. Criterios de aceptación

1. Los tests de tokens fijan los valores adoptados y citan
   `tdesign_flutter@0.2.7`.
2. Los tests de contraste verifican ambos temas y los umbrales de texto e
   indicadores.
3. El gate de diseño pasa y cada excepción ejecutable aparece también en el
   contrato normativo.
4. Los targets interactivos conservan ≥48 dp, el texto visible ≥11 sp y los
   layouts se validan a 320/360 dp y escala de fuente 200 %.
5. Cada pantalla sigue el patrón aplicable o registra una desviación con su
   motivo, sin cambiar reglas funcionales.
6. Las licencias y atribuciones están presentes y las librerías Flutter/Dart no
   se incluyen como dependencias de ejecución.
7. Las pruebas de navegación y back stack pasan sin cambios de contrato.
8. Las compilaciones, pruebas y comprobaciones de runtime se informan según lo
   ejecutado; compilar no se considera validación de runtime.

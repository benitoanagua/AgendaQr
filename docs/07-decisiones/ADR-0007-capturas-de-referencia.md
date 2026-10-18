# ADR-0007 — Verificación de layout sin capturas versionadas

**Estado:** APROBADA (técnica) — actualizada
**Relaciona:** ADR-0023 (fuente normativa de diseño), ADR-0010 (instrumentados).

## Contexto

La aceptación visual exige comprobar que las pantallas críticas se
componen sin recortes ni solapes en claro y oscuro, a 320/360 dp, con
escala de fuente al 100 % y 200 % y en tablet. Restricciones duras:

1. El repo **no versiona binarios de imagen** (`.gitignore` ignora
   png/jpg/jpeg/gif/webp/bmp).
2. La suite de UI corre sobre **Robolectric** en JVM, sin emulador en CI.

La verificación anterior se apoyaba en capturas PNG generadas en la suite
Robolectric y un `manifest.json` de SHA-256. Ese mecanismo tenía dos
problemas de fondo:

- El render NATIVE de Robolectric **depende del entorno** (JDK, sistema
  operativo): el mismo código produce hashes distintos en cada entorno, así
  que el hash no es una referencia estable.
- Las capturas y su manifiesto **cambiaban en cada reforma de tokens** y no
  aportaban una verificación determinista al gate: la comparación de hashes
  nunca formó parte de `verifyAgendaQrArchitecture` ni del CI.

## Decisión

**Verificación de layout por aserciones** sobre la suite Robolectric
existente. No se generan, versionan ni hashean capturas; la comparación de
hashes y su manifiesto dejan de existir.

Las pruebas de layout (`LayoutReferenceTest`, `LayoutReferenceExtraTest`)
componen cada escenario real de las pantallas críticas —claro/oscuro,
360 dp, 320 dp, tablet w720dp con `AmNavRail`, fuentes largas y escala de
fuente 100 %/200 %— y comprueban:

- la raíz se compone, se muestra y tiene tamaño;
- el árbol semántico no está vacío y contiene texto visible;
- ningún nodo sobresale lateralmente del root (sin recortes laterales),
  salvo el contenido dentro de un contenedor con scroll horizontal, cuyo
  desborde es intencional.

La protección determinista de recortes y solapes se completa con las
pruebas ya existentes: `AmScaffoldOverlapContractTest` (el contenido nunca
queda bajo el dock de acciones ni el FAB), `ScreensA11yCoverageTest` y
`AmAccessibilityContractTest` (objetivos táctiles y texto visible),
`FontScaleContractTest`/`AmPivotLayoutTest` (escala de fuente).

## Consecuencias

- El repo no contiene capturas de UI ni manifiestos de hashes.
- La verificación es determinista y no depende del JDK ni del sistema
  operativo.
- La regresión puramente estética (color o espaciado sutil) ya no se
  detecta automáticamente; su cobertura es por contrato de tokens
  (`AmSchemeTest`, `AmContrastPairsTest`, `AmTypographyTest`) y revisión
  humana en dispositivo, que se declara como no verificada en el CI.
- Compilar y pasar las pruebas de layout no sustituye la validación en
  hardware real, que sigue siendo una comprobación manual.

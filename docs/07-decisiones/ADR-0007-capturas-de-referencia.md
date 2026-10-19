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

La galería visual se produce **bajo demanda** y fuera del control de
versiones, en `build/`; nada se versiona ni se hashea. Hay dos artefactos
con propósitos distintos:

1. **Capturas de referencia de layout** (`build/roborazzi/<escenario>.png`),
   una por escenario y tema a 360 dp (claro/oscuro), sin variantes de ancho,
   tablet ni escala de fuente. El resto de configuraciones (320 dp, tablet
   w720dp, fuente 200 %) se sigue verificando por aserciones, sin
   capturarse. Reproducen como imagen cada escenario que ya comprueban las
   pruebas de layout; no se validan contra el contrato por pantalla (son
   escenarios de componente/pantalla, no la galería estricta por
   superficie). Se generan con:

   ```bash
   ./gradlew :feature:destinations:presentation:testDebugUnitTest \
       -Pagendaqr.capture=true
   ```

   Sin el flag la captura es un no-op: los tests siguen verificando el
   layout y no escriben ninguna imagen.

2. **Galería estricta por superficie** (`build/captures/gallery/`), con
   esquema de nombre determinista
   `<superficie>--<estado>--<ancho>dp--<tema>--font<escala>.png`. Cuando
   existe, `verifyScreenContract` la valida: dimensiones reales del bitmap
   coherentes con el ancho declarado (sin zoom, recorte ni viewport falso),
   sin duplicados byte-exactos ni repeticiones de la misma
   superficie/estado/configuración, y con cobertura de las configuraciones
   requeridas. Si no hay artefactos, el gate no falla porque la galería no
   se versiona.

**Productor (Fase 6).** `LayoutReferenceTest` produce la galería estricta con
un mapa explícito escenario → (superficie, estado): cada superficie del
contrato y sus estados declarados quedan capturados. La matriz prioriza
pantallas y estados únicos sobre combinaciones: 360 dp en claro/oscuro como
núcleo de revisión, un subconjunto denso a 320 dp, las superficies raíz con
`NavigationRail` a 720 dp (tablet) y un subconjunto legible a fuente 200 %;
todas las demás combinaciones siguen verificándose por aserciones. Los
overlays (S08 selector, S03 cámara) se capturan como su ventana de diálogo
completa, al tamaño real del componente. Hallazgo registrado: el vacío de
la bandeja de comprobantes sin asociar es byte-idéntico al vacío de
Actividades (comparten composición), así que solo se conserva una captura;
el copy propio del vacío queda como decisión de producto pendiente.

**Manifiesto (schema `agendaqr.captures/2`).** `build/captures/manifest.json`
declara por entrada la pantalla de AgendaQr, el identificador de su
pantalla de Stitch (ADR-0024; vacío para las superficies excluidas), estado,
tema, ancho, densidad, escala de fuente, dimensiones reales del bitmap,
hash, si es obligatorio, el motivo de inclusión y el test de origen. Cuando
hay capturas, el gate exige el manifiesto presente y sincronizado.

**Comandos.** Referencias de layout: `./gradlew
:feature:destinations:presentation:testDebugUnitTest -Pagendaqr.capture=true`.
Galería estricta completa con manifiesto e índices en un solo comando:

```bash
./gradlew captureGallery -Pagendaqr.capture=true
```

Con el flag las galerías se regeneran siempre completas y frescas (limpieza
previa; los PNG son efectos laterales que Gradle no rastrea); sin el flag,
la captura es un no-op y las pruebas siguen verificando el layout.

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
`FontScaleContractTest`/`AmTabsLayoutTest` (escala de fuente).

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

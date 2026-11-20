# ADR-0007 — Verificación de layout sin capturas versionadas

**Estado:** APROBADA (técnica) — actualizada
**Relaciona:** ADR-0025 (fuente normativa de diseño), ADR-0010 (instrumentados).

## Contexto

La verificación automática de la aceptación visual comprueba que las
pantallas críticas se componen sin recortes ni solapes en claro y oscuro,
a 360 dp en la galería, con escala de fuente al 100 % y 150 % en suites de
componentes. Restricciones duras:

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

La galería había crecido además con variantes de tablet (720 dp) y de fuente
al 200 %, con sufijo `--font<escala>` en el nombre de archivo. El coste de
generarlas y revisarlas no compensaba la señal que aportaban: se simplifica
la matriz a 360 dp, claro/oscuro, fuente 100 %.

## Decisión

**Verificación de layout por aserciones** sobre la suite Robolectric
existente. Las capturas no se versionan y la comparación de hashes y su
manifiesto dejan de existir como mecanismo de aceptación.

La galería visual se produce **bajo demanda** y fuera del control de
versiones, en `build/`; nada se versiona ni se hashea. Hay un único
artefacto de capturas: la **galería estricta por superficie**
(`build/captures/gallery/`), con esquema de nombre determinista
`<superficie>--<estado>--<tema>.png`. El ancho es único (360 dp) y vive como
metadato del manifiesto: no se repite en el nombre del archivo.
Sustituye al par anterior de galerías duplicadas
(`build/roborazzi/` y `build/captures/gallery/`): las pruebas siguen
componiendo y verificando cada escenario por aserciones, y solo los
escenarios declarados en el contrato escriben imagen.

Cuando la galería existe, `verifyScreenContract` la valida: dimensiones
reales del bitmap coherentes con el único ancho (360 dp, declarado en el
manifiesto; sin zoom, recorte ni viewport falso), sin duplicados byte-exactos
ni repeticiones de la misma superficie/estado/configuración, y con cobertura
de las dos configuraciones requeridas (`light`, `dark`). Si no hay artefactos,
el gate no falla porque la galería no
se versiona. Con el flag `-Pagendaqr.capture=true`, que la produce, las
pruebas corren siempre frescas y se limpia la galería antes de regenerarla.

**Productor.** `LayoutReferenceTest` produce la galería estricta con
un mapa explícito escenario → (superficie, estado): cada superficie del
contrato y sus estados declarados quedan capturados. La matriz prioriza
pantallas y estados únicos sobre combinaciones: solo 360 dp en claro/oscuro.
Se retiraron las raíces a anchos alternativos y a 720 dp (tablet) y el
subconjunto de pantallas a fuente 200 %: no se genera galería de tablet ni
capturas para el subconjunto de escalado. `AqNavRailLayoutTest` sí conserva
una aserción determinista del riel a 720 dp y 200 % de fuente, sin imagen
versionada. El resto de superficies adaptativas sigue sujeto a revisión
manual (ver Consecuencias). Los overlays (S08 selector, S03 cámara) se
capturan como su ventana de diálogo completa, al tamaño real del
componente.

**Manifiesto (schema `agendaqr.captures/6`).** `build/captures/manifest.json`
declara por entrada la pantalla de AgendaQr, el identificador de su
identificador de pantalla (sin ADR asociado; vacío para las superficies excluidas), estado,
tema, ancho, densidad, dimensiones reales del bitmap, hash, si es
obligatorio, el motivo de inclusión y el test de origen. No incluye el campo
`fontScale` porque el esquema de nombres ya no codifica la escala. Cuando
hay capturas, el gate exige el manifiesto presente y sincronizado.

**Comandos.** Galería estricta completa con manifiesto e índice en un solo
comando:

```bash
./gradlew captureGallery -Pagendaqr.capture=true
```

Con el flag la galería se regenera siempre completa y fresca (limpieza
previa; los PNG son efectos laterales que Gradle no rastrea); sin el flag,
la captura es un no-op y las pruebas siguen verificando el layout.

Las pruebas de layout (`LayoutReferenceTest`) componen cada escenario real
de las pantallas críticas —claro/oscuro, 360 dp y fuentes largas— y
comprueban:

- la raíz se compone, se muestra y tiene tamaño;
- el árbol semántico no está vacío y contiene texto visible;
- ningún nodo sobresale lateralmente del root (sin recortes laterales),
  salvo el contenido dentro de un contenedor con scroll horizontal, cuyo
  desborde es intencional.

La protección determinista de recortes y solapes se completa con las
pruebas ya existentes: `AqScaffoldOverlapContractTest` (el contenido nunca
queda bajo el dock de acciones ni el FAB), `ScreensA11yCoverageTest` y
`AqAccessibilityContractTest` (objetivos táctiles y texto visible),
`AqTabsLayoutTest`/`AqResponsiveLayoutContractTest` (escala de fuente al
100 %/150 %).

## Consecuencias

- El repo no contiene capturas de UI ni manifiestos de hashes versionados.
- La verificación es determinista y no depende del JDK ni del sistema
  operativo.
- Se retiran las suites de raíces a anchos alternativos: ni la galería ni
  esas suites se producen fuera de 360 dp. La cobertura JVM de fuente queda
  en `AqTabsLayoutTest` y `AqResponsiveLayoutContractTest`.
- Se reduce la cobertura automática de superficies completas: la galería no
  valida raíces tablet ni todas las pantallas al 200 %. `AqNavRailLayoutTest`
  cubre el riel a 720 dp y 200 % con aserciones de visibilidad y selección;
  el resto de las superficies adaptativas queda para revisión manual.
- Se retiran las suites específicas de escala de fuente
  (`FontScaleScreensTest`, `AuthFontScaleTest`, `FontScaleContractTest`): la
  cobertura JVM que queda vive en `AqTabsLayoutTest` y
  `AqResponsiveLayoutContractTest`; no hay
  verificación instrumentada de fuente.
- La regresión puramente estética (color o espaciado sutil) ya no se
  detecta automáticamente; su cobertura es por contrato de tokens
  (`AqSchemeTest`, `AqContrastPairsTest`, `AqTypographyTest`) y revisión
  humana en dispositivo, que se declara como no verificada en el CI.
- Compilar y pasar las pruebas de layout no sustituye la validación en
  hardware real, que sigue siendo una comprobación manual.

# ADR-0049 — Component Lab web (wasmJs) como módulo `:core:ui-lab`

**Estado:** ACEPTADO — 2026-11-26  
**Relacionado:** ADR-0007, ADR-0044, ADR-0046, ADR-0047, ADR-0048.

## 1. Contexto

El design system `Aq*` solo se inspeccionaba mediante `@Preview` y la
galería de capturas Robolectric (`AqDesignSystemGallery` +
`captureGallery`). Para iterar componentes —y para la verificación de
fidelidad Moon v1— hacía falta un banco de trabajo navegable: listado por
componente, variantes/estados, tema, escala de fuente, viewport y log de
interacciones, al estilo storybook/playbook.

## 2. Decisión

1. **Target wasmJs en `core/ui`**: se añade `wasmJs { browser() }` solo en
   `core/ui/build.gradle.kts` (no en el convention plugin compartido, para
   no contagiar a domain/data). Los 7 `expect` de plataforma reciben
   `actual` wasmJs: portapapeles vía `navigator.clipboard`, imágenes vía
   Skia (`Image.makeFromEncoded`), miniaturas de disco → `null`, ancho de
   ventana por viewport, sombras por la aproximación de elevación (misma
   que iOS, nueva excepción E-22) y `prefers-reduced-motion` por
   `matchMedia`.
2. **Módulo `:core:ui-lab`**: aplicación wasmJs browser
   (`binaries.executable()`, `outputFileName = agendaqr-ui-lab.js`) que
   depende únicamente de `:core:ui`. Convención propia
   `agendaqr.wasm-app` en build-logic (los plugins KMP/Compose ya están en
   el classpath vía build-logic; alias directos fallan). Node/Yarn del
   sistema y Binaryen vía npm (`binaryen@123`) porque el firewall bloquea
   las descargas oficiales; el lockfile queda en `kotlin-js-store/`.
3. **`AqComponentRegistry`** (`core/ui` commonMain, paquete `theme`):
   registro declarativo de los 78 composables públicos de `components/`
   con categoría, estados y composable de preview. El lab lo consume para
   lista, búsqueda, filtro, selector de estado y enlace profundo
   `#Componente[/estado]` (con `hashchange`). El Lab y la galería consumen el mismo registro; la galería es la vista
   read-only del catálogo, no una segunda implementación.
4. **Gate `verifyComponentRegistry`** (+ fixtures): todo `fun Aq*`
   público de `core/ui/components` debe tener entrada en el registro, y
   toda entrada debe corresponder a un componente con ≥1 estado.
   `core/ui-lab` queda excluido de `productionKotlinSources()` (junto a
   los tests): es harness de desarrollo, no UI de producto; sus literales
   (anchos de viewport 360/412/768 dp) no son tokens del sistema.
5. **Sin capturas versionadas**: el lab es herramienta viva; sus
   evidencias (capturas de verificación headless) van a `build/`, nunca a
   git (ADR-0007). La evidencia de fidelidad sigue siendo la pixel de
   ADR-0048 y las pruebas Robolectric.

## 3. Alternativas descartadas

- **Target Desktop JVM (Compose Desktop)**: daría un ejecutable nativo,
  pero exige distribuir un binario por plataforma, no comparte el stack
  web del producto final si Agenda QR publica web, y complicaría la CI.
  wasmJs reutiliza el mismo artefacto que un hipotético frontend web y se
  sirve con cualquier estático. Se reevalúa solo si wasmJs resulta
  inviable de forma permanente.
- **Portar la maquinaria de contratos de WaraWerse** (`ui-multiplatform`):
  su registro de 2.544 escenarios y gobernanza exceden el alcance; aquí el
  registro es una lista plana de previews por estado.

## 4. Nivel de fuente Figma para la Parte B

La fuente de verdad visual es la especificación **Moon Design System v1**
ya extraída del archivo de comunidad y versionada en el repo:
`docs/05-design-system/evidencia/figma/figma-moon-v1.md` (legible) y
`moon-v1-tokens-y-componentes.json` (datos crudos; prevalece sobre el .md
si discrepan). No se usó la API de Figma ni `moon_flutter`/`moon_tokens`
salvo para componentes ausentes de la especificación. Colores y tipografía siguen ADR-0044; los iconos de producto siguen ADR-0052
(Phosphor). Geometría, densidad, radios, padding/gap, alturas, estados
(hover = overlay "heles", pressed = escala 0,2 s, foco = anillo doble) y
variantes xs…xl vienen del JSON.

## 5. Consecuencias

- `verifyAgendaQrArchitecture` crece con `verifyComponentRegistry` y
  `verifyComponentRegistryFixtures`; un componente nuevo sin entrada en el
  registro rompe el gate.
- E-22 registra la sombra por elevación de `AqShadowPlatform.wasmJs.kt`
  (misma justificación que E-21 iOS); la sombra exacta de `BoxShadow` solo
  existe en Android.
- El lab se ejecuta con `./gradlew :core:ui-lab:wasmJsBrowserDevelopmentRun`
  o se sirve `wasmJsBrowserDistribution`; ver
  `docs/05-design-system/15-component-lab.md`.

# ADR-0007 — Capturas de referencia: Roborazzi + manifiesto de hashes

**Estado:** APROBADA (técnica, ronda 2 — 2026-10-09)
**Área:** Área D de la auditoría ronda 2 · Alcance: teléfonos Android (Robolectric JVM)

## Contexto

La aceptación de la ronda exige capturas de referencia de las pantallas
críticas en claro y oscuro. Restricciones duras:

1. **El repo no versiona binarios de imagen** (decisión del
   propietario, 2026-10-09: `.gitignore` ignora png/jpg/jpeg/gif/webp/bmp).
2. La suite existente de UI corre sobre **Robolectric** en JVM
   (`testDebugUnitTest`), sin emulador en CI.

## Decisión

**Roborazzi 1.26.0** (`roborazzi` + `roborazzi-compose` +
`roborazzi-junit-rule`, Apache 2.0) sobre la suite Robolectric existente.

Por qué no Paparazzi: exige layoutlib nativo + generación por módulo y no
reutiliza la infraestructura Robolectric ya existente (los tests de
accesibilidad y de fuente grande viven ahí); Roborazzi comparte el mismo
runner, las mismas `@Config(qualifiers="w320dp…")` y corre en el JVM del
gate actual. Compatibilidad verificada en esta pasada por compilación y
ejecución: AGP 8.11.1, Kotlin 2.2.20, Compose 1.8.2, Robolectric 4.14.1.

**Hallazgo crítico de integración:** sin
`@GraphicsMode(GraphicsMode.Mode.NATIVE)` las capturas salían todas negras
e idénticas entre temas (modo LEGACY de Robolectric no pinta). La anotación
es obligatoria en cada clase de captura y está documentada en el propio
test.

## Estrategia de referencia sin binarios

- Los PNG se generan en
  `feature/destinations/presentation/build/roborazzi/` (no versionados).
- La "referencia" es `docs/04-ux/visual-hashes/manifest.json`: SHA-256 de
  cada PNG (TEXTO versionado).
- Regresión visual = comparación de hashes:
  `./gradlew verifyVisualHashes` (regenera + compara).

**Sensibilidad al entorno:** el render NATIVE de Robolectric depende de la
plataforma (JDK, sistema). La comparación de hashes es determinista en un
mismo entorno (el manifest se generó en Linux x86_64 + JDK 17) y NO se
cuelga del CI por defecto: si cambia el entorno, se regenera el manifest y
el diff se revisa localmente con los PNG. El mecanismo es opt-in para
quien quiera blindarlo en su entorno.

## Pantallas capturadas (claro + oscuro)

S01 Inicio, login (AuthScreen), S02 Añadir, importación masiva (Idle). Los
componentes del DS tienen además los tests de layout/fontScale
(`Round2FontScaleTest`), que son la protección determinista del gate para
recortes/solapes.

## Comandos

```bash
./gradlew :feature:destinations:presentation:recordRoborazzi \
  -PallowDebugSigningForRc=true          # genera los PNG
./gradlew verifyVisualHashes  # regenera y compara hashes
```

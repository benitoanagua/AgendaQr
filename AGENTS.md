# Reglas del repositorio

## Atribución de commits

- El autor y committer de todo commit es la persona responsable:
  `Benito Anagua <benito.anagua@gmail.com>`.
- No añadir trailers `Co-Authored-By`, ni firmas `Generated with ...`,
  ni ninguna otra atribución automática de herramientas o agentes
  (Devin, Copilot, etc.) en los mensajes de commit.
- Antes de commitear, revisar `git config user.name` y `git config user.email`
  (local y global) y confirmar que corresponden a la identidad humana.

## Design System (Moon → `Aq*`)

Moon Design es la única autoridad visual (ADR-0044). La API viva es el puerto
Kotlin/Compose en `core/ui`; no hay runtime Flutter.

- Normas: `docs/05-design-system/` (contrato, tokens, catálogo).
- Reglas del agente: `.cursor/rules/moon-design-fidelity.mdc` y
  `moon-compose-ui.mdc`.
- Gates: `./gradlew verifyAgendaQrArchitecture` (ver `docs/QUALITY.md`).
- Galería: `./gradlew captureGallery -Pagendaqr.capture=true
  -PallowDebugSigningForRc=true`.
- Component Lab (wasmJs): `./gradlew :core:ui-lab:wasmJsBrowserDevelopmentRun`
  o `wasmJsBrowserDistribution`; registro en
  `docs/05-design-system/15-component-lab.md` (ADR-0049).

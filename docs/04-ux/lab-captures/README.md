# Capturas del laboratorio

> **Decisión (2026-10-09): los binarios de imagen ya no se trackean en el
> repo** (`.gitignore`: png/jpg/jpeg/gif/webp/bmp). Las capturas son
> evidencia perecedera generada en cada pasada; la evidencia ejecutable y
> reproducible vive en los tests (Robolectric/compose, gates de
> `verifyAgendaQrArchitecture`) y en el CI. Este README describe QUÉ se
> capturó y cómo regenerarlo.

## Capturas de la pasada V1.2 (2026-10-12)

Generadas con `componentLabWeb` + Chromium headless contra
`build/web/component-lab/` (no versionadas). El catálogo refleja los
componentes convencionales de Material 3 (botones/campos/listas/chrome
M3, tokens de formas 4/8/12/16/28). Las capturas cubren el default oscuro
en tres anchos (360/768/1440px) sin errores de consola.

- Checklist para revisión humana: legibilidad en estrecho, contraste del
  acento de marca sobre negro, estados del inspector en ambos temas,
  foco visible con teclado, NavigationBar del inspector de XauxaNavBar.

## Capturas históricas de la pasada Metro V1.1

La pasada Metro V1.1 (S01–S07, claro/oscuro/fuente 130 %) describía el
lenguaje retirado por ADR-0012; sus capturas no se conservan (evidencia
perecedera, decisión 2026-10-09) y la historia vive en `git log`.

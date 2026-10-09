# Capturas del laboratorio

> **Decisión (2026-10-09): los binarios de imagen ya no se trackean en el
> repo** (`.gitignore`: png/jpg/jpeg/gif/webp/bmp). Las capturas son
> evidencia perecedera generada en cada pasada; la evidencia ejecutable y
> reproducible vive en los tests (Robolectric/compose, gates de
> `verifyAgendaQrArchitecture`) y en el CI. Este README describe QUÉ se
> capturó y cómo regenerarlo.

## Pasada D6 (2026-10-05)

Generadas con `componentLabWeb` + Chromium headless (Puppeteer) contra
`build/web/component-lab/` (no versionadas):

| Captura | Ancho | Estado |
|---|---|---|
| `lab-light-narrow.png` | 360px | Render sin errores de consola |
| `lab-light-medium.png` | 768px | Render sin errores de consola |
| `lab-light-wide.png` | 1440px | Render sin errores de consola |
| `lab-dark-wide.png` / `lab-dark-wide2.png` | 1440px | Tema oscuro (default del lab) |

Notas honestas:
- El laboratorio se dibuja en un **canvas** (sin nodos de texto DOM): el
  toggle de tema ("Preview: oscuro/claro") no es alcanzable por
  automatización DOM, por lo que las capturas cubren el default oscuro en
  tres anchos; la captura "light" del flujo automatizado quedó idéntica
  (toggle no activado). El toggle manual queda para revisión humana.
- 0 errores de consola en las tres corridas (headless Chromium).
- Checklist para revisión humana: claridad en estrecho (tiles Metro con
  texto recortado?), contraste del acento de marca sobre negro, estados
  del inspector en ambos temas, foco visible con teclado.

## Cómo regenerar las capturas

```bash
./gradlew componentLabWeb
python3 -m http.server 8000 -d build/web/component-lab
# Chromium headless: http://localhost:8000 en 360/768/1440px
```

Las capturas de la pasada Metro V1.1 (S01–S07, claro/oscuro/fuente 130 %)
se describen en `metro-v11/README.md`.

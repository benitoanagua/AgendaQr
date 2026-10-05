# Capturas del laboratorio (pasada D6, 2026-10-05)

Generadas con `componentLabWeb` + Chromium headless (Puppeteer) contra
`build/web/component-lab/`:

| Archivo | Ancho | Estado |
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

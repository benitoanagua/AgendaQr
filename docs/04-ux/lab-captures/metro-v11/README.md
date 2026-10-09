# Capturas de verificación V1.1 (Metro) — emulador Android

Pasada de implementación UX/UI V1.1 (ADR-0005). Evidencia visual de T8
(2026-10-08), tomada en el AVD `agenda_qr` (API 34, x86_64) con la app
debug instalada y el stack local de Supabase
(`SUPABASE_URL=http://10.0.2.2:54321`). Complementan los tests de
accesibilidad (Robolectric) y el laboratorio Wasm; no los sustituyen.

> **Decisión (2026-10-09): las capturas NO están versionadas** (`.gitignore`
> global de imágenes). Este README documenta qué se verificó y cómo
> regenerarlas: instalar el APK debug en el emulador, navegar S01→S07 y
> capturar con `adb exec-out screencap -p > <nombre>.png` en claro, oscuro
> (`adb shell settings put secure ui_night_mode 2/1`) y fuente 130 %
> (`adb shell settings put system font_scale 1.3`).

## Capturas

| Archivo | Pantalla / estado |
|---|---|
| `s01-light.png` | S01 claro: título ligero, búsqueda dominante, rejilla de tiles (Añadir/Registrar/Favoritos/Contextos + tile vivo), Recientes sin borde, app bar inferior con Buscar primario y "…" |
| `s01-dark.png` | S01 oscuro: fondo negro puro, tiles con los mismos acentos (§12 Modo oscuro) |
| `s01-font130.png` | S01 con fuente del sistema al 130 % (§11 escalado) |
| `s02-light.png` | S02 Añadir: jerarquía congelada Galería → Desde otra app → Cámara; Volver en la app bar |
| `s04-light.png` | S04 Búsqueda: barra plana como primer bloque; app bar con Volver |
| `s06-lista-light.png` | S06 lista de contextos: cada fila con el bloque de acento derivado de su id (M4) |
| `s06-detalle-light.png` | S06 detalle: pivot QR · Actividades · Comprobantes con acento del contexto (M7) |
| `s06-pivot-act.png` | S06 pivot en la sección Actividades |
| `s06-pivot-comp.png` | S06 pivot en la sección Comprobantes |
| `s06-dark.png` | S06 oscuro: pivot y acentos sobre negro puro |
| `s07-registrar.png` | S07 Registrar: Guardar como acción principal de la app bar (defecto T7: primario nunca recortado), Volver en una línea (defecto T7) |
| `s07-font130.png` | S07 con fuente al 130 %: app bar y campos planos legibles |

## Verificación manual registrada (revisión por captura)

- Contraste: tiles con pares acento/texto de la tabla de la spec §12;
  revisado visualmente en claro y oscuro (el cálculo WCAG lo fijan los
  tests `XauxaAccentTest`).
- Foco y orden: verificado por semántica en tests Robolectric
  (`ScreensA11yCoverageTest`); el anillo sobre el acento queda pendiente
  de revisión con teclado físico/TalkBack interactivo.
- Recortes: S07 con fuente 130 % sin recorte del primario ni del Volver
  (defectos T7 resueltos; verificación explícita
  `t07_actions_live_in_the_bottom_app_bar`).
- S09 Revisar QR: preview centrada (cambio T6); no capturada aquí porque
  el flujo exige importar una imagen real — cubierta por el test T7 y
  pendiente de captura con QR impreso en la próxima pasada en físico.

## Pendiente de validación (no afirmado aquí)

- TalkBack/VoiceOver interactivo y foco por teclado en dispositivo físico.
- iOS runtime (requiere Xcode/simulador).
- QR real por cámara (validado en físico en V1; sin cambios en V1.1).

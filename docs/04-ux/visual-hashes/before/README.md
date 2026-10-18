# Línea base visual TDesign (T1)

Capturas generadas antes de modificar la UI, desde el commit inicial
`4165c9f` (`main`/PR #144), con Roborazzi sobre Robolectric SDK 34 y gráficos
NATIVE. Se incluyen las pantallas de referencia existentes en claro y oscuro,
a 360 dp y 320 dp, con escala de fuente al 100 % y 200 %.

- Escenarios: pruebas `VisualReferenceScreenshotTest` (40 escenarios con
  ambas anchuras y escalas) y las capturas adicionales existentes.
- PNG: 330; SHA-256 individual: `manifest.sha256`.
- Salidas de Gradle: `core:ui:allTests`, `feature:destinations:presentation:allTests`,
  `recordRoborazzi` y `verifyDesignSystemCompliance` pasaron. El gate se ejecutó
  con `-PallowDebugSigningForRc=true`, requerido por la configuración local.
- `verifyDesignSystemCompliance` pasó tras reejecutarse con esa propiedad; el
  primer intento sin ella falló al aplicar el plugin de firma, antes de evaluar
  las reglas del gate.
- Las capturas son referencias Robolectric, no validación en dispositivo.
  No había emulador ni dispositivo Android conectado.

La lista y hashes de la línea base se conservan para comparar cada artefacto
pre-migración durante la adopción visual. La manifest de Roborazzi normal sigue
siendo `../manifest.json`.

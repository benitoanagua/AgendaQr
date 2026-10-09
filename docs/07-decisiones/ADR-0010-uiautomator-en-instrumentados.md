# ADR-0010 — UiAutomator en las pruebas instrumentadas

**Estado:** APROBADA (pasada QA ronda 2 — 2026-10-09)
**Área:** Infraestructura de pruebas · androidTest

## Contexto

El checklist de la ronda 2 exige interactuar con superficies FUERA de la
app: diálogo de permisos del sistema, ajustes de la app, cambios de
`font_scale`/rotación/escalas de animación/modo avión, y auditoría del
árbol de accesibilidad de ventanas arbitrarias. `Espresso`/`ui-test-junit4`
solo alcanzan la propia app.

## Decisión

`androidx.test.uiautomator:uiautomator:2.4.0` (Apache 2.0) en
`androidTestImplementation` del `androidApp` — sin dependencias de
producción. Es la última versión estable publicada en los repositorios (la línea
2.6.x no existe en los repositorios — verificado contra el maven-metadata
de Google), compatible con `androidx.test` (core 1.6.1 / runner 1.6.2
del catálogo y con API 30/API 34; verificado por compilación y ejecución en esta pasada. Se acompaña de
`ui-test-junit4` (ya usado en Robolectric) para semántica Compose.

## Alternativas

- `UiAutomation.executeShellCommand` puro: cubre settings pero no
  diálogo de permisos ni dumps cómodos.
- `grantPermission`/`revokePermission` de `runner`: suficiente para
  revocar, insuficiente para pulsar "No permitir" en el diálogo del SO.

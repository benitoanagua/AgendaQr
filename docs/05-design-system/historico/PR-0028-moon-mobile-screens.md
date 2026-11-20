> **HISTÓRICO — no normativo.** Este documento fue consolidado en [11-matriz-gaps-mobile.md](../11-matriz-gaps-mobile.md) y [10-auditoria-fidelidad.md](../10-auditoria-fidelidad.md). No es fuente normativa; se conserva solo como registro histórico. La autoridad vigente es el contrato normativo v9.0 y ADR-0029.


> **HISTÓRICO — no normativo.** Este documento fue consolidado en [](../11-matriz-gaps-mobile.md) y [](../10-auditoria-fidelidad.md). No es fuente normativa; se conserva solo como registro histórico. La autoridad vigente es el contrato normativo v9.0 y ADR-0029.


# PR: ADR-0028 + aplicación móvil a pantallas (Polaris primario, Moon referencia)

## Resumen

Polaris sigue como autoridad visual. Moon solo como referencia de patrones
móviles. Se aplican gaps a pantallas reales sin dependencia Flutter.

## Cambios

### Documentación
- `ADR-0028-moon-patrones-moviles-polaris-primario.md`
- Enlace en `00-contrato-normativo-diseno.md` §7

### core/ui
- `AqExpansionPanel`: click solo en encabezado; semántica `expanded`
- `AqPinDots(error=)`: tono Danger en error de PIN (mensaje textual intacto)

### Pantallas
| Pantalla | Cambio |
|---|---|
| S07 Registrar | "Más detalles" → `AqExpansionPanel` |
| S04 Buscar | Chips de filtro por tipo si resultados mixtos |
| S00 Acceso / onboarding | `AqPinDots(error=…)` |
| S12 Importación (revisión) | Chips Todos / Duplicados / Sin clasificar |

## Verificación sugerida

```bash
./gradlew :core:ui:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew :feature:destinations:presentation:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true
./gradlew verifyDocs -PallowDebugSigningForRc=true
```

## Tests
- `MobilePatternScreensContractTest` (S04 chips mixtos, S12 filtros revisión)
- `AqPinDotsErrorTest`
- `AqExpansionPanelTest` (tags de encabezado/contenido)

## No incluido (a propósito)
- Dependencia `moon_design`
- S02 action sheet (la spec V1.17 fija lista con galería primaria)
- Bottom sheet para fecha/contexto (D-07)

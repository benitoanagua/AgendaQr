# Implementación Xauxa

> **Contrato normativo:** `docs/05-design-system/00-xauxa-contrato-normativo.md`. Este documento explica la distribución de implementación; no duplica ni redefine las reglas visuales. En caso de conflicto, prevalece el contrato normativo y los ADR aprobados.

Xauxa es la autoridad visual completa de Agenda QR. El código de producto no conserva una paleta propia, radios propios, sombras propias ni variantes visuales históricas.

## Capas

- `XauxaTokens.kt`: única fuente editable de tokens (color, espaciado, métricas, tipografía, movimiento y el conjunto de formas `XauxaShapes`).
- `XauxaTheme.kt`: adaptación del esquema Xauxa al runtime Compose (`MaterialTheme` con colorScheme, typography y shapes).
- `components/`: envoltorios finos de los componentes M3 (`XauxaComponents.kt`, `XauxaButtons.kt`, `XauxaInputs.kt`, `XauxaLists.kt`, `XauxaFeedback.kt`, `XauxaChrome.kt`, `XauxaDialogs.kt`, `XauxaTone.kt`, entre otros).
- `lab/`: validación visual y contractual del mismo sistema.

## Reglas

- Todo color sale de `XauxaColor`.
- Todo spacing sale de `XauxaSpacing`.
- Toda métrica sale de `XauxaMetrics`.
- Toda forma sale de `XauxaShapes` (vía `MaterialTheme.shapes`).
- Toda jerarquía tipográfica sale de `XauxaType`/`XauxaTextStyles` mapeada a los roles de M3.
- Todo movimiento sale de `XauxaMotion`.
- No usar `MaterialTheme.colorScheme` como fuente visual fuera de `XauxaTheme.kt`.
- No llamar componentes Material visuales directamente desde features: deben pasar por componentes Xauxa.
- No introducir sombras, radios ni colores locales.

## Reglas de la migración convencional (ADR-0012)

- Un acento por contexto, derivado de forma determinista del identificador del contexto; el acento de sistema `0067B8` es el valor por defecto. El color nunca se persiste en el dominio.
- Los controles interactivos son componentes M3 (o envoltorios finos): `Button`/`OutlinedButton`/`TextButton`/`IconButton`/`IconToggleButton`/`OutlinedTextField`/`ListItem`/`AlertDialog`/`NavigationBar`/`TopAppBar`/`Scaffold`, con tokens del tema; ningún `clickable(indication = null)` ni overlay de pulsación propio.
- Iconografía: únicamente el set Lucide mediante el wrapper `XauxaIcon` (tamaños desde tokens; descripción accesible en todo icono interactivo). Prohibido introducir otros sets.
- Movimiento: transición estándar corta entre superficies (fundido + desplazamiento pequeño); predictive back con progreso; reduced motion = cambio inmediato.
- Los tamaños de icono y las métricas viven como tokens en `XauxaTokens.kt`; el gate `verifyDesignSystemCompliance` rechaza literales en `core/ui`.

Estas reglas deben leerse junto al contrato normativo Xauxa y ADR-0012. No se consideran implementadas por el mero hecho de estar documentadas; verificar código, pruebas y consumidores antes de declarar alineación.

## Adaptación de plataforma

Material 3 es a la vez infraestructura y lenguaje: los componentes Xauxa delegan en los M3 con color, forma y tipografía del tema. La apariencia se determina exclusivamente por los tokens y contratos Xauxa.

## Terminación

Una pantalla está terminada cuando sus elementos visuales consumen el lenguaje Xauxa sin excepciones locales.

# Implementación Xauxa

> **Contrato normativo:** `docs/05-design-system/00-xauxa-contrato-normativo.md`. Este documento explica la distribución de implementación; no duplica ni redefine las reglas visuales. En caso de conflicto, prevalece el contrato normativo y los ADR aprobados.

Xauxa es la autoridad visual completa de Agenda QR. El código de producto no conserva una paleta propia, radios propios, sombras propias ni variantes visuales históricas.

## Capas

- `XauxaTokens.kt`: única fuente editable de tokens.
- `XauxaTheme.kt`: adaptación del esquema Xauxa al runtime Compose.
- `XauxaComponents.kt` y `XauxaExtendedComponents.kt`: primitives y componentes compartidos.
- `lab/`: validación visual y contractual del mismo sistema.

## Reglas

- Todo color sale de `XauxaColor`.
- Todo spacing sale de `XauxaSpacing`.
- Toda métrica sale de `XauxaMetrics`.
- Toda jerarquía tipográfica sale de `XauxaType`.
- Todo movimiento sale de `XauxaMotion`.
- No usar `MaterialTheme.colorScheme` como fuente visual fuera de `XauxaTheme.kt`.
- No usar `OutlinedTextField`, `AlertDialog`, chips o botones Material directamente desde features: deben pasar por componentes Xauxa.
- No introducir sombras, radios ni colores locales.

## Estado de implementación de reglas históricas (ADR-0005)

- Un acento por contexto, derivado de forma determinista del identificador del contexto; el acento de sistema `0067B8` es el valor por defecto. El color nunca se persiste en el dominio.
- Sin bordes de reposo en filas, botones, badges y tiles: los bordes son solo funcionales (foco, campo en foco/error, tile seleccionado).
- Campos de entrada planos: relleno `surfaceContainer`, etiqueta fija pequeña encima (no flotante), borde de 2 dp con acento solo en foco/error.
- Acciones: principal como bloque sólido de acento o en la app bar inferior; secundarias como texto con icono, sin caja.
- Iconografía: únicamente el set Lucide mediante el wrapper `XauxaIcon` (tamaños desde tokens; etiqueta visible en todo icono interactivo). Prohibido introducir otros sets.
- Movimiento: turnstile entre pantallas, tilt ≤150 ms en tiles, entrada escalonada 30–50 ms por tile (total ≤300 ms); live tile sin bucles; reduced motion = cambio de estado inmediato.
- Los tamaños de icono y las métricas de la rejilla de tiles viven como tokens en `XauxaTokens.kt` (múltiplos de 4 dp); el gate `verify-xauxa.sh` rechaza literales en `core/ui`.

Estas reglas históricas deben leerse junto al contrato normativo Xauxa y ADR-0010. No se consideran implementadas por el mero hecho de estar documentadas; verificar código, pruebas y consumidores antes de declarar alineación.

## Adaptación de plataforma

Material 3 puede seguir siendo infraestructura interna de los componentes Xauxa. La apariencia se determina exclusivamente por los tokens y contratos Xauxa.

## Terminación

Una pantalla está terminada cuando sus elementos visuales consumen el lenguaje Xauxa sin excepciones locales.

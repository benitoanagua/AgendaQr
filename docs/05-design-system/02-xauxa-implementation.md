# Implementación Xauxa

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

## Adaptación de plataforma

Material 3 puede seguir siendo infraestructura interna de los componentes Xauxa. La apariencia se determina exclusivamente por los tokens y contratos Xauxa.

## Terminación

Una pantalla está terminada cuando sus elementos visuales consumen el lenguaje Xauxa sin excepciones locales.

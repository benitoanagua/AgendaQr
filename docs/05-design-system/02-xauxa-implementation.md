# Implementación Xauxa en Agenda QR

Xauxa es la autoridad visual completa de Agenda QR. No existe una paleta propia de producto, una interpretación paralela de tokens ni una excepción visual local.

## Principios obligatorios

- Consumir tokens Xauxa; no valores visuales crudos en features.
- Rectángulos con radio 0.
- Sin sombras ni elevación decorativa.
- Separación mediante borde y spacing.
- Acciones e inputs compuestos desde primitives Xauxa existentes.
- Targets interactivos mínimos de 48dp / 44pt.
- Foco siempre visible.
- Movimiento con propósito, duración y easing Xauxa.
- Reduced motion respetado.
- Mapeo de plataforma definido antes de cerrar un componente.

## Tokens

`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/XauxaTokens.kt` es la única fuente editable de tokens. Los componentes solo consumen la fachada semántica `XauxaColor`, las escalas `XauxaSpacing`, `XauxaMetrics`, `XauxaType` y `XauxaMotion`.

## Aplicación

La UI de Agenda QR debe parecer una aplicación Xauxa aplicada al dominio de Agenda QR: misma geometría, misma densidad, mismo tratamiento tipográfico, mismas superficies, mismo lenguaje de estados y misma filosofía de movimiento.

No se conservan colores, radios, sombras, campos, chips, headers o composiciones de la implementación anterior cuando difieran de Xauxa.

## Gobierno

Toda desviación debe entrar como cambio explícito del sistema Xauxa, no como excepción de una feature.


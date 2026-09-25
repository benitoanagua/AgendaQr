# Xauxa — auditoría de fidelidad

El objetivo es una sola implementación visual: Xauxa.

## Control

Se consideran defectos de adopción:
- colores fuera de los tokens Xauxa;
- consumo de `MaterialTheme.colorScheme` fuera del adaptador de tema;
- primitives Material directos en features;
- radios no definidos por Xauxa;
- sombras/elevaciones decorativas;
- tamaños/spacing literales fuera de tokens;
- estados o motion que contradigan los contratos Xauxa;
- nomenclatura heredada de fuentes visuales anteriores.

## Situación de la rama

La rama migra la paleta, el tema, el laboratorio y las pantallas principales a Xauxa. Antes del merge se debe ejecutar la suite Android y revisar cualquier error de compilación generado por la migración.

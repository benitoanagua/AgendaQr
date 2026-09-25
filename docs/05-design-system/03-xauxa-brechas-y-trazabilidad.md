# Xauxa — auditoría de fidelidad

Agenda QR debe seguir Xauxa directamente. Este documento ya no mantiene una conciliación entre una implementación propia y una fuente visual histórica.

## Fuente

Xauxa define tokens, fundamentos, componentes, patterns, estados, movimiento, tipografía, accesibilidad y breakpoints.

## Criterio de terminado

Un componente o pantalla está terminado cuando:
1. consume exclusivamente primitives/componentes Xauxa existentes;
2. no introduce valores visuales locales;
3. no introduce radios ni elevaciones decorativas;
4. respeta los estados y motion de Xauxa;
5. tiene mapeo de plataforma;
6. pasa las compuertas de gobierno visual.

## Estado actual

La revisión sobre la rama `main` detectó residuos de la implementación anterior: inputs Material directos en features, uso de `MaterialTheme` dentro del laboratorio y documentación que presentaba una paleta propia.

Estos residuos se deben eliminar antes de considerar la adopción fiel.

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


## Pendientes de cierre y límites de implementación

La migración de fundamentos y los ajustes de presentación registrados en `04-component-audit-register.md` no equivalen a una auditoría completa de conformidad. Mantener estos puntos abiertos hasta contar con evidencia:

1. **Identidad/contraste:** confirmar formalmente el acento de marca y medir contraste de texto, controles, estados y foco en temas claro y oscuro. No sustituir el color actual por una elección arbitraria.
2. **Accesibilidad real:** recorrer componentes y flujos con TalkBack y VoiceOver; validar foco/teclado, orden de lectura, targets, asociación de errores y anuncios de carga/banners, incluyendo ausencia de anuncios repetidos.
3. **Responsive:** inspeccionar listas, encabezados con varias acciones, formularios, diálogos y acciones largas en tamaños compactos/amplios y con escalado de texto. Corregir los casos reproducibles por grupos de pantallas, no mediante cambios aislados sin patrón.
4. **QR/plataformas:** validar captura, preview, permisos, errores y persistencia en Android; verificar implementación y comportamiento equivalentes en iOS. El estado común `commonMain` no demuestra por sí mismo que el flujo de plataforma esté probado.
5. **Navegación/motion:** resolver el alcance de la transición turnstile (qué transiciones deben usarla) y el comportamiento con movimiento reducido antes de conectarla globalmente. Probar atrás predictivo Android en dispositivo; revisar navegación nativa iOS.
6. **Cobertura de producción:** completar inventario de consumidores de componentes, revisar primitives Material directos y valores visuales literales fuera de tokens, y alinear laboratorio/catálogo con las capacidades efectivas.
7. **Validación final:** ejecutar tests y builds acordados, después pruebas visuales y de accesibilidad manuales; registrar plataforma, dispositivo/tamaño, resultado y defectos. No marcar como verificado algo que solo está documentado o compila.

No se deben cerrar los puntos que requieren decisión de producto (acento, alcance de turnstile) ni los que requieren dispositivo/lector de pantalla mediante cambios de código especulativos. Las pruebas locales y builds permanecen aplazadas hasta la fase final acordada.

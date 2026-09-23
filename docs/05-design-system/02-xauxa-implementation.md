# Implementación Xauxa en Agenda QR

La implementación Compose/KMP adopta los invariantes de Xauxa Design System v8:

- tokens antes que valores visuales locales;
- `radius = 0` para rectángulos;
- sin elevación/sombras para jerarquía;
- un único acento de producto: AgendaQr `#0E7D6E` y `#7FD9C9`;
- separación mediante borde y spacing;
- targets táctiles mínimos de 48dp;
- foco visible;
- motion con propósito y respeto de reduced motion;
- paridad Compose/SwiftUI como condición de terminado;
- composición de componentes antes de crear componentes nuevos.

## Aplicación al UX/UI V1 congelado

El contrato visual se aplica a las pantallas definidas en `docs/04-ux/02-especificacion-ux-ui-v1.md`.

- Home prioriza búsqueda sin convertir Agenda QR en dashboard administrativo.
- Añadir prioriza Galería, luego Desde otra app, y deja Cámara como captura secundaria.
- Buscar utiliza resultados heterogéneos con etiquetas semánticas; no crea navegación previa por tipo.
- Contexto utiliza filas/secciones y no se convierte en dashboard financiero.
- Command Bar se reserva para superficies de detalle/foco con acciones concretas.
- El scanner reutiliza lenguaje visual XauxaXcan, sin métricas técnicas.
- Importación, guardado, sincronización y error se expresan como estados de UI, no destinos artificiales.
- Estados semánticos no dependen únicamente del color.
- Reduced motion no puede eliminar información ni recuperación.

## Componentes/patrones

Se prioriza composición de componentes existentes. Pueden reutilizarse, donde corresponda:

- Search bar;
- Text input / Textarea;
- Favorite toggle;
- Category chip;
- Destination card;
- QR fullscreen;
- Multi-source import;
- Optional biometric/PIN lock.

No se crea un componente nuevo solo porque una pantalla tenga una composición diferente.

## Nota

La especificación UX/UI V1 está congelada. Este documento describe cómo aplicar Xauxa al contrato; no autoriza modificar el contrato UX.

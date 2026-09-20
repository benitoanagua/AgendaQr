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

Componentes/patrones específicos de AgendaQr definidos por Xauxa:

- Search bar (C19)
- Text input / Textarea (C20)
- Favorite toggle (C21)
- Category chip (C22)
- Destination card (C23)
- QR fullscreen (P3)
- Multi-source import (P4)
- Optional biometric/PIN lock (P5)

RF-09 no introduce un indicador visual offline porque el Design System explícitamente deja ese comportamiento sin definir.

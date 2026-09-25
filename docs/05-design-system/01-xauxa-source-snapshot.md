# Xauxa source snapshot used by Agenda QR

This file records the portions of Xauxa Design System that are normative for this implementation.
The complete design artifact and Xauxa project remain external source references.

## Product brand

AgendaQr:

- `--brand`: `#0E7D6E`
- `--brand-accent`: `#7FD9C9`

## Invariants

1. Semantic tokens; no raw visual values in feature code.
2. `radius = 0` for rectangular containers; real circles remain circles.
3. No decorative elevation/shadow.
4. One product accent; other colors are semantic.
5. Compose existing primitives before adding components.
6. Infinite motion only for a real active state.
7. Respect reduced motion.
8. Define platform mapping before a component is considered complete.
9. Interactive targets are at least 48dp / 44pt.
10. Focus remains visible.

## Typography

- Archivo: display/page/tile headers.
- Roboto/San Francisco: UI/body/metadata.
- Left alignment by default.
- Centering is reserved for full-page empty/error states.

## Layout

- Base spacing: 4px.
- Page/grid composition is full-bleed rather than decorative framed cards.
- Structural separation uses border and spacing.

## Motion

- Medium duration: 300ms.
- Standard easing: cubic-bezier(.4,0,.2,1).
- Reduced motion must suppress animation/transition.

## Xauxa reconciliation

The Xauxa source contains historical implementation details such as `shadow-lg` and small `rounded-*` usages. Those are not copied into Agenda QR because the Xauxa Design System explicitly marks them as violations. The specification prevails over the historical implementation.

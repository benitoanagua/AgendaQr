# Kotlin Way — Agenda QR

## Domain

- Domain models are immutable `data class` values.
- Business actions are small use cases with `operator fun invoke()`.
- Domain exposes interfaces, not framework implementations.
- Platform and Compose dependencies are forbidden in `domain`.

## Data

- Repositories implement domain contracts.
- Persistence is hidden behind a tiny `DestinationStore` port.
- Serialization is an implementation detail of the data layer.
- Concurrency is serialized with `Mutex` around read/write mutations.

## Presentation

- UI consumes immutable `DestinationsUiState`.
- User intents are represented by a sealed interface.
- Routes are represented by a sealed interface.
- Side effects live in the ViewModel/use-case boundary, not in reusable UI components.
- `Flow`/`StateFlow` is preferred over callback chains for observable state.

## Multiplatform

- Platform APIs are isolated in `androidMain`/`iosMain`.
- `expect`/`actual` is used only where a platform capability is genuinely required.
- Product/domain modules remain platform-neutral.

## UI

- Xauxa semantic tokens are the only source of visual values.
- Rectangular surfaces use `RectangleShape`.
- Elevation and shadow are not used as decorative styling.
- Interaction geometry remains at least 48dp where applicable.
- Screens compose approved Xauxa primitives; they do not create a second component vocabulary.

## Build

- Convention plugins centralize module behavior.
- Version catalog centralizes dependency versions.
- Verification tasks are explicit and deterministic.

# Android application host

`androidApp` is the Android host application for Agenda QR.

It is intentionally named `androidApp` to mirror the repository's platform hosts:

- `androidApp/` — Android application host and Android-only integration boundary.
- `iosApp/` — iOS/Xcode application host and iOS-only integration boundary.
- `shared/` — Kotlin Multiplatform shared application/runtime layer.

The Android host owns Android lifecycle, intents, permissions, camera/picker/share entry points and other Android platform concerns. Business rules remain outside this module.

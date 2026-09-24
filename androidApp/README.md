# Android application host

`androidApp` is the Android host application for Agenda QR.

It is intentionally named `androidApp` to mirror the repository's platform hosts:

- `androidApp/` — Android application host and Android-only integration boundary.
- `iosApp/` — iOS/Xcode application host and iOS-only integration boundary.
- `shared/` — Kotlin Multiplatform shared application/runtime layer.

The Android host owns Android lifecycle, intents, permissions, camera/picker/share entry points and other Android platform concerns. Business rules remain outside this module.

## Supabase configuration

The Android build reads two public configuration values at compile time
(`feature/destinations/data/build.gradle.kts`), from a Gradle property or an
environment variable with the same name, in that order:

- `SUPABASE_URL`
- `SUPABASE_PUBLISHABLE_KEY`

For local development, put them in `~/.gradle/gradle.properties` (user-level
file, outside the repository; never in the project `gradle.properties`):

```properties
SUPABASE_URL=https://<project-ref>.supabase.co
SUPABASE_PUBLISHABLE_KEY=<publishable-key>
```

Symptom of a build without configuration: the app starts on the login screen
and any sign-in attempt shows "Supabase no está configurado en esta
compilación; no se puede iniciar sesión." That message comes from
`UnconfiguredAuthRepository` and means the APK was built with both values
empty — it is not a network or credentials error.

Only the public/publishable key is used. Never place a `service_role` key in
the app, the repository, or any build input. Full policy in
`docs/06-arquitectura/05-local-first-supabase.md`.

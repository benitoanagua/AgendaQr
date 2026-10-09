# Kotlin way — política y excepciones

El repositorio NO contiene scripts shell/JS/TS/Python: toda la lógica de
build, verificación y testing es Kotlin/Gradle. La tarea
`verifyNoScripts` (en `verifyAgendaQrArchitecture`) lo garantiza.

## Excepciones (no son scripts de producto)

| Archivo | Razón |
|---|---|
| `gradlew` | Wrapper estándar de Gradle (posix); requerido para ejecutar builds. |
| `gradlew.bat` | Wrapper estándar de Gradle (Windows). |

## Mapeo de scripts eliminados → tareas Gradle

| Antes (script) | Después (tarea Gradle) |
|---|---|
| `docs/05-design-system/verify-xauxa.sh` | `./gradlew verifyDesignSystemCompliance` + `verifyControlMinSize` |
| `docs/04-ux/visual-hashes/verify.sh` | `./gradlew recordVisualHashes` + `verifyVisualHashes` |
| `supabase/tests/run-acceptance.sh` | `DATABASE_URL=... ./gradlew :supabase:acceptance:test` |

# Estado de implementación

> La historia de cambios vive en `git log`, no aquí. Este documento describe
> el estado ACTUAL: capacidades, límites conocidos y puertas de calidad.

## Capacidades implementadas

| Área | Estado | Cobertura |
|---|---|---|
| Dominio (operaciones, comprobantes, contextos) | Implementado | domain tests |
| Datos (Supabase local-first, sync queue) | Implementado | data tests |
| UX/UI V1.1 (Metro/Xauxa, S01–S12) | Implementado | Robolectric suite + visual hashes |
| Búsqueda global (S04/S05) | Implementado | Robolectric suite |
| Importación QR (galería/cámara/share) | Implementado | Instrumentados + unit |
| Registro de actividad (S07) | Implementado | Robolectric suite |
| Permisos de cámara (política de ajustes) | Implementado | Instrumentados |
| Navegación (back stack restaurable) | Implementado | commonTest |
| Accesibilidad (contraste, háptica, anuncios) | Implementado | core:ui tests + Robolectric |
| Iconografía (Lucide ISC) | Implementado | compilación 3 targets |

## Límites conocidos (decisiones, no bugs)

- iOS runtime sin validar (requiere Xcode/simulador).
- TalkBack/VoiceOver interactivo requiere dispositivo físico.
- `XauxaPivot` no desplaza horizontalmente con fuente al 200 % (riesgo conocido).
- Glifos "Editar", "Compartir", "Mostrar QR" sin icono asignado (ADR-0005, Puntos abiertos).

## Puertas de calidad (comandos actuales)

| Puerta | Comando |
|---|---|
| Arquitectura + design system + no-scripts | `./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true` |
| Visual hashes (Roborazzi) | `./gradlew verifyVisualHashes -PallowDebugSigningForRc=true` |
| Supabase acceptance (JDBC) | `DATABASE_URL=jdbc:postgresql://... ./gradlew :supabase:acceptance:test` |
| Tests de módulos | `./gradlew :core:ui:testDebugUnitTest :feature:destinations:presentation:testDebugUnitTest ...` |
| Lint + release | `./gradlew :androidApp:lint :androidApp:assembleRelease -PallowDebugSigningForRc=true` |
| Lab web (wasm) | `./gradlew componentLabWeb` |
| CI (4 jobs) | Push a main → Android validation workflow |

## Defectos abiertos de implementación

1. Badge "Sincronizado" compite por ancho de fila en pantallas estrechas.
2. Destinos "Sin nombre"/"QR importado" indistinguibles.

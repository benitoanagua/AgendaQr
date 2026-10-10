# Estado de implementación

> La historia de cambios vive en `git log`, no aquí. Este documento describe
> el estado ACTUAL: capacidades, límites conocidos y puertas de calidad.
> Solo se declara "verificado" lo que una puerta ejecutada en este entorno
> respalda; lo que no puede ejecutarse se registra como **NO VERIFICADO**,
> nunca como PASS (contrato §10).

## Capacidades implementadas y verificadas

| Área | Estado | Verificación |
|---|---|---|
| Dominio (operaciones, comprobantes, contextos) | Implementado | domain tests |
| Datos (Supabase local-first, sync queue) | Implementado | data tests |
| UX/UI V1.1 (Metro/Xauxa, S01–S12) | Implementado | Robolectric suite + hashes visuales + gate del design system |
| Chrome e insets (barra inferior M7, `XauxaScreenScaffold`) | Implementado | Robolectric suite + gate |
| Búsqueda global (S04/S05) | Implementado | Robolectric suite |
| Importación QR (galería/cámara/share) | Implementado | Instrumentados + unit |
| Registro de actividad (S07) | Implementado | Robolectric suite |
| Permisos de cámara (política de ajustes) | Implementado | Instrumentados |
| Navegación (back stack restaurable) | Implementado | commonTest |
| Accesibilidad (contraste, foco visible, pulsación, háptica, anuncios) | Implementado | `core:ui` tests + Robolectric |
| Iconografía (Lucide ISC) | Implementado | compilación 3 targets |

## NO VERIFICADO en esta pasada

Estas puertas no pueden ejecutarse en este entorno (falta de Xcode,
dispositivo físico o red). No se declaran PASS.

| Capacidad | Motivo | Cómo se verifica |
|---|---|---|
| Runtime iOS (arranque y render) | Requiere Xcode/simulador | `IOS_RUNTIME_CHECKLIST.md` |
| TalkBack/VoiceOver interactivo | Requiere dispositivo físico | checklist manual (ronda 2) |
| Insets, cámara, foco nativo y motion en dispositivo | Requiere dispositivo físico | `ANDROID_PHYSICAL_DEVICE_TEST.md` |
| Supabase acceptance (JDBC) | Requiere base de datos | `:supabase:acceptance:test` con `DATABASE_URL` |

## Límites conocidos (decisiones, no bugs)

- Glifos "Editar", "Compartir" y "Mostrar QR" sin icono asignado
  (ADR-0005, punto 6): esas acciones de la app bar llevan etiqueta de
  texto visible; no se deduce la acción de un glifo sin decisión de
  producto.
- El contraste NO textual de algunos tiles de acento contra el fondo del
  tema es < 3:1 (contrato §5.2, ADR-0005 punto 7). Se acepta porque el
  tile siempre lleva etiqueta visible, su foco usa el color del contenido
  (≥3:1 contra el acento) y su pulsación el overlay opuesto; la separación
  entre tiles la da el `tileGap`, no un borde.
- Chips de filtro con contorno de selección: son controles con estado
  (borde funcional de M9), no contenedores (ADR-0005, punto 9).

## Defectos abiertos de implementación

Ninguno registrado. Los dos defectos previos —el badge de sincronización
compitiendo por el ancho de la fila y los destinos "Sin nombre"/"QR
importado" indistinguibles— están cerrados y cubiertos por
`DestinationRowLayoutTest` y `DestinationSubtitleTest`.

## Puertas de calidad

| Puerta | Comando | Resultado (esta pasada) |
|---|---|---|
| Arquitectura + design system + no-scripts | `./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true` | PASS |
| Tests de módulos | `./gradlew :core:ui:testDebugUnitTest :feature:destinations:presentation:testDebugUnitTest -PallowDebugSigningForRc=true` | PASS |
| Regresión visual (Roborazzi + SHA-256) | `./gradlew :feature:destinations:presentation:recordRoborazziDebug recordVisualHashes verifyVisualHashes -PallowDebugSigningForRc=true` | PASS (8 capturas) |
| Lint + release | `./gradlew :androidApp:lint :androidApp:assembleRelease -PallowDebugSigningForRc=true` | PASS |
| Lab web (wasm) | `./gradlew componentLabWeb` | PASS |
| Supabase acceptance (JDBC) | `DATABASE_URL=jdbc:postgresql://... ./gradlew :supabase:acceptance:test` | NO VERIFICADO (sin base de datos) |
| CI (4 jobs) | Push a `main` → Android validation workflow | NO VERIFICADO (fuera del entorno local) |

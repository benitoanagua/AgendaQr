# Arquitectura — Overview

Agenda QR es una aplicación Kotlin Multiplatform con dos hosts de plataforma y una capa compartida.

```text
androidApp/  → aplicación Android
      │
shared/      → runtime/capa KMP compartida
      │
feature/*    → dominio, datos y presentación
      │
core/*       → capacidades transversales

iosApp/      → aplicación iOS/Xcode
```

## Regla de dependencia

```text
presentation → domain
presentation → data (solo mediante wiring permitido)
data → domain
platform host → shared/features
```

El dominio no conoce Compose, Android, UIKit, SwiftUI ni almacenamiento concreto.

## Hosts

`androidApp` e `iosApp` son deliberadamente simétricos como hosts de plataforma. El primero es un módulo Gradle Android; el segundo es el proyecto/host Xcode que consume el framework KMP.

## Validación

Android se valida primero. iOS se mantiene compilable/estructuralmente soportado, pero no se declara validado hasta una ejecución real con Xcode.

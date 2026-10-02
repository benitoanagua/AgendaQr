# Hosts de plataforma

## Android

El host se denomina `androidApp/`, no `app/`, para hacer explícita su responsabilidad y mantener simetría con `iosApp/`.

`androidApp` contiene únicamente responsabilidades propias de Android:

- lifecycle;
- `Activity`;
- intents;
- permisos;
- camera/picker boundaries;
- Android Sharesheet;
- `FileProvider`;
- inicialización de dependencias Android.

## iOS

`iosApp` contiene el host SwiftUI/Xcode y los boundaries nativos de iOS.

## Regla

Ningún host de plataforma define reglas de negocio de Agenda QR. Esas reglas permanecen en los módulos compartidos y en el dominio.

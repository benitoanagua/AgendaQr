# Estado de implementación

## Plataforma

| Plataforma | Estado | Criterio |
|---|---|---|
| Android | Validación inicial | debe probarse en emulador/dispositivo |
| iOS | Soporte arquitectónico | no se declara validado hasta Xcode |

## Implementado en la base

- Kotlin Multiplatform con Android e iOS.
- `androidApp` como host Android.
- `iosApp` como host iOS/Xcode.
- Dominio aislado de Compose y APIs de plataforma.
- Repository + use cases + `StateFlow`.
- Persistencia local Android/iOS.
- Búsqueda, favoritos, recientes y categorías.
- Crear, editar, eliminar y reemplazar QR.
- QR fullscreen.
- Share outbound como imagen.
- Share inbound Android mediante `ACTION_SEND` / `ACTION_SEND_MULTIPLE`.
- Cámara, galería y selección múltiple Android con decodificación QR.
- Tokens semánticos Xauxa y gates visuales/arquitectónicos.

## Pendiente de validación real

- Build Gradle completo cuando el entorno pueda resolver el Gradle Wrapper y dependencias.
- Ejecución Android en emulador/dispositivo.
- Cámara y picker en hardware Android.
- Share inbound/outbound con aplicaciones reales.
- Ejecución iOS mediante Xcode.
- Validación visual por screenshots/Roborazzi.
- Integraciones nativas iOS de cámara, Photos, share y protección local.

## Regla

Código implementado no equivale a capacidad validada. La implementación compartida y la validación de cada plataforma se registran por separado.

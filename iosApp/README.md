# iOS host

The Kotlin Multiplatform `:shared` module publishes the `AgendaQrShared` framework. The SwiftUI host consumes `MainViewController(filePath:)` and provides the platform-local application-support path.

AgendaMobile parity requires native integrations (camera/share/biometric/brightness) to be added at the platform boundary without changing the domain model.

## AppIcon — instrucciones para el equipo

El proyecto Xcode (no versionado) requiere un ícono de app. **No se genera ningún PNG en este repositorio.** La fuente de verdad es el SVG vectorial:

    docs/branding/agendaqr-logo.svg

### Si usas Xcode 26+ con Icon Composer

> **Nota:** a la fecha de esta documentación (octubre 2026), Icon Composer es
> una herramienta nueva en Xcode 26. Verifica en la documentación oficial de
> Apple ([developer.apple.com/documentation](https://developer.apple.com/documentation))
> que la versión de Xcode instalada soporte SVG como capa de entrada antes de
> seguir estos pasos.

1. En Xcode, abre el proyecto `iosApp/AgendaQrIOS.xcodeproj`.
2. Selecciona `Assets.xcassets` → `AppIcon`.
3. Usa **Icon Composer** (menú Editor → Open in Icon Composer o clic en el
   ícono de la herramienta en la barra).
4. En Icon Composer, importa `docs/branding/agendaqr-logo.svg` como capa.
5. Ajusta la capa al 100 % del canvas (el SVG ya tiene fondo azul integrado).
6. Exporta / genera los tamaños requeridos por iOS desde Icon Composer.

### Si usas Xcode < 26 (sin Icon Composer o sin soporte SVG)

El equipo de diseño debe generar un PNG de **1024 × 1024 px** desde el SVG de
referencia (por ejemplo con Inkscape, Figma o la CLI `rsvg-convert`) y añadirlo
manualmente al `AppIcon` de `Assets.xcassets` en Xcode:

```
rsvg-convert -w 1024 -h 1024 docs/branding/agendaqr-logo.svg -o /tmp/AppIcon-1024.png
```

Ese PNG **no se versiona** en este repositorio (la política es SVG-only);
se genera localmente y se agrega desde Xcode.

### Colores de marca para referencia

| Uso               | Hex       |
|-------------------|-----------|
| Azul principal    | `#0067B8` |
| Azul oscuro       | `#004A87` |
| Blanco            | `#FFFFFF` |

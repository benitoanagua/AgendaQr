# Xauxa — Tipografía

**Contrato (spec V1.2 §12, ADR-0012):** familia del sistema en todos los
estilos — **Roboto** en Android, **San Francisco** en iOS — con los roles
tipográficos estándar de Material 3. No se empaqueta ninguna fuente
propia; la marca vive en el color de acento.

## Familia única

| Token | Familia | Origen | Dónde se usa |
|---|---|---|---|
| `FamilyUi` | Sans del sistema (`FontFamily.Default`) | No se empaqueta | Todos los estilos: títulos, cuerpo, etiquetas, números |
| `FamilyMono` | Monospace del sistema | No se empaqueta | Números/identificadores (hero, stats) |

- `XauxaType.FamilyDisplay` ya no existe: el título de página usa el rol
  `titleLarge`/`headlineSmall` de M3 con familia de sistema.
- La fuente Archivo (OFL 1.1) empaquetada en V1.1 fue retirada del
  binario en la migración a V1.2 (ADR-0012); el registro de licencias
  (`06-licencias-terceros.md`) la dio de baja con ella.

## Roles sobre Material 3

`XauxaTheme` entrega a `MaterialTheme(typography = …)` un mapeo desde
`XauxaType`/`XauxaTextStyles` a los roles estándar de M3, de modo que
todos los componentes hereden la misma tipografía:

| Rol M3 | Uso | Tamaño (sp) |
|---|---|---|
| `headlineSmall` | Titular de Auth | 24 |
| `titleLarge` | Título de página en la barra superior | 22 |
| `titleMedium` | Título de sección/dialog | 16 |
| `bodyLarge` | Cuerpo | 16 |
| `bodyMedium` | Fila/subtítulo | 14 |
| `labelLarge` | Botones/acciones | 14 |
| `labelMedium` | Badge/soporte | 12 |
| `labelSmall` | Caption/pistas | 11 |

Los tamaños viven como tokens (`XauxaType`) en `sp` y se respetan las
alturas de línea de M3; el tracking es el estándar del rol (sin
`LetterSpacingWide` en cuerpo).

## Escalado de fuente del sistema

Todos los tamaños están en `sp`: el escalado de fuente del usuario se
aplica sin configuración adicional (§11). Las pantallas se prueban a
100/150/200 % sin recortar contenido esencial ni acciones primarias.

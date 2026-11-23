# Ficha de componente: Acción rápida (`AqQuickAction`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** Patrón de tarjeta de acción rápida de Moon Design (`MoonSizes`).
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqQuickAction.kt`.

## Origen
Tarjeta presionable con glifo arriba y etiqueta centrada debajo. Usada en la rejilla de acciones de Inicio (S01).

## Anatomía
Glifo (24 dp = `AqSize.Xs`) arriba, etiqueta centrada debajo (máximo 2 líneas). Todo centrado horizontalmente.

## Geometría
- Icono: 24 dp (`AqSize.Xs`), tinte `TextPrimary`.
- Etiqueta: hasta 2 líneas, `TextAlign.Center`, estilo `Label`.
- Relleno: vertical `AqSize.X3s` (12 dp), horizontal `AqSize.X4s` (8 dp).
- La celda acepta el ancho del padre: el llamador pasa `Modifier.weight(1f)` cuando las acciones comparten una fila (p. ej. 3 en Inicio). Se eliminó el ancho fijo de 48 dp que forzaba a la etiqueta a envolverse una letra por línea (S1).
- Objetivo táctil ≥ 48 dp (D-02) mediante `defaultMinSize(minHeight = 48 dp)`.

## Estados
Habilitado / deshabilitado (tinte `OnDisabled`).

## Accesibilidad
`Modifier.clickable(role = Role.Button)`, `contentDescription` con la etiqueta, sin Material (se compone con piezas Moon + tokens).

## Uso en pantalla
Disponible; rejilla de acciones de Inicio (Traer un QR, Registrar actividad, Crear contexto).

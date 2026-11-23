# Ficha de componente: Celda de lista (`AqMenuItem`)

**Estado:** Disponible — portado de Moon Design.
**Fuente:** `moon_design` 1.1.0, `lib/src/widgets/menu_item` (catálogo §4).
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqMenuItem.kt`.

## Origen
Puerto de `MoonMenuItem`. Patrón de composición: `AqMenuItem` + `AqAvatar` / `AqTag`.

## Anatomía
`leading` (avatar o glifo) a la izquierda, bloque de texto en el centro (título + subtítulo opcional) y `trailing` (glifo, chevron o slot) a la derecha. El título es una línea; el subtítulo es la segunda línea.

## Geometría (Moon § MenuItem)
- Relleno: 12 dp en los cuatro lados (`AqLayout.CellPadding`).
- Hueco entre bloques (leading / texto / trailing): 8 dp (`AqLayout.InGroup`).
- Hueco título → subtítulo: 4 dp (`AqSize.X5s`).
- Alto mínimo visual: 40 dp; objetivo táctil ≥ 48 dp (D-02).
- El bloque de texto se expande (`weight(1f, fill = true)`); el `trailing` queda con `wrapContentWidth` (sin `Spacer` de peso).

## Estados
Reposo, presionado, seleccionado (fondo `SelectionSurface`), deshabilitado.

## Accesibilidad
Rol `Role.Button`, `contentDescription` con título + subtítulo, anuncio de estado seleccionado.

## Uso en pantalla
Disponible; filas de destino, contexto, actividades y bandeja. Reemplaza al retirado `AqListRow`.

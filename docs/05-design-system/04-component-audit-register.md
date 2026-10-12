# Auditoría de componentes — decisiones y estado actual

> La historia de auditorías vive en `git log`. Este documento fija las
> DECISIONES vigentes (ADR-0012, diseño convencional sobre Material 3)
> sobre cada componente del design system Xauxa.

## Criterios de clasificación

- **Conservar:** cumple el contrato visual y sus estados/accesibilidad están cubiertos.
- **Adaptar:** función válida; pasa a ser envoltorio fino del componente M3 equivalente.
- **Reemplazar:** existe la necesidad; la implementación cambia de soporte.
- **Retirar:** sin uso de producto ni valor de accesibilidad; eliminar sin romper compatibilidad.
- **Sin uso:** sin consumidores en producción; decidir adoptar o retirar.

## Decisiones por componente (V1.2, ADR-0012)

| Componente | Decisión |
|---|---|
| `XauxaPrimaryButton` | **Adaptar**: envoltorio de `Button` M3 (≥48 dp, estado ocupado con etiqueta). |
| `XauxaSecondaryButton` | **Adaptar**: envoltorio de `OutlinedButton` M3. |
| `XauxaTextAction` | **Adaptar**: envoltorio de `TextButton` M3 para acciones compactas. |
| `XauxaDangerButton` | **Adaptar**: `Button` M3 con tokens danger (usado por `XauxaDialog(destructive)`). |
| `XauxaIconButton` | **Adaptar**: envoltorio de `IconButton` M3. |
| `XauxaFavoriteToggle` | **Reemplazar**: el favorito por fila pasa a `IconToggleButton` M3 (estrella con estado seleccionado y descripción que cambia con el estado). |
| `XauxaFavoriteIndicator` | **Retirar**: sustituido por el toggle anterior; la excepción E-02 se retira con él. |
| `XauxaTextInput` | **Adaptar**: envoltorio de `OutlinedTextField` (etiqueta, apoyo, error icono+texto, borde reposo `borderControl` ≥3:1, IME/autofill, slot mostrar/ocultar contraseña); la excepción E-06 se retira con el campo plano. |
| `XauxaSearchBar` | **Adaptar**: `OutlinedTextField` con glifo de búsqueda y limpieza etiquetada. |
| `XauxaSearchTrigger` | **Conservar**: disparador de nodo único (rol Button) sobre la barra decorativa. |
| `XauxaFilterChip` | **Adaptar**: `FilterChip` M3 con tokens (borde de control ≥3:1). |
| `XauxaListRow` | **Adaptar**: `ListItem` M3 (título/subtítulo/meta/trailing; separación por divisor o espacio; punto de acento solo como identidad de contexto, E-05). |
| `XauxaTile` | **Retirar**: las filas/tarjetas de datos pasan a `ListItem`/superficies M3. |
| `XauxaMetroTile`, `XauxaTileGrid`, `XauxaLiveTile` | **Retirar**: sin tiles en producción (`TILES_EN_INICIO = false`). |
| `XauxaPageTitle` | **Retirar**: el título de página pasa a la barra superior (`titleLarge` M3) o al titular de Auth (`headlineSmall`). |
| `XauxaSectionHeader` | **Adaptar**: encabezado de sección estándar (`titleSmall`/`labelLarge`, color estándar); sin marcador subrayado. El acento de contexto en S06 tiñe los encabezados de SU superficie. |
| `XauxaPivot` | **Adaptar**: pestañas estándar M3 (scrollable, estado seleccionado, acento de contexto legible como texto). |
| `XauxaAppBar` | **Retirar**: la barra inferior desaparece; se sustituye por `XauxaTopBar` (`TopAppBar` M3) + `XauxaNavBar`/`XauxaNavRail` en raíces. |
| `XauxaCommandBar` | **Retirar**: sin usos de producción; el overflow vive en la barra superior. |
| `XauxaTopBar` | **Alta**: `TopAppBar` M3 pequeña (título a la izquierda, flecha atrás, máx. 2 acciones + menú "…"). |
| `XauxaNavBar` / `XauxaNavRail` | **Alta**: `NavigationBar`/`NavigationRail` M3 con 4 destinos raíz, etiqueta + icono siempre. |
| `XauxaScreenScaffold` | **Adaptar**: `Scaffold` M3 (`topBar`, `bottomBar`, `snackbarHost`, `floatingActionButton`, `contentWindowInsets`). |
| `XauxaDialog` | **Adaptar**: `AlertDialog` M3 (mismos contratos: `destructive`, foco inicial conservativo, `onDismissRequest`). |
| `XauxaDialogList` | **Conservar**: lista scrolleable dentro del diálogo. |
| `XauxaStatusBanner` | **Conservar** para estados persistentes importantes, dentro del Scaffold; insets resueltos por el contenedor. |
| `XauxaToast` | **Retirar**: el feedback transitorio pasa al `Snackbar` del `SnackbarHostState`. |
| `XauxaInlineResult` | **Retirar**: sin usos; cubierto por banner/snackbar. |
| `XauxaErrorPage` | **Retirar**: spec §10 prohíbe ErrorScreen global. |
| `XauxaLoading` | **Adaptar**: indicadores de progreso M3 con mensaje. |
| `XauxaDotProgress` | **Retirar**: sustituido por `CircularProgressIndicator`/`LinearProgressIndicator` M3. |
| `XauxaSkeleton` | **Conservar**: placeholder de carga estático. |
| `XauxaEmptyState` | **Conservar**: título, subtítulo y acción opcional. |
| `XauxaLoadMoreFooter` | **Conservar**: paginación con copy del llamador. |
| `XauxaBadge` | **Conservar**: etiqueta semántica de estado con texto (no interactiva). |
| `XauxaCategoryChip` | **Conservar**: chip estático de clasificación (no interactiva). |
| `XauxaHeroCard`, `XauxaStatBlock`, `XauxaTileHeader` | **Retirar**: sin usos de producción. |
| `XauxaFileUpload` | **Retirar**: sin usos; la adquisición son los selectores de plataforma (S02). |
| `XauxaScannerViewport` / `XauxaScannerFrame` | **Conservar**: lenguaje congelado de S03 (excepción E-03). |
| `XauxaQrPreview` | **Conservar**: expect/actual por plataforma; lienzo claro garantizado. |
| `XauxaFeedbackEvent` | **Conservar**: háptica + anuncio accesible por evento; la capa de feedback publica además en el `SnackbarHostState`. |
| `XauxaSection` | **Conservar**: encabezado + contenido. |
| `XauxaTurnstileNav` | **Retirar**: transición estándar corta (fundido + desplazamiento pequeño, tokens `XauxaMotion`). |
| Tokens `BreakpointCompact`/`BreakpointMedium` | **Retirar**: la adaptación de ancho pasa a `WindowSizeClass`. |
| Token `ContentMaxWidth` | **Conservar**: límite de contenido en ancho expandido. |
| Tokens de rejilla (`TileUnit`, `TileWideHeight`, `TileGap`, `Catalog*`) | **Retirar** con los tiles (el catálogo del lab consume anchos propios de M3). |
| Tokens `Marker`/`BorderStrong` | `Marker` se retira con los marcadores; `BorderStrong` se conserva (encuadre E-03). |

## Contratos de componente

- `XauxaStatusBanner`: `dismissLabel` (requerido con `onDismiss`); icono por tono; liveRegion.
- `XauxaTopBar`: flecha atrás con descripción accesible; acciones con contentDescription; overflow con etiqueta del llamador.
- `XauxaNavBar`: destino activo con etiqueta + icono (nunca solo icono); el cambio de destino conserva la raíz del back stack.
- `XauxaLoadMoreFooter`: labels del llamador.
- `XauxaSearchBar`: `label`/`placeholder` sin defaults.
- `XauxaDialog`: `destructive` (botón peligro + foco inicial en Cancelar).
- `XauxaListRow`: identidad de contexto solo como dato real (E-05); `selected` con semántica.
- `XauxaTextInput`: etiqueta como nombre accesible; error liveRegion + semántica del campo; disabled distinguible.
- `XauxaSearchTrigger`: disparador de búsqueda de nodo único.
- `XauxaFeedbackEvent`: háptica + liveRegion por evento.

## Excepciones de chrome (nivel pantalla)

El registro normativo de excepciones vive en el contrato §12; el gate
`verifyDesignSystemCompliance` compara sus IDs E-xx con su propio registro
y falla si no coinciden. Esta auditoría y el contrato deben leerse juntos:

- **E-07 — AuthScreen sin barra de navegación**: la autenticación no es un
  destino raíz de sesión y su única acción legítima es el inicio/registro;
  el botón principal del cuerpo es la acción principal. Retirada cuando
  producto defina navegación o acciones secundarias en Auth (nuevo ADR).

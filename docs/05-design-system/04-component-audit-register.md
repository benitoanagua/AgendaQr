# Auditoría de componentes — decisiones y estado actual

> La historia de auditorías vive en `git log`. Este documento fija las
> DECISIONES vigentes sobre cada componente del design system Xauxa.

## Criterios de clasificación

- **Conservar:** cumple el contrato visual y sus estados/accesibilidad están cubiertos.
- **Adaptar:** función válida, presentación o API necesita alinearse al contrato.
- **Reemplazar:** existe la necesidad pero la implementación no puede cumplirla.
- **Retirar:** sin uso de producto ni valor de accesibilidad; eliminar sin romper compatibilidad.
- **Sin uso:** sin consumidores en producción; decidir adoptar o retirar.

## Componentes sin uso de producción

| Componente | Decisión |
|---|---|
| `XauxaErrorPage` | **Retirar**: spec §10 prohíbe ErrorScreen global; banners contextuales lo cubren. |
| `XauxaToast` | **Sin uso**: feedback transitorio ya cubierto por banners liveRegion + XauxaFeedbackEvent. |
| `XauxaInlineResult` | **Sin uso**: misma razón que Toast. |
| `XauxaSettingRow` | **Retirado** (2026-10-10): sin pantalla de ajustes en V1; código, catálogo del laboratorio y tests eliminados; la excepción E-04 del contrato §12 se retiró con él. |
| `XauxaIconButton` | **Solo interno** (vía Toast/CommandBar); decisión conjunta con Toast. |
| `XauxaFavoriteToggle` | **Conservar la acción de texto en filas** (§11); toggle para futura pantalla de favoritos. |
| `XauxaDangerButton` | **En uso** vía `XauxaDialog(destructive)`. |
| Tokens `Breakpoint*`, `ContentMaxWidth` | **Lab-only**: retirar junto con el laboratorio o migrarlo. |

## Contratos de componente

- `XauxaStatusBanner`: +`dismissLabel` (requerido con `onDismiss`); icono por tono; sin borde de reposo.
- `XauxaAppBar`: `backLabel` requerido; +`overflowLabel` (requerido con overflow); acción primaria distinguible.
- `XauxaLoadMoreFooter`: +labels del llamador.
- `XauxaSearchBar`: `label`/`placeholder` sin defaults.
- `XauxaToast`: +`dismissDescription`; liveRegion.
- `XauxaFavoriteToggle`: `contentDescription` requerido.
- `XauxaDialog`: +`destructive` (botón peligro + foco inicial en Cancelar).
- `XauxaListRow`: barra lateral solo con tono/acento; +`selected` con semántica.
- `XauxaTextInput`: etiqueta como nombre accesible; error liveRegion; disabled distinguible.
- `XauxaDotProgress`: sustituye a CircularProgressIndicator (indicador de puntos plano).
- `XauxaMetroTile`: altura mínima; etiqueta escalable; overlay de pulsación opuesto.
- `XauxaSearchTrigger`: disparador de búsqueda de nodo único.
- `XauxaFeedbackEvent`: háptica + liveRegion por evento.

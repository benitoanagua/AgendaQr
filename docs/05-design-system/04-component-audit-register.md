# Auditoría de componentes — registro de decisiones

Este registro inicia la auditoría del inventario que ya declara el laboratorio de `core:ui`. No equivale a una inspección visual ni a una validación de cada implementación. Las decisiones definitivas se anotarán tras revisar el código fuente de cada componente y sus usos en pantallas reales.

## Criterios de clasificación

- **Conservar:** ya cumple el contrato visual unificado y sus estados/accesibilidad están cubiertos.
- **Adaptar:** aporta una función válida, pero su presentación o API necesita alinearse al contrato.
- **Reemplazar:** existe una necesidad real, pero la implementación actual no puede cumplirla sin complejidad o duplicación excesiva.
- **Retirar:** no tiene uso de producto ni valor de accesibilidad/plataforma y puede eliminarse sin romper compatibilidad necesaria.
- **Pendiente de inspección:** no se decide por el nombre ni por la documentación; requiere leer implementación y consumidores.

## Inventario inicial del laboratorio

| Área | Componentes/tokens existentes | Estado de auditoría |
|---|---|---|
| Fundamentos | `XauxaColor`, `XauxaSpacing`, `XauxaMetrics`, `XauxaType`, `XauxaMotion`, `XauxaFocus` | Pendiente de inspección de definiciones, uso y contraste/escala |
| Superficies | `XauxaScreen`, `XauxaSection`, `XauxaTile`, `XauxaHeroCard`, `XauxaTileHeader`, `XauxaDialog` | Pendiente de inspección de implementación y consumidores |
| Acciones y entrada | `XauxaPrimaryButton`, `XauxaSecondaryButton`, `XauxaDangerButton`, `XauxaTextAction`, `XauxaIconButton`, `XauxaFilterChip`, `XauxaTextInput`, `XauxaSearchBar`, `XauxaSettingRow`, `XauxaFavoriteToggle` | Pendiente de inspección de estados, semántica, targets y usos |
| Feedback | `XauxaToast`, `XauxaStatusBanner`, `XauxaInlineResult`, `XauxaLoading`, `XauxaSkeleton`, `XauxaEmptyState`, `XauxaErrorPage`, `XauxaLoadMoreFooter` | Pendiente de inspección de estados y casos de producto |
| Datos | `XauxaListRow`, `XauxaStatBlock`, `XauxaBadge` | Pendiente de inspección de densidad, significado y accesibilidad |

## Secuencia de revisión

1. Leer las implementaciones y sus APIs públicas; detectar duplicados y estilos locales.
2. Buscar consumidores de producción y verificar que cada pieza resuelve una necesidad real de AgendaQr.
3. Registrar por componente una decisión, la regla que la justifica, los consumidores afectados y los cambios compatibles necesarios.
4. Migrar fundamentos compartidos antes de cambiar pantallas, para evitar estilos alternativos temporales.
5. Actualizar el catálogo del laboratorio junto con cada cambio real, sin marcar como verificado lo que aún no se haya probado.

## Restricción de alcance

La auditoría visual no autoriza cambiar reglas de negocio, modelos, repositorios, navegación funcional ni persistencia. Si una mejora de interfaz requiere alterar un flujo de dominio, se registra como decisión aparte y no se mezcla con la migración visual.

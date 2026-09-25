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

## Hallazgos de la primera inspección de código

Revisión estática inicial de `XauxaComponents.kt`, `XauxaExtendedComponents.kt`, `LabCatalogPane.kt` y búsquedas de consumidores en `feature/destinations/presentation`. Esto confirma la presencia y algunos contratos de API, pero no sustituye la inspección de todos los componentes, la revisión visual en ejecución ni las pruebas.

| Elemento revisado | Hallazgo de código | Decisión provisional |
|---|---|---|
| `XauxaScreen`, `XauxaSection`, `XauxaTile` | Composición compartida y superficies rectangulares; `XauxaTile` admite acción opcional y aplica anillo de foco propio. | Conservar como base; verificar contraste/foco y composición en pantalla al validar visualmente. |
| Botones primario/secundario/peligro y acción de texto | Usan altura mínima semántica, formas rectangulares y tokens de color/tipo. Los textos de botones se convierten incondicionalmente a mayúsculas. | Adaptar: preservar la capitalización editorial recibida, salvo patrón de etiqueta que justifique mayúsculas. Revisar en la migración de acciones para evitar cambios de contenido inesperados. |
| `XauxaIconButton` y filas interactivas | Declaran rol de botón, interacción/foco y descripción accesible en el botón de icono. | Conservar provisionalmente; confirmar navegación por teclado y tamaño táctil efectivo en plataformas. |
| `XauxaFilterChip` / `XauxaCategoryChip` | Hay dos piezas con semánticas distintas: filtro interactivo y etiqueta estática. | Conservar ambas; mantener diferenciación explícita en catálogo y usos. |
| `XauxaTextInput` | API ofrece etiqueta, error, modo de una línea y mínimo de líneas; implementación basada en `OutlinedTextField`. | Inspección parcial; revisar estados de ayuda/error, foco y accesibilidad en la siguiente pasada. |
| `XauxaMetrics.CatalogCardMinWidth` | El ancho mínimo de tarjeta del laboratorio ya está centralizado en token; el consumidor usa ese token en la cuadrícula adaptativa. | Conservar; no sustituir por literal local. |

### Consumidores de producción encontrados

La búsqueda del símbolo `XauxaPrimaryButton` encontró usos en autenticación, destinos, detalle/edición, importación y operaciones. Estos consumidores indican que el cambio de capitalización es transversal y debe hacerse con revisión de capturas/etiquetas y pruebas al final; no se debe alterar la lógica de callbacks ni los textos de negocio durante esa adaptación. La búsqueda no constituye un inventario exhaustivo de todos los componentes ni de todos sus consumidores.

### Siguiente paso

Completar la inspección por grupos (feedback, datos, campos y patrones), registrar excepciones de plataforma y después aplicar cambios compatibles a los componentes compartidos. Las pruebas locales siguen deliberadamente aplazadas hasta la fase final solicitada.

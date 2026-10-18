# Contrato normativo de diseño

**Estado:** Normativo — versión 5.0
**Autoridad:** ADR-0023 y este documento definen el sistema de diseño de
AgendaQr. La especificación UX/UI define intención, flujos y composición por
pantalla. La implementación canónica vive en `core/ui`; las pantallas componen
sus componentes y tokens.

## 1. Principios

1. Los valores, estados, componentes y patrones de interacción siguen TDesign
   Mobile 1.16.2. `tdesign-mobile-vue@1.16.2` es la fuente de referencia; no es
   una dependencia de ejecución.
2. Las reglas de producto, la navegación, la persistencia y el copy funcional
   se conservan según sus contratos vigentes.
3. Android e iOS comparten intención y jerarquía. Los servicios nativos
   conservan las convenciones propias de cada plataforma.
4. El estado se comunica con texto o semántica además del color.
5. Los cambios de diseño se implementan en `core/ui`; las pantallas no definen
   valores visuales locales.
6. El sistema admite fuente del sistema escalada hasta 200 % sin recortar
   contenido esencial ni acciones.

## 2. Capas de implementación

| Capa | Ubicación | Responsabilidad |
|---|---|---|
| Tokens | `core/ui/.../theme/AmTokens.kt` | Paletas, roles semánticos, espaciado, formas, métricas, tipografía, movimiento y elevación. |
| Tema | `core/ui/.../theme/AmTheme.kt` | Adapta los roles semánticos al tema de componentes base. |
| Componentes | `core/ui/.../components/` | Acciones, campos, listas, estados, diálogo, feedback, medios e iconografía. |
| Patrones | `AmScaffold`, chrome y componentes de lista | Navegación, barras fijas, grupos y composición adaptable. |
| Pantallas | `feature/destinations/presentation` | Componen patrones y exponen el comportamiento definido por producto. |
| Pruebas | `core/ui` y `feature/destinations/presentation` | Tokens, contraste, interacción, accesibilidad, layout y regresión visual. |

Compose Material 3 se usa como infraestructura de componentes base; ningún
componente hereda su estilo.

## 3. Reglas ejecutables

`verifyDesignSystemCompliance` aplica estas reglas:

- Los literales de color, tamaño, forma y elevación pertenecen a la capa de
  tokens. Los vectores de iconos TDesign pueden declarar su viewport intrínseco
  y un color de origen monocromo; el componente de icono aplica el tinte
  semántico.
- `MaterialTheme.colorScheme` no es fuente visual fuera de `AmTheme.kt`.
- Los iconos de producción se consumen por `AmIcons` y `AmIcon`.
- Los controles prefabricados de material3 no se importan en `feature/`,
  `shared/` ni `androidApp/`; las pantallas componen los equivalentes de
  `core/ui`.
- El texto de interfaz procede de `AppStrings`; los componentes compartidos
  reciben copy por parámetros.
- La interacción, la indicación, el foco y la semántica de controles viven en
  los componentes del sistema.
- Las animaciones consumen `AmMotion`; la reducción de movimiento elimina la
  transición.
- Los bordes y las sombras de producción requieren una excepción registrada
  tanto aquí como en el gate.

## 4. Color

Los temas claro y oscuro usan las escalas TDesign 1.16.2 de marca, grises,
éxito, advertencia y error. Los componentes consumen roles semánticos, no
índices de escala.

| Rol | Aplicación |
|---|---|
| `background` | Fondo de página. |
| `surface`, `surfaceSecondary`, `surfaceContainer*` | Superficies y niveles de contenedor. |
| `textPrimary`, `textSecondary`, `textTertiary` | Jerarquía de texto. |
| `accent`, `accentText`, `accentContainer`, `onAccent` | Marca, selección y acción primaria. |
| `danger`, `dangerFill`, `dangerContainer`, `onDanger` | Error y acción destructiva. |
| `success`, `successFill`, `successContainer`, `onSuccessFill` | Confirmación y estado correcto. |
| `warning`, `warningFill`, `warningContainer`, `onWarningFill` | Aviso. |
| `info`, `infoContainer` | Información. |
| `focus`, `disabled`, `onDisabled` | Foco y estado inactivo. |
| `border`, `functionalBorder`, `divider` | Contornos, límites funcionales y separadores. |
| `inverseSurface`, `inverseOnSurface`, `scrim`, `qrCanvas` | Feedback inverso, superposición y lienzo del QR. |

El tema claro usa `#0052D9` como marca y `#F3F3F3` como fondo; el tema oscuro
usa `#2667D4` como marca y `#181818` como fondo. Los tests fijan las escalas y
roles completos para ambos temas.

Para texto se elige un par de la paleta TDesign con contraste ≥4.5:1; para
indicadores no textuales, ≥3:1. El lienzo QR es blanco en ambos temas. El color
no comunica por sí solo error, éxito, selección ni deshabilitado.

## 5. Tipografía

- Se usa la fuente del sistema; no se empaquetan familias tipográficas.
- Los valores de tamaño e interlínea se fijan en `AmType` y
  `AmTextStyles`. La escala incluye cuerpo 10/16, 12/20, 14/22 y 16/24;
  títulos 14/22, 16/24, 18/26 y 20/28; titulares 24/32, 28/36 y 36/44;
  display 48/56 y 64/72.
- El texto visible usa un mínimo de 11 sp; el token de 10 sp no se asigna a
  copy visible. El tamaño compacto aplicado es 12 sp.
- Los títulos usan peso 600 cuando corresponde; los importes usan 28/36,
  peso 600 y cifras tabulares.
- Todo texto respeta el escalado de fuente del sistema.

## 6. Espaciado, formas y métricas

- Espaciado: 8, 12, 16, 24, 32, 48 y 80 dp. Se admite 4 dp para separación
  micro interna. El margen de pantalla es 16 dp.
- Radios: 3, 6, 9, 12 y 999 dp; las etiquetas pueden usar 4 dp.
- Targets interactivos: mínimo 48 dp.
- Botones: tamaños visuales 48, 40, 32 y 28 dp.
- Barra superior e inferior: 48 dp; ícono estándar 24 dp.
- Búsqueda y campos: 40 dp visuales, con target interactivo mínimo de 48 dp.
- Contenedor de icono de acción de inicio: 48 dp.
- Icono de Empty: 96 dp; icono de Result: 80 dp.
- Cabecera de grupo: 45 dp; divisor inset: 16 dp.
- Diálogo: ancho de referencia 311 dp, limitado al espacio disponible.
- Contenido en ventanas amplias: ancho máximo 720 dp.

Las métricas se declaran en `AmSpacing`, `AmRadius` y `AmMetrics`; las
pantallas no repiten literales.

## 7. Iconografía

Los glifos de producto proceden de TDesign Icons, convertidos a vectores
Compose en `core/ui`. `AmIcons` ofrece un nombre estable por concepto y
`AmIcon` centraliza tamaño, tinte y semántica. El catálogo no es una
dependencia de ejecución. La atribución MIT figura en
`06-licencias-terceros.md`.

Los iconos decorativos usan descripción nula. Todo icono interactivo tiene
etiqueta visible o una descripción accesible explícita; ningún estado depende
solo del glifo.

## 8. Movimiento

- Easing: `cubic-bezier(0.38, 0, 0.24, 1)`.
- Duraciones: 200, 240 y 280 ms.
- Movimiento reducido: duración cero.
- No se usan escala de pulsación, spring ni movimiento ornamental en bucle.
- La háptica de plataforma y el foco por teclado/D-pad se conservan.

## 9. Componentes y patrones

| Grupo | Componentes | Regla de composición |
|---|---|---|
| Chrome | `AmTopBar`, `AmNavBar`, `AmNavRail`, `AmScreenScaffold` | Título centrado; atrás a la izquierda; acciones y desbordamiento a la derecha; cuatro destinos raíz con etiquetas visibles. |
| Acciones | `AmPrimaryButton`, `AmSecondaryButton`, `AmDangerButton`, `AmTextAction`, `AmBottomAction` | Como máximo una primaria por contexto; el target mide ≥48 dp; acción fija solo cuando existe guardado real. |
| Campos | `AmTextField`, `AmAmountField`, `AmSearchField`, `AmSearchTrigger` | Etiqueta y error asociados; búsqueda con foco visible; la acción de limpiar tiene nombre accesible. |
| Listas | `AmListRow`, `AmListBody`, `AmSectionHeader`, `AmMetaRow` | Las filas relacionadas forman un Cell-group; las filas usan divisores inset y chevron solo cuando navegan. |
| Selección | `AmSegmentedControl`, `AmPivot`, `AmFilterChip`, `AmTonalTag`, `AmBadge` | El estado seleccionado tiene semántica; los tags comunican su tipo con texto. |
| Superficies | `AmCard`, `AmTappableCard`, `AmHairline` | Tarjetas para contenido agrupado; las filas no se envuelven en tarjetas individuales. |
| Estados | `AmEmptyState`, `AmLoading`, `AmSkeleton`, `AmStatusBanner`, `AmStatusPill` | Estados de error explican qué ocurrió, qué pasó con los datos y cómo recuperarse. |
| Feedback | `AmSnackbarHost`, `AmFeedbackEvent` | Confirmación efímera con texto; los avisos persistentes usan el host de estado. |
| Superposición | `AmDialog`, `AmDialogList` | Título y acciones legibles; las opciones se presentan como lista dentro del diálogo. |
| Medios | `AmQrPreview`, `AmScannerFrame`, `AmMediaFrame` | QR cuadrado en lienzo blanco; fallos de vista previa tienen estado explícito. |

### Jerarquía de acciones

| Nivel | Componente | Uso |
|---|---|---|
| Primaria | `AmPrimaryButton` / `AmBottomAction` | Objetivo principal existente; como máximo una por contexto. |
| Secundaria | `AmSecondaryButton` | Alternativa importante que no compite con la primaria. |
| Terciaria | `AmTextAction` | Acción auxiliar o de navegación. |
| Destructiva | `AmDangerButton` o acción de peligro | Requiere confirmación y etiqueta explícita de consecuencia. |

La búsqueda, lectura o selección puede no tener una acción primaria.

## 10. Estados

Los componentes resuelven estados normal, presionado, enfocado, seleccionado,
error, deshabilitado y cargando. Cada estado incluye una señal de texto o
semántica además del tratamiento cromático. Un proceso de guardado no mueve
acciones ni cambia su ancho; los errores recuperables conservan disponibles las
acciones reales de reintento o salida.

## 11. Accesibilidad y adaptación

- Texto normal: contraste ≥4.5:1; contornos funcionales y foco: ≥3:1.
- Target táctil: mínimo 48 dp, aunque la superficie visual sea menor.
- El orden de lectura, las etiquetas y los roles describen la interacción.
- Los errores de campo combinan icono, texto y semántica de error.
- Los destinos raíz tienen etiqueta e icono.
- El escalado a 200 % no recorta contenido esencial ni acciones.
- Los layouts se comprueban a 320 y 360 dp en ambos temas.
- Las áreas seguras, IME, foco, permisos y servicios nativos respetan cada
  plataforma.

## 12. Registro de excepciones del gate

El conjunto de IDs de esta tabla debe coincidir exactamente con
`exceptionRegistry` en `build-logic/src/main/kotlin/agendaqr.design-system.gradle.kts`.

| ID | Uso autorizado | Registro |
|---|---|---|
| E-01 | Borde de botón secundario en `AmButtons.kt`. | `border:AmButtons.kt` |
| E-02 | Borde funcional de badge en `AmLists.kt`. | `border:AmLists.kt` |
| E-03 | Borde de grupos y tarjetas de contenido en `AmLists.kt`. | `border:AmLists.kt` |
| E-04 | Borde de foco del campo de búsqueda en `AmInputs.kt`. | `border:AmInputs.kt` |
| E-05 | Elevación de las superficies de lista en `AmLists.kt`. | `shadow:AmLists.kt` |
| E-06 | Elevación de botones en `AmButtons.kt`. | `shadow:AmButtons.kt` |

Toda excepción nueva requiere justificación, prueba y actualización simultánea
del contrato y del gate.

## 13. Verificación

| Propiedad | Verificación |
|---|---|
| Escalas y roles de color | `AmSchemeTest` |
| Contraste | `AmContrastPairsTest`, `AmAccentTest` |
| Tipografía y escalado | `AmTypographyTest`, `AmUiPolishContractTest`, `FontScaleContractTest` |
| Interacción y foco | `AmAccessibilityContractTest`, `ScreensA11yCoverageTest` |
| Touch targets | `verifyControlMinSize` |
| Literales, controles y excepciones | `verifyDesignSystemCompliance`, `verifyDesignSystemFixtures` |
| Layout | `LayoutReferenceTest`, `LayoutReferenceExtraTest` (ADR-0007) |
| Documentación | `verifyDocs` |

## 14. Gobernanza

1. Un cambio de valor visual actualiza el token, sus pruebas y capturas.
2. Un componente nuevo necesita consumidor real o propósito contractual,
   contrato accesible y pruebas de estado.
3. Un cambio de composición mantiene la intención y las reglas de producto de
   su pantalla.
4. Las excepciones se registran en el contrato y en el gate dentro del mismo
   cambio.
5. Una decisión estructural del sistema se documenta en ADR.

## 15. Desviaciones vigentes

| Desviación | Resolución |
|---|---|
| Identidad de contextos con 12 tonos deterministas. | Se conserva `AmAccents` y el hash FNV-1a; las pruebas verifican los pares legibles. |
| Algunos tonos oficiales no alcanzan el contraste requerido. | Se usa el siguiente escalón de la familia TDesign con contraste suficiente. |
| Controles de menor tamaño visual que el target accesible. | La superficie conserva la geometría oficial y el target llega a 48 dp. |
| `body-extraSmall` de 10 sp. | No se usa en texto visible; el tamaño compacto visible es 12 sp. |
| Navegación lateral para ventanas amplias. | `AmNavRail` mantiene los cuatro destinos con los tokens del sistema. |
| Sombras CSS con múltiples capas. | Compose usa la capa dominante declarada en los tokens. |
| Selector de contexto, fecha, hora y menú de desbordamiento. | Se presentan mediante `AmDialog` y listas; la composición permite interacción verificable bajo Robolectric. |
| Fuentes tipográficas de plataforma. | Se usa la fuente del sistema y no se distribuyen archivos de fuente. |

## 16. Composición por pantalla

| Superficie | Componentes y patrón |
|---|---|
| Inicio | Barra superior, búsqueda de entrada, tres acciones de creación, filtros, favoritos y Cell-group de destinos. Empty no duplica acciones. |
| Detalle de destino | Barra con atrás y desbordamiento; vista QR; metadatos agrupados; acción fija para mostrar el QR; compartir como secundaria; confirmación para eliminar. |
| Añadir | Cell-group ordenado por galería, archivo compatible y cámara; privacidad junto a la entrada de cámara; ayuda de compartir solo en plataformas compatibles. |
| Cámara | Vista previa nativa, marco de lectura y controles accesibles de salida. |
| Buscar y resultados | Search, grupos por tipo, Cell-group de resultados y Empty para consultas sin resultados. |
| Contextos | Lista de contextos en Cell-group; detalle con pestañas QR, actividades y comprobantes. |
| Registrar actividad | Form por grupos, control segmentado para tipo, campos para importe y entidad, selección de fecha/hora, acceso al selector de contexto y guardado fijo. |
| Seleccionar contexto | Diálogo con lista de opciones, creación integrada y salida explícita que conserva el draft. |
| Revisar QR | Vista previa cuadrada, evidencia interpretada, avisos y acción fija para guardar. |
| Revisar comprobante | Vista previa y datos agrupados; asociar como primaria y guardar sin asociar como secundaria. |
| Resolver ambigüedad | Pregunta única, grupo de opciones mutuamente excluyentes y continuación habilitada tras seleccionar. |
| Resultado de importación | Estado final, resumen, grupos de elementos con estado textual, guardado reconocido fijo y revisión pendiente como secundaria. |
| Actividades y detalle | Búsqueda y filtros existentes; filas agrupadas; importe tabular y metadatos con estado textual. |
| Cuenta y respaldo | Estado de cuenta en grupos; nube opcional; formulario de cuenta con acción primaria y recuperación explícita. |
| Acceso local | Contenido centrado, campos numéricos, errores asociados al campo y acción fija para guardar o desbloquear. |

La composición no añade acciones, gestos, datos ni transiciones de dominio que
no pertenezcan al flujo correspondiente.

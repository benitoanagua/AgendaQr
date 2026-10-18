# Contrato normativo de diseño

**Estado:** Normativo — versión 6.0
**Autoridad:** ADR-0023 y este documento definen el sistema de diseño de
AgendaQr. La especificación UX/UI define intención, flujos y composición por
pantalla. La implementación canónica vive en `core/ui`; las pantallas componen
sus componentes y tokens.

## 1. Principios

1. Los valores, estados, componentes y patrones de interacción siguen
   `tdesign_flutter@0.2.7`. Esa es la fuente de referencia y no es una
   dependencia de ejecución.
2. Cuando cualquier otra fuente contradiga a `tdesign_flutter@0.2.7`, prevalece
   el código de `0.2.7`: tokens en
   `lib/src/theme/td_default_theme.dart` y componentes en `lib/src/components/`.
3. Las reglas de producto, la navegación, la persistencia y el copy funcional
   se conservan según sus contratos vigentes.
4. Android e iOS comparten intención y jerarquía. Los servicios nativos
   conservan las convenciones propias de cada plataforma.
5. El estado se comunica con texto o semántica además del color.
6. Los cambios de diseño se implementan en `core/ui`; las pantallas no definen
   valores visuales locales.
7. El sistema admite fuente del sistema escalada hasta 200 % sin recortar
   contenido esencial ni acciones.

## 2. Capas de implementación

| Capa | Ubicación | Responsabilidad |
|---|---|---|
| Tokens | `core/ui/.../theme/AmTokens.kt` | Paletas, roles semánticos, espaciado, formas, métricas, tipografía, movimiento y elevación. |
| Tema | `core/ui/.../theme/AmTheme.kt` | Adapta los roles semánticos al tema de componentes base. |
| Componentes | `core/ui/.../components/` | Acciones, campos, listas, estados, diálogo, feedback, medios e iconografía. |
| Patrones | `AmScaffold`, chrome y componentes de lista | Navegación, barras fijas, grupos y composición adaptable. |
| Pantallas | `feature/destinations/presentation` | Componen patrones y exponen el comportamiento definido por producto. |
| Pruebas | `core/ui` y `feature/destinations/presentation` | Tokens, contraste, interacción, accesibilidad y layout. |

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

Los temas claro y oscuro usan las escalas de `td_default_theme.dart`
(`default` y `defaultDark`): marca, grises, éxito, advertencia y error. Los
componentes consumen roles semánticos, no índices de escala.

### 4.1 Escalas de la fuente

| Escala | Tema claro | Tema oscuro |
|---|---|---|
| `brandColor1..10` | `#f2f3ff`, `#d9e1ff`, `#b5c7ff`, `#8eabff`, `#618dff`, `#366ef4`, `#0052d9`, `#003cab`, `#002a7c`, `#001a57` | `#1b2f51`, `#173463`, `#143975`, `#103d88`, `#0d429a`, `#054bbe`, `#2667d4`, `#4582e6`, `#699ef5`, `#96bbf8` |
| `grayColor1..14` | `#f3f3f3`, `#eeeeee`, `#e8e8e8`, `#dddddd`, `#c6c6c6`, `#a6a6a6`, `#8b8b8b`, `#777777`, `#5e5e5e`, `#4b4b4b`, `#393939`, `#2c2c2c`, `#242424`, `#181818` | idéntica |
| `errorColor1..10` | `#fff0ed`, `#ffd8d2`, `#ffb9b0`, `#ff9285`, `#f6685d`, `#d54941`, `#ad352f`, `#881f1c`, `#68070a`, `#490002` | `#472324`, `#5e2a2d`, `#703439`, `#83383e`, `#a03f46`, `#c64751`, `#de6670`, `#ec888e`, `#edb1b6`, `#eeced0` |
| `warningColor1..10` | `#fff1e9`, `#ffd9c2`, `#ffb98c`, `#fa9550`, `#e37318`, `#be5a00`, `#954500`, `#713300`, `#532300`, `#3b1700` | `#4f2a1d`, `#582f21`, `#733c23`, `#a75d2b`, `#cf6e2d`, `#dc7633`, `#e8935c`, `#ecbf91`, `#eed7bf`, `#f3e9dc` |
| `successColor1..10` | `#e3f9e9`, `#c6f3d7`, `#92dab2`, `#56c08d`, `#2ba471`, `#008858`, `#006c45`, `#005334`, `#003b23`, `#002515` | `#193a2a`, `#1a4230`, `#17533d`, `#0d7a55`, `#059465`, `#43af8a`, `#46bf96`, `#80d2b6`, `#b4e1d3`, `#deede8` |
| Texto gris (`fontGyColor1..4`) | `#E5000000`, `#99000000`, `#66000000`, `#42000000` | idéntica |
| Texto blanco (`fontWhColor1..4`) | `#ffffff`, `#8CFFFFFF`, `#59FFFFFF`, `#38FFFFFF` | `#E5FFFFFF`, `#8CFFFFFF`, `#59FFFFFF`, `#38FFFFFF` |

### 4.2 Correspondencia de roles

La tabla siguiente fija la clave de la fuente que alimenta cada rol de
`AmColor`. Una entrada `D-02` indica que el valor vigente es el escalón
siguiente de la misma familia que cumple el umbral; el valor exacto queda
fijado por `AmSchemeTest`.

| Rol AgendaQr | Clave o paso de la fuente (claro) | Clave o paso de la fuente (oscuro) |
|---|---|---|
| `background` | `bgColorPage` (`gray2` `#EEEEEE`) | `bgColorPage` (`gray14` `#181818`) |
| `surface` | `bgColorContainer` (`#ffffff`) | `bgColorContainer` (`gray13` `#242424`) |
| `surfaceSecondary` | `bgColorSecondaryContainer` (`gray1` `#F3F3F3`) | `bgColorSecondaryContainer` (`gray12` `#2C2C2C`) |
| `surfaceContainer` | `bgColorPage` (`gray2`) | `bgColorPage` (`gray14`) |
| `surfaceContainerLow` | `bgColorContainer` (`#ffffff`) | `bgColorContainer` (`gray13`) |
| `surfaceContainerHigh` | `bgColorComponent` (`gray3` `#E8E8E8`) | `bgColorComponent` (`gray11` `#393939`) |
| `surfaceContainerHighest` | `bgColorSecondaryComponent` (`gray4` `#DDDDDD`) | `bgColorSecondaryComponent` (`gray10` `#4B4B4B`) |
| `surfaceActive` | `bgColorContainerActive` (`gray3`) | `bgColorSecondaryContainer` (`gray12`) por D-02 |
| `componentActive` | `bgColorComponentActive` (`gray6` `#A6A6A6`) | `bgColorComponentActive` (`gray9` `#5E5E5E`) |
| `border` | `componentBorderColor` (`gray4`) | `componentBorderColor` (`gray9`) |
| `functionalBorder` | `gray7` `#8B8B8B` por D-02 | `gray8` `#777777` por D-02 |
| `divider` | `gray3` `#E8E8E8` | `gray11` `#393939` |
| `textPrimary` | `textColorPrimary` (`fontGyColor1` `#E5000000`) | `textColorPrimary` (`fontWhColor1` `#E5FFFFFF`) |
| `textSecondary` | `textColorSecondary` (`fontGyColor2` `#99000000`) | `textColorSecondary` (`fontWhColor2` `#8CFFFFFF`) |
| `textPlaceholder` | `gray9` `#5E5E5E` por D-02 | `fontWhColor2` `#8CFFFFFF` por D-02 |
| `textTertiary` | `gray9` `#5E5E5E` (la fuente usa `fontGyColor4` con alfa) | `gray6` `#A6A6A6` (la fuente usa `fontWhColor4` con alfa) |
| `accent` | `brandNormalColor` (`brandColor7` `#0052D9`) | `brandColor7` `#2667D4` por D-02 |
| `accentActive` | `brandActiveColor` (`brandColor8`) | `brandActiveColor` (`brandColor9`) |
| `accentActiveContent` | texto anti (`#ffffff`) | `brandLightColor` (`brandColor1`) |
| `accentDisabled` | `brandDisabledColor` (`brandColor3`) | `brandDisabledColor` (`brandColor3`) |
| `accentFocus` | `brandFocusColor` (`brandColor2` `#D9E1FF`) | `brandFocusColor` (`brandColor2` `#173463`) |
| `accentLightActive` | `brandColorLightHover` (`brandColor2`) | `brandColorLightHover` (`brandColor2`) |
| `accentText` | `textColorBrand` (`brandColor7`) | `brandColor9` `#699EF5` por D-02 |
| `accentContainer` | `brandLightColor` (`brandColor1`) | `brandLightColor` (`brandColor1`) |
| `danger` | `errorColor7` `#AD352F` por D-02 | `errorColor8` `#EC888E` por D-02 |
| `dangerFill` | `errorColor7` `#AD352F` por D-02 | `errorColor6` `#C64751` por D-02 |
| `dangerActive` | `errorColor8` `#881F1C` | `errorColor6` `#C64751` |
| `dangerDisabled` | `errorDisabledColor` (`errorColor3`) | `errorDisabledColor` (`errorColor3`) |
| `dangerContainer` | `errorLightColor` (`errorColor1`) | `errorLightColor` (`errorColor1`) |
| `success` | `successColor7` `#006C45` por D-02 | `successColor6` `#43AF8A` por D-02 |
| `successFill` | `successColor6` `#008858` por D-02 | `successColor5` `#059465` |
| `successContainer` | `successLightColor` (`successColor1`) | `successLightColor` (`successColor1`) |
| `warning` | `warningColor7` `#954500` por D-02 | `warningColor7` `#E8935C` por D-02 |
| `warningFill` | `warningColor6` `#BE5A00` por D-02 | `warningColor5` `#CF6E2D` |
| `warningContainer` | `warningLightColor` (`warningColor1`) | `warningLightColor` (`warningColor1`) |
| `info` | `textColorBrand` (`brandColor7`) | `brandColor9` `#699EF5` por D-02 |
| `infoContainer` | `brandLightColor` (`brandColor1`) | `brandLightColor` (`brandColor1`) |
| `focus` | `brandNormalColor` (`brandColor7`) | `brandColor9` `#699EF5` por D-02 |
| `disabled` | `bgColorComponentDisabled` (`gray2`) | `bgColorComponentDisabled` (`gray12`) |
| `onDisabled` | `textDisabledColor` (`fontGyColor4`) | `textDisabledColor` (`fontWhColor4`) |
| `inverseSurface` | `gray14` | `gray2` |
| `inverseOnSurface` | `#ffffff` | `fontGyColor1` |
| `scrim` | `#99000000` | `#66000000` |
| `qrCanvas` | `#ffffff` | `#ffffff` |
| `onAccent`, `onDanger` | `#ffffff` | `#ffffff` |
| `onSuccessFill`, `onWarningFill` | `#ffffff` | `gray14` |

Los tests fijan las escalas y los roles completos para ambos temas. Para texto
se elige un par de la paleta TDesign con contraste ≥4.5:1; para indicadores no
textuales, ≥3:1. El lienzo QR es blanco en ambos temas. El color no comunica por
sí solo error, éxito, selección ni deshabilitado.

## 5. Tipografía

- Se usa la fuente del sistema; no se empaquetan familias tipográficas.
- La escala es la de `td_default_theme.dart`, campo `font`, idéntico en ambos
  temas; el peso `fontWeight: 6` equivale a `w600`:

| Clave de la fuente | Tamaño | Interlínea | Peso |
|---|---|---|---|
| `fontDisplayLarge` | 64 | 72 | 600 |
| `fontDisplayMedium` | 48 | 56 | 600 |
| `fontHeadlineLarge` | 36 | 44 | 600 |
| `fontHeadlineMedium` | 28 | 36 | 600 |
| `fontHeadlineSmall` | 24 | 32 | 600 |
| `fontTitleExtraLarge` | 20 | 28 | 600 |
| `fontTitleLarge` | 18 | 26 | 600 |
| `fontTitleMedium` | 16 | 24 | 600 |
| `fontTitleSmall` | 14 | 22 | 400 |
| `fontBodyExtraLarge` | 18 | 26 | 400 |
| `fontBodyLarge` | 16 | 24 | 400 |
| `fontBodyMedium` | 14 | 22 | 400 |
| `fontBodySmall` | 12 | 20 | 400 |
| `fontBodyExtraSmall` | 10 | 16 | 400 |
| `fontMarkLarge` | 16 | 24 | 600 |
| `fontMarkMedium` | 14 | 22 | 600 |
| `fontMarkSmall` | 12 | 20 | 600 |
| `fontMarkExtraSmall` | 10 | 16 | 600 |
| `fontLinkLarge` / `Medium` / `Small` | 16 / 14 / 12 | 24 / 22 / 20 | 400 |

- La familia numérica de la fuente es `TCloudNumber`; no se empaqueta (D-09).
- El texto visible usa un mínimo de 11 sp; `fontBodyExtraSmall` (10 sp) no se
  asigna a copy visible (D-04).
- Los importes usan 28/36, peso 600 y cifras tabulares.
- Todo texto respeta el escalado de fuente del sistema.

## 6. Espaciado, formas y métricas

- Espaciado desde `spacer` de la fuente: 4, 8, 12, 16, 24, 32, 40, 48, 64, 96
  y 160 dp. El margen de pantalla es 16 dp.
- Radios desde `radius` de la fuente: 3, 6, 9, 12 y 9999 dp (`radiusRound` y
  `radiusCircle`).
- La elevación usa las sombras `shadowsBase`, `shadowsMiddle` y `shadowsTop`
  (tres capas cada una); Compose conserva la capa dominante halvando su radio
  de desenfoque (D-06).
- Targets interactivos: mínimo 48 dp (D-03).
- Botones (`td_button.dart`): alto y ancho visual 48, 40, 32 y 28 dp; icono
  24, 20, 18 y 14 dp; relleno horizontal 20, 16, 12 y 8 dp; etiqueta
  `fontMarkLarge` en grande/mediano y `fontMarkMedium` en pequeño/minúsculo.
- Barra superior (`td_nav_bar.dart`): 48 dp.
- Barra inferior (`td_bottom_tab_bar.dart`): 56 dp.
- Divisor (`td_divider.dart`): 0.5 dp; celda con divisor inset.
- Icono de Empty (`td_empty.dart`): 96 dp; icono de Result
  (`td_result.dart`): 70 dp.
- Búsqueda y campos: superficie visual compacta con target interactivo de
  48 dp (D-03); la barra de búsqueda de la fuente mide 56 dp.
- Contenido en ventanas amplias: ancho máximo 720 dp.

Las métricas se declaran en `AmSpacing`, `AmRadius` y `AmMetrics`; las
pantallas no repiten literales.

## 7. Iconografía

Los glifos de producto proceden de TDesign Icons, convertidos a vectores
Compose en `core/ui`. `AmIcons` ofrece un nombre estable por concepto y
`AmIcon` centraliza tamaño, tinte y semántica. El catálogo no es una
dependencia de ejecución. La atribución MIT figura en
[`06-licencias-terceros.md`](06-licencias-terceros.md).

Los iconos decorativos usan descripción nula. Todo icono interactivo tiene
etiqueta visible o una descripción accesible explícita; ningún estado depende
solo del glifo.

## 8. Movimiento

- Easing: `cubic-bezier(0.38, 0, 0.24, 1)`.
- Duraciones: 200, 240 y 280 ms.
- Movimiento reducido: duración cero.
- `tdesign_flutter@0.2.7` no expone tokens de movimiento; los valores se
  declaran en `AmMotion` (D-10).
- No se usan escala de pulsación, spring ni movimiento ornamental en bucle.
- La háptica de plataforma y el foco por teclado/D-pad se conservan.

## 9. Componentes y patrones

Cada componente de AgendaQr corresponde a un componente de
`tdesign_flutter@0.2.7`; su geometría y sus estados siguen ese origen.

| Componente AgendaQr | Origen en `tdesign_flutter@0.2.7` |
|---|---|
| `AmTopBar` | `TDNavBar` |
| `AmNavBar` | `TDBottomTabBar` |
| `AmNavRail` | Sin equivalente (D-05) |
| `AmListRow`, `AmListBody`, `AmSectionHeader`, `AmMetaRow` | `TDCell`, `TDCellGroup` |
| `AmHairline`, divisores | `TDDivider`, `TDInsetDivider` |
| `AmButton`, `AmPrimaryButton`, `AmSecondaryButton`, `AmOutlineButton`, `AmLightButton`, `AmDangerButton`, `AmTextAction`, `AmBottomAction`, `AmIconButton`, `AmCompactAction` | `TDButton` |
| `AmTextField`, `AmAmountField` | `TDInput`, `TDTextarea` |
| `AmSearchField`, `AmSearchTrigger` | `TDSearchBar` |
| `AmTonalTag`, `AmFilterChip`, `AmStatusPill` | `TDTag` (`TDSelectTag`) |
| `AmBadge`, `AmIconBadge` | `TDBadge` |
| `AmTabs` | `TDTabs`, `TDHorizontalTabBar` |
| `AmSegmentedControl` | `TDTabs` (grupo de opciones) |
| `AmCard`, `AmTappableCard`, `AmQuickAction` | `TDCellGroup` (tema tarjeta) |
| `AmEmptyState` | `TDEmpty` |
| `AmStatusBanner` | `TDNoticeBar` |
| `AmDialog`, `AmDialogList`, `AmActionSheet` | `TDDialog`, `TDActionSheet` |
| `AmDatePickerDialog` | `TDCalendar` |
| `AmTimePickerDialog` | `TDPicker` |
| `AmSnackbarHost`, `AmFeedbackEvent` | `TDToast`, `TDMessage` |
| `AmLoading`, `AmSkeleton` | `TDLoading`, `TDSkeleton` |
| `AmQrPreview`, `AmMediaFrame` | `TDImage`, `TDImageViewer` |
| `AmScannerFrame` | `TDImage` (marco de lectura) |
| `AmExtendedFab` | `TDFab` |
| `AmText`, `AmHeading`, `AmHeroHeader`, `AmSection` | `TDText` |
| `AmSuccessCheck`, `AmContextBadge`, `AmFavoriteToggle` | `TDIcons`, `TDAvatar` |
| `AmClipboard` | Servicio de plataforma |

| Grupo | Componentes | Regla de composición |
|---|---|---|
| Chrome | `AmTopBar`, `AmNavBar`, `AmNavRail`, `AmScreenScaffold` | Título centrado; atrás a la izquierda; acciones y desbordamiento a la derecha; cuatro destinos raíz con etiquetas visibles. |
| Acciones | `AmPrimaryButton`, `AmSecondaryButton`, `AmDangerButton`, `AmTextAction`, `AmBottomAction` | Como máximo una primaria por contexto; el target mide ≥48 dp; acción fija solo cuando existe guardado real. |
| Campos | `AmTextField`, `AmAmountField`, `AmSearchField`, `AmSearchTrigger` | Etiqueta y error asociados; búsqueda con foco visible; la acción de limpiar tiene nombre accesible. |
| Listas | `AmListRow`, `AmListBody`, `AmSectionHeader`, `AmMetaRow` | Las filas relacionadas forman un Cell-group; las filas usan divisores inset y chevron solo cuando navegan. |
| Selección | `AmSegmentedControl`, `AmTabs`, `AmFilterChip`, `AmTonalTag`, `AmBadge` | El estado seleccionado tiene semántica; los tags comunican su tipo con texto. |
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

1. Un cambio de valor visual actualiza el token y sus pruebas.
2. Un componente nuevo necesita consumidor real o propósito contractual,
   contrato accesible y pruebas de estado.
3. Un cambio de composición mantiene la intención y las reglas de producto de
   su pantalla.
4. Las excepciones se registran en el contrato y en el gate dentro del mismo
   cambio.
5. Una decisión estructural del sistema se documenta en ADR.

## 15. Desviaciones vigentes

| ID | Desviación | Resolución |
|---|---|---|
| D-01 | Identidad de contextos con 12 tonos deterministas. | Se conserva `AmAccents` y el hash FNV-1a; las pruebas verifican los pares legibles. |
| D-02 | Algunos tonos oficiales no alcanzan el contraste requerido. | Se usa el siguiente escalón de la familia TDesign con contraste suficiente. |
| D-03 | Controles de menor tamaño visual que el target accesible. | La superficie conserva la geometría oficial y el target llega a 48 dp. |
| D-04 | `fontBodyExtraSmall` de 10 sp. | No se usa en texto visible; el tamaño compacto visible es 12 sp. |
| D-05 | Navegación lateral para ventanas amplias. | `AmNavRail` mantiene los cuatro destinos con los tokens del sistema. |
| D-06 | Sombras de varias capas. | Compose usa la capa dominante declarada en los tokens. |
| D-07 | Selector de contexto, fecha, hora y menú de desbordamiento. | Se presentan mediante `AmDialog` y listas; la composición permite interacción verificable bajo Robolectric. |
| D-08 | Fuentes tipográficas de plataforma. | Se usa la fuente del sistema y no se distribuyen archivos de fuente. |
| D-09 | Familia numérica `TCloudNumber`. | No se empaqueta; los importes usan cifras tabulares de la fuente del sistema. |
| D-10 | La fuente no expone tokens de movimiento. | Los valores se declaran en `AmMotion`. |

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

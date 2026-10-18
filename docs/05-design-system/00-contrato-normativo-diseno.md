# Contrato normativo de diseño

**Estado:** Normativo — versión 4.0 (ADR-0020, ADR-0021, ADR-0022)
**Autoridad:** este documento es la fuente única y obligatoria de la identidad visual de AgendaQr. La dirección visual vigente es la **Super-App de alta densidad** descrita en [`08-referencia-super-app.md`](08-referencia-super-app.md): densidad, vibrancia, gradientes y rejilla de accesos, con los pisos de accesibilidad del §10 (ADR-0022 suplanta la paleta y la escala tipográfica de ADR-0020 y enmienda ADR-0021). **Android e iOS definen las convenciones de navegación y comportamiento de plataforma**. La implementación vive en `core/ui` (`com.agendaqr.core.ui`, prefijo `Am*` como convención de código, sin valor de marca). Material 3 es detalle de implementación, no autoridad visual.

## 1. Principios

1. **Identidad propia y auditable**: la paleta, superficies, texto, estados, radios, densidad, controles y feedback se definen en este contrato. Las referencias externas inspiran patrones, pero no gobiernan tokens ni obligan a copiar componentes.
2. **Navegación convencional de plataforma**: destinos raíz con barra inferior en compacto, barra superior jerárquica con Atrás, back stack con restauración, back del sistema y predictive back en Android, gestos en iOS. La identidad visual propia no sustituye las convenciones nativas de navegación.
3. **Mobile-first real**: se diseña para el pulgar en un teléfono Android de 360 dp; la adaptación a tablet/escritorio (compacto → medium/expanded vía `WindowSizeClass`) nunca degrada el patrón compacto.
4. **Material 3 es implementación, no autoridad**: los componentes M3 se adaptan con tokens; ningún color, radio, altura o tipografía se define por defecto de la plantilla.
5. **El estado nunca es solo color**: tono + texto/icono + semántica accesible en cada estado (carga, error, selección, deshabilitado, foco).
6. **Contraste como contrato**: texto ≥ 4.5:1, bordes/foco ≥ 3:1, verificado por `AmSchemeTest` y `AmContrastPairsTest`. La identidad visual nunca justifica degradar legibilidad o accesibilidad.
7. **Escala de formas propia y consistente**: 2/4/6/8/12 dp (`AmRadius`) como única escala de forma (tag 2, botón 4, búsqueda 6, tarjeta/diálogo 8, hoja 12).
8. **Movimiento corto y justificado**: 150 ms con `AmMotion`; movimiento reducido = cambio inmediato (duración 0), nunca "una animación más corta".

## 2. Arquitectura de capas

| Capa | Ubicación | Responsabilidad |
|---|---|---|
| 1. Tokens privados | `AmTokens.kt` → `AmPalette` (internal) | Primitivas por tema; sin exposición al producto |
| 2. Tokens semánticos | `AmTokens.kt` → `AmColorScheme`, `AmColor`, `AmSpacing`, `AmRadius`/`AmShapes`, `AmMetrics`, `AmType`/`AmTextStyles`, `AmMotion`, `AmAccent`/`AmAccents`/`accentFor` | La ÚNICA capa que el producto consume |
| 3. Tema | `AmTheme.kt` | Puente claro/oscuro a Material 3; esquema M3 derivado COMPLETO (incluye contenedores) |
| 4. Componentes | `components/` | Implementaciones Compose nativas del lenguaje visual AgendaQr con tokens; contratos de estado y accesibilidad |
| 5. Patrones | `components/` (scaffold, chrome, listas de datos) | Composición reutilizable de producto |
| 6. Pantallas | `feature/destinations/presentation` | Componen componentes; cero literales visuales |
| 7. Pruebas | `core/ui` commonTest/androidUnitTest + Roborazzi | Contrato de color, tipografía, accesibilidad y regresión visual |

## 3. Prohibiciones (enforcement: `verifyDesignSystemCompliance`)

- Hex, `dp`, `sp`, `Color(` o `Color.<nombre>` crudos fuera de `AmTokens.kt`.
- `RoundedCornerShape`/`CutCornerShape`/`CircleShape`/`RectangleShape` fuera de la capa de tokens, y `shadow(` fuera de la capa de tokens salvo las excepciones registradas E-05/E-06; los componentes consumen `AmShapes` y `amElevation`.
- `MaterialTheme.colorScheme` como autoridad fuera de `AmTheme.kt`; los componentes leen `AmColor`.
- Iconos Material en producción: la iconografía es Lucide (ISC) SOLO vía `AmIcons`/`AmIcon`.
- Controles M3 (`Button`, `TextField`, `AlertDialog`, `FilterChip`…) importados en `feature/`, `shared/` o `androidApp/`: se componen los equivalentes del sistema.
- Copy de UI literal en `feature/` (fuera de `AppStrings`) o en `components/` (todo texto llega como parámetro, ADR-0008).
- `.clickable(` crudo o `indication = null` en `feature/`: la interacción vive en el sistema.
- Duraciones de animación literales: se consumen `AmMotion`.
- `.border(`/`BorderStroke(` en producción: la separación usa hairlines (`Divider`), el foco y el estado usan componentes M3 y canvas.

## 4. Color e identidad de marca

La paleta expresa la dirección Super-App de alta densidad (ADR-0022): gradiente de acción rojo→naranja como primaria, azul fintech reservado a rellenos/enlaces semánticos, superficies neutras y tonos de estado. No se usa rojo/dorado como decoración cultural ni se colorea cada tarjeta por separado.

### 4.1 Roles semánticos

`AmColorScheme` expone exactamente: `background`, `surface`, `surfaceSecondary`, `border`, `divider`, `textPrimary`, `textSecondary`, `textTertiary`, `accent`, `onAccent`, `accentContainer`, `onAccentContainer`, `accentText`, `wathet`, `danger`, `onDanger`, `dangerContainer`, `success`, `successContainer`, `warning`, `warningContainer`, `info`, `infoContainer`, `focus`, `disabled`, `onDisabled`, `inverseSurface`, `inverseOnSurface`, `inverseAccent`, `qrCanvas`, los contenedores M3 explícitos (`surfaceContainerLow/Container/High/Highest`) y los roles de la dirección Super-App: `gradientStart`/`gradientMid`/`gradientEnd`/`onGradient` (gradiente de acción), `promo`/`promoContainer`/`onPromo`, `fintech`/`onFintech`/`fintechText`, `dividerSubtle` y `surfaceBorder`.

### 4.2 Paleta base

La paleta vigente es la del Anexo A (ADR-0022) con los ajustes de contraste mínimos para AA. Claro:

| Rol | Claro | Oscuro |
|---|---|---|
| Página | `#F5F6F8` | `#0A0A0A` |
| Superficie | `#FFFFFF` | `#1A1A1A` |
| Superficie secundaria | `#F1F2F5` | `#141414` |
| Texto principal | `#1A1A1A` | `#E6E6E6` |
| Texto secundario | `#666666` | `#9A9A9A` |
| Texto terciario | `#6B6B6B` | `#8A8A8A` |
| Divisor decorativo | `#EEEEEE` | `#2A2A2A` |
| Borde de superficie | `#E2E4E9` | `#333333` |
| Acento de acción (rojo) | `#E4001F` | `#FF5A70` |
| Gradiente de acción (inicio/medio/fin) | `#E4001F` / `#D12810` / `#BE4F00` | igual (marca) |

El texto blanco (`onGradient`) sobre cada extremo del gradiente cumple ≥ 4.5:1 (4.87 / 5.23 / 4.89). El acento oscuro usa texto oscuro `#2A0A10`. El azul fintech de relleno es `#1677FF`; como texto/enlace, `#0067F7` (claro, 4.90:1 sobre blanco) y `#4D9BFF` (oscuro, 6.17:1 sobre `#1A1A1A`). El éxito como texto usa `#008156` (claro, 4.92:1 sobre blanco). El rojo `#FF5A70` (oscuro, 5.76:1 sobre `#1A1A1A`) y el azul `#4D9BFF` son los roles de texto rojo/azul en oscuro. Los valores exactos quedan fijados por `AmSchemeTest`.

### 4.3 Estados semánticos

| Estado | Claro (contenido / contenedor) | Oscuro (contenido / contenedor) |
|---|---|---|
| Éxito | `#008156` / `#ECFBF4` | `#3CCB8E` / `#0E2A1E` |
| Advertencia | `#8A4B00` / `#FFF2D9` | `#FFC15A` / `#3A2B0E` |
| Peligro | `#C62828` / `#FDECEC` | `#FF6B78` / `#3A151D` |
| Información | `#0067F7` / `#F2F8FF` | `#4D9BFF` / `#10233F` |

Los estados siempre combinan tono con texto, icono o semántica accesible; nunca dependen solo del color. La identidad de contexto conserva sus 12 acentos deterministas, independientes del acento de marca y sin cambiar el dominio. El color de acción (rojo) es distinto del de peligro: la acción destructiva nunca usa relleno de gradiente y siempre lleva glifo + etiqueta de consecuencia.

### 4.4 Contraste y superficies

- Texto normal y contenido de estado: contraste ≥ 4.5:1 sobre las superficies donde se presenta.
- Foco e indicadores funcionales: contraste ≥ 3:1 respecto a las superficies adyacentes.
- El par deshabilitado mantiene un suelo de calidad ≥ 3:1; el estado también se expresa semánticamente.
- Las superficies claras son neutras; las oscuras usan matiz azul-gris para dar profundidad sin sombras generalizadas.
- Los contenedores semánticos deben admitir texto principal y acciones sin perder contraste; `AmSchemeTest` y `AmContrastPairsTest` son la autoridad ejecutable.
- `qrCanvas` permanece blanco en ambos temas para preservar la zona de silencio escaneable del QR.

## 5. Tipografía

- Familia de sistema en todos los roles (Roboto/San Francisco): ninguna fuente se empaqueta. La fuente de sistema ya resuelve CJK (Noto en Android, PingFang en iOS) sin empaquetar nada (ADR-0022). MiSans Global y una familia monoespaciada quedan como experimentos locales, no como dependencias aprobadas.
- **Escala del Anexo A (ADR-0022):** Importe hero 30 sp peso 800 (Anexo 28–32/800), con símbolo de moneda 14 sp/700 más pequeño que el número; H1 20/600 (`TitleLarge`); H2 16/600 (`RowTitle`, `SectionHeader`); Body 14/400; Caption 12/400; etiqueta/botón 14; badges y etiquetas de barra 11–12. **Mínimo absoluto 11 sp.** Todo en `sp`.
- Cifras tabulares en los roles financieros (`fontFeatureSettings` de "tnum") para alinear importes y fechas.
- Interlineado siempre > 1.1× su tamaño (`AmUiPolishContractTest`); el escalado de fuente 100/150/200 % se respeta y se prueba. El tracking sigue los valores por defecto de M3 (`AmTracking`), con tracking ligeramente positivo solo en la etiqueta de la acción primaria.

## 6. Espaciado y métrica

- Escala: 4 · 8 · 12 · 16 · 20 · 24 · 32 (+40 excepcional) — `AmSpacing`; **margen de pantalla 12** (`ScreenMargin`), gutters 8–12 y padding interno de tarjeta 12 (`AmSpacing.Md`); densidad compacta y legible (celda 12+12, hairlines inset a 56 dp bajo la marca de fila — `ListDividerInset`).
- `AmMetrics`: área táctil mínima 48 dp (`ControlMinSize`, invariante del gate) con **separación de superficie visible y zona interactiva**: la superficie VISIBLE de botones secundarios (40 dp, `ButtonCompactHeight`), píldoras (~32 dp, `ChipCompactHeight`), entradas (44 dp, `FieldCompactHeight`) y acciones de fila (~36 dp) puede ser menor que su zona interactiva de 48 dp; el buscador mide 48 dp mínimo (desviación documentada); barra superior 56 dp (`TopBarHeight`), barra inferior 56 dp (`NavBarHeight`; cada ítem garantiza 48 dp), icono estándar 24 dp, icono compacto 18 dp (`IconSizeCompact`), la celda de la rejilla de Inicio mide al menos 48 dp de alto por celda con etiqueta de una línea; `QrPreviewSize` 240, `ContentMaxWidth` 720 (medium/expanded), `DialogListMaxHeight` 320, `DialogMinWidth` 280 con tope del 75 % del ancho (`DialogMaxWidthFraction`), marco del scanner 3 dp (`BorderStrong`), glifo del estado vacío 64 dp, ancho de mini-tarjeta de frecuentes 184 (`FavCardWidth`), ancho mínimo de fila para que el metadato final comparta la línea del título 320 (`ListRowEndMetaMinWidth`; por debajo, o con fuente al 200 % — `ListRowEndMetaStackFontScale` —, el `endMeta` de `AmListRow` baja a línea propia).
- Radios (`AmRadius`, Anexo A): badges/etiquetas/cinta 4–8 (`ExtraSmall`/`Band`), controles y tarjetas pequeñas 8 (`Small`), tarjeta de lista/tappable/media/diálogo/banner 12 (`Medium`), panel y tarjeta destacada 16 (`Large`), píldora 20+ (`ExtraLarge`) y píldora completa para la acción primaria, chips, FAB y píldoras de estado (`Pill`); icono de rejilla 14 (`GridIcon`). El hairline perimetral de las tarjetas es la excepción E-03; el borde de acento en foco del buscador es la excepción E-04; las sombras E-1/E-2 y E-3 son las excepciones E-05 y E-06 vía `AmElevation`.

## 7. Iconografía

- Lucide (ISC + MIT para glifos derivados de Feather) vía `AmIcons`; licencia registrada en `06-licencias-terceros.md`. Sin mezcla de familias; sin emojis funcionales.
- Un glifo por concepto; set mínimo con glifos de estado y de estados vacíos. Glifos financieros (ADR-0015): `Income` (flecha entrante, cobro), `Expense` (flecha saliente, pago), `Attachment` (comprobante adjunto) — decorativos; el texto de la fila comunica el tipo.
- Icono decorativo = `contentDescription = null` (excluido del lector); todo icono interactivo lleva etiqueta de texto visible compuesta por el llamador.

## 8. Movimiento

- `AmMotion` (Anexo A): duración 200–300 ms (`DurationShortMs`=200, `DurationMediumMs`=260, `DurationLongMs`=300); easing de rebote `cubic-bezier(0.34, 1.56, 0.64, 1)` (`Easings.Spring`) para pulsaciones y confirmaciones, y `Easings.Standard` para transiciones de superficie.
- Escala de pulsación 0.95 (`PressScale`); el brillo (shimmer) de entrada dura ≤ 5 s (`ShimmerDurationMs`) y luego queda estático (en esqueletos de carga puede repetirse mientras dure la carga).
- Transición de superficies: fundido + desplazamiento de 1/8 del ancho (`AmSurfaceSlideFraction`).
- Con movimiento reducido (`LocalReducedMotion`/`ProvideReducedMotion`): duración 0, escala 1, brillo estático, sin confeti ni check animado. Reducir no es "animar más rápido".
- Sin animaciones decorativas en bucle salvo esqueletos de carga; el contenido en movimiento es detenible.

## 9. Componentes y estados (lenguaje visual AgendaQr)

| Grupo | Componentes | Composición |
|---|---|---|
| Estructura | `AmScreenScaffold`, `AmTopBar` (+ acciones, overflow; `statusBarsPadding` propio), `AmNavBar`/`AmNavRail` (+ destinos, estado; `navigationBarsPadding` propio), `AmExtendedFab` (compacto 48 dp, etiqueta 13/600) | Barra inferior de destinos raíz; barra superior jerárquica con Atrás (título a la izquierda por convención Android); FAB extendido de plataforma. **Edge-to-edge: los slots de Scaffold NO reciben insets — cada barra aplica los suyos (`statusBarsPadding`/`navigationBarsPadding`); en Robolectric los insets son 0, así que esta regla se valida en dispositivo/instrumentados** |
| Acciones | `AmPrimaryButton`/`AmBottomAction` (píldora con gradiente de acción `gradientStart→gradientMid→gradientEnd`, altura 48, sombra E-3, texto blanco 14/600 con tracking positivo, escala 0.95 al pulsar con háptica, `loading` sin salto de ancho), `AmSecondaryButton` (fondo claro + hairline + texto primario; compacto 40 visual / 48 táctil, variante `solid`), `AmTextAction` (none), `AmDangerButton` (contorno o tono, SIN gradiente, con glifo y etiqueta de consecuencia), `AmIconButton`, `AmFilterChip` (píldora completa: reposo box sin borde, activa sólida de acento, etiqueta 14, conteo y glifo opcionales), `AmCompactAction` (acción de fila/tarjeta ~36 visual / 48 táctil), `AmSegmentedControl` (pista box, item activo en superficie, opciones con glifo y tono activo), `AmFavoriteToggle` | El primario es SIEMPRE píldora con gradiente; el destructivo nunca rellena con gradiente; separación de superficie visible y zona interactiva |
| Elevación | `AmElevation` (E-0 sin sombra, E-1 `0 2 8 rgba(0,0,0,.04)`, E-2 `0 4 16 rgba(0,0,0,.08)`, E-3 `0 8 24` del color de acción al 20 %); el modificador `amElevation` vive en la capa de tokens (`AmTokens.kt`) como ÚNICO punto que llama a `shadow` y las excepciones E-05/E-06 lo habilitan por archivo | Tarjetas E-1/E-2 sobre fondo gris; acción primaria flotante E-3; en oscuro la superficie se separa además con borde sutil (`surfaceBorder`) |
| Formularios | `AmTextField` (patrón Form/List con etiqueta flotante persistente — desviación documentada —, guía hairline en foco/error; label + helper + error accesible + glifo de concepto opcional), `AmAmountField` (importe 26 sp con borrado etiquetado y guía hairline), `AmSearchField` (SearchBar: box, radio 6, borde de acento en foco E-04, 48 dp mínimo, sin etiqueta flotante; teclado configurable, autofoco opt-in), `AmSearchTrigger` | Input + Form/List; placeholder en texto secundario con contraste AA fijado por los tokens y sus pruebas; limpieza etiquetada; foco al entrar en Buscar |
| Superposiciones | `AmDialog` (popup radio 12, título 18 bold centrado, contenido 14, fila de acciones a ancho completo con hairlines; foco conservativo en destructivas), `AmDialogList` | Dialog centrado; sin hoja inferior (limitación Robolectric documentada) |
| Datos | `AmListRow` (título 17, descripción 13, `meta` en línea propia **solo metadatos/badges — prohibido colocar acciones ahí**, `endMeta` corto de solo texto alineado a la derecha en la línea del título — fecha, estado o importe; exclusivo con `showChevron` y con acciones en `trailing`; a 320 dp de fila o con fuente al 200 % pasa a línea propia bajo el título —, `leading` tipado — tile tonal 40 dp —, `showChevron` opt-in para filas que navegan, divisor opt-in con inset a 56 dp, semántica `selected`), `AmListBody` (tarjeta radio 12 con hairline perimetral E-03, padding interno 0 por defecto — filas), `AmCard` (tarjeta de superficie para contenido libre, padding interno `AmSpacing.Lg` por defecto), `AmTappableCard` (tarjeta presionable radio 12: interacción y semántica en el DS), `AmHairline`, `AmBadge`/`AmTonalTag` (Tag: radio 4 con `round` opt-in, 11 sp normal, mayúsculas tipadas), `AmIconBadge` (tile 40), `AmMetaRow` (metadatos label/valor con acción final), `AmSectionHeader` (List header: 16, texto secundario, peso normal), `AmSection`, `AmPivot` (Tabs con indicador de acento, título 17) | List/Item, Tag outline/round, Tabs, cápsulas con conteo, metadatos compactos |
| Estados | `AmStatusBanner` (NoticeBar: radio 12; layout en **columna**: fila 1 icono + mensaje a ancho completo, fila 2 acciones al final con envoltura; API `AmBannerAction` para action/dismiss), `AmStatusPill` (variante `rounded`: reaseguro tonal o inverso "Modo local"; también cinta 4–8 con texto real de estado), `AmLoading`, `AmEmptyState`, `AmSkeleton` (brillo de entrada ≤ 5 s; estático con movimiento reducido), `AmSuccessCheck` (check decorativo animado con rebote + doble háptica; el banner/evento adyacente comunica el éxito), `AmSuccessBurst` (confeti breve, solo S12 completada), `AmLoadMoreFooter`, `AmQuickAction` (celda de la rejilla de acción de S01) | NoticeBar; Empty; aviso persistente de producto; confirmación y confeti acotados y detenibles |
| Feedback | `AmFeedbackHost`/`AmSnackbarHost`/`AmFeedbackEvent` (háptica + anuncio + snackbar inferior en Android; contenedor en superficie inversa con radio 8 y texto 15) | Aviso inferior de plataforma; anuncio por evento |
| Medios | `AmQrPreview` (expect/actual, tamaño configurable 1:1, revisión a 320 dp; fallo de decodificación = glifo explícito, nunca rectángulo blanco), `AmMediaFrame` (Surface con radio 8: el límite es el bloque tonal, sin bordes — gate E-01/E-02), `AmScannerFrame` (esquinas de acento en canvas sobre el preview real; el viewport tonal se retiró por reservar espacio vacío) | Bloque con radio 8 |
| Tono | `AmTone` (Neutral/Success/Danger/Warning/Info) con par content/container y glifo por tono | Colores semánticos propios |

Estados obligatorios: normal, pressed (indicación M3), focused (indicación de foco del componente M3 con el rol `AmColor.Focus`), selected (semántica + borde de acento/fondo wathet), error (icono + texto + `error()` semántica), disabled (opacidad .4 + par `Disabled/OnDisabled`), loading (spinner superpuesto sin salto de ancho + `stateDescription`).

### Jerarquía de acciones (4 niveles, contracto §9.1)

Toda acción de la app pertenece a un nivel semántico; el nivel decide el componente,
no al revés. Documentación detallada en la javadoc de `AmButtons.kt`; cobertura
comportamental en `ActionHierarchyTest` y `ScreensA11yCoverageTest`.

| Nivel | Componente | Tratamiento | Regla de uso |
|---|---|---|---|
| 1 — Primaria | `AmPrimaryButton` / `AmBottomAction` | Píldora con gradiente de acción y sombra E-3 | El objetivo principal del usuario por sección; **como máximo una** por contexto; en lectura/búsqueda/selección puede no existir |
| 2 — Secundaria | `AmSecondaryButton` | Tonal (contenedor bajo + hairline); variante `solid` solo para la acción insignia de una tarjeta; **a ancho completo cuando el llamador pasa `fillMaxWidth()` (la superficie visible ocupa todo el ancho)** | Alternativa importante; nunca compite con la primaria; una destacada por contexto |
| 3 — Terciaria | `AmTextAction` | Texto con target de 48 dp; variantes `Default` (acento), `Neutral` (texto primario semibold) y `Danger` (destructiva); prohibido usar grises como color de acción. Descartar se alinea siempre a la derecha (`Arrangement.End`) en todas las tarjetas de pendientes | Acciones contextuales y navegación auxiliar |
| 4 — Destructiva | `AmDangerButton` / `AmDialog(destructive = true)` | Tono peligro + texto específico de consecuencia; confirmación si hay pérdida irreversible | Nunca compite con la primaria; el color nunca es la única señal (glifo o etiqueta específica) |

Reglas derivadas verificadas en los flujos: etiquetas específicas de consecuencia en
diálogos (nada de «Aceptar» genérico); acciones destructivas dentro del contenido del
diálogo cuando la salida segura es el slot de descarte; descarte con confirmación solo
cuando se pierde información no recuperable; etiquetas completas sin elipsis (los
botones apilan a ancho disponible antes de comprimir texto).

### Nota sobre la hoja inferior

El Action-sheet y los selectores usan `AmDialog` con lista desplazable. La hoja inferior en composición no despacha las acciones de forma fiable bajo Robolectric; esta alternativa conserva controles comprobables y accesibles.

## 10. Accesibilidad como contrato

- Semántica de controles (rol, nombre accesible, estado) probada en `AmAccessibilityContractTest` y la suite de presentación.
- Área táctil ≥ 48 dp en todos los interactivos (gate estático + pruebas de layout).
- Errores de campo: icono + texto liveRegion cortés + `error()` en el nodo editable; sin depender del color.
- Encabezados con `heading()`; órden de foco lógico; foco inicial en la acción conservativa de diálogos destructivos.
- Escalado de fuente hasta 200 % sin recortes ni solapes (pruebas de layout a 320/360 dp).
- IME respetado (scaffold consume `WindowInsets.ime`); restauración tras rotación/recreación probada por instrumentados.
- Anuncios de cambios por evento (liveRegion por token, no por estado persistente) — `AmFeedbackEvent`.

## 11. Plataforma y paridad

- Android e iOS comparten TODO el código visual (commonMain); solo `AmQrPreview`, `amWindowWidthSizeClass` y `ProvideReducedMotion` son expect/actual.
- Navegación: back del sistema y predictive back en Android; gestos de retroceso en iOS; áreas seguras resueltas una vez en el scaffold; sin insets manuales duplicados.
- Las superficies M3 se fijan explícitamente en `AmTheme` (ningún componente hereda la plantilla base de Material).
- La validación iOS se realiza por compilación de targets (`compileKotlinIosSimulatorArm64`); la validación de runtime requiere Xcode (checklist en `OPERATIONS.md`).

## 12. Registro de excepciones

El gate compara esta tabla con `exceptionRegistry` en `build-logic/agendaqr.design-system.gradle.kts`: deben coincidir exactamente.

| ID | Regla | Aplicación |
|---|---|---|
| E-01 | Hairline perimetral del botón secundario (fondo claro + borde hairline + texto primario; NO es outline de acento) | `border:AmButtons.kt` — verificado por la suite (`AmSecondaryButton` se renderiza y conserva 48 dp en `AmResponsiveLayoutContractTest`/`FontScaleContractTest`) |
| E-02 | Borde outline de la insignia de estado (borde de 1 dp del tono, no hairline decorativo) | `border:AmLists.kt` — verificado por la suite (`AmBadge` se renderiza en `AppStatusHostTest`/`ScreensA11yCoverageTest`) |
| E-03 | Hairline perimetral del cuerpo de lista/tarjeta (`AmListBody`/`AmTappableCard`: superficie clara delimitada por un borde de 1 px; el límite de la tarjeta es el borde, no una sombra) | `border:AmLists.kt` — verificado por las capturas (ADR-0007) |
| E-04 | Borde de acento en foco del buscador (borde de 1 px del color primario cuando el campo está activo; en reposo el borde es transparente) | `border:AmInputs.kt` — verificado por la suite (el campo se renderiza y conserva su nombre accesible en `ScreensA11yCoverageTest`; el foco cumple ≥ 3:1 por `AmSchemeTest`) |
| E-05 | Sombra E-1/E-2 de superficies elevadas (tarjeta de lista y tarjeta destacada sobre fondo gris; el límite se refuerza con borde sutil en oscuro) | `shadow:AmLists.kt` — verificado por la suite y por las capturas (ADR-0007) |
| E-06 | Sombra E-3 (color de acción al 20 %) de la acción primaria flotante | `shadow:AmButtons.kt` — verificado por la suite y por las capturas (ADR-0007) |

El gate interpreta las reglas `shadow:` del registro igual que las `border:`: el `Modifier.shadow` sigue centralizado en la capa de tokens (`AmElevation`/`amElevation`) y solo los archivos registrados pueden invocarlo.

Para proponer una excepción: registrarla aquí y en el gate en el MISMO cambio, con justificación de accesibilidad y prueba asociada.

## 13. Gates de calidad

| Gate | Tarea |
|---|---|
| Compliance del sistema | `verifyDesignSystemCompliance` (+ autotest `verifyDesignSystemFixtures`) |
| Área táctil mínima | `verifyControlMinSize` (AmMetrics.ControlMinSize = 48.dp) |
| Contraste y esquema | `AmSchemeTest`, `AmContrastPairsTest`, `AmAccentTest` |
| Tipografía y layout | `AmTypographyTest`, `AmPivotLayoutTest`, `AmUiPolishContractTest` |
| Accesibilidad de componentes | `AmAccessibilityContractTest`, `ScreensA11yCoverageTest` |
| Regresión visual | `recordVisualHashes`/`verifyVisualHashes` (ADR-0007) |
| Documentación | `verifyDocs` |
| Agregador | `verifyAgendaQrArchitecture` |

## 14. Gobernanza

1. Cambio visual = cambio de token en `AmTokens.kt` + actualización del pin en `AmSchemeTest` + regeneración de capturas (`recordRoborazzi` → `recordVisualHashes`) — nunca un literal en la pantalla.
2. Componente nuevo: consumidor real, contrato documentado aquí, pruebas de estado/accesibilidad; sin abstracciones sin consumidor.
3. Los retiros de componentes se registran en el ADR que los decide.
4. Decisiones estructurales del sistema visual: ADR.

## 15. Excepciones deliberadas (ADR-0020, ADR-0022)

Las excepciones del sistema visual y de la plataforma se documentan aquí. No existe requisito de fidelidad a una librería externa.

| Excepción | Motivo |
|---|---|
| Foco visible por teclado/D-pad/Switch Access conservado (el Anexo A dice "foco: no aplica" para entorno táctil) | Accesibilidad: el teclado y el switch necesitan indicador de foco; el estado táctil es escala 0.95 + háptica, no anillo |
| Sombras E-1/E-2/E-3 mediante `AmElevation` y excepciones E-05/E-06 | El Anexo A exige sombras; el gate las permite por token y excepción registrada, no desactivando la regla |
| Rejilla de Inicio ("King Kong") con solo acciones existentes y retiro del FAB con hub de Inicio | El Anexo A pide rejilla; se implementa sin duplicar acciones (enmienda a S01) |
| Importe con la tipografía del Anexo A y color neutro con signo (no rojo) | No hay precios comerciales; el rojo en finanzas se lee como egreso/peligro; pintarlo rojo es un cambio de un token |
| Escala 14/12 con mínimo absoluto 11 sp | Anexo A (cuerpo 14, caption 12); el 11 protege la legibilidad de badges y etiquetas de barra |
| Destructiva sin gradiente (contorno/tono + glifo + consecuencia + confirmación) | La acción primaria y la destructiva no pueden compartir el rojo de marca como relleno dominante |
| Ningún dato simulado (carrito, cuentas regresivas, monedas, niveles, "HOT/限时/-50 %", servicio flotante, insignias de conteo) | El producto no tiene esas funciones ni datos; no se inventan |
| Barra superior 56 dp con título 20 (H1) a la izquierda | Convención Android/iOS y objetivos táctiles de 48 dp |
| Barra inferior 56 dp, etiqueta 11 y selección sin píldora | Convención de plataforma y legibilidad |
| Buscador de 48 dp mínimo | Objetivo táctil mínimo (gate `verifyControlMinSize`) |
| Snackbar inferior en vez de aviso centrado | Convención Android; feedback consistente con el sistema |
| FAB extendido en píldora | Convención Android (sin equivalente en la plataforma) |
| Superficies oscuras con matiz azul-gris | Diferenciación de superficies sin sombras generalizadas |
| `AmTextField` con etiqueta persistente y guía hairline | Etiqueta persistente y estado de foco/error accesibles |
| Placeholder en texto secundario | Contraste AA |
| Segmentado sin sombra en el ítem activo (el diseño de origen usa sombra suave) | El gate prohíbe `shadow(`; el límite lo da el bloque tonal |
| Action-sheet y selectores como `AmDialog` con lista; hoja inferior en composición no usada | Las acciones de la hoja inferior no se despachan de forma fiable bajo Robolectric |
| Recalibraciones de paleta y contenido de acento | La accesibilidad y la legibilidad prevalecen sobre el parecido con cualquier referencia |

## 16. Referencias de patrón por pantalla (ADR-0021, ADR-0022)

La dirección visual vigente es la Super-App de alta densidad (`08-referencia-super-app.md`, ADR-0022). Las referencias inspiran composición y jerarquía; no gobiernan tokens ni se copian activos (iconos, ilustraciones, logotipos, textos, colores de marca, composiciones calcadas).

| Superficie | Referencia de patrón |
|---|---|
| Inicio | Super-App (bazar denso): cabecera con gradiente, búsqueda en píldora, rejilla de acciones existentes, favoritos en carrusel y lista densa (ADR-0022 enmienda S01) |
| Detalle de destino / QR | Alipay (código de cobro) |
| Buscar y resultados | WeChat (búsqueda) |
| Contextos | WeChat (chats y contactos) |
| Actividades, detalle e importación | Alipay y WeChat Pay (detalle de factura) |
| Formularios y acceso local | HyperOS y WeChat (ajustes) |
| Navegación raíz | WeChat (barra inferior de 4 destinos) |

Cualquier cambio visual derivado de esta tabla actualiza las capturas y sus hashes generados en local (ADR-0007); la accesibilidad (§10) prevalece sobre el parecido con la referencia. La auditoría de brecha por pantalla y su estado (aprobada, implementada o pendiente) viven en ADR-0021; este contrato solo fija la referencia de patrón.

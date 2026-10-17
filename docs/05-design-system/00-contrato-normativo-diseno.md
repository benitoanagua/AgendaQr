# Contrato normativo de diseño

**Estado:** Normativo — versión 3.1 (ADR-0020, ADR-0021)
**Autoridad:** este documento es la fuente única y obligatoria de la identidad visual de AgendaQr. La dirección visual es propia, inspirada en aplicaciones móviles chinas de consumo (utilidad cotidiana, jerarquía funcional, densidad controlada y acabado pulido), sin replicar una librería concreta. **Android e iOS definen las convenciones de navegación y comportamiento de plataforma**. La implementación vive en `core/ui` (`com.agendaqr.core.ui`, prefijo `Am*` como convención de código, sin valor de marca). Material 3 es detalle de implementación, no autoridad visual.

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
- `RoundedCornerShape`/`CutCornerShape`/`CircleShape`/`RectangleShape`/`shadow(` fuera de la capa de tokens; los componentes consumen `AmShapes`.
- `MaterialTheme.colorScheme` como autoridad fuera de `AmTheme.kt`; los componentes leen `AmColor`.
- Iconos Material en producción: la iconografía es Lucide (ISC) SOLO vía `AmIcons`/`AmIcon`.
- Controles M3 (`Button`, `TextField`, `AlertDialog`, `FilterChip`…) importados en `feature/`, `shared/` o `androidApp/`: se componen los equivalentes del sistema.
- Copy de UI literal en `feature/` (fuera de `AppStrings`) o en `components/` (todo texto llega como parámetro, ADR-0008).
- `.clickable(` crudo o `indication = null` en `feature/`: la interacción vive en el sistema.
- Duraciones de animación literales: se consumen `AmMotion`.
- `.border(`/`BorderStroke(` en producción: la separación usa hairlines (`Divider`), el foco y el estado usan componentes M3 y canvas.

## 4. Color e identidad de marca

La paleta expresa una herramienta digital moderna: azul tecnológico para la acción principal, superficies neutras y profundas, y tonos semánticos reservados a estados. No se usa rojo/dorado como decoración cultural ni se colorea cada tarjeta por separado.

### 4.1 Roles semánticos

`AmColorScheme` expone exactamente: `background`, `surface`, `surfaceSecondary`, `border`, `divider`, `textPrimary`, `textSecondary`, `textTertiary`, `accent`, `onAccent`, `accentContainer`, `onAccentContainer`, `accentText`, `wathet`, `danger`, `onDanger`, `dangerContainer`, `success`, `successContainer`, `warning`, `warningContainer`, `info`, `infoContainer`, `focus`, `disabled`, `onDisabled`, `inverseSurface`, `inverseOnSurface`, `inverseAccent`, `qrCanvas`, y los contenedores M3 explícitos (`surfaceContainerLow/Container/High/Highest`).

### 4.2 Paleta base

| Rol | Claro | Oscuro |
|---|---|---|
| Página | `#F7F8FA` | `#10141B` |
| Superficie | `#FFFFFF` | `#171D26` |
| Superficie secundaria | `#F1F4F8` | `#0C1118` |
| Texto principal | `#1F2937` | `#F1F5F9` |
| Texto secundario | `#596579` | `#C1CAD6` |
| Texto terciario | `#5D6879` | `#A9B5C5` |
| Divisor/borde | `#E2E7EF` | `#303844` |
| Acento de acción | `#245FE5` | `#70A5FF` |
| Texto de acento | `#245FE5` | `#80B0FF` |
| Fondo de selección | `#E8F0FF` | `#172B49` |

El acento claro con texto blanco cumple 5.46:1. El acento oscuro usa tinta `#101B2E` (6.99:1); el texto de acento oscuro usa `#80B0FF` para mantener legibilidad sobre superficies. Los valores exactos quedan fijados por `AmSchemeTest`.

### 4.3 Estados semánticos

| Estado | Claro (contenido / contenedor) | Oscuro (contenido / contenedor) |
|---|---|---|
| Éxito | `#087443` / `#E2F7EB` | `#5DDBA5` / `#123628` |
| Advertencia | `#8A4B00` / `#FFF2D9` | `#FFC15A` / `#3A2B0E` |
| Peligro | `#C62828` / `#FDECEC` | `#FF6B78` / `#3A151D` |
| Información | Acento / fondo de selección | Acento / fondo de selección |

Los estados siempre combinan tono con texto, icono o semántica accesible; nunca dependen solo del color. La identidad de contexto conserva sus 12 acentos deterministas, independientes del acento de marca y sin cambiar el dominio.

### 4.4 Contraste y superficies

- Texto normal y contenido de estado: contraste ≥ 4.5:1 sobre las superficies donde se presenta.
- Foco e indicadores funcionales: contraste ≥ 3:1 respecto a las superficies adyacentes.
- El par deshabilitado mantiene un suelo de calidad ≥ 3:1; el estado también se expresa semánticamente.
- Las superficies claras son neutras; las oscuras usan matiz azul-gris para dar profundidad sin sombras generalizadas.
- Los contenedores semánticos deben admitir texto principal y acciones sin perder contraste; `AmSchemeTest` y `AmContrastPairsTest` son la autoridad ejecutable.
- `qrCanvas` permanece blanco en ambos temas para preservar la zona de silencio escaneable del QR.

## 5. Tipografía

- Familia de sistema en todos los roles (Roboto/San Francisco): ninguna fuente se empaqueta en esta fase. Es una decisión consciente para preservar el funcionamiento offline-first y evitar licencias/recursos multiplataforma hasta validar una alternativa en capturas reales. MiSans Global y una familia monoespaciada quedan como experimentos locales, no como dependencias aprobadas.
- Roles semánticos de AgendaQr: título de pantalla (`AmType.TitleLarge`, 18 sp, en `AmTopBar` de 56 dp), título de diálogo (`AmTextStyles.Title`, 18 sp centrado, bold), cuerpo/botón/título de fila/entrada/pestañas (`AmTextStyles.Body`/`ButtonLabel`/`RowTitle`, 17 sp), etiqueta/contenido de modal/cápsula/extra de lista (`AmTextStyles.Label`, 15 sp), descripción y metadato (`BodySmall`, 13 sp — font-size-main), texto de ayuda/error (`AmTextStyles.Support`/`Caption`, 12 sp), tags (`AmTextStyles.Tag`, 11 sp peso normal), etiquetas de barra (11 sp por legibilidad). Importe (`AmTextStyles.Amount`, 26 sp SemiBold), Display 32 y Headline 24 son extensiones financieras sin equivalente en la referencia. La etiqueta de botón conserva peso Medium (desviación documentada). Tracking neutro en todos los roles.
- Interlineado siempre > 1.1× su tamaño (`AmUiPolishContractTest`); el escalado de fuente 100/150/200 % se respeta y se prueba.
- El tracking sigue los valores por defecto de M3 (`AmTracking`).

## 6. Espaciado y métrica

- Escala: 4 · 8 · 12 · 16 · 20 · 24 · 32 (+40 excepcional) — `AmSpacing`; margen de pantalla 16; densidad compacta y legible de AgendaQr (celda 12+12, hairlines inset a 56 dp bajo la marca de fila — `ListDividerInset`).
- `AmMetrics`: área táctil mínima 48 dp (`ControlMinSize`, invariante del gate) con **separación de superficie visible y zona interactiva**: la superficie VISIBLE de botones secundarios (40 dp, `ButtonCompactHeight`), píldoras (~32 dp, `ChipCompactHeight`), entradas (44 dp, `FieldCompactHeight`) y acciones de fila (~36 dp) puede ser menor que su zona interactiva de 48 dp; el buscador mide 48 dp mínimo (desviación documentada frente a los 32 px de la referencia); barra superior 56 dp (`TopBarHeight`), barra inferior 56 dp (`NavBarHeight`; cada ítem garantiza 48 dp), icono estándar 24 dp, icono compacto 18 dp (`IconSizeCompact`), `QrPreviewSize` 240, `ContentMaxWidth` 720 (medium/expanded), `DialogListMaxHeight` 320, `DialogMinWidth` 280 (mínimo del popup) con tope del 75 % del ancho (`DialogMaxWidthFraction`), marco del scanner 3 dp (`BorderStrong`), glifo del estado vacío 64 dp, ancho de mini-tarjeta de frecuentes 184 (`FavCardWidth`).
- Radios (escala oficial 2/4/6/8/12): tag/selector/segmentado 2 (`ExtraSmall`, con `round` opt-in en tags), botón 4 (`Small`), búsqueda 6 (`Medium`), tarjeta de lista/tappable/media/diálogo/banner 8 (`Large`), hoja inferior 12 (`ExtraLarge`, hoy sin uso); píldora para chips de filtro estilo CapsuleTabs, FAB y píldoras de estado. El hairline perimetral de las tarjetas es la excepción E-03; el borde de acento en foco del buscador es la excepción E-04.

## 7. Iconografía

- Lucide (ISC + MIT para glifos derivados de Feather) vía `AmIcons`; licencia registrada en `06-licencias-terceros.md`. Sin mezcla de familias; sin emojis funcionales.
- Un glifo por concepto; set mínimo con glifos de estado y de estados vacíos. Glifos financieros (ADR-0015, referencia Stitch): `Income` (flecha entrante, cobro), `Expense` (flecha saliente, pago), `Attachment` (comprobante adjunto) — decorativos; el texto de la fila comunica el tipo.
- Icono decorativo = `contentDescription = null` (excluido del lector); todo icono interactivo lleva etiqueta de texto visible compuesta por el llamador.

## 8. Movimiento

- `AmMotion`: 150 ms corto; easing estándar `cubic-bezier(0.1, 0.9, 0.2, 1)` (transición de la referencia 0.15s).
- Transición de superficies: fundido + desplazamiento de 1/8 del ancho (`AmSurfaceSlideFraction`); con movimiento reducido: duración 0 (`LocalReducedMotion`/`ProvideReducedMotion`).
- Sin animaciones decorativas; la interacción es clara sin movimiento.

## 9. Componentes y estados (lenguaje visual AgendaQr)

| Grupo | Componentes | Referencia |
|---|---|---|
| Estructura | `AmScreenScaffold`, `AmTopBar` (+ acciones, overflow; `statusBarsPadding` propio), `AmNavBar`/`AmNavRail` (+ destinos, estado; `navigationBarsPadding` propio), `AmExtendedFab` (compacto 48 dp, etiqueta 13/600) | Barra inferior de destinos raíz; barra superior jerárquica con Atrás (título a la izquierda por convención Android); FAB extendido de plataforma. **Edge-to-edge: los slots de Scaffold NO reciben insets — cada barra aplica los suyos (`statusBarsPadding`/`navigationBarsPadding`); en Robolectric los insets son 0, así que esta regla se valida en dispositivo/instrumentados** |
| Acciones | `AmPrimaryButton` (solid 48), `AmSecondaryButton` (botón `default` de la referencia: fondo claro + hairline + texto primario; compacto 40 visual / 48 táctil, variante `solid`), `AmTextAction` (none), `AmDangerButton` (solid), `AmIconButton`, `AmFilterChip` (cápsula estilo CapsuleTabs: reposo box sin borde, activa sólida de acento, etiqueta 15, conteo y glifo opcionales, ~32 visual / 48 táctil), `AmCompactAction` (acción de fila/tarjeta ~36 visual / 48 táctil), `AmSegmentedControl` (pista box, item activo en superficie, radios 2, opciones con glifo y tono activo), `AmFavoriteToggle` | Fills solid/default/none de la referencia; cápsulas con conteo; separación de superficie visible y zona interactiva |
| Formularios | `AmTextField` (patrón Form/List con etiqueta flotante persistente — desviación documentada —, guía hairline en foco/error; label + helper + error accesible + glifo de concepto opcional), `AmAmountField` (importe 26 sp con borrado etiquetado y guía hairline), `AmSearchField` (SearchBar: box, radio 6, borde de acento en foco E-04, 48 dp mínimo, sin etiqueta flotante; teclado configurable, autofoco opt-in), `AmSearchTrigger` | Input + Form/List; placeholder en texto secundario con contraste AA fijado por los tokens y sus pruebas; limpieza etiquetada; foco al entrar en Buscar |
| Superposiciones | `AmDialog` (Dialog de la referencia: popup radio 8, título 18 bold centrado, contenido 15, fila de acciones a ancho completo con hairlines; foco conservativo en destructivas), `AmDialogList` | Dialog centrado; sin hoja inferior (limitación Robolectric documentada) |
| Datos | `AmListRow` (título 17, descripción 13, `meta` en línea propia **solo metadatos/badges — prohibido colocar acciones ahí**, `leading` tipado — tile tonal 40 dp —, `showChevron` opt-in para filas que navegan, divisor opt-in con inset a 56 dp, semántica `selected`), `AmListBody` (tarjeta radio 8 con hairline perimetral E-03, padding interno 0 por defecto — filas), `AmCard` (tarjeta de superficie para contenido libre, padding interno `AmSpacing.Lg` por defecto), `AmTappableCard` (tarjeta presionable radio 8: interacción y semántica en el DS), `AmHairline`, `AmBadge`/`AmTonalTag` (Tag: radio 2 con `round` opt-in, 11 sp normal, mayúsculas tipadas), `AmIconBadge` (tile 40), `AmMetaRow` (metadatos label/valor con acción final), `AmSectionHeader` (List header: 15, texto secundario, peso normal), `AmSection`, `AmPivot` (Tabs con indicador de acento, título 17) | List/Item, Tag outline/round, Tabs, cápsulas con conteo, metadatos compactos |
| Estados | `AmStatusBanner` (NoticeBar: radio 8; layout en **columna**: fila 1 icono + mensaje a ancho completo, fila 2 acciones al final con envoltura; API `AmBannerAction` para action/dismiss, sin estado inválido posible), `AmStatusPill` (variante `rounded`: reaseguro tonal o inverso "Modo local"), `AmLoading`, `AmEmptyState` (glifo decorativo + título + acción opcional), `AmSkeleton`, `AmLoadMoreFooter`, `AmQuickAction` (tile de acción rápida S01) | NoticeBar; Empty; aviso persistente de producto |
| Feedback | `AmFeedbackHost`/`AmSnackbarHost`/`AmFeedbackEvent` (háptica + anuncio + snackbar inferior en Android; contenedor en superficie inversa con radio 8 y texto 15, como el Toast oficial) | Toast de plataforma; anuncio por evento |
| Medios | `AmQrPreview` (expect/actual, tamaño configurable 1:1, revisión a 320 dp; fallo de decodificación = glifo explícito, nunca rectángulo blanco), `AmMediaFrame` (Surface con radio 8, la tarjeta de la referencia: el límite es el bloque tonal, sin bordes — gate E-01/E-02), `AmScannerFrame` (esquinas de acento en canvas sobre el preview real; el viewport tonal se retiró por reservar espacio vacío) | Card (radio 8) |
| Tono | `AmTone` (Neutral/Success/Danger/Warning/Info) con par content/container y glifo por tono | Colores semánticos de la referencia |

Estados obligatorios: normal, pressed (indicación M3), focused (indicación de foco del componente M3 con el rol `AmColor.Focus`), selected (semántica + borde de acento/fondo wathet), error (icono + texto + `error()` semántica), disabled (opacidad .4 + par `Disabled/OnDisabled`), loading (spinner superpuesto sin salto de ancho + `stateDescription`).

### Jerarquía de acciones (4 niveles, contracto §9.1)

Toda acción de la app pertenece a un nivel semántico; el nivel decide el componente,
no al revés. Documentación detallada en la javadoc de `AmButtons.kt`; cobertura
comportamental en `ActionHierarchyTest` y `ScreensA11yCoverageTest`.

| Nivel | Componente | Tratamiento | Regla de uso |
|---|---|---|---|
| 1 — Primaria | `AmPrimaryButton` / `AmBottomAction` | Relleno de acento | El objetivo principal del usuario por sección; **como máximo una** por contexto; en lectura/búsqueda/selección puede no existir |
| 2 — Secundaria | `AmSecondaryButton` | Tonal (contenedor bajo + hairline); variante `solid` solo para la acción insignia de una tarjeta; **a ancho completo cuando el llamador pasa `fillMaxWidth()` (la superficie visible ocupa todo el ancho)** | Alternativa importante; nunca compite con la primaria; una destacada por contexto |
| 3 — Terciaria | `AmTextAction` | Texto con target de 48 dp; variantes `Default` (acento), `Neutral` (texto primario semibold) y `Danger` (destructiva); prohibido usar grises como color de acción. Descartar se alinea siempre a la derecha (`Arrangement.End`) en todas las tarjetas de pendientes | Acciones contextuales y navegación auxiliar |
| 4 — Destructiva | `AmDangerButton` / `AmDialog(destructive = true)` | Tono peligro + texto específico de consecuencia; confirmación si hay pérdida irreversible | Nunca compite con la primaria; el color nunca es la única señal (glifo o etiqueta específica) |

Reglas derivadas verificadas en los flujos: etiquetas específicas de consecuencia en
diálogos (nada de «Aceptar» genérico); acciones destructivas dentro del contenido del
diálogo cuando la salida segura es el slot de descarte; descarte con confirmación solo
cuando se pierde información no recuperable; etiquetas completas sin elipsis (los
botones apilan a ancho disponible antes de comprimir texto).

### Nota sobre la hoja inferior

El patrón de hoja para selectores (ActionSheet de la referencia) se evaluó con `ModalBottomSheet` de M3 y se retiró: la ventana de la hoja no despacha acciones semánticas bajo Robolectric (el contrato de pruebas UI de este repositorio), dejando el flujo S08 inverificable en el gate. Los selectores permanecen como `AmDialog` con lista desplazable. Reevaluar cuando el entorno lo soporte (ver ADR-0013/ADR-0014, riesgos).

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
| E-01 | Hairline perimetral del botón secundario (botón `default` de la referencia: fondo claro + borde hairline + texto primario; NO es outline de acento) | `border:AmButtons.kt` — verificado por la suite (`AmSecondaryButton` se renderiza y conserva 48 dp en `AmResponsiveLayoutContractTest`/`FontScaleContractTest`) |
| E-02 | Borde outline de la insignia de estado (Tag outline de la referencia: borde de 1 dp del tono, no hairline decorativo) | `border:AmLists.kt` — verificado por la suite (`AmBadge` se renderiza en `AppStatusHostTest`/`ScreensA11yCoverageTest`) |
| E-03 | Hairline perimetral del cuerpo de lista/tarjeta (`AmListBody`/`AmTappableCard`: "Flat #ffffff surface delineated by a crisp 1px solid #f0f0f0 border", DESIGN.md Stitch — el límite de la tarjeta es el borde, no una sombra) | `border:AmLists.kt` — verificado por las capturas de referencia (ADR-0007) |
| E-04 | Borde de acento en foco del buscador (SearchBar de la referencia: borde 1 px del color primario cuando el campo está activo; en reposo el borde es transparente) | `border:AmInputs.kt` — verificado por la suite (el campo se renderiza y conserva su nombre accesible en `ScreensA11yCoverageTest`; el foco cumple ≥ 3:1 por `AmSchemeTest`) |

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

## 15. Excepciones deliberadas (ADR-0020)

Las excepciones del sistema visual y de la plataforma se documentan aquí. No existe requisito de fidelidad a una librería externa.

| Excepción | Motivo |
|---|---|
| Barra superior 56 dp con título 18 a la izquierda | Convención Android/iOS y objetivos táctiles de 48 dp |
| Barra inferior 56 dp, etiqueta 11 y selección sin píldora | Convención de plataforma y legibilidad |
| Buscador de 48 dp mínimo | Objetivo táctil mínimo (gate `verifyControlMinSize`) |
| Snackbar inferior en vez de Toast centrado | Convención Android; feedback consistente con el sistema |
| FAB extendido en píldora | Convención Android (sin equivalente oficial) |
| Superficies oscuras con matiz azul-gris | Diferenciación de superficies sin sombras generalizadas |
| `AmTextField` con etiqueta persistente y guía hairline | Etiqueta persistente y estado de foco/error accesibles |
| Placeholder en texto secundario | Contraste AA |
| Etiqueta de botón en Medium | Jerarquía legible en acciones compactas |
| Segmentado sin sombra en el ítem activo (oficial: sombra suave) | El gate prohíbe `shadow(`; el límite lo da el bloque tonal |
| Hoja inferior 12 sin uso; selectores como `AmDialog` con lista | `ModalBottomSheet` no despacha acciones bajo Robolectric (bloqueo documentado) |
| Recalibraciones de paleta y contenido de acento | La accesibilidad y la legibilidad prevalecen sobre cualquier referencia externa |

## 16. Referencias de patrón por pantalla (ADR-0021)

Las referencias inspiran composición y jerarquía; no gobiernan tokens ni se copian activos (iconos, ilustraciones, logotipos, textos, colores de marca, composiciones calcadas).

| Superficie | Referencia de patrón |
|---|---|
| Inicio | Alipay (inicio) y WeChat (búsqueda) |
| Detalle de destino / QR | Alipay (código de cobro) |
| Buscar y resultados | WeChat (búsqueda) |
| Contextos | WeChat (chats y contactos) |
| Actividades, detalle e importación | Alipay y WeChat Pay (detalle de factura) |
| Formularios y acceso local | HyperOS y WeChat (ajustes) |
| Navegación raíz | WeChat (barra inferior de 4 destinos) |

Cualquier cambio visual derivado de esta tabla actualiza las capturas y sus hashes generados en local (ADR-0007); la accesibilidad (§10) prevalece sobre el parecido con la referencia.

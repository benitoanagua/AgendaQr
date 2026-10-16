# Contrato normativo de diseño

**Estado:** Normativo — versión 2.1 (ADR-0017)
**Autoridad:** este documento es la fuente única y obligatoria de la capa visual de Agenda QR. La **referencia visual es Ant Design Mobile** (variables de tema, componentes y patrones oficiales); **Android e iOS definen las convenciones de navegación y comportamiento de plataforma**. La implementación vive en `core/ui` (`com.agendaqr.core.ui`, prefijo `Am*` como convención de código, sin valor de marca). Vant no es referencia ni dependencia. Material 3 es detalle de implementación, no autoridad.

## 1. Principios

1. **Referencia única y auditable**: Ant Design Mobile define paleta, superficies, texto, estados, radios, densidad, controles y feedback. Cada desviación (plataforma o accesibilidad) se documenta y se fija por prueba.
2. **Navegación convencional de plataforma**: destinos raíz con barra inferior en compacto, barra superior jerárquica con Atrás, back stack con restauración, back del sistema y predictive back en Android, gestos en iOS. El chrome de navegación sigue la plataforma aunque la referencia web difiera (título centrado, otras densidades).
3. **Mobile-first real**: se diseña para el pulgar en un teléfono Android de 360 dp; la adaptación a tablet/escritorio (compacto → medium/expanded vía `WindowSizeClass`) nunca degrada el patrón compacto.
4. **Material 3 es implementación, no autoridad**: los componentes M3 se adaptan con tokens; ningún color, radio, altura o tipografía se define por defecto de la plantilla.
5. **El estado nunca es solo color**: tono + texto/icono + semántica accesible en cada estado (carga, error, selección, deshabilitado, foco).
6. **Contraste como contrato**: texto ≥ 4.5:1, bordes/foco ≥ 3:1, verificado por `AmSchemeTest` y `AmContrastPairsTest`. Donde la referencia no alcanza AA, la accesibilidad prevalece con recalibración mínima documentada (§4.4).
7. **Radios de la referencia**: 2/4/6/8/12 dp (`AmRadius`) como única escala de forma (tag 2, botón 4, búsqueda 6, tarjeta/diálogo 8, hoja 12).
8. **Movimiento corto y justificado**: 150 ms con `AmMotion`; movimiento reducido = cambio inmediato (duración 0), nunca "una animación más corta".

## 2. Arquitectura de capas

| Capa | Ubicación | Responsabilidad |
|---|---|---|
| 1. Tokens privados | `AmTokens.kt` → `AmPalette` (internal) | Primitivas por tema; sin exposición al producto |
| 2. Tokens semánticos | `AmTokens.kt` → `AmColorScheme`, `AmColor`, `AmSpacing`, `AmRadius`/`AmShapes`, `AmMetrics`, `AmType`/`AmTextStyles`, `AmMotion`, `AmAccent`/`AmAccents`/`accentFor` | La ÚNICA capa que el producto consume |
| 3. Tema | `AmTheme.kt` | Puente claro/oscuro a Material 3; esquema M3 derivado COMPLETO (incluye contenedores) |
| 4. Componentes | `components/` | Réplicas Compose de la referencia con tokens; contratos de estado y accesibilidad |
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

## 4. Color (referencia: variables de tema de Ant Design Mobile)

### 4.1 Roles semánticos

`AmColorScheme` expone exactamente: `background`, `surface`, `surfaceSecondary`, `border`, `divider`, `textPrimary`, `textSecondary`, `textTertiary`, `accent`, `onAccent`, `accentContainer`, `onAccentContainer`, `accentText`, `wathet`, `danger`, `onDanger`, `dangerContainer`, `success`, `successContainer`, `warning`, `warningContainer`, `info`, `infoContainer`, `focus`, `disabled`, `onDisabled`, `inverseSurface`, `inverseOnSurface`, `inverseAccent`, `qrCanvas`, y los contenedores M3 explícitos (`surfaceContainerLow/Container/High/Highest`).

### 4.2 Superficies y texto (valores de la referencia)

- **Claro**: página `#F5F5F5` (box), superficie `#FFFFFF`, secundaria `#F5F5F5`; texto `#333333`, secundario `#666666`.
- **Oscuro**: página `#1A1A1A` (background), superficie `#1F1F1F` (elevación mínima para distinguir tarjetas del fondo — desviación documentada, la referencia no define elevación), secundaria `#0A0A0A` (box, rellenos inset); texto `#E6E6E6`, secundario `#B3B3B3`.
- **Separadores**: hairline `#EEEEEE` / `#2B2B2B` (border oficial de la referencia en ambos temas).
- **Lienzo del QR** (`qrCanvas`): blanco garantizado en ambos temas (zona de silencio escaneable); no conmuta con el tema.

### 4.3 Acento y estados

- **Acento**: primario de la referencia, recalibrado a `#1668DC` en claro (ver §4.4); oscuro `#3086FF` (oficial) con texto de tinta `#0A1120` (5.37:1).
- **wathet** (`#E7F1FF` / `#0D2543`): tinte de la referencia para fondos de selección (chips, pestañas), adoptado sin cambios en ambos temas. El contenido sobre wathet es `accentText` (no el acento puro), que garantiza AA (ver §4.4).
- **Éxito/advertencia/peligro**: de la referencia, recalibrados en claro (`#0A7A44`, `#A85A00`, `#D21F2E`; ver §4.4); oscuro oficial (`#34B368`/`#FFA930`/`#FF4A58`, este último con tinta `#1A1A1A`) con contenedores `#0E2C1C`/`#3A2B0E`/`#3A1512`.
- **Informativo**: la referencia no define tono info; lo informativo usa el acento (`info` = acento, `infoContainer` = wathet, contenido sobre el contenedor = `accentText`).
- **Identidad de contexto**: 12 tonos + acento de sistema (`AmAccents`), par fijo negro/blanco por acento, asignación determinista por `accentFor` (FNV-1a 32 bits sobre UTF-8, idéntico en Android e iOS). Congelada: los valores no cambian para no reasignar identidades.

### 4.4 Recalibraciones AA documentadas (la accesibilidad prevalece sobre la referencia)

| Valor de referencia | Problema | Rol calibrado |
|---|---|---|
| Primario `#1677ff` + blanco 4.1:1 | Falla AA como relleno y como texto | Acento claro `#1668DC` (5.19:1 en ambas direcciones) |
| Éxito `#00b578` 2.7:1 | Falla AA | Éxito claro `#0A7A44` (5.41:1) |
| Peligro `#ff3141` ~4.0:1 | Falla AA | Peligro claro `#D21F2E` (5.28:1) |
| Advertencia `#ff8f1f` 2.28:1 | Falla AA | Advertencia clara `#A85A00` (5.09:1 sobre `FFFFFF`, 4.67:1 sobre `F5F5F5`, 4.61:1 sobre `FDF3DC`) |
| Terciario `#999999` 2.9:1 | Falla AA como texto | Terciario claro `#6E6E6E` (5.10:1 sobre `FFFFFF`, 4.68:1 sobre `F5F5F5`) / oscuro `#8C8C8C` (5.18:1 sobre `1A1A1A`, 4.90:1 sobre `1F1F1F`; el oficial `#808080` = 4.41:1, falla) |
| Placeholder `#cccccc` 1.61:1 | Falla AA | Placeholder en texto secundario (desviación documentada, ver §"Desviaciones deliberadas") |
| Oscuro: blanco sobre `#3086ff` 3.51:1 | Falla AA | Tinta `#0A1120` sobre acento (5.37:1); acento como texto `#4F96FF` (5.92:1 sobre `1A1A1A`, 5.60:1 sobre `1F1F1F`, 5.24:1 sobre wathet) |
| Oscuro: blanco sobre `#ff4a58` 3.30:1 | Falla AA | Tinta `#1A1A1A` sobre peligro (5.28:1); peligro como texto 5.28:1 sobre `1A1A1A`, 5.00:1 sobre `1F1F1F` |
| Oscuro: acento `#3086ff` como TEXTO sobre wathet oficial `#0d2543` 4.39:1 | Falla AA como texto | Wathet oficial `#0D2543` sin cambios; el contenido informativo usa `accentText` `#4F96FF` (5.24:1); el acento puro sobre wathet se reserva a glifos y bordes |
| Oscuro: éxito `#34b368` / advertencia `#ffa930` | Cumplen AA (6.46:1 / 9.09:1 sobre `1A1A1A`) | Se adoptan sin cambios |

### 4.5 Umbrales (AmSchemeTest)

- Contenido sobre su fondo/container: ≥ 4.5:1 en ambos temas (texto, tonos de estado, acento como texto, contenido informativo sobre wathet con `accentText`).
- `focus` contra `background`/`surface`/`surfaceSecondary`: ≥ 3:1.
- Par deshabilitado `onDisabled`/`disabled`: ≥ 3:1 (suelo de calidad propio; WCAG exime los controles deshabilitados; los botones además usan opacidad .4 de la referencia).
- Los valores exactos están fijados (pin anti-cambio-silencioso) en `AmSchemeTest`.

## 5. Tipografía

- Familia de sistema en todos los roles (Roboto/San Francisco): ninguna fuente se empaqueta. La referencia usa su propia fuente; la diferencia es intencional (offline-first, sin descargas ni licencias por plataforma — ADR-0015).
- Roles semánticos (escala de la referencia): título de pantalla (`AmType.TitleLarge`, 18 sp, en `AmTopBar` de 56 dp), título de diálogo (`AmTextStyles.Title`, 18 sp centrado, bold), cuerpo/botón/título de fila/entrada/pestañas (`AmTextStyles.Body`/`ButtonLabel`/`RowTitle`, 17 sp), etiqueta/contenido de modal/cápsula/extra de lista (`AmTextStyles.Label`, 15 sp), descripción y metadato (`BodySmall`, 13 sp — font-size-main), texto de ayuda/error (`AmTextStyles.Support`/`Caption`, 12 sp), tags (`AmTextStyles.Tag`, 11 sp peso normal), etiquetas de barra (11 sp — la referencia usa 10; se conserva 11 por legibilidad). Importe (`AmTextStyles.Amount`, 26 sp SemiBold), Display 32 y Headline 24 son extensiones financieras sin equivalente en la referencia. La etiqueta de botón conserva peso Medium (desviación documentada). Tracking neutro en todos los roles.
- Interlineado siempre > 1.1× su tamaño (`AmUiPolishContractTest`); el escalado de fuente 100/150/200 % se respeta y se prueba.
- El tracking sigue los valores por defecto de M3 (`AmTracking`).

## 6. Espaciado y métrica

- Escala: 4 · 8 · 12 · 16 · 20 · 24 · 32 (+40 excepcional) — `AmSpacing`; margen de pantalla 16; densidad de listas de la referencia (celda 12+12, hairlines inset a 56 dp bajo la marca de fila — `ListDividerInset`).
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

## 9. Componentes y estados (referencia: catálogo oficial)

| Grupo | Componentes | Referencia |
|---|---|---|
| Estructura | `AmScreenScaffold`, `AmTopBar` (+ acciones, overflow; `statusBarsPadding` propio), `AmNavBar`/`AmNavRail` (+ destinos, estado; `navigationBarsPadding` propio), `AmExtendedFab` (compacto 48 dp, etiqueta 13/600) | Barra inferior de destinos raíz; barra superior jerárquica con Atrás (título a la izquierda por convención Android); FAB extendido de plataforma. **Edge-to-edge: los slots de Scaffold NO reciben insets — cada barra aplica los suyos (`statusBarsPadding`/`navigationBarsPadding`); en Robolectric los insets son 0, así que esta regla se valida en dispositivo/instrumentados** |
| Acciones | `AmPrimaryButton` (solid 48), `AmSecondaryButton` (botón `default` de la referencia: fondo claro + hairline + texto primario; compacto 40 visual / 48 táctil, variante `solid`), `AmTextAction` (none), `AmDangerButton` (solid), `AmIconButton`, `AmFilterChip` (cápsula estilo CapsuleTabs: reposo box sin borde, activa sólida de acento, etiqueta 15, conteo y glifo opcionales, ~32 visual / 48 táctil), `AmCompactAction` (acción de fila/tarjeta ~36 visual / 48 táctil), `AmSegmentedControl` (pista box, item activo en superficie, radios 2, opciones con glifo y tono activo), `AmFavoriteToggle` | Fills solid/default/none de la referencia; cápsulas con conteo; separación de superficie visible y zona interactiva |
| Formularios | `AmTextField` (patrón Form/List con etiqueta flotante persistente — desviación documentada —, guía hairline en foco/error; label + helper + error accesible + glifo de concepto opcional), `AmAmountField` (importe 26 sp con borrado etiquetado y guía hairline), `AmSearchField` (SearchBar: box, radio 6, borde de acento en foco E-04, 48 dp mínimo, sin etiqueta flotante; teclado configurable, autofoco opt-in), `AmSearchTrigger` | Input + Form/List; placeholder en texto secundario (desviación: el `light` oficial no alcanza AA); limpieza etiquetada; foco al entrar en Buscar |
| Superposiciones | `AmDialog` (Dialog de la referencia: popup radio 8, título 18 bold centrado, contenido 15, fila de acciones a ancho completo con hairlines; foco conservativo en destructivas), `AmDialogList` | Dialog centrado; sin hoja inferior (limitación Robolectric documentada) |
| Datos | `AmListRow` (título 17, descripción 13, `meta` en línea propia **solo metadatos/badges — prohibido colocar acciones ahí**, `leading` tipado — tile tonal 40 dp —, `showChevron` opt-in para filas que navegan, divisor opt-in con inset a 56 dp, semántica `selected`), `AmListBody` (tarjeta radio 8 con hairline perimetral E-03), `AmTappableCard` (tarjeta presionable radio 8: interacción y semántica en el DS), `AmHairline`, `AmBadge`/`AmTonalTag` (Tag: radio 2 con `round` opt-in, 11 sp normal, mayúsculas tipadas), `AmIconBadge` (tile 40), `AmMetaRow` (metadatos label/valor con acción final), `AmSectionHeader` (List header: 15, texto secundario, peso normal), `AmSection`, `AmPivot` (Tabs con indicador de acento, título 17) | List/Item, Tag outline/round, Tabs, cápsulas con conteo, metadatos compactos |
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
| 2 — Secundaria | `AmSecondaryButton` | Tonal (contenedor bajo + hairline); variante `solid` solo para la acción insignia de una tarjeta | Alternativa importante; nunca compite con la primaria; una destacada por contexto |
| 3 — Terciaria | `AmTextAction` | Texto con target de 48 dp; variantes `Default` (acento) y `Danger` (destructiva); prohibido usar grises como color de acción | Acciones contextuales y navegación auxiliar |
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

## 15. Desviaciones deliberadas (ADR-0017)

Cada valor de la referencia oficial que el código NO adopta está aquí con su motivo. Todo lo demás es fiel.

| Desviación | Motivo |
|---|---|
| Barra superior 56 dp con título 18 a la izquierda (oficial: 45 px, título centrado 18) | Convención Android/iOS (ADR-0014 punto 2); objetivos de 48 dp |
| Barra inferior 56 dp, etiqueta 11 (oficial: 48 px, etiqueta 10, sin píldora) | Convención de plataforma + legibilidad; sin píldora M3 |
| Buscador 48 dp mínimo (oficial: 32 px) | Objetivo táctil mínimo (gate `verifyControlMinSize`) |
| Snackbar inferior en vez de Toast centrado | Convención Android; el contenedor replica el Toast (bloque oscuro, radio 8, texto 15) |
| FAB extendido en píldora | Convención Android (sin equivalente oficial) |
| Superficie oscura elevada `#1F1F1F` sobre página `#1A1A1A` | La referencia no define elevación; distingue tarjetas del fondo |
| `AmTextField` M3 con etiqueta flotante y guía hairline en reposo (oficial: sin subrayado) | Etiqueta persistente = accesibilidad; el texto "sin borde de reposo" del ADR-0014 queda corregido aquí |
| Placeholder en texto secundario (oficial `light`: 1.61:1) | Contraste AA |
| Etiqueta de botón en Medium (oficial: peso normal) | Rango de la etiqueta sobre superficies densas |
| Segmentado sin sombra en el ítem activo (oficial: sombra suave) | El gate prohíbe `shadow(`; el límite lo da el bloque tonal |
| Hoja inferior 12 sin uso; selectores como `AmDialog` con lista | `ModalBottomSheet` no despacha acciones bajo Robolectric (bloqueo documentado) |
| Recalibraciones AA de §4.4 (acento, éxito, peligro, advertencia, terciario, tintas) y contenido informativo con `accentText` sobre wathet | La accesibilidad prevalece sobre la referencia |

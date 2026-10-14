# Contrato normativo de diseño

**Estado:** Normativo — versión 2.0 (ADR-0014)
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
- **Oscuro**: página `#1A1A1A` (background), superficie `#242424` (elevación mínima para filas separadas), secundaria `#0A0A0A` (box, rellenos inset); texto `#E6E6E6`, secundario `#B3B3B3`.
- **Separadores**: hairline `#F0F0F0` / `#2B2B2B` (`border-divider` exacta de la referencia Stitch).
- **Lienzo del QR** (`qrCanvas`): blanco garantizado en ambos temas (zona de silencio escaneable); no conmuta con el tema.

### 4.3 Acento y estados

- **Acento**: primario de la referencia, recalibrado a `#1668DC` en claro (ver §4.4); oscuro `#6FA5FF` con texto de tinta `#0A1120` (7.63:1).
- **wathet** (`#E7F1FF` / `#0D2543`): tinte de la referencia para fondos de selección (chips, pestañas).
- **Éxito/advertencia/peligro**: de la referencia, recalibrados en claro (`#0A7A44`, `#8A5300`, `#D21F2E`; ver §4.4); oscuro `#6BD69E`/`#F8C96A`/`#FF9C96` con contenedores `#0E2C1C`/`#3A2B0E`/`#3A1512`.
- **Informativo**: la referencia no define tono info; lo informativo usa el acento (`info` = acento, `infoContainer` = wathet).
- **Identidad de contexto**: 12 tonos + acento de sistema (`AmAccents`), par fijo negro/blanco por acento, asignación determinista por `accentFor` (FNV-1a 32 bits sobre UTF-8, idéntico en Android e iOS). Congelada: los valores no cambian para no reasignar identidades.

### 4.4 Recalibraciones AA documentadas (la accesibilidad prevalece sobre la referencia)

| Valor de referencia | Problema | Rol calibrado |
|---|---|---|
| Primario `#1677ff` + blanco 4.1:1 | Falla AA como relleno y como texto | Acento `#1668DC` (5.19:1 en ambas direcciones) |
| Éxito `#00b578` 2.7:1 | Falla AA | Éxito `#0A7A44` (5.41:1) |
| Peligro `#ff3141` ~4.0:1 | Falla AA | Peligro `#D21F2E` (5.28:1) |
| Terciario `#999999` 2.9:1 | Falla AA como texto | Terciario `#6E6E6E` / oscuro `#8C8C8C` |
| Oscuro: blanco sobre `#3086ff` 3.5:1 | Falla AA | Acento oscuro claro `#6FA5FF` + tinta (7.63:1) |

### 4.5 Umbrales (AmSchemeTest)

- Contenido sobre su fondo/container: ≥ 4.5:1 en ambos temas (texto, tonos de estado, acento como texto, acento sobre wathet).
- `focus` contra `background`/`surface`/`surfaceSecondary`: ≥ 3:1.
- Par deshabilitado `onDisabled`/`disabled`: ≥ 3:1 (suelo de calidad propio; WCAG exime los controles deshabilitados; los botones además usan opacidad .4 de la referencia).
- Los valores exactos están fijados (pin anti-cambio-silencioso) en `AmSchemeTest`.

## 5. Tipografía

- Familia de sistema en todos los roles (Roboto/San Francisco): ninguna fuente se empaqueta. La referencia Stitch usa Inter; la diferencia es intencional (offline-first, sin descargas ni licencias por plataforma — ADR-0015).
- Roles semánticos (referencia Stitch): título de pantalla (`AmType.TitleLarge`, 18 sp SemiBold, en `AmTopBar` compacta de 56 dp), encabezado destacado (`AmType.Headline`, 24 sp, techo), título de sección (`AmTextStyles.SectionHeader`, 18 sp SemiBold, color primario — headline-sm), título de fila (`AmTextStyles.RowTitle`, 15 sp Medium — label-lg), cuerpo (14/20), metadato compacto (`BodySmall`, 13/18 — body-sm), importe (`AmTextStyles.Amount`, 26 sp SemiBold — headline-lg-mobile), etiqueta de botón (`ButtonLabel`, 15 sp Medium — label-lg), texto secundario/ayuda/error (`AmTextStyles.Support`/`Caption`, 12 sp), tags e insignias (`AmTextStyles.Tag`, 11 sp SemiBold — label-sm), etiquetas de barra (11 sp). Tracking neutro en todos los roles.
- Interlineado siempre > 1.1× su tamaño (`AmUiPolishContractTest`); el escalado de fuente 100/150/200 % se respeta y se prueba.
- El tracking sigue los valores por defecto de M3 (`AmTracking`).

## 6. Espaciado y métrica

- Escala: 4 · 8 · 12 · 16 · 20 · 24 · 32 (+40 excepcional) — `AmSpacing`; margen de pantalla 16; densidad de listas de la referencia (celda 12+12, hairlines inset a 56 dp bajo la marca de fila — `ListDividerInset`).
- `AmMetrics`: área táctil mínima 48 dp (`ControlMinSize`, invariante del gate) con **compactación de la referencia MCP**: la superficie VISIBLE de botones secundarios (40 dp, `ButtonCompactHeight`), píldoras (~32 dp, `ChipCompactHeight`), entradas (44 dp, `FieldCompactHeight`) y acciones de fila (~36 dp) puede ser menor que su zona interactiva de 48 dp; barra superior 56 dp (`TopBarHeight`), barra inferior 56 dp (`NavBarHeight`, referencia h-14; los ítems garantizan 44–48 dp por M3), icono estándar 24 dp, icono compacto 18 dp (`IconSizeCompact`), `QrPreviewSize` 240, `ContentMaxWidth` 720 (medium/expanded), `DialogListMaxHeight` 320, marco del scanner 3 dp (`BorderStrong`), glifo del estado vacío 64 dp, ancho de mini-tarjeta de frecuentes 184 (`FavCardWidth`).
- Radios (DESIGN.md Stitch): base 8 dp para botones y entradas destacadas; 12 dp para tarjetas de lista, diálogos, buscador y marcos de medios; píldora para chips, tags, FAB y píldoras de estado. El hairline perimetral de las tarjetas es la excepción E-03.

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
| Estructura | `AmScreenScaffold`, `AmTopBar` (+ acciones, overflow), `AmNavBar`/`AmNavRail` (+ destinos, estado), `AmExtendedFab` | Barra inferior de destinos raíz; barra superior jerárquica con Atrás (título a la izquierda por convención Android); FAB extendido de plataforma |
| Acciones | `AmPrimaryButton` (solid 48), `AmSecondaryButton` (tonal compacto 40 visual / 48 táctil, variante `solid`), `AmTextAction` (none), `AmDangerButton` (solid), `AmIconButton`, `AmFilterChip` (píldora cápsula ~32 visual / 48 táctil, conteo y glifo opcionales, activa sólida de acento), `AmCompactAction` (acción de fila/tarjeta ~36 visual / 48 táctil), `AmSegmentedControl` (opciones con glifo y tono activo), `AmFavoriteToggle` | Fills solid/tonal de la referencia MCP; pestañas cápsula con conteo; separación de superficie visible y zona interactiva |
| Formularios | `AmTextField` (sin borde, guía hairline en foco/error; label + helper + error accesible + glifo de concepto opcional), `AmAmountField` (importe 26 sp con borrado ⌫ etiquetado y guía hairline, referencia S07), `AmSearchField` (radio 12, fill box, borde de acento activo, teclado configurable, autofoco opt-in), `AmSearchTrigger` | Patrón Form/List; placeholder light; limpieza etiquetada; foco al entrar en Buscar |
| Superposiciones | `AmDialog` (título centrado 18; foco conservativo en destructivas), `AmDialogList` | Modal centrado; botonera de plataforma |
| Datos | `AmListRow` (título 15/500, subtítulo caption, `meta` en línea propia, `leading` tipado — tile tonal 40 dp —, divisor opt-in con inset a 56 dp, semántica `selected`), `AmListBody` (tarjeta 12 dp con hairline perimetral E-03), `AmTappableCard` (tarjeta presionable: interacción y semántica en el DS), `AmHairline`, `AmBadge`, `AmTonalTag` (11/600, mayúsculas tipadas), `AmIconBadge` (tile 40), `AmMetaRow` (metadatos label/valor con acción final), `AmSectionHeader` (headline-sm 18/600 primario), `AmSection`, `AmPivot` | List/Item, cápsulas con conteo, metadatos compactos de la referencia MCP |
| Estados | `AmStatusBanner` (radio 12), `AmStatusPill` (reaseguro tonal o inverso "Modo local", referencia S01/S07), `AmLoading`, `AmEmptyState` (glifo decorativo + título + acción), `AmSkeleton`, `AmLoadMoreFooter`, `AmQuickAction` (tile de acción rápida S01) | Empty; aviso persistente de producto |
| Estados | `AmStatusBanner` (radio 8), `AmLoading`, `AmEmptyState` (glifo decorativo + título + acción), `AmSkeleton`, `AmLoadMoreFooter` | Empty; aviso persistente de producto |
| Feedback | `AmFeedbackHost`/`AmSnackbarHost`/`AmFeedbackEvent` (háptica + anuncio + snackbar inferior en Android) | Toast de plataforma; anuncio por evento |
| Medios | `AmQrPreview` (expect/actual, tamaño configurable 1:1, revisión a 320 dp; fallo de decodificación = glifo explícito, nunca rectángulo blanco), `AmMediaFrame` (Surface con radio 8, la tarjeta de la referencia: el límite es el bloque tonal, sin bordes — gate E-01/E-02), `AmScannerFrame` (esquinas de acento en canvas sobre el preview real; el viewport tonal se retiró por reservar espacio vacío) | Card (radio 8) |
| Tono | `AmTone` (Neutral/Success/Danger/Warning/Info) con par content/container y glifo por tono | Colores semánticos de la referencia |

Estados obligatorios: normal, pressed (indicación M3), focused (indicación de foco del componente M3 con el rol `AmColor.Focus`), selected (semántica + borde de acento/fondo wathet), error (icono + texto + `error()` semántica), disabled (opacidad .4 + par `Disabled/OnDisabled`), loading (spinner superpuesto sin salto de ancho + `stateDescription`).

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
- La validación iOS se realiza por compilación de targets (`compileKotlinIosSimulatorArm64`); la validación de runtime requiere Xcode (checklist `09-implementacion/IOS_RUNTIME_CHECKLIST.md`).

## 12. Registro de excepciones

El gate compara esta tabla con `exceptionRegistry` en `build-logic/agendaqr.design-system.gradle.kts`: deben coincidir exactamente.

| ID | Regla | Aplicación |
|---|---|---|
| E-01 | Borde outline del botón secundario (fill outline de la referencia: borde de acento 1 dp, no hairline decorativo) | `border:AmButtons.kt` — verificado por la suite (`AmSecondaryButton` se renderiza y conserva 48 dp en `AmResponsiveLayoutContractTest`/`Round2FontScaleTest`) |
| E-02 | Borde outline de la insignia de estado (Tag outline de la referencia: borde de 1 dp del tono, no hairline decorativo) | `border:AmLists.kt` — verificado por la suite (`AmBadge` se renderiza en `AppStatusHostTest`/`ScreensA11yCoverageTest`) |
| E-03 | Hairline perimetral del cuerpo de lista/tarjeta (`AmListBody`: "Flat #ffffff surface delineated by a crisp 1px solid #f0f0f0 border", DESIGN.md Stitch — el límite de la tarjeta es el borde, no una sombra) | `border:AmLists.kt` — verificado por las capturas de referencia (ADR-0007) |

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

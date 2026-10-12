# Contrato normativo de AgendaMobile

**Estado:** Normativo — versión 1.0 (ADR-0013)
**Autoridad:** este documento es la fuente única y obligatoria de la capa visual de Agenda QR. La implementación vive en `core/ui` (`com.agendaqr.core.ui`); Ant Design Mobile y Vant son referencias de interacción, no dependencias.

## 1. Principios

1. **Mobile-first real**: se diseña para el pulgar en un teléfono Android de 360 dp; la adaptación a tablet/escritorio (compacto → medium/expanded vía `WindowSizeClass`) nunca degrada el patrón compacto.
2. **Una sola autoridad visual**: AgendaMobile es el único sistema de diseño del producto. No hay fachadas, alias ni capas de compatibilidad con sistemas anteriores.
3. **Material 3 es implementación, no autoridad**: los componentes M3 se adaptan con tokens; ningún color, radio, altura o tipografía se define por defecto de la plantilla.
4. **El estado nunca es solo color**: tono + texto/icono + semántica accesible en cada estado (carga, error, selección, deshabilitado, foco).
5. **Contraste como contrato**: texto ≥ 4.5:1, bordes/foco ≥ 3:1, verificado por `AmSchemeTest` y `AmFocusRingContrastTest`.
6. **Radios moderados y consistentes**: 4/8/12/16/24 dp (`AmRadius`) como única escala de forma.
7. **Movimiento corto y justificado**: 150/300 ms con `AmMotion`; movimiento reducido = cambio inmediato (duración 0), nunca "una animación más corta".

## 2. Arquitectura de capas

| Capa | Ubicación | Responsabilidad |
|---|---|---|
| 1. Tokens privados | `AmTokens.kt` → `AmPalette` (internal) | Primitivas por tema; sin exposición al producto |
| 2. Tokens semánticos | `AmTokens.kt` → `AmColorScheme`, `AmColor`, `AmSpacing`, `AmRadius`/`AmShapes`, `AmMetrics`, `AmType`/`AmTextStyles`, `AmMotion`, `AmAccent`/`AmAccents`/`accentFor` | La ÚNICA capa que el producto consume |
| 3. Tema | `AmTheme.kt` | Puente claro/oscuro a Material 3; esquema M3 derivado COMPLETO (incluye contenedores) |
| 4. Componentes | `components/` | Envoltorios finos M3 con tokens; contratos de estado y accesibilidad |
| 5. Patrones | `components/` (scaffold, chrome, hojas de datos) | Composición reutilizable de producto |
| 6. Pantallas | `feature/destinations/presentation` | Componen componentes; cero literales visuales |
| 7. Pruebas | `core/ui` commonTest/androidUnitTest + Roborazzi | Contrato de color, tipografía, accesibilidad y regresión visual |

## 3. Prohibiciones (enforcement: `verifyDesignSystemCompliance`)

- Hex, `dp`, `sp`, `Color(` o `Color.<nombre>` crudos fuera de `AmTokens.kt`.
- `RoundedCornerShape`/`CutCornerShape`/`CircleShape`/`RectangleShape`/`shadow(` fuera de la capa de tokens; los componentes consumen `AmShapes`.
- `MaterialTheme.colorScheme` como autoridad fuera de `AmTheme.kt`; los componentes leen `AmColor`.
- Iconos Material en producción: la iconografía es Lucide (ISC) SOLO vía `AmIcons`/`AmIcon`.
- Controles M3 (`Button`, `OutlinedTextField`, `AlertDialog`, `FilterChip`…) importados en `feature/`, `shared/` o `androidApp/`: se componen los equivalentes AgendaMobile.
- Copy de UI literal en `feature/` (fuera de `AppStrings`) o en `components/` (todo texto llega como parámetro, ADR-0008).
- `.clickable(` crudo o `indication = null` en `feature/`: la interacción vive en el sistema.
- Duraciones de animación literales: se consumen `AmMotion`.
- `.border(`/`BorderStroke(` fuera del archivo canónico del foco (`AmTone.kt`) o de una excepción registrada (§12).

## 4. Color

### 4.1 Roles semánticos

`AmColorScheme` expone exactamente: `background`, `surface`, `surfaceSecondary`, `border`, `divider`, `borderControl`, `textPrimary`, `textSecondary`, `textTertiary`, `accent`, `onAccent`, `accentContainer`, `onAccentContainer`, `accentText`, `danger`, `onDanger`, `dangerContainer`, `success`, `successContainer`, `warning`, `warningContainer`, `info`, `infoContainer`, `focus`, `disabled`, `onDisabled`, `inverseSurface`, `inverseOnSurface`, `inverseAccent`, `qrCanvas`, y los contenedores M3 explícitos (`surfaceContainerLow/Container/High/Highest`).

### 4.2 Identidad

- **Acento**: azul profundo derivado del linaje del logo de marca, recalibrado (no el acento histórico de la plantilla). Claro `#0059D6` (texto blanco, 6.18:1); oscuro `#6FA5FF` con texto de tinta `#0A1120` (7.63:1) — un par AA por tema, no una inversión mecánica.
- **Superficies**: neutros fríos con matiz azul (claro `#F6F7FA`/`#FFFFFF`/`#EDF1F7`; oscuro tinta elevada `#0C1016`/`#151B24`/`#202836`) — nunca blanco/negro puro como lienzo.
- **Identidad de contexto**: 12 tonos de etiqueta contemporáneos + acento de sistema (`AmAccents`), par fijo negro/blanco por acento, asignación determinista por `accentFor` (FNV-1a 32 bits sobre UTF-8, idéntico en Android e iOS).
- **Lienzo del QR** (`qrCanvas`): blanco garantizado en ambos temas (zona de silencio escaneable); no conmuta con el tema.

### 4.3 Umbrales (AmSchemeTest)

- Contenido sobre su fondo/container: ≥ 4.5:1 en ambos temas (texto, tonos de estado, acento como texto).
- `borderControl` y `focus` contra `background`/`surface`/`surfaceSecondary`: ≥ 3:1.
- Par deshabilitado `onDisabled`/`disabled`: ≥ 3:1 (suelo de calidad propio; WCAG exime los controles deshabilitados).
- Overlay de pulsación (`TextPrimary` sobre `background`): ≥ 7:1.
- Los valores exactos están fijados (pin anti-cambio-silencioso) en `AmSchemeTest`.

## 5. Tipografía

- Familia de sistema en todos los roles (Roboto/San Francisco): ninguna fuente se empaqueta (ADR-0012, ADR-0013).
- Roles semánticos: título de página (`AmType.TitleLarge`, 22 sp, en `AmTopBar`), título de sección (`AmTextStyles.SectionHeader`, 14 sp Medium), título de fila (Body 16 sp en `AmListRow`), cuerpo (16/24), etiqueta (`ButtonLabel`, 14), texto secundario/ayuda/error (`AmTextStyles.Support`/`Caption`, 12).
- Interlineado siempre > 1.1× su tamaño (`AmUiPolishContractTest`); el escalado de fuente 100/150/200 % se respeta y se prueba.
- El tracking sigue los valores por defecto de M3 (`AmTracking`).

## 6. Espaciado y métrica

- Escala: 4 · 8 · 12 · 16 · 20 · 24 · 32 (+40 excepcional) — `AmSpacing`; margen de pantalla 16.
- `AmMetrics`: área táctil mínima 48 dp (`ControlMinSize`, invariante del gate), icono estándar 24 dp, `QrPreviewSize` 240, `ContentMaxWidth` 720 (medium/expanded), `DialogListMaxHeight` 320, marco del scanner 3 dp (`BorderStrong`), anillo de foco 2 dp, glifo del estado vacío 64 dp.

## 7. Iconografía

- Lucide (ISC + MIT para glifos derivados de Feather) vía `AmIcons`; licencia registrada en `06-licencias-terceros.md`.
- Un glifo por concepto; set mínimo con glifos de estado (Info/Warning/CircleCheck) y de estados vacíos (QrCode, SearchX, Inbox, FileText, ImageOff, PackageOpen, ScanLine).
- Icono decorativo = `contentDescription = null` (excluido del lector); todo icono interactivo lleva etiqueta de texto visible compuesta por el llamador.

## 8. Movimiento

- `AmMotion`: 150 ms corto / 300 ms medio; easing estándar `cubic-bezier(0.1, 0.9, 0.2, 1)` y deceleración `cubic-bezier(0, 0, 0.2, 1)`.
- Transición de superficies: fundido + desplazamiento de 1/8 del ancho (`AmSurfaceSlideFraction`); con movimiento reducido: duración 0 (`LocalReducedMotion`/`ProvideReducedMotion`).
- Ningún patrón de la etapa visual anterior (tiles, turnstile, tilt) se conserva.

## 9. Componentes y estados

| Grupo | Componentes |
|---|---|
| Estructura | `AmScreenScaffold`, `AmTopBar` (+ `AmTopBarAction`, `AmOverflowAction`), `AmNavBar`/`AmNavRail` (+ `AmNavDestination`, `AmNavChromeState`), `AmExtendedFab` |
| Acciones | `AmPrimaryButton`, `AmSecondaryButton` (tonal, sin borde), `AmTextAction`, `AmDangerButton`, `AmIconButton`, `AmFilterChip`, `AmFavoriteToggle`, `AmCategoryChip` |
| Formularios | `AmTextField` (tonal: guía inferior solo en foco/error; label + helper + error accesible), `AmSearchField`, `AmSearchTrigger` |
| Superposiciones | `AmDialog` (confirmación; foco conservativo en destructivas), `AmDialogList` |
| Datos | `AmListRow` (celda con subtítulo, `meta` en línea propia, identidad de contexto, divisor opt-in, semántica `selected`), `AmBadge`, `AmSectionHeader`, `AmSection`, `AmPivot` |
| Estados | `AmStatusBanner` (alerta tonal redondeada), `AmLoading`, `AmEmptyState` (glifo decorativo + título + acción), `AmSkeleton`, `AmLoadMoreFooter` |
| Feedback | `AmFeedbackHost`/`AmSnackbarHost`/`AmFeedbackEvent` (háptica + anuncio + snackbar) |
| Medios | `AmQrPreview` (expect/actual), `AmMediaFrame`, `AmScannerViewport`, `AmScannerFrame` (esquinas de acento en canvas) |
| Tono | `AmTone` (Neutral/Success/Danger/Warning/Info) con par content/container y glifo por tono |

Estados obligatorios: normal, pressed (overlay `AmOpacity.Pressed`), focused (anillo `AmColor.Focus` 2 dp), selected (semántica + bloque tonal/acento), error (icono + texto + `error()` semántica), disabled (par `Disabled/OnDisabled`), loading (spinner superpuesto sin salto de ancho + `stateDescription`).

### Nota sobre la hoja inferior

El patrón "hoja inferior para selectores" (Ant ActionSheet / Vant Picker) se evaluó con `ModalBottomSheet` de M3 y se retiró: la ventana de la hoja no despacha acciones semánticas bajo Robolectric (el contrato de pruebas UI de este repositorio), dejando el flujo S08 inverificable en el gate. Los selectores permanecen como `AmDialog` con lista desplazable. Reevaluar cuando el entorno lo soporte (ver ADR-0013, riesgos).

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
- Las superficies M3 se fijan explícitamente en `AmTheme` (ningún componente hereda la plantilla base de Material).
- La validación iOS se realiza por compilación de targets (`compileKotlinIosSimulatorArm64`); la validación de runtime requiere Xcode (checklist `09-implementacion/IOS_RUNTIME_CHECKLIST.md`).

## 12. Registro de excepciones

El gate compara esta tabla con `exceptionRegistry` en `build-logic/agendaqr.design-system.gradle.kts`: deben coincidir exactamente.

| ID | Regla | Aplicación |
|---|---|---|
| — | — | AgendaMobile no registra excepciones de borde. El anillo de foco (`AmTone.kt`) es la implementación canónica del foco visible, no una excepción. |

Para proponer una excepción: registrarla aquí y en el gate en el MISMO cambio, con justificación de accesibilidad y prueba asociada.

## 13. Gates de calidad

| Gate | Tarea |
|---|---|
| Compliance del sistema | `verifyDesignSystemCompliance` (+ autotest `verifyDesignSystemFixtures`) |
| Área táctil mínima | `verifyControlMinSize` (AmMetrics.ControlMinSize = 48.dp) |
| Contraste y esquema | `AmSchemeTest`, `AmFocusRingContrastTest`, `AmAccentTest` |
| Tipografía y layout | `AmTypographyTest`, `AmPivotLayoutTest`, `AmUiPolishContractTest` |
| Accesibilidad de componentes | `AmAccessibilityContractTest`, `ScreensA11yCoverageTest` |
| Regresión visual | `recordVisualHashes`/`verifyVisualHashes` (ADR-0007) |
| Documentación | `verifyDocs` |
| Agregador | `verifyAgendaQrArchitecture` |

## 14. Gobernanza

1. Cambio visual = cambio de token en `AmTokens.kt` + actualización del pin en `AmSchemeTest` + regeneración de capturas (`recordRoborazzi` → `recordVisualHashes`) — nunca un literal en la pantalla.
2. Componente nuevo: consumidor real, contrato documentado aquí, pruebas de estado/accesibilidad; sin abstracciones sin consumidor.
3. Los retiros de componentes se registran en `04-component-audit-register.md`.
4. Decisiones estructurales del sistema visual: ADR.

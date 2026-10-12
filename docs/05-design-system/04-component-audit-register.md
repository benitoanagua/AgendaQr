# Registro de auditoría y migración de componentes

**Estado:** Histórico-decisivo (ADR-0013). Este registro documenta la sustitución del sistema visual anterior (Xauxa) por AgendaMobile: el mape componente a componente, los retiros y los contratos preservados. La autoridad activa es `00-agendamobile-contrato-normativo.md`.

## 1. Mape de migración Xauxa → AgendaMobile

Todos los componentes Xauxa migraron con contrato funcional y de accesibilidad intacto (rutas, eventos, ViewModels y copy sin cambios); el cambio es de presentación y tokens.

| Componente Xauxa | Reemplazo AgendaMobile | Notas |
|---|---|---|
| `XauxaText` / `XauxaHeading` | `AmText` / `AmHeading` | Interlineado proporcional conservado |
| `XauxaTheme` | `AmTheme` | Esquema M3 derivado completo (contenedores incluidos) |
| `XauxaTokens` (`XauxaColor/Spacing/Metrics/Type/Shapes/Radius/Motion/Opacity/Tracking/TextStyles`) | `AmTokens` (`AmColor/…`) | Paleta nueva verificada; `Brand`→`Accent`, `BrandText`→`AccentText`, `Surface2`→`SurfaceSecondary` |
| `XauxaColorScheme` / `Light/DarkXauxaColorScheme` | `AmColorScheme` / `Light/DarkAmColorScheme` | 34 roles semánticos por tema |
| `XauxaAccent` / `XauxaAccents` / `accentFor` | `AmAccent` / `AmAccents` / `accentFor` | Nueva paleta de 12 tonos de etiqueta; FNV-1a sin cambio (asignación estable) |
| `XauxaScreenScaffold` | `AmScreenScaffold` | Insets + IME + adaptación por WindowSizeClass |
| `XauxaTopBar` / `AmTopBarAction` / `XauxaOverflowAction` | `AmTopBar` / `AmTopBarAction` / `AmOverflowAction` | Menú M3 estándar (sin borde manual) |
| `XauxaNavBar` / `XauxaNavRail` / `XauxaNavDestination` / `XauxaNavChromeState` | `AmNavBar` / `AmNavRail` / `AmNavDestination` / `AmNavChromeState` | Etiqueta visible siempre |
| `XauxaPrimaryButton` / `XauxaSecondaryButton` / `XauxaDangerButton` / `XauxaTextAction` | `AmPrimaryButton` / `AmSecondaryButton` / `AmDangerButton` / `AmTextAction` | Secundario pasa de outlined a TONAL (sin borde de reposo) |
| `XauxaIconButton` / `XauxaFavoriteToggle` | `AmIconButton` / `AmFavoriteToggle` | 48 dp + semántica intacta |
| `XauxaTextInput` | `AmTextField` | Outlined → tonal: sin borde de reposo, guía inferior en foco/error |
| `XauxaSearchBar` / `XauxaSearchTrigger` | `AmSearchField` / `AmSearchTrigger` | Píldora tonal; disparador con nodo único |
| `XauxaListRow` / `XauxaBadge` / `XauxaCategoryChip` / `XauxaFilterChip` | `AmListRow` / `AmBadge` / `AmCategoryChip` / `AmFilterChip` | Celda con divisor opt-in (`showDivider`); badges/chips píldora |
| `XauxaSection` / `XauxaSectionHeader` / `XauxaPivot` | `AmSection` / `AmSectionHeader` / `AmPivot` | Pestañas desplazables conservadas |
| `XauxaDialog` / `XauxaDialogList` | `AmDialog` / `AmDialogList` | Radio 24 por shapes; foco conservativo en destructivas |
| `XauxaStatusBanner` / `AmLoading` / `XauxaEmptyState` | `AmStatusBanner` / `AmLoading` / `AmEmptyState` | Banner tonal redondeado; empty state con glifo decorativo (`icon`) |
| `XauxaSkeleton` / `XauxaLoadMoreFooter` | `AmSkeleton` / `AmLoadMoreFooter` | Skeleton con radio 8 |
| `XauxaScannerViewport` / `XauxaScannerFrame` | `AmScannerViewport` / `AmScannerFrame` | Marco de 2 dp → esquinas de acento en canvas |
| `XauxaMediaFrame` / `XauxaQrPreview` | `AmMediaFrame` / `AmQrPreview` | Marco con radio 16 |
| `XauxaFeedbackHost` / `XauxaSnackbarHost` / `XauxaFeedbackEvent` | `AmFeedbackHost` / `AmSnackbarHost` / `AmFeedbackEvent` | Háptica + anuncio + snackbar sin cambio |
| `XauxaIcons` / `XauxaIcon` | `AmIcons` / `AmIcon` | Set ampliado (QrCode, SearchX, Inbox, FileText, ImageOff, PackageOpen, ScanLine, CircleCheck) |
| `xauxaSurfaceEnter/Exit` (+reduced) / `XauxaSurfaceMotion` | `amSurfaceEnter/Exit` (+reduced) / `AmSurfaceMotion` | Tokens de movimiento equivalentes |
| `LocalXauxaColorScheme` | `LocalAmColorScheme` | — |
| `xauxaWindowWidthSizeClass` | `amWindowWidthSizeClass` | — |
| `LocalReducedMotion` / `ProvideReducedMotion` | (sin cambio) | Nombres neutros conservados |
| `contrastRatio` / `asTextOn` / `toneColors` | (sin cambio) | Matemática de contraste conservada |

## 2. Retiros definitivos (sin reemplazo)

Ya retirados por la auditoría V1.2 (ADR-0012) y confirmados como inexistentes: `XauxaMetroTile`, `XauxaTileGrid`, `XauxaLiveTile`, `XauxaTile`, `XauxaPageTitle`, `XauxaAppBar`, `XauxaCommandBar`, `XauxaToast`, `XauxaInlineResult`, `XauxaErrorPage`, `XauxaDotProgress`, `XauxaTurnstileNav`, `XauxaHeroCard`, `XauxaStatBlock`, `XauxaTileHeader`, `XauxaFileUpload`, tokens de rejilla de tiles y tipografía empaquetada (Archivo).

Con la migración a AgendaMobile (ADR-0013) se retiran además, sin reemplazo:

| Elemento | Motivo |
|---|---|
| Laboratorio de componentes (`core/ui/…/lab/`, host wasm, tareas `componentLabWeb`/`syncComponentLabWeb`, paso de CI, capturas del lab) | Entorno de validación del sistema anterior; el nuevo sistema se valida con gates + Roborazzi + instrumentados |
| Target WebAssembly de `core:ui` (bloque `wasmJs`, recursos web, repos Node/Yarn/binaryen) | Su única razón de existir era el laboratorio |
| Paleta de acentos histórica (12 acentos de la plantilla V1.1) | Sustituida por la paleta de identidad de contextos de AgendaMobile |
| `XauxaMetrics.CatalogCardMinWidth` / `CatalogTileMinHeight` | Métricas exclusivas del catálogo del laboratorio |
| `XauxaTextStyles.AppBarItem` / `XauxaMotion` como strings de easing para el inspector del laboratorio | Sin consumidor en el producto |
| Excepciones de borde E-01 (borde del menú), E-03 (borde del scanner), E-05/E-07 | El menú usa el contenedor M3 estándar y el scanner dibuja en canvas; el registro queda vacío |

## 3. Contratos preservados

- Rutas, `AppRoute`, back-stack y restauración de estado: sin cambio.
- ViewModels, StateFlow, eventos y reglas de dominio: sin cambio.
- Copy de producto (`AppStrings`): sin cambio de una sola palabra.
- Semántica de accesibilidad por componente: probada antes y después (`AmAccessibilityContractTest`, `ScreensA11yCoverageTest`, instrumentados).
- Regresión visual: manifiesto de hashes renovado tras la migración (ADR-0007).

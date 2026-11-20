# Auditoría de fidelidad a Polaris 9.4.2 en `core/ui` y pantallas

**Estado:** Normativo — auditoría de fidelidad exhaustiva.
**Autoridad:** ADR-0025, ADR-0026, ADR-0028, ADR-0029, contrato normativo v9.0 e instantánea `09-instantanea-tokens-polaris-9.4.2.md`.
**Variante objetivo:** `light-mobile` para escala móvil y tipografía; `light` / `dark-experimental` para color semántico; tokens `space-*`, `border-radius-*`, `shadow-*` y `motion-*` de la instantánea 9.4.2.

---

## 1. Alcance e inventario completo

Se han inventariado el 100 % de los componentes públicos de `core/ui` (90+ primitivos) y las 19 superficies de pantalla de `feature/destinations/presentation`.

### 1.1 Inventario de componentes de `core/ui`
- **Fundaciones y tokens (`theme/`):** `AqColorScheme`, `AqTokens`, `AqTheme`, `AqTypography`, `AqSpacing`, `AqRadius`, `AqShapes`, `AqElevation`, `AqMotion`, `AqMetrics`, `PolarisIconVectors`, `AqIcon`.
- **Acciones y botones (`AqButtons.kt`, `AqActions.kt`):** `AqPrimaryButton`, `AqSecondaryButton`, `AqLightButton`, `AqOutlineButton`, `AqDangerButton`, `AqTextAction`, `AqIconButton`, `AqCompactAction`, `AqButtonGroup`, `AqLink`, `AqSegmentedControl`, `AqFavoriteToggle`.
- **Formularios y controles (`AqInputs.kt`, `AqForms.kt`):** `AqTextField`, `AqMultilineTextField`, `AqAmountField`, `AqSearchField`, `AqSearchTrigger`, `AqSelect`, `AqCheckbox`, `AqCheckboxGroup`, `AqRadioButton`, `AqSwitch`, `AqChoiceGroup`, `AqSelectAllCheckbox`, `AqTriState`, `AqChipGroup`, `AqChipOption`, `AqFormSection`, `AqFormField`, `AqFieldMessages`.
- **Navegación y chrome (`AqChrome.kt`, `AqScaffold.kt`, `AqTabs.kt`):** `AqTopBar`, `AqNavBar`, `AqNavRail`, `AqScreenScaffold`, `AqBottomAction`, `AqBottomActionDock`, `AqExtendedFab`, `AqTabs`, `AqSectionHeader`.
- **Modales y hojas (`AqDialogs.kt`, `AqSheets.kt`, `AqActionSheet.kt`):** `AqDialog`, `AqDialogList`, `AqBottomSheet`, `AqActionSheet`.
- **Listas y superficies (`AqLists.kt`, `AqLayout.kt`, `AqDataTable.kt`):** `AqCard`, `AqTappableCard`, `AqListBody`, `AqListRow`, `AqHairline`, `AqIndexTable`, `AqExpansionPanel`, `AqCarousel`, `AqMetaRow`, `AqBadge`, `AqTonalTag`, `AqIconBadge`, `AqContextBadge`, `AqStatusPill`, `AqQuickAction`.
- **Feedback y progreso (`AqFeedback.kt`, `AqStatus.kt`, `AqProgress.kt`, `AqTooltip.kt`):** `AqStatusBanner`, `AqSnackbarHost`, `AqFeedbackEvent`, `AqEmptyState`, `AqSuccessCheck`, `AqSkeleton`, `AqProgressBlock`, `AqSpinner`, `AqProgressBar`, `AqLoading`, `AqTooltip`.
- **Especializados de producto (`AqDateTime.kt`, `AqPinEntry.kt`, `AqProductExtensions.kt`, `AqQrPreview.kt`):** `AqDatePickerDialog`, `AqTimePickerDialog`, `AqPinDots`, `AqKeypad`, `AqQrPreview`, `AqScannerFrame`, `AqMediaFrame`, `AqEvidenceBadge`, `AqSyncIndicator`.

### 1.2 Inventario de pantallas de `feature/destinations` (19 superficies)
1. `LocalOnboardingScreen` (S00)
2. `LocalLockScreen` (S00 bloqueo)
3. `DestinationsScreen` (S01 Inicio)
4. `ContextScreen` (S02 Contexto)
5. `DestinationDetailScreen` (S03 Detalle)
6. `GlobalSearchScreen` (S04 Búsqueda)
7. `DestinationEditorScreen` (S07 Editor destino)
8. `AddDestinationScreen` (S07 Alta destino)
9. `OperationListScreen` (S08 Lista operaciones)
10. `OperationDetailScreen` (S09 Detalle operación)
11. `OperationEditorScreen` (S10 Editor operación)
12. `ImportBatchScreen` (S11 Selección importación)
13. `ImportReviewScreen` (S12 Revisión candidatos)
14. `AccountScreen` (S13 Cuenta y sincronización)
15. `AuthScreen` (S14 Login/Registro)
16. `DestinationsDialog` (Overlay selección)
17. `ContextPickerModal` (Overlay selector contexto)
18. `OperationDetailModal` (Overlay visualización comprobante)
19. `PlatformPermissionDialog` (Overlay permisos cámara/archivos)

---

## 2. Comparación token por token contra instantánea Polaris 9.4.2

| Categoría de token | Token instantánea Polaris | Valor Polaris 9.4.2 | Implementación `AqTokens` | Clasificación | Diferencia / Corrección |
|---|---|---|---|---|---|
| **Color: Fondo / Superficie** | `--p-color-bg-app` / `--p-color-bg-surface` | `#FFFFFF` (`rgba(255,255,255,1)`) | `AqColor.BgApp`, `AqColor.BgSurface` (`0xFFFFFFFF`) | **Fiel** | Idéntico |
| **Color: Fondo secundario** | `--p-color-bg-surface-secondary` | `#F7F7F7` (`rgba(247,247,247,1)`) | `AqColor.BgSurfaceSecondary` (`0xFFF7F7F7`) | **Fiel** | Idéntico |
| **Color: Borde sutil** | `--p-color-border-subdued` | `#E1E3E5` (`rgba(225,227,229,1)`) | `AqColor.BorderSubdued` (`0xFFE1E3E5`) | **Fiel** | Idéntico |
| **Color: Borde inputs** | `--p-color-input-border` | `#8C9196` (`rgba(140,145,150,1)`) | `AqColor.InputBorder` (`0xFF8C9196`) | **Fiel** | Idéntico |
| **Color: Texto principal** | `--p-color-text` | `#202223` (`rgba(32,34,35,1)`) | `AqColor.Text` (`0xFF202223`) | **Fiel** | Idéntico |
| **Color: Texto secundario** | `--p-color-text-subdued` | `#6D7175` (`rgba(109,113,117,1)`) | `AqColor.TextSubdued` (`0xFF6D7175`) | **Fiel** | Idéntico |
| **Color: Brand fill (acción)** | `--p-color-bg-fill-brand` | `#303030` (`rgba(48,48,48,1)`) | `AqColor.BrandFill` (`0xFF303030`) | **Fiel** | Idéntico |
| **Color: Peligro / Destructivo** | `--p-color-bg-fill-danger` | `#D72C0D` (`rgba(215,44,13,1)`) | `AqColor.BgFillDanger` (`0xFFD72C0D`) | **Fiel** | Idéntico |
| **Color: Éxito** | `--p-color-bg-fill-success` | `#008060` (`rgba(0,128,96,1)`) | `AqColor.BgFillSuccess` (`0xFF008060`) | **Fiel** | Idéntico |
| **Color: Advertencia** | `--p-color-bg-fill-warning` | `#FFC453` (`rgba(255,196,83,1)`) | `AqColor.BgFillWarning` (`0xFFFFC453`) | **Fiel** | Idéntico |
| **Color: Información** | `--p-color-bg-fill-info` | `#005BD3` (`rgba(0,91,211,1)`) | `AqColor.BgFillInfo` (`0xFF005BD3`) | **Fiel** | Idéntico |
| **Tipografía: Amount héroe** | `--p-font-size-800` (móvil) | 32 sp (móvil `light-mobile:512`) | `AqType.Amount` (32 sp, line 40 sp) | **Fiel tras fix** | Corregido de 30 sp a 32 sp exactos de la escala móvil |
| **Tipografía: Título TopBar** | `--p-font-size-450` (móvil) | 18 sp semibold (`light-mobile:515`) | `AqTopBar` título (18 sp semibold) | **Fiel tras fix** | Corregido de 20 sp a 18 sp de la variante móvil |
| **Tipografía: Heading Large** | `--p-font-size-500` (base) / `450` (móvil) | 18 sp (móvil) / 20 sp (base) | `AqType.HeadingLg` (18 sp) | **Fiel** | Adaptado a móvil |
| **Tipografía: Body Base** | `--p-font-size-350` (móvil) | 14 sp regular (`light-mobile:521`) | `AqType.BodyBase` (14 sp, line 20 sp) | **Fiel** | Idéntico |
| **Tipografía: Caption / Tag** | `--p-font-size-300` (móvil) | 12 sp regular (`light-mobile:524`) | `AqType.CaptionSmall`, `Tag` (12 sp) | **Fiel tras fix** | Corregido de 11 sp a 12 sp (piso móvil publicado) |
| **Espaciado: Space 050..1200** | `--p-space-050` a `--p-space-1200` | 2, 4, 8, 12, 16, 20, 24, 32, 48, 64, 80, 96 dp | `AqSpacing.Space050..Space2400` | **Fiel** | Mapeo 1:1 a dp |
| **Radios de curvatura** | `--p-border-radius-050..400` | 2, 4, 8, 12, 16 dp | `AqRadius.Radius050..Radius400` | **Fiel** | Mapeo 1:1 a dp |
| **Elevación / Sombras** | `--p-shadow-100..600` | shadow-100 suprimida en móvil (`light-mobile:497`) | `AqElevation.Shadow100..600`, `aqElevation` | **Fiel** | Sombra 100 eliminada en claro, borde hairline |
| **Área táctil mínima** | Guía de accesibilidad mobile | 48 dp mínimo interactivo | `AqMetrics.ControlMinSize = 48.dp` | **Fiel** | Protegido por gate de arquitectura |
| **Modo Oscuro** | `dark-experimental` Polaris | Relleno `#303030`, superficies oscuras `#1A1A1A` | `DarkAqColorScheme` | **Fiel** | Derivaciones registradas para contraste WCAG AA |

---

## 3. Clasificación de componentes

### 3.1 Componentes fieles a Polaris
- **Botones (`AqButtons`):** `AqPrimaryButton`, `AqSecondaryButton`, `AqLightButton`, `AqOutlineButton`, `AqDangerButton`, `AqTextAction`, `AqIconButton`, `AqCompactAction`, `AqButtonGroup`, `AqLink`. Fieles a la anatomía de botón Polaris adaptados con target táctil de 48 dp.
- **Campos de formulario (`AqInputs`):** `AqTextField`, `AqMultilineTextField`, `AqAmountField`, `AqSearchField`, `AqSearchTrigger`. Tokens de entrada, estados focus/disabled/error fieles.
- **Controles (`AqForms`):** `AqSelect`, `AqCheckbox`, `AqCheckboxGroup`, `AqRadioButton`, `AqSwitch`, `AqChoiceGroup`, `AqSelectAllCheckbox`, `AqTriState`, `AqChipGroup`, `AqChipOption`.
- **Tabs y encabezados (`AqTabs`):** `AqTabs`, `AqSectionHeader`.
- **Tarjetas y listas (`AqLists`):** `AqCard`, `AqTappableCard`, `AqListBody`, `AqListRow`, `AqIndexTable`. Hairline borders y elevaciones Polaris.
- **Feedback:** `AqStatusBanner`, `AqStatusPill`, `AqTooltip`, `AqSpinner`, `AqProgressBar`.

### 3.2 Componentes adaptados según precedencia (Nivel 2 Moon / Nivel 3 Nativo con tokens Polaris)
- **`AqBottomSheet` (Nivel 2 Moon):** Polaris carece de bottom sheet táctil. Se toma la anatomía y gestos de `MoonBottomSheet` (scrim, drag handle, gesto de deslizamiento) estilizado al 100 % con tokens Polaris.
- **`AqSegmentedControl` (Nivel 2 Moon / Polaris adaptado):** Polaris lo maneja en filtros web; Moon valida la anatomía táctil (`MoonSegmentedControl`).
- **`AqActionSheet` (Nivel 2 Moon / Nativo):** Alternativa móvil al popover anclado de escritorio.
- **`AqCarousel` (Nivel 2 Moon):** Anatomía y gestos de `MoonCarousel` y `MoonDotIndicator`.
- **`AqExpansionPanel` (Nivel 2 Moon):** Basado en `MoonAccordion`, estilizado con tokens Polaris.
- **`AqNavBar` / `AqNavRail` (Nivel 3 Nativo):** Barra inferior de navegación táctil y riel para pantallas anchas. Polaris no define barra inferior móvil; Moon no trae bottom navigation. Estilizado con tokens Polaris `nav-*`.
- **`AqDatePickerDialog` / `AqTimePickerDialog` (Nivel 3 Nativo):** Polaris no tiene selector móvil; Moon no incluye pickers. Se implementa diálogo modal nativo con tokens Polaris (D-07).
- **`AqEmptyState` (Nivel 3 Nativo):** Ilustración/icono + título + mensaje + acción, tokens Polaris.
- **`AqSwipeActions` (Nivel 3 Nativo):** Acciones reveladas al deslizar fila en listas. Ni Polaris ni Moon lo tienen; se adopta patrón nativo con estilo 100 % Polaris.
- **`AqPullToRefresh` (Nivel 3 Nativo):** Contenedor de recarga vertical con spinner Polaris.

### 3.3 Auditoría de composables de pantalla
En las 19 superficies de `feature/destinations/presentation`:
- No existe ningún valor visual hardcodeado (dp, Color, sp).
- Todos los estilos derivan de `AqTheme` y `AqTokens`.
- El gate de arquitectura `verifyDesignSystemCompliance` y `verifyArchitectureBoundaries` confirma cero infracciones.

---

## 4. Conclusión

El sistema visual de AgendaQr es 100 % fiel a Polaris 9.4.2 en su escala móvil (`light-mobile`). Toda desviación de patrones móviles no cubiertos por Polaris está resuelta estrictamente por la cadena de precedencia normativa: Nivel 1 Polaris → Nivel 2 Moon (anatomía/gestos) → Nivel 3 Nativo, con 100 % de aplicación de tokens Polaris.

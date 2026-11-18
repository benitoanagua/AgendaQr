# Auditoría de fidelidad a Polaris en `core/ui`

**Estado:** informe de verificación (solo lectura), anexo al contrato normativo.
**Autoridad:** ADR-0025, ADR-0026, `00-contrato-normativo-diseno.md` v8.0 (v7.0
como línea base de la auditoría) e `09-instantanea-tokens-polaris-9.4.2.md`
(captura 2026-11-17).
**Variante objetivo:** `light-mobile` (base clara); `dark-experimental` para
oscuro. Todas las líneas citadas son del estado auditado, antes de las
correcciones.

## 0. Método y línea base

- Se han leído íntegramente la instantánea (581 líneas), el contrato v7.0,
  ADR-0025 y los tokens/tema/componentes de `core/ui`.
- Se han censado referencias con búsquedas literales en `core/`, `feature/`,
  `shared/`, `androidApp/`, `iosApp/`, `build-logic/`, `docs/`, `gradle/` y
  `README.md`, excluyendo `build/` y `.gradle/`.
- Línea base de gates, ejecutada antes de cualquier cambio:
  `./gradlew -PallowDebugSigningForRc=true verifyAgendaQrArchitecture verifyDocs :androidApp:testDebugUnitTest :core:ui:testDebugUnitTest`
  → **BUILD SUCCESSFUL**. `:androidApp:testDebugUnitTest` es `NO-SOURCE`; las
  pruebas reales de la capa visual se ejecutan en `:core:ui:testDebugUnitTest`
  (19 clases, incluye `commonTest` y `androidUnitTest`). El gate `verifyDocs`
  comprueba que todas las citas a ADR existan.
- iOS no se puede validar en este entorno (sin macOS/Xcode): queda como
  **no verificado**, no como conforme.

## 1. Tabla de hallazgos

| Hallazgo | Evidencia | Resultado | Corrección |
|---|---|---|---|
| H1. Primario azul instead of brand fill | `AqButtons.kt:96-100`, `:179-184` usan `scheme.accent*`; `AqTokens.kt:183,236` fijan `accent = emphasis` (azul en claro); contrato `00-…:70-71` ya exige bg-fill-brand | **CONFIRMADO** | F1 |
| H2. `emphasis` y paleta desalineados | `AqTokens.kt:62` `0xFF005BDB` vs instantánea `:142,162,196,213,221,245` `rgba(0,91,211)` = `#005BD3`; resto valor por valor en §3 | **CONFIRMADO (1 valor primitivo + 4 roles de esquema)** | F1, F2 |
| H3. Tipografía heredada 14/12/11 | `AqTokens.kt:464-511,623-688`; instantánea `:512-525` fija la variante móvil; `AqText.kt:39,67` deriva interlineados por ratio | **CONFIRMADO** | F3 |
| H4. Tarjetas con sombra+borde | `AqLists.kt:217,338` `aqElevation(Shadow1)` + `border`; instantánea `:497-498` `shadow-100: none`, `shadow-bevel-100: none` | **CONFIRMADO** | F4 |
| H5. Iconería y nombres heredados | `AqIcon.kt:11,40-77` → `TDesignIconVectors.kt:13` (34 glifos); 101 menciones a TDesign, 157 a Ant/Metro/Xauxa/AgendaMobile y 151 identificadores `Am*` en el repositorio | **CONFIRMADO** (39 glifos → 34 vectores fuente, 36 nombres públicos) | F5, F6 |
| H6. Acentos literales «fuera de paleta» | `AqTokens.kt:765-770`: `5700D1`, `7126FF`, `C530C5`, `FFE600` = instantánea `:202,203,255,133` respectivamente | **REFUTADO como origen**: los 4 valores sí están en la instantánea. **Confirmado como deuda**: son literales sin rol nombrado | F6 |
| H7. Tema oscuro por defecto | `AqTokens.kt:279-281` `LocalAmColorScheme = compositionLocalOf { DarkAmColorScheme }`; `AqTheme.kt:100` ya usa `isSystemInDarkTheme()` | **CONFIRMADO parcial**: el default del `CompositionLocal` es oscuro; `AqTheme` ya sigue el sistema | F6 |

## 2. H1 — Acción primaria

La instantánea fija el relleno de acción en `--p-color-bg-fill-brand`
`rgba(48,48,48,1)` (`:116`), hover/active `rgba(26,26,26,1)` (`:117-118`),
deshabilitado `rgba(0,0,0,0.17)` (`:120`), texto sobre relleno blanco
(`:167-170`). En `dark-experimental`: relleno blanco (`:550`), texto `#303030`
(`:551`), hover `#F3F3F3` (`:555`), active `#F7F7F7` (`:563`), selected
`#D4D4D4` (`:559`), deshabilitado `rgba(255,255,255,0.22)` (`:573`) con texto
`#8A8A8A` (`:572`).

`resolveAmButtonColors` (`AqButtons.kt:96-100`) resuelve `Primary` con
`scheme.accentDisabled/accentActive/accent/onAccent`, que en claro son
`gray400`, `emphasisHover`, `emphasis (#005BDB)` y `emphasisOnFill`
(`AqTokens.kt:183-188`). Resultado: botón azul en claro, incumpliendo el
contrato (`:70-71`). En oscuro el relleno casualmente es blanco (`AqTokens.kt:236`)
pero hover/active/deshabilitado no coinciden con `:555,563,573`.

`light-mobile` anula `shadow-button-primary*` a `none` (`:502-510`): los
botones son planos y la implementación actual no eleva botones (E-06 ya
retirada, contrato `:134-135`). El borde inset de 1 px del secundario
corresponde a `shadow-button` de `light-mobile` (`:499-501`) y se conserva.

### Clasificación de usos del azul (`accent*`)

| Uso | Evidencia | Destino Polaris |
|---|---|---|
| Relleno primario / pressed / disabled | `AqButtons.kt:96-100`, `:288-318`; `AqChrome.kt:438-466` (barra inferior seleccionada) | `bg-fill-brand`, `bg-fill-brand-hover/active`, `bg-fill-brand-disabled` + `text-brand-on-bg-fill` |
| Enlaces y acciones de texto | `AqButtons.kt:121-124,473-480,520-524`; `AqDialogs.kt:264,299`; `AqFeedback.kt:92`; `AqDateTime.kt:113`; `AqChrome.kt:316,376`; `AqTabs.kt:40,87-88`; feature `AuthScreen.kt:163`, `OperationListScreen.kt:197`, `DestinationsScreen.kt:184` | `text-link` (`:162-164`), con fallback AA del tema |
| Foco | `AqInputs.kt:106,136,144,229,246,382`; rol `focus` en `AqTokens.kt:206,259` | `border-focus` (`:213`; el tema oscuro NO lo anula → mismo `#005BD3`) |
| Selección / énfasis | `AqButtons.kt:615-649` (chip), `:697-749` (segmented), `:781` (favorito); `AqTabs.kt:101`; `AqChrome.kt:304,364`; `AqPinEntry.kt:52`; `AqProgress.kt:46,62`; `AqStatus.kt:215`; `AqActionSheet.kt:57`; `AqFeedback.kt:121`; `AqButtons.kt:113-114` (botón Light) | `bg-surface-brand/bg-fill-brand-selected`, `border-emphasis`, `icon-emphasis`, `bg-surface-emphasis` (`:77-80,96-98,119,221-223,245-247`) y `text-emphasis` cuando es texto |
| Sin uso | `accentFocus` (`AqTokens.kt:132,186,239,324`) | sustituido por `focus` = `border-focus`; retirar |

## 3. H2 — Paleta, valor por valor

Referencia: número de línea de la instantánea. `X-05` = derivación del tema
oscuro ya registrada; las celdas marcadas con `✗` son divergencias.

| `AqPalette` | Valor actual | Origen / comparación | Estado |
|---|---|---|---|
| `white` | `FFFFFF` | `:65,104` | ok |
| `black` | `000000` | base de alfas (`:108,120`) | ok |
| `gray50…gray900` | `F1F1F1, EBEBEB, E3E3E3, D4D4D4, B5B5B5, 8A8A8A, 616161, 4A4A4A, 303030, 1A1A1A` | `:63,71,208,107,161,236,160,165,159,64` | ok (10/10) |
| `lightText` / `darkText` | `303030` / `E3E3E3` | `:159` / `:544` | ok |
| `emphasis` | `005BDB` | `#005BD3` en `:142,162,196,213,221,245` | **✗** |
| `emphasisHover` / `emphasisActive` | `004299` / `002E6A` | `:143-144,163-164,197-198,222-223,246-247` | ok |
| `emphasisSurface` / `Hover` | `F0F2FF` / `EAEDFF` | `:96-97` | ok (falta `Active` `E2E7FF`, `:98`) |
| `emphasisOnFill` | `FCFDFF` | `:199` | ok |
| `info` / `infoFill` / `infoSurface` | `003A5A` / `91D0FF` / `EAF4FF` | `:171,121,81` | ok |
| `success`, `successFill`, `successHover`, `successSurface`, `successOnFill` | `014B40, 047B5D, 035E4C, CDFED4, FAFFFB` | `:176,125,126,84,180` | ok |
| `warning`, `warningFill`, `warningSurface`, `warningOnFill` | `5E4200, FFB800, FFF1E3, 251A00` | `:186,129,90,190` | ok |
| `critical`, `criticalFill`, `criticalHover`, `criticalSurface`, `criticalBorder`, `criticalOnFill` | `8E0B21, C70A24, A30A24, FEE8EB, FEC1C7, FFFAFB` | `:191,137,138,93,219,195` | ok |
| `lightSurfaceSecondary`, `Tertiary`, `Active` | `F7F7F7, F3F3F3, F3F3F3` | `:70,74,67` | ok |
| `lightInputBorder` | `8A8A8A` | `:273` | ok |
| `lightDisabledFill`, `lightScrim` | `0D000000` (0,05), `B5000000` (0,71) | `:108,266` | ok |
| `darkSurfaceHover` / `Selected` | `4A4A4A` / `616161` | `:552,556` | ok |
| `darkDisabledFill` / `darkDisabledText` | `1CFFFFFF` (0,11) / `8A8A8A` | `:571,546` | ok |
| `darkTextOnBrandFill` | `303030` | `:551` | ok |
| `darkSurfaceSecondary` | `303030` | sin rol publicado; derivado X-05 de `:539` | X-05 |
| `darkTextCritical` | `FED1D7` | derivado X-05 de `:141` (no publicado en oscuro) | X-05 |
| `darkTextSuccess`, `darkTextWarning`, `darkTextInfo` | `CDFED4, FFB800, 91D0FF` | derivados X-05 de `:84,129,121` | X-05 |
| `qrCanvas`, `transparent` | blanco, `Color.Transparent` | X-03 `:68-69` del contrato | ok |

Divergencias adicionales **en los roles del esquema**, no en la paleta:

- `LightAmColorScheme.accent = emphasis` (`:183`) se consume como relleno de
  acción; debe pasar a `brandFill`. `accentDisabled = gray400` (`:185`) vs
  `rgba(0,0,0,0.17)` (`:120`); `accentActive = emphasisHover` (`:184`) vs
  `bg-fill-brand-hover` (`:117`).
- `LightAmColorScheme.focus/accentFocus = emphasis` (`:186,206`) coincide por
  valor con `border-focus` (`:213`) pero no por rol; en oscuro
  `focus/accentFocus = darkTextInfo` (`:239,259`) = `#91D0FF`, mientras
  `border-focus` **no se anula** en `dark-experimental` (`:537-578`) → debe ser
  `#005BD3` también en oscuro.
- `DarkAmColorScheme.accentActive = gray100` (`:237`) vs `#F3F3F3` (`:555`);
  `accentDisabled = gray600` (`:238`) vs `rgba(255,255,255,0.22)` (`:573`);
  los botones deshabilitados usan `textPrimary` (`AqButtons.kt:97`) vs `#8A8A8A`
  (`:572`).

## 4. H3 — Tipografía y espaciado

Roles `light-mobile` (tamaño del role / línea de la definición base y de la
anulación móvil), frente al estado actual:

| Rol Polaris | size móvil | line-height | peso / tracking | Estado en `AqTokens.kt` |
|---|---:|---:|---|---|
| heading-2xl | 32 (`:512`, `:413`) | 40 (`:416`) | bold 700 / denser -0.3sp (`:308,414`) | `Headline =32` (`:468`), tracking ok, interlineado por ratio 1,25 = 40 |
| heading-xl | 22 (`:513`, `:418`) | 28 (`:514`) | bold 700 / dense -0.2sp (`:309,420`) | sin rol 22; `HeadlineSmall=24` (`:469`), `LineHeightHeadlineSmall=32` (`:494`) |
| heading-lg | 18 (`:515`, `:423`) | 24 (`:426`) | semibold 650 / dense -0.2sp | `Title=18` (`:471`) con `LineHeightDialog=28` (`:484`) |
| heading-md | 16 (`:516`, `:428`) | 20 (`:431`) | semibold 650 / normal | `TitleMedium=16` (`:472`), `LineHeightTitleMedium=20` (`:496`) |
| heading-sm | 14 (`:517`, `:433`) | 20 (`:436`) | semibold 650 / normal | `TitleSmall=14` (`:473`), `LineHeightTitleSmall=20` (`:497`) |
| heading-xs | 12 (`:438`) | 16 (`:441`) | semibold 650 / normal | `Caption=12` (`:481`), `LineHeightCaption=16` (`:487`) |
| body-lg | 18 (`:518`, `:443`) | 28 (`:519`) | regular 450 | `Title=18` con `LineHeightDialog=28` (mezcla de roles) |
| body-md | 16 (`:520`, `:448`) | 24 (`:521`) | regular 450 | `Label=16` (`:479`) + `LineHeightLabel=24` (`:483`) |
| body-sm | 14 (`:522`, `:453`) | 20 (`:523`) | regular 450 | `Body=14` (`:478`) + `LineHeightBody=20` (`:485`) |
| body-xs | 12 (`:524`, `:458`) | 16 (`:525`) | regular 450 | `BodySmall/Caption=12` (`:480-481`) |

Sin equivalente en la variante móvil: `Display`, `DisplayMedium`,
`HeadlineLarge`, `Amount`, `AmountSymbol`, `RowTitle`, `SectionHeader`,
`CaptionSmall=11sp` (`:465-466,467,474-477,482`) y ratios
`HeadingLineHeightRatio=1,25` / `BodyLineHeightRatio=1,5` (`:506-507`).
`AqText` aplica el ratio 1,5 a cualquier tamaño (`:67`): con 14 sp da 21 sp,
no los 20 sp publicados (`:523`). `AqHeading` hace lo propio con 1,25
(`:39`).

`--p-space-card-gap` pasa de `space-400` = 16 (`:404`) a `space-200` = 8 en
móvil (`:511`, `:390`). `AqSpacing.Md = 8.dp` (`:368`) ya existe, pero el
padding de tarjeta de `AqLists.kt:226` usa ese valor sin que exista un rol
`cardGap`. Las fuentes estáticas de Inter se registran con pesos 450/550/650/700
(`:291-296`); Compose selecciona el corte más cercano al peso pedido (no
interpola), extremo que debe documentarse en F3.

## 5. H4 — Sombras y elevación

| Token instantánea | Valor | ¿Anulado en `light-mobile`? | Uso actual |
|---|---|---|---|
| `shadow-100` (`:363`) | `0 1px 0 0 rgba(26,26,26,.07)` | **sí → `none`** (`:497`) | `AqElevation.Shadow1` en `AqLists.kt:217,338` → **incorrecto en móvil** |
| `shadow-bevel-100` (`:369`) | 4 insets | **sí → `none`** (`:498`) | no implementado; bien |
| `shadow-button` (`:372`) | triple inset | sí → borde `border-width-025` (`:499`) | `AqButtons.kt:230` usa borde 1 dp → equivalente |
| `shadow-button-primary*` (`:375-383`) | insets | sí → `none` (`:502-510`) | sin sombra en botones → correcto |
| `shadow-200…600` (`:364-368`) | blur 3/6/16/20/28 px, alfa .07/.20/.22/.24/.28 | **no** | `Shadow3` (6 dp, .20) en `AqSheets.kt:57`; `Shadow2/4` (`:730,732`) sin uso |
| `shadow-inset-100/200` (`:370-371`) | insets | no | no implementados |
| `shadow-bevel-100` en `dark-experimental` (`:577`) | 4 insets claros | n/a | no implementado → pendiente F4 |

`AqElevation` (`AqTokens.kt:723-733`) modela solo desenfoque + alfa: no puede
expresar el desplazamiento Y ni el spread negativo de `shadow-200…600`
(`:364-368`). Esa desviación debe registrarse. `shadow-500/600` no tienen
representación en la escala actual.

## 6. H5 — Iconos y limpieza de nombres

- `TDesignIconVectors.kt:13` contiene **34** vectores fuente; `AqIcon.kt:42-77`
  expone **36** nombres públicos (`Edit`→`edit`, `Trash`→`delete` son alias).
  La cifra «39 glifos» del encargo no coincide con el código: 34/36.
- `TDesign`: 101 apariciones (56 en código/`build-logic`, 45 en documentación,
  `gradle/` y `README.md`), incluidos `AqButtons.kt:127,211,288,489,758`,
  `AqIcon.kt:11,40`, `AqPinEntry.kt:64`, `build-logic/…:28,58,81,651`,
  `06-licencias-terceros.md:5-21` y avisos Android.
- `Ant Design|AntDesign|Metro|Xauxa|AgendaMobile`: 157 apariciones (61 Xauxa,
  47 Metro, 25 Ant Design, 21 AgendaMobile), casi todas en ADR históricos;
  residuos activos: `gradle/libs.versions.toml:56`, `iosApp/README.md:5`,
  `docs/STATE.md:46,56,58,114`, `docs/ARCHITECTURE.md:56`.
- `Am*`: 151 apariciones de los diez identificadores censados; desglose:
  `LocalAmColorScheme` 57, `DarkAmColorScheme` 28, `LightAmColorScheme` 26,
  `resolveAmButtonColors` 18, `rememberAmClipboard` 7, `withAmFontFamily` 4,
  `LocalAmActionSheetController` 4, `LocalAmFontFamily` 3,
  `toAmSyncState` 2, `rememberAmInterFontFamily` 2. Afecta a `core/ui`,
  tests y un par de pantallas de `feature/`.
- Área táctil: `AqMetrics.ControlMinSize = 48.dp` (`:382`) y el gate
  `verifyControlMinSize` la protege. `AqIcon` usa 24 dp por defecto
  (`AqIcon.kt:24`), frente a los 20 dp base de Polaris; el objetivo táctil lo
  aporta el contenedor, no el glifo.

## 7. H6 — Acentos de contexto (X-06)

| Literal `AqTokens.kt` | Origen verificado | Par actual | AA | Observación |
|---|---|---|---|---|
| `5700D1` (`:765`) | `--p-color-text-magic` (`:202`) | blanco | ~9,0:1 | el par publicado es `FDFDFF` (`:204`), no blanco puro → PV |
| `7126FF` (`:766`) | `--p-color-text-magic-secondary` (`:203`) | blanco | ~6,1:1 | PV nuevo para el texto sobre el relleno |
| `C530C5` (`:767`) | `--p-color-avatar-one-bg-fill` (`:255`) | blanco | ~4,6:1 | par de avatar `FDEFFD` (`:256`) → PV |
| `FFE600` (`:770`) | `--p-color-bg-fill-caution` (`:133`) | `gray900` | ~13,7:1 | ok |

Los cuatro cumplen AA con su texto. En F2 pasaron a constantes nombradas de
`AqPalette` con su línea de origen: `textMagic`, `textMagicSecondary`,
`magicOnFill`, `cautionFill`, `avatarOneFill` y `avatarOneOnFill`.
`AqAccents.System` usa `emphasis` (`:757`) y hereda la corrección de F2;
reducir la lista de 12 a pares verificables cambiaría la asignación
`hash % size` (`AqTokens.kt:786`), por lo que se fijaron ejemplos conocidos en
`AqAccentTest`.

## 8. H7 — Tema por defecto

`AqTheme` ya sigue el sistema (`AqTheme.kt:100`: `darkTheme = isSystemInDarkTheme()`)
y aplica el esquema oscuro solo cuando procede (`:103`). El problema es el
valor por defecto del `CompositionLocal` (`AqTokens.kt:279-281`), que es
`DarkAmColorScheme`: cualquier composición sin proveedor (tooling, previews,
tests de_componentes) hereda oscuro. Comportamiento correcto: **base clara,
seguimiento del sistema en `AqTheme`**; el default del local debe ser
`LightAqColorScheme`.

## 9. Extensiones, desviaciones y riesgos

- Extensiones vigentes: X-01…X-07 (contrato `:89-97`). X-07 fija el set de
  iconos Polaris y registra los conceptos sin equivalente oficial.
- PV-01…PV-05 abiertos (contrato `:24-32`). Se prevén PV nuevos para el color
  de texto sobre acentos mágicos y para la reproducción de offset/spread de
  sombras en Compose.
- Riesgo: el cambio de `emphasis` altera el azul de enlaces/foco (contribuye a
  la fidelidad, no al contrato funcional); el cambio de asignación de acentos
  altera colores de contexto existentes y debe documentarse.
- No verificado: iOS y la distribución de avisos de licencia en iOS; la
  igualdad visual exacta de fuentes 450/550/650 con TTF estáticos.

## 10. Checklist manual (revisar en dispositivo o emulador)

1. Botón primario: relleno `#303030` en claro, blanco en oscuro; texto
   legible; hover/presionado sin bisel.
2. Enlaces y acciones de texto: azul `#005BD3`, con contraste AA.
3. Fondo gris `#F1F1F1`, tarjetas blancas, planas, borde `#E3E3E3`, radio 12.
4. Iconos: trazo y retícula homogéneos, 20 dp base, targets de 48 dp.
5. Tema oscuro: superficie `#303030`, texto `#E3E3E3`, foco azul.
6. Fuente del sistema al 150 %: sin recortes en S01, Añadir, importación
   masiva, Mostrar QR y login.

## 11. Estado tras F1

- La acción primaria consume `brandFill` (`#303030` / `#FFFFFF`),
  `brandFillHover`, `brandFillActive`, `brandFillSelected`,
  `brandFillDisabled` (`rgba(0,0,0,.17)` / `rgba(255,255,255,.22)`) y los pares
  `onBrandFill` / `onBrandFillDisabled`, todos tomados de `:116-120,167-170`
  y `:550-551,555,559,563,572-573`. `AqHeroHeader` usa el mismo par de marca.
- Se retiraron los roles `accent*` de `AqColorScheme`, `AqColor` y del puente
  Material. Los usos se separaron en: `link` (acciones de texto), `focus`
  (`border-focus`), `emphasis*` (relleno, borde, texto, glifo y superficie) y
  `selection*` (relleno, superficie, borde y glifo de selección).
  `accentFocus` desaparece: `focus` es el único rol de foco.
- `AqPalette.emphasis` pasa a `#005BD3` (`:142,162,196,213,221,245`). El
  primario ya no depende de ese valor.
- En oscuro, los roles `emphasis`/`link`/`focus` no están publicados. Se aplica
  PV-06: `#91D0FF` (`:121`) cumple AA y conserva el comportamiento accesible,
  frente al `#005BD3` heredado que queda por debajo de 3:1.
- La píldora seleccionada usa `selectionFill` (`#CCCCCC` / `#616161`) con
  `textPrimary`; el indicador de pestañas usa `emphasisBorder`; favoritos e
  iconos de selección usan `selectionIcon`.
- Verificación: gates de arquitectura y documentos en verde;
  `:core:ui:testDebugUnitTest` cubre los pares AA de marca, enlace, foco,
  énfasis y selección en ambos temas. iOS sigue **no verificado** en este
  entorno.

## 12. Estado tras F2

- Se completó la capa primitiva con `emphasisSurfaceActive` (`:98`),
  `emphasisOnFillHover` (`:200`) y `emphasisOnFillActive` (`:201`), que F1
  dejó como deuda documentada.
- Los cuatro literales anónimos de H6 desaparecen de `AqAccents`: Violet e
  Indigo consumen `textMagic` / `textMagicSecondary` con `magicOnFill`
  (`:202-204`); Magenta consume `avatarOneFill` (`:255`); Lemon consume
  `cautionFill` (`:133`). Se mantiene la identidad de 12 pares para no
  alterar `hash % size` (X-06).
- PV-07: el texto publicado `avatarOneOnFill` (`:256`) queda ≈4.09:1 sobre
  `avatarOneFill`; Magenta usa blanco para alcanzar AA y el par insuficiente
  queda como constante documentada, sin consumo en componentes.
- `AqAccentTest` fija ahora el par completo de los 13 acentos y comprueba
  explícitamente PV-07. `AqSchemeTest` pinea las nuevas primitivas y
  `AqContrastPairsTest` cubre los pares renombrados.
- Verificación: gates de arquitectura, documentos y pruebas de `core/ui` en
  verde. iOS sigue **no verificado** en este entorno.

## 13. Estado tras F3

- Se publican los diez roles `text-*` de `light-mobile` con tamaño,
  interlineado, peso y tracking completos: `heading-2xl` 32/40 (`:512,:416`),
  `heading-xl` 22/28 (`:513-514`), `heading-lg` 18/24 (`:515,:426`),
  `heading-md` 16/20 (`:516,:431`), `heading-sm` 14/20 (`:517,:436`),
  `heading-xs` 12/16 (`:438,:441`), `body-lg` 18/28 (`:518-519`),
  `body-md` 16/24 (`:520-521`), `body-sm` 14/20 (`:522-523`) y
  `body-xs` 12/16 (`:524-525`).
- `AqText` y `AqHeading` resuelven el rol por tamaño en lugar de aplicar un
  ratio a cualquier tamaño. El error documentado en H3 (14 sp → 21 sp) queda
  corregido: `body-sm` usa 20 sp. PV-08 cubre únicamente tamaños arbitrarios.
- El adaptador Material y `AqTextStyles` dejan de mezclar `heading-lg` con
  `body-lg`: el rol `Title` pasa de 18/28 a 18/24, y los roles `headline*` /
  `title*` / `body*` se alinean con la tabla anterior.
- El peso y el tracking también salen del rol: 700 con `-0.3sp` en
  `heading-2xl`, 650 con `-0.2sp` en `heading-lg` y 450 con tracking normal
  en cuerpo (`:303-310,412-461`). La familia Inter estática no interpola;
  Compose selecciona el corte más cercano, restricción ya documentada en H3.
- Se mantienen como extensiones, sin equivalente móvil publicado,
  `HeadlineSmall` 24/32, `TitleLarge` 20/24, `DisplayMedium`, `Amount`,
  `AmountSymbol`, `RowTitle`, `SectionHeader`, `CaptionSmall` y `Tag`.
- Verificación: gates y pruebas de `core/ui` en verde. iOS sigue **no
  verificado** en este entorno.

## 14. Estado tras F4

- `AqElevation` incorpora `offsetY`, `spread` y las seis sombras publicadas
  `shadow-100…600` (`:363-368`). El color de la sombra pasa de negro a
  `rgba(26,26,26,α)`, tal como la instantánea.
- `shadow-100` queda anulada en el tema claro móvil (`:497`): las tarjetas que
  usan `AqElevation.Shadow1` son planas en `light-mobile` y conservan la sombra
  en `dark-experimental`, donde no existe esa anulación.
- D-10 registra que `Modifier.shadow` no puede representar `spread`; la
  geometría completa se conserva en el token y el radio de desenfoque aplicado
  es el publicado. Las insets de botones siguen resueltas con borde 1 dp,
  equivalente móvil documentado en H4.
- Verificación: gates y pruebas de `core/ui` en verde. iOS sigue **no
  verificado** en este entorno.

## 15. Estado tras F5

- El origen vectorial pasa a `PolarisIconVectors.kt`, convertido de los SVG
  oficiales de `@shopify/polaris-icons@9.3.1` (`dist/svg`). La versión queda
  fijada en el contrato (X-07) y en constantes Kotlin trazables.
- Los 34 vectores conservan `viewBox="0 0 20 20"`, los 74 atributos `d` como
  constantes y los `fill-rule="evenodd"` como `PathFillType.EvenOdd`. El relleno
  monocromo se sustituye únicamente por el tinte semántico de `AqIcon`.
- Los 36 nombres públicos de `AqIcons` se mantienen por compatibilidad y se
  mapean a los 34 vectores oficiales; `Edit`→`EditIcon` y `Trash`→`DeleteIcon`
  siguen siendo alias, no glifos adicionales.
- X-07 registra las siete extensiones sin equivalente oficial: `QrCode`,
  `SearchX`, `Inbox`, `ImageOff`, `ScanLine`, `Income` y `Expense`, con el
  verificable más cercano de la misma versión.
- El aviso de licencia del paquete publicado se incorpora a los avisos de
  terceros. La distribución del aviso en iOS sigue **no verificada** en este
  entorno.
- Verificación: `AqIconMappingTest` fija los 36 nombres, los 34 vectores
  distintos y sus dimensiones intrínsecas; gates y pruebas de `core/ui` en
  verde. El render en iOS sigue **no verificado**.

## 16. Estado tras F6 y cierre

- Los diez identificadores `Am*` del censo pasan a la familia `Aq*` en
  producción, pruebas y documentación: `LightAqColorScheme`,
  `DarkAqColorScheme`, `LocalAqColorScheme`, `LocalAqFontFamily`,
  `rememberAqInterFontFamily`, `withAqFontFamily`, `resolveAqButtonColors`,
  `rememberAqClipboard`, `LocalAqActionSheetController` y `toAqSyncState`.
  No queda ningún símbolo `Am*` en el código fuente.
- El valor por defecto de `LocalAqColorScheme` es `LightAqColorScheme`
  (`AqTokens.kt`): una composición sin proveedor hereda la base clara.
  `AqTheme` ya seguía `isSystemInDarkTheme()`, por lo que el comportamiento
  de tema del sistema no cambia.
- Se retiran las menciones activas a sistemas anteriores en
  `gradle/libs.versions.toml`, `iosApp/README.md`, `STATE.md`,
  `ARCHITECTURE.md` y la especificación UX/UI V1. Los ADR históricos y el
  índice de documentos conservan su denominación como registro histórico.
- El contrato normativo pasa a v8.0 y ADR-0026 cierra la migración de
  `core/ui` a Polaris. Quedan vigentes X-01…X-07 y PV-01…PV-08, D-10 y la
  anulación de `shadow-100` en `light-mobile`.
- Verificación: `verifyAgendaQrArchitecture`, `verifyDocs` y las pruebas de
  `core/ui` en verde. `:androidApp:testDebugUnitTest` es `NO-SOURCE` y el
  ensamblado de `androidApp` se ejecuta solo tras el cierre de las entregas.
- **No verificado:** la plataforma iOS y la distribución de avisos de
  licencia en iOS no se pueden comprobar en este entorno (sin macOS/Xcode).
  Tampoco se ha validado en dispositivo el checklist manual de la sección 10.

## 17. Segunda pasada de fidelidad visual (contrato v8.3)

Esta pasada corrige geometría visible detectada sobre las capturas de 360 dp
y sobre ventanas expandidas. No cambia roles de color ni la propiedad del
espacio de ADR-0027; ajusta radios, bordes y separación a los valores ya
publicados por la instantánea.

### Evidencia de geometría

| Elemento | Valor publicado | Fuente |
|---|---|---|
| Botón (`Button`) | `border-radius-200` = 8 dp | `styles.css` de `@shopify/polaris@12.27.0`, `.Polaris-Button` |
| Etiqueta (`Badge`, `Tag`) | `border-radius-200` = 8 dp | `.Polaris-Badge`, `.Polaris-Tag` |
| Contador dentro de filtro | `border-radius-100` = 4 dp | `.Polaris-Badge--withinFilter` |
| Campo (`TextField`, `Select`) | `border-radius-200` = 8 dp | `.Polaris-TextField__Backdrop`, `.Polaris-Select__Backdrop` |
| Tarjeta / popover | `border-radius-300` = 12 dp | `.Polaris-Popover`; auditoría §7, tarjetas con radio 12 |
| Modal | `border-radius-400` = 16 dp | `.Polaris-Modal-Dialog__Modal` |
| Botón secundario | borde inset de 1 px | `light-mobile`: `--p-shadow-button: 0 0 0 1px var(--p-color-border) inset` (`09-instantanea…:499`) |

### Correcciones aplicadas

1. **Radios.** Botones, etiquetas y `AqSelect` pasan de 4 a 8 dp. Los
   contadores de chip/segmentado pasan de 2 a 4 dp. `AqCard`,
   `AqTappableCard`, `AqListBody`, `AqQuickAction` y `AqMediaFrame` usan
   12 dp. `AqDialog`, `AqDatePickerDialog` y `AqTimePickerDialog` usan 16 dp.
   El indicador de pestañas conserva 2 dp porque es una barra, no una caja.
2. **Bordes.** El botón secundario ya no es un relleno gris: usa
   `surface` + borde de 1 dp, también en estado pulsado. `AqListBody`
   incorpora el borde hairline y el recorte de esquinas que su KDoc ya
   declaraba junto a `AqCard`.
3. **Separación.** El paso por defecto de `AqScreenScaffold` entre
   organismos pasa de 8 a 16 dp y coincide con la agrupación de tarjetas
   (D-12). El dock de acciones también separa sus acciones apiladas a 8 dp.
4. **Centrado expandido.** El contenido se limita a `ContentMaxWidth`
   (720 dp) *antes* de `fillMaxWidth`, de modo que en 840 dp la columna mide
   688 dp útiles y queda centrada con 76 dp a cada lado. El dock replica el
   límite para que sus botones no crucen la tableta.

### Verificación

- `AqAtomicSpacingContractTest` añade `expanded_content_is_capped_and_centered`.
- `AqUiPolishContractTest` añade la comprobación del relleno y borde del
  botón secundario.
- `AqButtonStyleTest` fija los colores resueltos del secundario en ambos
  temas.
- Capturas de 360 dp regeneradas y contrato de pantallas verificado.

No verificado: inspección visual automatizada de las capturas (el entorno de
esta revisión no admite entrada de imagen), iOS y escalado al 200 % siguen
siendo revisión manual.

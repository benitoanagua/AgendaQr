package com.agendaqr.core.ui.lab.model

/**
 * Central inventory of the AgendaQr component lab.
 *
 * Every entry describes a production composable or token group that really
 * exists in `core:ui`. The catalog is the single source of descriptive
 * metadata: the catalog list, the search, the inspector and the state matrix
 * all derive from these declarations.
 *
 * Honesty rules used while declaring states:
 * - a state is VERIFIED only if the current API actually offers it;
 * - DOCUMENTED records something specified by Xauxa but not implemented here;
 * - PENDING requires visual review, a design decision or an implementation;
 * - NOT_SUPPORTED records an API gap that was checked against the source.
 *
 * Logical patterns adapted from the reference game project
 * (`WwComponentContract`, `WwInteractionMatrix`, `WwApiFreeze`): separable
 * pure model, typed contracts, central inventory, explicit capability
 * classification and an inspector derived from contracts. Visual appearance
 * follows Xauxa, never the reference project.
 */
object LabComponentCatalog {

    /**
     * Estado de implementación compartido por los composables commonMain:
     * compilan en los tres targets; Android se renderiza en producción y en
     * el laboratorio; iOS y Web quedan pendientes de validación real.
     */
    private fun commonPlatforms() = listOf(
        LabPlatformStatus("Android", true, "Composable commonMain renderizado en producción y en el laboratorio."),
        LabPlatformStatus("iOS", false, "Compila en el target iOS; pendiente de validación en hardware."),
        LabPlatformStatus("Web (Wasm)", false, "Pendiente de revisión de renderizado en navegador (Fase 8)."),
    )

    /** Xauxa token foundations reviewed as tokens, not as composables. */
    val foundations: List<LabComponentContract> = listOf(
        LabComponentContract(
            id = "xauxa-color",
            name = "XauxaColor",
            category = LabCategory.FOUNDATIONS,
            purpose = "Capa semántica de color de Xauxa consumida por el código de producto.",
            description = "Tokens de color semántico. El código de producto debe consumir estos tokens " +
                "y nunca valores crudos de paleta.",
            states = listOf(
                LabState(
                    "Paleta clara",
                    "Los tokens semánticos compilan y son consumidos por los componentes de core:ui.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Paleta oscura",
                    "DarkXauxaColorScheme aplica la paleta monocromática definida en el contrato visual; " +
                        "validada en runtime Android (análisis de píxel: fondo negro puro en dark y blanco " +
                        "en light, sin crash en la recreación — pasada D6/R1).",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Tokens de fondo semántico",
                    "SuccessBg, DangerBg, WarningBg e InfoBg existen y los consumen Toast, InlineResult y StatusBanner.",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Acento cromático actual; decisión final pendiente de auditoría de identidad y contraste."),
                LabTokenRef("XauxaColor.Danger", "Semántica de error y peligro."),
                LabTokenRef("XauxaColor.DangerBg", "Contenedor de peligro."),
                LabTokenRef("XauxaColor.Surface3", "Tercera superficie del esquema."),
                LabTokenRef("XauxaColor.FocusRing", "Anillo de foco visible."),
            ),
            usage = listOf(
                "Todo color de producción debe salir de XauxaColor; la compuerta verifyDesignSystemCompliance " +
                    "rechaza hex y constructores Color fuera de la capa de tokens.",
            ),
            tags = listOf("color", "tokens", "tema"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "La paleta oscura aplica el contrato visual unificado; pendiente de validación visual de producto.",
            whenToUse = "Siempre que un componente necesite color. Nunca exponer Color ni hex en props: preferir XauxaTone.",
            androidMapping = "androidx.compose.ui.graphics.Color centralizado en object XauxaColor.",
            iosMapping = "Valores pendientes de exportar a Swift (Colors.swift de la referencia); hoy solo existe la capa Kotlin.",
        ),
        LabComponentContract(
            id = "xauxa-spacing",
            name = "XauxaSpacing",
            category = LabCategory.FOUNDATIONS,
            purpose = "Escala de espaciado de Xauxa aplicada a padding y separación.",
            description = "Escala base de 4dp con pasos de 0 a 40dp. Xauxa define s1…s10 (4…40px); " +
                "los alias None…Huge cubren los mismos valores.",
            states = listOf(
                LabState(
                    "Escala completa",
                    "None, Xs, Sm, Md, Lg, Xl, Xxl, Xxxl y Huge están declarados y en uso.",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(LabTokenRef("XauxaSpacing.Lg", "Padding estándar de paneles y tarjetas.")),
            usage = listOf("Padding de superficies, separación entre elementos y márgenes de sección."),
            tags = listOf("espaciado", "tokens"),
            darkThemeSupport = LabDarkThemeSupport.NOT_APPLICABLE,
            darkThemeNote = "Token no cromático: el tema no aplica.",
            whenToUse = "Todo padding, separación y margen. Nunca literales .dp fuera de la capa de tokens.",
            androidMapping = "androidx.compose.ui.unit.Dp en object XauxaSpacing.",
            iosMapping = "CGFloat pendiente de exportar; hoy solo existe la capa Kotlin.",
        ),
        LabComponentContract(
            id = "xauxa-metrics",
            name = "XauxaMetrics",
            category = LabCategory.FOUNDATIONS,
            purpose = "Métricas de borde, foco, target táctil y medidas de producto.",
            description = "Bordes de 1dp, anillo de foco de 2dp, target táctil mínimo de 48dp, ancho máximo " +
                "de contenido, tamaño de preview de QR e indicador de favorito.",
            states = listOf(
                LabState(
                    "Métricas de control y foco",
                    "Border, Focus y ControlMinSize cumplen los invariantes 09/10 de Xauxa (48dp, foco visible).",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Breakpoints adaptativos",
                    "Xauxa documenta breakpoints de 480px y 640px (§07); BreakpointCompact y BreakpointMedium " +
                    "existen en tokens y el laboratorio los consume para su layout adaptable.",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de controles interactivos."),
                LabTokenRef("XauxaMetrics.BreakpointMedium", "Breakpoint 640 de Xauxa §07."),
            ),
            usage = listOf("defaultMinSize de botones y targets interactivos; layout adaptable del laboratorio."),
            tags = listOf("métricas", "tokens", "accesibilidad"),
            darkThemeSupport = LabDarkThemeSupport.NOT_APPLICABLE,
            darkThemeNote = "Token no cromático: el tema no aplica.",
            whenToUse = "Bordes, foco, tamaños mínimos y medidas de producto. Nunca .dp literales.",
            androidMapping = "androidx.compose.ui.unit.Dp en object XauxaMetrics.",
            iosMapping = "CGFloat/CGSize pendiente de exportar; hoy solo existe la capa Kotlin.",
        ),
        LabComponentContract(
            id = "xauxa-type",
            name = "XauxaType",
            category = LabCategory.FOUNDATIONS,
            purpose = "Escala tipográfica de Xauxa para display, títulos, cuerpo y etiquetas.",
            description = "Seis tamaños (Display 32, Headline 24, Title 20, Body 16, Label 14, Caption 12).",
            states = listOf(
                LabState(
                    "Escala tipográfica",
                    "Los seis tamaños están declarados y se consumen en componentes.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Familias tipográficas",
                    "Como Xauxa: pila del sistema para UI (FamilyUi) y monoespaciada para " +
                        "valores (FamilyMono). Las webfonts Archivo/Roboto de la referencia " +
                        "complementaria siguen pendientes por decisión de producto.",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaType.Title", "Tamaño de título de sección y estado vacío."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking de etiquetas en mayúsculas."),
                LabTokenRef("XauxaType.FamilyMono", "Valores numéricos monoespaciados."),
            ),
            usage = listOf("Jerarquía textual de pantallas, secciones, etiquetas y metadatos."),
            tags = listOf("tipografía", "tokens"),
            darkThemeSupport = LabDarkThemeSupport.NOT_APPLICABLE,
            darkThemeNote = "Token no cromático: el tema no aplica.",
            whenToUse = "Todo tamaño de texto. Display solo para encabezados y números hero (patrón Xauxa).",
            androidMapping = "androidx.compose.ui.unit.TextUnit (sp) en object XauxaType.",
            iosMapping = "UIFont.preferredFont pendiente de mapear; hoy solo existe la capa Kotlin.",
        ),
        LabComponentContract(
            id = "xauxa-motion",
            name = "XauxaMotion",
            category = LabCategory.FOUNDATIONS,
            purpose = "Duraciones y curvas de movimiento con propósito.",
            description = "Duraciones corta 150ms, media 300ms y larga 600ms con curvas standard, " +
                "emphasized y decelerate. Sin loops decorativos; respetar reduced motion.",
            states = listOf(
                LabState(
                    "Default",
                    "XauxaMotion declara duraciones (150/300/600ms) y curvas (standard, emphasized, decelerate).",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Consumo en componentes",
                    "Ningún componente anima todavía de forma continua; Skeleton es estático y el " +
                    "movimiento con propósito queda pendiente de adopción.",
                    LabReviewStatus.PENDING,
                ),
            ),
            tokens = listOf(LabTokenRef("XauxaMotion.DurationMediumMs", "Duración media de referencia (300ms).")),
            usage = listOf("Toda animación futura de producto y laboratorio."),
            tags = listOf("movimiento", "tokens", "accesibilidad"),
            darkThemeSupport = LabDarkThemeSupport.NOT_APPLICABLE,
            darkThemeNote = "Token no cromático: el tema no aplica.",
            whenToUse = "Solo movimiento con propósito. Nunca loops decorativos ni duraciones literales.",
            androidMapping = "const Long/String en object XauxaMotion; tween() de Compose al adoptarse.",
            iosMapping = "Double/Animation.timingCurve pendiente de exportar (ver Motion.swift de la referencia).",
        ),
        LabComponentContract(
            id = "xauxa-focus",
            name = "XauxaFocus",
            category = LabCategory.FOUNDATIONS,
            purpose = "Anillo de foco visible dedicado para todo elemento interactivo.",
            description = "Color FocusRing (brand-accent) con grosor Focus de 2dp, aplicado por el " +
                "modificador xauxaFocusRing sobre clickables personalizados.",
            states = listOf(
                LabState(
                    "Default",
                    "Anillo dedicado: XauxaColor.FocusRing con grosor XauxaMetrics.Focus (2dp), " +
                    "aplicado por xauxaFocusRing en clickables personalizados.",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.FocusRing", "Color del anillo de foco."),
                LabTokenRef("XauxaMetrics.Focus", "Grosor del anillo (2dp)."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Objetivo táctil mínimo (48dp)."),
            ),
            usage = listOf("Todo clickable personalizado (tile, fila, icono, toggle, upload) aplica xauxaFocusRing."),
            tags = listOf("foco", "tokens", "accesibilidad"),
            darkThemeSupport = LabDarkThemeSupport.NOT_APPLICABLE,
            darkThemeNote = "Token no cromático en grosor; el color es fijo claro.",
            whenToUse = "Siempre en clickables personalizados. Los botones Material conservan su indicación propia.",
            androidMapping = "Modifier.border con FocusRing + collectIsFocusedAsState (xauxaFocusRing).",
            iosMapping = ".focusable() + overlay pendiente en SwiftUI.",
        ),
    )

    /** Components implemented in core:ui and consumable by production screens. */
    val components: List<LabComponentContract> = listOf(
        LabComponentContract(
            id = "xauxa-screen",
            name = "XauxaScreen",
            category = LabCategory.SURFACES,
            purpose = "Contenedor raíz de pantalla.",
            description = "Surface de ancho completo con fondo XauxaColor.Background, sin radios y sin " +
                "elevación. Es la base de toda pantalla de AgendaQr.",
            props = listOf(
                LabProp("modifier", "Modifier"),
                LabProp("content", "@Composable () -> Unit"),
            ),
            states = listOf(
                LabState("Default", "Contenedor pasivo; no tiene estados interactivos.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(LabTokenRef("XauxaColor.Background", "Fondo de pantalla.")),
            usage = listOf(
                "Base de pantallas de destinos, detalle, contexto, búsqueda global y editores.",
            ),
            notes = listOf("No usar como tarjeta ni como superficie interior de otra superficie."),
            tags = listOf("contenedor", "pantalla"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Como raíz de cada pantalla. Para bloques interiores preferir XauxaTile o XauxaSection.",
            androidMapping = "Material3 Surface a ancho completo, RectangleShape, sin elevación.",
            iosMapping = "SwiftUI ZStack/VStack a ancho completo con fondo de superficie.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-section",
            name = "XauxaSection",
            category = LabCategory.SURFACES,
            purpose = "Encabezado de sección con título y acción contextual opcional.",
            description = "Columna con título XauxaType.Title en mayúsculas con tracking, slot trailing alineado a la " +
                "derecha y contenido debajo separado por XauxaSpacing.Md (patrón tile h2 de Xauxa).",
            props = listOf(
                LabProp("title", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("trailing", "(@Composable () -> Unit)?", "null"),
                LabProp("content", "@Composable () -> Unit"),
            ),
            states = listOf(
                LabState("Default", "Sección con título y contenido.", LabReviewStatus.VERIFIED),
                LabState(
                    "Con acción contextual",
                    "El slot trailing aloja la acción de la sección (por ejemplo XauxaTextAction).",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.TextPrimary", "Color del título."),
                LabTokenRef("XauxaType.Title", "Tamaño del título."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking del título en mayúsculas."),
                LabTokenRef("XauxaSpacing.Md", "Separación entre título y contenido."),
            ),
            usage = listOf(
                "Agrupar filtros, obligaciones, comprobantes o metadatos en listas y detalles.",
            ),
            tags = listOf("sección", "encabezado"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Para agrupar contenido con título. Cabecera con banda de marca con XauxaTileHeader; sin título preferir XauxaTile directamente.",
            androidMapping = "Column + Row de encabezado (SpaceBetween) Material3 Text.",
            iosMapping = "SwiftUI VStack + HStack de encabezado.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-tile",
            name = "XauxaTile",
            category = LabCategory.SURFACES,
            purpose = "Superficie rectangular para una entidad o bloque de contenido.",
            description = "Surface plana SIN borde de reposo (V1.1 M9) con fondo XauxaColor.Surface2. Con " +
                "onClick no nulo añade rol Button, foco y anillo xauxaFocusRing.",
            props = listOf(
                LabProp("modifier", "Modifier"),
                LabProp("onClick", "(() -> Unit)?", "null"),
                LabProp("content", "@Composable () -> Unit"),
            ),
            states = listOf(
                LabState("Estático", "Tile de presentación sin acción.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Tile con onClick: rol Button y foco activables.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Activación del tile; solo se emite cuando onClick != null.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface2", "Fondo del tile (bloque de color, M9)."),
                LabTokenRef("XauxaColor.FocusRing", "Anillo de foco en modo interactivo (borde funcional, M9)."),
            ),
            usage = listOf(
                "Obligación, destino QR, comprobante, contacto o bloque resumen dentro de una sección.",
            ),
            notes = listOf(
                "El contenido debe aportar su propia semántica textual; el tile solo aporta rol Button.",
                "Para cabecera con banda de marca componer XauxaTileHeader sobre el tile.",
            ),
            tags = listOf("superficie", "tarjeta", "tile"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Bloques de contenido y entidades. Para número grande + footer preferir XauxaHeroCard.",
            androidMapping = "Material3 Surface rectangular con clickable + focusable.",
            iosMapping = "SwiftUI RoundedRectangle radius 0 + .border + .onTapGesture + .focusable().",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-page-title",
            name = "XauxaPageTitle",
            category = LabCategory.SURFACES,
            purpose = "Título de página Metro: display ligero (M6).",
            description = "Archivo peso Light 300 a XauxaType.DisplayPage (>= 40 sp), capitalización de " +
                "oración y semántica heading(). V1.1 (ADR-0005, spec §12 Tipografía).",
            props = listOf(
                LabProp("text", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("color", "Color", "XauxaColor.TextPrimary"),
            ),
            states = listOf(
                LabState("Default", "Título de página en display ligero.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaType.DisplayPage", "Tamaño del título de página (>= 40 sp)."),
                LabTokenRef("XauxaColor.TextPrimary", "Color del título."),
            ),
            usage = listOf("Título raíz de cada pantalla V1.1 (S01 Agenda QR, S06 nombre de contexto)."),
            tags = listOf("título", "tipografía", "metro"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; validación visual de oscuro pendiente en T8.",
            whenToUse = "Título raíz de pantalla. Encabezados de sección con XauxaSectionHeader.",
            androidMapping = "Material3 Text con FontFamily Archivo Light.",
            iosMapping = "Mismo typeface empaquetado; no SF Display de plataforma.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-section-header",
            name = "XauxaSectionHeader",
            category = LabCategory.SURFACES,
            purpose = "Encabezado de sección pequeño en color de acento (M6).",
            description = "14-16 sp (XauxaType.SectionHeader) en color de acento — sistema por defecto, " +
                "acento del contexto en S06 (M4). Semántica heading(). V1.1 (ADR-0005).",
            props = listOf(
                LabProp("text", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("accent", "Color", "XauxaColor.Brand"),
            ),
            states = listOf(
                LabState("Default", "Encabezado en acento de sistema.", LabReviewStatus.VERIFIED),
                LabState("Acento de contexto", "accent derivado del contexto (accentFor).", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaType.SectionHeader", "Tamaño del encabezado (14-16 sp)."),
                LabTokenRef("XauxaColor.Brand", "Acento por defecto."),
            ),
            usage = listOf("Secciones de S04/S05 (tipo de resultado) y del pivot de S06."),
            tags = listOf("encabezado", "sección", "metro"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "El acento fijado por acento+texto es válido en claro y oscuro (spec §12); validación visual en T8.",
            whenToUse = "Encabezados de sección. Título de página con XauxaPageTitle.",
            androidMapping = "Material3 Text con color de acento.",
            iosMapping = "SwiftUI Text con tinte de acento.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-metro-tile",
            name = "XauxaMetroTile",
            category = LabCategory.SURFACES,
            purpose = "Tile Metro: bloque plano de acento, icono centrado, etiqueta abajo a la izquierda.",
            description = "Bloque de color plano sin borde ni sombra (M3/M9), tilt <=150 ms al presionar " +
                "(M11; sin tilt con reduced motion) y etiqueta siempre visible (M14: rol Button, nombre, " +
                "estado y orden de foco). V1.1 (ADR-0005).",
            props = listOf(
                LabProp("label", "String"),
                LabProp("accent", "XauxaAccent"),
                LabProp("onClick", "(() -> Unit)?"),
                LabProp("size", "XauxaTileSize", "SMALL"),
                LabProp("icon", "ImageVector?", "null"),
                LabProp("content", "(@Composable () -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Bloque plano con etiqueta visible.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Con onClick: rol Button, foco y tilt al presionar.", LabReviewStatus.VERIFIED),
                LabState("Ancho con contenido", "WIDE: contenido en texto (último QR/actividad), etiqueta visible.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Activación del tile (cuando onClick != null).")),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.TileUnit", "Unidad 1x1 de la rejilla."),
                LabTokenRef("XauxaMetrics.TileWideHeight", "Alto de los tiles 2x2 y 4x2."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Objetivo táctil mínimo."),
                LabTokenRef("XauxaMetrics.IconSizeTile", "Tamaño del icono centrado."),
                LabTokenRef("XauxaColor.FocusRing", "Anillo de foco sobre el acento (M9/M14)."),
                LabTokenRef("XauxaMotion.DurationShortMs", "Tilt <=150 ms y entrada del tile."),
            ),
            usage = listOf("Rejilla de S01: Añadir, Registrar, Favoritos, Contextos y tile vivo ancho."),
            notes = listOf(
                "El tile ancho omite el icono y muestra contenido textual; la etiqueta nunca desaparece.",
                "El acento viene de XauxaAccents: par fondo/texto fijo por contraste calculado (M5).",
            ),
            tags = listOf("tile", "metro", "rejilla"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Acentos idénticos en ambos temas por diseño (spec §12 Modo oscuro); validación visual en T8.",
            whenToUse = "Entradas de acción de S01. Bloques de contenido sin acción con XauxaTile.",
            androidMapping = "Box + background plano + clickable con rol Button.",
            iosMapping = "SwiftUI Rectangle + onTapGesture + accessibilityLabel.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-tile-grid",
            name = "XauxaTileGrid",
            category = LabCategory.SURFACES,
            purpose = "Rejilla Metro de 4 columnas con entrada escalonada (M3/M11).",
            description = "Coloca XauxaTileItem en 4 columnas en compacto (reduce columnas antes que " +
                "comprimir); el tile ancho ocupa la fila. Entrada escalonada de 30-50 ms por tile, total " +
                "<=300 ms; con reduced motion aparece de inmediato.",
            props = listOf(
                LabProp("items", "List<XauxaTileItem>"),
                LabProp("modifier", "Modifier"),
                LabProp("reducedMotion", "Boolean", "LocalReducedMotion.current"),
            ),
            states = listOf(
                LabState("Default", "Rejilla compuesta por unidades de 4 columnas.", LabReviewStatus.VERIFIED),
                LabState("Entrada escalonada", "Fade por tile, total <=300 ms (sin reduced motion).", LabReviewStatus.VERIFIED),
                LabState("Reduced motion", "Sin escalonado ni transición: estado inmediato.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.TileUnit", "Unidad base de la rejilla (1x1)."),
                LabTokenRef("XauxaSpacing.TileGap", "Separación entre tiles (multiplo de 4 dp)."),
                LabTokenRef("XauxaSpacing.ScreenMargin", "Margen de pantalla (multiplo de 4 dp)."),
                LabTokenRef("XauxaMotion.DurationMediumMs", "Tope total del escalonado (300 ms)."),
            ),
            usage = listOf("S01: rejilla de acciones principales."),
            notes = listOf("La comprension nunca depende de la animacion (spec §11)."),
            tags = listOf("rejilla", "tile", "metro", "motion"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Composición de acentos fijos; validación visual en T8.",
            whenToUse = "S01 y cualquier superficie de entrada con tiles.",
            androidMapping = "Column/Row manual con spans (sin LazyGrid: el conjunto de S01 es finito y corto).",
            iosMapping = "SwiftUI Grid con columnas adaptadas.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-hero-card",
            name = "XauxaHeroCard",
            category = LabCategory.SURFACES,
            purpose = "Tarjeta hero con número grande y footer de estadísticas.",
            description = "Compone XauxaTile: valor en tipografía Display, etiqueta y footer opcional " +
                "con borde superior (patrón tile-stats de Xauxa). No duplica superficie.",
            props = listOf(
                LabProp("value", "String"),
                LabProp("label", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("footer", "String?", "null"),
                LabProp("onClick", "(() -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Número grande con etiqueta.", LabReviewStatus.VERIFIED),
                LabState("Con footer", "Footer de estadísticas con borde superior.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Con onClick delega rol Button y foco al tile.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Activación de la tarjeta; solo se emite cuando onClick != null.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface", "Fondo heredado del tile."),
                LabTokenRef("XauxaType.Display", "Tamaño del número grande."),
                LabTokenRef("XauxaType.FamilyMono", "Numerales monoespaciados."),
                LabTokenRef("XauxaMetrics.BorderStrong", "Separador del footer (2px)."),
                LabTokenRef("XauxaType.Caption", "Tamaño del footer."),
                LabTokenRef("XauxaSpacing.Lg", "Padding interior."),
            ),
            usage = listOf("Totales del dashboard, saldos y contadores destacados a pantalla completa."),
            notes = listOf("Un solo hero por pantalla; el footer es texto, nunca solo color."),
            tags = listOf("tarjeta", "hero", "estadística", "dashboard"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Números destacados con contexto. Para bloques sin número preferir XauxaTile.",
            androidMapping = "XauxaTile + Material3 Text Display/Caption.",
            iosMapping = "Tile SwiftUI + Text de jerarquía display/footnote.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-tile-header",
            name = "XauxaTileHeader",
            category = LabCategory.SURFACES,
            purpose = "Cabecera de tile con banda de marca.",
            description = "Banda XauxaColor.Brand a ancho completo con título en mayúsculas, estado " +
                "opcional y slot de acciones (patrón tile-header de Xauxa, sin sombra).",
            props = listOf(
                LabProp("title", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("status", "String?", "null"),
                LabProp("actions", "(@Composable () -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Banda con título.", LabReviewStatus.VERIFIED),
                LabState("Con estado", "status no nulo: etiqueta de estado en la banda.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Fondo de la banda."),
                LabTokenRef("XauxaColor.OnBrand", "Título y estado."),
                LabTokenRef("XauxaType.Title", "Tamaño del título."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking del título."),
                LabTokenRef("XauxaSpacing.Lg", "Padding de la banda."),
            ),
            usage = listOf(
                "Cabeceras de tiles de dashboard (escáner, historial, resultados).",
            ),
            tags = listOf("cabecera", "tile", "marca", "dashboard"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Cabeceras con banda de marca. Secciones de contenido con XauxaSection.",
            androidMapping = "Row con fondo Brand a ancho completo.",
            iosMapping = "SwiftUI HStack con fondo de marca.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-dialog",
            name = "XauxaDialog",
            category = LabCategory.SURFACES,
            purpose = "Diálogo modal con confirmación destructiva de solo dos acciones.",
            description = "AlertDialog rectangular sin elevación: título, mensaje, confirmación danger " +
                "y descarte textual. Exactamente dos acciones por API.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("message", "String"),
                LabProp("confirmLabel", "String"),
                LabProp("onConfirm", "() -> Unit"),
                LabProp("dismissLabel", "String"),
                LabProp("onDismiss", "() -> Unit"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState("Default", "Diálogo visible con sus dos acciones.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("onConfirm", "Confirmación de la acción destructiva."),
                LabEvent("onDismiss", "Descarte sin efectos."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface", "Fondo del diálogo."),
                LabTokenRef("XauxaColor.TextPrimary", "Color del título."),
                LabTokenRef("XauxaColor.TextSecondary", "Color del mensaje."),
                LabTokenRef("XauxaType.Title", "Tamaño del título."),
            ),
            usage = listOf("Confirmación destructiva (eliminar destino o comprobante) seguida de toast con Deshacer."),
            notes = listOf("Nunca más de dos acciones; la destructiva siempre es XauxaDangerButton."),
            tags = listOf("diálogo", "modal", "confirmación"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Confirmaciones destructivas. Para avisos no bloqueantes preferir XauxaToast.",
            androidMapping = "Material3 AlertDialog con RectangleShape.",
            iosMapping = "SwiftUI .confirmationDialog o .alert con dos botones.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-pivot",
            name = "XauxaPivot",
            category = LabCategory.SURFACES,
            purpose = "Pivot de secciones (M7, S06): QR / Actividades / Comprobantes.",
            description = "Encabezados de seccion con la activa en acento; la conmutacion es local y no " +
                "crea rutas ni navegacion nueva (spec §3). Cada pestana declara rol Tab con estado " +
                "selected y etiqueta de texto visible (§11/M14). Sin panorama horizontal en V1.1.",
            props = listOf(
                LabProp("sections", "List<String>"),
                LabProp("selectedIndex", "Int"),
                LabProp("onSelect", "(Int) -> Unit"),
                LabProp("accent", "Color", "XauxaColor.Brand"),
                LabProp("content", "@Composable (Int) -> Unit"),
            ),
            states = listOf(
                LabState("Default", "Primera seccion activa.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Cambio de seccion con rol Tab y foco visible.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onSelect", "Selección de seccion; el caller conserva la pantalla actual.")),
            tokens = listOf(
                LabTokenRef("XauxaType.SectionHeader", "Titulos de seccion del pivot."),
                LabTokenRef("XauxaColor.Brand", "Acento por defecto de la seccion activa."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Objetivo tactil de cada pestana."),
            ),
            usage = listOf("S06 Contexto: secciones QR, Actividades y Comprobantes (modelo mental §2)."),
            tags = listOf("pivot", "secciones", "metro"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; validacion visual en T8.",
            whenToUse = "S06. No es una navegacion nueva: dentro de una misma pantalla.",
            androidMapping = "Row de pestañas textuales + contenido conmutado.",
            iosMapping = "SwiftUI HStack + contenido conmutado (sin UIPageViewController).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-app-bar",
            name = "XauxaAppBar",
            category = LabCategory.ACTIONS,
            purpose = "Barra de aplicacion inferior: 2-4 acciones con icono + etiqueta visible y menu ... .",
            description = "Aloja la accion principal (bloque solido de acento, M10) y las secundarias de " +
                "la pantalla (M7). Sustituye a botones sueltos de Volver/Guardar/Cerrar sesion en el " +
                "cuerpo. Respeta insets de navegacion/teclado: la accion principal nunca queda recortada. " +
                "Etiqueta visible en TODO icono (§11/M12: desviacion consciente de Metro).",
            props = listOf(
                LabProp("actions", "List<XauxaAppBarAction>", "2-4 con icono + etiqueta"),
                LabProp("modifier", "Modifier"),
                LabProp("overflowActions", "List<XauxaOverflowAction>", "emptyList()"),
                LabProp("onBack", "(() -> Unit)?", "null"),
                LabProp("backLabel", "String", "'Atrás'"),
            ),
            states = listOf(
                LabState("Default", "Acciones con icono + etiqueta; principal en bloque de acento.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Activacion de acciones, overflow y atras.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("action.onClick", "Ejecuta la accion etiquetada."),
                LabEvent("onBack", "Atras (requerido en iOS; en Android el Back del sistema se mantiene)."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.AppBarHeight", "Altura minima de la barra."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Objetivo tactil de cada accion."),
                LabTokenRef("XauxaColor.Brand", "Bloque de acento de la accion principal."),
                LabTokenRef("XauxaColor.FocusRing", "Anillo de foco sobre cada accion."),
            ),
            usage = listOf("Barra inferior de cada pantalla V1.1 (accion principal + secundarias + ...)."),
            notes = listOf(
                "El boton ... reutiliza XauxaOverflowAction de XauxaCommandBar (misma semantica de menu).",
                "La flecha atras es requerida en iOS y redundante en Android (spec §12).",
            ),
            tags = listOf("appbar", "navegacion", "acciones", "metro"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; validacion visual en T8.",
            whenToUse = "Acciones primaria/secundarias de pantalla. Secciones internas con XauxaPivot.",
            androidMapping = "Surface rectangular anclada a bottomBar del scaffold.",
            iosMapping = "SwiftUI safeArea bottom bar con etiqueta e icono.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-primary-button",
            name = "XauxaPrimaryButton",
            category = LabCategory.ACTIONS,
            purpose = "Acción primaria de AgendaQr.",
            description = "Botón relleno con XauxaColor.Brand y altura mínima de XauxaMetrics.ControlMinSize, " +
                "sin radios. Con isLoading muestra espera y bloquea la activación.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
                LabProp("isLoading", "Boolean", "false"),
            ),
            states = listOf(
                LabState("Default", "Botón habilitado con fondo de marca.", LabReviewStatus.VERIFIED),
                LabState(
                    "Deshabilitado",
                    "enabled = false cambia el contenedor a Surface2 y el texto a TextTertiary.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Carga",
                    "isLoading = true muestra indicador y deshabilita la activación.",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            events = listOf(LabEvent("onClick", "Evento de activación principal.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Contenedor del botón."),
                LabTokenRef("XauxaColor.OnBrand", "Contenido sobre la marca."),
                LabTokenRef("XauxaColor.Surface2", "Contenedor deshabilitado."),
                LabTokenRef("XauxaColor.TextTertiary", "Contenido deshabilitado."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking de la etiqueta en mayúsculas."),
            ),
            usage = listOf(
                "Añadir QR, guardar obligación, registrar comprobante, confirmar una acción.",
            ),
            notes = listOf("Una sola acción primaria visible por pantalla."),
            tags = listOf("botón", "acción", "primario"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "La acción principal de la pantalla. Secundarias con XauxaSecondaryButton; peligro con XauxaDangerButton.",
            androidMapping = "Material3 Button rectangular con ButtonDefaults.buttonColors de marca.",
            iosMapping = "SwiftUI Button con .buttonStyle(.borderedProminent) y radio 0.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-secondary-button",
            name = "XauxaSecondaryButton",
            category = LabCategory.ACTIONS,
            purpose = "Acción secundaria o alternativa.",
            description = "Botón outlined con borde XauxaColor.Border y texto TextPrimary, con la misma " +
                "altura mínima de control que el primario. Con isLoading muestra espera.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
                LabProp("isLoading", "Boolean", "false"),
            ),
            states = listOf(
                LabState("Default", "Botón outlined habilitado.", LabReviewStatus.VERIFIED),
                LabState("Deshabilitado", "enabled = false atenúa el borde y el contenido.", LabReviewStatus.VERIFIED),
                LabState("Carga", "isLoading = true muestra indicador y deshabilita la activación.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Evento de activación principal.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Border", "Borde del botón."),
                LabTokenRef("XauxaColor.TextPrimary", "Color del texto."),
                LabTokenRef("XauxaMetrics.Border", "Grosor del borde."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking de la etiqueta en mayúsculas."),
            ),
            usage = listOf("Escanear, ver detalle, abrir comprobante, cancelar un diálogo."),
            tags = listOf("botón", "acción", "secundario"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Acciones alternativas. La principal con XauxaPrimaryButton; peligro con XauxaDangerButton.",
            androidMapping = "Material3 OutlinedButton rectangular.",
            iosMapping = "SwiftUI Button con .buttonStyle(.bordered) y radio 0.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-danger-button",
            name = "XauxaDangerButton",
            category = LabCategory.ACTIONS,
            purpose = "Acción destructiva o de peligro.",
            description = "Botón relleno con XauxaColor.Danger y texto blanco, misma altura mínima y " +
                "estados que el primario. Reservado a acciones irreversibles.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
                LabProp("isLoading", "Boolean", "false"),
            ),
            states = listOf(
                LabState("Default", "Botón habilitado con fondo de peligro.", LabReviewStatus.VERIFIED),
                LabState("Deshabilitado", "enabled = false atenúa a Surface2/TextTertiary.", LabReviewStatus.VERIFIED),
                LabState("Carga", "isLoading = true muestra indicador y deshabilita la activación.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Evento de activación destructiva.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Danger", "Contenedor del botón."),
                LabTokenRef("XauxaColor.White", "Contenido sobre peligro."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking de la etiqueta en mayúsculas."),
            ),
            usage = listOf("Confirmar eliminación en XauxaDialog; quitar comprobante o destino."),
            notes = listOf("Solo dentro de confirmaciones destructivas; nunca como acción primaria de pantalla."),
            tags = listOf("botón", "acción", "peligro", "destructivo"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Únicamente acciones destructivas confirmadas. Resto con primary/secondary.",
            androidMapping = "Material3 Button rectangular con contenedor Danger.",
            iosMapping = "SwiftUI Button con rol .destructive.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-text-action",
            name = "XauxaTextAction",
            category = LabCategory.ACTIONS,
            purpose = "Acción textual de bajo énfasis.",
            description = "TextButton con color de marca y altura mínima de control, sin relleno ni borde.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState("Default", "Acción textual habilitada.", LabReviewStatus.VERIFIED),
                LabState(
                    "Deshabilitado",
                    "La API no expone enabled: no se puede presentar deshabilitado. Brecha frente a la matriz " +
                        "de estados de Xauxa, que exige disabled en controles.",
                    LabReviewStatus.NOT_SUPPORTED,
                ),
            ),
            events = listOf(LabEvent("onClick", "Evento de activación principal.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Color del texto."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking de la etiqueta en mayúsculas."),
            ),
            usage = listOf("Editar, cancelar o disparar una acción secundaria dentro de tiles o secciones."),
            tags = listOf("acción", "texto"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Acciones terciarias en línea. Con peso visual usar botones; como icono usar XauxaIconButton.",
            androidMapping = "Material3 TextButton rectangular.",
            iosMapping = "SwiftUI Button con .buttonStyle(.plain).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-icon-button",
            name = "XauxaIconButton",
            category = LabCategory.ACTIONS,
            purpose = "Acción solo-icono con etiqueta accesible y área táctil suficiente.",
            description = "Objetivo de 48dp con rol Button, contentDescription obligatoria y anillo " +
                "de foco. El contenido lo aporta el llamador (texto o icono).",
            props = listOf(
                LabProp("contentDescription", "String"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
                LabProp("content", "@Composable () -> Unit"),
            ),
            states = listOf(
                LabState("Default", "Acción habilitada con etiqueta accesible.", LabReviewStatus.VERIFIED),
                LabState("Deshabilitado", "enabled = false bloquea la activación.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Evento de activación.")),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.ControlMinSize", "Área táctil de 48dp."),
                LabTokenRef("XauxaColor.FocusRing", "Anillo de foco."),
            ),
            usage = listOf("Copiar resultado, abrir comprobante, descartar notificación, voltear cámara."),
            tags = listOf("icono", "botón", "acción", "accesibilidad"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Acciones compactas con etiqueta. Con texto visible preferir botones o XauxaTextAction.",
            androidMapping = "Box clickable + focusable con semantics de contentDescription.",
            iosMapping = "SwiftUI Button con .accessibilityLabel y .frame(minWidth:44, minHeight:44).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-icon",
            name = "XauxaIcon",
            category = LabCategory.DATA,
            purpose = "Único punto de entrada de iconografía de producto (set Lucide, ISC).",
            description = "T2/V1.1 (ADR-0005, spec §12): glifos de línea de un solo color, tamaño y " +
                "color por tokens. contentDescription nulo marca el icono como decorativo y lo " +
                "excluye de la accesibilidad; los iconos interactivos siempre llevan etiqueta de " +
                "texto visible compuesta por el llamador (nunca el icono solo).",
            props = listOf(
                LabProp("imageVector", "ImageVector"),
                LabProp("contentDescription", "String?"),
                LabProp("modifier", "Modifier"),
                LabProp("size", "Dp", "XauxaMetrics.IconSize"),
                LabProp("tint", "Color", "XauxaColor.TextPrimary"),
            ),
            states = listOf(
                LabState("Default", "Glifo con nombre accesible (contentDescription no nulo).", LabReviewStatus.VERIFIED),
                LabState("Decorativo", "contentDescription nulo: excluido de la accesibilidad (clearAndSetSemantics).", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.IconSize", "Tamaño por defecto del icono."),
                LabTokenRef("XauxaColor.TextPrimary", "Tinte por defecto (un solo color)."),
            ),
            usage = listOf(
                "Iconos de la app bar inferior, de tiles y de acciones secundarias con etiqueta visible.",
            ),
            notes = listOf(
                "El mapeo mínimo de la spec vive en XauxaIcons (Search, Add, Register, Favorite, " +
                    "Context, Back, Save, Cancel, More, Gallery, Camera); fuera de esa lista no se " +
                    "añaden iconos sin decisión.",
                "Las acciones interactivas usan XauxaIconButton/XauxaAppBar con etiqueta visible; " +
                    "XauxaIcon aporta solo el glifo.",
            ),
            tags = listOf("icono", "lucide", "accesibilidad"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "El tinte se resuelve vía XauxaColor o el color del acento del contexto; el esquema oscuro queda pendiente de validación visual.",
            whenToUse = "Para cualquier glifo de producto. Nunca sustituye por sí solo a una acción etiquetada.",
            androidMapping = "Material3 Icon con vector Lucide (com.composables:icons-lucide).",
            iosMapping = "Mismo vector compartido; sin SF Symbols de plataforma.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-filter-chip",
            name = "XauxaFilterChip",
            category = LabCategory.ACTIONS,
            purpose = "Filtro interactivo de selección simple o múltiple.",
            description = "FilterChip rectangular: seleccionado usa fondo Brand; altura mínima de " +
                "control. Distinto de XauxaCategoryChip, que solo clasifica.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("selected", "Boolean"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
            ),
            states = listOf(
                LabState("Default", "Filtro sin seleccionar.", LabReviewStatus.VERIFIED),
                LabState("Seleccionado", "selected = true: fondo de marca.", LabReviewStatus.VERIFIED),
                LabState("Deshabilitado", "enabled = false bloquea la activación.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Evento de selección del filtro.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Fondo seleccionado y borde."),
                LabTokenRef("XauxaColor.OnBrand", "Texto seleccionado."),
                LabTokenRef("XauxaColor.Border", "Borde sin seleccionar."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking de la etiqueta en mayúsculas."),
            ),
            usage = listOf("Filtrar historial por tipo (patrón filter-pills de Xauxa)."),
            notes = listOf("Filtra; no clasifica: para etiquetas estáticas usar XauxaCategoryChip."),
            tags = listOf("chip", "filtro", "selección"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Filtros que cambian el contenido. Etiquetas estáticas con XauxaCategoryChip.",
            androidMapping = "Material3 FilterChip rectangular.",
            iosMapping = "SwiftUI Button con borde y fondo condicional.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-text-input",
            name = "XauxaTextInput",
            category = LabCategory.ACTIONS,
            purpose = "Entrada de texto etiquetada con estado de error.",
            description = "OutlinedTextField rectangular con etiqueta, error textual y altura mínima " +
                "de control. Con singleLine = false y minLines funciona como textarea.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("value", "String"),
                LabProp("onValueChange", "(String) -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
                LabProp("isError", "Boolean", "false"),
                LabProp("errorMessage", "String?", "null"),
                LabProp("singleLine", "Boolean", "true"),
                LabProp("minLines", "Int", "1"),
            ),
            states = listOf(
                LabState("Default", "Entrada vacía o con contenido.", LabReviewStatus.VERIFIED),
                LabState("Error", "isError = true: indicador Danger y mensaje textual.", LabReviewStatus.VERIFIED),
                LabState("Deshabilitado", "enabled = false bloquea la edición.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onValueChange", "Emitido por cada cambio aceptado.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Indicador enfocado."),
                LabTokenRef("XauxaColor.Border", "Indicador sin foco."),
                LabTokenRef("XauxaColor.Danger", "Indicador y mensaje de error."),
                LabTokenRef("XauxaColor.TextPrimary", "Texto y etiqueta."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
            ),
            usage = listOf("Nombre de destino, notas de obligación, montos y descripciones (textarea)."),
            notes = listOf("El error nunca es solo color: el mensaje textual es obligatorio."),
            tags = listOf("entrada", "texto", "formulario", "error"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Captura de texto en formularios. Búsqueda global con XauxaSearchBar.",
            androidMapping = "Material3 OutlinedTextField con RectangleShape.",
            iosMapping = "SwiftUI TextField/TextEditor con .border y .frame(minHeight:44).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-search-bar",
            name = "XauxaSearchBar",
            category = LabCategory.ACTIONS,
            purpose = "Búsqueda textual de una línea con limpieza opcional.",
            description = "OutlinedTextField rectangular de una línea con etiqueta, placeholder y " +
                "acción Limpiar cuando hay texto y onClear no es nulo.",
            props = listOf(
                LabProp("value", "String"),
                LabProp("onValueChange", "(String) -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("label", "String", "\"Buscar\""),
                LabProp("placeholder", "String", "\"Buscar\""),
                LabProp("onClear", "(() -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Campo vacío con placeholder.", LabReviewStatus.VERIFIED),
                LabState("Con texto", "Muestra la acción Limpiar cuando onClear no es nulo.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("onValueChange", "Emitido por cada cambio aceptado."),
                LabEvent("onClear", "Limpieza del texto; solo disponible con texto no vacío."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Indicador enfocado."),
                LabTokenRef("XauxaColor.Border", "Indicador sin foco."),
                LabTokenRef("XauxaColor.TextTertiary", "Placeholder."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
            ),
            usage = listOf("Búsqueda global de destinos y filtrado del catálogo del laboratorio."),
            tags = listOf("búsqueda", "entrada", "filtro"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Búsquedas. Formularios de captura con XauxaTextInput.",
            androidMapping = "Material3 OutlinedTextField singleLine con trailingIcon.",
            iosMapping = "SwiftUI .searchable o TextField con botón de limpieza.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-setting-row",
            name = "XauxaSettingRow",
            category = LabCategory.ACTIONS,
            purpose = "Fila de ajuste con toggle cuadrado o navegación.",
            description = "Fila con título, descripción y toggle rectangular de marca; con checked " +
                "nulo actúa como fila de navegación. Toda la fila es el objetivo táctil.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("description", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("checked", "Boolean?", "null"),
                LabProp("onCheckedChange", "((Boolean) -> Unit)?", "null"),
                LabProp("onClick", "(() -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Presentación base apagada; checked = false con fondo Surface2.", LabReviewStatus.VERIFIED),
                LabState("Encendido", "checked = true: toggle con fondo de marca.", LabReviewStatus.VERIFIED),
                LabState("Navegación", "checked = null con onClick: fila de acción sin toggle.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("onCheckedChange", "Cambio del ajuste."),
                LabEvent("onClick", "Activación en modo navegación."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Fondo del toggle encendido."),
                LabTokenRef("XauxaColor.Surface2", "Fondo del toggle apagado."),
                LabTokenRef("XauxaColor.White", "Pastilla del toggle."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de fila y toggle."),
            ),
            usage = listOf("Ajustes de AgendaQr (patrón setting-toggle de Xauxa, cuadrado por invariante 02)."),
            tags = listOf("ajuste", "toggle", "selector", "preferencia"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Ajustes binarios y filas de preferencia. Favoritos con XauxaFavoriteToggle.",
            androidMapping = "Row clickable + toggle cuadrado personalizado.",
            iosMapping = "SwiftUI Toggle o NavigationLink según modo.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-favorite-toggle",
            name = "XauxaFavoriteToggle",
            category = LabCategory.ACTIONS,
            purpose = "Toggle interactivo de favorito.",
            description = "Control real de 48dp con rol Checkbox y anillo de foco. La integración " +
                "con producto queda pendiente: hoy no persiste.",
            props = listOf(
                LabProp("favorite", "Boolean"),
                LabProp("onCheckedChange", "(Boolean) -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("contentDescription", "String", "\"Marcar como favorito\""),
            ),
            states = listOf(
                LabState("Marcado", "favorite = true: indicador con marca.", LabReviewStatus.VERIFIED),
                LabState("Sin marcar", "favorite = false: indicador Surface2.", LabReviewStatus.VERIFIED),
                LabState(
                    "Integración de producto",
                    "El control es real pero no persiste: demostración sin conexión a datos.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            events = listOf(LabEvent("onCheckedChange", "Cambio del estado de favorito.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Fondo marcado."),
                LabTokenRef("XauxaColor.Surface2", "Fondo sin marcar."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Área táctil de 48dp."),
                LabTokenRef("XauxaMetrics.FavoriteIndicatorSize", "Tamaño del indicador."),
            ),
            usage = listOf("Marcar destinos u obligaciones como favoritos en listas y detalles."),
            notes = listOf("Para solo presentación usar XauxaFavoriteIndicator; para interacción este toggle."),
            tags = listOf("favorito", "toggle", "demostración"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Interacción de favorito. Indicador pasivo con XauxaFavoriteIndicator.",
            androidMapping = "Box clickable con rol Checkbox + indicador.",
            iosMapping = "SwiftUI Button con .accessibilityAddTraits(.isSelected).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-toast",
            name = "XauxaToast",
            category = LabCategory.FEEDBACK,
            purpose = "Notificación con acción y descarte.",
            description = "Fila con marcador semántico de 2px, fondo de contenedor -bg, acción " +
                "opcional y descarte. Sin auto-cierre en loop: el llamador decide la duración.",
            props = listOf(
                LabProp("message", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("tone", "XauxaTone", "Neutral"),
                LabProp("actionLabel", "String?", "null"),
                LabProp("onAction", "(() -> Unit)?", "null"),
                LabProp("onDismiss", "(() -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Mensaje neutro sobre Surface2.", LabReviewStatus.VERIFIED),
                LabState("Tonos semánticos", "Success, Danger, Warning e Info con fondos -bg.", LabReviewStatus.VERIFIED),
                LabState("Con acción", "actionLabel + onAction muestran la acción textual.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("onAction", "Activación de la acción de la notificación."),
                LabEvent("onDismiss", "Descarte de la notificación."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.SuccessBg", "Contenedor de éxito."),
                LabTokenRef("XauxaColor.DangerBg", "Contenedor de peligro."),
                LabTokenRef("XauxaColor.TextPrimary", "Mensaje."),
                LabTokenRef("XauxaType.Label", "Tamaño del mensaje."),
            ),
            usage = listOf("Confirmación con Deshacer tras eliminar; fallos puntuales con reintento."),
            notes = listOf("Efímera y descartable; para estado persistente usar XauxaStatusBanner."),
            tags = listOf("notificación", "toast", "acción", "feedback"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Fallo puntual o confirmación con acción. Estado persistente con XauxaStatusBanner.",
            androidMapping = "Row personalizada (Snackbar solo admite una acción y otro estilo).",
            iosMapping = "SwiftUI overlay HStack con borde y fondos semánticos.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-status-banner",
            name = "XauxaStatusBanner",
            category = LabCategory.FEEDBACK,
            purpose = "Mensaje persistente de estado.",
            description = "Caja de ancho completo con borde; el tono se declara con tone (T12: danger se retiró). " +
                "fondo DangerBg y texto Danger.",
            props = listOf(
                LabProp("message", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("danger", "Boolean", "false"),
            ),
            states = listOf(
                LabState("Neutro", "Mensaje informativo sobre Surface2.", LabReviewStatus.VERIFIED),
                LabState("Peligro", "tone = Danger: fondo DangerBg y texto Danger.", LabReviewStatus.VERIFIED),
                LabState(
                    "Tonos éxito/información/advertencia",
                    "Existen tokens SuccessBg/WarningBg/InfoBg; el banner actual solo distingue " +
                        "neutro y peligro.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Danger", "Semántica de peligro."),
                LabTokenRef("XauxaColor.DangerBg", "Fondo de peligro."),
                LabTokenRef("XauxaColor.Surface2", "Fondo neutro."),
                LabTokenRef("XauxaColor.TextSecondary", "Texto neutro."),
                LabTokenRef("XauxaColor.Border", "Borde."),
                LabTokenRef("XauxaSpacing.Lg", "Padding del banner."),
                LabTokenRef("XauxaType.Label", "Tamaño del texto."),
            ),
            usage = listOf(
                "Estado de sincronización, comprobante pendiente de revisión, fallo de guardado.",
            ),
            notes = listOf("El estado no debe comunicarse solo con color: el texto del mensaje es obligatorio."),
            tags = listOf("feedback", "banner", "estado"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Estado persistente en contexto. Fallo puntual con XauxaToast; contenido con XauxaInlineResult.",
            androidMapping = "Box con borde y fondo de contenedor.",
            iosMapping = "SwiftUI VStack con .border y fondos semánticos.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-inline-result",
            name = "XauxaInlineResult",
            category = LabCategory.FEEDBACK,
            purpose = "Resultado embebido con marcador semántico y acciones.",
            description = "Banner de contenido con marcador lateral de 2px, fondo -bg por tono y " +
                "slot de acciones. Patrón result-item de Xauxa sin radios ni sombras.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("meta", "String?", "null"),
                LabProp("tone", "XauxaTone", "Info"),
                LabProp("actions", "(@Composable () -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Resultado informativo con fondo InfoBg.", LabReviewStatus.VERIFIED),
                LabState("Tonos semánticos", "Success, Danger, Warning e Info con fondos -bg.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.InfoBg", "Contenedor informativo."),
                LabTokenRef("XauxaColor.Info", "Marcador informativo."),
                LabTokenRef("XauxaColor.TextPrimary", "Título."),
                LabTokenRef("XauxaColor.TextSecondary", "Metadatos."),
            ),
            usage = listOf("Resultado de escaneo o importación dentro del flujo, sin salir de contexto."),
            tags = listOf("resultado", "banner", "feedback"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Resultados en contexto con acciones. Filas de lista con XauxaListRow; página con XauxaErrorPage.",
            androidMapping = "Row con marcador lateral y slot de acciones.",
            iosMapping = "SwiftUI HStack con marcador y fondo semántico.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-loading",
            name = "XauxaLoading",
            category = LabCategory.FEEDBACK,
            purpose = "Indicador de carga.",
            description = "Box centrado con CircularProgressIndicator de color de marca y padding Xxl.",
            props = listOf(LabProp("modifier", "Modifier")),
            states = listOf(
                LabState("Carga indeterminada", "Spinner centrado sobre ancho completo.", LabReviewStatus.VERIFIED),
                LabState(
                    "Carga determinada",
                    "La API no expone progreso ni mensaje; no se puede representar avance.",
                    LabReviewStatus.NOT_SUPPORTED,
                ),
                LabState(
                    "Skeleton loading",
                    "Para contenido en carga preferir XauxaSkeleton; este spinner es espera genérica.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            tokens = listOf(LabTokenRef("XauxaColor.Brand", "Color del indicador.")),
            usage = listOf("Carga de destinos, comprobantes o sincronización en curso."),
            tags = listOf("feedback", "carga", "espera"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Espera genérica. Contenido esqueleto con XauxaSkeleton; pie de lista con XauxaLoadMoreFooter.",
            androidMapping = "Material3 CircularProgressIndicator centrado.",
            iosMapping = "SwiftUI ProgressView.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-skeleton",
            name = "XauxaSkeleton",
            category = LabCategory.FEEDBACK,
            purpose = "Esqueleto de carga estático sin movimiento decorativo.",
            description = "Bloques Surface2 (título + líneas) sin animación continua, conforme a la " +
                "invariante de movimiento con propósito.",
            props = listOf(
                LabProp("modifier", "Modifier"),
                LabProp("lines", "Int", "3"),
            ),
            states = listOf(
                LabState("Default", "Título y líneas de marcador.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface2", "Color de los bloques."),
                LabTokenRef("XauxaSpacing.Lg", "Padding del esqueleto."),
            ),
            usage = listOf("Listas y detalles mientras cargan (contenido con forma conocida)."),
            tags = listOf("carga", "esqueleto", "feedback"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Carga de contenido con forma conocida. Espera genérica con XauxaLoading.",
            androidMapping = "Column de Box Surface2 sin animación.",
            iosMapping = "SwiftUI VStack de Rectangle sin animación (.redacted puede evaluarse).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-empty-state",
            name = "XauxaEmptyState",
            category = LabCategory.FEEDBACK,
            purpose = "Estado sin elementos con una acción de recuperación.",
            description = "Columna centrada con título Title semibold expuesto como encabezado semántico y botón " +
                "primario, separados por XauxaSpacing.Lg y con padding Huge.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("actionLabel", "String"),
                LabProp("onAction", "() -> Unit"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState("Default", "Título centrado expuesto como encabezado semántico más botón primario de acción.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onAction", "Activación de la acción de recuperación.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.TextPrimary", "Color del título."),
                LabTokenRef("XauxaType.Title", "Tamaño del título."),
                LabTokenRef("XauxaSpacing.Lg", "Separación título-acción."),
                LabTokenRef("XauxaSpacing.Huge", "Padding del estado."),
            ),
            usage = listOf("Sin destinos, sin comprobantes, búsqueda sin resultados."),
            notes = listOf(
                "El centrado está reservado para estados de página completa, según Xauxa tipografía.",
            ),
            tags = listOf("feedback", "vacío", "estado"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Primera vez o sin datos con salida clara. Fallo total con XauxaErrorPage.",
            androidMapping = "Column centrada + XauxaPrimaryButton.",
            iosMapping = "SwiftUI VStack centrado + botón prominente.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-error-page",
            name = "XauxaErrorPage",
            category = LabCategory.FEEDBACK,
            purpose = "Página de error a pantalla completa.",
            description = "Estado centrado con título Danger, mensaje, acción primaria y secundaria " +
                "opcional. Patrón error-page de Xauxa sin sombras.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("message", "String"),
                LabProp("actionLabel", "String"),
                LabProp("onAction", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("secondaryLabel", "String?", "null"),
                LabProp("onSecondary", "(() -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Título, mensaje y acción de recuperación.", LabReviewStatus.VERIFIED),
                LabState("Con secundaria", "Segunda salida textual opcional.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("onAction", "Acción de recuperación."),
                LabEvent("onSecondary", "Salida secundaria."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Danger", "Color del título."),
                LabTokenRef("XauxaColor.TextSecondary", "Color del mensaje."),
                LabTokenRef("XauxaType.Headline", "Tamaño del título."),
                LabTokenRef("XauxaSpacing.Huge", "Padding de página."),
            ),
            usage = listOf("Fallo de aplicación o de carga total con reintento."),
            tags = listOf("error", "página", "feedback", "reintento"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Fallo de aplicación/página. Fallo puntual con XauxaToast; vacío con XauxaEmptyState.",
            androidMapping = "Column centrada a pantalla completa + botones.",
            iosMapping = "SwiftUI VStack centrado + botones.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-load-more",
            name = "XauxaLoadMoreFooter",
            category = LabCategory.FEEDBACK,
            purpose = "Footer de Cargar más; la paginación se compone de él.",
            description = "Botón secundario, estado de carga o mensaje de fin. No existe un " +
                "componente de paginación duplicado: la paginación es este footer.",
            props = listOf(
                LabProp("modifier", "Modifier"),
                LabProp("isLoading", "Boolean", "false"),
                LabProp("endReached", "Boolean", "false"),
                LabProp("onLoadMore", "(() -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Botón Cargar más cuando onLoadMore no es nulo.", LabReviewStatus.VERIFIED),
                LabState("Carga", "isLoading = true: espera con etiqueta.", LabReviewStatus.VERIFIED),
                LabState("Final", "endReached = true: mensaje de fin de lista.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onLoadMore", "Solicitud de la siguiente página.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Indicador de carga."),
                LabTokenRef("XauxaColor.TextSecondary", "Etiquetas."),
                LabTokenRef("XauxaSpacing.Lg", "Padding del footer."),
            ),
            usage = listOf("Historial, resultados y listas paginadas (patrón tile-footer de Xauxa)."),
            tags = listOf("paginación", "cargar", "lista", "footer"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Listas paginadas. Carga inicial de página con XauxaLoading o XauxaSkeleton.",
            androidMapping = "Box centrado + XauxaSecondaryButton o espera.",
            iosMapping = "SwiftUI HStack centrado + Button o ProgressView.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-list-row",
            name = "XauxaListRow",
            category = LabCategory.DATA,
            purpose = "Fila de datos abierta (sin borde de reposo, V1.1 M9) con marcador semántico.",
            description = "Fila con marcador lateral de tono de 2px, título, subtítulo opcional y " +
                "slot trailing. La separación entre filas es el borde de 1px.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("subtitle", "String?", "null"),
                LabProp("tone", "XauxaTone", "Neutral"),
                LabProp("onClick", "(() -> Unit)?", "null"),
                LabProp("trailing", "(@Composable () -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Fila de presentación.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Con onClick: rol Button y anillo de foco.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Activación de la fila; solo se emite cuando onClick != null.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.TextPrimary", "Título."),
                LabTokenRef("XauxaColor.TextSecondary", "Subtítulo."),
                LabTokenRef("XauxaColor.Border", "Separador."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima."),
            ),
            usage = listOf("Historial, resultados y movimientos (patrón history-item de Xauxa)."),
            tags = listOf("fila", "lista", "datos"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Filas homogéneas de lista. Resultado con acciones en contexto con XauxaInlineResult.",
            androidMapping = "Row clickable con marcador lateral y borde.",
            iosMapping = "SwiftUI HStack con marcador y .border inferior.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-stat-block",
            name = "XauxaStatBlock",
            category = LabCategory.DATA,
            purpose = "Bloque de estadística centrado: valor + etiqueta.",
            description = "Valor en Headline con tono opcional y etiqueta Caption, centrados " +
                "(patrón stats de Xauxa).",
            props = listOf(
                LabProp("value", "String"),
                LabProp("label", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("tone", "XauxaTone", "Neutral"),
            ),
            states = listOf(
                LabState("Default", "Valor neutro con etiqueta.", LabReviewStatus.VERIFIED),
                LabState("Tono semántico", "Valor con color de tono.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.TextPrimary", "Valor neutro."),
                LabTokenRef("XauxaColor.TextSecondary", "Etiqueta."),
                LabTokenRef("XauxaType.Headline", "Tamaño del valor."),
                LabTokenRef("XauxaType.FamilyMono", "Valor monoespaciado."),
                LabTokenRef("XauxaType.Caption", "Tamaño de la etiqueta."),
            ),
            usage = listOf("Totales de historial, FPS/escaneos del viewport y métricas de dashboard."),
            tags = listOf("estadística", "métrica", "datos"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Métricas aisladas. Número hero con contexto en XauxaHeroCard.",
            androidMapping = "Column centrada + Text Headline/Caption.",
            iosMapping = "SwiftUI VStack centrado + Text.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-badge",
            name = "XauxaBadge",
            category = LabCategory.DATA,
            purpose = "Etiqueta semántica outline o sólida.",
            description = "Badge sin caja de reposo (V1.1 M9); solid usa bloque de color de tono. Sin " +
                "radios: Xauxa usa rounded-sm y aquí se fija a 0 por invariante.",
            props = listOf(
                LabProp("text", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("tone", "XauxaTone", "Neutral"),
                LabProp("solid", "Boolean", "false"),
            ),
            states = listOf(
                LabState("Default", "Presentación outline: borde y texto de tono sobre Surface.", LabReviewStatus.VERIFIED),
                LabState("Sólido", "solid = true: fondo de tono.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Border", "Borde neutro."),
                LabTokenRef("XauxaType.Caption", "Tamaño del texto."),
                LabTokenRef("XauxaType.LetterSpacingWide", "Tracking del texto en mayúsculas."),
                LabTokenRef("XauxaSpacing.Sm", "Padding horizontal."),
            ),
            usage = listOf("Estados de tile, tipos de QR y conteos (patrón tile-status-badge de Xauxa)."),
            tags = listOf("badge", "etiqueta", "estado"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Etiquetas de estado y tipo. Clasificación estática con XauxaCategoryChip; filtros con XauxaFilterChip.",
            androidMapping = "Box con borde de tono y fondo condicional.",
            iosMapping = "SwiftUI Text con .border y .background condicionales.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-category-chip",
            name = "XauxaCategoryChip",
            category = LabCategory.DATA,
            purpose = "Chip de categoría estático: clasifica, no filtra.",
            description = "Etiqueta Surface2 no interactiva. Distinta de XauxaFilterChip por " +
                "diseño: sin selected, sin onClick, sin eventos.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState("Default", "Etiqueta de categoría.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface2", "Fondo."),
                LabTokenRef("XauxaColor.TextSecondary", "Texto."),
                LabTokenRef("XauxaType.Caption", "Tamaño del texto."),
            ),
            usage = listOf("Categorías de destino y tipos de comprobante en lectura."),
            notes = listOf("Si filtra contenido es un filtro: usar XauxaFilterChip."),
            tags = listOf("chip", "categoría", "etiqueta"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Clasificación en lectura. Interacción con XauxaFilterChip; estado con XauxaBadge.",
            androidMapping = "Box Surface2 no interactivo.",
            iosMapping = "SwiftUI Text con fondo Surface2.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-favorite-indicator",
            name = "XauxaFavoriteIndicator",
            category = LabCategory.DATA,
            purpose = "Indicador visual de favorito.",
            description = "Cuadrado (radio 0, Metro) de XauxaMetrics.FavoriteIndicatorSize con fondo Brand " +
                "(marcado) o Surface2 (sin marcar). Fase 2 (auditoría a11y): glifo Favorito solo cuando está " +
                "marcado — el estado no depende solo del color (§11); contentDescription opcional para exponer " +
                "su significado cuando el llamador lo necesita.",
            props = listOf(
                LabProp("favorite", "Boolean"),
                LabProp("modifier", "Modifier"),
                LabProp("contentDescription", "String?", "null"),
            ),
            states = listOf(
                LabState("Marcado", "favorito = true: cuadrado de marca con glifo Favorito en OnBrand.", LabReviewStatus.VERIFIED),
                LabState("Sin marcar", "favorito = false: cuadrado Surface2 vacío (sin glifo).", LabReviewStatus.VERIFIED),
                LabState(
                    "Interactivo (toggle)",
                    "Para interacción usar XauxaFavoriteToggle; este indicador es solo presentación.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Fondo del estado marcado."),
                LabTokenRef("XauxaColor.Surface2", "Fondo del estado sin marcar."),
                LabTokenRef("XauxaMetrics.FavoriteIndicatorSize", "Lado del indicador cuadrado."),
                LabTokenRef("XauxaMetrics.IconSize", "Tamaño del glifo."),
            ),
            usage = listOf("Marcar visualmente un destino u obligación favorita en listas y detalles."),
            notes = listOf(
                "Fase 2: la excepción histórica del círculo (CircleShape) se retiró — radio 0 en TODO, " +
                    "incluidos los indicadores (el catálogo anterior documentaba el círculo como permitido).",
            ),
            tags = listOf("favorito", "indicador", "datos"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Solo presentación. Interacción con XauxaFavoriteToggle.",
            androidMapping = "Box circular con fondo condicional.",
            iosMapping = "SwiftUI Circle con fill condicional.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-qr-preview",
            name = "XauxaQrPreview",
            category = LabCategory.QR,
            purpose = "Preview del QR almacenado de un destino.",
            description = "Contrato expect/actual: Android decodifica un PNG en base64 y lo renderiza; con " +
                "dato inválido o vacío muestra placeholder blanco. iOS difiere el decodificado y muestra " +
                "placeholder hasta validación de hardware. El host web (Wasm) muestra placeholder hasta que " +
                "exista una ruta de decodificación en navegador.",
            props = listOf(
                LabProp("encodedQr", "String"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState(
                    "QR renderizado",
                    "Android decodifica el base64 a bitmap y lo muestra a XauxaMetrics.QrPreviewSize.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Placeholder",
                    "Dato inválido o vacío: caja blanca con borde. Comportamiento verificado en Android.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Render en iOS",
                    "La implementación iOS aún no decodifica: siempre muestra placeholder hasta validar en " +
                    "hardware.",
                    LabReviewStatus.PENDING,
                ),
                LabState(
                    "Render en web (Wasm)",
                    "El host Wasm del laboratorio aún no decodifica: siempre muestra placeholder blanco hasta " +
                    "implementar la ruta de decodificación en navegador.",
                    LabReviewStatus.PENDING,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.White", "Fondo del placeholder."),
                LabTokenRef("XauxaColor.Border", "Borde del contenedor."),
                LabTokenRef("XauxaMetrics.QrPreviewSize", "Tamaño del preview."),
                LabTokenRef("XauxaMetrics.Border", "Grosor del borde."),
            ),
            usage = listOf(
                "Confirmar el QR almacenado antes de compartir o reutilizar; detalle de destino.",
            ),
            notes = listOf(
                "La inspección visual de este contrato es por plataforma: el laboratorio muestra la " +
                    "implementación real del target donde corre.",
            ),
            tags = listOf("qr", "comprobante", "preview"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "QR ya almacenado. Captura nueva con XauxaScannerViewport; archivo con XauxaFileUpload.",
            androidMapping = "expect/actual: Image bitmap decodificado de base64 (Android real).",
            iosMapping = "expect/actual: placeholder hasta validar decodificación en hardware.",
            platforms = listOf(
                LabPlatformStatus("Android", true, "Decodifica base64 a bitmap y renderiza."),
                LabPlatformStatus("iOS", false, "Placeholder hasta validación en hardware."),
                LabPlatformStatus("Web (Wasm)", false, "Placeholder hasta ruta de decodificación en navegador."),
            ),
        ),
        LabComponentContract(
            id = "xauxa-scanner-viewport",
            name = "XauxaScannerViewport",
            category = LabCategory.QR,
            purpose = "Viewport de cámara: encuadre preview y estado de escaneo.",
            description = "Superficie con marco de encuadre de marca de 2px y texto de ayuda. El " +
                "encuadre es preview real; el escaneo real requiere cámara por plataforma.",
            props = listOf(
                LabProp("modifier", "Modifier"),
                LabProp("scanning", "Boolean", "false"),
                LabProp("hint", "String", "\"Encuadre el código QR\""),
            ),
            states = listOf(
                LabState("Default", "Encuadre con ayuda.", LabReviewStatus.VERIFIED),
                LabState("Escaneando", "scanning = true: etiqueta de escaneo en curso.", LabReviewStatus.VERIFIED),
                LabState(
                    "Escaneo real",
                    "Android: INTEGRADO (T8: CameraX + análisis ZXing continuo; runtime de sesión OPENED " +
                        "con primer frame verificado en el AVD; la detección de un QR real queda pendiente " +
                        "de hardware). iOS: implementado con AVFoundation (U2, runtime BLOCKED sin Xcode). " +
                        "Web: getUserMedia pendiente.",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface2", "Fondo del viewport."),
                LabTokenRef("XauxaColor.Brand", "Marco de encuadre."),
                LabTokenRef("XauxaMetrics.BorderStrong", "Grosor del marco (2px)."),
                LabTokenRef("XauxaColor.TextSecondary", "Texto de ayuda."),
                LabTokenRef("XauxaMetrics.QrPreviewSize", "Tamaño del encuadre."),
            ),
            usage = listOf("Pantalla de escaneo de AgendaQr (patrón scanner-viewport de Xauxa)."),
            notes = listOf("Preview y escaneo real se declaran por separado, como exige el contrato."),
            tags = listOf("escáner", "cámara", "qr", "viewport"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Captura con cámara. QR almacenado con XauxaQrPreview; archivo con XauxaFileUpload.",
            androidMapping = "Preview + escaneo real con CameraX (T8); detección de QR real pendiente de hardware.",
            iosMapping = "AVFoundation implementado (U2); runtime pendiente de Xcode.",
            platforms = listOf(
                LabPlatformStatus("Android", true, "Preview + escaneo CameraX integrados (T8)."),
                LabPlatformStatus("iOS", false, "AVFoundation implementado (U2); runtime pendiente de Xcode."),
                LabPlatformStatus("Web (Wasm)", false, "Preview real; getUserMedia pendiente."),
            ),
        ),
        LabComponentContract(
            id = "xauxa-file-upload",
            name = "XauxaFileUpload",
            category = LabCategory.QR,
            purpose = "Zona de carga de archivos con estados.",
            description = "Dropzone presentacional dirigida por props: reposo, archivo seleccionado, " +
                "carga y error. La selección real depende de la plataforma.",
            props = listOf(
                LabProp("modifier", "Modifier"),
                LabProp("fileName", "String?", "null"),
                LabProp("isLoading", "Boolean", "false"),
                LabProp("error", "String?", "null"),
                LabProp("onSelect", "(() -> Unit)?", "null"),
                LabProp("onClear", "(() -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Reposo con ayuda de formatos.", LabReviewStatus.VERIFIED),
                LabState("Con archivo", "fileName no nulo con acción Quitar.", LabReviewStatus.VERIFIED),
                LabState("Carga", "isLoading = true: espera.", LabReviewStatus.VERIFIED),
                LabState("Error", "error no nulo: mensaje Danger textual.", LabReviewStatus.VERIFIED),
                LabState(
                    "Selección real",
                    "Picker nativo por plataforma: pendiente de integrar.",
                    LabReviewStatus.PENDING,
                ),
            ),
            events = listOf(
                LabEvent("onSelect", "Solicitud de selección de archivo."),
                LabEvent("onClear", "Quitar el archivo seleccionado."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface", "Fondo de la zona."),
                LabTokenRef("XauxaColor.Border", "Borde de la zona."),
                LabTokenRef("XauxaColor.Danger", "Mensaje de error."),
                LabTokenRef("XauxaType.Caption", "Ayuda y error."),
            ),
            usage = listOf("Importar comprobantes desde archivo (patrón upload-zone de Xauxa)."),
            tags = listOf("archivo", "carga", "importación", "qr"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Importación desde archivo. Cámara con XauxaScannerViewport; pegado manual con XauxaTextInput.",
            androidMapping = "Zona presentacional; ActivityResult pendiente.",
            iosMapping = "Zona presentacional; PhotosPicker/FileImporter pendiente.",
            platforms = listOf(
                LabPlatformStatus("Android", false, "Presentacional; picker pendiente."),
                LabPlatformStatus("iOS", false, "Presentacional; picker pendiente."),
                LabPlatformStatus("Web (Wasm)", false, "Presentacional; File API pendiente."),
            ),
        ),
    )

    /** Full catalog: foundations first, then components in declaration order. */
    val all: List<LabComponentContract> = foundations + components

    fun find(id: String): LabComponentContract? = all.firstOrNull { it.id == id }
}

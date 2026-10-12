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
        LabPlatformStatus("Web (Wasm)", false, "Pendiente de revisión de renderizado en navegador ."),
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
                "de contenido y tamaño de preview de QR.",
            states = listOf(
                LabState(
                    "Métricas de control y foco",
                    "Border, BorderStrong, Focus y ControlMinSize fijan bordes funcionales y el target 48dp.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Breakpoints históricos",
                    "BreakpointCompact y BreakpointMedium existen en tokens y el laboratorio aún los consume " +
                    "para su layout adaptable; la producción usa WindowSizeClass.",
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
            purpose = "Escala tipográfica de Xauxa para títulos, cuerpo y etiquetas.",
            description = "Tamaños en sp mapeados a los roles de Material 3 (titleLarge, headlineMedium, " +
                "bodyLarge, labelLarge, …) con la familia de sistema en todos los estilos.",
            states = listOf(
                LabState(
                    "Escala tipográfica",
                    "Los tamaños están declarados, en sp, y se consumen en componentes.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Familias tipográficas",
                    "Familia del sistema en todos los estilos (FamilyUi: Roboto en Android, " +
                        "San Francisco en iOS) y monoespaciada para valores (FamilyMono). " +
                        "Sin fuentes empaquetadas (ADR-0012).",
                    LabReviewStatus.VERIFIED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaType.TitleLarge", "Tamaño del título de página (22 sp)."),
                LabTokenRef("XauxaType.FamilyMono", "Valores numéricos monoespaciados."),
            ),
            usage = listOf("Jerarquía textual de pantallas, secciones, etiquetas y metadatos."),
            tags = listOf("tipografía", "tokens"),
            darkThemeSupport = LabDarkThemeSupport.NOT_APPLICABLE,
            darkThemeNote = "Token no cromático: el tema no aplica.",
            whenToUse = "Todo tamaño de texto. Los roles M3 se consumen vía XauxaTextStyles/XauxaMaterialTypography.",
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
                LabTokenRef("XauxaTracking.LabelLarge", "Tracking de etiqueta de M3."),
                LabTokenRef("XauxaSpacing.Md", "Separación entre título y contenido."),
            ),
            usage = listOf(
                "Agrupar filtros, obligaciones, comprobantes o metadatos en listas y detalles.",
            ),
            tags = listOf("sección", "encabezado"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Para agrupar contenido con título. Sin título preferir XauxaTile directamente.",
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
                "Bloques de contenido y entidades.",
            ),
            tags = listOf("superficie", "tarjeta", "tile"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Bloques de contenido y entidades.",
            androidMapping = "Material3 Surface rectangular con clickable + focusable.",
            iosMapping = "SwiftUI RoundedRectangle radius 0 + .border + .onTapGesture + .focusable().",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-page-title",
            name = "XauxaPageTitle",
            category = LabCategory.SURFACES,
            purpose = "Título de página con el rol titleLarge de Material 3.",
            description = "Rol titleLarge de M3 (22 sp) con la familia de sistema, capitalización de " +
                "oración y semántica heading(). V1.2 (ADR-0012, spec §12 Tipografía).",
            props = listOf(
                LabProp("text", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("color", "Color", "XauxaColor.TextPrimary"),
            ),
            states = listOf(
                LabState("Default", "Título de página estándar.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaType.TitleLarge", "Tamaño del título de página (22 sp)."),
                LabTokenRef("XauxaColor.TextPrimary", "Color del título."),
            ),
            usage = listOf("Titular de pantallas sin barra superior (Auth) y transición al chrome V1.2."),
            tags = listOf("título", "tipografía"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; validación visual de oscuro pendiente en dispositivo.",
            whenToUse = "Titular de pantalla. En superficies con barra superior, el título vive en la barra.",
            androidMapping = "Material3 Text con MaterialTheme.typography.titleLarge.",
            iosMapping = "Mismo estilo compartido; familia de sistema (San Francisco).",
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
            whenToUse = "Confirmaciones destructivas. Para avisos no bloqueantes preferir el Snackbar del sistema.",
            androidMapping = "Material3 AlertDialog con RectangleShape.",
            iosMapping = "SwiftUI .confirmationDialog o .alert con dos botones.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-pivot",
            name = "XauxaPivot",
            category = LabCategory.SURFACES,
            purpose = "Secciones con pestañas estándar (S06): QR / Actividades / Comprobantes.",
            description = "PrimaryScrollableTabRow de M3: pestañas desplazables con la activa en acento " +
                "(de contexto si es legible); la conmutación es local y no crea rutas ni navegación " +
                "nueva (spec §3). Cada pestaña declara rol Tab con estado selected y etiqueta de " +
                "texto visible (§11).",
            props = listOf(
                LabProp("sections", "List<String>"),
                LabProp("selectedIndex", "Int"),
                LabProp("onSelect", "(Int) -> Unit"),
                LabProp("accent", "Color?"),
                LabProp("content", "@Composable (Int) -> Unit"),
            ),
            states = listOf(
                LabState("Default", "Primera sección activa.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Cambio de sección con rol Tab y foco visible.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onSelect", "Selección de sección; el caller conserva la pantalla actual.")),
            tokens = listOf(
                LabTokenRef("XauxaType.SectionHeader", "Tamaño de etiqueta de sección."),
                LabTokenRef("XauxaColor.Brand", "Acento por defecto de la sección activa."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Objetivo táctil de cada pestaña."),
            ),
            usage = listOf("S06 Contexto: secciones QR, Actividades y Comprobantes (modelo mental §2)."),
            tags = listOf("pivot", "secciones"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; validación visual en dispositivo.",
            whenToUse = "S06. No es una navegación nueva: dentro de una misma pantalla.",
            androidMapping = "PrimaryScrollableTabRow + Tab de Material3.",
            iosMapping = "Mismo componente compartido (Material3); SwiftUI Picker segmentado como referencia.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-top-bar",
            name = "XauxaTopBar",
            category = LabCategory.ACTIONS,
            purpose = "Barra superior estándar: TopAppBar pequeña con título a la izquierda.",
            description = "Flecha atrás cuando hay a dónde volver (la misma en ambas plataformas), " +
                "máximo 2 acciones visibles y menú \"…\" para el resto (§12 V1.2). Las acciones sin " +
                "glifo aprobado se pintan como acción de texto; las descripciones accesibles son " +
                "siempre la etiqueta (§11).",
            props = listOf(
                LabProp("title", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("onBack", "(() -> Unit)?", "null"),
                LabProp("backLabel", "String"),
                LabProp("actions", "List<XauxaTopBarAction>", "máximo 2 visibles"),
                LabProp("overflowActions", "List<XauxaOverflowAction>", "emptyList()"),
                LabProp("overflowLabel", "String"),
            ),
            states = listOf(
                LabState("Default", "Título titleLarge y flecha atrás cuando onBack != null.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Acciones, overflow y atrás con ripple estándar.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("action.onClick", "Ejecuta la acción etiquetada."),
                LabEvent("onBack", "Atrás — misma flecha en Android e iOS."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.ControlMinSize", "Objetivo táctil de cada acción."),
                LabTokenRef("XauxaColor.BorderControl", "Límite del overlay del menú (E-01)."),
            ),
            usage = listOf("Barra superior de todas las superficies de la V1.2."),
            notes = listOf(
                "El botón … expone XauxaOverflowAction con la misma semántica de menú.",
                "Las superficies raíz no llevan flecha atrás: la navegación inferior cubre el regreso.",
            ),
            tags = listOf("topbar", "navegación", "acciones"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Colores del esquema vía MaterialTheme; validación visual en dispositivo.",
            whenToUse = "Barra superior de cada pantalla. Navegación raíz con XauxaNavBar.",
            androidMapping = "Material3 TopAppBar con IconButton/DropdownMenu.",
            iosMapping = "Mismo componente compartido (Material3).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-nav-bar",
            name = "XauxaNavBar",
            category = LabCategory.ACTIONS,
            purpose = "Navegación raíz: NavigationBar con 4 destinos etiquetados.",
            description = "Cada destino lleva etiqueta + icono SIEMPRE (nunca solo icono, §12 V1.2). " +
                "Solo aparece en las superficies raíz cuando están en su lista raíz; en ancho " +
                "expandido el scaffold la sustituye por XauxaNavRail. Cambiar de destino conserva " +
                "la raíz del back stack.",
            props = listOf(
                LabProp("destinations", "List<XauxaNavDestination>"),
                LabProp("selectedId", "String"),
                LabProp("onSelect", "(String) -> Unit"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState("Default", "Destino seleccionado con etiqueta e icono.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Cambio de destino con ripple estándar.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onSelect", "Selección de destino raíz (popToRoot + push).")),
            tokens = listOf(
                LabTokenRef("XauxaMetrics.ControlMinSize", "Objetivo táctil de cada destino."),
                LabTokenRef("XauxaColor.OnBrand", "Icono del destino activo."),
                LabTokenRef("XauxaColor.TextSecondary", "Icono de destinos inactivos."),
            ),
            usage = listOf("Superficies raíz: Inicio · Buscar · Actividades · Contextos."),
            notes = listOf("La etiqueta es visible siempre; el significado nunca depende solo del icono."),
            tags = listOf("navbar", "navegación", "raíz"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Colores del esquema vía MaterialTheme; validación visual en dispositivo.",
            whenToUse = "Solo superficies raíz en su lista raíz. En expandido, XauxaNavRail.",
            androidMapping = "Material3 NavigationBar/NavigationRail.",
            iosMapping = "Mismo componente compartido (Material3).",
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
                LabTokenRef("XauxaTracking.LabelLarge", "Tracking de etiqueta de M3."),
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
                LabTokenRef("XauxaTracking.LabelLarge", "Tracking de etiqueta de M3."),
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
                LabTokenRef("XauxaColor.OnDanger", "Contenido sobre peligro."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
                LabTokenRef("XauxaTracking.LabelLarge", "Tracking de etiqueta de M3."),
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
                LabTokenRef("XauxaTracking.LabelLarge", "Tracking de etiqueta de M3."),
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
            description = "V1.1 (ADR-0005, spec §12): glifos de línea de un solo color, tamaño y " +
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
                LabTokenRef("XauxaTracking.LabelLarge", "Tracking de etiqueta de M3."),
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
            purpose = "Entrada de texto etiquetada con estado de error (§12 V1.2).",
            description = "OutlinedTextField con etiqueta flotante, texto de apoyo y error contextual " +
                "con icono + texto. El borde en reposo cumple >= 3:1 (borderControl). Con " +
                "singleLine = false y minLines funciona como textarea.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("value", "String"),
                LabProp("onValueChange", "(String) -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
                LabProp("isError", "Boolean", "false"),
                LabProp("errorMessage", "String?", "null"),
                LabProp("helperMessage", "String?", "null"),
                LabProp("singleLine", "Boolean", "true"),
                LabProp("minLines", "Int", "1"),
                LabProp("keyboardOptions", "KeyboardOptions"),
                LabProp("trailing", "(@Composable () -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Entrada vacía o con contenido.", LabReviewStatus.VERIFIED),
                LabState("Error", "isError = true: borde Danger, icono + mensaje textual.", LabReviewStatus.VERIFIED),
                LabState("Deshabilitado", "enabled = false bloquea la edición.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onValueChange", "Emitido por cada cambio aceptado.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Borde enfocado."),
                LabTokenRef("XauxaColor.BorderControl", "Borde en reposo (>= 3:1)."),
                LabTokenRef("XauxaColor.Danger", "Borde y mensaje de error."),
                LabTokenRef("XauxaColor.TextPrimary", "Texto."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
            ),
            usage = listOf("Credenciales de Auth, nombre de destino, notas, montos y descripciones."),
            notes = listOf(
                "El error nunca es solo color: icono + mensaje textual obligatorios.",
                "La obligatoriedad la expresa el llamador con copy (texto de apoyo o error), sin asterisco.",
            ),
            tags = listOf("entrada", "texto", "formulario", "error"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Captura de texto en formularios. Búsqueda global con XauxaSearchBar.",
            androidMapping = "Material3 OutlinedTextField.",
            iosMapping = "Mismo componente compartido (Material3).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-search-bar",
            name = "XauxaSearchBar",
            category = LabCategory.ACTIONS,
            purpose = "Búsqueda textual de una línea con limpieza opcional (§12 V1.2).",
            description = "OutlinedTextField de una línea con etiqueta, placeholder, glifo de búsqueda " +
                "y botón de limpiar con nombre accesible cuando hay texto y onClear no es nulo.",
            props = listOf(
                LabProp("value", "String"),
                LabProp("onValueChange", "(String) -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("label", "String"),
                LabProp("placeholder", "String"),
                LabProp("onClear", "(() -> Unit)?", "null"),
                LabProp("clearLabel", "String", "\"\""),
            ),
            states = listOf(
                LabState("Default", "Campo vacío con placeholder.", LabReviewStatus.VERIFIED),
                LabState("Con texto", "Muestra el botón de limpiar cuando onClear no es nulo.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(
                LabEvent("onValueChange", "Emitido por cada cambio aceptado."),
                LabEvent("onClear", "Limpieza del texto; solo disponible con texto no vacío."),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Borde enfocado."),
                LabTokenRef("XauxaColor.BorderControl", "Borde en reposo (>= 3:1)."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
            ),
            usage = listOf("Búsqueda global de destinos y filtrado del catálogo del laboratorio."),
            tags = listOf("búsqueda", "entrada", "filtro"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Búsquedas. Formularios de captura con XauxaTextInput.",
            androidMapping = "Material3 OutlinedTextField singleLine con trailingIcon.",
            iosMapping = "Mismo componente compartido (Material3).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-favorite-toggle",
            name = "XauxaFavoriteToggle",
            category = LabCategory.ACTIONS,
            purpose = "Conmutador de favorito por fila (§12 V1.2).",
            description = "IconToggleButton de M3 con la estrella de Lucide: 48dp, estado seleccionado " +
                "en la semántica y descripción accesible del llamador que cambia con el estado (§11).",
            props = listOf(
                LabProp("favorite", "Boolean"),
                LabProp("onCheckedChange", "(Boolean) -> Unit"),
                LabProp("contentDescription", "String"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState("Marcado", "favorite = true: estrella en acento.", LabReviewStatus.VERIFIED),
                LabState("Sin marcar", "favorite = false: estrella en texto terciario.", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onCheckedChange", "Cambio del estado de favorito.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Estrella marcada."),
                LabTokenRef("XauxaColor.TextTertiary", "Estrella sin marcar."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Área táctil de 48dp."),
            ),
            usage = listOf("Favorito de cada fila de Inicio (S01)."),
            notes = listOf("La descripción accesible cambia con el estado; el color solo acompaña (§11)."),
            tags = listOf("favorito", "toggle", "acciones-de-fila"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Acción de favorito en filas de lista.",
            androidMapping = "Material3 IconToggleButton con Icon.",
            iosMapping = "Mismo componente compartido (Material3).",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-status-banner",
            name = "XauxaStatusBanner",
            category = LabCategory.FEEDBACK,
            purpose = "Mensaje persistente de estado.",
            description = "Bloque tonal de ancho completo con icono por tono y liveRegion cortés; " +
                "acciones opcionales (acción + descarte) con copy del llamador. Vive dentro del " +
                "Scaffold; los insets los resuelve el contenedor.",
            props = listOf(
                LabProp("message", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("tone", "XauxaTone?", "Neutral"),
                LabProp("actionLabel", "String?", "null"),
                LabProp("onAction", "(() -> Unit)?", "null"),
                LabProp("onDismiss", "(() -> Unit)?", "null"),
                LabProp("dismissLabel", "String?", "null"),
            ),
            states = listOf(
                LabState("Neutro", "Mensaje informativo sobre Surface2.", LabReviewStatus.VERIFIED),
                LabState("Peligro", "tone = Danger: fondo DangerBg y texto Danger.", LabReviewStatus.VERIFIED),
                LabState("Éxito/Información/Advertencia", "Pares content/container de cada tono.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Danger", "Semántica de peligro."),
                LabTokenRef("XauxaColor.DangerBg", "Fondo de peligro."),
                LabTokenRef("XauxaColor.Surface2", "Fondo neutro."),
                LabTokenRef("XauxaColor.TextSecondary", "Texto neutro."),
                LabTokenRef("XauxaSpacing.Lg", "Padding del banner."),
                LabTokenRef("XauxaType.Label", "Tamaño del texto."),
            ),
            usage = listOf(
                "Estado de sincronización, comprobante pendiente de revisión, fallo de guardado.",
            ),
            notes = listOf("El estado no debe comunicarse solo con color: icono + texto obligatorios."),
            tags = listOf("feedback", "banner", "estado"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Estado persistente en contexto. Feedback transitorio con el Snackbar del sistema.",
            androidMapping = "Box tonal con liveRegion.",
            iosMapping = "Mismo componente compartido.",
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
                LabState("Carga indeterminada", "Indicador centrado sobre ancho completo.", LabReviewStatus.VERIFIED),
                LabState(
                    "Con mensaje",
                    "message expone un texto de acompañamiento (copy del llamador).",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Carga determinada",
                    "La API no expone progreso; no se puede representar avance.",
                    LabReviewStatus.NOT_SUPPORTED,
                ),
                LabState(
                    "Skeleton loading",
                    "Para contenido en carga preferir XauxaSkeleton; este indicador es espera genérica.",
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
            whenToUse = "Primera vez o sin datos con salida clara.",
            androidMapping = "Column centrada + XauxaPrimaryButton.",
            iosMapping = "SwiftUI VStack centrado + botón prominente.",
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
            purpose = "Fila de datos con ListItem de M3 (§12 V1.2).",
            description = "Fila estándar: título, subtítulo, metadato en línea propia, acción final " +
                "y punto de acento opcional como identidad de contexto (E-05). La altura crece con " +
                "el contenido y la escala de fuente.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("subtitle", "String?", "null"),
                LabProp("onClick", "(() -> Unit)?", "null"),
                LabProp("trailing", "(@Composable () -> Unit)?", "null"),
                LabProp("accent", "Color?", "null"),
                LabProp("selected", "Boolean", "false"),
                LabProp("meta", "(@Composable () -> Unit)?", "null"),
            ),
            states = listOf(
                LabState("Default", "Fila de presentación.", LabReviewStatus.VERIFIED),
                LabState("Interactivo", "Con onClick: rol Button con ripple estándar.", LabReviewStatus.VERIFIED),
                LabState("Seleccionado", "selected expone la semántica de selección (§11).", LabReviewStatus.VERIFIED),
            ),
            events = listOf(LabEvent("onClick", "Activación de la fila; solo se emite cuando onClick != null.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.TextPrimary", "Título."),
                LabTokenRef("XauxaColor.TextSecondary", "Subtítulo."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima."),
                LabTokenRef("XauxaSpacing.Md", "Punto de identidad de contexto (E-05)."),
            ),
            usage = listOf("Recientes de Inicio, resultados de búsqueda, filas de contexto."),
            tags = listOf("fila", "lista", "datos"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Filas homogéneas de lista. Acciones de fila como botones de icono en trailing.",
            androidMapping = "ListItem de Material3 con clickable.",
            iosMapping = "Mismo componente compartido (Material3); SwiftUI List como referencia.",
            platforms = commonPlatforms(),
        ),
        LabComponentContract(
            id = "xauxa-badge",
            name = "XauxaBadge",
            category = LabCategory.DATA,
            purpose = "Etiqueta semántica de estado.",
            description = "Texto con el color del tono sobre su contenedor (par garantizado); no " +
                "interactiva. El estado se expresa con texto, nunca solo con color (§11).",
            props = listOf(
                LabProp("text", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("tone", "XauxaTone", "Neutral"),
            ),
            states = listOf(
                LabState("Default", "Etiqueta neutra sobre Surface2.", LabReviewStatus.VERIFIED),
                LabState("Tono", "Par content/container del tono semántico.", LabReviewStatus.VERIFIED),
            ),
            tokens = listOf(
                LabTokenRef("XauxaType.Caption", "Tamaño del texto."),
                LabTokenRef("XauxaTracking.LabelMedium", "Tracking de etiqueta de M3."),
                LabTokenRef("XauxaSpacing.Sm", "Padding horizontal."),
            ),
            usage = listOf("Estado de sincronización por elemento (ElementSyncBadge), conteos."),
            tags = listOf("badge", "etiqueta", "estado"),
            darkThemeSupport = LabDarkThemeSupport.PENDING,
            darkThemeNote = "Resuelve el esquema vía XauxaColor; el esquema oscuro aplica valores de referencia pendientes de validación visual.",
            whenToUse = "Etiquetas de estado y tipo. Clasificación estática con XauxaCategoryChip; filtros con XauxaFilterChip.",
            androidMapping = "Box tonal con Text.",
            iosMapping = "Mismo componente compartido (Material3).",
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
                    "Dato inválido o vacío: lienzo claro (QrCanvas) enmarcado por el bloque tonal " +
                        "XauxaMediaFrame, sin borde de reposo. Comportamiento verificado en Android.",
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
                    "El host Wasm del laboratorio aún no decodifica: siempre muestra placeholder claro hasta " +
                    "implementar la ruta de decodificación en navegador.",
                    LabReviewStatus.PENDING,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Surface2", "Marco tonal (XauxaMediaFrame), sin borde de reposo."),
                LabTokenRef("XauxaColor.QrCanvas", "Lienzo claro garantizado del QR en ambos temas."),
                LabTokenRef("XauxaMetrics.QrPreviewSize", "Tamaño del preview."),
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
            whenToUse = "QR ya almacenado. Captura nueva con XauxaScannerViewport.",
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
                    "Android: INTEGRADO (CameraX + análisis ZXing continuo; runtime de sesión OPENED " +
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
            whenToUse = "Captura con cámara. QR almacenado con XauxaQrPreview.",
            androidMapping = "Preview + escaneo real con CameraX ; detección de QR real pendiente de hardware.",
            iosMapping = "AVFoundation implementado (U2); runtime pendiente de Xcode.",
            platforms = listOf(
                LabPlatformStatus("Android", true, "Preview + escaneo CameraX integrados ."),
                LabPlatformStatus("iOS", false, "AVFoundation implementado (U2); runtime pendiente de Xcode."),
                LabPlatformStatus("Web (Wasm)", false, "Preview real; getUserMedia pendiente."),
            ),
        ),
    )

    /** Full catalog: foundations first, then components in declaration order. */
    val all: List<LabComponentContract> = foundations + components

    fun find(id: String): LabComponentContract? = all.firstOrNull { it.id == id }
}

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
 * - NOT_SUPPORTED records an API gap that was checked against the source.
 */
object LabComponentCatalog {

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
                    "Los 16 tokens semánticos compilan y son consumidos por los componentes de core:ui.",
                    LabReviewStatus.VERIFIED,
                ),
                LabState(
                    "Paleta oscura",
                    "No existen variantes dark de los tokens; el tema Material oscuro no los altera.",
                    LabReviewStatus.NOT_SUPPORTED,
                ),
                LabState(
                    "Tokens de fondo semántico",
                    "Xauxa v13 define success-bg, danger-bg, warning-bg e info-bg para contenedores de estado; " +
                        "la implementación actual no los tiene.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Acento de marca AgendaQr (teal)."),
                LabTokenRef("XauxaColor.Danger", "Semántica de error y peligro."),
            ),
            usage = listOf(
                "Todo color de producción debe salir de XauxaColor; la compuerta verifyDesignSystemCompliance " +
                    "rechaza hex y constructores Color fuera de la capa de tokens.",
            ),
            tags = listOf("color", "tokens", "tema"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "Los tokens son valores claros fijos; no hay dimensión de tema en la implementación actual.",
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
                    "Xauxa documenta Archivo (display) y Roboto (UI); la implementación define tamaños " +
                    "pero no conecta las familias de fuente.",
                    LabReviewStatus.PENDING,
                ),
            ),
            tokens = listOf(LabTokenRef("XauxaType.Title", "Tamaño de título de sección y estado vacío.")),
            usage = listOf("Jerarquía textual de pantallas, secciones, etiquetas y metadatos."),
            tags = listOf("tipografía", "tokens"),
            darkThemeSupport = LabDarkThemeSupport.NOT_APPLICABLE,
            darkThemeNote = "Token no cromático: el tema no aplica.",
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
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "El fondo usa un token claro fijo; el contenedor no cambia con el tema Material oscuro.",
        ),
        LabComponentContract(
            id = "xauxa-section",
            name = "XauxaSection",
            category = LabCategory.SURFACES,
            purpose = "Encabezado de sección con título y acción contextual opcional.",
            description = "Columna con título XauxaType.Title en semibold, slot trailing alineado a la " +
                "derecha y contenido debajo separado por XauxaSpacing.Md.",
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
                LabTokenRef("XauxaSpacing.Md", "Separación entre título y contenido."),
            ),
            usage = listOf(
                "Agrupar filtros, obligaciones, comprobantes o metadatos en listas y detalles.",
            ),
            tags = listOf("sección", "encabezado"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "El título usa XauxaColor.TextPrimary fijo; no cambia con el tema.",
        ),
        LabComponentContract(
            id = "xauxa-tile",
            name = "XauxaTile",
            category = LabCategory.SURFACES,
            purpose = "Superficie rectangular para una entidad o bloque de contenido.",
            description = "Surface plana con borde XauxaMetrics.Border y fondo XauxaColor.Surface. Con " +
                "onClick no nulo añade rol Button y foco.",
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
                LabTokenRef("XauxaColor.Surface", "Fondo del tile."),
                LabTokenRef("XauxaColor.Border", "Borde del tile."),
                LabTokenRef("XauxaMetrics.Border", "Grosor del borde."),
            ),
            usage = listOf(
                "Obligación, destino QR, comprobante, contacto o bloque resumen dentro de una sección.",
            ),
            notes = listOf(
                "El contenido debe aportar su propia semántica textual; el tile solo aporta rol Button.",
            ),
            tags = listOf("superficie", "tarjeta", "tile"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "Fondo y borde usan tokens claros fijos; no cambian con el tema.",
        ),
        LabComponentContract(
            id = "xauxa-primary-button",
            name = "XauxaPrimaryButton",
            category = LabCategory.ACTIONS,
            purpose = "Acción primaria de AgendaQr.",
            description = "Botón relleno con XauxaColor.Brand y altura mínima de XauxaMetrics.ControlMinSize, " +
                "sin radios.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
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
                    "La matriz de estados de Xauxa exige un estado de carga en botones; la API actual no expone isLoading.",
                    LabReviewStatus.NOT_SUPPORTED,
                ),
            ),
            events = listOf(LabEvent("onClick", "Evento de activación principal.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Contenedor del botón."),
                LabTokenRef("XauxaColor.OnBrand", "Contenido sobre la marca."),
                LabTokenRef("XauxaColor.Surface2", "Contenedor deshabilitado."),
                LabTokenRef("XauxaColor.TextTertiary", "Contenido deshabilitado."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
            ),
            usage = listOf(
                "Añadir QR, guardar obligación, registrar comprobante, confirmar una acción.",
            ),
            notes = listOf("Una sola acción primaria visible por pantalla."),
            tags = listOf("botón", "acción", "primario"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "Los colores del botón son tokens claros fijos; no cambian con el tema.",
        ),
        LabComponentContract(
            id = "xauxa-secondary-button",
            name = "XauxaSecondaryButton",
            category = LabCategory.ACTIONS,
            purpose = "Acción secundaria o alternativa.",
            description = "Botón outlined con borde XauxaColor.Border y texto TextPrimary, con la misma " +
                "altura mínima de control que el primario.",
            props = listOf(
                LabProp("label", "String"),
                LabProp("onClick", "() -> Unit"),
                LabProp("modifier", "Modifier"),
                LabProp("enabled", "Boolean", "true"),
            ),
            states = listOf(
                LabState("Default", "Botón outlined habilitado.", LabReviewStatus.VERIFIED),
                LabState("Deshabilitado", "enabled = false atenúa el borde y el contenido.", LabReviewStatus.VERIFIED),
                LabState(
                    "Carga",
                    "La matriz de estados de Xauxa exige un estado de carga en botones; la API actual no expone isLoading.",
                    LabReviewStatus.NOT_SUPPORTED,
                ),
            ),
            events = listOf(LabEvent("onClick", "Evento de activación principal.")),
            tokens = listOf(
                LabTokenRef("XauxaColor.Border", "Borde del botón."),
                LabTokenRef("XauxaColor.TextPrimary", "Color del texto."),
                LabTokenRef("XauxaMetrics.Border", "Grosor del borde."),
                LabTokenRef("XauxaMetrics.ControlMinSize", "Altura mínima de 48dp."),
            ),
            usage = listOf("Escanear, ver detalle, abrir comprobante, cancelar un diálogo."),
            tags = listOf("botón", "acción", "secundario"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "Los colores del botón son tokens claros fijos; no cambian con el tema.",
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
            ),
            usage = listOf("Editar, cancelar o disparar una acción secundaria dentro de tiles o secciones."),
            tags = listOf("acción", "texto"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "El color del texto es un token claro fijo; no cambia con el tema.",
        ),
        LabComponentContract(
            id = "xauxa-status-banner",
            name = "XauxaStatusBanner",
            category = LabCategory.FEEDBACK,
            purpose = "Mensaje persistente de estado.",
            description = "Caja de ancho completo con borde y fondo Surface2; con danger = true usa fondo " +
                "Danger al 10% y texto Danger.",
            props = listOf(
                LabProp("message", "String"),
                LabProp("modifier", "Modifier"),
                LabProp("danger", "Boolean", "false"),
            ),
            states = listOf(
                LabState("Neutro", "Mensaje informativo sobre Surface2.", LabReviewStatus.VERIFIED),
                LabState("Peligro", "danger = true: fondo Danger translúcido y texto Danger.", LabReviewStatus.VERIFIED),
                LabState(
                    "Tonos éxito/información/advertencia",
                    "Xauxa (C13/C17) documenta tonos semánticos con tokens -bg; la implementación actual solo " +
                    "distingue neutro y peligro.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Danger", "Semántica de peligro."),
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
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "Fondo y texto usan tokens claros fijos; no cambian con el tema.",
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
                    "Xauxa C25 documenta skeleton para contenido en carga; no está implementado.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            tokens = listOf(LabTokenRef("XauxaColor.Brand", "Color del indicador.")),
            usage = listOf("Carga de destinos, comprobantes o sincronización en curso."),
            tags = listOf("feedback", "carga", "espera"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "El color del indicador es un token claro fijo; no cambia con el tema.",
        ),
        LabComponentContract(
            id = "xauxa-empty-state",
            name = "XauxaEmptyState",
            category = LabCategory.FEEDBACK,
            purpose = "Estado sin elementos con una acción de recuperación.",
            description = "Columna centrada con título Title semibold y botón primario, separados por " +
                "XauxaSpacing.Lg y con padding Huge.",
            props = listOf(
                LabProp("title", "String"),
                LabProp("actionLabel", "String"),
                LabProp("onAction", "() -> Unit"),
                LabProp("modifier", "Modifier"),
            ),
            states = listOf(
                LabState("Default", "Título centrado más botón primario de acción.", LabReviewStatus.VERIFIED),
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
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "Título y botón usan tokens claros fijos; no cambian con el tema.",
        ),
        LabComponentContract(
            id = "xauxa-favorite-indicator",
            name = "XauxaFavoriteIndicator",
            category = LabCategory.DATA,
            purpose = "Indicador visual de favorito.",
            description = "Círculo de XauxaMetrics.FavoriteIndicatorSize con fondo Brand (marcado) o Surface2 " +
                "(sin marcar).",
            props = listOf(LabProp("favorite", "Boolean")),
            states = listOf(
                LabState("Marcado", "favorito = true: círculo con color de marca.", LabReviewStatus.VERIFIED),
                LabState("Sin marcar", "favorito = false: círculo con Surface2.", LabReviewStatus.VERIFIED),
                LabState(
                    "Interactivo (toggle)",
                    "Xauxa C21 documenta un Favorite toggle interactivo; la implementación actual es un " +
                    "indicador de solo presentación, sin evento.",
                    LabReviewStatus.DOCUMENTED,
                ),
            ),
            tokens = listOf(
                LabTokenRef("XauxaColor.Brand", "Fondo del estado marcado."),
                LabTokenRef("XauxaColor.Surface2", "Fondo del estado sin marcar."),
                LabTokenRef("XauxaMetrics.FavoriteIndicatorSize", "Diámetro del indicador."),
            ),
            usage = listOf("Marcar visualmente un destino u obligación favorita en listas y detalles."),
            notes = listOf(
                "Al no ser interactivo no aplica el target de 48dp, pero carece de contentDescription y " +
                    "semántica propia: brecha de accesibilidad pendiente.",
            ),
            tags = listOf("favorito", "indicador", "datos"),
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "Los colores del indicador son tokens claros fijos; no cambian con el tema.",
        ),
        LabComponentContract(
            id = "xauxa-qr-preview",
            name = "XauxaQrPreview",
            category = LabCategory.QR,
            purpose = "Preview del QR almacenado de un destino.",
            description = "Contrato expect/actual: Android decodifica un PNG en base64 y lo renderiza; con " +
                "dato inválido o vacío muestra placeholder blanco. iOS difiere el decodificado y muestra " +
                "placeholder hasta validación de hardware.",
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
            darkThemeSupport = LabDarkThemeSupport.NOT_SUPPORTED,
            darkThemeNote = "El contenedor usa tokens claros fijos; no cambia con el tema.",
        ),
    )

    /** Full catalog: foundations first, then components in declaration order. */
    val all: List<LabComponentContract> = foundations + components

    fun find(id: String): LabComponentContract? = all.firstOrNull { it.id == id }
}

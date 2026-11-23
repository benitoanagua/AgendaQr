import java.io.File

/**
 * Gate del sistema de diseño. Las reglas por línea operan sobre el código
 * de producción; los source sets de test pueden usar primitivas para
 * verificar contratos como el tamaño mínimo de interacción.
 */
fun Project.productionKotlinSources(): Sequence<File> = sequence {
    sequenceOf("androidApp", "core", "feature", "shared").forEach { root ->
        val directory = file(root)
        if (directory.exists()) {
            yieldAll(directory.walkTopDown().filter {
                it.isFile && it.extension == "kt" &&
                    !it.path.contains("/build/") &&
                    !it.path.contains("/src/test/") &&
                    !it.path.contains("/src/androidTest/") &&
                    !Regex("/src/[a-zA-Z]*[Tt]est/").containsMatchIn(it.path)
            })
        }
    }
}

fun visualSources(): List<File> = productionKotlinSources().toList()

/** Capa de tokens: el único archivo con literales visuales. */
private val TokenLayerFile = "AqTokens.kt"
private val ThemeLayerFile = "AqTheme.kt"
private val IconVectorFile = "PolarisIconVectors.kt"

/**
 * Allowlist de literales de UI permitidos en feature/: solo identificadores
 * técnicos (ids, prefijos, MIME, rutas), nunca copy.
 */
val uiLiteralAllowlist = listOf(
    "image/png", "image/jpeg", "image/webp", "application/pdf",
    "file://", "ctx-", "operation-", "destination-", "comprobante-",
    "import-", "m-", "u-", "qr-", "d-", "photo-", "Revisar ",
)

fun isUiLiteralAllowed(text: String): Boolean =
    uiLiteralAllowlist.any { text.contains(it) }

/**
 * REGISTRO DE EXCEPCIONES del gate. El contrato normativo
 * (docs/05-design-system/00-contrato-normativo-diseno.md) declara la
 * tabla de excepciones E-xx; esta tarea compara AMBOS conjuntos y falla si
 * no coinciden. Las excepciones registradas son la única vía: ningún archivo de
 * producción dibuja bordes con `.border(`/`BorderStroke(` (la indicación
 * de foco y de estado vive en los componentes M3 y en el canvas).
 */
val exceptionRegistry: Map<String, List<String>> = mapOf(
    "E-01" to listOf("border:AqButtons.kt"),
    "E-04" to listOf("border:AqInputs.kt"),
    "E-07" to listOf("border:AqForms.kt"),
    // Bordes de teclas del teclado PIN (contraste en tema oscuro).
    "E-08" to listOf("border:AqPinEntry.kt"),
    // Borde de los campos de entrada Moon (AqTextInput / AqTextArea).
    "E-12" to listOf("border:AqTextInput.kt"),
    "E-13" to listOf("border:AqTextArea.kt"),
    // Borde de los campos del código de autenticación (AqAuthCode).
    "E-15" to listOf("border:AqAuthCode.kt"),
    // Borde del anillo de selección de MoonRadio (AqRadio.kt es su propio archivo).
    "E-14" to listOf("border:AqRadio.kt"),
    // `Modifier.shadow` se centraliza en la capa de tokens; solo los
    // archivos registrados pueden invocarlo (o elevarse vía `aqElevation`).
    // E-06 (elevación de botones) se retiró: los botones son planos y
    // `AqButtons.kt` no eleva nada; un registro sin uso lo detecta
    // `verifyScreenContract`.
)

/** Lista de símbolos legacy de la capa Polaris que NO deben importarse ni
 * llamarse desde `feature/`, `androidApp`, `iosApp` ni `shared`. Cada uno
 * tiene un equivalente Moon en el catálogo (§1 de la auditoría 2026-11-21)
 * o se compone únicamente con piezas Moon (catálogo §4). Se borra su
 * definición en `core/ui` en la Fase D.
 */
val legacySymbols = listOf(
    "AqActionSheet",
    "AqAmountField",
    "AqBadge",
    "AqBlockStack",
    "AqBottomAction",
    "AqBottomActionDock",
    "AqButtonGroup",
    "AqCard",
    "AqCheckboxGroup",
    "AqChipGroup",
    "AqChoiceGroup",
    "AqCompactAction",
    "AqContextBadge",
    "AqDangerButton",
    "AqDatePickerDialog",
    "AqDialog",
    "AqDialogList",
    "AqEmptyState",
    "AqEvidenceBadge",
    "AqExpansionPanel",
    "AqExtendedFab",
    "AqFeedbackEvent",
    "AqFeedbackHost",
    "AqFieldMessages",
    "AqFormField",
    "AqHeading",
    "AqIconBadge",
    "AqIconButton",
    "AqIndexTable",
    "AqInlineStack",
    "AqLink",
    "AqListBody",
    "AqListRow",
    "AqLoading",
    "AqLoadMoreFooter",
    "AqMetaRow",
    "AqNavBar",
    "AqNavRail",
    "AqOutlineButton",
    "AqOverflowAction",
    "AqPrimaryButton",
    "AqRadioButton",
    "AqScreenScaffold",
    "AqSearchField",
    "AqSearchTrigger",
    "AqSecondaryButton",
    "AqSection",
    "AqSectionHeader",
    "AqSelect",
    "AqSelectAllCheckbox",
    "AqSelectionBar",
    "AqShapes",
    "AqSkeleton",
    "AqSnackbarHost",
    "AqSpinner",
    "AqStatusBanner",
    "AqStatusPill",
    "AqTabs",
    "AqTappableCard",
    "AqTextAction",
    "AqTextField",
    "AqTonalTag",
    "AqTopBar",
)

/** Símbolos prohibidos en TODO el repo (declaración y uso): los de
 *  `legacySymbols` más `AqShapes`. Ningún archivo de producción puede
 *  declararlos ni llamarlos; además no puede resurgir ningún `AqMoon*`.
 */
val prohibitedSymbols = legacySymbols + listOf("AqShapes")

/** Prefijo prohibido en cualquier parte del repo (fuentes, tests, docs). */
val forbiddenAqMoonPrefix = "AqMoon"

/** Archivos con `.border(`/`BorderStroke(` permitidos: ninguno. */
val borderAllowedFiles: Set<String> =
    buildSet {
        exceptionRegistry.values.forEach { rules ->
            rules.forEach { rule -> if (rule.startsWith("border:")) add(rule.removePrefix("border:")) }
        }
    }

/** Archivos con `shadow(`/`.shadow(` permitidos (excepciones E-05/E-06). */
val shadowAllowedFiles: Set<String> =
    buildSet {
        exceptionRegistry.values.forEach { rules ->
            rules.forEach { rule -> if (rule.startsWith("shadow:")) add(rule.removePrefix("shadow:")) }
        }
    }

fun kotlinDesignViolations(path: String, lines: List<String>): List<String> = buildList {
    // Solo los vectores Polaris vía AqIcon; los APIs de iconos de Material
    // están prohibidos en producción.
    val bannedImports = listOf(
        "androidx.compose.material.icons",
        "Icons.Filled", "Icons.Outlined", "Icons.Rounded", "Icons.Default",
        "Icons.AutoMirrored", "Icons.Sharp", "Icons.TwoTone",
    )
    val rawHex = Regex("#[0-9A-Fa-f]{6,8}")
    val rawDp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.dp\\b")
    val rawSp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.sp\\b")
    val rawColor = Regex("\\bColor\\s*\\(")
    // Espaciado: los números sueltos en padding/spacedBy no pertenecen ni a
    // páginas ni a componentes; todo paso sale de AqSpacing.
    val rawPaddingLiteral = Regex("\\b(?:Modifier\\.)?padding\\(\\s*(?:[a-zA-Z]+\\s*=\\s*)?\\d")
    val rawPaddingValuesLiteral = Regex("\\bPaddingValues\\(\\s*(?:[a-zA-Z]+\\s*=\\s*)?\\d")
    val rawSpacedByLiteral = Regex("\\bspacedBy\\(\\s*\\d")
    val forbiddenVisualAuthority = listOf("MaterialTheme.colorScheme")
    // Las formas con esquinas viven SOLO en la capa de tokens; el tema
    // las pasa a MaterialTheme.shapes sin construir ninguna.
    val shapesAllowedFiles = listOf(TokenLayerFile)
    val isShadowAllowed = shapesAllowedFiles.any { path.endsWith(it) } ||
        shadowAllowedFiles.any { path.endsWith(it) }
    // La capa feature compone componentes del sistema; los componentes Material solo
    // viven en core:ui (que los adapta con tokens). Un import directo de
    // estos controles en feature/ es una fuga del design system.
    val bannedMaterialInFeature = listOf(
        "androidx.compose.material3.OutlinedTextField",
        "androidx.compose.material3.AlertDialog",
        "androidx.compose.material3.Button",
        "androidx.compose.material3.OutlinedButton",
        "androidx.compose.material3.TextButton",
        "androidx.compose.material3.FilterChip",
        "androidx.compose.material3.AssistChip",
    )
    val isFeatureSource = path.contains("/feature/")
    val isTokenLayer = path.endsWith(TokenLayerFile)
    // Los vectores declaran dimensiones intrínsecas y un color monocromo
    // que se reemplaza por el tinte semántico en AqIcon.
    val isIconVectorSource = path.endsWith(IconVectorFile)
    lines.forEachIndexed { index, line ->
        val location = "$path:${index + 1}"
        if (bannedImports.any(line::contains)) add("$location: forbidden Material icon API: $line")
        if (!isTokenLayer && rawHex.containsMatchIn(line)) add("$location: raw hex outside token layer: $line")
        if (!isTokenLayer && !isIconVectorSource && rawDp.containsMatchIn(line)) add("$location: raw dp outside token layer: $line")
        if (!isTokenLayer && rawSp.containsMatchIn(line)) add("$location: raw sp outside token layer: $line")
        if (!isTokenLayer && rawColor.containsMatchIn(line)) add("$location: raw Color constructor outside token layer: $line")
        if (!isTokenLayer && rawPaddingLiteral.containsMatchIn(line)) {
            add("$location: numeric padding literal; use an AqSpacing token: $line")
        }
        if (!isTokenLayer && rawPaddingValuesLiteral.containsMatchIn(line)) {
            add("$location: numeric PaddingValues literal; use AqSpacing tokens: $line")
        }
        if (!isTokenLayer && rawSpacedByLiteral.containsMatchIn(line)) {
            add("$location: numeric spacedBy literal; use an AqSpacing token: $line")
        }
        if (shapesAllowedFiles.none { path.endsWith(it) } &&
            (line.contains("RoundedCornerShape") || line.contains("CutCornerShape"))
        ) {
            add("$location: forbidden radius/elevation API: $line")
        }
        if (!isShadowAllowed && (line.contains("shadow(") || line.contains(".shadow("))) {
            add("$location: forbidden shadow API (register a shadow: exception): $line")
        }
        if (!isTokenLayer &&
            (line.contains("CircleShape") || line.contains("RectangleShape"))
        ) {
            add("$location: forbidden radius API (use AqCorner + AqSquircleShape, no CircleShape/RectangleShape): $line")
        }
        // ADR-0039: AqShapes es el legado de forma de Polaris; ya no se usa
        // en ningún componente de core/ui/components (solo AqCorner +
        // AqSquircleShape). La capa de tokens sigue siendo la única fuente.
        val isComponentSource = path.contains("/components/")
        if (isComponentSource && Regex("\\bAqShapes\\b").containsMatchIn(line)) {
            add("$location: AqShapes retirado; usar AqCorner / AqSquircleShape: $line")
        }
        // Colores nombrados de Compose fuera de la capa de tokens: todo
        // literal visual va al rol semántico (QrCanvas, TextPrimary…).
        if (!isTokenLayer && !isIconVectorSource &&
            Regex("\\bColor\\.(Black|White|Transparent|Red|Gray|LightGray|DarkGray|Yellow|Green|Blue|Cyan|Magenta|Unspecified)\\b")
                .containsMatchIn(line)
        ) {
            add("$location: Color.<name> outside token layer (use a semantic role from AqTokens.kt): $line")
        }
        // Duraciones literales en animaciones: toda animación consume
        // AqMotion (tween(150) local diluye el lenguaje de movimiento).
        val literalDuration = Regex("\\btween\\(\\s*\\d|\\bdurationMillis\\s*=\\s*\\d|\\bdelayMillis\\s*=\\s*\\d")
        if (!isTokenLayer && literalDuration.containsMatchIn(line)) {
            add("$location: raw animation duration; use AqMotion tokens: $line")
        }
        if (!path.endsWith(ThemeLayerFile) && forbiddenVisualAuthority.any(line::contains)) add("$location: MaterialTheme cannot be the visual authority outside AqTheme: $line")
        // `.border(`/`BorderStroke(` prohibidos en todo el código de
        // producción (las excepciones registradas son la única vía).
        val hasBorderCall = line.contains(".border(") || Regex("\\bBorderStroke\\s*\\(").containsMatchIn(line)
        if (hasBorderCall && borderAllowedFiles.none { path.endsWith(it) }) {
            add("$location: .border(/BorderStroke( outside the canonical focus file (register an exception or remove the border): $line")
        }
        // PatrÃ³n prohibido: AqQuickAction con ancho fijo
        // (AqMetrics.GridIconContainerSize = 48 dp). La celda debe aceptar el
        // ancho del padre (el llamador pasa weight(1f)); sÃ­ntoma S1.
        if (line.contains(".width(AqMetrics.GridIconContainerSize)")) {
            add("$location: AqQuickAction con ancho fijo prohibido; quitar .width(AqMetrics.GridIconContainerSize): $line")
        }
        // material3/material prohibido en feature/, shared/src,
        // androidApp/src e iosApp/src (solo core:ui puede adaptarlos).
        val isAppSource = isFeatureSource || path.contains("/shared/src/") ||
            path.contains("/androidApp/src/") || path.contains("/iosApp/src/")
        if (isAppSource && (bannedMaterialInFeature.any(line::contains) ||
                line.contains("androidx.compose.material3.") ||
                line.contains("androidx.compose.material.") ||
                line.contains("MaterialTheme.colorScheme"))
        ) {
            add("$location: app/feature code must use core:ui tokens: $line")
        }
        // Símbolos legacy de Polaris prohibidos en feature/ y en las apps:
        // cada uno tiene un equivalente Moon o se compone con piezas Moon.
        if (isAppSource) {
            legacySymbols.forEach { symbol ->
                if (Regex("\\b$symbol\\b").containsMatchIn(line)) {
                    add("$location: símbolo legacy prohibido '$symbol'; migrar al componente Moon equivalente: $line")
                }
            }
        }
        // Copy de UI en feature/ debe salir de AppStrings.
        if (isFeatureSource) {
            val uiCall = Regex("(AqText|AqHeading|AqEmptyState|AqStatusBanner|AqPrimaryButton|AqSecondaryButton|AqTextAction|AqDialog)\\(\\s*\"([^\"]+)\"").find(line)
            if (uiCall != null && !isUiLiteralAllowed(uiCall.groupValues[2])) {
                add("$location: UI string literal outside AppStrings: ${uiCall.groupValues[2]}")
            }
            val namedArg = Regex("(title|subtitle|actionLabel|label|placeholder|confirmLabel|dismissLabel|message|text|hint|what|dataStatus) = \"([^\"]+)\"").find(line)
            if (namedArg != null && !isUiLiteralAllowed(namedArg.groupValues[2])) {
                add("$location: UI string literal outside AppStrings: ${namedArg.groupValues[2]}")
            }
            // Copy dentro de EXPRESIONES también es copy: `label = if (...) "…"` y
            // variantes con when esquivaban la regla anterior.
            val expressionLiteral = Regex(
                "(title|subtitle|actionLabel|label|placeholder|confirmLabel|dismissLabel|message|text|hint|errorMessage|helperMessage|backLabel|busyDescription|what|dataStatus) = (?:if|when)[^\n]*?\"([^\"]{2,})\"",
            ).find(line)
            if (expressionLiteral != null && !isUiLiteralAllowed(expressionLiteral.groupValues[2])) {
                add("$location: UI string literal in expression outside AppStrings: ${expressionLiteral.groupValues[2]}")
            }
            // Literales en ErrorAction (constructor posicional) también son copy.
            val errorActionLiteral = Regex("ErrorAction\\.\\w+\\(\\s*\"([^\"]{2,})\"").find(line)
            if (errorActionLiteral != null && !isUiLiteralAllowed(errorActionLiteral.groupValues[1])) {
                add("$location: UI string literal in ErrorAction outside AppStrings: ${errorActionLiteral.groupValues[1]}")
            }
            // La interacción cruda es cosa del design system: feature/
            // compone componentes del sistema, nunca clickable propio
            // (feedback de pulsación, rol, foco y semántica viven en el DS).
            if (line.contains(".clickable(")) {
                add("$location: raw .clickable( in feature/ (compose a system component): $line")
            }
            if (line.contains("indication = null")) {
                add("$location: raw indication override in feature/ (feedback lives in the DS): $line")
            }
        }
        // Copy en EXPRESIONES que escapaban al regex: ifBlank { "…" },
        // Elvis `?: "frase"` y plantillas "…$var" en la capa de UI.
        if (path.contains("/presentation/")) {
            val trimmed = line.trim()
            val isComment = trimmed.startsWith("//") || trimmed.startsWith("*") || trimmed.startsWith("/*")
            val isDevMessage = line.contains("throw ") || line.contains("require(") ||
                line.contains("check(") || line.contains("IllegalStateException") ||
                line.contains("println(") || line.contains(".printStackTrace")
            if (!isComment && !isDevMessage) {
                Regex("ifBlank\\s*\\{\\s*\"([^\"]+)\"").find(line)?.let { match ->
                    val literal = match.groupValues[1]
                    if (!isUiLiteralAllowed(literal)) {
                        add("$location: UI literal in ifBlank{} must come from AppStrings: $literal")
                    }
                }
                Regex("\\?:\\s*\"([^\"]+)\"").findAll(line).forEach { match ->
                    val literal = match.groupValues[1]
                    // Solo frases de texto (letras + espacio): los defaults
                    // técnicos ("bin", "new", "image/png") no son copy.
                    val looksLikeCopy = literal.contains(" ") && literal.any { it.isLetter() }
                    if (looksLikeCopy && !isUiLiteralAllowed(literal)) {
                        add("$location: UI literal in Elvis expression must come from AppStrings: $literal")
                    }
                }
                Regex("\"([^\"]*\\$\\{?\\w+[^\"]*)\"").findAll(line).forEach { match ->
                    val literal = match.groupValues[1]
                    // Quitar TODOS los segmentos interpolados: si lo que
                    // queda no tiene letras, no hay copy que centralizar.
                    val withoutVariables = Regex("\\$\\{?[A-Za-z_][A-Za-z0-9_.]*}?").replace(literal, "")
                    val looksLikeCopy = withoutVariables.any { it.isLetter() }
                    if (looksLikeCopy && !isUiLiteralAllowed(literal)) {
                        add("$location: UI string template must come from AppStrings: $literal")
                    }
                }
            }
        }
        // core/ui no hardcodea COPY: en components/ todo texto de UI llega
        // como parámetro del llamador. Excepciones: mensajes require/error
        // (desarrollador), labels de animación "am_*" y glifos de un
        // carácter (×, ⋮, ⧉).
        if (path.contains("/core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/")) {
            val trimmed = line.trim()
            val isDevMessage = line.contains("require(") || line.contains("error(") || line.contains("check(")
            val isComment = trimmed.startsWith("*") || trimmed.startsWith("//") || trimmed.startsWith("/*")
            val isContinuation = trimmed.startsWith("\"")
            if (!isDevMessage && !isComment && !isContinuation) {
                Regex("\"([^\"]{3,})\"").findAll(line).forEach { match ->
                    val text = match.groupValues[1]
                    val isAnimationLabel = text.startsWith("am_")
                    val looksLikeCode = !text.contains(" ") && !text.contains("…") && !text.contains("·") &&
                        text.firstOrNull()?.isLetter() == true && text.first().isLowerCase()
                    val isTemplate = text.contains("$") || text.endsWith(" *")
                    if (text.any { it.isLetter() } && !isAnimationLabel && !looksLikeCode && !isTemplate) {
                        add("$location: UI string literal in core/ui components (pass it as a parameter, ADR-0008): $text")
                    }
                }
            }
        }
    }
}

fun checkViolations(): List<String> {
    val violations = mutableListOf<String>()
    visualSources().forEach { file ->
        violations += kotlinDesignViolations(file.path, file.readLines())
    }
    // No puede haber dos declaraciones públicas del mismo Aq* (p. ej.
    // AqBottomSheet declarado en AqOverlay.kt y en AqSheets.kt).
    violations += duplicateAqDeclarations()
    // El catálogo Moon debe estar completo: sin estados "pendiente" y cada
    // componente disponible/adoptado debe tener su símbolo Aq* exportado.
    violations += catalogCompletenessViolations()
    // En TODO el repo no puede haber identificadores AqMoon* (wrappers
    // legacy prohibidos), ni declaraciones/usos de símbolos prohibidos
    // (legacySymbols + AqShapes) en capa de producción.
    violations += forbiddenAqMoonAnywhere()
    violations += prohibitedSymbolsAnywhere()
    return violations
}

/** Todas las declaraciones públicas de nivel superior de un símbolo Aq* en
 * los fuentes de producción, indexadas por nombre. */
fun exportedAqSymbols(): Map<String, List<String>> {
    val result = mutableMapOf<String, MutableList<String>>()
    productionKotlinSources().forEach { file ->
        file.readLines().forEachIndexed { index, line ->
            val trimmed = line.trimStart()
            // Solo cuentas declaraciones propias de un símbolo Aq*: se excluyen
            // funciones/propiedades de extensión (`fun Receptor.metodo`) porque
            // no declaran al receptor, y se requiere límite de palabra seguido
            // de no-punto para evitar capturas parciales.
            Regex("""^(?:@Composable\s+)?(?:fun|class|data class|enum class|object|sealed class|sealed interface|annotation class|interface|typealias|val|var)\s+(Aq[A-Za-z0-9_]+)\b(?!\.)""").find(trimmed)?.let { match ->
                result.getOrPut(match.groupValues[1]) { mutableListOf() }.add("${file.path}:${index + 1}")
            }
        }
    }
    return result
}

/** Detecta declaraciones públicas duplicadas de un mismo Aq*. */
fun duplicateAqDeclarations(): List<String> =
    exportedAqSymbols()
        .filter { it.value.size > 1 }
        .flatMap { (name, locations) ->
            locations.map { "declaracion-duplicada: '$name' declarado en $it (debe quedar una sola implementación)" }
        }
        .toList()

/** Verifica que el catálogo Moon esté completo y que cada símbolo Aq* que
 * declara esté realmente exportado desde core/ui. */
fun catalogCompletenessViolations(): List<String> {
    val catalogFile = file("docs/05-design-system/13-catalogo-moon.md")
    if (!catalogFile.exists()) {
        return listOf("catalogo: no se encuentra docs/05-design-system/13-catalogo-moon.md")
    }
    val content = catalogFile.readText()
    val exported = exportedAqSymbols().keys.toSet()
    val violations = mutableListOf<String>()
    val catalogRow = Regex("""\|[^|]*\b(Aq[A-Za-z0-9_]+)\b[^|]*\|\s*(disponible|adoptado|pendiente|revisar)\s*\|""")
    content.lines().forEach { line ->
        catalogRow.find(line)?.let { match ->
            val aqName = match.groupValues[1]
            val estado = match.groupValues[2]
            when (estado) {
                "pendiente", "revisar" ->
                    violations += "catalogo: '$aqName' sigue con estado '$estado'; completar el puerto Moon y actualizar la ficha"
                else -> {
                    if (aqName !in exported) {
                        violations += "catalogo: '$aqName' marcado como '$estado' pero no se encuentra su declaración en core/ui"
                    }
                }
            }
        }
    }
    return violations
}

/** Ningún identificador AqMoon* en ningún fuente Kotlin del repo. */
fun forbiddenAqMoonAnywhere(): List<String> {
    val violations = mutableListOf<String>()
    file(".").walkTopDown().filter { it.isFile && it.extension == "kt" && !it.path.contains("/build/") }.forEach { file ->
        file.readLines().forEachIndexed { index, line ->
            Regex("\\bAqMoon[A-Za-z0-9_]*").find(line)?.let { match ->
                violations.add("${file.path}:${index + 1}: identificador AqMoon* prohibido: ${match.value}")
            }
        }
    }
    return violations
}

/** Ningún símbolo prohibido (legacySymbols + AqShapes) declarado ni llamado
 *  en capas de feature/shared/androidApp/iosApp. */
fun prohibitedSymbolsAnywhere(): List<String> {
    val violations = mutableListOf<String>()
    productionKotlinSources().filter { it.path.contains("/feature/") || it.path.contains("/shared/src/") || it.path.contains("/androidApp/src/") || it.path.contains("/iosApp/src/") }.forEach { file ->
        file.readLines().forEachIndexed { index, line ->
            prohibitedSymbols.forEach { symbol ->
                if (Regex("\\b$symbol\\b").containsMatchIn(line)) {
                    violations.add("${file.path}:${index + 1}: símbolo prohibido '${symbol}' en producción: $line")
                }
            }
        }
    }
    return violations
}

/**
 * Guardia permanente del área táctil: el mínimo interactivo canónico es
 * 48 dp. Si alguien baja este valor, la accesibilidad se rompe.
 */
tasks.register("verifyControlMinSize") {
    group = "verification"
    description = "AqMetrics.ControlMinSize must remain 48.dp."
    doLast {
        val tokens = file("core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/AqTokens.kt")
        if (!tokens.exists()) return@doLast
        val content = tokens.readText()
        if (!content.contains("val ControlMinSize = 48.dp")) {
            throw GradleException("Design gate failed: AqMetrics.ControlMinSize must remain 48.dp.")
        }
    }
}

/**
 * Guardia permanente: ningún script sh/py/js/mjs/ts/ps1/bat fuera de la
 * lista de excepciones (gradlew, gradlew.bat, salidas en build/).
 */
tasks.register("verifyNoScripts") {
    group = "verification"
    description = "No shell/JS/TS scripts in the repo (Kotlin way)."
    doLast {
        val scriptExtensions = listOf(".sh", ".py", ".js", ".mjs", ".ts", ".ps1", ".bat")
        val exceptions = listOf("gradlew", "gradlew.bat")
        val root = project.rootDir
        val violations = mutableListOf<String>()
        root.walkTopDown().forEach { file ->
            if (file.isFile && !file.path.contains("/build/") && !file.path.contains("/.git/")) {
                val name = file.name
                val isScript = scriptExtensions.any { name.endsWith(it) }
                if (isScript && name !in exceptions) {
                    violations.add("${file.relativeTo(root).path}")
                }
            }
        }
        if (violations.isNotEmpty()) {
            throw GradleException(
                "Kotlin way: shell/JS/TS scripts are forbidden. " +
                    "Port them to Gradle tasks or Kotlin modules. Found:\n" +
                    violations.joinToString("\n  ") { "  $it" }
            )
        }
        logger.lifecycle("verifyNoScripts: PASS (no forbidden scripts)")
    }
}

/**
 * Verificación de documentación:
 * (a) enlaces/paths existen; (b) orphans desde README; (c) marcadores
 * transitorios; (d) ADR citado existe; numeración.
 */
tasks.register("verifyDocs") {
    group = "verification"
    description = "Verifies docs: links, reachability, transient markers, ADR refs."
    doLast {
        val docs = file("docs")
        val allMd = docs.walkTopDown().filter { it.extension == "md" }.toList()
        val violations = mutableListOf<String>()

        // (a) Dead links (relative .md paths).
        allMd.forEach { file ->
            val content = file.readText()
            Regex("\\]\\(([^)#\\s]+\\.md)\\)").findAll(content).forEach { match ->
                val linked = match.groupValues[1]
                if (!linked.startsWith("http") && !File(file.parentFile, linked).exists()) {
                    violations.add("dead-link: ${file.relativeTo(docs)} -> $linked")
                }
            }
        }

        // (b) Orphans (not reachable from docs/README.md).
        val readme = File(docs, "README.md")
        if (readme.exists()) {
            val linked = Regex("\\]\\(([\\w./-]+\\.md)\\)").findAll(readme.readText())
                .map { it.groupValues[1] }.toSet()
            allMd.forEach { file ->
                val rel = file.relativeTo(docs).path
                if (rel != "README.md" && rel !in linked) {
                    violations.add("orphan: $rel (not in docs/README.md)")
                }
            }
        }

        // (c) Transient markers.
        val markers = mapOf(
            "Ronda \\d+" to "'Ronda N'",
            "Fase \\d+" to "'Fase N'",
            "TODO:" to "TODO marker",
            "\\bFIXME\\b" to "FIXME",
        )
        allMd.forEach { file ->
            val content = file.readText()
            val isAdr = file.name.startsWith("ADR-")
            markers.forEach { (pattern, desc) ->
                Regex(pattern).findAll(content).forEach { _ ->
                    // ADRs can mention "Ronda" in their context sections.
                    if (!isAdr || desc == "TODO" || desc == "FIXME") {
                        violations.add("transient: ${file.relativeTo(docs)} has $desc")
                    }
                }
            }
        }

        // (d) ADR references exist.
        val existingAdrs = allMd.filter { it.name.startsWith("ADR-") }
            .mapNotNull { Regex("^ADR-(\\d+)").find(it.name)?.groupValues?.get(1)?.toInt() }
            .toSet()
        allMd.forEach { file ->
            Regex("ADR-(\\d{4})").findAll(file.readText()).forEach { match ->
                val num = match.groupValues[1].toIntOrNull()
                if (num != null && num !in existingAdrs) {
                    violations.add("missing-adr: ${file.relativeTo(docs)} cites ADR-${match.groupValues[1]} (not found)")
                }
            }
        }

        // (e) Marcadores transitorios en COMENTARIOS de código Kotlin
        // (producción): el porqué permanente se conserva, el sello de la
        // pasada no (la historia vive en git log).
        val ktMarkers = mapOf(
            "Ronda \\d+" to "'Ronda N'",
            "\\bFase \\d+" to "'Fase N'",
            "\\bP[01]\\b" to "'P0/P1' como historia",
            "\\bT\\d+\\b" to "'T<N>' como historia",
            "\\bts\\d+\\b" to "'tsN' como historia",
            "TODO:" to "TODO marker",
            "\\bFIXME\\b" to "FIXME",
        )
        productionKotlinSources().forEach { file ->
            file.readLines().forEachIndexed { index, line ->
                val trimmed = line.trim()
                val commentText = when {
                    trimmed.startsWith("//") -> trimmed.removePrefix("//")
                    trimmed.startsWith("/*") || trimmed.startsWith("*") -> trimmed
                    "//" in line -> line.substring(line.lastIndexOf("//"))
                    else -> null
                }
                if (commentText != null) {
                    ktMarkers.forEach { (pattern, desc) ->
                        if (Regex(pattern).containsMatchIn(commentText)) {
                            violations.add("transient-kt: ${file.relativeTo(rootDir)}:${index + 1} has $desc")
                        }
                    }
                }
            }
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                "verifyDocs found ${violations.size} violations:\n" +
                    violations.take(30).joinToString("\n") { "  $it" } +
                    if (violations.size > 30) "\n  ... and ${violations.size - 30} more" else ""
            )
        }
        logger.lifecycle("verifyDocs: PASS (${allMd.size} documents)")
    }
}

tasks.register("verifyDesignSystemCompliance") {
    group = "verification"
    description = "Enforces design-system invariants in production Kotlin sources."
    dependsOn("verifyLayoutGeometry")
    doLast {
        val violations = checkViolations().toMutableList()
        if (file("design-tokens.json").exists()) {
            violations += "design-tokens.json was retired as an editable source (AqTokens.kt is canonical); delete it instead of editing"
        }
        // La lista de excepciones del gate DEBE coincidir con la tabla del
        // contrato: sin entrada en AMBAS partes, no existe excepción válida.
        val contractFile = file("docs/05-design-system/00-contrato-normativo-diseno.md")
        if (contractFile.exists()) {
            val tableIds = contractFile.readLines()
                .mapNotNull { row -> Regex("^\\| (E-\\d+) \\|").find(row)?.groupValues?.get(1) }
                .toSet()
            val gateIds = exceptionRegistry.keys.toSet()
            val missingInGate = tableIds - gateIds
            val unknownInGate = gateIds - tableIds
            if (missingInGate.isNotEmpty()) {
                violations += "contrato: excepciones sin registro en el gate: ${missingInGate.sorted().joinToString()}"
            }
            if (unknownInGate.isNotEmpty()) {
                violations += "contrato: el gate registra excepciones que NO están en la tabla: ${unknownInGate.sorted().joinToString()}"
            }
        } else {
            violations += "contrato: no se encuentra ${contractFile.path} para verificar la tabla de excepciones"
        }
        require(violations.isEmpty()) { "Design System violations:\n${violations.joinToString("\n")}" }
    }
}

tasks.register("verifyDesignSystemFixtures") {
    group = "verification"
    description = "Self-test: forbidden examples must fail, token-correct usage must pass."
    doLast {
        // Kotlin fixtures: los imports Material prohibidos en feature/
        // DEBEN fallar, y el mismo import en core:ui (donde los componentes
        // el sistema adaptan Material) no debe.
        val bannedFeatureImports = listOf(
            "import androidx.compose.material3.OutlinedTextField",
            "import androidx.compose.material3.AlertDialog",
            "import androidx.compose.material3.Button",
            "import androidx.compose.material3.OutlinedButton",
            "import androidx.compose.material3.TextButton",
            "import androidx.compose.material3.FilterChip",
            "import androidx.compose.material3.AssistChip",
        )
        val featurePath = "/repo/feature/destinations/presentation/src/commonMain/kotlin/Fixture.kt"
        val missedBans = bannedFeatureImports.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isEmpty()
        }
        require(missedBans.isEmpty()) {
            "feature/ Material imports that must fail passed: ${missedBans.joinToString()}"
        }
        val corePath = "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/Fixture.kt"
        val falseCoreBans = bannedFeatureImports.filter {
            kotlinDesignViolations(corePath, listOf(it)).isNotEmpty()
        }
        require(falseCoreBans.isEmpty()) {
            "core:ui adapter imports must not be banned: ${falseCoreBans.joinToString()}"
        }

        // Copy de UI en feature/: el literal crudo DEBE fallar…
        val rawLiteral = "AqText(\"Sin conexión\", color = AqColor.TextSecondary)"
        require(kotlinDesignViolations(featurePath, listOf(rawLiteral)).isNotEmpty()) {
            "feature/ UI literal that must fail passed"
        }
        // …y el mismo texto vía AppStrings o id técnico debe pasar.
        val allowedD2 = listOf(
            "AqText(AppStrings.offlineBanner, color = AqColor.TextSecondary)",
            "AqText(\"operation-123\", color = AqColor.TextSecondary)",
        )
        val falseLiterals = allowedD2.filter { kotlinDesignViolations(featurePath, listOf(it)).isNotEmpty() }
        require(falseLiterals.isEmpty()) {
            "Valid AppStrings/technical literals failed enforcement: ${falseLiterals.joinToString()}"
        }

        // Literales de copy DENTRO de expresiones deben fallar…
        val expressionLiterals = listOf(
            "errorMessage = if (emailError) \"Ingresa un correo válido\" else null",
            "label = when (type) { PAGO -> \"Pago\" else -> \"Cobro\" }",
        )
        val missedExpressions = expressionLiterals.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isEmpty()
        }
        require(missedExpressions.isEmpty()) {
            "feature/ expression literals that must fail passed"
        }
        // …y la MISMA forma con AppStrings (o id técnico) debe pasar.
        val allowedExpressions = listOf(
            "errorMessage = if (emailError) AppStrings.IngresaUnCorreoValido else null",
            "label = when (type) { PAGO -> AppStrings.Pago else -> AppStrings.Cobro }",
            "AqText(\"ctx-\" + id, color = AqColor.TextSecondary)",
        )
        val falseExpressionFlags = allowedExpressions.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isNotEmpty()
        }
        require(falseExpressionFlags.isEmpty()) {
            "Valid expression usage failed enforcement: ${falseExpressionFlags.joinToString()}"
        }

        // Copy en core/ui/components DEBE fallar…
        val componentsPath = "/repo/core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/Fixture.kt"
        val copyInComponents = listOf(
            "Text(\"Cargando archivo…\", color = AqColor.TextSecondary)",
            "AqTextAction(label = \"Quitar\", onClick = {})",
        )
        val missedCopy = copyInComponents.filter { kotlinDesignViolations(componentsPath, listOf(it)).isEmpty() }
        require(missedCopy.isEmpty()) {
            "components/ copy literals that must fail passed (ADR-0008): ${missedCopy.joinToString()}"
        }
        // …y lo que NO es copy debe pasar: parámetros, mensajes de
        // desarrollador, labels de animación y glifos de un carácter.
        val allowedInComponents = listOf(
            "Text(loadingLabel, color = AqColor.TextSecondary)",
            "require(visibleActions.size <= 3) { \"máximo 3 acciones visibles\" }",
            "animateFloatAsState(targetValue = 1f, label = \"am_press\")",
            "Text(\"×\", fontSize = AqType.Title)",
        )
        val falseCopyFlags = allowedInComponents.filter { kotlinDesignViolations(componentsPath, listOf(it)).isNotEmpty() }
        require(falseCopyFlags.isEmpty()) {
            "Valid components usage failed enforcement: ${falseCopyFlags.joinToString()}"
        }

        // Interacción cruda: .clickable( e indication = null en feature/
        // deben fallar; en core:ui (donde viven los componentes) pasan.
        val rawInteraction = listOf(
            "Modifier.clickable(onClick = { open() })",
            ".clickable(interactionSource = source, indication = null, role = Role.Button, onClick = action)",
        )
        val missedInteraction = rawInteraction.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isEmpty()
        }
        require(missedInteraction.isEmpty()) {
            "feature/ raw interaction that must fail passed: ${missedInteraction.joinToString()}"
        }
        val falseInteractionFlags = rawInteraction.filter {
            kotlinDesignViolations(corePath, listOf(it)).isNotEmpty()
        }
        require(falseInteractionFlags.isEmpty()) {
            "core:ui interaction must not be banned: ${falseInteractionFlags.joinToString()}"
        }

        // Espaciado: padding/PaddingValues/spacedBy con números sueltos
        // DEBEN fallar en páginas y componentes…
        val rawSpacing = listOf(
            "Modifier.padding(horizontal = 16.dp)",
            "Modifier.padding(16)",
            "contentPadding = PaddingValues(horizontal = 8.dp)",
            "verticalArrangement = Arrangement.spacedBy(12.dp)",
        )
        val missedSpacing = rawSpacing.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isEmpty()
        }
        require(missedSpacing.isEmpty()) {
            "numeric spacing that must fail passed: ${missedSpacing.joinToString()}"
        }
        // …y la misma forma con tokens debe pasar (en capa de tokens y fuera).
        val allowedSpacing = listOf(
            "Modifier.padding(horizontal = AqSpacing.Lg)",
            "contentPadding = PaddingValues(horizontal = AqSpacing.Md)",
            "verticalArrangement = Arrangement.spacedBy(AqSpacing.MdPlus)",
        )
        val falseSpacingFlags = allowedSpacing.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isNotEmpty()
        }
        require(falseSpacingFlags.isEmpty()) {
            "Valid token spacing failed enforcement: ${falseSpacingFlags.joinToString()}"
        }

        // verifyLayoutGeometry: AqQuickAction con ancho fijo (S1) debe ser
        // rechazado; el uso correcto con weight(1f) debe pasar.
        val quickActionFixedWidth =
            "AqQuickAction(label = \"ctx-1\", icon = icon, onClick = {}, modifier = Modifier.width(AqMetrics.GridIconContainerSize))"
        require(kotlinDesignViolations(featurePath, listOf(quickActionFixedWidth)).isNotEmpty()) {
            "AqQuickAction con width fija debe fallar verifyLayoutGeometry"
        }
        require(kotlinDesignViolations(featurePath, listOf(
            "AqQuickAction(label = \"ctx-1\", icon = icon, onClick = {}, modifier = Modifier.weight(1f))"
        )).isEmpty()) {
            "AqQuickAction con weight(1f) debe pasar verifyLayoutGeometry"
        }

        // Formas fuera del design system: en feature/, shared/ y
        // androidApp/ están prohibidas; solo la capa de tokens las define.
        val shapeInFeature = "val card = RoundedCornerShape(12.dp)"
        require(kotlinDesignViolations(featurePath, listOf(shapeInFeature)).isNotEmpty()) {
            "feature/ RoundedCornerShape(12.dp) must fail (shapes are tokens)"
        }
        val componentShapePath = "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/FixtureShape.kt"
        require(kotlinDesignViolations(componentShapePath, listOf(shapeInFeature)).isNotEmpty()) {
            "components/ RoundedCornerShape must fail"
        }
        val tokenShapeFixture = "val AqShapeMedium = RoundedCornerShape(size = AqRadius.Medium)"
        val tokenShapePath = "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/AqTokens.kt"
        require(kotlinDesignViolations(tokenShapePath, listOf(tokenShapeFixture)).isEmpty()) {
            "token layer must allow the shape set"
        }

        // Bordes: `.border(`/`BorderStroke(` DEBEN fallar en todo el
        // código de producción (el sistema no dibuja bordes; el foco y
        // el estado viven en los componentes M3 y en el canvas).
        val borderInFeature = "Modifier.border(BorderStroke(AqMetrics.Border, AqColor.Accent), AqShapes.Medium)"
        listOf(
            featurePath,
            "/repo/shared/src/commonMain/kotlin/Fixture.kt",
            "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqTone.kt",
            "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AqDialogs.kt",
        ).forEach { bannedPath ->
            require(kotlinDesignViolations(bannedPath, listOf(borderInFeature)).isNotEmpty()) {
                ".border( must fail in production code: $bannedPath"
            }
        }

        // Color.<nombre> y duraciones literales DEBEN fallar fuera de la
        // capa de tokens…
        val colorFixtures = listOf("Color.White", "Color.Black", "Color.Transparent", "Color(0xFF000000)")
        colorFixtures.forEach { colorLiteral ->
            val line = "val bg = $colorLiteral"
            require(kotlinDesignViolations(featurePath, listOf(line)).isNotEmpty()) {
                "$colorLiteral outside tokens must fail"
            }
            require(kotlinDesignViolations(componentsPath, listOf(line)).isNotEmpty()) {
                "$colorLiteral in components must fail"
            }
            require(kotlinDesignViolations(tokenShapePath, listOf(line)).isEmpty()) {
                "$colorLiteral in token layer must pass"
            }
        }
        require(kotlinDesignViolations(featurePath, listOf("val bg = AqColor.Transparent")).isEmpty()) {
            "AqColor.Transparent must pass"
        }
        listOf(
            "animateFloatAsState(1f, tween(150))",
            "animateFloatAsState(1f, tween(durationMillis = 150))",
            "animateFloatAsState(1f, tween(300, delayMillis = 100))",
        ).forEach { durationLiteral ->
            require(kotlinDesignViolations(featurePath, listOf(durationLiteral)).isNotEmpty()) {
                "raw animation duration must fail: $durationLiteral"
            }
        }
        require(kotlinDesignViolations(featurePath, listOf("animateFloatAsState(1f, tween(AqMotion.DurationShortMs))")).isEmpty()) {
            "AqMotion token duration must pass"
        }
        // Shapes fuera del design system: en feature/, SHARED y
        // androidApp/ también están prohibidas.
        listOf(
            "RectangleShape",
            "CircleShape",
            "RoundedCornerShape(0.dp)",
        ).forEach { shape ->
            listOf(
                featurePath,
                componentsPath,
                "/repo/shared/src/commonMain/kotlin/Fixture.kt",
                "/repo/androidApp/src/main/kotlin/Fixture.kt",
            ).forEach { bannedPath ->
                require(kotlinDesignViolations(bannedPath, listOf("val s = $shape")).isNotEmpty()) {
                    "$shape must fail in $bannedPath"
                }
            }
        }
        // Iconos: los presets de Material están prohibidos en producción;
        // Polaris va por AqIcons.
        listOf("Icons.Default", "Icons.AutoMirrored", "Icons.Sharp", "Icons.TwoTone").forEach { preset ->
            require(kotlinDesignViolations(componentsPath, listOf("val i = ${preset}.Add")).isNotEmpty()) {
                "$preset must fail (material icons are banned)"
            }
        }
        require(kotlinDesignViolations(componentsPath, listOf("val i = AqIcons.Add")).isEmpty()) {
            "AqIcons must pass"
        }

        // D2 extendida: ifBlank/Elvis/plantillas en la capa de UI DEBEN
        // fallar…
        listOf(
            "AqText(operation.amount.orEmpty().ifBlank { \"sin monto\" }, color = AqColor.TextPrimary)",
            "val extension = candidate.extension ?: \"Elemento importado\"",
        ).forEach { line ->
            require(kotlinDesignViolations(featurePath, listOf(line)).isNotEmpty()) {
                "D2 expression must fail: $line"
            }
        }
        // …y el pegamento técnico / defaults de datos NO son copy.
        val allowedExpressionsExtended = listOf(
            "val file = candidate.extension ?: \"application/octet-stream\"",
            "val key = existing?.id ?: \"new\"",
            "saveImportedAssets(name = AppStrings.QrImportado + if (many) \" \" + (index + 1) else \"\")",
        )
        val falseD2Flags = allowedExpressionsExtended.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isNotEmpty()
        }
        require(falseD2Flags.isEmpty()) {
            "Technical defaults failed enforcement: ${falseD2Flags.joinToString()}"
        }

        // Símbolos legacy de Polaris prohibidos en feature/ y en las apps.
        listOf("AqDialog(", "AqTextField(", "AqPrimaryButton(", "AqSnackbarHost(", "AqTopBar(", "AqShapes.Medium").forEach { legacyCall ->
            require(kotlinDesignViolations(featurePath, listOf("val x = $legacyCall")).isNotEmpty()) {
                "el símbolo legacy '$legacyCall' en feature/ debe fallar el gate"
            }
            require(kotlinDesignViolations("/repo/shared/src/commonMain/kotlin/Fixture.kt", listOf("val x = $legacyCall")).isNotEmpty()) {
                "el símbolo legacy '$legacyCall' en shared/ debe fallar el gate"
            }
            require(kotlinDesignViolations("/repo/androidApp/src/main/kotlin/Fixture.kt", listOf("val x = $legacyCall")).isNotEmpty()) {
                "el símbolo legacy '$legacyCall' en androidApp/ debe fallar el gate"
            }
            require(kotlinDesignViolations("/repo/iosApp/src/main/kotlin/Fixture.kt", listOf("val x = $legacyCall")).isNotEmpty()) {
                "el símbolo legacy '$legacyCall' en iosApp/ debe fallar el gate"
            }
        }
        // Los equivalentes Moon NO están prohibidos en feature/.
        require(kotlinDesignViolations(featurePath, listOf("val x = AqButton(label = \"ctx-1\", onClick = {})")).isEmpty()) {
            "AqButton (Moon) en feature/ debe pasar el gate"
        }
        require(kotlinDesignViolations(featurePath, listOf("val x = AqModal(show = true, title = \"ctx-1\", dismissLabel = \"ctx-2\", onDismissRequest = {})")).isEmpty()) {
            "AqModal (Moon) en feature/ debe pasar el gate"
        }
        // AqShapes prohibido en core/ui/components (la capa de componentes
        // debe usar AqCorner / AqSquircleShape).
        require(kotlinDesignViolations(componentsPath, listOf("val s = AqShapes.Medium")).isNotEmpty()) {
            "AqShapes en core/ui/components debe fallar el gate"
        }
        // AqTokens.kt es la definición de AqShapes (capa de tokens): se
        // permite allí y solo allí.
        require(kotlinDesignViolations(tokenShapePath, listOf("object AqShapes { val Dummy = 1 }")).isEmpty()) {
            "la definición de AqShapes en el token layer debe pasar"
        }

        // Fixtures negativos de verifyNoLegacyAnywhere: declaración legacy
        // en core/ui/components, uso legacy en un androidUnitTest, y
        // AqShapes.Medium deben ser detectadas como legacy.
        val legacySymbolRegexes = (legacySymbols + "AqShapes").distinct().map { it to Regex("\\b$it\\b") }
        val legacyInCoreUi = "fun AqSpinner() = Unit"
        require(legacySymbolRegexes.any { (_, r) -> r.containsMatchIn(legacyInCoreUi) }) {
            "debe detectar símbolo legacy declarado en core/ui/components"
        }
        val legacyInAndroidUnitTest = "val x = AqSpinner()"
        require(legacySymbolRegexes.any { (_, r) -> r.containsMatchIn(legacyInAndroidUnitTest) }) {
            "debe detectar símbolo legacy usado en un androidUnitTest"
        }
        require(legacySymbolRegexes.any { (_, r) -> r.containsMatchIn("val s = AqShapes.Medium") }) {
            "debe detectar AqShapes.Medium prohibido en TODO el repo"
        }
        // Fixtures positivos: uso Moon correcto no debe ser detectado como legacy.
        require(legacySymbolRegexes.none { (_, r) -> r.containsMatchIn("val x = AqButton(label = \"ctx-1\", onClick = {})") }) {
            "AqButton (Moon) no debe ser detectado como legacy"
        }
        require(legacySymbolRegexes.none { (_, r) -> r.containsMatchIn("val x = AqModal(show = true)") }) {
            "AqModal (Moon) no debe ser detectado como legacy"
        }
        require(legacySymbolRegexes.none { (_, r) -> r.containsMatchIn("val x = AqCircularLoader()") }) {
            "AqCircularLoader (Moon) no debe ser detectado como legacy"
        }
        // Import material3/material prohibido en feature/.
        require(kotlinDesignViolations(featurePath, listOf("import androidx.compose.material3.Text")).isNotEmpty()) {
            "import material3 en feature/ debe fallar el gate"
        }
        require(kotlinDesignViolations(featurePath, listOf("import androidx.compose.material.Surface")).isNotEmpty()) {
            "import material en feature/ debe fallar el gate"
        }
        require(kotlinDesignViolations(corePath, listOf("import androidx.compose.material3.Text")).isEmpty()) {
            "import material3 en core:ui debe pasar el gate (adaptador del DS)"
        }

        // Declaración duplicada de un mismo Aq*: la función debe ser
        // invocable y devolver solo violaciones de duplicado (el repo real
        // tiene AqBottomSheet duplicado, se espera rojo hasta la Fase D).
        val dups = duplicateAqDeclarations()
        require(dups.all { it.startsWith("declaracion-duplicada:") }) {
            "duplicateAqDeclarations debe devolver solo violaciones de duplicado: $dups"
        }

        // Catálogo Moon: ningún componente debe quedar con estado "pendiente"
        // ni "revisar", y cada disponible/adoptado debe tener su Aq* exportado.
        // Mientras haya puertos pendientes, este requisito falla (backlog).
        val catalogViolations = catalogCompletenessViolations()
        require(catalogViolations.all { it.startsWith("catalogo:") }) {
            "catalogCompletenessViolations debe devolver solo violaciones de catálogo: $catalogViolations"
        }
    }
}

/** Gate de geometría de layout (Fase 5). Ejecuta la suite AqLayoutGeometryTest
 *  (360 dp, claro/oscuro, fuente 100 % y 150 %) y rechaza los patrones
 *  prohibidos detectados en kotlinDesignViolations (AqQuickAction con ancho
 *  fijo). La suite recorre las geometrías crÃ­ticas de los componentes Moon.
 */
tasks.register("verifyLayoutGeometry") {
    group = "verification"
    description = "Verifica la geometría Moon mediante AqLayoutGeometryTest y patrones prohibidos."
    dependsOn(":core:ui:testDebugUnitTest")
    doLast {
        val violations = mutableListOf<String>()
        // Detecta patrones prohibidos en componentes de core/ui.
        val componentsDir = file("core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components")
        if (componentsDir.exists()) {
            componentsDir.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .forEach { source ->
                    violations += kotlinDesignViolations(
                        source.path,
                        source.readLines()
                    )
                }
        }
        // Resultados de la suite AqLayoutGeometryTest.
        val resultFile = file(
            "core/ui/build/test-results/testDebugUnitTest/" +
                "TEST-com.agendaqr.core.ui.components.AqLayoutGeometryTest.xml"
        )
        if (!resultFile.exists()) {
            violations += "verifyLayoutGeometry: no se encontrÃ³ el resultado de AqLayoutGeometryTest"
        } else {
            val content = resultFile.readText()
            Regex("failures=\"(\\d+)\"").find(content)?.groupValues?.get(1)?.toIntOrNull()?.takeIf { it > 0 }?.let {
                violations += "verifyLayoutGeometry: AqLayoutGeometryTest tiene $it fallas"
            }
        }
        require(violations.isEmpty()) {
            "Layout geometry violations (${violations.size}):\n" +
                violations.take(40).joinToString("\n") { "  $it" }
        }
    }
}

tasks.register("verifyArchitectureBoundaries") {
    group = "verification"
    description = "Prevents UI/domain dependency inversion and WaraWerse business leakage."
    doLast {
        val domain = file("feature/destinations/domain/src/commonMain/kotlin")
            .walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
        val forbidden = domain.flatMap { source ->
            source.readLines().mapIndexedNotNull { index, line ->
                if (line.contains("androidx.compose") || line.contains("com.agendaqr.core.ui") ||
                    line.contains("android.") || line.contains("platform.") ||
                    line.contains("warawerse") || line.contains("GameRules") || line.contains("Phonetic")) {
                    "${source.path}:${index + 1}: $line"
                } else null
            }
        }
        require(forbidden.isEmpty()) { "Domain boundary violations:\n${forbidden.joinToString("\n")}" }

        val all = file(".").walkTopDown().filter { it.isFile && it.extension == "kt" && !it.path.contains("/build/") }.toList()
        val leakage = all.flatMap { source ->
            source.readLines().mapIndexedNotNull { index, line ->
                if (line.contains("com.warawerse") || line.contains("WaraWerse") || line.contains("GameRules") || line.contains("feature.game")) {
                    "${source.path}:${index + 1}: $line"
                } else null
            }
        }
        require(leakage.isEmpty()) { "WaraWerse product leakage detected:\n${leakage.joinToString("\n")}" }
    }
}

/** Código de producción como pares (ruta relativa, contenido). */
fun productionKotlinPairs(): List<Pair<String, String>> =
    productionKotlinSources().toList().map { it.relativeTo(rootDir).path to it.readText() }

/** Lector de archivos por ruta relativa al repositorio (null si no existe). */
fun repoRead(relativePath: String): String? =
    file(relativePath).takeIf { it.isFile }?.readText()

private val screenContractDocPath = "docs/04-ux/03-contrato-interaccion-pantallas.md"

/**
 * Galería estricta por superficie (`<superficie>--<estado>--<ancho>dp--<tema>.png`),
 * único artefacto de capturas. Se genera con `-Pagendaqr.capture=true` y se
 * valida contra el contrato por pantalla (ADR-0007).
 */
val captureGalleryDir = "build/captures/gallery"

/** Recolecta capturas PNG declaradas bajo build/ (nunca versionadas). */
fun collectCaptureArtifacts(): Pair<List<CaptureArtifact>, List<String>> {
    val artifacts = mutableListOf<CaptureArtifact>()
    val malformed = mutableListOf<String>()
    val galleryMarker = "/${captureGalleryDir.removePrefix("build/")}/"
    listOf("feature/destinations/presentation", "androidApp", "core/ui").forEach { module ->
        val dir = file("$module/build")
        if (!dir.exists()) return@forEach
        dir.walkTopDown()
            .filter { it.isFile && it.extension == "png" && it.path.contains(galleryMarker) }
            .forEach { png ->
                val rel = png.relativeTo(rootDir).path
                val name = parseCaptureName(png.name)
                val bytes = png.readBytes()
                val dimensions = if (name == null) null else pngDimensions(bytes)
                when {
                    name == null || dimensions == null ->
                        malformed += "$rel (no cumple el esquema de nombre o no es un PNG legible)"
                    else -> artifacts += CaptureArtifact(
                        relPath = rel,
                        name = name,
                        widthPx = dimensions.first,
                        heightPx = dimensions.second,
                        sha256 = sha256(bytes),
                    )
                }
            }
    }
    return artifacts to malformed
}

tasks.register("generateScreenContractDoc") {
    group = "verification"
    description = "Renders docs/04-ux/03-contrato-interaccion-pantallas.md from the structured contract."
    doLast {
        val target = file(screenContractDocPath)
        target.parentFile.mkdirs()
        target.writeText(renderScreenContractDoc(ScreenContracts.all))
        logger.lifecycle("generateScreenContractDoc: wrote ${screenContractDocPath}")
    }
}

tasks.register("generateCaptureManifest") {
    group = "verification"
    description = "Writes the on-demand capture manifest (build/captures/manifest.json) from existing PNGs."
    doLast {
        val (artifacts, malformed) = collectCaptureArtifacts()
        malformed.forEach { logger.warn("generateCaptureManifest: artefacto malformado: $it") }
        val target = file(captureManifestPath)
        target.parentFile.mkdirs()
        target.writeText(renderCaptureManifest(artifacts, ScreenContracts.all))
        logger.lifecycle(
            "generateCaptureManifest: wrote $captureManifestPath (${artifacts.size} capturas)",
        )
    }
}

/**
 * Galería estricta en un solo comando (ADR-0007): produce las
 * capturas (tests con `-Pagendaqr.capture=true`), su manifiesto y el
 * índice navegable. Requiere el flag explícito; nada se versiona y el
 * gate (`verifyScreenContract`) valida la galería cuando existe.
 */
tasks.register("captureGallery") {
    group = "verification"
    description = "Produces the strict per-surface gallery plus its manifest on demand (requires -Pagendaqr.capture=true)."
    dependsOn(":feature:destinations:presentation:captureScreenshots", ":generateCaptureManifest")
    doLast {
        val (artifacts, malformed) = collectCaptureArtifacts()
        malformed.forEach { logger.warn("captureGallery: artefacto malformado: $it") }
        val byWidth = artifacts.groupBy { captureWidthDp }
            .entries.sortedBy { it.key }
            .joinToString { "${it.value.size} a ${it.key}dp" }
        val byTheme = artifacts.groupBy { it.name.theme }
            .entries.sortedBy { it.key }
            .joinToString { "${it.value.size} en ${it.key}" }
        logger.lifecycle(
            "captureGallery: ${artifacts.size} capturas estrictas ($byWidth; $byTheme); " +
                "manifiesto en $captureManifestPath",
        )
        // La galería no puede degradarse en silencio: si falta algún estado
        // declarado de alguna superficie (p. ej. un escenario omitido porque
        // el viewport no medía 360 dp), el comando falla aquí mismo, no solo
        // en verifyScreenContract.
        val incomplete = captureViolations(artifacts, ScreenContracts.all)
            .filter { it.startsWith("galeria-") || it.startsWith("captura-") }
        if (incomplete.isNotEmpty()) {
            throw GradleException(
                "captureGallery: galería incompleta o inválida (${incomplete.size}):\n" +
                    incomplete.take(40).joinToString("\n") { "  $it" },
            )
        }
    }
}

// El manifiesto se escribe DESPUÉS de que los tests produzcan la galería.
tasks.named("generateCaptureManifest") {
    mustRunAfter(":feature:destinations:presentation:captureScreenshots")
}

tasks.register("verifyScreenContract") {
    group = "verification"
    description = "Cross-checks the structured per-screen contract against code, tests and generated doc."
    doLast {
        val production = productionKotlinPairs()
        val violations = screenContractViolations(ScreenContracts.all, ::repoRead, production).toMutableList()
        violations += exceptionUsageViolations(exceptionRegistry, production)

        val rendered = renderScreenContractDoc(ScreenContracts.all)
        val committed = file(screenContractDocPath).takeIf { it.isFile }?.readText()
        if (screenContractDocIsStale(committed, rendered)) {
            violations += "contrato-doc-desactualizado: $screenContractDocPath difiere del contrato; " +
                "ejecuta ./gradlew generateScreenContractDoc"
        }

        val (artifacts, malformed) = collectCaptureArtifacts()
        malformed.forEach { violations += "captura-nombre: $it" }
        if (artifacts.isNotEmpty()) {
            violations += captureViolations(artifacts, ScreenContracts.all)
            val manifest = file(captureManifestPath).takeIf { it.isFile }?.readText()
            violations += captureManifestViolations(artifacts, ScreenContracts.all, manifest)
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                "Screen contract violations (${violations.size}):\n" +
                    violations.take(40).joinToString("\n") { "  $it" } +
                    if (violations.size > 40) "\n  ... and ${violations.size - 40} more" else ""
            )
        }
        logger.lifecycle(
            "verifyScreenContract: PASS (${ScreenContracts.all.size} superficies; " +
                "${artifacts.size} capturas verificadas)"
        )
    }
}

tasks.register("verifyScreenContractFixtures") {
    group = "verification"
    description = "Self-test: the contract validator must fail on each divergence class."
    doLast {
        val destinationsFile =
            "feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/DestinationsState.kt"
        val appRouteFile =
            "feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/AppRoute.kt"
        val repo = mapOf(
            appRouteFile to "sealed interface AppRoute {\n    data object Home : AppRoute\n}",
            destinationsFile to "sealed interface DestinationRoute {\n" +
                "    data object List : DestinationRoute\n    data object Add : DestinationRoute\n}",
            "src/Home.kt" to "class HomeSurface",
            "src/Add.kt" to "class AddDestinationScreen",
            "tests/HomeTest.kt" to "class HomeTest { fun renders() {} }",
            "tests/AddTest.kt" to "class AddTest { fun renders() {} }",
            "a11y/HomeA11yTest.kt" to "fun t() { node.contentDescription == null }",
            "a11y/AddA11yTest.kt" to "fun t() { node.assertIsEnabled() }",
            "a11y/WeakA11yTest.kt" to "class Weak { fun nothing() {} }",
        )
        val read: (String) -> String? = { repo[it] }
        val core = listOf(
            "core/ui/AqButtons.kt" to "fun AqPrimaryButton() = Unit",
            "core/ui/AqLists.kt" to "fun AqListRow() = Unit",
        )
        fun screen(
            id: String,
            surface: String,
            kind: SurfaceKind,
            route: String,
            covers: List<String>,
            source: String,
            tests: List<String>,
            a11y: List<String>,
            destinations: List<String> = listOf("system-exit"),
            components: List<String> = listOf("AqPrimaryButton"),
            states: List<String> = listOf("content"),
            captureStates: List<String> = listOf("content"),
            gestures: List<String> = emptyList(),
        ) = ScreenContract(
            id = id, surface = surface, title = surface, kind = kind, route = route, covers = covers,
            source = source, entryPoints = listOf("entry"), exits = listOf("system-exit"),
            destinations = destinations, primaryAction = null, primaryActionKey = null,
            states = states, gestures = gestures, captureStates = captureStates,
            components = components, a11yTests = a11y, tests = tests,
        )
        val valid = listOf(
            screen(
                "S01", "home", SurfaceKind.Root, "AppRoute.Home",
                listOf("AppRoute.Home", "DestinationRoute.List"), "src/Home.kt",
                listOf("tests/HomeTest.kt"), listOf("a11y/HomeA11yTest.kt"),
                destinations = listOf("DestinationRoute.Add", "system-exit"),
            ),
            screen(
                "S02", "add", SurfaceKind.Inner, "DestinationRoute.Add",
                listOf("DestinationRoute.Add"), "src/Add.kt",
                listOf("tests/AddTest.kt"), listOf("a11y/AddA11yTest.kt"),
            ),
        )
        require(screenContractViolations(valid, read, core).isEmpty()) {
            "el contrato sintético válido no debe fallar: ${screenContractViolations(valid, read, core)}"
        }

        // Pantalla real sin contrato.
        require(screenContractViolations(listOf(valid[0]), read, core).isNotEmpty()) {
            "una ruta sin contrato debe fallar"
        }
        // Destino inexistente.
        require(
            screenContractViolations(listOf(valid[0].copy(destinations = listOf("AppRoute.Nope")), valid[1]), read, core)
                .isNotEmpty(),
        ) { "un destino inexistente debe fallar" }
        // Referencia de prueba inexistente.
        require(
            screenContractViolations(listOf(valid[0].copy(tests = listOf("tests/Missing.kt")), valid[1]), read, core)
                .isNotEmpty(),
        ) { "una prueba inexistente debe fallar" }
        // Pantalla sin cobertura a11y real.
        require(
            screenContractViolations(listOf(valid[0].copy(a11yTests = listOf("a11y/WeakA11yTest.kt")), valid[1]), read, core)
                .isNotEmpty(),
        ) { "una prueba a11y sin señal debe fallar" }
        // Componente inventado.
        require(
            screenContractViolations(listOf(valid[0].copy(components = listOf("AqNonexistent")), valid[1]), read, core)
                .isNotEmpty(),
        ) { "un componente inexistente debe fallar" }
        // Estado fuera del vocabulario.
        require(
            screenContractViolations(listOf(valid[0].copy(states = listOf("weird")), valid[1]), read, core)
                .isNotEmpty(),
        ) { "un estado fuera del vocabulario debe fallar" }

        // Excepción documentada sin uso (registro zombie).
        val zombie = exceptionUsageViolations(mapOf("E-99" to listOf("border:AqLists.kt")), core)
        require(zombie.isNotEmpty()) { "una excepción sin uso debe fallar" }
        val used = exceptionUsageViolations(
            mapOf("E-01" to listOf("border:AqButtons.kt")),
            listOf("core/ui/AqButtons.kt" to "Modifier.border(BorderStroke(1.dp, color), shape)"),
        )
        require(used.isEmpty()) { "una excepción usada no debe fallar: $used" }

        // Capturas: esquema de nombre.
        val parsed = parseCaptureName("home--content--light.png")
        require(parsed != null && parsed.surface == "home" && parsed.widthDp == captureWidthDp &&
            parsed.state == "content" && parsed.theme == "light") {
            "el esquema de nombre válido debe parsear con el ancho fijo"
        }
        require(parseCaptureName("account-dark.png") == null) {
            "un nombre ambiguo debe rechazarse"
        }
        require(parseCaptureName("home--content--light--font200.png") == null) {
            "un sufijo de escala de fuente debe rechazarse"
        }
        require(parseCaptureName("home--content--360dp--light.png") == null) {
            "un ancho redundante en el nombre debe rechazarse"
        }
        require(parseCaptureName("home--content--720dp--light.png") == null) {
            "un viewport de tablet debe rechazarse"
        }
        require(captureWidthDp == 360) {
            "la galería solo admite 360 dp"
        }
        // Dimensiones reales desde la cabecera PNG.
        fun fakePng(width: Int, height: Int): ByteArray {
            val bytes = ByteArray(24)
            intArrayOf(137, 80, 78, 71, 13, 10, 26, 10).forEachIndexed { i, v -> bytes[i] = v.toByte() }
            bytes[11] = 13
            "IHDR".forEachIndexed { i, c -> bytes[12 + i] = c.code.toByte() }
            fun put(offset: Int, value: Int) {
                bytes[offset] = (value ushr 24).toByte()
                bytes[offset + 1] = (value ushr 16).toByte()
                bytes[offset + 2] = (value ushr 8).toByte()
                bytes[offset + 3] = value.toByte()
            }
            put(16, width); put(20, height)
            return bytes
        }
        val dims = pngDimensions(fakePng(640, 800))
        require(dims == (640 to 800)) { "pngDimensions debe leer el IHDR: $dims" }
        require(pngDimensions(ByteArray(24)) == null) { "una cabecera inválida debe ser null" }

        fun artifact(path: String, px: Int, hash: String): CaptureArtifact =
            CaptureArtifact(path, parseCaptureName(path.substringAfterLast('/'))!!, px, 800, hash)
        val screens = listOf(valid[0], valid[1])
        // Densidad no soportada (viewport falso / zoom).
        require(
            captureViolations(
                listOf(artifact("a/home--content--light.png", 641, "h1")),
                screens,
            ).any { it.contains("captura-zoom") },
        ) { "una densidad no soportada debe fallar" }
        // Duplicado byte-exacto y misma configuración.
        val repeated = captureViolations(
            listOf(
                artifact("a/home--content--light.png", 360, "same"),
                artifact("b/home--content--light.png", 360, "same"),
            ),
            screens,
        )
        require(repeated.any { it.contains("captura-duplicada") }) { "un duplicado byte-exacto debe fallar" }
        require(repeated.any { it.contains("captura-repetida") }) { "una configuración repetida debe fallar" }
        // Captura de superficie inexistente.
        require(
            captureViolations(
                listOf(artifact("a/ghost--content--light.png", 360, "h")),
                screens,
            ).any { it.contains("captura-orbita") },
        ) { "una superficie inexistente debe fallar" }

        // Manifiesto: ausente con artefactos presentes.
        val someArtifacts = listOf(
            artifact("a/home--content--light.png", 360, "h1"),
            artifact("a/add--content--light.png", 360, "h2"),
        )
        require(
            captureManifestViolations(someArtifacts, screens, null).any { it.contains("manifiesto-ausente") },
        ) { "capturas sin manifiesto deben fallar" }
        val manifest = renderCaptureManifest(someArtifacts, screens)
        require(captureManifestViolations(someArtifacts, screens, manifest).isEmpty()) {
            "un manifiesto sincronizado no debe fallar"
        }
        require(
            captureManifestViolations(someArtifacts, screens, "$manifest ").any {
                it.contains("manifiesto-desactualizado")
            },
        ) { "un manifiesto desincronizado debe fallar" }

        // Documentación generada desincronizada.
        val rendered = renderScreenContractDoc(valid)
        require(screenContractDocIsStale("algo distinto\n", rendered)) {
            "un documento desincronizado debe detectarse"
        }
        require(!screenContractDocIsStale(rendered, rendered)) {
            "un documento idéntico no debe marcarse como desactualizado"
        }

    }
}

/**
 * Cruza los contratos de transición UX (UxTransitions) contra la lista de
 * pantallas declaradas en ScreenContracts y falla si hay orígenes o
 * destinos no declarados, transiciones duplicadas o pantallas críticas sin
 * transiciones definidas.
 */
tasks.register("verifyUxTransitionContracts") {
    group = "verification"
    description = "Valida que los contratos de transición UX referencien solo pantallas declaradas y sin duplicados."
    doLast {
        val violations = mutableListOf<String>()
        violations += com.agendaqr.buildlogic.ux.validarTransiciones(com.agendaqr.buildlogic.ux.UxTransitions)
        // Toda pantalla con contrato debe aparecer como origen o destino de al menos una transición.
        val referenciadas = com.agendaqr.buildlogic.ux.UxTransitions.flatMap { t ->
            listOf(t.pantallaOrigen) + (t.pantallaDestino?.let { listOf(it) } ?: emptyList())
        }.toSet()
        val noReferenciadas = com.agendaqr.buildlogic.ux.pantallasValidas - referenciadas
        noReferenciadas.sorted().forEach {
            violations.add("pantalla-sin-transiciones: '$it' no aparece en ninguna transición UX declarada")
        }
        // Las transiciones críticas (guardar/cancelar/eliminar/reintentar/recuperar/error/bloquea-al-guardar)
        // deben existir para las pantallas que las declaran como acción primaria o estado saving/error.
        if (violations.isNotEmpty()) {
            throw GradleException(
                "UX transition contract violations (${violations.size}):\n" +
                    violations.joinToString("\n") { "  $it" },
            )
        }
        logger.lifecycle(
            "verifyUxTransitionContracts: PASS (${com.agendaqr.buildlogic.ux.UxTransitions.size} transiciones; " +
                "${com.agendaqr.buildlogic.ux.transicionesCriticas.size} críticas)"
        )
    }
}

/**
 * Mide el legado de Polaris que queda en TODO el repo (core/ui y tests
 * incluidos). Ahora que el conteo es cero, forma parte de
 * `verifyAgendaQrArchitecture` (ADR-0041).
 */
tasks.register("verifyNoLegacyAnywhere") {
    group = "verification"
    description = "Fails if any legacySymbols entry is declared or used anywhere in Kotlin sources."
    doLast {
        val violations = mutableListOf<String>()
        val symbolRegexes = (legacySymbols + "AqShapes").distinct().map { it to Regex("\\b$it\\b") }
        sequenceOf("androidApp", "core", "feature", "shared", "iosApp").forEach { root ->
            val directory = file(root)
            if (!directory.exists()) return@forEach
            directory.walkTopDown()
                .filter { it.isFile && it.extension == "kt" && !it.path.contains("/build/") }
                .forEach { source ->
                    source.readLines().forEachIndexed { index, line ->
                        val trimmed = line.trim()
                        if (trimmed.startsWith("*") || trimmed.startsWith("//") || trimmed.startsWith("/*")) return@forEachIndexed
                        symbolRegexes.forEach { (symbol, regex) ->
                            if (regex.containsMatchIn(line)) {
                                violations.add("${source.relativeTo(rootDir).path}:${index + 1}: legacy '$symbol'")
                            }
                        }
                    }
                }
        }
        require(violations.isEmpty()) {
            "Legacy residual (${violations.size} referencias):\n" + violations.take(200).joinToString("\n")
        }
    }
}

tasks.register("verifyAgendaQrArchitecture") {
    group = "verification"
    dependsOn(
        "verifyDesignSystemCompliance",
        "verifyLayoutGeometry",
        "verifyArchitectureBoundaries",
        "verifyDesignSystemFixtures",
        "verifyNoScripts",
        "verifyControlMinSize",
        "verifyDocs",
        "verifyScreenContract",
        "verifyScreenContractFixtures",
        "verifyUxTransitionContracts",
        "verifyNoLegacyAnywhere",
    )
    description = "Runs the complete Agenda QR architecture and design-system gates."
}

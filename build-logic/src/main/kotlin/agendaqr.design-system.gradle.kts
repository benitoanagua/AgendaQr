import java.io.File

fun Project.productionKotlinSources(): Sequence<File> = sequence {
    sequenceOf("androidApp", "core", "feature", "shared").forEach { root ->
        val directory = file(root)
        if (directory.exists()) {
            yieldAll(directory.walkTopDown().filter {
                it.isFile && it.extension == "kt" &&
                    !it.path.contains("/build/") &&
                    !it.path.contains("/src/test/") &&
                    !it.path.contains("/src/androidTest/") &&
                    // Los source sets de test (commonTest, androidUnitTest,
                    // iosTest…) no son código de producción: las pruebas
                    // pueden usar primitivas (p. ej. 48.dp para verificar
                    // touch targets) sin violar la autoridad visual.
                    !Regex("/src/[a-zA-Z]*[Tt]est/").containsMatchIn(it.path)
            })
        }
    }
}

fun visualSources(): List<File> = productionKotlinSources().toList()

/**
 * Reglas por línea, parametrizadas por ruta: así la tarea de fixtures
 * puede auto-verificar el guard con contenido sintético.
 */
/**
 * D2 — allowlist de literales de UI permitidos en feature/: solo
 * identificadores técnicos (ids, prefijos, MIME, rutas), nunca copy.
 */
val uiLiteralAllowlist = listOf(
    "image/png", "image/jpeg", "image/webp", "application/pdf",
    "file://", "ctx-", "operation-", "destination-", "comprobante-",
    "import-", "m-", "u-", "qr-", "d-", "photo-", "Revisar ",
)

fun isUiLiteralAllowed(text: String): Boolean =
    uiLiteralAllowlist.any { text.contains(it) }

/**
 * Contrato §12 — REGISTRO DE EXCEPCIONES del gate. Cada ID E-xx declarado
 * en la tabla del contrato debe existir AQUÍ con su aplicación; la tarea
 * verifyDesignSystemCompliance compara AMBOS conjuntos y falla si no
 * coinciden ("la lista de excepciones del gate DEBE coincidir con esta
 * tabla"). No existe excepción válida sin entrada en ambas partes.
 *
 * - "border:" — el/los archivos donde esa excepción permite `.border(`/`BorderStroke(`.
 * - otros — excepciones sin borde (marcador/chrome): su aplicación es un
 *   componente + prueba, no un archivo con borde.
 */
val exceptionRegistry: Map<String, List<String>> = mapOf(
    "E-01" to listOf(
        "border:XauxaAppBar.kt",
        "border:XauxaCommandBar.kt",
    ),
    "E-02" to listOf("border:XauxaFeedback.kt"),
    "E-03" to listOf("border:XauxaFeedback.kt"),
    "E-05" to listOf("marker:XauxaListRow — barra lateral solo con tono no-Neutral o acento de contexto real (componente + Robolectric)"),
    "E-06" to listOf("border:XauxaInputs.kt"),
    "E-07" to listOf("chrome:AuthScreen sin barra inferior (capturas login claro/oscuro)"),
)

/** Archivos con `.border(`/`BorderStroke(` permitidos por las excepciones. */
val borderAllowedFiles: Set<String> =
    buildSet {
        exceptionRegistry.values.forEach { rules ->
            rules.forEach { rule -> if (rule.startsWith("border:")) add(rule.removePrefix("border:")) }
        }
        // Bordes de ESTADO del contrato §4.2/§7 (anillo de foco) — no son
        // excepciones: son la implementación canónica del foco visible.
        add("XauxaTone.kt")
    }

fun kotlinDesignViolations(path: String, lines: List<String>): List<String> = buildList {
    // M12/§4.5: SOLO Lucide vía XauxaIcon — los APIs de iconos de Material
    // están prohibidos en producción (Filled y Default son el mismo set;
    // Outlined/Rounded/Sharp/TwoTone/AutoMirrored).
    val bannedImports = listOf(
        "androidx.compose.material.icons",
        "Icons.Filled", "Icons.Outlined", "Icons.Rounded", "Icons.Default",
        "Icons.AutoMirrored", "Icons.Sharp", "Icons.TwoTone",
    )
    val rawHex = Regex("#[0-9A-Fa-f]{6,8}")
    val rawDp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.dp\\b")
    val rawSp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.sp\\b")
    val rawColor = Regex("\\bColor\\s*\\(")
    val forbiddenShapes = listOf("RoundedCornerShape", "CutCornerShape", "shadow(", ".shadow(")
    val forbiddenVisualAuthority = listOf("MaterialTheme.colorScheme")
    // Fase 4: las formas con esquinas solo existen en la capa de tokens
    // (XauxaShapeFlat, la única, aplanada a 0) y en XauxaTheme.kt como red
    // de seguridad. Fuera de ahí, ni siquiera aplanadas: radio 0 se dice
    // con RectangleShape.
    val shapesAllowedFiles = listOf("XauxaTokens.kt", "XauxaTheme.kt")
    // T12 — la capa feature compone Xauxa; los componentes Material solo
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
    lines.forEachIndexed { index, line ->
        val location = "$path:${index + 1}"
        if (bannedImports.any(line::contains)) add("$location: forbidden Material icon API: $line")
        if (!path.endsWith("XauxaTokens.kt") && rawHex.containsMatchIn(line)) add("$location: raw hex outside token layer: $line")
        if (!path.endsWith("XauxaTokens.kt") && rawDp.containsMatchIn(line)) add("$location: raw dp outside token layer: $line")
        if (!path.endsWith("XauxaTokens.kt") && rawSp.containsMatchIn(line)) add("$location: raw sp outside token layer: $line")
        if (!path.endsWith("XauxaTokens.kt") && rawColor.containsMatchIn(line)) add("$location: raw Color constructor outside token layer: $line")
        if (shapesAllowedFiles.none { path.endsWith(it) } && forbiddenShapes.any(line::contains)) {
            add("$location: forbidden radius/elevation API: $line")
        }
        // verify-xauxa.sh parity: CircleShape/RectangleShape también están
        // prohibidos FUERA de la capa de tokens/tema — en componentes de
        // core:ui y en feature/, shared/, androidApp/. El lab exhibe
        // tokens (entorno de validación, no producto): exento.
        if (!path.endsWith("XauxaTokens.kt") && !path.endsWith("XauxaTheme.kt") &&
            !path.contains("/lab/") &&
            (line.contains("CircleShape") || line.contains("RectangleShape"))
        ) {
            add("$location: production code must use XauxaShape/token API (no CircleShape/RectangleShape): $line")
        }
        // Contrato §5: colores nombrados de Compose (Color.White/Black/
        // Transparent/…) fuera de XauxaTokens.kt — todo literal visual va
        // al token semántico (QrCanvas, Transparent, TextPrimary…).
        if (!path.endsWith("XauxaTokens.kt") &&
            Regex("\\bColor\\.(Black|White|Transparent|Red|Gray|LightGray|DarkGray|Yellow|Green|Blue|Cyan|Magenta|Unspecified)\\b")
                .containsMatchIn(line)
        ) {
            add("$location: Color.<name> outside token layer (use a semantic token from XauxaTokens.kt): $line")
        }
        // Contrato §7: duraciones literales en animaciones — toda
        // animación consume XauxaMotion (tween(150), durationMillis=150 o
        // delayMillis=150 locales diluyen el lenguaje de movimiento).
        val literalDuration = Regex("\\btween\\(\\s*\\d|\\bdurationMillis\\s*=\\s*\\d|\\bdelayMillis\\s*=\\s*\\d")
        if (!path.endsWith("XauxaTokens.kt") && literalDuration.containsMatchIn(line)) {
            add("$location: raw animation duration; use XauxaMotion tokens: $line")
        }
        if (!path.endsWith("XauxaTheme.kt") && forbiddenVisualAuthority.any(line::contains)) add("$location: MaterialTheme cannot be the visual authority outside XauxaTheme: $line")
        // Contrato §12: `.border(`/`BorderStroke(` solo en archivos de las
        // EXCEPCIONES registradas (borderAllowedFiles) y del estado de foco
        // (§4.2). El laboratorio exhibe tokens (entorno de validación, no
        // producto): exento.
        val hasBorderCall = line.contains(".border(") || Regex("\\bBorderStroke\\s*\\(").containsMatchIn(line)
        if (hasBorderCall && !path.contains("/lab/") &&
            borderAllowedFiles.none { path.endsWith(it) }
        ) {
            add("$location: .border(/BorderStroke( outside §12 exception files (register the exception or remove the border): $line")
        }
        // verify-xauxa.sh parity: material3/colorScheme prohibido en
        // feature/, shared/src y androidApp/src (solo core:ui puede adaptarlo).
        val isAppSource = isFeatureSource || path.contains("/shared/src/") || path.contains("/androidApp/src/")
        if (isAppSource && (bannedMaterialInFeature.any(line::contains) ||
                line.contains("androidx.compose.material3.") ||
                line.contains("MaterialTheme.colorScheme"))
        ) {
            add("$location: app/feature code must use core:ui and Xauxa tokens: $line")
        }
        // D2 — copy de UI en feature/ debe salir de AppStrings.
        if (isFeatureSource) {
            val uiCall = Regex("(XauxaText|XauxaHeading|XauxaEmptyState|XauxaStatusBanner|XauxaPrimaryButton|XauxaSecondaryButton|XauxaTextAction|XauxaDialog)\\(\\s*\"([^\"]+)\"").find(line)
            if (uiCall != null && !isUiLiteralAllowed(uiCall.groupValues[2])) {
                add("$location: UI string literal outside AppStrings (D2): ${uiCall.groupValues[2]}")
            }
            val namedArg = Regex("(title|subtitle|actionLabel|label|placeholder|confirmLabel|dismissLabel|message|text|hint) = \"([^\"]+)\"").find(line)
            if (namedArg != null && !isUiLiteralAllowed(namedArg.groupValues[2])) {
                add("$location: UI string literal outside AppStrings (D2): ${namedArg.groupValues[2]}")
            }
            // Fase 4(a): copy dentro de EXPRESIONES también es copy —
            // `label = if (...) "Texto" else null` y variantes con when
            // esquivaban la regla anterior.
            val expressionLiteral = Regex(
                "(title|subtitle|actionLabel|label|placeholder|confirmLabel|dismissLabel|message|text|hint|errorMessage|helperMessage|backLabel|busyDescription) = (?:if|when)[^\n]*?\"([^\"]{2,})\"",
            ).find(line)
            if (expressionLiteral != null && !isUiLiteralAllowed(expressionLiteral.groupValues[2])) {
                add("$location: UI string literal in expression outside AppStrings (D2): ${expressionLiteral.groupValues[2]}")
            }
            // Fase 4(b): la interacción cruda es cosa del design system —
            // feature/ compone componentes Xauxa, nunca clickable propio
            // (feedback de pulsación, rol, foco y semántica viven en el DS).
            if (line.contains(".clickable(")) {
                add("$location: raw .clickable( in feature/ (compose a Xauxa component): $line")
            }
            if (line.contains("indication = null")) {
                add("$location: raw indication override in feature/ (feedback lives in the DS): $line")
            }
        }
        // D2 extendida: copy en EXPRESIONES que escapaban al regex —
        // ifBlank { "…" }, Elvis `?: "frase"` y plantillas "…$var" en la
        // capa de UI (presentation). No disparan: comentarios, mensajes de
        // desarrollador (throw/require/check), defaults técnicos
        // ("bin"/"new"/MIME vía la allowlist) y plantillas que SOLO
        // interpolan variables (pegamento puro como " $index").
        if (path.contains("/presentation/")) {
            val trimmed = line.trim()
            val isComment = trimmed.startsWith("//") || trimmed.startsWith("*") || trimmed.startsWith("/*")
            val isDevMessage = line.contains("throw ") || line.contains("require(") ||
                line.contains("check(") || line.contains("IllegalStateException") ||
                // println/log de desarrollador: no es copy de UI.
                line.contains("println(") || line.contains(".printStackTrace")
            if (!isComment && !isDevMessage) {
                Regex("ifBlank\\s*\\{\\s*\"([^\"]+)\"").find(line)?.let { match ->
                    val literal = match.groupValues[1]
                    if (!isUiLiteralAllowed(literal)) {
                        add("$location: UI literal in ifBlank{} must come from AppStrings (D2): $literal")
                    }
                }
                Regex("\\?:\\s*\"([^\"]+)\"").findAll(line).forEach { match ->
                    val literal = match.groupValues[1]
                    // Solo frases de texto (letras + espacio): los defaults
                    // técnicos ("bin", "new", "image/png") no son copy.
                    val looksLikeCopy = literal.contains(" ") && literal.any { it.isLetter() }
                    if (looksLikeCopy && !isUiLiteralAllowed(literal)) {
                        add("$location: UI literal in Elvis expression must come from AppStrings (D2): $literal")
                    }
                }
                Regex("\"([^\"]*\\$\\{?\\w+[^\"]*)\"").findAll(line).forEach { match ->
                    val literal = match.groupValues[1]
                    // Quitar TODOS los segmentos interpolados: si lo que
                    // queda no tiene letras, no hay copy que centralizar
                    // ("qr:$id", "$what\n$dataStatus", " $index"…).
                    val withoutVariables = Regex("\\$\\{?[A-Za-z_][A-Za-z0-9_.]*}?").replace(literal, "")
                    val looksLikeCopy = withoutVariables.any { it.isLetter() }
                    if (looksLikeCopy && !isUiLiteralAllowed(literal)) {
                        add("$location: UI string template must come from AppStrings (D2): $literal")
                    }
                }
            }
        }
        // Ronda 2 (Área H/ADR-0008): core/ui no hardcodea COPY: en
        // components/ todo texto de UI llega como parámetro del llamador.
        // Excepciones: mensajes require/error (desarrollador), labels de
        // animación "xauxa_*" y glifos de un carácter (×, ⋮, ⧉).
        if (path.contains("/core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/")) {
            val trimmed = line.trim()
            val isDevMessage = line.contains("require(") || line.contains("error(") || line.contains("check(")
            // Comentarios (KDoc/linea) y CONTINUACIONES de strings previos
            // (mensajes require multilínea) no son copy de UI.
            val isComment = trimmed.startsWith("*") || trimmed.startsWith("//") || trimmed.startsWith("/*")
            val isContinuation = trimmed.startsWith("\"")
            if (!isDevMessage && !isComment && !isContinuation) {
                Regex("\"([^\"]{3,})\"").findAll(line).forEach { match ->
                    val text = match.groupValues[1]
                    val isAnimationLabel = text.startsWith("xauxa_")
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
    return violations
}

/**
 * verify-xauxa.sh parity: el mínimo interactivo canónico es 48dp.
 * Si alguien baja este valor, la accesibilidad se rompe (§11).
 */
tasks.register("verifyControlMinSize") {
    group = "verification"
    description = "XauxaMetrics.ControlMinSize must remain 48.dp."
    doLast {
        val tokens = file("core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/XauxaTokens.kt")
        if (!tokens.exists()) return@doLast
        val content = tokens.readText()
        if (!content.contains("val ControlMinSize = 48.dp")) {
            throw GradleException("Xauxa gate failed: XauxaMetrics.ControlMinSize must remain 48.dp.")
        }
    }
}

/**
 * A.6 Guardia permanente: ningún script sh/py/js/mjs/ts/ps1/bat fuera de
 * la lista de excepciones (gradlew, gradlew.bat, salidas en build/).
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
 * A.2: Visual hash tasks (replaces docs/04-ux/visual-hashes/verify.sh).
 * SHA-256 of each Roborazzi PNG compared against manifest.json (text).
 * SENSIBLE AL ENTORNO (ADR-0007): el render depende del JDK/OS; usar en
 * el mismo entorno que generó el manifest o regenerarlo.
 */
tasks.register("recordVisualHashes") {
    group = "verification"
    description = "Regenerates SHA-256 manifest from Roborazzi captures."
    val outputDir = file("feature/destinations/presentation/build/roborazzi")
    val manifestFile = file("docs/04-ux/visual-hashes/manifest.json")
    // SIN outputs.file: verifyVisualHashes lee el manifiesto como input;
    // declararlo como output de ESTA tarea haría inválida la ejecución
    // conjunta `recordVisualHashes verifyVisualHashes` (validación de
    // dependencias implícitas de Gradle). Regenerar es una acción manual
    // deliberada (ADR-0007): sin up-to-date, siempre reescribe.
    doLast {
        if (!outputDir.exists()) {
            throw GradleException("No Roborazzi output at ${outputDir}. Run :feature:destinations:presentation:recordRoborazzi first.")
        }
        val hashes = sortedMapOf<String, String>()
        outputDir.listFiles()?.filter { it.extension == "png" }?.sortedBy { it.name }?.forEach { png ->
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            png.inputStream().use { input ->
                val buf = ByteArray(8192)
                var read: Int
                while (input.read(buf).also { read = it } > 0) {
                    digest.update(buf, 0, read)
                }
            }
            hashes[png.name] = digest.digest().joinToString("") { "%02x".format(it) }
        }
        manifestFile.parentFile.mkdirs()
        manifestFile.writeText(
            hashes.entries.joinToString("", "{\n", "\n}\n") { (k, v) ->
                "  \"${k}\": \"${v}\"${if (k == hashes.keys.last()) "" else ","}\n"
            },
        )
        logger.lifecycle("recordVisualHashes: ${hashes.size} hashes written to ${manifestFile.path}")
    }
}

tasks.register("verifyVisualHashes") {
    group = "verification"
    description = "Compares Roborazzi captures against the SHA-256 manifest (ADR-0007)."
    val outputDir = file("feature/destinations/presentation/build/roborazzi")
    val manifestFile = file("docs/04-ux/visual-hashes/manifest.json")
    inputs.dir(outputDir)
    inputs.file(manifestFile)
    doLast {
        if (!manifestFile.exists()) {
            throw GradleException("Manifest not found: ${manifestFile}. Run recordVisualHashes first.")
        }
        if (!outputDir.exists()) {
            throw GradleException("No Roborazzi output at ${outputDir}. Run :feature:destinations:presentation:recordRoborazzi first.")
        }
        val expected = mutableMapOf<String, String>()
        manifestFile.readLines().forEach { line ->
            val m = Regex("\"([^\"]+)\": \"([a-f0-9]+)\"").find(line)
            if (m != null) expected[m.groupValues[1]] = m.groupValues[2]
        }
        val actual = mutableMapOf<String, String>()
        outputDir.listFiles()?.filter { it.extension == "png" }?.forEach { png ->
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            png.inputStream().use { input ->
                val buf = ByteArray(8192)
                var read: Int
                while (input.read(buf).also { read = it } > 0) {
                    digest.update(buf, 0, read)
                }
            }
            actual[png.name] = digest.digest().joinToString("") { "%02x".format(it) }
        }
        val missing = (expected.keys - actual.keys).sorted()
        val extra = (actual.keys - expected.keys).sorted()
        val changed = expected.filter { (k, v) -> k in actual && actual[k] != v }.keys.sorted()
        if (missing.isNotEmpty() || extra.isNotEmpty() || changed.isNotEmpty()) {
            val sb = StringBuilder("VISUAL_HASHES=FAIL\n")
            missing.forEach { sb.append("  - missing: $it\n") }
            extra.forEach { sb.append("  - new: $it\n") }
            changed.forEach { sb.append("  - pixel changed: $it\n") }
            throw GradleException(sb.toString())
        }
        logger.lifecycle("VISUAL_HASHES=PASS (${actual.size} captures)")
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
                Regex(pattern).findAll(content).forEach { match ->
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
        // (producción): "Fase N", "Ronda N", "P0/P1", "T<N>"/"tsN" como
        // historia — el porqué permanente se conserva, el sello de la
        // pasada no (la historia vive en git log). Se inspeccionan las
        // líneas de comentario y el segmento tras "//" de las de código.
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
    description = "Enforces Xauxa Design System invariants in production Kotlin sources."
    doLast {
        val violations = checkViolations().toMutableList()
        if (file("design-tokens.json").exists()) {
            violations += "design-tokens.json was retired as an editable source (XauxaTokens.kt is canonical); delete it instead of editing"
        }
        // Contrato §12: "la lista de excepciones del gate DEBE coincidir
        // con esta tabla" — se leen los IDs E-xx de la tabla del contrato y
        // se comparan con el registro del gate (exceptionRegistry). Sin
        // entrada en AMBAS partes, no existe excepción válida.
        val contractFile = file("docs/05-design-system/00-xauxa-contrato-normativo.md")
        if (contractFile.exists()) {
            val tableIds = contractFile.readLines()
                .mapNotNull { row -> Regex("^\\| (E-\\d+) \\|").find(row)?.groupValues?.get(1) }
                .toSet()
            val gateIds = exceptionRegistry.keys.toSet()
            val missingInGate = tableIds - gateIds
            val unknownInGate = gateIds - tableIds
            if (missingInGate.isNotEmpty()) {
                violations += "contrato §12: excepciones sin registro en el gate: ${missingInGate.sorted().joinToString()}"
            }
            if (unknownInGate.isNotEmpty()) {
                violations += "contrato §12: el gate registra excepciones que NO están en la tabla: ${unknownInGate.sorted().joinToString()}"
            }
        } else {
            violations += "contrato §12: no se encuentra ${contractFile.path} para verificar la tabla de excepciones"
        }
        require(violations.isEmpty()) { "Xauxa Design System violations:\n${violations.joinToString("\n")}" }
    }
}

fun cssViolations(path: String, content: String): List<String> = buildList {
    content.lines().forEachIndexed { index, line ->
        val location = "$path:${index + 1}"
        val radius = Regex("border-radius\\s*:\\s*([^;]+);?").find(line)?.groupValues?.get(1)?.trim()
        if (radius != null && radius != "0" && radius != "0px") add("$location: forbidden radius '$radius' (rectangular containers use 0)")
        val shadow = Regex("box-shadow\\s*:\\s*([^;]+);?").find(line)?.groupValues?.get(1)?.trim()
        if (shadow != null && shadow != "none") add("$location: forbidden elevation '$shadow' (separation uses 1-2px borders)")
        if (Regex("#[0-9A-Fa-f]{3,8}").containsMatchIn(line)) add("$location: raw hex in CSS (consume token output): $line".trim())
    }
}

fun cssSources(): List<File> =
    file("core/ui/src/wasmJsMain/resources").walkTopDown()
        .filter { it.isFile && it.extension == "css" }.toList()

tasks.register("verifyWebDesignSystem") {
    group = "verification"
    description = "Enforces Xauxa visual invariants in the Wasm host CSS."
    doLast {
        val violations = cssSources().flatMap { cssViolations(it.path, it.readText()) }
        require(violations.isEmpty()) { "Web Design System violations:\n${violations.joinToString("\n")}" }
    }
}

tasks.register("verifyDesignSystemFixtures") {
    group = "verification"
    description = "Self-test: forbidden CSS examples must fail, token-correct usage must pass."
    doLast {
        val forbidden = mapOf(
            "radius" to "dialog { border-radius: 8px; }",
            "shadow" to ".tile { box-shadow: 0 1px 3px rgba(0,0,0,.3); }",
            "hex" to ".badge { color: #4A1F7A; }",
        )
        val failures = forbidden.filter { (name, css) -> cssViolations("fixture-$name.css", css).isEmpty() }.keys
        require(failures.isEmpty()) { "Fixtures that must fail passed: ${failures.joinToString()}" }
        val allowed = listOf(
            ".tile { border: 1px solid var(--xauxa-border); border-radius: 0; box-shadow: none; }",
            "canvas { display: block; }",
        )
        val falsePositives = allowed.filter { cssViolations("fixture-ok.css", it).isNotEmpty() }
        require(falsePositives.isEmpty()) { "Valid token usage failed enforcement: ${falsePositives.joinToString()}" }

        // T12 — fixtures Kotlin: los imports Material prohibidos en
        // feature/ DEBEN fallar, y el mismo import en core:ui (donde los
        // componentes Xauxa adaptan Material) no debe.
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

        // D2 — copy de UI en feature/: el literal crudo DEBE fallar...
        val rawLiteral = "XauxaText(\"Sin conexión\", color = XauxaColor.TextSecondary)"
        require(kotlinDesignViolations(featurePath, listOf(rawLiteral)).isNotEmpty()) {
            "feature/ UI literal that must fail passed (D2)"
        }
        // ...y el mismo texto vía AppStrings o id técnico debe pasar.
        val allowedD2 = listOf(
            "XauxaText(AppStrings.offlineBanner, color = XauxaColor.TextSecondary)",
            "XauxaText(\"operation-123\", color = XauxaColor.TextSecondary)",
        )
        val falseLiterals = allowedD2.filter { kotlinDesignViolations(featurePath, listOf(it)).isNotEmpty() }
        require(falseLiterals.isEmpty()) {
            "Valid AppStrings/technical literals failed enforcement (D2): ${falseLiterals.joinToString()}"
        }

        // Fase 4 — literales de copy DENTRO de expresiones deben fallar...
        val expressionLiterals = listOf(
            "errorMessage = if (emailError) \"Ingresa un correo válido\" else null",
            "label = when (type) { PAGO -> \"Pago\" else -> \"Cobro\" }",
        )
        val missedExpressions = expressionLiterals.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isEmpty()
        }
        require(missedExpressions.isEmpty()) {
            "feature/ expression literals that must fail passed (D2): ${missedExpressions.joinToString()}"
        }
        // ...y la MISMA forma con AppStrings (o id técnico) debe pasar.
        val allowedExpressions = listOf(
            "errorMessage = if (emailError) AppStrings.IngresaUnCorreoValido else null",
            "label = when (type) { PAGO -> AppStrings.Pago else -> AppStrings.Cobro }",
            "XauxaText(\"ctx-\" + id, color = XauxaColor.TextSecondary)",
        )
        val falseExpressionFlags = allowedExpressions.filter {
            kotlinDesignViolations(featurePath, listOf(it)).isNotEmpty()
        }
        require(falseExpressionFlags.isEmpty()) {
            "Valid expression usage failed enforcement (D2): ${falseExpressionFlags.joinToString()}"
        }

        // Ronda 2 (Área H) — copy en core/ui/components DEBE fallar...
        val componentsPath = "/repo/core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/Fixture.kt"
        val copyInComponents = listOf(
            "Text(\"Cargando archivo…\", color = XauxaColor.TextSecondary)",
            "XauxaTextAction(label = \"Quitar\", onClick = {})",
        )
        val missedCopy = copyInComponents.filter { kotlinDesignViolations(componentsPath, listOf(it)).isEmpty() }
        require(missedCopy.isEmpty()) {
            "components/ copy literals that must fail passed (ADR-0008): ${missedCopy.joinToString()}"
        }
        // ...y lo que NO es copy debe pasar: parámetros, mensajes de
        // desarrollador, labels de animación y glifos de un carácter.
        val allowedInComponents = listOf(
            "Text(loadingLabel, color = XauxaColor.TextSecondary)",
            "require(visibleActions.size <= 3) { \"máximo 3 acciones visibles\" }",
            "animateFloatAsState(targetValue = 1f, label = \"xauxa_tile_tilt\")",
            "Text(\"×\", fontSize = XauxaType.Title)",
        )
        val falseCopyFlags = allowedInComponents.filter { kotlinDesignViolations(componentsPath, listOf(it)).isNotEmpty() }
        require(falseCopyFlags.isEmpty()) {
            "Valid components usage failed enforcement (ADR-0008): ${falseCopyFlags.joinToString()}"
        }

        // Fase 4 — .clickable( e indication = null en feature/ deben
        // fallar; en core:ui (donde viven los componentes) deben pasar.
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

        // Fase 4 — RoundedCornerShape solo en tokens/tema: en feature/ y en
        // componentes DEBE fallar (radio 0 se dice con RectangleShape)...
        val flatShapeInFeature = "val card = RoundedCornerShape(0.dp)"
        require(kotlinDesignViolations(featurePath, listOf(flatShapeInFeature)).isNotEmpty()) {
            "feature/ RoundedCornerShape(0.dp) must fail (use RectangleShape/tokens)"
        }
        val componentShapePath = "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/FixtureShape.kt"
        require(kotlinDesignViolations(componentShapePath, listOf(flatShapeInFeature)).isNotEmpty()) {
            "components/ RoundedCornerShape must fail"
        }
        // ...y en la capa de tokens/tema pasa (XauxaShapeFlat + red de
        // seguridad de Shapes). El fixture de tokens usa el constructor
        // real (0.dp vive ahí); el del tema usa el import, para no tocar la
        // regla de literales dp (que en el tema sigue prohibida).
        val tokenShapeFixture = "val XauxaShapeFlat = RoundedCornerShape(size = 0.dp)"
        val themeShapeFixture = "import androidx.compose.foundation.shape.RoundedCornerShape"
        val tokenShapePath = "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/XauxaTokens.kt"
        val themeShapePath = "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/XauxaTheme.kt"
        require(kotlinDesignViolations(tokenShapePath, listOf(tokenShapeFixture)).isEmpty()) {
            "token layer must allow flat shapes"
        }
        require(kotlinDesignViolations(themeShapePath, listOf(themeShapeFixture)).isEmpty()) {
            "theme layer must allow flat shapes"
        }

        // Contrato §12 — .border(/BorderStroke( solo en los archivos de
        // excepciones del gate: en feature/ y en componentes ajenos DEBE
        // fallar...
        val borderInFeature = "Modifier.border(BorderStroke(XauxaMetrics.Focus, XauxaColor.Brand), XauxaShape)"
        require(kotlinDesignViolations(featurePath, listOf(borderInFeature)).isNotEmpty()) {
            "feature/ .border( must fail (contrato §12: solo archivos de excepción)"
        }
        require(kotlinDesignViolations("core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/XauxaDialogs.kt", listOf(borderInFeature)).isNotEmpty()) {
            "unordered components/ .border( must fail"
        }
        // ...y pasa en los archivos registrados (E-06, estado §4.2) y en
        // el laboratorio (entorno de validación, tokens expuestos).
        listOf(
            "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/XauxaInputs.kt",
            "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/XauxaTone.kt",
            "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/lab/LabFoundationPreview.kt",
        ).forEach { allowedPath ->
            require(kotlinDesignViolations(allowedPath, listOf(borderInFeature)).isEmpty()) {
                "border allowed file must pass: $allowedPath"
            }
        }

        // Contrato §5/§7 — Color.<nombre> y duraciones literales DEBEN
        // fallar fuera de la capa de tokens…
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
        require(kotlinDesignViolations(featurePath, listOf("val bg = XauxaColor.Transparent")).isEmpty()) {
            "XauxaColor.Transparent must pass"
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
        require(kotlinDesignViolations(featurePath, listOf("animateFloatAsState(1f, tween(XauxaMotion.DurationShortMs))")).isEmpty()) {
            "XauxaMotion token duration must pass"
        }
        // Shapes fuera del design system: en feature/ SHARED y androidApp/
        // también están prohibidas.
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
        // Lucide va por XauxaIcons.
        listOf("Icons.Default", "Icons.AutoMirrored", "Icons.Sharp", "Icons.TwoTone").forEach { preset ->
            require(kotlinDesignViolations(componentsPath, listOf("val i = ${preset}.Add")).isNotEmpty()) {
                "$preset must fail (material icons are banned)"
            }
        }
        require(kotlinDesignViolations(componentsPath, listOf("val i = XauxaIcons.Add")).isEmpty()) {
            "XauxaIcons must pass"
        }

        // D2 extendida — ifBlank/Elvis/plantillas en la capa de UI
        // (presentation) DEBEN fallar…
        listOf(
            "XauxaText(operation.amount.orEmpty().ifBlank { \"sin monto\" }, color = XauxaColor.TextPrimary)",
            "val extension = candidate.extension ?: \"Elemento importado\"",
            "val name = \"Archivo: " + "$" + "it\"",
            "XauxaText(\"3 QR listos para guardar\")".replaceFirstChar { it }, // literal completo
        ).forEach { line ->
            // nota: los dos primeros son expresiones; el último es literal
            // posicional (ya cubierto por D2) — todos deben fallar.
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
            "Technical defaults failed enforcement (D2): ${falseD2Flags.joinToString()}"
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

tasks.register("verifyAgendaQrArchitecture") {
    group = "verification"
    dependsOn("verifyDesignSystemCompliance", "verifyArchitectureBoundaries", "verifyWebDesignSystem", "verifyDesignSystemFixtures", "verifyNoScripts", "verifyControlMinSize", "verifyDocs")
    description = "Runs the complete Agenda QR architecture and Xauxa design-system gates."
}

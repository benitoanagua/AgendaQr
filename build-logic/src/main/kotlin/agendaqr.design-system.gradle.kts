import java.io.File

/**
 * Gate del sistema de diseño (ADR-0020, identidad visual propia AgendaQr). Las reglas por línea
 * operan sobre el código de producción; los source sets de test pueden
 * usar primitivas (p. ej. 48.dp para verificar touch targets) sin violar
 * el contrato visual.
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
private val TokenLayerFile = "AmTokens.kt"
private val ThemeLayerFile = "AmTheme.kt"

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
    "E-01" to listOf("border:AmButtons.kt"),
    "E-02" to listOf("border:AmLists.kt"),
    "E-03" to listOf("border:AmLists.kt"),
    "E-04" to listOf("border:AmInputs.kt"),
)

/** Archivos con `.border(`/`BorderStroke(` permitidos: ninguno. */
val borderAllowedFiles: Set<String> =
    buildSet {
        exceptionRegistry.values.forEach { rules ->
            rules.forEach { rule -> if (rule.startsWith("border:")) add(rule.removePrefix("border:")) }
        }
    }

fun kotlinDesignViolations(path: String, lines: List<String>): List<String> = buildList {
    // Solo Lucide vía AmIcon: los APIs de iconos de Material están
    // prohibidos en producción.
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
    // Las formas con esquinas viven SOLO en la capa de tokens; el tema
    // las pasa a MaterialTheme.shapes sin construir ninguna.
    val shapesAllowedFiles = listOf(TokenLayerFile)
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
    lines.forEachIndexed { index, line ->
        val location = "$path:${index + 1}"
        if (bannedImports.any(line::contains)) add("$location: forbidden Material icon API: $line")
        if (!isTokenLayer && rawHex.containsMatchIn(line)) add("$location: raw hex outside token layer: $line")
        if (!isTokenLayer && rawDp.containsMatchIn(line)) add("$location: raw dp outside token layer: $line")
        if (!isTokenLayer && rawSp.containsMatchIn(line)) add("$location: raw sp outside token layer: $line")
        if (!isTokenLayer && rawColor.containsMatchIn(line)) add("$location: raw Color constructor outside token layer: $line")
        if (shapesAllowedFiles.none { path.endsWith(it) } && forbiddenShapes.any(line::contains)) {
            add("$location: forbidden radius/elevation API: $line")
        }
        if (!isTokenLayer &&
            (line.contains("CircleShape") || line.contains("RectangleShape"))
        ) {
            add("$location: production code must use AmShapes/token API (no CircleShape/RectangleShape): $line")
        }
        // Colores nombrados de Compose fuera de la capa de tokens: todo
        // literal visual va al rol semántico (QrCanvas, TextPrimary…).
        if (!isTokenLayer &&
            Regex("\\bColor\\.(Black|White|Transparent|Red|Gray|LightGray|DarkGray|Yellow|Green|Blue|Cyan|Magenta|Unspecified)\\b")
                .containsMatchIn(line)
        ) {
            add("$location: Color.<name> outside token layer (use a semantic role from AmTokens.kt): $line")
        }
        // Duraciones literales en animaciones: toda animación consume
        // AmMotion (tween(150) local diluye el lenguaje de movimiento).
        val literalDuration = Regex("\\btween\\(\\s*\\d|\\bdurationMillis\\s*=\\s*\\d|\\bdelayMillis\\s*=\\s*\\d")
        if (!isTokenLayer && literalDuration.containsMatchIn(line)) {
            add("$location: raw animation duration; use AmMotion tokens: $line")
        }
        if (!path.endsWith(ThemeLayerFile) && forbiddenVisualAuthority.any(line::contains)) add("$location: MaterialTheme cannot be the visual authority outside AmTheme: $line")
        // `.border(`/`BorderStroke(` prohibidos en todo el código de
        // producción (las excepciones registradas son la única vía).
        val hasBorderCall = line.contains(".border(") || Regex("\\bBorderStroke\\s*\\(").containsMatchIn(line)
        if (hasBorderCall && borderAllowedFiles.none { path.endsWith(it) }) {
            add("$location: .border(/BorderStroke( outside the canonical focus file (register an exception or remove the border): $line")
        }
        // material3/colorScheme prohibido en feature/, shared/src y
        // androidApp/src (solo core:ui puede adaptarlos).
        val isAppSource = isFeatureSource || path.contains("/shared/src/") || path.contains("/androidApp/src/")
        if (isAppSource && (bannedMaterialInFeature.any(line::contains) ||
                line.contains("androidx.compose.material3.") ||
                line.contains("MaterialTheme.colorScheme"))
        ) {
            add("$location: app/feature code must use core:ui tokens: $line")
        }
        // Copy de UI en feature/ debe salir de AppStrings.
        if (isFeatureSource) {
            val uiCall = Regex("(AmText|AmHeading|AmEmptyState|AmStatusBanner|AmPrimaryButton|AmSecondaryButton|AmTextAction|AmDialog)\\(\\s*\"([^\"]+)\"").find(line)
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
    return violations
}

/**
 * Guardia permanente del área táctil: el mínimo interactivo canónico es
 * 48 dp. Si alguien baja este valor, la accesibilidad se rompe.
 */
tasks.register("verifyControlMinSize") {
    group = "verification"
    description = "AmMetrics.ControlMinSize must remain 48.dp."
    doLast {
        val tokens = file("core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/AmTokens.kt")
        if (!tokens.exists()) return@doLast
        val content = tokens.readText()
        if (!content.contains("val ControlMinSize = 48.dp")) {
            throw GradleException("Design gate failed: AmMetrics.ControlMinSize must remain 48.dp.")
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
 * Visual hash tasks: SHA-256 of each Roborazzi PNG compared against
 * manifest.json (text). SENSIBLE AL ENTORNO (ADR-0007): el render depende
 * del JDK/OS; usar en el mismo entorno que generó el manifest o
 * regenerarlo.
 */
tasks.register("recordVisualHashes") {
    group = "verification"
    description = "Regenerates SHA-256 manifest from Roborazzi captures."
    val outputDir = file("feature/destinations/presentation/build/roborazzi")
    val manifestFile = file("docs/04-ux/visual-hashes/manifest.json")
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
    doLast {
        val violations = checkViolations().toMutableList()
        if (file("design-tokens.json").exists()) {
            violations += "design-tokens.json was retired as an editable source (AmTokens.kt is canonical); delete it instead of editing"
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
        val rawLiteral = "AmText(\"Sin conexión\", color = AmColor.TextSecondary)"
        require(kotlinDesignViolations(featurePath, listOf(rawLiteral)).isNotEmpty()) {
            "feature/ UI literal that must fail passed"
        }
        // …y el mismo texto vía AppStrings o id técnico debe pasar.
        val allowedD2 = listOf(
            "AmText(AppStrings.offlineBanner, color = AmColor.TextSecondary)",
            "AmText(\"operation-123\", color = AmColor.TextSecondary)",
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
            "AmText(\"ctx-\" + id, color = AmColor.TextSecondary)",
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
            "Text(\"Cargando archivo…\", color = AmColor.TextSecondary)",
            "AmTextAction(label = \"Quitar\", onClick = {})",
        )
        val missedCopy = copyInComponents.filter { kotlinDesignViolations(componentsPath, listOf(it)).isEmpty() }
        require(missedCopy.isEmpty()) {
            "components/ copy literals that must fail passed (ADR-0008): ${missedCopy.joinToString()}"
        }
        // …y lo que NO es copy debe pasar: parámetros, mensajes de
        // desarrollador, labels de animación y glifos de un carácter.
        val allowedInComponents = listOf(
            "Text(loadingLabel, color = AmColor.TextSecondary)",
            "require(visibleActions.size <= 3) { \"máximo 3 acciones visibles\" }",
            "animateFloatAsState(targetValue = 1f, label = \"am_press\")",
            "Text(\"×\", fontSize = AmType.Title)",
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
        val tokenShapeFixture = "val AmShapeMedium = RoundedCornerShape(size = AmRadius.Medium)"
        val tokenShapePath = "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/AmTokens.kt"
        require(kotlinDesignViolations(tokenShapePath, listOf(tokenShapeFixture)).isEmpty()) {
            "token layer must allow the shape set"
        }

        // Bordes: `.border(`/`BorderStroke(` DEBEN fallar en todo el
        // código de producción (el sistema no dibuja bordes; el foco y
        // el estado viven en los componentes M3 y en el canvas).
        val borderInFeature = "Modifier.border(BorderStroke(AmMetrics.Border, AmColor.Accent), AmShapes.Medium)"
        listOf(
            featurePath,
            "/repo/shared/src/commonMain/kotlin/Fixture.kt",
            "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AmTone.kt",
            "core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/AmDialogs.kt",
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
        require(kotlinDesignViolations(featurePath, listOf("val bg = AmColor.Transparent")).isEmpty()) {
            "AmColor.Transparent must pass"
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
        require(kotlinDesignViolations(featurePath, listOf("animateFloatAsState(1f, tween(AmMotion.DurationShortMs))")).isEmpty()) {
            "AmMotion token duration must pass"
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
        // Lucide va por AmIcons.
        listOf("Icons.Default", "Icons.AutoMirrored", "Icons.Sharp", "Icons.TwoTone").forEach { preset ->
            require(kotlinDesignViolations(componentsPath, listOf("val i = ${preset}.Add")).isNotEmpty()) {
                "$preset must fail (material icons are banned)"
            }
        }
        require(kotlinDesignViolations(componentsPath, listOf("val i = AmIcons.Add")).isEmpty()) {
            "AmIcons must pass"
        }

        // D2 extendida: ifBlank/Elvis/plantillas en la capa de UI DEBEN
        // fallar…
        listOf(
            "AmText(operation.amount.orEmpty().ifBlank { \"sin monto\" }, color = AmColor.TextPrimary)",
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
    dependsOn("verifyDesignSystemCompliance", "verifyArchitectureBoundaries", "verifyDesignSystemFixtures", "verifyNoScripts", "verifyControlMinSize", "verifyDocs")
    description = "Runs the complete Agenda QR architecture and design-system gates."
}

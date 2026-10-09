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

fun kotlinDesignViolations(path: String, lines: List<String>): List<String> = buildList {
    val bannedImports = listOf("androidx.compose.material.icons", "Icons.Filled", "Icons.Outlined", "Icons.Rounded")
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
        if (!path.endsWith("XauxaTheme.kt") && forbiddenVisualAuthority.any(line::contains)) add("$location: MaterialTheme cannot be the visual authority outside XauxaTheme: $line")
        if (isFeatureSource && bannedMaterialInFeature.any(line::contains)) {
            add("$location: Material component imported from feature/ (compose Xauxa instead): $line")
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
    }
}

fun checkViolations(): List<String> {
    val violations = mutableListOf<String>()
    visualSources().forEach { file ->
        violations += kotlinDesignViolations(file.path, file.readLines())
    }
    return violations
}

tasks.register("verifyDesignSystemCompliance") {
    group = "verification"
    description = "Enforces Xauxa Design System invariants in production Kotlin sources."
    doLast {
        val violations = checkViolations().toMutableList()
        if (file("design-tokens.json").exists()) {
            violations += "design-tokens.json was retired as an editable source (XauxaTokens.kt is canonical); delete it instead of editing"
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
    dependsOn("verifyDesignSystemCompliance", "verifyArchitectureBoundaries", "verifyWebDesignSystem", "verifyDesignSystemFixtures")
    description = "Runs the complete Agenda QR architecture and Xauxa design-system gates."
}

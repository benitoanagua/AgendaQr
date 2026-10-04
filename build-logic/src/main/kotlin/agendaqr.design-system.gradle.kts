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
fun kotlinDesignViolations(path: String, lines: List<String>): List<String> = buildList {
    val bannedImports = listOf("androidx.compose.material.icons", "Icons.Filled", "Icons.Outlined", "Icons.Rounded")
    val rawHex = Regex("#[0-9A-Fa-f]{6,8}")
    val rawDp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.dp\\b")
    val rawSp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.sp\\b")
    val rawColor = Regex("\\bColor\\s*\\(")
    val forbiddenShapes = listOf("RoundedCornerShape", "CutCornerShape", "shadow(", ".shadow(")
    val forbiddenVisualAuthority = listOf("MaterialTheme.colorScheme")
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
        if (forbiddenShapes.any(line::contains)) add("$location: forbidden radius/elevation API: $line")
        if (!path.endsWith("XauxaTheme.kt") && forbiddenVisualAuthority.any(line::contains)) add("$location: MaterialTheme cannot be the visual authority outside XauxaTheme: $line")
        if (isFeatureSource && bannedMaterialInFeature.any(line::contains)) {
            add("$location: Material component imported from feature/ (compose Xauxa instead): $line")
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

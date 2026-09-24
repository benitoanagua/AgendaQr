import java.io.File

fun Project.productionKotlinSources(): Sequence<File> = sequence {
    sequenceOf("app", "core", "feature", "shared").forEach { root ->
        val directory = file(root)
        if (directory.exists()) {
            yieldAll(directory.walkTopDown().filter {
                it.isFile && it.extension == "kt" &&
                    !it.path.contains("/build/") &&
                    !it.path.contains("/src/test/") &&
                    !it.path.contains("/src/androidTest/")
            })
        }
    }
}

fun visualSources(): List<File> = productionKotlinSources().toList()

fun checkViolations(): List<String> {
    val sources = visualSources()
    val violations = mutableListOf<String>()
    val bannedImports = listOf("androidx.compose.material.icons", "Icons.Filled", "Icons.Outlined", "Icons.Rounded")
    val rawHex = Regex("#[0-9A-Fa-f]{6,8}")
    val rawDp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.dp\\b")
    val rawSp = Regex("(?<![A-Za-z0-9_])(\\d+(?:\\.\\d+)?)\\.sp\\b")
    val rawColor = Regex("\\bColor\\s*\\(")
    val forbiddenShapes = listOf("RoundedCornerShape", "CutCornerShape", "shadow(", ".shadow(")

    sources.forEach { file ->
        file.readLines().forEachIndexed { index, line ->
            val location = "${file.path}:${index + 1}"
            if (bannedImports.any(line::contains)) violations += "$location: forbidden Material icon API: $line"
            if (!file.path.endsWith("XauxaTokens.kt") && rawHex.containsMatchIn(line)) violations += "$location: raw hex outside token layer: $line"
            if (!file.path.endsWith("XauxaTokens.kt") && rawDp.containsMatchIn(line)) violations += "$location: raw dp outside token layer: $line"
            if (!file.path.endsWith("XauxaTokens.kt") && rawSp.containsMatchIn(line)) violations += "$location: raw sp outside token layer: $line"
            if (!file.path.endsWith("XauxaTokens.kt") && rawColor.containsMatchIn(line)) violations += "$location: raw Color constructor outside token layer: $line"
            if (forbiddenShapes.any(line::contains)) violations += "$location: forbidden radius/elevation API: $line"
        }
    }
    return violations
}

tasks.register("verifyDesignSystemCompliance") {
    group = "verification"
    description = "Enforces Xauxa Design System invariants in production Kotlin sources."
    doLast {
        val violations = checkViolations()
        require(violations.isEmpty()) { "Xauxa Design System violations:\n${violations.joinToString("\n")}" }
    }
}

/**
 * Single token direction: the canonical source is core/ui XauxaTokens.kt and
 * design-tokens.json is its documented snapshot. Every Color literal in
 * XauxaTokens.kt must be documented as a hex value in the snapshot
 * (references like {color.primitive.teal60} are resolved first), so no token
 * value can exist without documentation. Unused reference primitives in the
 * snapshot are allowed: they are Xauxa source context, not product claims.
 */
fun tokenSnapshotViolations(): List<String> {
    val snapshot = file("design-tokens.json")
    if (!snapshot.exists()) return listOf("design-tokens.json missing at repository root")
    val raw = snapshot.readText()
    val declared = Regex("\"[A-Za-z0-9]+\"\\s*:\\s*\"(#[0-9A-Fa-f]{6})\"")
        .findAll(raw).associate { match ->
            Regex("\"([A-Za-z0-9]+)\"").find(match.value)!!.groupValues[1] to match.groupValues[1].uppercase()
        }
    val resolved = Regex("\\{[A-Za-z0-9.]+\\}").replace(raw) { match ->
        declared[match.value.substringAfterLast(".").dropLast(1)] ?: match.value
    }
    val documented = Regex("#([0-9A-FA-f]{6})").findAll(resolved).map { it.groupValues[1] }.toSet()
    val tokensKt = file("core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/theme/XauxaTokens.kt")
    if (!tokensKt.exists()) return listOf("XauxaTokens.kt missing: cannot verify token snapshot")
    val kotlinHexes = Regex("0xFF([0-9A-FA-F]{6})").findAll(tokensKt.readText()).map { it.groupValues[1] }.toSet()
    return kotlinHexes.filter { it !in documented }.map { "Color(0xFF$it) in XauxaTokens.kt is not documented in design-tokens.json" }
}

tasks.register("verifyTokenSnapshot") {
    group = "verification"
    description = "Checks every Color in the canonical XauxaTokens.kt is documented in design-tokens.json."
    doLast {
        val violations = tokenSnapshotViolations()
        require(violations.isEmpty()) { "Token snapshot drift:\n${violations.joinToString("\n")}" }
    }
}

/**
 * CSS enforcement for the wasmJs host resources (mirrors Regla 01-03 of the
 * xauxa stylelint sidecar, scoped to this repository: radius 0 in
 * rectangular containers, no visual elevation, no raw hex outside token
 * output). Pure functions over content so the fixture self-test below can
 * prove that forbidden examples fail and token-correct usage passes.
 */
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
    dependsOn("verifyDesignSystemCompliance", "verifyArchitectureBoundaries", "verifyTokenSnapshot", "verifyWebDesignSystem", "verifyDesignSystemFixtures")
    description = "Runs the complete Agenda QR architecture and Xauxa design-system gates."
}

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
    dependsOn("verifyDesignSystemCompliance", "verifyArchitectureBoundaries")
    description = "Runs the complete Agenda QR architecture and Xauxa design-system gates."
}

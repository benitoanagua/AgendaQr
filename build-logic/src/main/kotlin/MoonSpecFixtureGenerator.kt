import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Genera `MoonSpecFixtures.kt` (androidUnitTest de `:core:ui`) a partir de
 * `docs/05-design-system/evidencia/figma/moon-v1-tokens-y-componentes.json`,
 * la fuente de verdad de geometría Moon v1 (ADR-0050).
 *
 * Por cada familia del JSON y cada componente `Aq*` mapeado emite la fila
 * canónica por tamaño (xs/sm/md/lg/xl; 2xs/3xs… en Switch/Tag/Loader/
 * Progress): la variante "en reposo" con menos utilería (sin iconos,
 * etiquetas, contadores ni estados hover/focus/pressed/disabled/error).
 * El JSON es la autoridad cuando difiere del .md legible.
 *
 * Convenciones del extractor de Figma reflejadas aquí:
 * - `pad` llega en orden CSS `[top, right, bottom, left]`.
 * - `radius`/`stroke` pueden venir ausentes en algunas variantes aunque la
 *   familia sí los defina en otras: se rellenan con el valor modal de las
 *   filas del mismo (familia, tamaño); si ninguna lo define, se emite -1
 *   ("no especificado") y la prueba no lo compara.
 * - El tamaño puede venir en `Size=`/`size=` o, en Progress bar circular,
 *   en `Property 1=`.
 */
@CacheableTask
abstract class MoonSpecFixtureGenerator : DefaultTask() {

    @get:InputFile
    @get:Classpath
    abstract val specJson: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val root = JsonSlurper().parse(specJson.get().asFile) as Map<*, *>
        val components = root["components"] as Map<*, *>

        val rows = mutableListOf<FixtureRow>()
        components.forEach { (family, rawRows) ->
            val mappings = familyToAq[family as String] ?: return@forEach
            val rowsList = (rawRows as List<*>).filterIsInstance<Map<*, *>>()
            mappings.forEach { mapping ->
                val filtered = if (mapping.include != null) {
                    rowsList.filter { mapping.include.containsMatchIn(it["name"] as String) }
                } else {
                    rowsList
                }
                canonicalRows(filtered).forEach { (size, row) ->
                    // Relleno de radio solo entre filas del MISMO tamaño:
                    // el radio puede variar legítimamente por tamaño
                    // (p. ej. Select r4 sm / r8 resto). El borde se rellena
                    // por firma de variante (un Fill no hereda el borde
                    // del Outline).
                    val signature = variantSignature(row["name"] as String)
                    val sameSize = filtered.filter { sizeOf(it["name"] as String) == size }
                    val sameShape = filtered.filter {
                        variantSignature(it["name"] as String) == signature
                    }
                    rows.add(
                        toFixtureRow(
                            family, mapping.aq, size, row,
                            radiusSiblings = sameSize,
                            borderSiblings = sameShape,
                        ),
                    )
                }
            }
        }

        val out = outputDir.get().file("MoonSpecFixtures.kt").asFile
        out.parentFile.mkdirs()
        out.writeText(render(rows))
    }

    /** Radio/borde efectivos de una fila canónica: si el extractor no los
     * registró en esa variante, se toma el valor modal del grupo
     * (familia+tamaño); -1 si ninguna fila del grupo lo define. */
    private fun effectiveInt(
        row: Map<*, *>,
        siblings: List<Map<*, *>>,
        parse: (Map<*, *>) -> Int?,
    ): Int {
        parse(row)?.let { return it }
        val values = siblings.mapNotNull { parse(it) }.groupingBy { it }.eachCount()
        return values.maxByOrNull { it.value }?.key ?: -1
    }

    private fun radiusOf(row: Map<*, *>): Int? =
        (row["radius"] as? Number)?.toInt()

    private fun borderOf(row: Map<*, *>): Int? {
        val stroke = row["stroke"] as? String ?: return null
        return Regex("([0-9.]+)px").find(stroke)?.groupValues?.get(1)?.toDoubleOrNull()?.toInt()
    }

    private fun toFixtureRow(
        family: String,
        aq: String,
        size: String,
        row: Map<*, *>,
        radiusSiblings: List<Map<*, *>>,
        borderSiblings: List<Map<*, *>>,
    ): FixtureRow {
        val pad = (row["pad"] as? List<*>)?.map { intOf(it) } ?: listOf(0, 0, 0, 0)
        return FixtureRow(
            family = family,
            aq = aq,
            size = size,
            name = row["name"] as String,
            w = intOf(row["w"]),
            h = intOf(row["h"]),
            radius = effectiveInt(row, radiusSiblings, ::radiusOf),
            padTop = pad.getOrElse(0) { 0 },
            padRight = pad.getOrElse(1) { 0 },
            padBottom = pad.getOrElse(2) { 0 },
            padLeft = pad.getOrElse(3) { 0 },
            gap = intOf(row["gap"]),
            border = effectiveInt(row, borderSiblings, ::borderOf),
        )
    }

    private fun intOf(value: Any?): Int = when (value) {
        is Number -> value.toInt()
        else -> 0
    }

    /** Devuelve pares (tamaño, fila canónica) por grupo de tamaño. */
    private fun canonicalRows(rows: List<Map<*, *>>): List<Pair<String, Map<*, *>>> {
        // Estados no canónicos: hover/focus/pressed/disabled/error/loading y
        // "State=Active" (campo enfocado).
        val excluded = Regex(
            "hover|focus|pressed|disabled|error|loading|State=Active",
            RegexOption.IGNORE_CASE,
        )
        val rest = rows.filter { !(it["name"] as String).contains(excluded) }
        // Agrupar por tamaño y quedarse con la variante menos utilería.
        // Solo penaliza utilería ACTIVADA: "=True", "=Left/Right/Both/Center",
        // "Icon only", "Arrow", "Nested", "More than" y tipos no-texto.
        // Los flags "=False" no computan.
        val feature = Regex(
            "=True|=(Left|Right|Both|Center)\\b|Icon only|Arrow|Nested|More than|" +
                "Number|Password|Type=Search|uppercase=true|Meta=True|Label right=True",
        )
        return rest.groupBy { sizeOf(it["name"] as String) }
            .map { (size, group) ->
                size to group.minWithOrNull(compareBy(
                    { feature.findAll(it["name"] as String).count() },
                    { it["name"] as String },
                ))!!
            }
    }

    /** Firma de variante: nombre sin tokens de tamaño ni `state=rest`;
     * dos filas con la misma firma son la misma variante en distinto
     * tamaño (o estado visual equivalente). */
    private fun variantSignature(name: String): String =
        name
            .replace(Regex("(Size|size|Property 1)=([A-Za-z0-9]+)"), "")
            .replace("state=rest", "")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun sizeOf(name: String): String {
        val raw = Regex("(?:^|[,\\s])(?:Size|size)=([A-Za-z0-9]+)").find(name)?.groupValues?.get(1)
            ?: Regex("Property 1=([A-Za-z0-9]+)").find(name)?.groupValues?.get(1)
            ?: return ""
        return if (raw in knownSizes) raw else ""
    }

    private val knownSizes = setOf("2xs", "3xs", "4xs", "5xs", "6xs", "xs", "sm", "md", "lg", "xl", "2xl")

    private data class FixtureRow(
        val family: String,
        val aq: String,
        val size: String,
        val name: String,
        val w: Int,
        val h: Int,
        val radius: Int,
        val padTop: Int,
        val padRight: Int,
        val padBottom: Int,
        val padLeft: Int,
        val gap: Int,
        val border: Int,
    )

    private fun render(rows: List<FixtureRow>): String {
        val sb = StringBuilder()
        sb.append("package com.agendaqr.core.ui.fidelity\n\n")
        sb.append("/**\n")
        sb.append(" * GENERADO por `generateMoonSpecFixtures` desde\n")
        sb.append(" * `docs/05-design-system/evidencia/figma/moon-v1-tokens-y-componentes.json`.\n")
        sb.append(" * No editar: regenera con `./gradlew generateMoonSpecFixtures`.\n")
        sb.append(" * Métricas en dp, estado en reposo, tolerancia 0 (ADR-0050).\n")
        sb.append(" * -1 = el JSON no especifica la métrica en esa variante.\n")
        sb.append(" */\n")
        sb.append("object MoonSpecFixtures {\n")
        sb.append("    data class Row(\n")
        sb.append("        val family: String,\n")
        sb.append("        val aq: String,\n")
        sb.append("        val size: String,\n")
        sb.append("        val variant: String,\n")
        sb.append("        val width: Int,\n")
        sb.append("        val height: Int,\n")
        sb.append("        val radius: Int,\n")
        sb.append("        val padTop: Int,\n")
        sb.append("        val padRight: Int,\n")
        sb.append("        val padBottom: Int,\n")
        sb.append("        val padLeft: Int,\n")
        sb.append("        val gap: Int,\n")
        sb.append("        val border: Int,\n")
        sb.append("    )\n\n")
        sb.append("    val rows: List<Row> = listOf(\n")
        rows.sortedWith(compareBy({ it.aq }, { it.size }, { it.family })).forEach { r ->
            sb.append("        Row(\n")
            sb.append("            family = \"${esc(r.family)}\",\n")
            sb.append("            aq = \"${esc(r.aq)}\",\n")
            sb.append("            size = \"${esc(r.size)}\",\n")
            sb.append("            variant = \"${esc(r.name.take(120))}\",\n")
            sb.append("            width = ${r.w},\n")
            sb.append("            height = ${r.h},\n")
            sb.append("            radius = ${r.radius},\n")
            sb.append("            padTop = ${r.padTop},\n")
            sb.append("            padRight = ${r.padRight},\n")
            sb.append("            padBottom = ${r.padBottom},\n")
            sb.append("            padLeft = ${r.padLeft},\n")
            sb.append("            gap = ${r.gap},\n")
            sb.append("            border = ${r.border},\n")
            sb.append("        ),\n")
        }
        sb.append("    )\n}\n")
        return sb.toString()
    }

    private fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")

    /** Mapeo familia JSON → componente `Aq*`; [include] filtra las variantes
     * del JSON que alimentan ese componente (p. ej. Tabs pill vs. barra). */
    data class AqMapping(val aq: String, val include: Regex? = null)

    companion object {
        val familyToAq: Map<String, List<AqMapping>> = mapOf(
            "Accordion" to listOf(AqMapping("AqAccordion")),
            "Alert" to listOf(AqMapping("AqAlert")),
            "Avatar" to listOf(AqMapping("AqAvatar")),
            "Auth code" to listOf(AqMapping("AqAuthCode")),
            "Breadcrumb" to listOf(AqMapping("AqBreadcrumb")),
            "Button" to listOf(AqMapping("AqButton")),
            "Bottom sheet" to listOf(AqMapping("AqBottomSheet")),
            "Checkbox" to listOf(AqMapping("AqCheckbox")),
            "Chip" to listOf(AqMapping("AqChip")),
            "Combobox" to listOf(AqMapping("AqCombobox")),
            "Drawer" to listOf(AqMapping("AqDrawer")),
            "Dropdown" to listOf(AqMapping("AqDropdown")),
            "Empty state" to listOf(AqMapping("AqEmptyPlaceholder")),
            "Leaderboard" to listOf(AqMapping("AqLeaderboard")),
            // «Loader» del Figma v1 es el spinner circular (w=h por tamaño);
            // el loader lineal indeterminado no aparece en la especificación
            // y queda como excepción de cobertura (E-nn del contrato).
            "Loader" to listOf(AqMapping("AqCircularLoader")),
            "Menu item" to listOf(AqMapping("AqMenuItem")),
            "Modal" to listOf(AqMapping("AqModal")),
            "Pagination" to listOf(AqMapping("AqPagination")),
            "Popover" to listOf(AqMapping("AqPopover")),
            "Progress bar" to listOf(
                AqMapping("AqLinearProgress", Regex("Variant=Linear, pin=false")),
                AqMapping(
                    "AqProgressBar",
                    Regex("(?=.*Variant=Linear)(?=.*pin=false)(?=.*Size=4xs)"),
                ),
                AqMapping("AqCircularProgress", Regex("Circular")),
            ),
            "Radio button" to listOf(AqMapping("AqRadio")),
            "Segmented control" to listOf(AqMapping("AqSegmentedControl")),
            "Select" to listOf(AqMapping("AqSelect")),
            "Snackbar" to listOf(AqMapping("AqToast")),
            "Sidebar" to listOf(AqMapping("AqSidebar")),
            "Switch" to listOf(AqMapping("AqSwitch")),
            "Table" to listOf(AqMapping("AqTable")),
            "Tabs" to listOf(
                AqMapping("AqTabBar", Regex("Type=(Default|Underline)")),
                AqMapping("AqPillTab", Regex("Type=Pill")),
            ),
            "Tag" to listOf(AqMapping("AqTag")),
            "Textarea" to listOf(AqMapping("AqTextArea")),
            "Text input" to listOf(
                AqMapping("AqTextInput", Regex("Type=Text")),
                AqMapping("AqSearchInput", Regex("Type=Search")),
            ),
            "Text input group" to listOf(AqMapping("AqTextInputGroup")),
            "Tooltip" to listOf(AqMapping("AqTooltip")),
        )
    }
}

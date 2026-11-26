package com.agendaqr.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/** Modelo inmutable de una opción de selección, sin estado interno. */
data class AqChoiceOption(
    val id: String,
    val label: String,
    val description: String? = null,
)

/** Estado visual compartido por los controles booleanos del formulario. */
data class AqFieldState(
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val helperMessage: String? = null,
)

internal fun Modifier.fieldSemantics(state: AqFieldState): Modifier =
    if (state.isError && state.errorMessage != null) {
        this.semantics { error(state.errorMessage) }
    } else {
        this
    }

@Composable
internal fun AqFieldHelp(state: AqFieldState) {
    val message = if (state.isError) state.errorMessage else state.helperMessage
    if (!message.isNullOrBlank()) {
        AqText(
            text = message,
            size = AqType.Caption,
            color = if (state.isError) AqColor.Danger else AqColor.TextSecondary,
        )
    }
}

/** Casilla equivalente a `s-checkbox`, con target táctil de 48 dp. */
@Composable
fun AqCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
) {
    // Sin etiqueta (casilla suelta dentro de una celda) el control mide solo
    // su área táctil: antes fillMaxWidth() le quitaba todo el ancho al título
    // de la fila contigua.
    val hasText = label.isNotBlank() || description != null || state.helperMessage != null ||
        (state.isError && state.errorMessage != null)
    Row(
        modifier = modifier
            .then(if (hasText) Modifier.fillMaxWidth() else Modifier)
            // Moon `checkbox.dart`: `tapAreaSizeValue` = 40 para la casilla
            // suelta; con etiqueta la fila conserva el mínimo de 48 dp.
            .defaultMinSize(
                minHeight = if (hasText) AqMetrics.ControlMinSize else AqSize.Md,
            )
            .fieldSemantics(state)
            .selectable(
                selected = checked,
                enabled = state.enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clearAndSetSemantics { }
                .size(AqSize.Md),
            contentAlignment = Alignment.Center,
        ) {
            AqCheckboxGlyph(checked = checked, enabled = state.enabled)
        }
        if (hasText) Column(modifier = Modifier.padding(start = AqSpacing.Md).weight(1f)) {
            AqText(
                text = label,
                size = AqType.Body,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            )
            if (description != null) {
                AqText(
                    text = description,
                    size = AqType.Caption,
                    color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                )
            }
            AqFieldHelp(state)
        }
    }
}

/** Grupo de opciones con semántica de selección mutuamente excluyente (usa [AqRadio]). */
@Composable
fun AqChoiceSet(
    label: String,
    options: List<AqChoiceOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
) {
    Column(
        modifier = modifier.fieldSemantics(state).selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqText(
            text = label,
            size = AqType.Label,
            fontWeight = AqWeight.Medium,
            color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
        )
        val childState = state.copy(isError = false, errorMessage = null, helperMessage = null)
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .selectable(
                        selected = option.id == selectedId,
                        enabled = state.enabled,
                        role = Role.RadioButton,
                        onClick = { onSelected(option.id) },
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md),
            ) {
                // La fila completa es la superficie seleccionable; el radio es
                // solo el indicador (sin segundo clickable anidado).
                AqRadio(
                    selected = option.id == selectedId,
                    onClick = null,
                    modifier = Modifier.clearAndSetSemantics { },
                    enabled = state.enabled,
                )
                Column(modifier = Modifier.weight(1f)) {
                    AqText(
                        text = option.label,
                        size = AqType.Body,
                        fontWeight = AqWeight.Medium,
                        color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
                    )
                    option.description?.let {
                        AqText(
                            text = it,
                            size = AqType.Caption,
                            color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                        )
                    }
                }
            }
        }
        AqFieldHelp(state)
    }
}

/** Grupo de casillas con estado derivado, sin estado interno. */
@Composable
fun AqCheckboxSet(
    label: String,
    options: List<AqChoiceOption>,
    checkedIds: Set<String>,
    onToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
) {
    Column(
        modifier = modifier.fieldSemantics(state).selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqText(
            text = label,
            size = AqType.Label,
            fontWeight = AqWeight.Medium,
            color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
        )
        val childState = state.copy(isError = false, errorMessage = null, helperMessage = null)
        options.forEach { option ->
            AqCheckbox(
                label = option.label,
                description = option.description,
                checked = option.id in checkedIds,
                onCheckedChange = { onToggle(option.id, it) },
                state = childState,
            )
        }
        AqFieldHelp(state)
    }
}

/**
 * Casilla de selección total con estado mixto derivado por el consumidor.
 * Se usa junto a [AqCheckboxSet] cuando una lista permite seleccionar
 * todos sus elementos. El llamador decide si al pulsar un estado mixto
 * selecciona o limpia el conjunto.
 */
@Composable
fun AqSelectAll(
    label: String,
    checkedState: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .fieldSemantics(state)
            .triStateToggleable(
                state = checkedState,
                enabled = state.enabled,
                role = Role.Checkbox,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clearAndSetSemantics { }
                .size(AqMetrics.ControlMinSize),
            contentAlignment = Alignment.Center,
        ) {
            AqCheckboxGlyph(
                checked = when (checkedState) {
                    ToggleableState.On -> true
                    ToggleableState.Indeterminate -> null
                    ToggleableState.Off -> false
                },
                enabled = state.enabled,
            )
        }
        Column(modifier = Modifier.padding(start = AqSpacing.Md).weight(1f)) {
            AqText(
                text = label,
                size = AqType.Body,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            )
            if (description != null) {
                AqText(
                    text = description,
                    size = AqType.Caption,
                    color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                )
            }
            AqFieldHelp(state)
        }
    }
}

// ---------------------------------------------------------------------------
// Jerarquía de formulario: sección con encabezado único + campo con label
// ---------------------------------------------------------------------------

/**
 * Sección de formulario con UN encabezado tipográfico ([AqSectionTitle],
 * semántica `heading`). El título nombra el GRUPO ("Detalles",
 * "Comprobante"), nunca repite el label de un campo hijo — la regla de
 * jerarquía del DS es "encabezado de sección ≠ etiqueta de campo".
 */
@Composable
fun AqFormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aq-form-section"),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
    ) {
        // En formulario el encabezado va al borde, sin la sangría de lista.
        AqSectionTitle(text = title)
        Column(
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
            content = content,
        )
    }
}

/**
 * Campo de formulario con etiqueta persistente, marcador de opcionalidad
 * discreto ([optionalLabel], un solo patrón) y mensajes de ayuda/error
 * ([AqFieldHelp]). Para contenido compuesto (chips, fechas, filas);
 * los campos hoja usan su propio label + [optionalLabel].
 */
@Composable
fun AqFormEntry(
    label: String,
    modifier: Modifier = Modifier,
    optionalLabel: String? = null,
    state: AqFieldState = AqFieldState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth().fieldSemantics(state),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            AqText(
                text = label,
                size = AqType.Label,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
                modifier = Modifier.weight(1f, fill = false),
            )
            optionalLabel?.let {
                AqText(
                    text = it,
                    size = AqType.Caption,
                    color = AqColor.TextTertiary,
                )
            }
        }
        Column(content = content)
        AqFieldHelp(state)
    }
}

// ---------------------------------------------------------------------------
// ChipGroup — selección inequívoca de opciones en formulario o filtro
// ---------------------------------------------------------------------------

/** Una opción seleccionable de [AqChipRow]. */
data class AqChipOption(
    val label: String,
    /** Conteo junto a la etiqueta; null = sin conteo. */
    val count: Int? = null,
    val enabled: Boolean = true,
)

/**
 * Grupo de chips seleccionables: conjunto compacto de opciones dentro de
 * una superficie (moneda, atajos de fecha, sugerencias). La selección NO
 * depende solo del color: el chip activo lleva check + relleno de acento
 * + borde reforzado — distinguible en claro, oscuro y para daltónicos.
 * Objetivo táctil de 48 dp; semántica `selectable` + `selected`.
 *
 * [horizontalScroll] = fila desplazable (opciones que no caben);
 * si no, los chips fluyen y saltan de línea. [allowDeselect] permite
 * volver a pulsar el activo para limpiarlo (`onSelect(-1)`).
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AqChipRow(
    options: List<AqChipOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalScroll: Boolean = false,
    allowDeselect: Boolean = false,
) {
    require(options.isNotEmpty()) { "AqChipRow requiere al menos una opción" }
    val onChipClick: (Int) -> Unit = { index ->
        onSelect(if (allowDeselect && index == selectedIndex) -1 else index)
    }
    if (horizontalScroll) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            options.forEachIndexed { index, option ->
                AqChip(
                    text = option.label,
                    selected = index == selectedIndex,
                    enabled = option.enabled,
                    onClick = { onChipClick(index) },
                    icon = if (index == selectedIndex) AqIcons.Check else null,
                    count = option.count,
                    size = AqChipSize.Md,
                )
            }
        }
    } else {
        androidx.compose.foundation.layout.FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            options.forEachIndexed { index, option ->
                AqChip(
                    text = option.label,
                    selected = index == selectedIndex,
                    enabled = option.enabled,
                    onClick = { onChipClick(index) },
                    icon = if (index == selectedIndex) AqIcons.Check else null,
                    count = option.count,
                    size = AqChipSize.Md,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Glifo de casilla Moon (`checkbox.dart` + `checkbox_painter.dart`)
// ---------------------------------------------------------------------------

/**
 * Glifo de 16×16 dp de [MoonCheckbox]: squircle `interactiveXs` (radio 4),
 * relleno `piccolo` al activarse, borde `trunks` de 1 dp en reposo y marca
 * check/dash en `goten`. `checked == null` = estado mixto (trazo horizontal).
 * La animación de 200 ms `easeInOutCubic` reproduce el `ToggleablePainter`
 * de Flutter (el check se dibuja en dos segmentos y el dash crece desde el
 * centro). Deshabilitado = opacidad 0.6.
 */
@Composable
private fun AqCheckboxGlyph(
    checked: Boolean?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue = if (checked == false) 0f else 1f,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqCheckbox",
    )
    val fillColor = AqColor.BrandFill
    val borderColor = AqColor.TextSecondary
    val markColor = AqColor.OnBrandFill
    val strokeWidth = AqLayout.CheckboxStrokeWidth
    Canvas(
        modifier = modifier
            .size(AqLayout.CheckboxGlyphSize)
            .alpha(if (enabled) 1f else AqStateAlpha.Disabled)
            .testTag("aq-checkbox-glyph"),
    ) {
        val outline = AqSquircles.InteractiveXs.createOutline(size, layoutDirection, this)
        val boxPath = (outline as? Outline.Generic)?.path
        if (boxPath == null) return@Canvas
        // Relleno activo: aparece durante la primera mitad de la transición.
        if (progress > 0f) {
            drawPath(
                path = boxPath,
                color = fillColor.copy(alpha = fillColor.alpha * progress),
            )
        }
        // Borde inactivo: desaparece al seleccionar (Moon resuelve `side` a
        // null cuando `MaterialState.selected`).
        val borderAlpha = 1f - progress
        if (borderAlpha > 0f) {
            drawPath(
                path = boxPath,
                color = borderColor.copy(alpha = borderColor.alpha * borderAlpha),
                style = Stroke(width = strokeWidth.toPx()),
            )
        }
        // Marca: check en dos segmentos o dash horizontal (estado mixto).
        if (progress > 0f) {
            val s = size.width
            val markPaint = Stroke(width = strokeWidth.toPx())
            if (checked == null) {
                val startX = s * 0.2f
                val midX = s * 0.5f
                val endX = s * 0.8f
                val y = s * 0.5f
                drawLine(
                    color = markColor,
                    start = Offset(
                        androidx.compose.ui.util.lerp(startX, midX, 1f - progress),
                        y,
                    ),
                    end = Offset(
                        androidx.compose.ui.util.lerp(midX, endX, progress),
                        y,
                    ),
                    strokeWidth = strokeWidth.toPx(),
                )
            } else {
                val start = Offset(s * 0.15f, s * 0.45f)
                val mid = Offset(s * 0.4f, s * 0.7f)
                val end = Offset(s * 0.85f, s * 0.25f)
                // Primer segmento (start→mid) en la primera mitad de la
                // animación; segundo segmento (mid→end) en la segunda.
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    if (progress <= 0.5f) {
                        val t = progress * 2f
                        lineTo(
                            androidx.compose.ui.util.lerp(start.x, mid.x, t),
                            androidx.compose.ui.util.lerp(start.y, mid.y, t),
                        )
                    } else {
                        lineTo(mid.x, mid.y)
                        val t = (progress - 0.5f) * 2f
                        lineTo(
                            androidx.compose.ui.util.lerp(mid.x, end.x, t),
                            androidx.compose.ui.util.lerp(mid.y, end.y, t),
                        )
                    }
                }
                drawPath(path = path, color = markColor, style = markPaint)
            }
        }
    }
}

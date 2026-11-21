package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqComposition
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Estados: banner de estado (alerta tonal redondeada),
 * carga, estado vacío con glifo y agrupador de sección.
 */

/**
 * Banner de resultado embebido (patrón Alert): marcador semántico +
 * contenido + acciones sobre un bloque tonal con radio de tokens — sin
 * borde de reposo (el bloque define el límite).
 *
 * El tono se expresa SIEMPRE con [tone]; las acciones heredan el color de
 * CONTENIDO del tono (par garantizado por AqSchemeTest).
 */
/**
 * Acción de un banner de estado. El DS no hardcodea copy: la etiqueta
 * llega del llamador (habitualmente desde `UserFacingError` o `AppStrings`).
 */
data class AqBannerAction(val label: String, val onClick: () -> Unit)

@Composable
fun AqStatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: AqTone? = null,
    /** Título opcional (negrita, TextStyle del DS) encima del mensaje; evita
     *  el "\\n" manual en el copy del llamador. */
    title: String? = null,
    /** Acción principal (p. ej. reintentar). */
    action: AqBannerAction? = null,
    /** Acción de descarte (p. ej. descartar). Puede ser null; si es null
     *  no se renderiza fila de acciones cuando tampoco hay [action]. */
    dismiss: AqBannerAction? = null,
) {
    val resolvedTone = tone ?: AqTone.Neutral
    val background = resolvedTone.container()
    val foreground = resolvedTone.content()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background, AqShapes.Medium)
            .testTag("am-status-banner")
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.MdPlus),
    ) {
        // Fila 1: icono discreto (compacto, alineado al inicio del título)
        // + título/mensaje a ancho completo.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            resolvedTone.iconVector?.let { glyph ->
                AqIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = foreground,
                )
            }
            // Jerarquía del mensaje: título semibold dominante, descripción
            // cuerpo normal con separación propia (sin heredar la altura
            // del título).
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            ) {
                title?.let {
                    AqHeading(
                        text = it,
                        size = AqType.TitleMedium,
                        fontWeight = AqWeight.SemiBold,
                        color = foreground,
                    )
                }
                if (message.isNotBlank()) {
                    AqText(
                        text = message,
                        color = foreground,
                    )
                }
            }
            // Descarte como icono X simple (acción, no estado) accesible;
            // nunca repite el glifo semántico del tono.
            dismiss?.let { d ->
                AqIconButton(
                    contentDescription = d.label,
                    onClick = d.onClick,
                ) {
                    AqIcon(
                        imageVector = AqIcons.Close,
                        contentDescription = null,
                        size = AqMetrics.IconSizeCompact,
                        tint = foreground,
                    )
                }
            }
        }
        // Fila 2: acción principal como botón Default/Small, separada del
        // contenido por 12 dp y con el relleno propio del banner.
        action?.let { a ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("aq-banner-actions"),
                horizontalArrangement = Arrangement.End,
            ) {
                AqButton(
                    label = a.label,
                    onClick = a.onClick,
                    variant = AqButtonVariant.Default,
                    size = AqButtonSize.Sm,
                )
            }
        }
    }
}

/**
 * Píldora de estado/reaseguro (S01/S07/S09): tonal por
 * defecto (surface-container-low con icono de estado y metadato 13 sp)
 * o INVERSA para "Modo local" (bloque oscuro con contenido inverso, par
 * garantizado por AqSchemeTest). No interactiva.
 */
@Composable
fun AqStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    /** Glifo decorativo que acompaña el texto (opcional). */
    icon: ImageVector? = null,
    /** Tono del glifo (solo variante tonal). */
    tone: AqTone = AqTone.Success,
    /** Variante inversa: bloque oscuro "Modo local". */
    inverse: Boolean = false,
    /** Contenido final (p. ej. el estado "Al día" del llamador). */
    trailing: (@Composable (() -> Unit))? = null,
) {
    val container = if (inverse) AqColor.InverseSurface else tone.container()
    val content = if (inverse) AqColor.InverseOnSurface else tone.content()
    Row(
        modifier = modifier
            .background(container, AqShapes.Pill)
            .padding(horizontal = AqSpacing.Sm, vertical = AqSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        if (inverse) {
            // Punto "en vivo" del modo local (decorativo; el texto comunica).
            Box(
                modifier = Modifier
                    .size(AqSpacing.Xs)
                    .background(AqColor.SuccessContainer, AqShapes.Pill),
            )
        } else {
            icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = tone.content(),
                )
            }
        }
        Text(text, style = AqTextStyles.Tag, color = content)
        trailing?.invoke()
    }
}

/**
 * Indicador de carga con mensaje opcional y anuncio cortés.
 */
@Composable
fun AqLoading(modifier: Modifier = Modifier, message: String? = null) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AqSpacing.Xxxl)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
    ) {
        AqSpinner(contentDescription = message.orEmpty())
        message?.let { Text(it, color = AqColor.TextSecondary, fontSize = AqType.Label) }
    }
}

/**
 * Estado vacío: glifo decorativo en círculo tonal + título + subtítulo +
 * acción opcional. El glifo no se anuncia al lector de pantalla (el
 * título y el subtítulo comunican); la acción es la acción primaria del
 * estado cuando la acción viable no vive en el chrome.
 */
@Composable
fun AqEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    /** Glifo decorativo del estado (p. ej. [AqIcons.QrCode]). */
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AqSpacing.Xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
    ) {
        icon?.let { glyph ->
            // Círculo tonal del glifo: `Surface` (distinto del fondo en
            // ambos temas), sin bordes decorativos (gate E-01/E-02).
            Box(
                modifier = Modifier.size(AqMetrics.EmptyStateGlyphSize)
                    .background(AqColor.Surface, AqShapes.Pill),
                contentAlignment = Alignment.Center,
            ) {
                AqIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    tint = AqColor.TextTertiary,
                )
            }
        }
        // Título de estado vacío (17 sp) distinto del título de pantalla
        // (18 sp): evita la misma jerarquía visual entre ambos.
        AqHeading(text = title, size = AqType.Body, fontWeight = AqWeight.SemiBold)
        subtitle?.let { Text(it, color = AqColor.TextSecondary, fontSize = AqType.Label) }
        if (actionLabel != null && onAction != null) {
            AqPrimaryButton(actionLabel, onAction)
        }
    }
}

/**
 * Agrupador de sección: encabezado + contenido con el espaciado del
 * sistema.
 */
@Composable
fun AqSection(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable (() -> Unit))? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AqSectionHeader(text = title)
            trailing?.invoke()
        }
        Spacer(Modifier.height(AqSpacing.Md))
        content()
    }
}

/** Confirmación visual decorativa que acompaña al texto del resultado. */
@Composable
fun AqSuccessCheck(
    modifier: Modifier = Modifier,
    /** Lado del círculo de confirmación. */
    size: Dp = AqMetrics.ResultGlyphSize,
) {
    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(AqColor.SuccessContainer, AqShapes.Pill),
            contentAlignment = Alignment.Center,
        ) {
            AqIcon(
                imageVector = AqIcons.CircleCheck,
                contentDescription = null,
                size = size * AqComposition.SuccessCheckIconFraction,
                tint = AqColor.Success,
            )
        }
    }
}

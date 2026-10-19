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
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmType

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
 * CONTENIDO del tono (par garantizado por AmSchemeTest).
 */
/**
 * Acción de un banner de estado. El DS no hardcodea copy: la etiqueta
 * llega del llamador (habitualmente desde `UserFacingError` o `AppStrings`).
 */
data class AmBannerAction(val label: String, val onClick: () -> Unit)

@Composable
fun AmStatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: AmTone? = null,
    /** Título opcional (negrita, TextStyle del DS) encima del mensaje; evita
     *  el "\\n" manual en el copy del llamador. */
    title: String? = null,
    /** Acción principal (p. ej. reintentar). */
    action: AmBannerAction? = null,
    /** Acción de descarte (p. ej. descartar). Puede ser null; si es null
     *  no se renderiza fila de acciones cuando tampoco hay [action]. */
    dismiss: AmBannerAction? = null,
    /**
     * Margen horizontal propio del banner. Por defecto es
     * [AmSpacing.ScreenMargin]: el componente suele componerse fuera de un
     * contenedor ya acotado (bandeja de estado, overlay de cámara). Cuando
     * el llamador vive DENTRO del contenido con margen del
     * `AmScreenScaffold`, debe pasar [AmSpacing.None]: si no, los dos
     * márgenes se suman y el bloque se estrecha 16 dp por lado (defecto
     * registrado en S12).
     */
    screenMargin: Dp = AmSpacing.ScreenMargin,
) {
    val resolvedTone = tone ?: AmTone.Neutral
    val background = resolvedTone.container()
    val foreground = resolvedTone.content()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = screenMargin)
            .background(background, AmShapes.Medium)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = AmSpacing.Md, vertical = AmSpacing.Md),
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
    ) {
        // Fila 1: icono discreto (compacto, alineado al inicio del título)
        // + título/mensaje a ancho completo.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
        ) {
            resolvedTone.iconVector?.let { glyph ->
                AmIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    size = AmMetrics.IconSizeCompact,
                    tint = foreground,
                )
            }
            // Jerarquía del mensaje: título semibold dominante, descripción
            // cuerpo normal con separación propia (sin heredar la altura
            // del título).
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
            ) {
                title?.let {
                    AmHeading(
                        text = it,
                        size = AmType.TitleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = foreground,
                    )
                }
                if (message.isNotBlank()) {
                    AmText(
                        text = message,
                        color = foreground,
                    )
                }
            }
            // Descarte como icono X accesible, no como segundo texto.
            dismiss?.let { d ->
                AmIconButton(
                    contentDescription = d.label,
                    onClick = d.onClick,
                ) {
                    AmIcon(imageVector = AmIcons.Cancel, contentDescription = null, tint = foreground)
                }
            }
        }
        // Fila 2: acción principal con peso SemiBold, altura compacta
        // (36 dp visual / 48 dp táctil), texto alineado con el mensaje.
        action?.let { a ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                AmCompactAction(
                    label = a.label,
                    onClick = a.onClick,
                    color = foreground,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/**
 * Píldora de estado/reaseguro (S01/S07/S09): tonal por
 * defecto (surface-container-low con icono de estado y metadato 13 sp)
 * o INVERSA para "Modo local" (bloque oscuro con contenido inverso, par
 * garantizado por AmSchemeTest). No interactiva.
 */
@Composable
fun AmStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    /** Glifo decorativo que acompaña el texto (opcional). */
    icon: ImageVector? = null,
    /** Tono del glifo (solo variante tonal). */
    tone: AmTone = AmTone.Success,
    /** Variante inversa: bloque oscuro "Modo local". */
    inverse: Boolean = false,
    /** Contenido final (p. ej. el estado "Al día" del llamador). */
    trailing: (@Composable (() -> Unit))? = null,
) {
    val container = if (inverse) AmColor.InverseSurface else tone.container()
    val content = if (inverse) AmColor.InverseOnSurface else tone.content()
    Row(
        modifier = modifier
            .background(container, AmShapes.Pill)
            .padding(horizontal = AmSpacing.Sm, vertical = AmSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
    ) {
        if (inverse) {
            // Punto "en vivo" del modo local (decorativo; el texto comunica).
            Box(
                modifier = Modifier
                    .size(AmSpacing.Xs)
                    .background(AmColor.SuccessContainer, AmShapes.Pill),
            )
        } else {
            icon?.let {
                AmIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AmMetrics.IconSizeCompact,
                    tint = tone.content(),
                )
            }
        }
        Text(text, style = AmTextStyles.Tag, color = content)
        trailing?.invoke()
    }
}

/**
 * Indicador de carga con mensaje opcional y anuncio cortés.
 */
@Composable
fun AmLoading(modifier: Modifier = Modifier, message: String? = null) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AmSpacing.Xxxl)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
    ) {
        CircularProgressIndicator(color = AmColor.Accent)
        message?.let { Text(it, color = AmColor.TextSecondary, fontSize = AmType.Label) }
    }
}

/**
 * Estado vacío: glifo decorativo en círculo tonal + título + subtítulo +
 * acción opcional. El glifo no se anuncia al lector de pantalla (el
 * título y el subtítulo comunican); la acción es la acción primaria del
 * estado cuando la acción viable no vive en el chrome.
 */
@Composable
fun AmEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    /** Glifo decorativo del estado (p. ej. [AmIcons.QrCode]). */
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AmSpacing.Xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
    ) {
        icon?.let { glyph ->
            // Círculo tonal del glifo: `Surface` (distinto del fondo en
            // ambos temas), sin bordes decorativos (gate E-01/E-02).
            Box(
                modifier = Modifier.size(AmMetrics.EmptyStateGlyphSize)
                    .background(AmColor.Surface, AmShapes.Pill),
                contentAlignment = Alignment.Center,
            ) {
                AmIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    tint = AmColor.TextTertiary,
                )
            }
        }
        // Título de estado vacío (17 sp) distinto del título de pantalla
        // (18 sp): evita la misma jerarquía visual entre ambos.
        AmHeading(text = title, size = AmType.Body, fontWeight = FontWeight.SemiBold)
        subtitle?.let { Text(it, color = AmColor.TextSecondary, fontSize = AmType.Label) }
        if (actionLabel != null && onAction != null) {
            AmPrimaryButton(actionLabel, onAction)
        }
    }
}

/**
 * Agrupador de sección: encabezado + contenido con el espaciado del
 * sistema.
 */
@Composable
fun AmSection(
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
            AmSectionHeader(text = title)
            trailing?.invoke()
        }
        Spacer(Modifier.height(AmSpacing.Md))
        content()
    }
}

/** Confirmación visual decorativa que acompaña al texto del resultado. */
@Composable
fun AmSuccessCheck(
    modifier: Modifier = Modifier,
    /** Lado del círculo de confirmación. */
    size: Dp = AmMetrics.ResultGlyphSize,
) {
    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(AmColor.SuccessContainer, AmShapes.Pill),
            contentAlignment = Alignment.Center,
        ) {
            AmIcon(
                imageVector = AmIcons.CircleCheck,
                contentDescription = null,
                size = size * 0.55f,
                tint = AmColor.Success,
            )
        }
    }
}

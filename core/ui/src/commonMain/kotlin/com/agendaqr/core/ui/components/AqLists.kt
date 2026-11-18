package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqElevation
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.accentFor
import com.agendaqr.core.ui.theme.aqElevation

/**
 * Celdas y etiquetas (referencia: List/Item + Tag): la celda (`AqListRow`)
 * es la unidad de las listas de producto — patrón de lista móvil con
 * divisores opt-in y metadatos en línea propia.
 */

/**
 * Etiqueta semántica de estado (Tag outline de la referencia): borde y
 * texto del tono (par garantizado por AqSchemeTest) sobre fondo
 * transparente. No interactiva; el estado se expresa con TEXTO (nunca
 * solo color). [round] activa la variante píldora del Tag.
 */
@Composable
fun AqBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: AqTone = AqTone.Neutral,
    /** Variante píldora (Tag `round` de la referencia). */
    round: Boolean = false,
) {
    val toneColor = if (tone == AqTone.Neutral) AqColor.TextSecondary else tone.content()
    val shape = if (round) AqShapes.Pill else AqShapes.ExtraSmall
    Box(
        modifier = modifier
            .border(
                AqMetrics.Border,
                toneColor,
                shape,
            )
            .padding(horizontal = AqSpacing.Sm, vertical = AqSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = AqTextStyles.Tag,
            color = toneColor,
        )
    }
}

/**
 * Etiqueta semántica de estado con fondo tonal (Tag de la referencia):
 * par texto/contenedor del tono garantizado por AqSchemeTest sobre el
 * bloque tonal. No interactiva; el estado se expresa con TEXTO (nunca
 * solo color). Para el estilo outline usa [AqBadge]; [round] activa la
 * variante píldora.
 */
@Composable
fun AqTonalTag(
    text: String,
    modifier: Modifier = Modifier,
    tone: AqTone = AqTone.Neutral,
    /** Mayúsculas tipadas (p. ej. "PAGO"): presentación, no copy nuevo. */
    uppercase: Boolean = false,
    /** Variante píldora (Tag `round` de la referencia). */
    round: Boolean = false,
) {
    val label = if (uppercase) text.uppercase() else text
    Box(
        modifier = modifier
            .background(tone.container(), if (round) AqShapes.Pill else AqShapes.ExtraSmall)
            .padding(horizontal = AqSpacing.Sm, vertical = AqSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AqTextStyles.Tag,
            color = tone.content(),
        )
    }
}

/**
 * Insignia de identidad tipada: glifo del tono sobre el bloque tonal
 * (p. ej. flecha entrante verde = cobro, saliente azul = pago). El glifo
 * es decorativo — el TEXTO de la fila comunica el tipo; la forma
 * acompaña, nunca es la única señal (contrato de accesibilidad). Tamaño
 * de tile 40 dp (mismo tile que la marca tipada de fila).
 */
@Composable
fun AqIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tone: AqTone = AqTone.Neutral,
) {
    Box(
        modifier = modifier
            .background(tone.container(), AqShapes.Large)
            .padding(AqSpacing.Sm),
        contentAlignment = Alignment.Center,
    ) {
        AqIcon(
            imageVector = icon,
            contentDescription = null,
            tint = tone.content(),
        )
    }
}

/**
 * Insignia de identidad de contexto (M4): círculo de 40 dp con el color
 * del contexto derivado del id. El color comunica la identidad visual del
 * contexto; el nombre en la fila es la señal primaria. No interactiva.
 */
@Composable
fun AqContextBadge(
    contextId: String,
    modifier: Modifier = Modifier,
) {
    val color = accentFor(contextId).background
    Box(
        modifier = modifier
            .size(AqMetrics.ContextBadgeSize)
            .background(color, AqShapes.Pill),
        contentAlignment = Alignment.Center,
    ) {}
}

/**
 * Fila de metadatos compacta (label + valor): etiqueta secundaria a la
 * izquierda, valor principal alineado a la derecha y acción final
 * opcional (p. ej. copiar). Para valores largos multilinea (notas) usa
 * texto aparte, no esta fila.
 */
@Composable
fun AqMetaRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    /** Glifo decorativo del concepto de la fila (opcional). */
    icon: ImageVector? = null,
    /** Acción final compacta (p. ej. copiar el valor). */
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        icon?.let {
            AqIcon(imageVector = it, contentDescription = null, tint = AqColor.TextTertiary)
        }
        Text(label, style = AqTextStyles.Body, color = AqColor.TextSecondary)
        Text(
            value,
            modifier = Modifier.weight(1f),
            style = AqTextStyles.Body,
            color = AqColor.TextPrimary,
            textAlign = TextAlign.End,
        )
        trailing?.invoke()
    }
}

/**
 * Tarjeta presionable del sistema: superficie de [AqListBody] con la
 * interacción y semántica de botón viviendo en el DS (feature/ no compone
 * clickable crudo). Para filas estándar usa [AqListRow].
 */
@Composable
fun AqTappableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = AqShapes.Medium
    // TDCell usa `GestureDetector` con color activo en `onTapDown`: la
    // pulsación se resuelve con el token activo, sin ripple Material.
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aqElevation(AqElevation.Shadow1, shape)
            .background(if (pressed) AqColor.SurfaceActive else AqColor.Surface, shape)
            .border(AqMetrics.Border, AqColor.Border, shape)
            .clickable(
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(AqSpacing.Md),
    ) {
        content()
    }
}

/**
 * Acción rápida de Inicio: icono tonal de 48 dp y etiqueta centrada,
 * ambos dentro de un único objetivo táctil accesible.
 */
@Composable
fun AqQuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AqTappableCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            Box(
                modifier = Modifier
                    .size(AqMetrics.GridIconContainerSize)
                    .background(AqColor.InfoContainer, AqShapes.GridIcon),
                contentAlignment = Alignment.Center,
            ) {
                AqIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AqColor.Info,
                )
            }
            AqText(
                text = label,
                size = AqType.Caption,
                color = AqColor.TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Hairline del sistema: separador sutil entre filas/controles dentro de
 * una tarjeta. Los bordes y separadores viven en el DS (gate E-01/02/03);
 * las pantallas componen este componente, nunca divisores de M3.
 */
@Composable
fun AqHairline(
    modifier: Modifier = Modifier,
    /** Alineación con el contenido cuando hay marca a la izquierda. */
    insetStart: Dp = AqSpacing.None,
) {
    HorizontalDivider(
        modifier = if (insetStart > AqSpacing.None) {
            Modifier.padding(start = insetStart)
        } else {
            Modifier
        }.then(modifier),
        thickness = AqMetrics.DividerHeight,
        color = AqColor.Divider,
    )
}

/**
 * Grupo de celdas sobre una superficie continua. Las filas usan
 * `showDivider` salvo la última; el cuerpo mide su contenido.
 */
@Composable
fun AqListBody(
    modifier: Modifier = Modifier,
    /** Padding interno del cuerpo. Por defecto 0 (las filas gestionan su
     *  propio padding). */
    contentPadding: PaddingValues = PaddingValues(AqSpacing.None),
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.Surface)
            .padding(contentPadding),
    ) {
        content()
    }
}

/**
 * Tarjeta de superficie para contenido libre (no filas de lista): mismo
 * radio 12, sombra E-1 (E-05) y borde hairline (E-03) que [AqListBody]
 * pero con padding interno por defecto ([AqSpacing.Lg]), para que el
 * contenido no toque el borde.
 *
 * - Usar [AqListBody] para agrupar filas de lista (padding 0).
 * - Usar [AqCard] para contenido libre (resúmenes, formularios, tarjetas
 *   de estado) que necesita aire interno.
 */
@Composable
fun AqCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(AqSpacing.Lg),
    content: @Composable () -> Unit,
) {
    val shape = AqShapes.Medium
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aqElevation(AqElevation.Shadow1, shape)
            .background(AqColor.Surface, shape)
            .border(AqMetrics.Border, AqColor.Border, shape)
            .padding(contentPadding),
    ) {
        content()
    }
}

/**
 * Celda de lista: título, subtítulo, metadatos en línea propia, acción
 * final e identidad de contexto opcional — el patrón de las pantallas de
 * listas de Agenda QR.
 *
 * - La altura crece con el contenido y la escala de fuente; se prohíben
 *   alturas fijas que recorten.
 * - [accent] es la identidad REAL del contexto (derivada del id): un
 *   punto de acento a la izquierda; nunca una barra ornamental.
 * - [selected] expone la selección en la semántica (nunca solo color).
 * - [trailing] son las acciones de fila: botones de icono, no enlaces de
 *   texto.
 * - [showChevron] añade la flecha de la referencia (List.Item `arrow`,
 *   decorativa) cuando la fila navega y no tiene acciones finales.
 * - [endMeta] es un metadato CORTO de solo texto (fecha, estado o
 *   importe sin formato de acción) alineado a la derecha en la MISMA
 *   línea del título, como en las listas de chats y facturas. Coexiste
 *   con un valor no interactivo en [trailing] (p. ej. un importe); NO
 *   coexiste con acciones en [trailing] ni con [showChevron] (misma zona
 *   final). Si la fila es angosta o la fuente del sistema llega al 200 %
 *   el metadato pasa a su propia línea bajo el título, sin recortes.
 * - [showDivider] añade el separador inferior de la lista agrupada; lo
 *   activa la pantalla que agrupa celdas contiguas.
 */
@Composable
fun AqListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    /** Identidad de contexto: punto de acento derivado del id. */
    accent: Color? = null,
    /** Identidad tipada del contenido (p. ej. cobro/pago); sustituye al
     * punto de acento. El glifo es decorativo: el texto comunica. */
    leading: (@Composable () -> Unit)? = null,
    /** selección explícita con semántica `selected`. */
    selected: Boolean = false,
    /** Metadato en línea PROPIA, bajo el subtítulo (p. ej. el badge de
     * sincronización): no compite por el ancho con la acción de la fila.
     * PROHIBIDO: acciones interactivas (botones/enlaces) aquí; las acciones
     * de fila viven en [trailing] o fuera del meta. */
    meta: (@Composable () -> Unit)? = null,
    /** Flecha decorativa de navegación (List.Item `arrow`); solo en filas
     * que navegan y sin acciones finales. */
    showChevron: Boolean = false,
    /** Separador inferior de la lista agrupada. */
    showDivider: Boolean = false,
    /** Límite de líneas para el título (default 1 con elipsis). */
    titleMaxLines: Int = 1,
    /** Límite de líneas para el subtítulo (default 1 con elipsis). */
    subtitleMaxLines: Int = 1,
    /** Metadato corto de solo texto alineado a la derecha en la línea del
     * título (fecha/estado/importe). Exclusivo con [showChevron]; con
     * acciones en [trailing] no se usa (el texto lo pasa la pantalla). */
    endMeta: String? = null,
) {
    require(endMeta == null || !showChevron) {
        "AqListRow endMeta y showChevron compiten en la zona final: usa uno solo"
    }
    Column(modifier = modifier) {
        BoxWithConstraints {
            // Fuente grande o fila angosta: el metadato no compite con el
            // título y baja a su propia línea.
            val stackEndMeta = endMeta != null && (
                maxWidth <= AqMetrics.ListRowEndMetaMinWidth ||
                    LocalDensity.current.fontScale >= AqMetrics.ListRowEndMetaStackFontScale
                )
            // TDCell: color activo en la pulsación (sin ripple Material).
            val interactionSource = remember { MutableInteractionSource() }
            val pressed = interactionSource.collectIsPressedAsState().value
            ListItem(
                modifier = Modifier
                    .then(
                        if (onClick != null) {
                            Modifier.clickable(
                                role = Role.Button,
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onClick,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (selected) Modifier.semantics { this.selected = true } else Modifier,
                    ),
                colors = ListItemDefaults.colors(
                    containerColor = if (pressed && onClick != null) {
                        AqColor.SurfaceActive
                    } else {
                        AqColor.Transparent
                    },
                ),
                headlineContent = {
                    val shortMeta = endMeta
                    if (shortMeta == null) {
                        Text(
                            title,
                            style = AqTextStyles.RowTitle,
                            color = AqColor.TextPrimary,
                            maxLines = titleMaxLines,
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else if (stackEndMeta) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                        ) {
                            Text(
                                title,
                                style = AqTextStyles.RowTitle,
                                color = AqColor.TextPrimary,
                                maxLines = titleMaxLines,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                shortMeta,
                                style = AqTextStyles.Body,
                                color = AqColor.TextTertiary,
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                        ) {
                            Text(
                                title,
                                modifier = Modifier.weight(1f),
                                style = AqTextStyles.RowTitle,
                                color = AqColor.TextPrimary,
                                maxLines = titleMaxLines,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                shortMeta,
                                style = AqTextStyles.BodySmall,
                                color = AqColor.TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                supportingContent = if (subtitle != null || meta != null) {
                    {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                        ) {
                            subtitle?.let {
                                Text(
                                    it,
                                    style = AqTextStyles.Body,
                                    color = AqColor.TextSecondary,
                                    maxLines = subtitleMaxLines,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            meta?.invoke()
                        }
                    }
                } else {
                    null
                },
                leadingContent = leading ?: accent?.let { color ->
                    {
                        Box(
                            modifier = Modifier
                                .size(AqSpacing.Md)
                                .background(color, AqShapes.Pill),
                        )
                    }
                },
                trailingContent = trailing ?: if (showChevron) {
                    {
                        AqIcon(
                            imageVector = AqIcons.ChevronRight,
                            contentDescription = null,
                            size = AqMetrics.IconSizeCompact,
                            tint = AqColor.TextTertiary,
                        )
                    }
                } else {
                    null
                },
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = AqMetrics.ListDividerInset),
                thickness = AqMetrics.DividerHeight,
                color = AqColor.Divider,
            )
        }
    }
}

package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    val shape = if (round) AqShapes.Pill else AqShapes.Medium
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
            .background(tone.container(), if (round) AqShapes.Pill else AqShapes.Medium)
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
    val shape = AqShapes.Large
    // TDCell usa `GestureDetector` con color activo en `onTapDown`: la
    // pulsación se resuelve con el token activo, sin ripple Material.
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .aqElevation(AqElevation.Shadow1, shape)
            .background(if (pressed) AqColor.SurfaceActive else AqColor.Surface, shape)
            .border(AqMetrics.Border, AqColor.Border, shape)
            .semantics(mergeDescendants = true) {}
            .clickable(
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .testTag("aq-tappable-card")
            .padding(AqSpacing.Lg),
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
    val shape = AqShapes.Large
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .aqElevation(AqElevation.Shadow1, shape)
            .background(AqColor.Surface, shape)
            .border(AqMetrics.Border, AqColor.Border, shape)
            .testTag("aq-list-body")
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
    val shape = AqShapes.Large
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .aqElevation(AqElevation.Shadow1, shape)
            .background(AqColor.Surface, shape)
            .border(AqMetrics.Border, AqColor.Border, shape)
            .testTag("aq-card")
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
            // Fila propia (no ListItem de M3): el material publicado pide
            // 12 dp verticales y M3 impone una altura mínima que centra el
            // contenido y diluye ese relleno. El relleno vive aquí, dentro
            // del objetivo táctil (48 dp mínimo por contenido de una línea).
            val rowModifier = Modifier
                .fillMaxWidth()
                .then(
                    if (selected) {
                        Modifier.background(AqColor.SelectionSurface, AqShapes.Large)
                            .border(AqMetrics.Border, AqColor.Border, AqShapes.Large)
                    } else if (pressed && onClick != null) {
                        Modifier.background(AqColor.SurfaceActive)
                    } else {
                        Modifier.background(AqColor.Transparent)
                    },
                )
                .clip(AqShapes.Large)
                .semantics(mergeDescendants = true) {}
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
                )
                .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.MdPlus)
            Row(
                modifier = rowModifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leading?.let { content ->
                    Box(Modifier.padding(end = AqSpacing.Lg)) { content() }
                } ?: accent?.let { color ->
                    Box(
                        modifier = Modifier
                            .padding(end = AqSpacing.Lg)
                            .size(AqSpacing.Md)
                            .background(color, AqShapes.Pill),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
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
                    if (subtitle != null || meta != null) {
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
                }
                val trailingContent: (@Composable () -> Unit)? =
                    trailing ?: if (showChevron) {
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
                    }
                trailingContent?.let { content ->
                    Box(Modifier.padding(start = AqSpacing.Lg)) { content() }
                }
            }
        }
        // El divisor separa filas vecinas; nunca atraviesa la superficie
        // de una fila seleccionada (la selección debe percibirse como una
        // unidad visual completa).
        if (showDivider && !selected) {
            HorizontalDivider(
                // Márgenes simétricos: el divisor pertenece al contenido
                // interior de la lista, no debe extenderse hasta el borde
                // ni quedar asymmetrico respecto al relleno de la fila.
                modifier = Modifier
                    .padding(horizontal = AqMetrics.ListDividerInset)
                    .testTag("aq-list-divider"),
                thickness = AqMetrics.DividerHeight,
                color = AqColor.Divider,
            )
        }
    }
}

/**
 * Panel expandible justificado por el flujo de registro de operación
 * (S07 "Más detalles"): título + chevron que alterna entre colapsado y
 * expandido, con el contenido revelado debajo. El estado de expansión lo
 * posee el llamador; el DS solo renderiza y anuncia el estado.
 *
 * - Radio de tarjeta (Large / 12 dp), superficie y borde de tokens (E-02/E-03).
 * - Objetivo táctil ≥ 48 dp en toda la fila de título.
 * - Semántica `expanded` anunciada al lector de pantalla.
 * - Sin movimiento decorativo; la animación de aparición delegada al llamador.
 */
@Composable
fun AqExpansionPanel(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    /** Glifo decorativo opcional a la izquierda del título. */
    leading: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = AqShapes.Large
    val chevronIcon = if (expanded) AqIcons.ArrowUp else AqIcons.ChevronRight
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AqColor.Surface, shape)
            .border(AqMetrics.Border, AqColor.Border, shape)
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                .clickable(
                    role = Role.Button,
                    onClickLabel = title,
                    onClick = onToggle,
                )
                // Compose 1.8 no expone un estado `expanded` booleano; la
                // señal accesible del disclosure es la acción Expandir/
                // Contraer que el lector de pantalla anuncia en el header.
                .semantics {
                    if (expanded) {
                        collapse { onToggle(); true }
                    } else {
                        expand { onToggle(); true }
                    }
                }
                .testTag("aq-expansion-header")
                .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.MdPlus),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            leading?.invoke()
            Text(
                text = title,
                style = AqTextStyles.RowTitle,
                color = AqColor.TextPrimary,
                modifier = Modifier.weight(1f),
            )
            AqIcon(
                imageVector = chevronIcon,
                contentDescription = null,
                size = AqMetrics.IconSizeCompact,
                tint = AqColor.TextTertiary,
            )
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Sm)
                    .fillMaxWidth()
                    .testTag("aq-expansion-content"),
            ) {
                content()
            }
        }
    }
}

/**
 * Carrusel horizontal de tarjetas justificado por el carrusel de favoritos
 * de Inicio (S01). La siguiente tarjeta asoma como indicio visual, sin
 * instrucción de deslizamiento (UX V1: los gestos nunca son la única forma
 * de acción; el scroll horizontal es suficiente).
 *
 * - Ítems con ancho fijo y objetivo táctil ≥ 48 dp.
 * - Espaciado entre ítems con tokens del sistema.
 * - Primer y último ítem con margen de pantalla para alinearse al borde
 *   y que el siguiente asome.
 * - Sin animación decorativa; el scroll es nativo de la plataforma.
 * - Semántica de accesibilidad por ítem (contentDescription del llamador).
 */
data class AqCarouselItem(val id: String, val label: String? = null)

@Composable
fun AqCarousel(
    items: List<AqCarouselItem>,
    itemWidth: Dp = AqMetrics.FavCardWidth,
    itemSpacing: Dp = AqSpacing.Md,
    modifier: Modifier = Modifier,
    content: @Composable (AqCarouselItem) -> Unit,
) {
    require(items.isNotEmpty()) { "AqCarousel necesita al menos un ítem" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        items.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .then(
                        if (index == 0) {
                            Modifier.padding(start = AqSpacing.ScreenMargin)
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (index == items.lastIndex) {
                            Modifier.padding(end = AqSpacing.ScreenMargin)
                        } else {
                            Modifier
                        },
                    )
                    .testTag("aq-carousel-item-${item.id}"),
            ) {
                content(item)
            }
        }
    }
}

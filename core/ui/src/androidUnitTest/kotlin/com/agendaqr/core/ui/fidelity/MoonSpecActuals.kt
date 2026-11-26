package com.agendaqr.core.ui.fidelity

import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize

/**
 * Lado `Aq*` de la prueba de fidelidad Moon v1 (ADR-0050): las métricas que
 * el componente produce, expresadas con los mismos tokens que consume. La
 * prueba compara cada fila de [MoonSpecFixtures] (JSON del Figma) contra
 * estos valores con tolerancia 0 dp; un `null` indica que la métrica no es
 * un invariante del componente (p. ej. alto de un frame o padding derivado
 * del centrado) y no se compara.
 *
 * Cuando el JSON no especifica una métrica la fila llega con -1 y la prueba
 * la omite; las excepciones de cobertura (componentes sin familia en el
 * Figma v1) están en el contrato normativo con id E-nn.
 */
object MoonSpecActuals {

    /** Métricas en dp; `null` = no verificable para ese componente. */
    data class Actual(
        val width: Int? = null,
        val height: Int? = null,
        val radius: Int? = null,
        val padTop: Int? = null,
        val padRight: Int? = null,
        val padBottom: Int? = null,
        val padLeft: Int? = null,
        val gap: Int? = null,
        val border: Int? = null,
    )

    private fun i(dp: Dp): Int = dp.value.toInt()

    /** Token de tamaño Moon por etiqueta del fixture (2xs…2xl). */
    private fun sizeOf(label: String): Dp = when (label) {
        "6xs" -> AqSize.X6s
        "5xs" -> AqSize.X5s
        "4xs" -> AqSize.X4s
        "3xs" -> AqSize.X3s
        "2xs" -> AqSize.X2s
        "xs" -> AqSize.Xs
        "sm" -> AqSize.Sm
        "md" -> AqSize.Md
        "lg" -> AqSize.Lg
        "xl" -> AqSize.Xl
        "2xl" -> AqSize.X2l
        else -> throw IllegalArgumentException("tamaño Moon desconocido: '$label'")
    }

    /** Resuelve las métricas reales del componente para (aq, tamaño). */
    fun probe(aq: String, size: String): Actual? = when (aq) {
        "AqAccordion" -> {
            // Cabecera de altura fija; el padding horizontal de la cabecera
            // es `headerPadding` del enum de tamaño.
            val (height, padH) = when (size) {
                "sm" -> AqSize.Sm to AqSize.X4s
                "md" -> AqSize.Md to AqSize.X3s
                "lg" -> AqSize.Lg to AqSize.X3s
                else -> AqSize.Xl to AqSize.X2s // xl
            }
            Actual(
                height = i(height),
                radius = i(AqCorner.InteractiveSm),
                padRight = i(padH),
                padLeft = i(padH),
            )
        }

        "AqAlert" -> Actual(
            height = i(AqSize.Xl), // minHeight del componente
            radius = i(AqCorner.InteractiveSm),
            padTop = i(AqSize.X2s),
            padRight = i(AqSize.X2s),
            padBottom = i(AqSize.X2s),
            padLeft = i(AqSize.X2s),
            gap = i(AqSize.X4s),
        )

        "AqAuthCode" -> Actual(
            height = i(AqSize.Xl), // alto de casilla
            radius = i(AqCorner.InteractiveSm),
            gap = i(AqSize.X4s),
        )

        "AqAvatar" -> Actual(
            // El JSON registra radios de nodo interno (4/8/12/9999 según
            // variante); el avatar Moon es circular — no se compara.
            width = i(sizeOf(size)),
            height = i(sizeOf(size)),
        )

        "AqBottomSheet" -> Actual(
            radius = i(AqCorner.SurfaceMd), // spec r12 (corregido)
            // Los pads del fixture (8) son del marco de contenido; el
            // componente delega el espaciado al llamador (ver contrato).
        )

        "AqBreadcrumb" -> Actual(gap = i(AqLayout.BreadcrumbGap))

        "AqButton" -> {
            val height = when (size) {
                "xs" -> AqLayout.ButtonHeightXs
                "sm" -> AqLayout.ButtonHeightSm
                "md" -> AqLayout.ButtonHeightMd
                "lg" -> AqLayout.ButtonHeightLg
                else -> AqLayout.ButtonHeightXl
            }
            val padH = when (size) {
                "xs" -> AqLayout.ButtonPaddingHorizontalXs
                "sm" -> AqLayout.ButtonPaddingHorizontalSm
                "md" -> AqLayout.ButtonPaddingHorizontalMd
                "lg" -> AqLayout.ButtonPaddingHorizontalLg
                else -> AqLayout.ButtonPaddingHorizontalXl
            }
            // Spec: xs r4, sm/md/lg r8, xl r12.
            val radius = when (size) {
                "xs" -> AqCorner.InteractiveXs
                "xl" -> AqCorner.InteractiveMd
                else -> AqCorner.InteractiveSm
            }
            // El padding vertical efectivo depende del interlineado del
            // texto (tipografía del sistema por ADR-0044): no es invariante.
            Actual(
                height = i(height),
                radius = i(radius),
                padRight = i(padH),
                padLeft = i(padH),
            )
        }

        "AqCheckbox" -> Actual(width = i(AqSize.Xs), height = i(AqSize.Xs))

        "AqChip" -> {
            val (height, padH, radius) = when (size) {
                "sm" -> Triple(AqSize.Sm, AqSize.X4s, AqCorner.InteractiveXs)
                else -> Triple(AqSize.Md, AqSize.X3s, AqCorner.InteractiveSm)
            }
            Actual(
                height = i(height),
                radius = i(radius),
                padRight = i(padH),
                padLeft = i(padH),
            )
        }

        "AqCircularLoader" -> Actual(
            width = i(sizeOf(size)),
            height = i(sizeOf(size)),
        )

        "AqCircularProgress" -> Actual(
            width = i(sizeOf(size)),
            height = i(sizeOf(size)),
        )

        "AqCombobox", "AqSelect", "AqSearchInput", "AqTextInput" -> {
            // Campo de texto Moon: altura por tamaño, r8 en md/lg/xl y r4 en
            // sm (el JSON no fija radio en sm/lg de Select/Text input; la
            // fila llega con -1 y no se compara).
            val height = sizeOf(size)
            val radius = if (size == "sm") AqCorner.InteractiveXs else AqCorner.InteractiveSm
            Actual(
                height = i(height),
                radius = i(radius),
                border = if (aq == "AqCombobox") {
                    i(com.agendaqr.core.ui.theme.AqMetrics.Border)
                } else {
                    null
                },
            )
        }

        "AqDrawer" -> Actual(
            width = i(AqLayout.DrawerWidth),
            radius = i(AqCorner.SurfaceMd),
        )

        "AqDropdown" -> Actual(
            radius = i(AqCorner.InteractiveMd),
            padTop = i(AqLayout.DropdownContentPadding),
            padRight = i(AqLayout.DropdownContentPadding),
            padBottom = i(AqLayout.DropdownContentPadding),
            padLeft = i(AqLayout.DropdownContentPadding),
        )

        "AqEmptyPlaceholder" -> Actual(
            // Hueco entre icono/título/subtítulo/acción: spec 8 dp.
            gap = i(AqSize.X4s),
        )

        "AqLeaderboard" -> Actual(
            // Filas con alto mínimo md y separación de 12 dp.
            gap = i(AqSize.X3s),
        )

        "AqLinearProgress" -> Actual(height = i(sizeOf(size)))

        "AqMenuItem" -> Actual(
            height = i(AqSize.Md),
            radius = i(AqCorner.InteractiveSm),
        )

        "AqModal" -> Actual(
            radius = i(AqCorner.InteractiveMd), // spec r12 (corregido)
            padTop = i(AqSize.X4s),
            padRight = i(AqSize.X4s),
            padBottom = i(AqSize.X4s),
            padLeft = i(AqSize.X4s),
        )

        "AqPagination" -> Actual(
            height = i(AqSize.Sm),
            gap = i(AqSize.X5s),
        )

        "AqPillTab", "AqTabBar" -> {
            val height = if (size == "sm") AqSize.Sm else AqSize.Md
            // Padding horizontal de la pestaña: pill 12/16, barra 8/16
            // (spec del JSON; corregido en E-2x).
            val padH = when {
                aq == "AqPillTab" && size == "sm" -> AqSize.X3s
                aq == "AqPillTab" -> AqSize.X2s
                size == "sm" -> AqSize.X4s
                else -> AqSize.X2s
            }
            val radiusDp = if (aq == "AqPillTab") {
                if (size == "sm") AqCorner.InteractiveXs else AqCorner.InteractiveSm
            } else {
                // La barra subrayada/default no declara radio propio.
                null
            }
            Actual(
                height = i(height),
                radius = radiusDp?.let { i(it) },
                padRight = i(padH),
                padLeft = i(padH),
            )
        }

        "AqPopover" -> Actual(
            radius = i(AqCorner.InteractiveMd),
            padTop = i(AqLayout.PopoverContentPadding),
            padRight = i(AqLayout.PopoverContentPadding),
            padBottom = i(AqLayout.PopoverContentPadding),
            padLeft = i(AqLayout.PopoverContentPadding),
            // El gap de 4 del fixture lo aporta el contenido de demo; la
            // tarjeta no impone separación propia.
        )

        "AqProgressBar" -> Actual(height = i(AqSize.X4s)) // barra fija 8 dp

        "AqRadio" -> Actual(width = i(AqSize.Xs), height = i(AqSize.Xs))

        "AqSegmentedControl" -> {
            val height = if (size == "sm") AqSize.Md else AqSize.Lg
            Actual(
                height = i(height),
                radius = i(AqCorner.InteractiveMd),
                padTop = i(AqLayout.SegmentedControlPadding),
                padRight = i(AqLayout.SegmentedControlPadding),
                padBottom = i(AqLayout.SegmentedControlPadding),
                padLeft = i(AqLayout.SegmentedControlPadding),
                gap = i(AqLayout.SegmentedControlOuterGap),
            )
        }

        "AqSidebar" -> Actual(
            width = i(AqLayout.SidebarWidthExpanded),
            radius = i(AqCorner.SurfaceMd),
            padTop = i(AqSize.X4s),
            padRight = i(AqSize.X4s),
            padBottom = i(AqSize.X4s),
            padLeft = i(AqSize.X4s),
            gap = i(AqSize.X2s),
        )

        "AqSwitch" -> {
            // 2xs 32x16, xs 44x24, sm 60x32; pista y pulgar redondos.
            val (w, h) = when (size) {
                "2xs" -> 32 to 16
                "xs" -> 44 to 24
                else -> 60 to 32 // sm
            }
            Actual(width = w, height = h, radius = 9999)
        }

        "AqTable" -> Actual(
            // La altura del fixture (56) es del marco demo; la tabla no
            // fija alto propio (depende de header + filas).
        )

        "AqTag" -> {
            val height = if (size == "2xs") AqSize.X2s else AqSize.Xs
            Actual(
                height = i(height),
                radius = i(AqCorner.InteractiveXs),
                padRight = i(AqSize.X4s),
                padLeft = i(AqSize.X4s),
            )
        }

        "AqTextArea" -> Actual(
            height = i(AqLayout.TextAreaHeight),
            radius = i(AqCorner.InteractiveSm),
            border = i(com.agendaqr.core.ui.theme.AqMetrics.Border),
        )

        "AqTextInputGroup" -> Actual(
            // El alto del grupo es el de sus campos hijos; el fixture (56)
            // mide un marco con un solo campo xl.
            radius = i(AqCorner.InteractiveSm),
        )

        "AqToast" -> Actual(
            radius = i(AqCorner.SurfaceSm),
            padTop = i(AqSize.X2s),
            padRight = i(AqSize.X2s),
            padBottom = i(AqSize.X2s),
            padLeft = i(AqSize.X2s),
            gap = i(AqSize.X2s),
        )

        "AqTooltip" -> Actual(
            // El alto de la burbuja depende del texto; el fixture (40) es
            // una etiqueta concreta.
            radius = i(AqCorner.InteractiveXs),
            padTop = i(AqSize.X3s),
            padRight = i(AqSize.X3s),
            padBottom = i(AqSize.X3s),
            padLeft = i(AqSize.X3s),
            border = i(com.agendaqr.core.ui.theme.AqMetrics.Border),
        )

        else -> null
    }
}

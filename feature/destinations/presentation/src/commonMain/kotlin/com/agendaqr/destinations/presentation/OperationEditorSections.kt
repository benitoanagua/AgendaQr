package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAccordion
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAmountInput
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqChip
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqFieldState
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextArea
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.rememberAqFormFocus
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.*
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Selector de moneda (BOB / USD / otra). Extraído de OperationEditorScreen (A-11). */
@Composable
internal fun OperationCurrencySelector(
    currency: String,
    customOpen: Boolean,
    onCurrencyChange: (String) -> Unit,
    onCustomOpenChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqText(
            text = AppStrings.LabelMoneda,
            size = AqType.Label,
            color = AqColor.TextPrimary,
        )
        AqSegmentedControl(
            options = listOf(
                com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.MonedaBob),
                com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.MonedaUsd),
                com.agendaqr.core.ui.components.AqSegmentedOption(
                    label = if (customOpen && currency.isNotBlank()) currency.trim().uppercase() else AppStrings.MonedaOtra,
                ),
            ),
            selectedIndex = when {
                currency == AppStrings.MonedaBob -> 0
                currency == AppStrings.MonedaUsd -> 1
                customOpen -> 2
                else -> -1
            },
            onSelect = { index ->
                when (index) {
                    0 -> { onCurrencyChange(AppStrings.MonedaBob); onCustomOpenChange(false) }
                    1 -> { onCurrencyChange(AppStrings.MonedaUsd); onCustomOpenChange(false) }
                    2 -> {
                        if (currency == AppStrings.MonedaBob || currency == AppStrings.MonedaUsd) { onCurrencyChange("") }
                        onCustomOpenChange(true)
                    }
                }
            },
        )
        if (customOpen) {
            AqTextInput(
                label = AppStrings.CodigoMoneda,
                value = currency,
                onValueChange = onCurrencyChange,
                leadingIcon = com.agendaqr.core.ui.components.AqIcons.Wallet,
            )
        }
    }
}

/** Fecha y hora con atajos Hoy/Ayer. */
@Composable
internal fun OperationDateTimeSection(
    occurredAtMillis: Long,
    onPickDate: () -> Unit,
    onChange: (Long) -> Unit,
) {
    AqSectionTitle(text = AppStrings.FechaYHora)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        AqMenuItem(
            title = (relativeDayLabel(occurredAtMillis, com.agendaqr.destinations.domain.nowMillis())?.let { it + " · " } ?: "") + formatDateTime(occurredAtMillis),
            subtitle = AppStrings.Fecha,
            onClick = onPickDate,
            leading = {
                AqIcon(
                    imageVector = com.agendaqr.core.ui.components.AqIcons.Calendar,
                    contentDescription = null,
                    tint = AqColor.TextSecondary,
                )
            },
        )
        val quickDay = when (relativeDayLabel(occurredAtMillis, com.agendaqr.destinations.domain.nowMillis())) {
            AppStrings.Hoy -> 0
            AppStrings.Ayer -> 1
            else -> -1
        }
        AqSegmentedControl(
            options = listOf(
                com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.Hoy),
                com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.Ayer),
            ),
            selectedIndex = quickDay,
            onSelect = { index ->
                onChange(
                    when (index) {
                        0 -> com.agendaqr.destinations.domain.nowMillis()
                        1 -> minusCivilDays(com.agendaqr.destinations.domain.nowMillis(), 1)
                        else -> occurredAtMillis
                    },
                )
            },
        )
    }
}

/** Persona/entidad, sugerencias recientes y contexto. */
@Composable
internal fun OperationDetailsSection(
    person: String,
    onPersonChange: (String) -> Unit,
    operations: List<com.agendaqr.destinations.domain.Operation>,
    contexts: List<com.agendaqr.destinations.domain.Context>,
    contextSelection: ContextSelection,
    onOpenContextPicker: () -> Unit,
) {
    AqSectionTitle(text = AppStrings.DetallesDeLaActividad)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqTextInput(
            label = AppStrings.LabelPersonaOEntidad,
            value = person,
            onValueChange = onPersonChange,
            maxLength = FieldLimits.PersonaOEntidad,
            leadingIcon = com.agendaqr.core.ui.components.AqIcons.Store,
        )
        val counterparts = remember(operations) { recentCounterparts(operations) }
        if (counterparts.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                counterparts.forEachIndexed { index, c ->
                    AqChip(
                        text = c,
                        selected = index == counterparts.indexOf(person.trim()).takeIf { it >= 0 } ?: -1,
                        onClick = { onPersonChange(c) },
                    )
                }
            }
        }
        AqMenuItem(
            title = contextSelection.contextId?.let { id ->
                AppStrings.ParaContextoElegido + (contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico)
            } ?: AppStrings.ParaElegirContexto,
            subtitle = AppStrings.PuedesSinContexto,
            onClick = onOpenContextPicker,
            leading = {
                AqIcon(
                    imageVector = com.agendaqr.core.ui.components.AqIcons.QrCode,
                    contentDescription = null,
                    tint = AqColor.TextSecondary,
                )
            },
        )
    }
}

package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqCorner

/** Wrapper Moon para AqDialog. */
@Composable
fun AqMoonDialog(
    title: String,
    message: String? = null,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String? = null,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    confirmAsText: Boolean = false,
    destructive: Boolean = false,
) {
    AqDialog(
        title = title,
        message = message,
        confirmLabel = confirmLabel,
        onConfirm = onConfirm,
        dismissLabel = dismissLabel,
        onDismiss = onDismiss,
        modifier = modifier,
        content = content,
        onDismissRequest = onDismissRequest,
        confirmAsText = confirmAsText,
        destructive = destructive,
    )
}

/** AqTextField wrapper. */
@Composable
fun AqMoonTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    optionalLabel: String? = null,
    maxLength: Int? = null,
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null,
    size: AqTextInputSize = AqTextInputSize.Md,
) {
    AqTextInput(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        helperMessage = helperMessage,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailing = trailing,
        optionalLabel = optionalLabel,
        maxLength = maxLength,
        focusRequester = focusRequester,
        size = size,
    )
}

/** AqTextAction wrapper. */
@Composable
fun AqMoonTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    color: Color? = null,
    variant: AqTextActionVariant = AqTextActionVariant.Default,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(
        horizontal = AqSpacing.Md,
        vertical = AqSpacing.None,
    ),
) {
    AqTextAction(
        label = label,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        color = color,
        variant = variant,
        contentPadding = contentPadding,
    )
}

/** AqPrimaryButton wrapper. */
@Composable
fun AqMoonPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    busyDescription: String? = null,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Primary,
        enabled = enabled,
        icon = icon,
        isLoading = isLoading,
        busyDescription = busyDescription,
    )
}

/** AqSecondaryButton wrapper. */
@Composable
fun AqMoonSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    busyDescription: String? = null,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Text,
        enabled = enabled,
        icon = icon,
        isLoading = isLoading,
        busyDescription = busyDescription,
    )
}

/** AqDangerButton wrapper. */
@Composable
fun AqMoonDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    size: AqButtonSize = AqButtonSize.Large,
    isLoading: Boolean = false,
    busyDescription: String? = null,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Danger,
        enabled = enabled,
        icon = icon,
        size = size,
        isLoading = isLoading,
        busyDescription = busyDescription,
    )
}

/** AqChip wrapper. */
@Composable
fun AqMoonChip(
    text: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier,
    size: AqChipSize = AqChipSize.Md,
) {
    AqChip(
        text = text,
        selected = selected,
        enabled = enabled,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        size = size,
    )
}

/** AqTag wrapper. */
@Composable
fun AqMoonTag(
    text: String,
    tone: AqTone = AqTone.Neutral,
    size: AqTagSize = AqTagSize.Xs,
    modifier: Modifier = Modifier,
) {
    AqTag(
        text = text,
        tone = tone,
        size = size,
        modifier = modifier,
    )
}

/** Wrapper Moon para AqScreenScaffold. */
@Composable
fun AqMoonScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    navDestinations: List<AqNavDestination> = emptyList(),
    navSelectedId: String? = null,
    onNavSelect: (String) -> Unit = {},
    snackbarHost: (@Composable () -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    statusContent: (@Composable () -> Unit)? = null,
    scrollable: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: androidx.compose.foundation.layout.Arrangement.Vertical = androidx.compose.foundation.layout.Arrangement.spacedBy(AqSpacing.Lg),
    contentWindowInsets: androidx.compose.foundation.layout.WindowInsets = androidx.compose.material3.ScaffoldDefaults.contentWindowInsets,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    AqScreenScaffold(
        modifier = modifier,
        topBar = topBar,
        navDestinations = navDestinations,
        navSelectedId = navSelectedId,
        onNavSelect = onNavSelect,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        bottomAction = bottomAction,
        statusContent = statusContent,
        scrollable = scrollable,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        contentWindowInsets = contentWindowInsets,
        content = content,
    )
}

/** Wrapper Moon para AqIconButton. */
@Composable
fun AqMoonIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    AqIconButton(
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        if (icon != null) {
            AqIcon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

/** Wrapper Moon para AqEmptyState. */
@Composable
fun AqMoonEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    AqEmptyState(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        actionLabel = actionLabel,
        onAction = onAction,
    )
}

/** Wrapper Moon para AqStatusBanner. */
@Composable
fun AqMoonStatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: AqTone? = null,
    title: String? = null,
    action: AqBannerAction? = null,
    dismiss: AqBannerAction? = null,
    subtitleMaxLines: Int? = null,
    showChevron: Boolean = false,
) {
    AqStatusBanner(
        message = message,
        modifier = modifier,
        tone = tone,
        title = title,
        action = action,
        dismiss = dismiss,
    )
}

/** AqMoonListRow: fila de lista sobre AqMenuItem (Moon). */
@Composable
fun AqMoonListRow(
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    subtitleMaxLines: Int? = null,
    showChevron: Boolean = false,
) {
    AqMenuItem(
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        showDivider = showDivider,
        leading = leading,
        trailing = trailing,
        selected = selected,
        modifier = modifier,
    )
}

/** AqMoonListBody: cuerpo de lista sobre Column (Moon). */
@Composable
fun AqMoonListBody(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        content()
    }
}

/** AqMoonMetaRow: fila meta con etiqueta y valor. */
@Composable
fun AqMoonMetaRow(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    meta: (@Composable () -> Unit)? = null,
    endMeta: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AqText(
            text = label,
            size = AqType.Body,
            color = AqColor.TextSecondary,
        )
        value?.let {
            AqText(
                text = it,
                size = AqType.Body,
                color = AqColor.TextPrimary,
                modifier = Modifier.weight(1f),
            )
        }
        meta?.invoke()
        endMeta?.invoke()
    }
}

/** Wrapper Moon para AqCompactAction. */
@Composable
fun AqMoonCompactAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    prominent: Boolean = false,
) {
    AqCompactAction(
        label = label,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        prominent = prominent,
    )
}

/** Wrapper Moon para AqBottomAction. */
@Composable
fun AqMoonBottomAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    busyDescription: String? = null,
) {
    AqBottomAction(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
    )
}

/** Wrapper Moon para AqBottomActionDock. */
@Composable
fun AqMoonBottomActionDock(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    AqBottomActionDock(
        modifier = modifier,
        content = content,
    )
}

/** Wrapper Moon para AqFeedbackEvent. */
@Composable
fun AqMoonFeedbackEvent(
    event: Any?,
    message: String,
    haptic: Boolean = true,
    feedbackHost: AqFeedbackHost? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    AqFeedbackEvent(
        event = event,
        message = message,
        haptic = haptic,
        feedbackHost = feedbackHost,
        actionLabel = actionLabel,
        onAction = onAction,
    )
}

/** Wrapper Moon para AqSnackbarHost. */
@Composable
fun AqMoonSnackbarHost(host: AqFeedbackHost) {
    AqSnackbarHost(host)
}

/** Wrapper Moon para AqSearchTrigger. */
@Composable
fun AqMoonSearchTrigger(
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AqSearchTrigger(
        label = label,
        placeholder = placeholder,
        onClick = onClick,
        modifier = modifier,
    )
}

/** AqMoonSelectionBar: barra de selección sobre piezas Moon. */
@Composable
fun AqMoonSelectionBar(
    closeLabel: String,
    selectAllLabel: String,
    selectedCountLabel: String,
    selectedCount: Int,
    totalCount: Int,
    selectAllChecked: Boolean,
    onSelectAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AqText(
                text = selectedCountLabel,
                size = AqType.Body,
                color = AqColor.TextPrimary,
                fontWeight = AqWeight.Medium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AqMoonTextAction(
                    label = closeLabel,
                    onClick = onClose,
                )
                AqMoonTextAction(
                    label = selectAllLabel,
                    onClick = onSelectAll,
                )
            }
        }
    }
}

/** Wrapper Moon para AqLoadMoreFooter. */
@Composable
fun AqMoonLoadMoreFooter(
    onLoadMore: () -> Unit,
    loadMoreLabel: String,
    loadingLabel: String,
    endLabel: String,
    modifier: Modifier = Modifier,
) {
    AqLoadMoreFooter(
        onLoadMore = onLoadMore,
        loadMoreLabel = loadMoreLabel,
        loadingLabel = loadingLabel,
        endLabel = endLabel,
        modifier = modifier,
    )
}

/** Wrapper Moon para AqCard. */
@Composable
fun AqMoonCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.Surface, AqSquircles.InteractiveSm)
            .padding(AqSpacing.Md),
    ) {
        content()
    }
}

/** Wrapper Moon para AqTappableCard. */
@Composable
fun AqMoonTappableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.Surface, AqSquircles.InteractiveSm)
            .padding(AqSpacing.Md)
            .clickable(onClick = onClick),
    ) {
        content()
    }
}

/** Wrapper Moon para AqSectionHeader. */
@Composable
fun AqMoonSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    inset: Boolean = true,
) {
    AqSectionHeader(
        text = text,
        modifier = modifier,
        accent = accent,
        inset = inset,
    )
}

/** Wrapper Moon para AqLoading. */
@Composable
fun AqMoonLoading(
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    AqLoading(
        modifier = modifier,
        message = message,
    )
}

/** Wrapper Moon para AqSkeleton. */
@Composable
fun AqMoonSkeleton(
    modifier: Modifier = Modifier,
    lines: Int = 3,
) {
    AqSkeleton(
        modifier = modifier,
        lines = lines,
    )
}

/** Wrapper Moon para AqDialogList. */
@Composable
fun AqMoonDialogList(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AqDialogList(
        modifier = modifier,
        content = content,
    )
}

/** Wrapper Moon para AqTabs. */
@Composable
fun AqMoonTabs(
    tabs: List<AqTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    enabled: Boolean = true,
    content: @Composable (Int) -> Unit,
) {
    AqTabs(
        tabs = tabs,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        modifier = modifier,
        accent = accent,
        enabled = enabled,
        content = content,
    )
}

/** Wrapper Moon para AqStatusPill. */
@Composable
fun AqMoonStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tone: AqTone = AqTone.Success,
    inverse: Boolean = false,
    trailing: (@Composable (() -> Unit))? = null,
) {
    AqStatusPill(
        text = text,
        modifier = modifier,
        icon = icon,
        tone = tone,
        inverse = inverse,
        trailing = trailing,
    )
}

/** AqMoonIconBadge: ícono insignia sobre AqIcon (Moon). */
@Composable
fun AqMoonIconBadge(
    icon: ImageVector,
    tone: AqTone = AqTone.Info,
    modifier: Modifier = Modifier,
) {
    val tint = when (tone) {
        AqTone.Info -> AqColor.Link
        AqTone.Success -> AqColor.Success
        AqTone.Danger -> AqColor.Danger
        AqTone.Warning -> AqColor.Warning
        else -> AqColor.TextSecondary
    }
    AqIcon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = modifier,
    )
}

/** AqMoonContextBadge: insignia de contexto sobre AqTag. */
@Composable
fun AqMoonContextBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    AqTag(
        text = text,
        tone = AqTone.Neutral,
        modifier = modifier,
    )
}

/** AqMoonTonalTag: etiqueta tonal sobre AqTonalTag (Moon). */
@Composable
fun AqMoonTonalTag(
    text: String,
    tone: AqTone = AqTone.Neutral,
    modifier: Modifier = Modifier,
    round: Boolean = false,
) {
    AqTonalTag(
        text = text,
        tone = tone,
        modifier = modifier,
        round = round,
    )
}

/** AqMoonBadge: insignia sobre AqTag. */
@Composable
fun AqMoonBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    AqTag(
        text = text,
        tone = AqTone.Neutral,
        modifier = modifier,
    )
}

/** Wrapper Moon para AqSection. */
@Composable
fun AqMoonSection(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable (() -> Unit))? = null,
    content: @Composable () -> Unit,
) {
    AqSection(
        title = title,
        modifier = modifier,
        trailing = trailing,
        content = content,
    )
}

/** Wrapper Moon para AqEvidenceBadge. */
@Composable
fun AqMoonEvidenceBadge(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tone: AqTone = AqTone.Success,
) {
    AqEvidenceBadge(
        text = text,
        icon = icon,
        modifier = modifier,
        tone = tone,
    )
}

/** Wrapper Moon para AqDatePickerDialog. */
@Composable
fun AqMoonDatePickerDialog(
    selectedDateUtc: Long?,
    title: String,
    onConfirm: (utcDateMillis: Long) -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String,
    dismissLabel: String,
    monthNames: List<String>,
    weekdays: List<String>,
    previousMonthLabel: String,
    nextMonthLabel: String,
) {
    AqDatePickerDialog(
        selectedDateUtc = selectedDateUtc,
        title = title,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmLabel = confirmLabel,
        dismissLabel = dismissLabel,
        monthNames = monthNames,
        weekdays = weekdays,
        previousMonthLabel = previousMonthLabel,
        nextMonthLabel = nextMonthLabel,
    )
}

/** Wrapper Moon para AqAmountField. */
@Composable
fun AqMoonAmountField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String? = null,
    onClear: (() -> Unit)? = null,
    clearLabel: String = "",
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
) {
    AqAmountField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        currencySymbol = currencySymbol,
        onClear = onClear,
        clearLabel = clearLabel,
        keyboardOptions = keyboardOptions,
    )
}

/** AqMoonExtendedFab: FAB extendido sobre AqButton. */
@Composable
fun AqMoonExtendedFab(
    label: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Primary,
        icon = icon,
        size = AqButtonSize.Large,
    )
}

/** AqMoonChipGroup: grupo de chips sobre Row. */
@Composable
fun AqMoonChipGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        content()
    }
}


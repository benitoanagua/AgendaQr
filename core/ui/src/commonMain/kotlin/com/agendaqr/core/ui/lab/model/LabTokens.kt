package com.agendaqr.core.ui.lab.model

/**
 * Index of the Xauxa token names that really exist in
 * `com.agendaqr.core.ui.theme` (XauxaColor / XauxaSpacing / XauxaMetrics /
 * XauxaType). Contracts may only reference tokens present here; the catalog
 * integrity check enforces it so the lab cannot document tokens that do not
 * exist. Keep this index in sync with the token objects: the lab renders the
 * real values, this list only validates references.
 */
object XauxaTokenIndex {

    val color: Set<String> = setOf(
        "XauxaColor.Background",
        "XauxaColor.Surface",
        "XauxaColor.Surface2",
        "XauxaColor.Surface3",
        "XauxaColor.Border",
        "XauxaColor.TextPrimary",
        "XauxaColor.TextSecondary",
        "XauxaColor.TextTertiary",
        "XauxaColor.Brand",
        "XauxaColor.BrandAccent",
        "XauxaColor.OnBrand",
        "XauxaColor.White",
        "XauxaColor.Success",
        "XauxaColor.Danger",
        "XauxaColor.Warning",
        "XauxaColor.Info",
        "XauxaColor.SuccessBg",
        "XauxaColor.DangerBg",
        "XauxaColor.WarningBg",
        "XauxaColor.InfoBg",
        "XauxaColor.FocusRing",
    )

    val spacing: Set<String> = setOf(
        "XauxaSpacing.None",
        "XauxaSpacing.Xs",
        "XauxaSpacing.Sm",
        "XauxaSpacing.Md",
        "XauxaSpacing.Lg",
        "XauxaSpacing.Xl",
        "XauxaSpacing.Xxl",
        "XauxaSpacing.Xxxl",
        "XauxaSpacing.Huge",
    )

    val metrics: Set<String> = setOf(
        "XauxaMetrics.Border",
        "XauxaMetrics.BorderStrong",
        "XauxaMetrics.Focus",
        "XauxaMetrics.ControlMinSize",
        "XauxaMetrics.ContentMaxWidth",
        "XauxaMetrics.QrPreviewSize",
        "XauxaMetrics.FavoriteIndicatorSize",
        "XauxaMetrics.BreakpointCompact",
        "XauxaMetrics.BreakpointMedium",
    )

    val type: Set<String> = setOf(
        "XauxaType.Display",
        "XauxaType.Headline",
        "XauxaType.Title",
        "XauxaType.Body",
        "XauxaType.Label",
        "XauxaType.Caption",
        "XauxaType.LetterSpacingWide",
        "XauxaType.FamilyUi",
        "XauxaType.FamilyMono",
    )

    val motion: Set<String> = setOf(
        "XauxaMotion.DurationShortMs",
        "XauxaMotion.DurationMediumMs",
        "XauxaMotion.DurationLongMs",
        "XauxaMotion.EasingStandard",
        "XauxaMotion.EasingEmphasized",
        "XauxaMotion.EasingDecelerate",
    )

    val all: Set<String> = color + spacing + metrics + type + motion

    fun isValid(token: String): Boolean = token in all
}

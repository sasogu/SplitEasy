package es.sasogu.spliteasy.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import es.sasogu.spliteasy.domain.AppEntry
import es.sasogu.spliteasy.domain.AppPair
import es.sasogu.spliteasy.domain.CapabilityLevel
import es.sasogu.spliteasy.domain.CapabilityReport
import es.sasogu.spliteasy.domain.DeviceFacts
import es.sasogu.spliteasy.domain.GuideVendor
import es.sasogu.spliteasy.domain.NavigationMode
import es.sasogu.spliteasy.ui.theme.SplitEasyTheme

private val previewApps = listOf(
    AppEntry("com.android.chrome", null, "Chrome"),
    AppEntry("com.openai.chatgpt", null, "ChatGPT"),
    AppEntry("com.google.android.youtube", null, "YouTube"),
    AppEntry("com.google.android.keep", null, "Notas"),
    AppEntry("com.google.android.gm", null, "Gmail"),
    AppEntry("com.google.android.calendar", null, "Calendario"),
)

private val previewPair = AppPair(
    id = "preview",
    packageA = "com.android.chrome",
    labelA = "Chrome",
    packageB = "com.openai.chatgpt",
    labelB = "ChatGPT",
    createdAt = 0L,
)

private val previewState = UiState(
    loadingApps = false,
    apps = previewApps,
    installedPackages = previewApps.mapTo(mutableSetOf()) { it.packageName },
    slotA = previewApps[0],
    slotB = previewApps[1],
    favorites = listOf(
        previewPair,
        AppPair(
            id = "preview-2",
            packageA = "com.google.android.youtube",
            labelA = "YouTube",
            packageB = "com.google.android.keep",
            labelB = "Notas",
            createdAt = 1L,
        ),
    ),
    facts = DeviceFacts(
        sdkInt = 35,
        manufacturer = "Google",
        brand = "google",
        model = "Pixel Tablet",
        freeformWindowManagement = true,
        lowRamDevice = false,
        smallestScreenWidthDp = 800,
    ),
    capability = CapabilityReport(
        level = CapabilityLevel.WINDOWED,
        canAttemptFreeform = true,
        freeformFailures = 0,
        experimentalAdjacentEnabled = false,
    ),
    navigationMode = NavigationMode.GESTURES,
    vendor = GuideVendor.PIXEL,
)

@Preview(name = "Inicio tableta", widthDp = 900, heightDp = 720, showBackground = true)
@Composable
private fun HomeTabletPreview() {
    SplitEasyTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            HomeScreen(
                state = previewState,
                onPickA = {},
                onPickB = {},
                onClearA = {},
                onClearB = {},
                onSwap = {},
                onOpenBoth = {},
                onSavePair = {},
                onOpenFavorite = {},
                onLoadFavorite = {},
                onPinFavorite = {},
                onDeleteFavorite = {},
                onHelp = {},
            )
        }
    }
}

@Preview(name = "Selector", widthDp = 380, heightDp = 760, showBackground = true)
@Composable
private fun PickerPreview() {
    SplitEasyTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppPickerScreen(
                state = previewState.copy(route = Route.PICKER, query = ""),
                onQueryChange = {},
                onSelect = {},
            )
        }
    }
}

@Preview(name = "Apertura manual", widthDp = 380, heightDp = 800, showBackground = true)
@Composable
private fun RunPreview() {
    SplitEasyTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            RunPairScreen(
                state = previewState.copy(
                    route = Route.RUN,
                    runPhase = RunPhase.MANUAL,
                ),
                onNavigationMode = {},
                onOpenDeveloperOptions = {},
                onRefreshCapability = {},
                onAttemptAutomatic = {},
                onConfirmAutomaticSuccess = {},
                onReportAutomaticFailure = {},
                onShowManualSteps = {},
                onPrepareManualLaunch = {},
                onLaunchFirst = {},
                onLaunchSecond = {},
                onManualSuccess = {},
                onSavePair = {},
                onPinShortcut = {},
                onFinish = {},
                onHelp = {},
            )
        }
    }
}

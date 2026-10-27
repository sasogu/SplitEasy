package es.sasogu.spliteasy.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt
import es.sasogu.spliteasy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitEasyRoot(viewModel: SplitEasyViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val navigationInsets = WindowInsets.navigationBars

    val bottomInsetDp = with(density) {
        navigationInsets.getBottom(this).toDp().value.roundToInt()
    }
    val leftInsetDp = with(density) {
        navigationInsets.getLeft(this, layoutDirection).toDp().value.roundToInt()
    }
    val rightInsetDp = with(density) {
        navigationInsets.getRight(this, layoutDirection).toDp().value.roundToInt()
    }

    LaunchedEffect(bottomInsetDp, leftInsetDp, rightInsetDp) {
        viewModel.updateNavigationInsets(bottomInsetDp, leftInsetDp, rightInsetDp)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message.text)
        }
    }

    BackHandler(enabled = state.route != Route.HOME) {
        viewModel.backToHome()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (state.route) {
                            Route.HOME -> stringResource(R.string.app_name)
                            Route.PICKER -> stringResource(
                                if (state.pickerSlot == Slot.A) {
                                    R.string.picker_title_a
                                } else {
                                    R.string.picker_title_b
                                },
                            )
                            Route.RUN -> stringResource(R.string.home_open_both)
                            Route.HELP -> stringResource(R.string.help_title)
                        },
                    )
                },
                navigationIcon = {
                    if (state.route != Route.HOME) {
                        IconButton(onClick = viewModel::backToHome) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                            )
                        }
                    }
                },
                actions = {
                    if (state.route == Route.HOME) {
                        TextButton(onClick = viewModel::goToHelp) {
                            Text(stringResource(R.string.action_help))
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
        ) {
            when (state.route) {
                Route.HOME -> HomeScreen(
                    state = state,
                    onPickA = { viewModel.openPicker(Slot.A) },
                    onPickB = { viewModel.openPicker(Slot.B) },
                    onClearA = { viewModel.clearSlot(Slot.A) },
                    onClearB = { viewModel.clearSlot(Slot.B) },
                    onSwap = viewModel::swapSlots,
                    onOpenBoth = viewModel::openBoth,
                    onSavePair = viewModel::saveCurrentPair,
                    onOpenFavorite = viewModel::openFavorite,
                    onLoadFavorite = viewModel::loadFavorite,
                    onPinFavorite = viewModel::pinFavorite,
                    onDeleteFavorite = viewModel::deleteFavorite,
                    onHelp = viewModel::goToHelp,
                )

                Route.PICKER -> AppPickerScreen(
                    state = state,
                    onQueryChange = viewModel::updateQuery,
                    onSelect = viewModel::selectApp,
                )

                Route.RUN -> RunPairScreen(
                    state = state,
                    onNavigationMode = viewModel::setNavigationMode,
                    onAttemptAutomatic = viewModel::attemptAutomatic,
                    onConfirmAutomaticSuccess = viewModel::confirmAutomaticSuccess,
                    onReportAutomaticFailure = viewModel::reportAutomaticFailure,
                    onShowManualSteps = viewModel::showManualSteps,
                    onPrepareManualLaunch = viewModel::prepareManualLaunch,
                    onLaunchFirst = { viewModel.launchSingle(Slot.A) },
                    onLaunchSecond = { viewModel.launchSingle(Slot.B) },
                    onManualSuccess = viewModel::markManualSuccess,
                    onSavePair = viewModel::saveCurrentPair,
                    onPinShortcut = viewModel::pinCurrentPair,
                    onFinish = viewModel::backToHome,
                    onHelp = viewModel::goToHelp,
                )

                Route.HELP -> HelpScreen(
                    experimentalEnabled = state.experimentalEnabled,
                    manufacturer = state.facts?.manufacturer.orEmpty(),
                    model = state.facts?.model.orEmpty(),
                    vendor = state.vendor,
                    onExperimentalChange = viewModel::setExperimentalEnabled,
                    onResetCapability = viewModel::resetCapability,
                )
            }
        }
    }
}

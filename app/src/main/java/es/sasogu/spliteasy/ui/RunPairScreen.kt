package es.sasogu.spliteasy.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.sasogu.spliteasy.R
import es.sasogu.spliteasy.domain.GuideVendor
import es.sasogu.spliteasy.domain.NavigationMode
import es.sasogu.spliteasy.ui.components.NavigationDiagram
import es.sasogu.spliteasy.ui.components.NavigationSelector
import es.sasogu.spliteasy.ui.components.PairHeader
import es.sasogu.spliteasy.ui.components.StepCard
import es.sasogu.spliteasy.ui.components.WarningCard
import es.sasogu.spliteasy.ui.components.WarningText

@Composable
fun RunPairScreen(
    state: UiState,
    onNavigationMode: (NavigationMode) -> Unit,
    onAttemptAutomatic: (Boolean) -> Unit,
    onConfirmAutomaticSuccess: () -> Unit,
    onReportAutomaticFailure: () -> Unit,
    onShowManualSteps: () -> Unit,
    onPrepareManualLaunch: () -> Unit,
    onLaunchFirst: () -> Unit,
    onLaunchSecond: () -> Unit,
    onManualSuccess: () -> Unit,
    onSavePair: () -> Unit,
    onPinShortcut: () -> Unit,
    onFinish: () -> Unit,
    onHelp: () -> Unit,
) {
    val first = state.slotA
    val second = state.slotB
    if (first == null || second == null) return

    val missing = state.pairHasMissingApp
    var experimentalDialogVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 780.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PairHeader(
                labelA = first.label,
                labelB = second.label,
                packageA = first.packageName,
                packageB = second.packageName,
            )

            if (missing) {
                WarningText(text = stringResource(R.string.home_favorite_missing_app))
            }

            when (state.runPhase) {
                RunPhase.ATTEMPTING -> AttemptingCard()

                RunPhase.ASK -> AskCard(
                    experimental = state.lastAttemptWasExperimental,
                    firstLabel = first.label,
                    secondLabel = second.label,
                    onYes = onConfirmAutomaticSuccess,
                    onNo = onReportAutomaticFailure,
                )

                RunPhase.SUCCESS -> SuccessCard(
                    pairSaved = state.pairSaved,
                    missing = missing,
                    onSavePair = onSavePair,
                    onPinShortcut = onPinShortcut,
                    onFinish = onFinish,
                )

                RunPhase.READY -> {
                    if (state.capability?.canAttemptFreeform == true && !missing) {
                        AutoAttemptCard(
                            onAttempt = { onAttemptAutomatic(false) },
                            onShowManual = onShowManualSteps,
                        )
                    } else {
                        ManualSection(
                            state = state,
                            missing = missing,
                            onNavigationMode = onNavigationMode,
                            onPrepareLaunch = onPrepareManualLaunch,
                            onLaunchFirst = onLaunchFirst,
                            onLaunchSecond = onLaunchSecond,
                            onDone = onManualSuccess,
                            onRetryAutomatic = {
                                if (state.capability?.canAttemptFreeform == true) {
                                    onAttemptAutomatic(false)
                                }
                            },
                            onExperimental = { experimentalDialogVisible = true },
                            onHelp = onHelp,
                        )
                    }
                }

                RunPhase.MANUAL -> ManualSection(
                    state = state,
                    missing = missing,
                    onNavigationMode = onNavigationMode,
                    onPrepareLaunch = onPrepareManualLaunch,
                    onLaunchFirst = onLaunchFirst,
                    onLaunchSecond = onLaunchSecond,
                    onDone = onManualSuccess,
                    onRetryAutomatic = {
                        if (state.capability?.canAttemptFreeform == true) {
                            onAttemptAutomatic(false)
                        }
                    },
                    onExperimental = { experimentalDialogVisible = true },
                    onHelp = onHelp,
                )
            }
        }
    }

    if (experimentalDialogVisible) {
        AlertDialog(
            onDismissRequest = { experimentalDialogVisible = false },
            title = { Text(stringResource(R.string.run_experimental_dialog_title)) },
            text = { Text(stringResource(R.string.run_experimental_dialog_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        experimentalDialogVisible = false
                        onAttemptAutomatic(true)
                    },
                ) {
                    Text(stringResource(R.string.run_experimental_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { experimentalDialogVisible = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun AttemptingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                strokeWidth = 3.dp,
            )
            Text(
                text = stringResource(R.string.run_auto_working),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun AskCard(
    experimental: Boolean,
    firstLabel: String,
    secondLabel: String,
    onYes: () -> Unit,
    onNo: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (experimental) {
                    stringResource(R.string.run_experimental_ask, firstLabel, secondLabel)
                } else {
                    stringResource(R.string.run_auto_question)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Button(
                onClick = onYes,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp),
            ) {
                Text(stringResource(R.string.run_auto_yes))
            }
            OutlinedButton(
                onClick = onNo,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp),
            ) {
                Text(stringResource(R.string.run_auto_no))
            }
        }
    }
}

@Composable
private fun SuccessCard(
    pairSaved: Boolean,
    missing: Boolean,
    onSavePair: () -> Unit,
    onPinShortcut: () -> Unit,
    onFinish: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(30.dp),
                )
                Text(
                    text = stringResource(R.string.run_success_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = stringResource(R.string.run_success_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            if (!pairSaved) {
                FilledTonalButton(
                    onClick = onSavePair,
                    enabled = !missing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                ) {
                    Text(stringResource(R.string.run_save_pair))
                }
            }
            OutlinedButton(
                onClick = onPinShortcut,
                enabled = !missing,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.5f,
                    ),
                ),
            ) {
                Text(stringResource(R.string.run_pin_shortcut))
            }
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
            ) {
                Text(stringResource(R.string.run_finish))
            }
        }
    }
}

@Composable
private fun AutoAttemptCard(
    onAttempt: () -> Unit,
    onShowManual: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.run_auto_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Text(
                text = stringResource(R.string.run_auto_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Button(
                onClick = onAttempt,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(stringResource(R.string.run_auto_button))
            }
            TextButton(
                onClick = onShowManual,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(R.string.run_manual_title))
            }
        }
    }
}

@Composable
private fun ManualSection(
    state: UiState,
    missing: Boolean,
    onNavigationMode: (NavigationMode) -> Unit,
    onPrepareLaunch: () -> Unit,
    onLaunchFirst: () -> Unit,
    onLaunchSecond: () -> Unit,
    onDone: () -> Unit,
    onRetryAutomatic: () -> Unit,
    onExperimental: () -> Unit,
    onHelp: () -> Unit,
) {
    val first = state.slotA ?: return
    val second = state.slotB ?: return

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.run_manual_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = stringResource(R.string.run_manual_intro),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                TextButton(
                    onClick = onHelp,
                    modifier = Modifier.align(Alignment.Start),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                ) {
                    Text(stringResource(R.string.home_capability_learn_more))
                }
            }
        }

        NavigationSelector(
            selected = state.navigationMode,
            onSelect = onNavigationMode,
        )

        StepCard(
            number = 1,
            title = stringResource(R.string.run_manual_prepare),
        ) {
            Text(
                text = stringResource(R.string.run_manual_prepare_note),
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onPrepareLaunch,
                enabled = !missing,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp),
            ) {
                Text(stringResource(R.string.run_manual_prepare))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onLaunchFirst,
                    enabled = !missing,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp),
                ) {
                    Text(
                        text = stringResource(R.string.run_manual_open_a, first.label),
                        maxLines = 2,
                    )
                }
                OutlinedButton(
                    onClick = onLaunchSecond,
                    enabled = !missing,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp),
                ) {
                    Text(
                        text = stringResource(R.string.run_manual_open_b, second.label),
                        maxLines = 2,
                    )
                }
            }
        }

        StepCard(
            number = 2,
            title = stringResource(R.string.run_manual_step2_title),
        ) {
            Text(
                text = when (state.navigationMode) {
                    NavigationMode.GESTURES -> stringResource(R.string.run_manual_step2_gestures)
                    NavigationMode.THREE_BUTTON -> stringResource(R.string.run_manual_step2_buttons)
                    NavigationMode.UNKNOWN -> stringResource(R.string.run_manual_step2_unknown)
                },
                style = MaterialTheme.typography.bodyMedium,
            )
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    NavigationDiagram(mode = state.navigationMode)
                }
            }
        }

        StepCard(
            number = 3,
            title = stringResource(R.string.run_manual_step3_title),
        ) {
            Text(
                text = vendorStepText(state.vendor, second.label),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.run_manual_step3_fallback),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        StepCard(
            number = 4,
            title = stringResource(R.string.run_manual_step4_title),
        ) {
            Text(
                text = stringResource(R.string.run_manual_step4, first.label),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        WarningCard(
            title = stringResource(R.string.run_manual_warning_title),
            body = stringResource(R.string.run_manual_warning_body),
        )

        Button(
            onClick = onDone,
            enabled = !missing,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(
                text = stringResource(R.string.run_manual_done),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (state.capability?.canAttemptFreeform == true) {
            TextButton(
                onClick = onRetryAutomatic,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(R.string.run_manual_try_again))
            }
        }

        if (state.experimentalEnabled) {
            TextButton(
                onClick = onExperimental,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(R.string.run_manual_experimental))
            }
            Text(
                text = stringResource(R.string.run_manual_experimental_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun vendorStepText(vendor: GuideVendor, secondLabel: String): String = when (vendor) {
    GuideVendor.SAMSUNG -> stringResource(R.string.run_manual_step3_samsung, secondLabel)
    GuideVendor.XIAOMI -> stringResource(R.string.run_manual_step3_xiaomi, secondLabel)
    GuideVendor.MOTOROLA -> stringResource(R.string.run_manual_step3_motorola, secondLabel)
    GuideVendor.PIXEL -> stringResource(R.string.run_manual_step3_pixel, secondLabel)
    GuideVendor.GENERIC -> stringResource(R.string.run_manual_step3_generic, secondLabel)
}

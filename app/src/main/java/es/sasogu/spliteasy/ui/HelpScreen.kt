package es.sasogu.spliteasy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.sasogu.spliteasy.BuildConfig
import es.sasogu.spliteasy.R
import es.sasogu.spliteasy.domain.CapabilityReport
import es.sasogu.spliteasy.domain.DeviceFacts
import es.sasogu.spliteasy.domain.GuideVendor
import es.sasogu.spliteasy.domain.NavigationMode
import es.sasogu.spliteasy.ui.components.ExpandableSection
import es.sasogu.spliteasy.ui.components.NavigationDiagram
import es.sasogu.spliteasy.ui.components.SplitDiagram
import es.sasogu.spliteasy.ui.components.WarningText

@Composable
fun HelpScreen(
    experimentalEnabled: Boolean,
    carMode: Boolean,
    facts: DeviceFacts?,
    capability: CapabilityReport?,
    manufacturer: String,
    model: String,
    vendor: GuideVendor,
    onExperimentalChange: (Boolean) -> Unit,
    onCarModeChange: (Boolean) -> Unit,
    onOpenDeveloperOptions: () -> Unit,
    onRefreshCapability: () -> Unit,
    onResetCapability: () -> Unit,
) {
    var resetDialogVisible by rememberSaveable { mutableStateOf(false) }
    var showAdbCommands by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 820.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ExpandableSection(
                title = stringResource(R.string.help_intro_title),
                initiallyExpanded = true,
            ) {
                Text(
                    text = stringResource(R.string.help_intro_body),
                    style = MaterialTheme.typography.bodyLarge,
                )
                SplitDiagram()
            }

            ExpandableSection(title = stringResource(R.string.help_nav_title)) {
                Text(
                    text = stringResource(R.string.help_nav_body),
                    style = MaterialTheme.typography.bodyLarge,
                )
                NavigationModeExample(
                    title = stringResource(R.string.run_nav_gestures),
                    mode = NavigationMode.GESTURES,
                )
                NavigationModeExample(
                    title = stringResource(R.string.run_nav_buttons),
                    mode = NavigationMode.THREE_BUTTON,
                )
            }

            ExpandableSection(title = stringResource(R.string.help_vendors_title)) {
                WarningText(text = stringResource(R.string.help_vendors_caveat))

                VendorRow(
                    title = stringResource(R.string.help_vendor_samsung),
                    body = stringResource(R.string.help_vendor_samsung_body),
                    highlighted = vendor == GuideVendor.SAMSUNG,
                )
                VendorRow(
                    title = stringResource(R.string.help_vendor_xiaomi),
                    body = stringResource(R.string.help_vendor_xiaomi_body),
                    highlighted = vendor == GuideVendor.XIAOMI,
                )
                VendorRow(
                    title = stringResource(R.string.help_vendor_motorola),
                    body = stringResource(R.string.help_vendor_motorola_body),
                    highlighted = vendor == GuideVendor.MOTOROLA,
                )
                VendorRow(
                    title = stringResource(R.string.help_vendor_pixel),
                    body = stringResource(R.string.help_vendor_pixel_body),
                    highlighted = vendor == GuideVendor.PIXEL,
                )
                VendorRow(
                    title = genericVendorTitle(manufacturer, model),
                    body = stringResource(R.string.help_vendor_generic_body),
                    highlighted = vendor == GuideVendor.GENERIC,
                )
            }

            ExpandableSection(title = stringResource(R.string.help_carplay_title)) {
                Text(
                    text = stringResource(R.string.help_carplay_body),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.car_mode_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = carMode,
                        onCheckedChange = onCarModeChange,
                    )
                }
                Text(
                    text = stringResource(R.string.car_mode_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Button(
                    onClick = onOpenDeveloperOptions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                ) {
                    Text(stringResource(R.string.car_mode_open_dev))
                }
                OutlinedButton(
                    onClick = onRefreshCapability,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                ) {
                    Text(stringResource(R.string.car_mode_check))
                }
                Text(
                    text = stringResource(R.string.car_mode_dev_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                TextButton(
                    onClick = { showAdbCommands = !showAdbCommands },
                    modifier = Modifier.align(Alignment.Start),
                ) {
                    Text(
                        text = stringResource(
                            if (showAdbCommands) {
                                R.string.car_mode_adb_hide
                            } else {
                                R.string.car_mode_adb_show
                            },
                        ),
                    )
                }
                if (showAdbCommands) {
                    Text(
                        text = stringResource(R.string.car_mode_adb_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SelectionContainer {
                            Text(
                                text = FREEFORM_ADB_COMMANDS,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.car_mode_unsupported),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (facts != null) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    )
                    Text(
                        text = stringResource(R.string.diag_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(
                            R.string.diag_android,
                            facts.androidRelease.ifBlank { facts.sdkInt.toString() },
                            facts.sdkInt,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = stringResource(
                            R.string.diag_model,
                            listOf(facts.manufacturer, facts.model)
                                .filter { it.isNotBlank() }
                                .joinToString(" ")
                                .ifBlank { "-" },
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = stringResource(
                            R.string.diag_freeform_feature,
                            yesNo(facts.freeformWindowManagement),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = stringResource(
                            R.string.diag_freeform_dev,
                            yesNo(facts.freeformDeveloperOption),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = stringResource(
                            R.string.diag_force_resizable,
                            yesNo(facts.forceResizableActivities),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = stringResource(
                            R.string.diag_car_mode,
                            yesNo(carMode),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (capability?.freeformViaDeveloperOption == true) {
                        Text(
                            text = stringResource(R.string.home_capability_windowed_dev_detail),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            ExpandableSection(title = stringResource(R.string.help_why_title)) {
                Text(
                    text = stringResource(R.string.help_why_body),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            ExpandableSection(title = stringResource(R.string.help_privacy_title)) {
                Text(
                    text = stringResource(R.string.help_privacy_body),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            ExpandableSection(title = stringResource(R.string.help_about_title)) {
                Text(
                    text = stringResource(
                        R.string.help_about_version,
                        BuildConfig.VERSION_NAME,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.help_about_license),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.help_about_source),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            ExpandableSection(title = stringResource(R.string.help_advanced_title)) {
                OutlinedButton(
                    onClick = { resetDialogVisible = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                ) {
                    Text(stringResource(R.string.help_advanced_reset))
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.help_advanced_experimental),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = experimentalEnabled,
                        onCheckedChange = onExperimentalChange,
                    )
                }
                Text(
                    text = stringResource(R.string.help_advanced_experimental_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_privacy_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
    }

    if (resetDialogVisible) {
        AlertDialog(
            onDismissRequest = { resetDialogVisible = false },
            title = { Text(stringResource(R.string.help_advanced_reset_dialog_title)) },
            text = { Text(stringResource(R.string.help_advanced_reset_dialog_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        resetDialogVisible = false
                        onResetCapability()
                    },
                ) {
                    Text(stringResource(R.string.help_advanced_reset_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { resetDialogVisible = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun NavigationModeExample(
    title: String,
    mode: NavigationMode,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            NavigationDiagram(mode = mode)
        }
    }
}

@Composable
private fun VendorRow(
    title: String,
    body: String,
    highlighted: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (highlighted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    color = if (highlighted) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                if (highlighted) {
                    Text(
                        text = stringResource(R.string.help_vendor_detected),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = if (highlighted) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
private fun yesNo(value: Boolean): String =
    stringResource(if (value) R.string.diag_yes else R.string.diag_no)

@Composable
private fun genericVendorTitle(manufacturer: String, model: String): String {
    val device = listOf(manufacturer, model)
        .filter { it.isNotBlank() }
        .joinToString(" ")
    return if (device.isBlank()) {
        stringResource(R.string.help_vendor_generic)
    } else {
        stringResource(R.string.help_vendor_other, device)
    }
}

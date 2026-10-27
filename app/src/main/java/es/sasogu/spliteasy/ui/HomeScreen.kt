package es.sasogu.spliteasy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.sasogu.spliteasy.R
import es.sasogu.spliteasy.domain.AppPair
import es.sasogu.spliteasy.ui.components.AppSlotCard
import es.sasogu.spliteasy.ui.components.CapabilityCard
import es.sasogu.spliteasy.ui.components.FavoriteRow
import es.sasogu.spliteasy.ui.components.SectionTitle
import es.sasogu.spliteasy.ui.components.WarningText

@Composable
fun HomeScreen(
    state: UiState,
    onPickA: () -> Unit,
    onPickB: () -> Unit,
    onClearA: () -> Unit,
    onClearB: () -> Unit,
    onSwap: () -> Unit,
    onOpenBoth: () -> Unit,
    onSavePair: () -> Unit,
    onOpenFavorite: (AppPair) -> Unit,
    onLoadFavorite: (AppPair) -> Unit,
    onPinFavorite: (AppPair) -> Unit,
    onDeleteFavorite: (AppPair) -> Unit,
    onHelp: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val wide = maxWidth >= 700.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.home_tagline),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(20.dp))

                if (wide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        AppSlotCard(
                            title = stringResource(R.string.home_slot_a),
                            app = state.slotA,
                            onChange = onPickA,
                            onClear = onClearA,
                            modifier = Modifier.weight(1f),
                        )
                        AppSlotCard(
                            title = stringResource(R.string.home_slot_b),
                            app = state.slotB,
                            onChange = onPickB,
                            onClear = onClearB,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    AppSlotCard(
                        title = stringResource(R.string.home_slot_a),
                        app = state.slotA,
                        onChange = onPickA,
                        onClear = onClearA,
                    )
                    Spacer(Modifier.height(10.dp))
                    AppSlotCard(
                        title = stringResource(R.string.home_slot_b),
                        app = state.slotB,
                        onChange = onPickB,
                        onClear = onClearB,
                    )
                }

                TextButton(
                    onClick = onSwap,
                    enabled = state.slotA != null && state.slotB != null,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(stringResource(R.string.action_swap))
                }

                Button(
                    onClick = onOpenBoth,
                    enabled = state.canOpenBoth && !state.pairHasMissingApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 10.dp),
                    )
                    Text(
                        text = stringResource(R.string.home_open_both),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_open_both_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                )

                if (state.pairHasMissingApp) {
                    Spacer(Modifier.height(12.dp))
                    WarningText(text = stringResource(R.string.home_favorite_missing_app))
                }

                Spacer(Modifier.height(16.dp))
                CapabilityCard(
                    report = state.capability,
                    onClick = onHelp,
                )

                Spacer(Modifier.height(16.dp))
                if (state.pairSaved) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp),
                            )
                            Text(
                                text = stringResource(R.string.home_pair_saved),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                } else {
                    FilledTonalButton(
                        onClick = onSavePair,
                        enabled = state.canOpenBoth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(stringResource(R.string.home_save_pair))
                    }
                }

                Spacer(Modifier.height(28.dp))
                SectionTitle(text = stringResource(R.string.home_favorites_title))
                Spacer(Modifier.height(10.dp))

                if (state.favorites.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_favorites_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (pair in state.favorites) {
                            val installed = !state.loadingApps &&
                                pair.packageA in state.installedPackages &&
                                pair.packageB in state.installedPackages
                            FavoriteRow(
                                pair = pair,
                                installed = installed,
                                onOpen = { onOpenFavorite(pair) },
                                onLoad = { onLoadFavorite(pair) },
                                onPin = { onPinFavorite(pair) },
                                onDelete = { onDeleteFavorite(pair) },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))
                Text(
                    text = stringResource(R.string.home_privacy_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
        }
    }
}

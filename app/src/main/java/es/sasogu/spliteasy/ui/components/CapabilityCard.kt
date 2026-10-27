package es.sasogu.spliteasy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.sasogu.spliteasy.R
import es.sasogu.spliteasy.domain.CapabilityLevel
import es.sasogu.spliteasy.domain.CapabilityReport

/**
 * Indicador del modo disponible. Nunca afirma compatibilidad basándose solo en
 * la versión de Android: usa la evidencia recogida en [CapabilityReport].
 */
@Composable
fun CapabilityCard(
    report: CapabilityReport?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val level = report?.level ?: CapabilityLevel.ASSISTED
    val title = stringResource(level.titleResource())
    val detail = stringResource(level.detailResource())
    val icon: ImageVector = when (level) {
        CapabilityLevel.SPLIT_CONFIRMED,
        CapabilityLevel.WINDOWED,
        -> Icons.Filled.CheckCircle

        CapabilityLevel.ASSISTED -> Icons.Filled.Info
        CapabilityLevel.UNCONFIRMED -> Icons.Filled.Warning
    }
    val container: Color = when (level) {
        CapabilityLevel.SPLIT_CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        CapabilityLevel.WINDOWED -> MaterialTheme.colorScheme.tertiaryContainer
        CapabilityLevel.ASSISTED -> MaterialTheme.colorScheme.secondaryContainer
        CapabilityLevel.UNCONFIRMED -> MaterialTheme.colorScheme.errorContainer
    }
    val content: Color = when (level) {
        CapabilityLevel.SPLIT_CONFIRMED -> MaterialTheme.colorScheme.onPrimaryContainer
        CapabilityLevel.WINDOWED -> MaterialTheme.colorScheme.onTertiaryContainer
        CapabilityLevel.ASSISTED -> MaterialTheme.colorScheme.onSecondaryContainer
        CapabilityLevel.UNCONFIRMED -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = container,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(28.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_capability_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = content.copy(alpha = 0.8f),
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = content,
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = content,
                )
                Text(
                    text = stringResource(R.string.home_capability_learn_more),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = content,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = content,
            )
        }
    }
}

private fun CapabilityLevel.titleResource(): Int = when (this) {
    CapabilityLevel.SPLIT_CONFIRMED -> R.string.home_capability_split
    CapabilityLevel.WINDOWED -> R.string.home_capability_windowed
    CapabilityLevel.ASSISTED -> R.string.home_capability_assisted
    CapabilityLevel.UNCONFIRMED -> R.string.home_capability_unconfirmed
}

private fun CapabilityLevel.detailResource(): Int = when (this) {
    CapabilityLevel.SPLIT_CONFIRMED -> R.string.home_capability_split_detail
    CapabilityLevel.WINDOWED -> R.string.home_capability_windowed_detail
    CapabilityLevel.ASSISTED -> R.string.home_capability_assisted_detail
    CapabilityLevel.UNCONFIRMED -> R.string.home_capability_unconfirmed_detail
}

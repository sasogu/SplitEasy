package es.sasogu.spliteasy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import es.sasogu.spliteasy.R
import es.sasogu.spliteasy.domain.NavigationMode

/** Esquema simple de una pantalla dividida con dos mitades. */
@Composable
fun SplitDiagram(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.diagram_split_cd)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description },
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(88.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DiagramHalf(
                label = "A",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primaryContainer,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            DiagramHalf(
                label = "B",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.secondaryContainer,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun DiagramHalf(
    label: String,
    modifier: Modifier,
    color: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}

/**
 * Esquema de la barra de navegación. No pretende ser una captura exacta: sirve
 * para que el paso de abrir Recientes sea reconocible.
 */
@Composable
fun NavigationDiagram(
    mode: NavigationMode,
    modifier: Modifier = Modifier,
) {
    when (mode) {
        NavigationMode.THREE_BUTTON -> ThreeButtonDiagram(modifier)
        NavigationMode.GESTURES -> GestureDiagram(modifier)
        NavigationMode.UNKNOWN -> Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GestureDiagram()
            ThreeButtonDiagram()
        }
    }
}

@Composable
private fun GestureDiagram(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.diagram_gestures_cd)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "↑",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(9.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurfaceVariant),
        )
    }
}

@Composable
private fun ThreeButtonDiagram(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.diagram_buttons_cd)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DiagramButton(label = "◁")
            DiagramButton(label = "○")
            DiagramButton(
                label = "□",
                highlighted = true,
            )
        }
    }
}

@Composable
private fun DiagramButton(
    label: String,
    highlighted: Boolean = false,
) {
    Surface(
        shape = CircleShape,
        color = if (highlighted) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = if (highlighted) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

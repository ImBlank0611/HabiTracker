package haus.saint.habittracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Simple bar chart: one bar per entry, height = value in [0f, 1f].
 * Used for "last N weeks' consistency" / "last N days completed".
 */
@Composable
fun WeeklyBarChart(
    values: List<Float>,
    labels: List<String>,
    accentColor: Color,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 120.dp,
    highlightLastIndex: Boolean = true
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            if (values.isEmpty()) return@Canvas
            val n = values.size
            val gap = size.width * 0.02f
            val barWidth = (size.width - gap * (n - 1)) / n
            val baselineY = size.height

            // baseline
            drawLine(
                color = gridColor,
                start = Offset(0f, baselineY),
                end = Offset(size.width, baselineY),
                strokeWidth = 1.dp.toPx()
            )

            values.forEachIndexed { index, value ->
                val x = index * (barWidth + gap)
                val barHeight = (size.height - 4.dp.toPx()) * value.coerceIn(0f, 1f)
                val isLast = highlightLastIndex && index == n - 1
                val color = if (value <= 0f) trackColor else accentColor.copy(alpha = if (isLast) 1f else 0.7f)
                val top = baselineY - barHeight
                val h = if (value <= 0f) 3.dp.toPx() else barHeight
                val drawTop = if (value <= 0f) baselineY - 3.dp.toPx() else top
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, drawTop),
                    size = Size(barWidth, h),
                    cornerRadius = CornerRadius(barWidth / 2.5f, barWidth / 2.5f)
                )
                if (isLast && value > 0f) {
                    drawRoundRect(
                        color = accentColor,
                        topLeft = Offset(x, drawTop),
                        size = Size(barWidth, h),
                        cornerRadius = CornerRadius(barWidth / 2.5f, barWidth / 2.5f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }
        if (labels.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

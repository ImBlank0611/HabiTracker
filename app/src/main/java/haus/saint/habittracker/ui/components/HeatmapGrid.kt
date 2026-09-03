package haus.saint.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/**
 * A GitHub-contributions-style grid: one column per week, one row per weekday,
 * darker cells for completed days. Cells for dates before the habit existed, or
 * in the future, are left blank.
 */
@Composable
fun HeatmapGrid(
    completedDates: Set<LocalDate>,
    createdAt: LocalDate,
    accentColor: Color,
    weeksToShow: Int = 14,
    today: LocalDate = LocalDate.now(),
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val emptyColor = Color.Transparent

    val currentWeekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val gridStartWeek = currentWeekStart.minusWeeks((weeksToShow - 1).toLong())

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        for (w in 0 until weeksToShow) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                for (d in 0..6) {
                    val date = gridStartWeek.plusWeeks(w.toLong()).plusDays(d.toLong())
                    val color = when {
                        date.isAfter(today) || date.isBefore(createdAt) -> emptyColor
                        date in completedDates -> accentColor
                        else -> trackColor
                    }
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color, RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}

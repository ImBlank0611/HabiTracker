package haus.saint.habittracker.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import haus.saint.habittracker.HabitTrackerApp
import java.time.LocalDate

class ToggleHabitAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val habitId = parameters[habitIdKey] ?: return
        val repository = (context.applicationContext as HabitTrackerApp).repository
        repository.toggleCompletion(habitId, LocalDate.now())
        HabitWidget().update(context, glanceId)
    }

    companion object {
        val habitIdKey = ActionParameters.Key<Long>("habitId")
    }
}

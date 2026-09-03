package haus.saint.habittracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import haus.saint.habittracker.HabitTrackerApp
import haus.saint.habittracker.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as HabitTrackerApp).repository

        setContent {
            HabitTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HabitTrackerNavHost(repository = repository)
                }
            }
        }
    }
}

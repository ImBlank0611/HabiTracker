package haus.saint.habittracker.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import haus.saint.habittracker.data.HabitRepository
import haus.saint.habittracker.ui.addhabit.AddEditHabitScreen
import haus.saint.habittracker.ui.addhabit.AddEditHabitViewModel
import haus.saint.habittracker.ui.detail.HabitDetailScreen
import haus.saint.habittracker.ui.detail.HabitDetailViewModel
import haus.saint.habittracker.ui.home.HomeScreen
import haus.saint.habittracker.ui.home.HomeViewModel

private object Routes {
    const val HOME = "home"
    const val ADD = "add"
    const val EDIT = "edit/{habitId}"
    const val DETAIL = "detail/{habitId}"

    fun edit(habitId: Long) = "edit/$habitId"
    fun detail(habitId: Long) = "detail/$habitId"
}

@Composable
fun HabitTrackerNavHost(repository: HabitRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = viewModel,
                onAddHabit = { navController.navigate(Routes.ADD) },
                onOpenHabit = { id -> navController.navigate(Routes.detail(id)) }
            )
        }

        composable(Routes.ADD) {
            val viewModel: AddEditHabitViewModel = viewModel(
                factory = AddEditHabitViewModel.Factory(repository, null)
            )
            AddEditHabitScreen(viewModel = viewModel, onDone = { navController.popBackStack() })
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            val viewModel: AddEditHabitViewModel = viewModel(
                factory = AddEditHabitViewModel.Factory(repository, habitId)
            )
            AddEditHabitScreen(viewModel = viewModel, onDone = { navController.popBackStack() })
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            val viewModel: HabitDetailViewModel = viewModel(
                factory = HabitDetailViewModel.Factory(repository, habitId)
            )
            HabitDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.edit(id)) }
            )
        }
    }
}

package cz.mendelu.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cz.mendelu.project.ui.screens.about.AboutScreen
import cz.mendelu.project.ui.screens.expenses.addedit.ExpensesAddEditScreen
import cz.mendelu.project.ui.screens.expenses.list.ExpensesListScreen
import cz.mendelu.project.ui.screens.map.MapScreen
import cz.mendelu.project.ui.screens.mlkit.MLKitScreen
import cz.mendelu.project.ui.screens.odometer.OdometerScreen
import cz.mendelu.project.ui.screens.payment.PaymentScreen
import cz.mendelu.project.ui.screens.routines.addedit.RoutineAddEditScreen
import cz.mendelu.project.ui.screens.routines.list.RoutinesListScreen
import cz.mendelu.project.ui.screens.statistics.StatisticsScreen
import cz.mendelu.project.ui.screens.summary.SummaryScreen
import cz.mendelu.project.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    navigationRouter: INavigationRouter = remember {
        NavigationRouterImpl(navController)
    },
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        // ------------------- SUMMARY -----------------------
        composable(Destination.SummaryScreen.route) {
            SummaryScreen(navigationRouter = navigationRouter)
        }

        composable(Destination.SummaryScreenSkipIntro.route) {
            SummaryScreen(navigationRouter = navigationRouter, skipIntro = true)
        }

        // ------------------- ABOUT -----------------------
        composable(Destination.AboutScreen.route) {
            AboutScreen(navigationRouter = navigationRouter/*, id = null*/)
        }

        // ------------------- MAP -----------------------
        composable(Destination.MapScreen.route) {
            MapScreen(navigationRouter = navigationRouter)
        }

        // ------------------- ODOMETER -----------------------
        composable(Destination.OdometerScreen.route) {
            OdometerScreen(navigationRouter = navigationRouter)
        }

        // ------------------- MLKIT -----------------------
        composable(Destination.MLKitScreen.route + "/{id}", arguments = listOf(
            navArgument(name = "id") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )) {
            val id = it.arguments?.getLong("id")
            MLKitScreen(navigationRouter = navigationRouter, id = id!!)
        }

        // ------------------- PAYMENT -----------------------
        composable(Destination.PaymentScreen.route) {
            PaymentScreen(navigationRouter = navigationRouter)
        }

        // ------------------- SETTINGS -----------------------
        composable(Destination.SettingsScreen.route) {
            SettingsScreen(navigationRouter = navigationRouter)
        }

        // ------------------- STATISTICS -----------------------
        composable(Destination.StatisticsScreen.route) {
            StatisticsScreen(navigationRouter = navigationRouter)
        }

        // ------------------- EXPENSES ----------------------

        composable(Destination.ExpensesListScreen.route) {
            ExpensesListScreen(navigationRouter = navigationRouter)
        }

        composable(Destination.ExpensesAddEditScreen.route) {
            ExpensesAddEditScreen(navigationRouter = navigationRouter, id = null)
        }

        composable(Destination.ExpensesAddEditScreen.route + "/{id}", arguments = listOf(
            navArgument(name = "id") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )) {
            val id = it.arguments?.getLong("id")
            ExpensesAddEditScreen(navigationRouter = navigationRouter, id = id!!)
        }

        // ------------------- ROUTINE -----------------------

        composable(Destination.RoutinesListScreen.route) {
            RoutinesListScreen(navigationRouter = navigationRouter)
        }

        composable(Destination.RoutineAddEditScreen.route) {
            RoutineAddEditScreen(navigationRouter = navigationRouter, id = null)
        }

        composable(Destination.RoutineAddEditScreen.route + "/{id}", arguments = listOf(
            navArgument(name = "id") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )) {
            val id = it.arguments?.getLong("id")
            RoutineAddEditScreen(navigationRouter = navigationRouter, id = id!!)
        }
    }
}
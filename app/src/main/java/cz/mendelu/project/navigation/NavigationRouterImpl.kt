package cz.mendelu.project.navigation

import androidx.navigation.NavController

class NavigationRouterImpl(private val navController: NavController) : INavigationRouter {

    override fun navigateToStatisticsScreen() {
        navController.navigate(Destination.StatisticsScreen.route)
    }

    override fun navigateToAboutScreen() {
        navController.navigate(Destination.AboutScreen.route)
    }

    override fun navigateToMapScreen() {
        navController.navigate(Destination.MapScreen.route)
    }

    override fun navigateToOdometerScreen() {
        navController.navigate(Destination.OdometerScreen.route)
    }

    override fun navigateToPaymentScreen() {
        navController.navigate(Destination.PaymentScreen.route)
    }

    override fun navigateToSettingsScreen() {
        navController.navigate(Destination.SettingsScreen.route)
    }

    override fun navigateToMLKitScreen(id: Long) {
        navController.navigate(Destination.MLKitScreen.route + "/" + id)
    }

    override fun navigateToRoutinesAddEditScreen(id: Long?) {
        if (id != null) {
            navController.navigate(Destination.RoutineAddEditScreen.route + "/" + id)
        } else {
            navController.navigate(Destination.RoutineAddEditScreen.route)
        }
    }

    override fun navigateToRoutinesListScreen() {
        navController.navigate(Destination.RoutinesListScreen.route)
    }

    override fun navigateToExpensesListScreen() {
        navController.navigate(Destination.ExpensesListScreen.route)
    }

    override fun navigateToExpensesAddEditScreen(id: Long?) {
        if (id != null) {
            navController.navigate(Destination.ExpensesAddEditScreen.route + "/" + id)
        } else {
            navController.navigate(Destination.ExpensesAddEditScreen.route)
        }
    }

    override fun returnBack() {
        navController.popBackStack()
    }

    override fun getNavController(): NavController {
        return navController
    }

}
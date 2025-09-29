package cz.mendelu.project.navigation

import androidx.navigation.NavController

interface INavigationRouter {

    fun returnBack()
    fun getNavController(): NavController?

    // --------------------------------------

    fun navigateToStatisticsScreen()

    fun navigateToAboutScreen()

    fun navigateToMapScreen()

    fun navigateToOdometerScreen()

    fun navigateToPaymentScreen()

    fun navigateToSettingsScreen()

    fun navigateToMLKitScreen(id: Long)

    fun navigateToRoutinesAddEditScreen(id: Long?)
    fun navigateToRoutinesListScreen()

    fun navigateToExpensesListScreen()
    fun navigateToExpensesAddEditScreen(id: Long?)

}
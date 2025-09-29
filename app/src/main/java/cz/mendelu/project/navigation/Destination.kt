package cz.mendelu.project.navigation

sealed class Destination (val route: String){
    object SummaryScreen : Destination("summary")

    object SummaryScreenSkipIntro : Destination("summarySkipIntro")

    object AboutScreen : Destination("about")

    object StatisticsScreen : Destination("statistics")

    object RoutinesListScreen : Destination("routines_list_screen")
    object RoutineAddEditScreen : Destination("add_edit_routine")

    object MapScreen : Destination("map")

    object OdometerScreen : Destination("odometer")

    object SettingsScreen : Destination("settings")

    object MLKitScreen : Destination("mlkit")

    object PaymentScreen : Destination("payment")

    object ExpensesAddEditScreen : Destination("expenses_add_edit_screen")
    object ExpensesListScreen : Destination("expenses_list_screen")

}
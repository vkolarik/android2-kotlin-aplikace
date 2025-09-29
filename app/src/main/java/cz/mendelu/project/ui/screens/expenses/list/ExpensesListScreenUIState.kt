package cz.mendelu.project.ui.screens.expenses.list

sealed class ExpensesListScreenUIState {
    class Loading : ExpensesListScreenUIState()
    class DataLoaded(val data: ExpensesListScreenData) : ExpensesListScreenUIState()

}

package cz.mendelu.project.ui.screens.expenses.addedit

sealed class ExpensesAddEditScreenUIState {
    object Loading : ExpensesAddEditScreenUIState()
    class DataChanged(val data: ExpensesAddEditScreenData) : ExpensesAddEditScreenUIState()
    object ExpenseSaved : ExpensesAddEditScreenUIState()
    object ExpenseDeleted : ExpensesAddEditScreenUIState()
    object DispatchedMLKit : ExpensesAddEditScreenUIState()
}
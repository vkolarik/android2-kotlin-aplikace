package cz.mendelu.project.ui.screens.expenses.addedit

interface ExpensesAddEditScreenActions {
    fun onExpensesAddEditScreenDataChanged(data: ExpensesAddEditScreenData)
    fun saveExpense()
    fun dispatchMLKitTask(description: String)
}
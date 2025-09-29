package cz.mendelu.project.ui.screens.expenses.addedit

data class ExpensesAddEditScreenErrors(
    var errorOccurred: Boolean = false,
    var expenseTypeError: Int? = null,
    var mileageError: Int? = null,
    var noteError: Int? = null,
    var amountError: Int? = null,
    var dateError: Int? = null,

)
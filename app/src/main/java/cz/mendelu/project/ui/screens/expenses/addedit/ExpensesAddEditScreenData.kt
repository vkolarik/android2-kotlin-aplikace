package cz.mendelu.project.ui.screens.expenses.addedit

import android.graphics.Bitmap
import android.icu.util.IslamicCalendar
import cz.mendelu.project.R
import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.ExpenseType

class ExpensesAddEditScreenData {
    var errors: ExpensesAddEditScreenErrors = ExpensesAddEditScreenErrors()

    var expense: Expense? = null

    var expenseType: ExpenseType = ExpenseType.OTHER
    var noteString: String = ""
    var dateString: String = ""
    var amountString: String = ""
    var mileageString: String = ""

    var mlKitTaskId : Long? = null
}
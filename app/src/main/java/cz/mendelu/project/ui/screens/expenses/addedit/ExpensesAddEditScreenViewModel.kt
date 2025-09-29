package cz.mendelu.project.ui.screens.expenses.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.R
import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.extensions.toFormattedDate
import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.model.MLKitTask
import cz.mendelu.project.ui.screens.summary.SummaryScreenUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExpensesAddEditScreenViewModel @Inject constructor(
    private val repository: ILocalCarExpenseRepository
) : ViewModel(), ExpensesAddEditScreenActions {

    private val _expenseAddEditScreenUIState: MutableStateFlow<ExpensesAddEditScreenUIState> =
        MutableStateFlow(
            ExpensesAddEditScreenUIState.Loading
        )

    val expensesAddEditScreenUIState = _expenseAddEditScreenUIState.asStateFlow()

    private var data = ExpensesAddEditScreenData()

    override fun saveExpense() {
        val currentErrors = ExpensesAddEditScreenErrors()


        // ---------------------- VALIDATE -----------------------------

        // AMOUNT
        if (data.amountString.isBlank() || data.amountString.toLongOrNull() == 0L) {
            currentErrors.errorOccurred = true
            currentErrors.amountError = R.string.the_input_is_not_a_valid_number
        }

        // MIlEAGE
        if (data.mileageString.toIntOrNull() == ExpenseType.FUEL.typeStringId && (data.mileageString.isBlank() || data.mileageString.toLongOrNull() == 0L)) {
            currentErrors.errorOccurred = true
            currentErrors.mileageError = R.string.the_input_is_not_a_valid_number
        }


        // DATE
        val dateFormat = SimpleDateFormat("d.M.yyyy", Locale.getDefault())
        dateFormat.isLenient = false

        try {
            val date = dateFormat.parse(data.dateString)
            if (date == null) {
                currentErrors.errorOccurred = true
                currentErrors.dateError = R.string.invalid_date
            }
        } catch (e: Exception) {
            currentErrors.errorOccurred = true
            currentErrors.dateError = R.string.enter_date_in_format_example
        }


        if (!currentErrors.errorOccurred) {
            // ---------------------- ASSIGN TO MODEL -----------------------------
            data.expense?.note = data.noteString
            data.expense?.amount = data.amountString.toLong()
            data.expense?.expenseType = data.expenseType.typeStringId
            data.expense?.fuelMileageInfo = data.mileageString.toLongOrNull()
            data.expense?.date = dateFormat.parse(data.dateString)?.time

            // ---------------------- WRITE TO DB -----------------------------

            viewModelScope.launch {
                if (data.expense?.id != null) {
                    repository.update(data.expense!!)
                } else {
                    repository.insert(data.expense!!)
                }
                _expenseAddEditScreenUIState.update { ExpensesAddEditScreenUIState.ExpenseSaved }
            }


        } else {
            data.errors = currentErrors
            _expenseAddEditScreenUIState.update { ExpensesAddEditScreenUIState.DataChanged(data) }
        }
    }

    fun initialize(id: Long?) {
        data.expense = if (id == null) Expense() else null

        viewModelScope.launch {
            if (id != null) {
                data.expense = repository.getExpense(id)
                data.expenseType = ExpenseType.fromInt(data.expense!!.expenseType!!)!!
                data.amountString = data.expense!!.amount.toString()
                data.noteString = data.expense?.note ?: ""
                data.mileageString = data.expense!!.fuelMileageInfo.toString()
                data.dateString = data.expense!!.date?.toFormattedDate() ?: System.currentTimeMillis().toFormattedDate()
            }
            _expenseAddEditScreenUIState.update {
                ExpensesAddEditScreenUIState.DataChanged(data)
            }
        }
    }

    override fun onExpensesAddEditScreenDataChanged(data: ExpensesAddEditScreenData) {
        data.amountString = data.amountString.replace(Regex("[^0-9]"), "")
        data.mileageString = data.mileageString.replace(Regex("[^0-9]"), "")

        this.data = data
        _expenseAddEditScreenUIState.update { ExpensesAddEditScreenUIState.DataChanged(data) }
    }

    fun deleteExpense() {
        viewModelScope.launch {
            data.expense?.let {
                repository.delete(it)
                _expenseAddEditScreenUIState.update {
                    ExpensesAddEditScreenUIState.ExpenseDeleted
                }
            }
        }
    }

    override fun dispatchMLKitTask(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val mlKitTask = MLKitTask(description = description, completed = false)
            data.mlKitTaskId = repository.insert(mlKitTask)
            onExpensesAddEditScreenDataChanged(data)
            _expenseAddEditScreenUIState.update {
                ExpensesAddEditScreenUIState.DispatchedMLKit
            }
        }
    }

    fun checkMLKit() {
        data.mlKitTaskId?.let { taskId ->
            viewModelScope.launch(Dispatchers.IO) {
                val mlKitTask = repository.getMlKitTask(taskId)
                if (mlKitTask.completed == true) {
                    data.amountString = mlKitTask.returnValue!!
                    onExpensesAddEditScreenDataChanged(data)
                }
            }
        }
    }
}
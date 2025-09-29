package cz.mendelu.project.ui.screens.expenses.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.database.ILocalCarExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesListScreenViewModel @Inject constructor(private val repository: ILocalCarExpenseRepository) : ViewModel(), ExpensesListScreenActions{

    private val _expensesListScreenUIState: MutableStateFlow<ExpensesListScreenUIState> =
        MutableStateFlow(ExpensesListScreenUIState.Loading())
    val expensesListScreenUIState = _expensesListScreenUIState.asStateFlow()

    private var data = ExpensesListScreenData()

    fun initialize(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllExpenses().collect {
                data.expenses = it
                _expensesListScreenUIState.update {
                    ExpensesListScreenUIState.DataLoaded(data)
                }
            }
        }
    }
}

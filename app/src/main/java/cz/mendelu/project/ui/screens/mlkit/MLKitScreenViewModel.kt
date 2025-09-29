package cz.mendelu.project.ui.screens.mlkit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.R
import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.model.OdometerEntry
import cz.mendelu.project.ui.screens.summary.SummaryScreenErrors
import cz.mendelu.project.ui.screens.summary.SummaryScreenUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MLKitScreenViewModel @Inject constructor(private val repository: ILocalCarExpenseRepository) : ViewModel(), MLKitScreenActions{

    private val _mlKitScreenUIState: MutableStateFlow<MLKitScreenUIState> =
        MutableStateFlow(MLKitScreenUIState.Loading())
    val mlKitScreenUIState = _mlKitScreenUIState.asStateFlow()

    private var data = MLKitScreenData()

    fun initialize(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            data.mlKitTask = repository.getMlKitTask(id)
            onMLKitScreenDataChanged(data)
        }
    }

    fun onMLKitScreenDataChanged(data: MLKitScreenData){
        this.data = data
        _mlKitScreenUIState.update { MLKitScreenUIState.DataChanged(data) }
    }

    fun saveResult() {
        // clear errors
        val currentErrors = MLKitScreenErrors()

        // check validity
        if (data.mlKitTask.returnValue?.toLongOrNull()?.takeIf { it > 0 } == null) {
            currentErrors.returnValueError = R.string.the_input_is_not_a_valid_number
            currentErrors.errorOccurred = true
        }

        // Save if no errors
        if (!currentErrors.errorOccurred) {
            viewModelScope.launch(Dispatchers.IO) {
                data.mlKitTask.completed = true
                data.mlKitTask.returnValue
                repository.update(data.mlKitTask)
                _mlKitScreenUIState.update { MLKitScreenUIState.ResultSaved() }
            }
        } else {
            data.errors = currentErrors
            onMLKitScreenDataChanged(data)
        }
    }

    fun setMlKitStringValue(value: String){
        data.mlKitTask.returnValue = value
        onMLKitScreenDataChanged(data)
    }
}

package cz.mendelu.project.ui.screens.routines.addedit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.R
import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.constants.Constants
import cz.mendelu.project.extensions.toFormattedDate
import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.model.RecurringTaskType
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.TypeOfRepetition
import cz.mendelu.project.ui.screens.expenses.addedit.ExpensesAddEditScreenUIState
import cz.mendelu.project.utils.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RoutineAddEditScreenViewModel @Inject constructor(
    private val repository: ILocalCarExpenseRepository
) : ViewModel(), RoutineAddEditScreenActions {

    private val _addEditRoutineUIState: MutableStateFlow<RoutineAddEditScreenUIState> =
        MutableStateFlow(
            RoutineAddEditScreenUIState.Loading
        )

    val addEditRoutineUIState = _addEditRoutineUIState.asStateFlow()

    private var data = RoutineAddEditScreenData()

    override fun saveRoutine() {
        val currentErrors = RoutineAddEditScreenErrors()
        val dateFormat = SimpleDateFormat("d.M.yyyy", Locale.getDefault())

        if (data.typeOfRepetition == TypeOfRepetition.BY_TIME) {
            //DATE
            dateFormat.isLenient = false

            try {
                val date = dateFormat.parse(data.startDateInput)
                if (date == null) {
                    currentErrors.errorOccurred = true
                    currentErrors.dateInputError = R.string.invalid_date
                }
            } catch (e: Exception) {
                currentErrors.errorOccurred = true
                currentErrors.dateInputError = R.string.enter_date_in_format_example
            }
        } else {
            if (data.startMileageInput.toLongOrNull() == null) {
                currentErrors.errorOccurred = true
                currentErrors.startMileageInputError = R.string.the_input_is_not_a_valid_number
            }
        }

        // REPEAT BY
        if (data.repeatByInput.isBlank() || data.repeatByInput.toLongOrNull() == 0L) {
            currentErrors.errorOccurred = true
            currentErrors.repeatByInputError = R.string.enter_a_positive_number
        }

        if (!currentErrors.errorOccurred) {
            data.routine.note = data.noteInput
            data.routine.repeatBy = data.repeatByInput.toLongOrNull()
            data.routine.typeOfOperation = data.typeOfOperation.typeStringId
            data.routine.typeOfRepetition = data.typeOfRepetition.typeStringId
            if (data.typeOfRepetition == TypeOfRepetition.BY_TIME) {
                val newStartDate = dateFormat.parse(data.startDateInput)?.time
                Log.d("TAGGGG", "pass time, newstartdate $newStartDate")
                if (data.routine.lastAccomplished == null || newStartDate != data.routine.startDate) {
                    data.routine.startDate = newStartDate

                    Log.d("TAGGGG", "pass 2 time lastAccomplished${data.routine.lastAccomplished}, startDate ${data.routine.startDate}")
                    data.routine.lastAccomplished = data.routine.startDate
                }
                data.routine.startDate = newStartDate
            } else {
                Log.d("TAGGGG", "pass mileage")
                val newStartMileage = data.startMileageInput.toLongOrNull()
                if (data.routine.lastAccomplished == null || data.routine.startMileage != newStartMileage) {
                    data.routine.startMileage = newStartMileage

                    Log.d("TAGGGG", "pass 2 mileage")
                    data.routine.lastAccomplished = data.routine.startMileage
                }
                data.routine.startMileage = newStartMileage
            }

            viewModelScope.launch {
                if (data.routine.id != null) {
                    repository.update(data.routine)
                } else {
                    repository.insert(data.routine)
                }
                _addEditRoutineUIState.update { RoutineAddEditScreenUIState.RoutineSavedScreen }
            }
        } else {
            data.errors = currentErrors
            onRoutineDataChanged(data)
        }
    }

    override fun onRoutineDataChanged(data: RoutineAddEditScreenData) {
        data.startDateInput = data.startDateInput.replace(Regex("[^0-9.]"), "")
        data.repeatByInput = data.repeatByInput.replace(Regex("[^0-9]"), "")
        this.data = data
        _addEditRoutineUIState.update { RoutineAddEditScreenUIState.RoutineChangedScreen(data) }
    }

    fun loadRoutine(id: Long?) {
        viewModelScope.launch {
            data.currentOdometerStatus = withContext(Dispatchers.IO) {
                repository.getCurrentOdometerStatus()
            }
            if (id != null) {
                data.routine = repository.getRoutine(id)
                data.repeatByInput = data.routine.repeatBy.toString()
                data.typeOfOperation = RecurringTaskType.fromInt(
                    data.routine.typeOfOperation ?: RecurringTaskType.OTHER.typeStringId
                )!!
                data.startDateInput =
                    data.routine.startDate?.toFormattedDate() ?: System.currentTimeMillis()
                        .toFormattedDate()
                data.typeOfRepetition = TypeOfRepetition.fromInt(
                    data.routine.typeOfRepetition ?: TypeOfRepetition.BY_TIME.typeStringId
                )!!
                data.noteInput = data.routine.note ?: ""
                data.startMileageInput =
                    (data.routine.startMileage?.toString() ?: data.currentOdometerStatus.toString())
            } else {
                data.startMileageInput = data.currentOdometerStatus.toString()
                data.startDateInput = System.currentTimeMillis().toFormattedDate()
            }
            onRoutineDataChanged(data)
        }
    }

    override fun deleteRoutine() {
        viewModelScope.launch {
            repository.deleteRoutineById(data.routine.id!!)
            _addEditRoutineUIState.update {
                RoutineAddEditScreenUIState.RoutineDeletedScreen
            }
        }
    }
}
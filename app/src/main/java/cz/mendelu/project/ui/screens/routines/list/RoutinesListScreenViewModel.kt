package cz.mendelu.project.ui.screens.routines.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.TypeOfRepetition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutinesListScreenViewModel @Inject constructor(private val repository: ILocalCarExpenseRepository) : ViewModel(), RoutinesListScreenActions{

    private val _routinesListScreenUIState: MutableStateFlow<RoutinesListScreenUIState> =
        MutableStateFlow(RoutinesListScreenUIState.Loading())
    val routinesListScreenUIState = _routinesListScreenUIState.asStateFlow()

    private var data = RoutinesListScreenData()

    fun initialize(){
        viewModelScope.launch(Dispatchers.IO) {
            data.currentOdometerStatus = repository.getCurrentOdometerStatus()
            repository.getAllRoutines().collect {
                data.routines = it
                _routinesListScreenUIState.update {
                    RoutinesListScreenUIState.DataLoaded(data)
                }
            }
        }
    }

    override fun completeRoutine(routine: Routine) {
        viewModelScope.launch(Dispatchers.IO) {
            if (routine.typeOfRepetition == TypeOfRepetition.BY_TIME.typeStringId){
                routine.completeTimeTask()
            } else {
                routine.completeMileageTask(data.currentOdometerStatus)
            }
            repository.update(routine)
        }
    }
}

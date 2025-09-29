package cz.mendelu.project.ui.screens.odometer

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
class OdometerScreenViewModel @Inject constructor(private val repository: ILocalCarExpenseRepository) : ViewModel(), OdometerScreenActions{

    private val _odometerScreenUIState: MutableStateFlow<OdometerScreenUIState> =
        MutableStateFlow(OdometerScreenUIState.Loading())
    val odometerScreenUIState = _odometerScreenUIState.asStateFlow()

    private var data = OdometerScreenData()

    fun initialize(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllOdometerEntries().collect {
                data.odometerEntries = it
                _odometerScreenUIState.update {
                    OdometerScreenUIState.DataLoaded(data)
                }
            }
        }
    }

    override fun deleteLastOdometerEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastEntry = data.odometerEntries.lastOrNull()
            if (lastEntry != null) {
                repository.delete(lastEntry)
            }
        }
    }
}

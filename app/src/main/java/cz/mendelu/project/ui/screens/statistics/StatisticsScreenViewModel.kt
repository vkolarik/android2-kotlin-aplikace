package cz.mendelu.project.ui.screens.statistics

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.ui.screens.odometer.OdometerScreenUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsScreenViewModel @Inject constructor(private val repository: ILocalCarExpenseRepository) : ViewModel(){

    private val _statisticsScreenUIState: MutableStateFlow<StatisticsScreenUIState> =
        MutableStateFlow(StatisticsScreenUIState.Loading())
    val statisticsScreenUIState = _statisticsScreenUIState.asStateFlow()

    private var data = StatisticsScreenData()

    fun initialize(){
        viewModelScope.launch(Dispatchers.IO) {
            data.statistics = repository.getStatistics()
            _statisticsScreenUIState.update {
                StatisticsScreenUIState.DataLoaded(data)
            }
        }
    }
}

package cz.mendelu.project.ui.screens.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.R
import cz.mendelu.project.communication.CommunicationResult
import cz.mendelu.project.communication.IOverpassRemoteRepository
import cz.mendelu.project.communication.toGasStationItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(private val repository: IOverpassRemoteRepository) :
    ViewModel() {

    private val _uiState: MutableStateFlow<MapScreenUIState> =
        MutableStateFlow(value = MapScreenUIState.Loading)

    val uiState: StateFlow<MapScreenUIState> get() = _uiState.asStateFlow()

    fun loadGasStationData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update {
                MapScreenUIState.Loading
            }

            val result = withContext(Dispatchers.IO) {
                repository.fetchGasStations(lat, lon)
            }
            when (result) {
                is CommunicationResult.ConnectionError -> {
                    _uiState.update {
                        MapScreenUIState.Error(MapScreenError(R.string.loading))
                    }
                    Log.e("petstoredebug", "connection error")
                }

                is CommunicationResult.Error -> {
                    _uiState.update {
                        MapScreenUIState.Error(MapScreenError(R.string.loading))
                    }
                    Log.e("petstoredebug", "generic error")
                }

                is CommunicationResult.Exception -> {
                    _uiState.update {
                        MapScreenUIState.Error(MapScreenError(R.string.loading))
                    }
                    Log.e("petstoredebug", "exception" + result.exception.message)
                }

                is CommunicationResult.Success -> {
                    _uiState.update {
                        MapScreenUIState.DataLoaded(result.data.toGasStationItems())
                    }
                }
            }
        }
    }
}
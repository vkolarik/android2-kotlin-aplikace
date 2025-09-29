package cz.mendelu.project.ui.screens.map

import cz.mendelu.project.communication.GasStationItem
import java.io.Serializable

sealed class MapScreenUIState () : Serializable {
    object Loading : MapScreenUIState()
    class DataLoaded(val gasStations: List<GasStationItem>) : MapScreenUIState()
    class Error(val error: MapScreenError) : MapScreenUIState()

}
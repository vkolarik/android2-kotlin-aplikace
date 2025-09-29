package cz.mendelu.project.ui.screens.odometer

sealed class OdometerScreenUIState {
    class Loading : OdometerScreenUIState()
    class DataLoaded(val data: OdometerScreenData) : OdometerScreenUIState()

}

package cz.mendelu.project.ui.screens.routines.list

sealed class RoutinesListScreenUIState {
    class Loading : RoutinesListScreenUIState()
    class DataLoaded(val data: RoutinesListScreenData) : RoutinesListScreenUIState()

}

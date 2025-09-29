package cz.mendelu.project.ui.screens.routines.addedit

sealed class RoutineAddEditScreenUIState {
    object Loading : RoutineAddEditScreenUIState()
    object RoutineSavedScreen : RoutineAddEditScreenUIState()
    class RoutineChangedScreen(val data: RoutineAddEditScreenData) : RoutineAddEditScreenUIState()
    object RoutineDeletedScreen : RoutineAddEditScreenUIState()

}
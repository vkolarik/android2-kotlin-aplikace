package cz.mendelu.project.ui.screens.routines.addedit

interface RoutineAddEditScreenActions {
    fun onRoutineDataChanged(data: RoutineAddEditScreenData)

    fun saveRoutine()

    fun deleteRoutine()
}
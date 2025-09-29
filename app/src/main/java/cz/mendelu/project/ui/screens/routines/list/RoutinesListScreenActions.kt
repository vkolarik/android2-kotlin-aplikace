package cz.mendelu.project.ui.screens.routines.list

import cz.mendelu.project.model.Routine

interface RoutinesListScreenActions {
    fun completeRoutine(routine: Routine)
}
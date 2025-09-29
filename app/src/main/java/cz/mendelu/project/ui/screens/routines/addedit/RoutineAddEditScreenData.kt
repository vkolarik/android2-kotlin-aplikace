package cz.mendelu.project.ui.screens.routines.addedit

import cz.mendelu.project.model.RecurringTaskType
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.TypeOfRepetition

class RoutineAddEditScreenData {
    var errors: RoutineAddEditScreenErrors = RoutineAddEditScreenErrors()
    var routine: Routine = Routine()

    var currentOdometerStatus: Long = 0

    var typeOfOperation : RecurringTaskType = RecurringTaskType.OTHER
    var typeOfRepetition : TypeOfRepetition = TypeOfRepetition.BY_MILEAGE
    var startDateInput : String = ""
    var startMileageInput : String = ""
    var noteInput : String = ""
    var repeatByInput : String = ""
}
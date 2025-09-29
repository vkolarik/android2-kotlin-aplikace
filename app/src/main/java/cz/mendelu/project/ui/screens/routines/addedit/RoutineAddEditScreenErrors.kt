package cz.mendelu.project.ui.screens.routines.addedit

data class RoutineAddEditScreenErrors(
    var errorOccurred: Boolean = false,
    var typeOfOperationError: Int? = null,
    var typeOfRepetitionError: Int? = null,
    var noteInputError: Int? = null,
    var dateInputError: Int? = null,
    var repeatByInputError: Int? = null,
    var startMileageInputError: Int? = null,
)
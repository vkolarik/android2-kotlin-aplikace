package cz.mendelu.project.ui.screens.summary

data class SummaryScreenErrors(
    var errorOccurred: Boolean = false,
    var odometerError: Int? = null,
)
package cz.mendelu.project.ui.screens.summary


sealed class SummaryScreenUIState {
    object Loading : SummaryScreenUIState()
    object DispatchedMLKit : SummaryScreenUIState()
    class SummaryScreenDataChanged(val data : SummaryScreenData) : SummaryScreenUIState()

}
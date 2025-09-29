package cz.mendelu.project.ui.screens.statistics

sealed class StatisticsScreenUIState {
    class Loading : StatisticsScreenUIState()
    class DataLoaded(val data: StatisticsScreenData) : StatisticsScreenUIState()

}

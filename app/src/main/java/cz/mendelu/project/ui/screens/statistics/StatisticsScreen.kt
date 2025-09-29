package cz.mendelu.project.ui.screens.statistics

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.extensions.formatNumber
import cz.mendelu.project.extensions.formatWithSpaces
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.elements.HorizontalCard
import cz.mendelu.project.ui.elements.PlaceholderScreenDefinition
import cz.mendelu.project.ui.screens.odometer.OdometerScreenData
import cz.mendelu.project.ui.screens.odometer.OdometerScreenUIState
import cz.mendelu.project.ui.screens.odometer.OdometerScreenViewModel
import cz.mendelu.project.ui.theme.HalfMargin

@Composable
fun StatisticsScreen(navigationRouter: INavigationRouter) {

    val viewModel = hiltViewModel<StatisticsScreenViewModel>()

    val state = viewModel.statisticsScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(StatisticsScreenData())
    }

    state.value.let {
        when (it) {
            is StatisticsScreenUIState.Loading -> viewModel.initialize()
            is StatisticsScreenUIState.DataLoaded -> data = it.data
        }
    }


    BaseScreen(
        topBarText = stringResource(R.string.statistics),
        onBackClick = { navigationRouter.returnBack() },
        /*placeholderScreenDefinition = if (data.statistics == null) PlaceholderScreenDefinition(
            text = stringResource(
                R.string.loading
            )
        ) else null*/
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(HalfMargin())
                .fillMaxSize()
        ) {
            item {
                StatisticsScreenContent(data = data)
            }
        }
    }
}

@Composable
fun StatisticsScreenContent(
    data: StatisticsScreenData
) {
    HorizontalCard(
        title = stringResource(R.string.average_trip_cost),
        description = "${data.statistics?.costPerKmSum?.formatNumber()} " + stringResource(R.string.czk_per_km)
    )
    HorizontalCard(
        title = stringResource(R.string.fuel_costs),
        description = data.statistics?.costPerKmFuel?.formatNumber()?.let {
            "$it ${stringResource(R.string.czk_per_km)}"
        } ?: stringResource(R.string.no_data)
    )
    HorizontalCard(
        title = stringResource(R.string.service_and_insurance_costs),
        description = data.statistics?.costPerKmMaintenance?.formatNumber()?.let {
            "$it ${stringResource(R.string.czk_per_km)}"
        } ?: stringResource(R.string.no_data)
    )
    HorizontalCard(
        title = stringResource(R.string.depreciation),
        description = data.statistics?.costPerKmDepreciation?.formatNumber()?.let {
            "$it ${stringResource(R.string.czk_per_km)}"
        } ?: stringResource(R.string.no_data)
    )
    HorizontalCard(
        title = stringResource(R.string.annual_mileage),
        description = "${data.statistics?.mileageLastYear?.formatWithSpaces()} " + stringResource(R.string.km)
    )
    HorizontalCard(
        title = stringResource(R.string.annual_maintenance_costs),
        description = "${data.statistics?.yearlyMaintenanceCost?.formatWithSpaces()} " + stringResource(
            R.string.czk
        )
    )
}
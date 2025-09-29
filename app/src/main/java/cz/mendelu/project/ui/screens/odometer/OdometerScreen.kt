package cz.mendelu.project.ui.screens.odometer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import cz.mendelu.project.extensions.formatWithSpaces
import cz.mendelu.project.extensions.toFormattedDate
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.elements.ClickableHorizontalCard
import cz.mendelu.project.ui.elements.SummaryClickableHorizontalCard
import cz.mendelu.project.ui.elements.HorizontalCard
import cz.mendelu.project.ui.elements.PlaceholderScreenDefinition
import cz.mendelu.project.ui.theme.HalfMargin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdometerScreen(navigationRouter: INavigationRouter) {

    val viewModel = hiltViewModel<OdometerScreenViewModel>()

    val state = viewModel.odometerScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(OdometerScreenData())
    }

    state.value.let {
        when (it) {
            is OdometerScreenUIState.Loading -> {
                viewModel.initialize()
            }

            is OdometerScreenUIState.DataLoaded -> {
                data = it.data
            }
        }
    }


    BaseScreen(
        topBarText = stringResource(R.string.odometer_history),
        onBackClick = { navigationRouter.returnBack() },
        placeholderScreenDefinition = if (data.odometerEntries.isEmpty()) {
            PlaceholderScreenDefinition(
                image = R.drawable.undraw_add_files_re_v09g,
                text = stringResource(R.string.input_your_odometer_status_on_the_main_screen)
            )
        } else null
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(HalfMargin())
                .fillMaxSize()
        ) {

                item {
                    OdometerScreenContent(
                        data = data,
                        actions = viewModel
                    )
                }

        }
    }
}

@Composable
fun OdometerScreenContent(
    data: OdometerScreenData,
    actions: OdometerScreenActions
) {
    val entries = data.odometerEntries.reversed()

    entries.windowed(2).forEachIndexed { index, (current, previous) ->
        val difference = current.mileage!! - previous.mileage!!

        if (index == 0) {
            // Most recent entry (first in the reversed list)
            ClickableHorizontalCard(
                title = "${current.mileage!!.formatWithSpaces()} " + stringResource(R.string.km),
                description = "${current.timestamp!!.toFormattedDate()} (+${difference.formatWithSpaces()} " +
                        stringResource(R.string.km) + ")",
                onClick = {
                    actions.deleteLastOdometerEntry()
                },
                icon = Icons.Default.Delete
            )
        } else {
            // Older entries
            HorizontalCard(
                title = "${current.mileage!!.formatWithSpaces()} " + stringResource(R.string.km),
                description = "${current.timestamp!!.toFormattedDate()} (+${difference.formatWithSpaces()} " +
                        stringResource(R.string.km) + ")"
            )
        }
    }

    // Show the oldest entry without difference
    entries.lastOrNull()?.let { firstEntry ->
        HorizontalCard(
            title = "${firstEntry.mileage!!.formatWithSpaces()} " + stringResource(R.string.km),
            description = firstEntry.timestamp!!.toFormattedDate()
        )
    }
}

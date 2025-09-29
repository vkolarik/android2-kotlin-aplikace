package cz.mendelu.project.ui.screens.summary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.elements.SummaryClickableHorizontalCard
import cz.mendelu.project.ui.elements.PlaceholderScreenDefinition
import cz.mendelu.project.ui.theme.BasicMargin
import java.util.Locale

@Composable
fun SummaryScreen(navigationRouter: INavigationRouter, skipIntro: Boolean = false) {

    val viewModel = hiltViewModel<SummaryScreenViewModel>()

    val state = viewModel.summaryUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(SummaryScreenData())
    }

    LaunchedEffect(Unit) {
        viewModel.checkMLKit()
        viewModel.loadStatistics()
    }

    state.value.let {
        when (it) {
            is SummaryScreenUIState.Loading -> {
                viewModel.initialize(skipIntro)
            }

            is SummaryScreenUIState.SummaryScreenDataChanged -> {
                data = it.data
            }

            SummaryScreenUIState.DispatchedMLKit -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToMLKitScreen(data.mlKitTaskId!!)
                    viewModel.onOdometerEntryChanged(data)
                }
            }
        }
    }

    BaseScreen(
        topBarText = stringResource(id = R.string.app_name),
        actions = {
            if (state.value != SummaryScreenUIState.Loading && !data.isFirstRun) {
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.menu)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    DropdownMenuItem(onClick = {
                        expanded = false
                        navigationRouter.navigateToSettingsScreen()
                    }, text = {
                        Text(text = stringResource(id = R.string.settings))
                    })
                    DropdownMenuItem(onClick = {
                        expanded = false
                        navigationRouter.navigateToAboutScreen()
                    }, text = {
                        Text(text = stringResource(id = R.string.about_app))
                    })
                }
            }
        },
        placeholderScreenDefinition = if (state.value == SummaryScreenUIState.Loading) {
            PlaceholderScreenDefinition(
                text = stringResource(id = R.string.loading)
            )
        } else null
    ) {
        if (data.isFirstRun) {
            Column(modifier = Modifier.padding(it)) {
                FirstRunScreenContent(actions = viewModel, data = data, navigationRouter = navigationRouter)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(BasicMargin())
                    ) {
                        SummaryScreenContent(
                            navigationRouter = navigationRouter,
                            data = data,
                            actions = viewModel
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SummaryScreenContent(
    navigationRouter: INavigationRouter,
    data: SummaryScreenData,
    actions: SummaryScreenActions
) {
    val mlKitDescription = stringResource(R.string.current_odometer_value)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTagSummaryScreenMainLayout),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                label = { Text(text = stringResource(id = R.string.current_odometer_value)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTagSummaryScreenOdometerInput),
                value = data.odometerEntryString,
                onValueChange = {
                    data.odometerEntryString = it
                    actions.onOdometerEntryChanged(data)
                },
                singleLine = true,
                maxLines = 1,
                isError = data.errors.odometerError != null,
                supportingText = {
                    if (data.errors.odometerError != null) {
                        Text(text = stringResource(id = data.errors.odometerError!!))
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { actions.dispatchMLKitTask(mlKitDescription) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                            contentDescription = "Scan with OCR"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            if (data.errors.odometerError == null && data.odometerStatusMessage.isNotEmpty()) {
                Text(
                    text = data.odometerStatusMessage,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { navigationRouter.navigateToOdometerScreen() }) { Text(stringResource(R.string.mileage_history)) }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { actions.insertOdometerEntry() }) { Text(stringResource(R.string.save)) }
            }
        }
    }

    SummaryClickableHorizontalCard(
        title = stringResource(R.string.split_the_ride_expenses),
        descriptionContent = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.generate_a_qr_payment_code))
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = {
            navigationRouter.navigateToPaymentScreen()
        },
        icon = Icons.Default.PlayArrow
    )

    SummaryClickableHorizontalCard(
        title = stringResource(R.string.show_map),
        descriptionContent = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.gas_stations_nearby))
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = {
            navigationRouter.navigateToMapScreen()
        },
        icon = Icons.Default.PlayArrow
    )

    SummaryClickableHorizontalCard(
        title = stringResource(R.string.statistics),
        descriptionContent = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.on_average) +" ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(String.format("%.2f", data.statistics?.costPerKmSum) + " "+ stringResource(R.string.czk_per_km))
                    }
                    append(", "+ stringResource(R.string.this_year) +" ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(data.statistics?.mileageLastYear.toString() + " km")
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = {
            navigationRouter.navigateToStatisticsScreen()
        },
        icon = Icons.Default.PlayArrow
    )

    SummaryClickableHorizontalCard(
        title = stringResource(R.string.routine_tasks),
        descriptionContent = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.upcoming_month) +" ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        if ((data.statistics?.upcomingTasksCount ?: 0) > 0) {
                            append(data.statistics?.upcomingTasksCount.toString() + " "+ stringResource(
                                R.string.tasks
                            )
                            )
                        } else {
                            append(stringResource(R.string.no_tasks_summary))
                        }

                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = {
            navigationRouter.navigateToRoutinesListScreen()
        },
        icon = Icons.Default.PlayArrow
    )

    SummaryClickableHorizontalCard(
        title = stringResource(R.string.expenses),
        descriptionContent = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.this_year).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    } +" ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(data.statistics?.expensesLastYear.toString() + " "+ stringResource(R.string.czk))
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = {
            navigationRouter.navigateToExpensesListScreen()
        },
        icon = Icons.Default.PlayArrow
    )
}

@Composable
fun FirstRunScreenContent(
    actions: SummaryScreenActions,
    data: SummaryScreenData,
    navigationRouter: INavigationRouter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_icon),
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.welcome_in_car_expense_app),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.start_with_telling_us_a_few_things_about_your_car),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = {
                navigationRouter.navigateToSettingsScreen()
                actions.endFirstRun()
                data.isFirstRun = false
            },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.go_to_settings),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        /*Button(
            onClick = {
                actions.insertSampleData()
                data.isFirstRun = false
            },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.insert_sample_data),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }*/

        Text(
            text = stringResource(R.string.explore_and_enjoy_app),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

const val TestTagSummaryScreenMainLayout = "TestTagSummaryScreenMainLayout"
const val TestTagSummaryScreenStatisticsDetails = "TestTagSummaryScreenStatisticsDetails"
const val TestTagSummaryScreenOdometerInput = "TestTagSummaryScreenOdometerInput"


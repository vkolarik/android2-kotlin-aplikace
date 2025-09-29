package cz.mendelu.project.ui.screens.routines.list

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.extensions.formatWithSpaces
import cz.mendelu.project.extensions.toFormattedDate
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.TypeOfRepetition
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.elements.PlaceholderScreenDefinition
import cz.mendelu.project.ui.theme.BasicMargin

@Composable
fun RoutinesListScreen(navigationRouter: INavigationRouter) {

    val viewModel = hiltViewModel<RoutinesListScreenViewModel>()

    val state = viewModel.routinesListScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(RoutinesListScreenData())
    }

    state.value.let {
        when (it) {
            is RoutinesListScreenUIState.Loading -> {
                viewModel.initialize()
            }

            is RoutinesListScreenUIState.DataLoaded -> {
                data = it.data
            }
        }
    }


    BaseScreen(
        topBarText = stringResource(R.string.routines),
        onBackClick = { navigationRouter.returnBack() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigationRouter.navigateToRoutinesAddEditScreen(null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        placeholderScreenDefinition = if (data.routines.isEmpty()) {
            PlaceholderScreenDefinition(
                image = R.drawable.undraw_add_files_re_v09g,
                text = stringResource(R.string.you_can_fill_this_page_using_the_add_button)
            )
        } else null
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            item {
                RoutinesListScreenContent(
                    navigationRouter = navigationRouter,
                    data = data,
                    actions = viewModel
                )
            }
        }
    }
}

@Composable
fun RoutinesListScreenContent(
    navigationRouter: INavigationRouter,
    data: RoutinesListScreenData,
    actions: RoutinesListScreenActions,
) {
    // Separate overdue tasks and non-overdue tasks
    val overdueRoutines = data.routines.filter { routine ->
        routine.isTaskOverdue(data.currentOdometerStatus)
    }

    val otherRoutines = data.routines.filterNot { routine ->
        routine.isTaskOverdue(data.currentOdometerStatus)
    }

    // Show overdue routines first
    overdueRoutines.forEach { routine ->
        RoutineCard(
            routine = routine,
            currentMileage = data.currentOdometerStatus,
            onEditClick = {
                navigationRouter.navigateToRoutinesAddEditScreen(routine.id)
            },
            onCompleteClick = {
                actions.completeRoutine(routine)
            }
        )
    }

    // Show the rest of the routines
    otherRoutines.forEach { routine ->
        RoutineCard(
            routine = routine,
            currentMileage = data.currentOdometerStatus,
            onEditClick = {
                navigationRouter.navigateToRoutinesAddEditScreen(routine.id)
            },
            onCompleteClick = {
                actions.completeRoutine(routine)
            }
        )
    }
}

@Composable
fun RoutineCard(
    routine: Routine,
    onEditClick: () -> Unit,
    onCompleteClick: () -> Unit,
    currentMileage: Long,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = BasicMargin(), start = BasicMargin(), end = BasicMargin())
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // First Line: Operation Type
            Text(
                text = stringResource(routine.typeOfOperation ?: R.string.unknown),
                fontWeight = FontWeight.Bold
            )

            //Log.d("RoutineCard", "TOO: $routine , tsi "+TypeOfRepetition.BY_TIME.typeStringId)

            if (routine.typeOfRepetition == TypeOfRepetition.BY_TIME.typeStringId){
                val daysRemaining = (routine.getTimeTaskNextOccurence()
                    ?.minus(System.currentTimeMillis()))?.div((24 * 60 * 60 * 1000))

                Text(
                    text = buildAnnotatedString {
                        // First line (bold)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Next occurrence: " + (routine.getTimeTaskNextOccurence()
                                ?.toFormattedDate() ?: stringResource(R.string.invalid_date)))

                            if (daysRemaining != null) {
                                // Display days remaining or overdue message
                                if (daysRemaining > 0) {
                                    append(" (in $daysRemaining days)\n")
                                } else {
                                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                                        append(" (overdue for ${-daysRemaining} days)\n")
                                    }
                                }
                            }
                        }

                        // Second line (normal)
                        append("Repeats every ${routine.repeatBy} days")
                    },
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )

            } else {
                val mileageRemaining = routine.getMileageTaskNextOccurence()?.minus(currentMileage)

                Text(
                    text = buildAnnotatedString {
                        // First line (bold)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Next occurrence: " + (routine.getMileageTaskNextOccurence()
                                ?.formatWithSpaces() ?: "") + " km ")

                            if (mileageRemaining != null) {
                                // Display mileage remaining or overdue message
                                if (mileageRemaining > 0) {
                                    append(" (in ${mileageRemaining.formatWithSpaces()} km)\n")
                                } else {
                                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                                        append(" (overdue by ${(-mileageRemaining).formatWithSpaces()} km)\n")
                                    }
                                }
                            }
                        }

                        // Second line (normal)
                        append("Repeats every ${routine.repeatBy} kilometers")
                    },
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )


            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (routine.typeOfRepetition == TypeOfRepetition.BY_TIME.typeStringId){
                    if (routine.isTimeTaskOverdue()) {
                        Button(onClick = onCompleteClick) {
                            Text(text = stringResource(R.string.complete))
                        }
                    }

                } else {
                    if (routine.isMileageTaskOverdue(currentMileage)) {
                        Button(onClick = onCompleteClick) {
                            Text(text = stringResource(R.string.complete))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onEditClick) {
                    Text(text = stringResource(R.string.edit))
                }
            }
        }
    }
}

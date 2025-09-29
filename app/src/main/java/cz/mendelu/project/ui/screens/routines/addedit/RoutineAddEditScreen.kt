package cz.mendelu.project.ui.screens.routines.addedit

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.communication.FuelType
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.model.RecurringTaskType
import cz.mendelu.project.model.TypeOfRepetition
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.screens.expenses.addedit.ExpenseTypeDropdown
import cz.mendelu.project.ui.theme.BasicMargin
import cz.mendelu.project.ui.theme.HalfMargin
import cz.mendelu.project.utils.Time
import java.util.Calendar

@Composable
fun RoutineAddEditScreen(navigationRouter: INavigationRouter, id: Long?) {

    val viewModel = hiltViewModel<RoutineAddEditScreenViewModel>()

    val state = viewModel.addEditRoutineUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(RoutineAddEditScreenData())
    }

    state.value.let {
        when (it) {
            RoutineAddEditScreenUIState.Loading -> {
                viewModel.loadRoutine(id)
            }

            is RoutineAddEditScreenUIState.RoutineChangedScreen -> {
                data = it.data
            }

            RoutineAddEditScreenUIState.RoutineSavedScreen -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }

            RoutineAddEditScreenUIState.RoutineDeletedScreen -> {
                //launched effect zamezi rekompozici nad touto promennou a tak se nedostaneme na bilou obrazovku
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }
        }
    }

    BaseScreen(
        topBarText = if (id == null) stringResource(R.string.add_routine) else stringResource(id = R.string.edit_routine),
        onBackClick = { navigationRouter.returnBack() },
        actions = {
            if (id != null) {
                IconButton(onClick = { viewModel.deleteRoutine() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            item {
                RoutineAddEditScreenContent(data = data, actions = viewModel)
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineAddEditScreenContent(
    data: RoutineAddEditScreenData,
    actions: RoutineAddEditScreenActions,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(HalfMargin()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(HalfMargin())
    ) {

        // -------------------- RECURRING TASK TYPE -------------------------------

        RecurringTaskTypeDropdown(
            selectedRecurringTaskType = RecurringTaskType.fromInt(data.typeOfOperation.typeStringId),
            onRecurringTaskTypeSelected = { selectedType ->
                data.typeOfOperation = selectedType
                actions.onRoutineDataChanged(data)
            },
            error = data.errors.typeOfOperationError?.let { stringResource(id = it) }
        )

        Spacer(modifier = Modifier.height(HalfMargin()))


        // -------------------- NOTE -------------------------------

        TextField(
            label = { Text(stringResource(id = R.string.note)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.noteInput ?: "",
            onValueChange = {
                data.noteInput = it
                actions.onRoutineDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.noteInputError != null,
            supportingText = {
                if (data.errors.noteInputError != null) {
                    Text(text = stringResource(id = data.errors.noteInputError!!))
                }
            }
        )


        // ------------------- REPEAT TYPE -----------------------------

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.repeat))
        }

        val options = listOf(TypeOfRepetition.BY_TIME, TypeOfRepetition.BY_MILEAGE)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, value ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        data.typeOfRepetition = value
                        actions.onRoutineDataChanged(data)
                    },
                    selected = value == data.typeOfRepetition
                ) {
                    Text(stringResource(id = value.typeStringId))
                }
            }
        }

        Spacer(modifier = Modifier.height(HalfMargin()))

        if (data.typeOfRepetition == TypeOfRepetition.BY_TIME) {
            // ------------------- START DATE -----------------------------
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = data.routine?.startDate ?: System.currentTimeMillis()
            val datePickerDialog = DatePickerDialog(
                LocalContext.current,
                { _, year, month, dayOfMonth ->
                    // Format the selected date and update the text field value
                    val formattedDate = "$dayOfMonth.${month + 1}.$year"
                    data.startDateInput = formattedDate
                    actions.onRoutineDataChanged(data)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // UI
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = data.startDateInput,
                    onValueChange = {
                        data.startDateInput = it
                        actions.onRoutineDataChanged(data)
                    },
                    label = { Text(stringResource(R.string.start_date)) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.select_date)
                            )
                        }
                    },
                    isError = data.errors.dateInputError != null,
                    supportingText = {
                        if (data.errors.dateInputError != null) {
                            Text(text = stringResource(id = data.errors.dateInputError!!))
                        }
                    }
                )
            }
        } else {
            // -------------------- START MILEAGE -------------------------------

            TextField(
                label = { Text(text = stringResource(R.string.last_done_at_mileage)) },
                modifier = Modifier
                    .fillMaxWidth(),
                value = data.startMileageInput ?: "",
                onValueChange = {
                    data.startMileageInput = it
                    actions.onRoutineDataChanged(data)
                },
                singleLine = true,
                isError = data.errors.startMileageInputError != null,
                supportingText = {
                    if (data.errors.startMileageInputError != null) {
                        Text(text = stringResource(id = data.errors.startMileageInputError!!))
                    }
                }
            )
        }


        // -------------------- REPEAT BY -------------------------------


        TextField(
            label = {
                Text(
                    text = if (data.typeOfRepetition == TypeOfRepetition.BY_TIME)
                        stringResource(R.string.number_of_days)
                    else
                        stringResource(R.string.number_of_kilometers)
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.repeatByInput ?: "",
            onValueChange = {
                data.repeatByInput = it
                actions.onRoutineDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.repeatByInputError != null,
            supportingText = {
                if (data.errors.repeatByInputError != null) {
                    Text(text = stringResource(id = data.errors.repeatByInputError!!))
                }
            }
        )


        // ------------------- SAVE BUTTON -----------------------------
        Column(
            modifier = Modifier
                .padding(horizontal = HalfMargin())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                onClick = {
                    actions.saveRoutine()
                }) {
                Text(stringResource(id = R.string.save))
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTaskTypeDropdown(
    selectedRecurringTaskType: RecurringTaskType?,
    onRecurringTaskTypeSelected: (RecurringTaskType) -> Unit,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val options = RecurringTaskType.values() // Use RecurringTaskType enum

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedRecurringTaskType?.let { stringResource(id = it.typeStringId) } ?: "",
            onValueChange = {},
            label = { Text(stringResource(R.string.recurring_task_type)) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(), // Ensures dropdown anchors properly
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = error != null,
            colors = ExposedDropdownMenuDefaults.textFieldColors() // Styling for consistency
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { recurringTaskType ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = recurringTaskType.typeStringId)) },
                    onClick = {
                        onRecurringTaskTypeSelected(recurringTaskType)
                        expanded = false
                    }
                )
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

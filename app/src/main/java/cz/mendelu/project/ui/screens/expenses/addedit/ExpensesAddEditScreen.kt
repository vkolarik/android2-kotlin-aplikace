package cz.mendelu.project.ui.screens.expenses.addedit

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.screens.summary.SummaryScreenUIState
import cz.mendelu.project.ui.theme.HalfMargin
import java.util.Calendar

@Composable
fun ExpensesAddEditScreen(navigationRouter: INavigationRouter, id: Long?) {

    val viewModel = hiltViewModel<ExpensesAddEditScreenViewModel>()

    val state = viewModel.expensesAddEditScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(ExpensesAddEditScreenData())
    }

    LaunchedEffect(Unit) {
        viewModel.checkMLKit()
    }

    state.value.let {
        when (it) {
            ExpensesAddEditScreenUIState.Loading -> {
                viewModel.initialize(id)
            }

            is ExpensesAddEditScreenUIState.DataChanged -> {
                data = it.data
            }

            ExpensesAddEditScreenUIState.ExpenseSaved -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }

            ExpensesAddEditScreenUIState.ExpenseDeleted -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }

            ExpensesAddEditScreenUIState.DispatchedMLKit -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToMLKitScreen(data.mlKitTaskId!!)
                    viewModel.onExpensesAddEditScreenDataChanged(data)
                }
            }
        }
    }

    BaseScreen(
        topBarText = if (id == null) stringResource(R.string.add_expense) else stringResource(id = R.string.edit_expense),
        onBackClick = { navigationRouter.returnBack() },
        actions = {
            if (id != null) {
                IconButton(onClick = { viewModel.deleteExpense() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            item {
                ExpensesAddEditScreenContent(data = data, actions = viewModel)
            }
        }
    }
}

@Composable
fun ExpensesAddEditScreenContent(
    data: ExpensesAddEditScreenData,
    actions: ExpensesAddEditScreenActions,
) {
    val mlKitDescription = stringResource(R.string.amount_czk)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(HalfMargin()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(HalfMargin())
    ) {

        // ------------------- ROUTINE NAME -----------------------------

        ExpenseTypeDropdown(
            selectedExpenseType = data.expenseType,
            onExpenseTypeSelected = { selectedType ->
                data.expenseType = selectedType
                actions.onExpensesAddEditScreenDataChanged(data)
            },
            error = data.errors.expenseTypeError?.let { stringResource(id = it) }
        )

        Spacer(modifier = Modifier.height(HalfMargin()))

        // -------------------- MILEAGE -------------------------------

        if (data.expenseType.typeStringId == ExpenseType.FUEL.typeStringId) {

            TextField(
                label = { Text(stringResource(id = R.string.mileage))  },
                modifier = Modifier
                    .fillMaxWidth(),
                value = data.mileageString ?: "",
                onValueChange = {
                    data.mileageString = it
                    actions.onExpensesAddEditScreenDataChanged(data)
                },
                singleLine = true,
                isError = data.errors.mileageError != null,
                supportingText = {
                    if (data.errors.mileageError != null) {
                        Text(text = stringResource(id = data.errors.mileageError!!))
                    }
                }
            )
        }

        // ------------------- START DATE -----------------------------
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = data.expense?.date ?: System.currentTimeMillis()
        val datePickerDialog = DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                // Format the selected date and update the text field value
                val formattedDate = "$dayOfMonth.${month + 1}.$year"
                data.dateString = formattedDate
                actions.onExpensesAddEditScreenDataChanged(data)
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
                value = data.dateString,
                onValueChange = {
                    data.dateString = it
                    actions.onExpensesAddEditScreenDataChanged(data)
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
                isError = data.errors.dateError != null,
                supportingText = {
                    if (data.errors.dateError != null) {
                        Text(text = stringResource(id = data.errors.dateError!!))
                    }
                }
            )
        }

        // ------------------- Amount -----------------------------

        TextField(
            label = { Text(stringResource(R.string.amount_czk)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.amountString ?: "",
            onValueChange = {
                data.amountString = it
                actions.onExpensesAddEditScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.amountError != null,
            supportingText = {
                if (data.errors.amountError != null) {
                    Text(text = stringResource(id = data.errors.amountError!!))
                }
            }
        )

        // ------------------- MLKIT BUTTON -----------------------------
        Column(
            modifier = Modifier
                .padding(bottom = HalfMargin())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                onClick = {
                    actions.dispatchMLKitTask(mlKitDescription)
                }) {
                Text(stringResource(id = R.string.read_from_receipt))
            }
        }

        // ------------------- NOTE -----------------------------

        TextField(
            label = { Text(stringResource(id = R.string.notes)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.noteString ?: "",
            onValueChange = {
                data.noteString = it
                actions.onExpensesAddEditScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.noteError != null,
            supportingText = {
                if (data.errors.noteError != null) {
                    Text(text = stringResource(id = data.errors.noteError!!))
                }
            }
        )

        // ------------------- SAVE BUTTON -----------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    actions.saveExpense()
                }) {
                Text(stringResource(id = R.string.save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTypeDropdown(
    selectedExpenseType: ExpenseType?,
    onExpenseTypeSelected: (ExpenseType) -> Unit,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val options = ExpenseType.entries

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedExpenseType?.let { stringResource(id = it.typeStringId) } ?: "",
            onValueChange = {},
            label = { Text(stringResource(R.string.expense_type)) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(), // Add this modifier instead of clickable
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = error != null,
            colors = ExposedDropdownMenuDefaults.textFieldColors() // Add this for proper styling
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { expenseType ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = expenseType.typeStringId)) },
                    onClick = {
                        onExpenseTypeSelected(expenseType)
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

package cz.mendelu.project.ui.screens.expenses.list

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.extensions.toFormattedDate
import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.elements.PlaceholderScreenDefinition
import cz.mendelu.project.ui.screens.odometer.OdometerScreenData
import cz.mendelu.project.ui.screens.odometer.OdometerScreenUIState
import cz.mendelu.project.ui.theme.BasicMargin

@Composable
fun ExpensesListScreen(navigationRouter: INavigationRouter) {

    val viewModel = hiltViewModel<ExpensesListScreenViewModel>()

    val state = viewModel.expensesListScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(ExpensesListScreenData())
    }

    state.value.let {
        when (it) {
            is ExpensesListScreenUIState.Loading -> {
                viewModel.initialize()
            }

            is ExpensesListScreenUIState.DataLoaded -> {
                data = it.data
            }
        }
    }


    BaseScreen(
        topBarText = stringResource(R.string.expenses),
        onBackClick = { navigationRouter.returnBack() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigationRouter.navigateToExpensesAddEditScreen(null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        placeholderScreenDefinition = if (data.expenses.isEmpty()) {
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
                ExpensesListScreenContent(
                    navigationRouter = navigationRouter,
                    data = data,
                    actions = viewModel
                )
            }
        }
    }
}

@Composable
fun ExpensesListScreenContent(
    navigationRouter: INavigationRouter,
    data: ExpensesListScreenData,
    actions: ExpensesListScreenActions,
) {
    data.expenses.forEach { expense ->
        ExpenseCard(
            expense = expense,
            onEditClick = {
                navigationRouter.navigateToExpensesAddEditScreen(expense.id)
            }
        )
    }
}

@Composable
fun ExpenseCard(
    expense: Expense,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = BasicMargin(), start = BasicMargin(), end = BasicMargin())
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = SpaceBetween
                ) {
                    Text(
                        text = stringResource(expense.expenseType ?: R.string.other) +
                                if (expense.expenseType == ExpenseType.FUEL.typeStringId) {
                                    " (${expense.fuelMileageInfo ?: 0} km)"
                                } else "",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${expense.amount ?: 0} ${stringResource(R.string.czk)}",
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = expense.date?.toFormattedDate() ?: stringResource(R.string.invalid_date)
                )

                expense.note?.takeIf { it.isNotEmpty() }?.let { notes ->
                    Text(
                        text = stringResource(R.string.notes) + ":\n$notes",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(onEditClick, modifier = Modifier.align(Alignment.End)) {
                    Text(text = stringResource(R.string.edit))
                }
            }
        }
    }
}
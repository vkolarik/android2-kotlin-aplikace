package cz.mendelu.project.ui.screens.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.theme.HalfMargin
import kotlin.math.roundToInt

@Composable
fun PaymentScreen(navigationRouter: INavigationRouter) {

    val viewModel = hiltViewModel<PaymentScreenViewModel>()

    val state = viewModel.paymentScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(PaymentScreenData())
    }

    state.value.let {
        when (it) {
            PaymentScreenUIState.Loading -> {
                viewModel.initialize()
            }

            is PaymentScreenUIState.PaymentScreenDataChanged -> {
                data = it.data
            }
        }
    }

    BaseScreen(
        topBarText = stringResource(R.string.split_journey_cost),
        onBackClick = { navigationRouter.returnBack() }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            item {
                PaymentScreenContent(data = data, actions = viewModel)
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenContent(
    data: PaymentScreenData,
    actions: PaymentScreenActions,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(HalfMargin()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(HalfMargin())
    ) {

        // ------------------- DISTANCE -----------------------------

        TextField(
            label = { Text(stringResource(R.string.distance_km)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.distanceKmString,
            onValueChange = {
                data.distanceKmString = it
                actions.onPaymentScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.distanceError != null,
            supportingText = {
                if (data.errors.distanceError != null) {
                    Text(text = stringResource(id = data.errors.distanceError!!))
                }
            }
        )

        // ------------------- COST -----------------------------

        TextField(
            label = { Text(stringResource(R.string.cost_per_kilometer_czk)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.costCzkString,
            onValueChange = {
                data.costCzkString = it
                actions.onPaymentScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.costError != null,
            supportingText = {
                if (data.errors.costError != null) {
                    Text(text = stringResource(id = data.errors.costError!!))
                }
            }
        )

        // ------------------- SPLIT -----------------------------

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.split_factor))
        }

        val options = listOf(PaymentScreenSplitType.ENTER, PaymentScreenSplitType.HALF, PaymentScreenSplitType.FULL)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, value ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        data.selectedPaymentScreenSplitType = value
                        actions.onPaymentScreenDataChanged(data)
                    },
                    selected = value == data.selectedPaymentScreenSplitType
                ) {
                    Text(stringResource(id = value.typeResource))
                }
            }
        }

        Spacer(modifier = Modifier.height(HalfMargin()))

        // ------------------- SPLIT MANUAL INPUT -----------------------------

        if (data.selectedPaymentScreenSplitType == PaymentScreenSplitType.ENTER) {
            TextField(
                label = { Text(stringResource(R.string.how_many_people_are_splitting_the_cost)) },
                modifier = Modifier
                    .fillMaxWidth(),
                value = data.manualSplitDividerString,
                onValueChange = {
                    data.manualSplitDividerString = it
                    actions.onPaymentScreenDataChanged(data)
                },
                maxLines = 1,
                isError = data.errors.splitError != null,
                supportingText = {
                    if (data.errors.splitError != null) {
                        Text(text = stringResource(id = data.errors.splitError!!))
                    }
                }
            )
        }

        // ------------------- CALCULATED -----------------------------

        AmountsLayout(totalAmount = data.calculatedSum, splitAmount = data.calculatedSplitPart)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (data.errors.bankAccountError != null){
                Text(
                    text = stringResource(data.errors.bankAccountError!!),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            } else {
                if (data.qrCode !== null) {
                    Image(
                        bitmap = data.qrCode!!.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(300.dp)
                    )
                }

            }
        }

    }
}

@Composable
fun AmountsLayout(totalAmount: Double?, splitAmount: Double?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.full_amount),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = String.format("%.2f", totalAmount) + " " + stringResource(R.string.czk),
                style = MaterialTheme.typography.bodyMedium
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.split_amount),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = String.format("%.2f", splitAmount) + " " + stringResource(R.string.czk),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

enum class PaymentScreenSplitType(val typeResource: Int){
    ENTER(R.string.manual),
    FULL(R.string.full),
    HALF(R.string.half)
}
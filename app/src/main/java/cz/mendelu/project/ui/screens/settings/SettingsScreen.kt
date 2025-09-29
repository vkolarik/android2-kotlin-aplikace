package cz.mendelu.project.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.project.R
import cz.mendelu.project.communication.FuelType
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.theme.BasicMargin
import cz.mendelu.project.ui.theme.HalfMargin

@Composable
fun SettingsScreen(navigationRouter: INavigationRouter) {

    val viewModel = hiltViewModel<SettingsScreenViewModel>()

    val state = viewModel.settingsScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(SettingsScreenData())
    }

    state.value.let {
        when (it) {
            SettingsScreenUIState.Loading -> {
                viewModel.initialize()
            }

            is SettingsScreenUIState.DataChanged -> {
                data = it.data
            }

            SettingsScreenUIState.SettingsSaved -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }
        }
    }

    BaseScreen(
        topBarText = stringResource(R.string.settings),
        onBackClick = { navigationRouter.returnBack() },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            item {
                SettingsScreenContent(data = data, actions = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    data: SettingsScreenData,
    actions: SettingsScreenActions,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(BasicMargin()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(HalfMargin())
    ) {

        // ------------------- CAR MODEL -----------------------------

        TextField(
            label = { Text(stringResource(R.string.car_model)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.carNameInput,
            onValueChange = {
                data.carNameInput = it
                actions.onSettingsScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.carNameInputError != null,
            supportingText = {
                if (data.errors.carNameInputError != null) {
                    Text(text = stringResource(id = data.errors.carNameInputError!!))
                }
            }
        )

        // ------------------- FUEL TYPE -----------------------------

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.fuel_type))
        }

        val options = listOf(FuelType.PETROL, FuelType.DIESEL, FuelType.LPG)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, value ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        data.fuelType = value
                        actions.onSettingsScreenDataChanged(data)
                    },
                    selected = value.dbInt == data.fuelType.dbInt
                ) {
                    Text(stringResource(id = value.displayName))
                }
            }
        }

        Spacer(modifier = Modifier.height(HalfMargin()))

        // ------------------- PURCHASE PRICE -----------------------------

        TextField(
            label = { Text(stringResource(R.string.purchase_price_czk)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.carPurchasePriceInput,
            onValueChange = {
                data.carPurchasePriceInput = it
                actions.onSettingsScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.carPurchasePriceInputError != null,
            supportingText = {
                if (data.errors.carPurchasePriceInputError != null) {
                    Text(text = stringResource(id = data.errors.carPurchasePriceInputError!!))
                }
            }
        )

        // ------------------- DEPRECIATION FACTOR -----------------------------

        TextField(
            label = { Text(stringResource(R.string.yearly_depreciation_factor_in_percentage)) },
            modifier = Modifier
                .fillMaxWidth(),
                //.background(Color.Blue),
            value = data.carDepreciationFactorInput,
            onValueChange = {
                data.carDepreciationFactorInput = it
                actions.onSettingsScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.carDepreciationFactorInputError != null,
            supportingText = {
                if (data.errors.carDepreciationFactorInputError != null) {
                    Text(text = stringResource(id = data.errors.carDepreciationFactorInputError!!))
                } else {
                    Text(
                        text = data.depreciationMessage,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        )


        // ------------------- BANK ACCOUNT -----------------------------

        TextField(
            label = { Text(stringResource(R.string.your_bank_account_number)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.bankAccountInput,
            onValueChange = {
                data.bankAccountInput = it
                actions.onSettingsScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.bankAccountInputError != null,
            supportingText = {
                if (data.errors.bankAccountInputError != null) {
                    Text(text = stringResource(id = data.errors.bankAccountInputError!!))
                }
            }
        )

        // ------------------- EXPECTED YEARLY MILEAGE -----------------------------

        TextField(
            label = { Text(stringResource(R.string.expected_yearly_mileage_km)) },
            modifier = Modifier
                .fillMaxWidth(),
            value = data.expectedYearlyMileageInput,
            onValueChange = {
                data.expectedYearlyMileageInput = it
                actions.onSettingsScreenDataChanged(data)
            },
            singleLine = true,
            isError = data.errors.expectedYearlyMileageInputError != null,
            supportingText = {
                if (data.errors.expectedYearlyMileageInputError != null) {
                    Text(text = stringResource(id = data.errors.expectedYearlyMileageInputError!!))
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
                    actions.saveSettings()
                }) {
                Text(stringResource(id = R.string.save))
            }
        }
    }
}
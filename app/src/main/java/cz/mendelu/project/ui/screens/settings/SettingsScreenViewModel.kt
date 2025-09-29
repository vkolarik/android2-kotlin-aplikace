package cz.mendelu.project.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.R
import cz.mendelu.project.communication.FuelType
import cz.mendelu.project.database.ILocalCarExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val repository: ILocalCarExpenseRepository,
    @ApplicationContext private val context: Context
) : ViewModel(), SettingsScreenActions {

    private val _settingsScreenUIState: MutableStateFlow<SettingsScreenUIState> = MutableStateFlow(
        SettingsScreenUIState.Loading
    )

    val settingsScreenUIState = _settingsScreenUIState.asStateFlow()

    private var data = SettingsScreenData()

    override fun saveSettings() {
        val currentErrors = SettingsScreenErrors()


        // ------------------------- VALIDATION -------------------------

        // price if not null check valid number
        if (data.carPurchasePriceInput.isNotEmpty()){
            if (data.carPurchasePriceInput.toLongOrNull() == null) {
                currentErrors.errorOccurred = true
                currentErrors.carPurchasePriceInputError = R.string.the_input_is_not_a_valid_number
            }
        }

        // depreciation factor if not null check valid number
        if (data.carDepreciationFactorInput.isNotEmpty()) {
            val depreciationFactor = data.carDepreciationFactorInput.toLongOrNull()
            if (depreciationFactor == null || depreciationFactor !in 0..100) {
                currentErrors.errorOccurred = true
                currentErrors.carDepreciationFactorInputError = R.string.the_allowed_range_is_0_to_100
            } else {
                if (data.carPurchasePriceInput.isEmpty()){
                    currentErrors.errorOccurred = true
                    currentErrors.carDepreciationFactorInputError = R.string.fill_out_the_purchase_price_as_well
                }
            }
        }

        // bank account if not null check valid account
        if (data.bankAccountInput.isNotEmpty()) {
            val regex = """^\d{1,6}-?\d{1,10}/\d{4}$""".toRegex()
            if (!regex.matches(data.bankAccountInput)) {
                currentErrors.errorOccurred = true
                currentErrors.bankAccountInputError = R.string.invalid_bank_account_format
            }
        }

        // expected mileage if not null check valid number

        if (data.expectedYearlyMileageInput.isNotEmpty()){
            if (data.expectedYearlyMileageInput.toLongOrNull() == null) {
                currentErrors.errorOccurred = true
                currentErrors.expectedYearlyMileageInputError = R.string.the_input_is_not_a_valid_number
            }
        }

        if (!currentErrors.errorOccurred) {

            data.settings?.fuelType = data.fuelType.dbInt
            data.settings?.carName = data.carNameInput
            data.settings?.purchasePriceCZK = data.carPurchasePriceInput.toLongOrNull()
            data.settings?.yearlyDepreciationFactor = data.carDepreciationFactorInput.toIntOrNull()
            data.settings?.bankAccount = data.bankAccountInput
            data.settings?.expectedYearlyMileage = data.expectedYearlyMileageInput.toLongOrNull()

            viewModelScope.launch {

                repository.update(data.settings!!)
                _settingsScreenUIState.update { SettingsScreenUIState.SettingsSaved }
            }
        } else {
            data.errors = currentErrors
            _settingsScreenUIState.update { SettingsScreenUIState.DataChanged(data) }
        }
    }

    fun initialize() {
        viewModelScope.launch {
            data.settings = repository.getSettings()
            data.settings?.let {
                data.carPurchasePriceInput = it.purchasePriceCZK?.toString() ?: ""
                data.carDepreciationFactorInput = it.yearlyDepreciationFactor?.toString() ?: ""
                data.expectedYearlyMileageInput = it.expectedYearlyMileage?.toString() ?: ""
                data.bankAccountInput = it.bankAccount ?: ""
                data.carNameInput = it.carName ?: ""
                data.fuelType = FuelType.fromInt(it.fuelType ?: FuelType.PETROL.dbInt)
            }
            onSettingsScreenDataChanged(data)
        }

    }


    override fun onSettingsScreenDataChanged(data: SettingsScreenData) {
        // Banned characters in inputs
        data.carPurchasePriceInput = data.carPurchasePriceInput.replace(Regex("[^0-9]"), "")
        data.carDepreciationFactorInput =
            data.carDepreciationFactorInput.replace(Regex("[^0-9]"), "")
        data.expectedYearlyMileageInput =
            data.expectedYearlyMileageInput.replace(Regex("[^0-9]"), "")
        data.bankAccountInput = data.bankAccountInput.replace(Regex("[^0-9/-]"), "")

        if (data.carPurchasePriceInput.isNotEmpty() && data.carDepreciationFactorInput.isNotEmpty()) {
            val purchasePrice = data.carPurchasePriceInput.toLongOrNull()
            val depreciationFactor = data.carDepreciationFactorInput.toDoubleOrNull()

            if (purchasePrice != null && purchasePrice > 0 &&
                depreciationFactor != null && depreciationFactor in 0.0..100.0) {
                // Calculate depreciation
                val depreciationValue = purchasePrice * (depreciationFactor / 100)

                //MAKE THIS TRANSLATABLE
                data.depreciationMessage = context.getString(
                    R.string.depreciation_message,
                    purchasePrice,
                    depreciationFactor,
                    depreciationValue
                )
            } else {
                // Invalid input message
                data.depreciationMessage = context.getString(R.string.depreciation_invalid_input)
            }
        } else {
            // Default example message
            data.depreciationMessage = context.getString(R.string.depreciation_example_default)
        }


        this.data = data
        _settingsScreenUIState.update { SettingsScreenUIState.DataChanged(data) }
    }
}
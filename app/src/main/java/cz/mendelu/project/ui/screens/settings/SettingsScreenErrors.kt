package cz.mendelu.project.ui.screens.settings

data class SettingsScreenErrors(
    var errorOccurred: Boolean = false,
    var bankAccountInputError: Int? = null,
    var expectedYearlyMileageInputError: Int? = null,
    var carPurchasePriceInputError: Int? = null,
    var carDepreciationFactorInputError: Int? = null,
    var carNameInputError: Int? = null,
)
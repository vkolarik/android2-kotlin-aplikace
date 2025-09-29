package cz.mendelu.project.ui.screens.settings

import android.graphics.Bitmap
import android.icu.util.IslamicCalendar
import cz.mendelu.project.R
import cz.mendelu.project.communication.FuelType
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.model.Settings

class SettingsScreenData {
    var errors: SettingsScreenErrors = SettingsScreenErrors()
    var settings: Settings? = null

    var depreciationMessage: String = ""

    var bankAccountInput : String = ""
    var expectedYearlyMileageInput : String = ""
    var carNameInput : String = ""
    var carPurchasePriceInput : String = ""
    var carDepreciationFactorInput : String = ""
    var fuelType: FuelType = FuelType.UNKNOWN
}
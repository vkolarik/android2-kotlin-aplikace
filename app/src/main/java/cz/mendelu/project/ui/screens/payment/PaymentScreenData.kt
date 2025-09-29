package cz.mendelu.project.ui.screens.payment

import android.graphics.Bitmap
import cz.mendelu.project.R
import cz.mendelu.project.model.Settings
import cz.mendelu.project.model.Statistics

class PaymentScreenData {
    var errors: PaymentScreenErrors = PaymentScreenErrors()

    var distanceKmString : String = ""
    var costCzkString : String = ""
    var manualSplitDividerString : String = "3"
    var selectedPaymentScreenSplitType : PaymentScreenSplitType = PaymentScreenSplitType.ENTER
    var calculatedSum : Double? = null
    var calculatedSplitPart : Double? = null
    var qrCode : Bitmap? = null
    var bankAccount : String? = null

    var statistics : Statistics? = null
    var settings : Settings? = null

}
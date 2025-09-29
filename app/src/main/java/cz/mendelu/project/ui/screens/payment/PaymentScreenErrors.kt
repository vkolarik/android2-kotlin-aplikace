package cz.mendelu.project.ui.screens.payment

data class PaymentScreenErrors(
    var errorOccurred: Boolean = false,
    var distanceError: Int? = null,
    var costError: Int? = null,
    var splitError: Int? = null,
    var bankAccountError: Int? = null,
)
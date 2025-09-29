package cz.mendelu.project.ui.screens.payment

sealed class PaymentScreenUIState {
    object Loading : PaymentScreenUIState()
    class PaymentScreenDataChanged(val data: PaymentScreenData) : PaymentScreenUIState()
}
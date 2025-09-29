package cz.mendelu.project.ui.screens.payment

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import cz.mendelu.project.R
import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.extensions.isValidBankAccountNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class PaymentScreenViewModel @Inject constructor(
    private val repository: ILocalCarExpenseRepository
) : ViewModel(), PaymentScreenActions {

    private val _addEditRoutineUIState: MutableStateFlow<PaymentScreenUIState> = MutableStateFlow(
        PaymentScreenUIState.Loading
    )

    val paymentScreenUIState = _addEditRoutineUIState.asStateFlow()

    private var data = PaymentScreenData()

    fun initialize() {
        viewModelScope.launch(Dispatchers.IO) {
            data.statistics = repository.getStatistics()
            data.settings = repository.getSettings()

            data.distanceKmString = "100"
            data.costCzkString = String.format("%.2f", data.statistics?.costPerKmSum)
            data.bankAccount = data.settings?.bankAccount

            onPaymentScreenDataChanged(data)
        }

    }

    override fun onPaymentScreenDataChanged(data: PaymentScreenData) {
        // Start with clean plate
        val currentErrors = PaymentScreenErrors()

        // Banned characters in inputs
        data.distanceKmString = data.distanceKmString.replace(Regex("[^0-9.]"), "")
        data.costCzkString = data.costCzkString.replace(Regex("[^0-9.]"), "")
        data.manualSplitDividerString = data.manualSplitDividerString.replace(Regex("[^0-9]"), "")

        // Input fields validation
        if (data.distanceKmString.toDoubleOrNull() == null) {
            currentErrors.distanceError = R.string.the_input_is_not_a_valid_number
            currentErrors.errorOccurred = true
        }

        if (data.costCzkString.toDoubleOrNull() == null) {
            currentErrors.costError = R.string.the_input_is_not_a_valid_number
            currentErrors.errorOccurred = true
        }

        if (data.manualSplitDividerString.toDoubleOrNull() == null) {
            currentErrors.splitError = R.string.the_input_is_not_a_valid_number
            currentErrors.errorOccurred = true
        }

        if (data.manualSplitDividerString.toDoubleOrNull() == 0.0 && data.selectedPaymentScreenSplitType == PaymentScreenSplitType.ENTER) {
            currentErrors.splitError = R.string.division_by_zero_is_not_allowed
            currentErrors.errorOccurred = true
        }

        if (data.bankAccount == null || !data.bankAccount!!.isValidBankAccountNumber()) {
            currentErrors.bankAccountError =
                R.string.please_provide_a_valid_bank_account_number_in_settings
        }

        // Generate QR code
        if (!currentErrors.errorOccurred && data.bankAccount != null) {
            data.calculatedSum = data.distanceKmString.toDouble() * data.costCzkString.toDouble()
            val divideBy = when (data.selectedPaymentScreenSplitType) {
                PaymentScreenSplitType.FULL -> 1.0
                PaymentScreenSplitType.HALF -> 2.0
                PaymentScreenSplitType.ENTER -> data.manualSplitDividerString.toDouble()
            }
            data.calculatedSplitPart = data.calculatedSum!! / divideBy
            if (currentErrors.bankAccountError == null)
                data.qrCode = generatePaymentQRCode(
                    data.bankAccount!!, data.calculatedSplitPart!!,
                    "CarExpense", 300
                )
        } else {
            data.qrCode = null
            data.calculatedSum = 0.0
            data.calculatedSplitPart = 0.0
        }

        // Send updated data to screen
        data.errors = currentErrors
        this.data = data
        _addEditRoutineUIState.update { PaymentScreenUIState.PaymentScreenDataChanged(data) }
    }

    fun generatePaymentQRCode(
        accountNumber: String,
        amount: Double,
        note: String,
        currency: String = "CZK"
    ): String? {
        // Validate the account number format
        if (!accountNumber.isValidBankAccountNumber()) return null

        // Split the account into prefix, main number, and bank code
        val parts = accountNumber.split("/", "-").filter { it.isNotEmpty() }
        if (parts.size < 2) return null // Ensure main number and bank code are present

        val bankCode = parts.last()
        val mainNumber = parts[parts.size - 2]
        val prefix = if (parts.size == 3) parts.first() else null

        // Convert to IBAN
        val iban = convertToCzechIban(mainNumber, bankCode, prefix) ?: return null

        // Generate the QR code string
        return "SPD*1.0*ACC:$iban*AM:${"%.2f".format(amount)}*CC:$currency*MSG:$note"
    }

    fun convertToCzechIban(mainNumber: String, bankCode: String, prefix: String? = null): String? {
        // Pad the account components
        val paddedMainNumber = mainNumber.padStart(10, '0')
        val paddedPrefix = prefix?.padStart(6, '0') ?: "".padStart(6, '0')
        val paddedBankCode = bankCode.padStart(4, '0')

        // Create the BBAN (Basic Bank Account Number)
        val bban = "$paddedBankCode$paddedPrefix$paddedMainNumber"

        // Rearrange for checksum calculation
        val rearrangedBban = "$bban${"CZ00"}"

        // Convert characters to numbers for checksum
        val numericBban = rearrangedBban.map {
            when {
                it.isDigit() -> it.toString()
                it.isLetter() -> (it.uppercaseChar().code - 'A'.code + 10).toString()
                else -> return null // Invalid character
            }
        }.joinToString("")

        // Calculate checksum
        val checksum = (98 - (numericBban.toBigInteger().mod(BigInteger.valueOf(97))).toInt()).toString().padStart(2, '0')


        // Return the IBAN
        return "CZ$checksum$bban"
    }

    fun generatePaymentQRCode(
        accountNumber: String,
        amount: Double,
        message: String,
        size: Int
    ): Bitmap? {
        // Format data according to QR Payment Standard
        val qrData : String = generatePaymentQRCode(accountNumber, amount, message) ?: return null

        // Generate QR code from formatted data
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            qrData,
            BarcodeFormat.QR_CODE,
            size,
            size
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        return bitmap
    }

}
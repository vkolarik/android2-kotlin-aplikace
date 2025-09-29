package cz.mendelu.project.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.mendelu.project.R


@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var expenseType: Int? = null,
    var calculationType: Int? = null,
    var note: String? = null,
    var date: Long? = null,
    var amount: Long? = null,
    var fuelMileageInfo: Long? = null
)

enum class ExpenseType(val typeStringId: Int) {
    FUEL(R.string.fuel),
    MAINTENANCE(R.string.maintenance),
    TECHNICAL_INSPECTION(R.string.technical_inspection),
    INSURANCE(R.string.insurance),
    PARKING(R.string.parking),
    LOAN_PAYMENT(R.string.loan_payment),
    REGISTRATION_FEES(R.string.registration_fees),
    EMISSION_TAX(R.string.emission_tax),
    ACCIDENT_REPAIRS(R.string.accident_repairs),
    CLEANING(R.string.cleaning),
    ACCESSORIES(R.string.accessories),
    ROAD_ASSISTANCE(R.string.road_assistance),
    WINTER_TIRES(R.string.winter_tires),
    BATTERY_REPLACEMENT(R.string.battery_replacement),
    OTHER(R.string.other);

    companion object {
        fun fromInt(typeId: Int): ExpenseType? {
            return values().find { it.typeStringId == typeId }
        }
    }
}
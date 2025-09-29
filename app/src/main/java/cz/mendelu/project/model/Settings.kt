package cz.mendelu.project.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var carName: String? = null,
    var fuelType: Int? = null,
    var purchasePriceCZK: Long? = null,
    var yearlyDepreciationFactor: Int? = null,
    var bankAccount: String? = null,
    var expectedYearlyMileage: Long? = null,
)
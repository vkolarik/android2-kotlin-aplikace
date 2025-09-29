package cz.mendelu.project.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "odometer_entries")
data class OdometerEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var mileage: Long? = null,
    var timestamp: Long? = null
){
    companion object {
        fun createTodaysOdometerEntry(mileage: Long): OdometerEntry {
            return OdometerEntry(null, mileage, System.currentTimeMillis())
        }

    }
}
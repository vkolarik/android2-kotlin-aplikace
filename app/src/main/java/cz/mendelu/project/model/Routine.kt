package cz.mendelu.project.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cz.mendelu.project.R
import java.util.Date


@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var typeOfOperation: Int? = null,
    var typeOfRepetition: Int? = null,
    var startDate: Long? = null,
    var startMileage: Long? = null,
    var note: String? = null,
    var repeatBy: Long? = null,
    var lastAccomplished: Long? = null,
){
    // Method to complete a task and save the current time as the last accomplished time
    fun completeMileageTask(currentMileage: Long) {
        if (lastAccomplished != null && repeatBy != null) {
            lastAccomplished = lastAccomplished!! + repeatBy!! * ((currentMileage - lastAccomplished!!) / repeatBy!!)
        }
    }

    fun completeTimeTask() {
        if (lastAccomplished != null && startDate != null && repeatBy != null) {
            val repeatIntervalMillis = repeatBy!! * 24 * 60 * 60 * 1000 // Convert days to milliseconds
            lastAccomplished = lastAccomplished!! + repeatIntervalMillis *
                    ((System.currentTimeMillis() - lastAccomplished!!) / repeatIntervalMillis)
        }
    }

    fun isMileageTaskOverdue(currentMileage: Long): Boolean {
        return lastAccomplished?.let { last ->
            repeatBy?.let { repeatInterval ->
                last + repeatInterval <= currentMileage
            }
        } ?: false
    }


    fun isTimeTaskOverdue(): Boolean {
        return lastAccomplished?.let { last ->
            repeatBy?.let { repeatInterval ->
                last + repeatInterval * (24 * 60 * 60 * 1000) < System.currentTimeMillis()
            }
        } ?: false
    }

    fun getTimeTaskNextOccurence(): Long? {
        return lastAccomplished?.let { last ->
            repeatBy?.let { repeatInterval ->
                last + repeatInterval * (24 * 60 * 60 * 1000)
            }
        }
    }

    fun getMileageTaskNextOccurence(): Long? {
        return lastAccomplished?.let { last ->
            repeatBy?.let { repeatInterval ->
                last + repeatInterval
            }
        }
    }

    fun isTaskOverdue(currentMileage: Long): Boolean {
        if (typeOfRepetition == TypeOfRepetition.BY_TIME.typeStringId) {
            val nextOccurrence = getTimeTaskNextOccurence()
            if (nextOccurrence != null) {
                return (nextOccurrence - System.currentTimeMillis()) / (24 * 60 * 60 * 1000) < 0
            }
        } else {
            val nextMileage = getMileageTaskNextOccurence()
            if (nextMileage != null) {
                return nextMileage - currentMileage < 0
            }
        }
        return false
    }
}

enum class RecurringTaskType(val typeStringId: Int) {
    OIL_CHANGE(R.string.oil_change),
    TIRE_ROTATION(R.string.tire_rotation),
    BRAKE_INSPECTION(R.string.brake_inspection),
    FILTER_REPLACEMENT(R.string.filter_replacement),
    ALIGNMENT_CHECK(R.string.alignment_check),
    SPARK_PLUG_REPLACEMENT(R.string.spark_plug_replacement),
    COOLANT_REPLACEMENT(R.string.coolant_replacement),
    TOLL_PASS_PURCHASE(R.string.toll_pass_purchase),
    INSURANCE_RENEWAL(R.string.insurance_renewal),
    TECHNICAL_INSPECTION(R.string.technical_inspection),
    ROAD_ASSISTANCE_RENEWAL(R.string.road_assistance_renewal),
    OTHER(R.string.other);

    companion object {
        fun fromInt(typeId: Int): RecurringTaskType? {
            return values().find { it.typeStringId == typeId }
        }
    }
}

enum class TypeOfRepetition(val typeStringId: Int) {
    BY_MILEAGE(R.string.by_mileage),
    BY_TIME(R.string.by_time);

    companion object {
        fun fromInt(typeId: Int): TypeOfRepetition? {
            return values().find { it.typeStringId == typeId }
        }
    }
}
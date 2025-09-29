package cz.mendelu.project.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.model.MLKitTask
import cz.mendelu.project.model.OdometerEntry
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.Settings
import cz.mendelu.project.model.Statistics
import cz.mendelu.project.model.TypeOfRepetition
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@Dao
interface CarExpenseDao {

    //--------------------- routine ------------------------
    @Query("SELECT * FROM routines")
    fun getAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routines")
    fun getAllRoutinesList(): List<Routine>

    @Insert
    suspend fun insert(routine: Routine): Long

    @Update
    suspend fun update(routine: Routine)

    @Delete
    suspend fun delete(routine: Routine)

    @Query("DELETE FROM routines WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: Long)

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutine(id: Long): Routine


    //--------------------- odometer entry ------------------------

    @Query("SELECT * FROM odometer_entries")
    fun getAllOdometerEntries(): Flow<List<OdometerEntry>>

    @Insert
    suspend fun insert(odometerEntry: OdometerEntry): Long

    @Delete
    suspend fun delete(odometerEntry: OdometerEntry)

    //----------------------- expense --------------------------

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpense(id: Long) : Expense

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    //--------------------- mlkittask ------------------------

    @Query("SELECT * FROM mlkit_tasks WHERE id = :id")
    suspend fun getMlKitTask(id: Long) : MLKitTask

    @Insert
    suspend fun insert(mlKitTask: MLKitTask): Long

    @Update
    suspend fun update(mlKitTask: MLKitTask)

    @Delete
    suspend fun delete(mlKitTask: MLKitTask)

    //--------------------- statistics ------------------------
    @Query("SELECT COUNT(*) FROM routines")
    fun getTotalRoutines(): Int

    @Query("SELECT COALESCE(mileage, 0) FROM odometer_entries ORDER BY timestamp DESC LIMIT 1")
    fun getCurrentOdometerStatus(): Long

    suspend fun getStatistics(): Statistics {
        var settings = getSettings()
        if (settings == null) settings = Settings()
        val currentMileage = getCurrentOdometerStatus()
        val fuelExpenses = getTypeExpensesAfterDate(ExpenseType.FUEL.typeStringId, getLastYearTimestamp())
        val otherExpenses = getNonTypeExpensesAfterDate(ExpenseType.FUEL.typeStringId, getLastYearTimestamp())
        val odometerEntries = getOdometerEntriesAfterDate(getLastYearTimestamp())

        // Calculate kilometers driven
        val kilometersDriven = if (odometerEntries.isNotEmpty()) {
            val firstMileage = odometerEntries.minByOrNull { it.timestamp ?: 0 }?.mileage ?: 0
            val lastMileage = odometerEntries.maxByOrNull { it.timestamp ?: 0 }?.mileage ?: 0
            (lastMileage - firstMileage).toInt()
        } else {
            0
        }

        val expectedKilometersDriven : Int = (settings.expectedYearlyMileage ?: kilometersDriven).toInt()

        // Calculate cost per km values
        val fuelCost = fuelExpenses.sumOf { it.amount ?: 0 }
        val fuelMileage = fuelExpenses.sumOf { it.fuelMileageInfo ?: 0 }
        val costPerKmFuel = if (fuelMileage > 0) fuelCost.toDouble() / fuelMileage else null

        val depreciationCost = settings.purchasePriceCZK?.let { price ->
            settings.yearlyDepreciationFactor?.let { factor ->
                price * (factor / 100.0)
            }
        } ?: 0.0
        val costPerKmDepreciation = if (expectedKilometersDriven > 0) depreciationCost / expectedKilometersDriven else null

        val maintenanceCost = otherExpenses.sumOf { it.amount ?: 0 }
        val costPerKmMaintenance = if (expectedKilometersDriven > 0) maintenanceCost.toDouble() / expectedKilometersDriven else null

        val costPerKmSum = listOfNotNull(costPerKmFuel, costPerKmDepreciation, costPerKmMaintenance).sum()

        // Fetch expenses and mileage for last year
        val expensesLastYear = (fuelExpenses + otherExpenses).sumOf { it.amount ?: 0 }
        val mileageLastYear = kilometersDriven

        // Fetch yearly maintenance cost
        val yearlyMaintenanceCost = maintenanceCost.toInt()

        // Fetch upcoming tasks count
        val upcomingTasksCount = getAllRoutinesList().count { routine ->
            Log.d("TAGGGG", "expectedKilometersDriven $expectedKilometersDriven")

            if (routine.typeOfRepetition == TypeOfRepetition.BY_TIME.typeStringId){
                val nextTimeOccurrence = routine.getTimeTaskNextOccurence() ?: Long.MAX_VALUE
                Log.d("TAGGGG", "nextTimeOccurrence $nextTimeOccurrence")
                Log.d("TAGGGG", "compare ${System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L}")

                nextTimeOccurrence <= System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L
            } else {
                val nextMileageOccurrence = routine.getMileageTaskNextOccurence() ?: Long.MAX_VALUE
                Log.d("TAGGGG", "nextMileageOccurrence $nextMileageOccurrence")
                Log.d("TAGGGG", "compare ${currentMileage + expectedKilometersDriven / 12}")

                nextMileageOccurrence <= currentMileage + expectedKilometersDriven / 12
            }
        }

        return Statistics(
            costPerKmSum = costPerKmSum,
            costPerKmFuel = costPerKmFuel,
            costPerKmDepreciation = costPerKmDepreciation,
            costPerKmMaintenance = costPerKmMaintenance,
            mileageLastYear = mileageLastYear,
            expensesLastYear = expensesLastYear.toInt(),
            yearlyMaintenanceCost = yearlyMaintenanceCost,
            upcomingTasksCount = upcomingTasksCount
        )
    }


    @Query("SELECT * FROM expenses WHERE date >= :date AND expenseType = :expenseType")
    suspend fun getTypeExpensesAfterDate(expenseType: Int, date: Long): List<Expense>

    @Query("SELECT * FROM expenses WHERE date >= :date AND expenseType <> :expenseType")
    suspend fun getNonTypeExpensesAfterDate(expenseType: Int, date: Long): List<Expense>

    @Query("SELECT * FROM odometer_entries WHERE timestamp >= :date")
    suspend fun getOdometerEntriesAfterDate(date: Long): List<OdometerEntry>

    private fun getLastYearTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        return calendar.timeInMillis
    }

    //--------------------- settings ------------------------

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): Settings?
    @Insert
    suspend fun insert(settings: Settings)
    @Update
    suspend fun update(settings: Settings)

}
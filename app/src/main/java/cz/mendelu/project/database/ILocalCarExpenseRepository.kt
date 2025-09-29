package cz.mendelu.project.database

import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.MLKitTask
import cz.mendelu.project.model.OdometerEntry
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.Settings
import cz.mendelu.project.model.Statistics
import kotlinx.coroutines.flow.Flow

interface ILocalCarExpenseRepository {

    //--------------------- routine ------------------------
    fun getAllRoutines(): Flow<List<Routine>>

    fun getAllRoutinesList(): List<Routine>

    suspend fun insert(routine: Routine): Long

    suspend fun update(routine: Routine)

    suspend fun delete(routine: Routine)

    suspend fun deleteRoutineById(routineId: Long)

    suspend fun getRoutine(id: Long) : Routine

    //--------------------- odometer entry ------------------------

    fun getAllOdometerEntries(): Flow<List<OdometerEntry>>

    suspend fun insert(odometerEntry: OdometerEntry): Long

    suspend fun delete(odometerEntry: OdometerEntry)

    //--------------------- expense ------------------------

    fun getAllExpenses(): Flow<List<Expense>>

    suspend fun getExpense(id: Long) : Expense

    suspend fun insert(expense: Expense): Long

    suspend fun update(expense: Expense)

    suspend fun delete(expense: Expense)

    //--------------------- mlkittask ------------------------

    suspend fun getMlKitTask(id: Long) : MLKitTask

    suspend fun insert(mlKitTask: MLKitTask): Long

    suspend fun update(mlKitTask: MLKitTask)

    suspend fun delete(mlKitTask: MLKitTask)

    //--------------------- statistics ------------------------

    fun getTotalRoutines(): Int

    suspend fun getStatistics(): Statistics

    fun getCurrentOdometerStatus(): Long

    //--------------------- settings ------------------------

    suspend fun getSettings(): Settings?
    suspend fun insert(settings: Settings)
    suspend fun update(settings: Settings)
}
package cz.mendelu.project.database

import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.MLKitTask
import cz.mendelu.project.model.OdometerEntry
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.Settings
import cz.mendelu.project.model.Statistics
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalCarExpenseRepositoryImpl @Inject constructor(private val dao: CarExpenseDao) : ILocalCarExpenseRepository {

    override suspend fun insert(routine: Routine): Long {
        return dao.insert(routine)
    }

    override suspend fun insert(odometerEntry: OdometerEntry): Long {
        return dao.insert(odometerEntry)
    }

    override suspend fun insert(expense: Expense): Long {
        return dao.insert(expense)
    }

    override suspend fun insert(mlKitTask: MLKitTask): Long {
        return dao.insert(mlKitTask)
    }

    override suspend fun insert(settings: Settings) {
        return dao.insert(settings)
    }

    override suspend fun update(routine: Routine) {
        return dao.update(routine)
    }

    override suspend fun update(expense: Expense) {
        return dao.update(expense)
    }

    override suspend fun update(mlKitTask: MLKitTask) {
        return dao.update(mlKitTask)
    }

    override suspend fun update(settings: Settings) {
        return dao.update(settings)
    }

    override suspend fun delete(routine: Routine) {
        return dao.delete(routine)
    }

    override suspend fun delete(odometerEntry: OdometerEntry) {
        return dao.delete(odometerEntry)
    }

    override suspend fun delete(expense: Expense) {
        return dao.delete(expense)
    }

    override suspend fun delete(mlKitTask: MLKitTask) {
        return dao.delete(mlKitTask)
    }

    override fun getAllRoutines(): Flow<List<Routine>> {
        return dao.getAllRoutines()
    }

    override fun getAllRoutinesList(): List<Routine> {
        return dao.getAllRoutinesList()
    }

    override suspend fun deleteRoutineById(routineId: Long) {
        dao.deleteRoutineById(routineId)
    }

    override suspend fun getRoutine(id: Long): Routine {
        return dao.getRoutine(id)
    }

    override fun getAllOdometerEntries(): Flow<List<OdometerEntry>> {
        return dao.getAllOdometerEntries()
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return dao.getAllExpenses()
    }

    override suspend fun getExpense(id: Long): Expense {
        return dao.getExpense(id)
    }

    override suspend fun getMlKitTask(id: Long): MLKitTask {
        return dao.getMlKitTask(id)
    }

    override fun getTotalRoutines(): Int {
        return dao.getTotalRoutines()
    }

    override suspend fun getStatistics(): Statistics {
        return dao.getStatistics()
    }

    override fun getCurrentOdometerStatus(): Long {
        return dao.getCurrentOdometerStatus()
    }

    override suspend fun getSettings(): Settings? {
        return dao.getSettings()
    }

}
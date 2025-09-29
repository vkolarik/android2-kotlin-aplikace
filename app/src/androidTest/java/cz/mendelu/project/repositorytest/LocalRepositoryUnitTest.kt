package cz.mendelu.project.repositorytest

import cz.mendelu.project.database.CarExpenseDao
import cz.mendelu.project.database.CarExpenseDatabase
import cz.mendelu.project.model.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CarExpenseDatabaseTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: CarExpenseDatabase
    private lateinit var dao: CarExpenseDao

    @Before
    fun setUp() {
        hiltRule.inject()
        dao = database.carExpenseDao()
    }

    @After
    fun tearDown() {
        //database.close()
    }

    @Test
    fun testRoutineOperations() = runBlocking {
        val routine = Routine(
            typeOfOperation = RecurringTaskType.OIL_CHANGE.typeStringId,
            typeOfRepetition = TypeOfRepetition.BY_MILEAGE.typeStringId,
            startDate = System.currentTimeMillis(),
            startMileage = 50000L,
            note = "Regular oil change",
            repeatBy = 10000L,
            lastAccomplished = 50000L
        )

        val id = dao.insert(routine)
        assertNotNull(id)

        val retrievedRoutine = dao.getRoutine(id)
        assertEquals(routine.typeOfOperation, retrievedRoutine.typeOfOperation)
        assertEquals(routine.startMileage, retrievedRoutine.startMileage)

        val updatedRoutine = retrievedRoutine.copy(note = "Updated oil change note")
        dao.update(updatedRoutine)
        val changedRoutine = dao.getRoutine(id)
        assertEquals("Updated oil change note", changedRoutine.note)

        dao.delete(changedRoutine)
        val allRoutines = dao.getAllRoutines().first()
        assertTrue(allRoutines.isEmpty())
    }

    @Test
    fun testOdometerEntryOperations() = runBlocking {
        val entry = OdometerEntry(
            mileage = 50000L,
            timestamp = System.currentTimeMillis()
        )

        val id = dao.insert(entry)
        assertNotNull(id)

        val entries = dao.getAllOdometerEntries().first()
        assertEquals(1, entries.size)
        assertEquals(50000L, entries[0].mileage)

        dao.delete(entries[0])
        val updatedEntries = dao.getAllOdometerEntries().first()
        assertTrue(updatedEntries.isEmpty())
    }

    @Test
    fun testExpenseOperations() = runBlocking {
        val expense = Expense(
            expenseType = ExpenseType.FUEL.typeStringId,
            calculationType = 1,
            note = "Regular fuel",
            date = System.currentTimeMillis(),
            amount = 1000L,
            fuelMileageInfo = 500L
        )

        val id = dao.insert(expense)
        assertNotNull(id)

        val retrievedExpense = dao.getExpense(id)
        assertEquals(expense.amount, retrievedExpense.amount)
        assertEquals(expense.expenseType, retrievedExpense.expenseType)

        val updatedExpense = retrievedExpense.copy(amount = 1500L)
        dao.update(updatedExpense)
        val changedExpense = dao.getExpense(id)
        assertEquals(1500L, changedExpense.amount)

        dao.delete(changedExpense)
        val allExpenses = dao.getAllExpenses().first()
        assertTrue(allExpenses.isEmpty())
    }

    @Test
    fun testMLKitTaskOperations() = runBlocking {
        val mlKitTask = MLKitTask(
            returnValue = "1000",
            description = "Fuel receipt",
            completed = false
        )

        val id = dao.insert(mlKitTask)
        assertNotNull(id)

        val retrievedTask = dao.getMlKitTask(id)
        assertEquals(mlKitTask.returnValue, retrievedTask.returnValue)
        assertEquals(mlKitTask.description, retrievedTask.description)

        val updatedTask = retrievedTask.copy(completed = true)
        dao.update(updatedTask)
        val changedTask = dao.getMlKitTask(id)
        assertTrue(changedTask.completed == true)

        dao.delete(changedTask)
    }

    @Test
    fun testStatisticsCalculation() = runBlocking {
        val settings = Settings(
            id = 1L,
            carName = "Test Car",
            fuelType = 1,
            purchasePriceCZK = 500000L,
            yearlyDepreciationFactor = 10,
            expectedYearlyMileage = 20000L
        )
        dao.insert(settings)

        val expense1 = Expense(
            expenseType = ExpenseType.FUEL.typeStringId,
            amount = 1000L,
            date = System.currentTimeMillis(),
            fuelMileageInfo = 500L
        )
        dao.insert(expense1)

        val expense2 = Expense(
            expenseType = ExpenseType.MAINTENANCE.typeStringId,
            amount = 2000L,
            date = System.currentTimeMillis()
        )
        dao.insert(expense2)

        val odometerEntry1 = OdometerEntry(
            mileage = 50000L,
            timestamp = System.currentTimeMillis() - 86400000
        )
        val odometerEntry2 = OdometerEntry(
            mileage = 50500L,
            timestamp = System.currentTimeMillis()
        )
        dao.insert(odometerEntry1)
        dao.insert(odometerEntry2)

        val statistics = dao.getStatistics()
        //print before assertEquals
        /*println("costPerKmSum: ${statistics.costPerKmSum}")
        println("costPerKmFuel: ${statistics.costPerKmFuel}")
        println("costPerKmMaintenance: ${statistics.costPerKmMaintenance}")*/

        assertNotNull(statistics)
        assertEquals(4.6, statistics.costPerKmSum)
        assertEquals(2.0, statistics.costPerKmFuel)
        assertEquals(0.1, statistics.costPerKmMaintenance)
        assertEquals(500, statistics.mileageLastYear)
        assertEquals(3000, statistics.expensesLastYear)
    }
}